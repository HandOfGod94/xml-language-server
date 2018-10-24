package io.github.handofgod94.main;

import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticService;
import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticServiceFactory;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.resolve.SchemaResolver;
import io.github.handofgod94.schema.resolve.XsdSchemaResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * DocumentService for xml docs.
 */
public class XmlDocumentService implements TextDocumentService {

  private static final Logger logger = LogManager.getLogger(XmlDocumentService.class.getName());

  private XmlLanguageServer server;
  private Map<String, TextDocumentItem> openDocumentItems = new HashMap<>();
  private Map<String, SchemaDocument> schemaDocMap = new HashMap<>();

  @Inject XmlDiagnosticServiceFactory diagnosticServiceFactory;

  protected XmlDocumentService(XmlLanguageServer server) {
    this.server = server;
  }


  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    logger.info("File opened: {}", params.getTextDocument().getUri());

    TextDocumentItem documentItem = params.getTextDocument();
    openDocumentItems.put(documentItem.getUri(), documentItem);

    // Get instance from google guice injector
    SchemaResolver resolver = server.getInjector().getInstance(Key.get(SchemaResolver.class, Names.named("Xsd")));
    Optional<SchemaDocument> optSchemaDocument = resolver.resolve(documentItem.getText());

    // Generate diagnostics on open of document
    optSchemaDocument.ifPresent(schemaDocument -> {
      diagnosticServiceFactory.create(documentItem, server, schemaDocument).compute();
      schemaDocMap.put(documentItem.getUri(), schemaDocument);
    });
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
    // Update document if any changes found.
    List<TextDocumentContentChangeEvent> contentChanges = params.getContentChanges();
    TextDocumentItem textDocumentItem = openDocumentItems.get(params.getTextDocument().getUri());
    if (!contentChanges.isEmpty()) {
      textDocumentItem.setText(contentChanges.get(0).getText());
    }
  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
    logger.info("File closed: {}", params.getTextDocument().getUri());

    // Remove entries from global maps
    openDocumentItems.remove(params.getTextDocument().getUri());
  }

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
    TextDocumentItem documentItem = openDocumentItems.get(params.getTextDocument().getUri());
    documentItem.setText(params.getText());

    // Since schema is already loaded when we opened the document, so no need to
    // load it again
    SchemaDocument schemaDocument = schemaDocMap.get(documentItem.getUri());

    new XmlDiagnosticService(documentItem, server, schemaDocument).compute();
	}


}
