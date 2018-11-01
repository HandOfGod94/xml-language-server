package io.github.handofgod94.main;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticService;
import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticServiceFactory;
import io.github.handofgod94.lsp.hover.XmlHover;
import io.github.handofgod94.lsp.hover.provider.XmlHoverProvider;
import io.github.handofgod94.lsp.hover.provider.XmlHoverProviderFactory;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.resolve.SchemaResolver;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.services.TextDocumentService;

/**
 * DocumentService for xml docs.
 */
public class XmlDocumentService implements TextDocumentService {

  private static final Logger logger = LogManager.getLogger(XmlDocumentService.class.getName());

  private XmlLanguageServer server;
  private Map<String, TextDocumentItem> openDocumentItems = new HashMap<>();
  private SchemaDocument schemaDocument;

  @Inject private final XmlDiagnosticServiceFactory diagnosticServiceFactory;
  @Inject private final XmlHoverProviderFactory xmlHoverProviderFactory;
  @Inject private final SchemaResolver resolver;

  /**
   * Create Document service for XML documents.
   * @param server the language server instance
   */
  public XmlDocumentService(XmlLanguageServer server) {
    this.server = server;
    diagnosticServiceFactory = server.getInjector().getInstance(XmlDiagnosticServiceFactory.class);
    xmlHoverProviderFactory = server.getInjector().getInstance(XmlHoverProviderFactory.class);
    // TODO: named injection should be based on dtd or xsd texts.
    resolver = server.getInjector().getInstance(Key.get(SchemaResolver.class, Names.named("Xsd")));
  }

  @Override
  public CompletableFuture<Hover> hover(TextDocumentPositionParams params) {

    Position position = params.getPosition();
    TextDocumentItem documentItem = openDocumentItems.get(params.getTextDocument().getUri());
    XmlHoverProvider provider =
        xmlHoverProviderFactory.create(position, schemaDocument, documentItem);
    Optional<XmlHover> optHover = provider.get();

    // Show hover if documentation is present, else show empty/no hover.
    Hover hover = optHover
                  .map(XmlHover::getHover)
                  .orElse(new Hover(Collections.emptyList()));
    return CompletableFuture.completedFuture(hover);
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    logger.info("File opened: {}", params.getTextDocument().getUri());

    TextDocumentItem documentItem = params.getTextDocument();
    openDocumentItems.put(documentItem.getUri(), documentItem);

    // Get instance from google guice injector
    Optional<SchemaDocument> optSchemaDocument = resolver.resolve(documentItem.getText());

    // Generate diagnostics when xml document is opened
    optSchemaDocument.ifPresent(schemaDocument -> {
      this.schemaDocument = schemaDocument;
      XmlDiagnosticService service =
          diagnosticServiceFactory.create(documentItem, server, schemaDocument);
      service.compute();
    });
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
    // Update text document if any changes found.
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

    // TODO: This should be observable, if document changes, then it requires resync.

    // Currently, its again fetching the whole xsd and reparsing it
    // to create new xs models. This must be avoided.
    Optional<SchemaDocument> optSchemaDocument = resolver.resolve(documentItem.getText());

    // Generate diagnostics on open of a text document
    optSchemaDocument.ifPresent(schemaDocument -> {
      this.schemaDocument = schemaDocument;
      XmlDiagnosticService service =
        diagnosticServiceFactory.create(documentItem, server, schemaDocument);
      service.compute();
    });
  }
}
