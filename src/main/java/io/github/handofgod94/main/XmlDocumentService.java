package io.github.handofgod94.main;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.lsp.completion.XmlCompletion;
import io.github.handofgod94.lsp.completion.XmlCompletionFactory;
import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticService;
import io.github.handofgod94.lsp.hover.XmlHover;
import io.github.handofgod94.lsp.hover.provider.XmlHoverProvider;
import io.github.handofgod94.lsp.hover.provider.XmlHoverProviderFactory;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.resolve.SchemaResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

/**
 * DocumentService for xml docs.
 */
public class XmlDocumentService implements TextDocumentService {

  private static final Logger logger = LogManager.getLogger(XmlDocumentService.class.getName());

  private XmlLanguageServer server;
  private Map<String, TextDocumentItem> openDocumentItems = new HashMap<>();
  private Map<String, SchemaDocument> openSchemas = new HashMap<>();

  @Inject private final XmlCompletionFactory xmlCompletionFactory;
  @Inject private final XmlHoverProviderFactory xmlHoverProviderFactory;
  @Inject private final DocumentManager.Factory docManagerFactory;
  @Inject private final SchemaResolver resolver;

  /**
   * Create Document service for XML documents.
   *
   * @param server the language server instance
   */
  public XmlDocumentService(XmlLanguageServer server) {
    this.server = server;
    xmlHoverProviderFactory = server.getInjector().getInstance(XmlHoverProviderFactory.class);
    xmlCompletionFactory = server.getInjector().getInstance(XmlCompletionFactory.class);
    docManagerFactory = server.getInjector().getInstance(DocumentManager.Factory.class);
    // TODO: named injection should be based on dtd or xsd texts.
    resolver = server.getInjector().getInstance(Key.get(SchemaResolver.class, Names.named("Xsd")));
  }

  @Override
  public CompletableFuture<Hover> hover(TextDocumentPositionParams params) {
    Hover hover = new Hover(Collections.emptyList());
    Position position = params.getPosition();
    TextDocumentItem documentItem = openDocumentItems.get(params.getTextDocument().getUri());
    SchemaDocument schemaDocument = openSchemas.get(params.getTextDocument().getUri());

    if (schemaDocument != null && documentItem != null) {
      XmlHoverProvider provider =
          xmlHoverProviderFactory.create(position, schemaDocument, documentItem);
      Optional<XmlHover> optHover = provider.get();

      // Show hover if documentation is present, else show empty/no hover.
      hover = optHover
        .map(XmlHover::getHover)
        .orElse(new Hover(Collections.emptyList()));
    }
    return CompletableFuture.completedFuture(hover);
  }

  @Override
  public CompletableFuture<Either<List<CompletionItem>, CompletionList>>
      completion(CompletionParams params) {
    List<CompletionItem> list = new ArrayList<>();

    SchemaDocument schemaDocument = openSchemas.get(params.getTextDocument().getUri());
    TextDocumentItem documentItem =
        openDocumentItems.get(params.getTextDocument().getUri());

    if (schemaDocument != null && documentItem != null) {
      Optional<XmlCompletion> completion =
          xmlCompletionFactory.create(schemaDocument, params, documentItem);

      if (completion.isPresent()) {
        list = completion.get().getCompletions();
      }
    }

    return CompletableFuture.completedFuture(Either.forLeft(list));
  }



  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    logger.info("File opened: {}", params.getTextDocument().getUri());

    TextDocumentItem documentItem = params.getTextDocument();
    openDocumentItems.put(documentItem.getUri(), documentItem);

    Map<Position, String> errorMessages = XmlUtil.checkWellFormedXml(documentItem.getText());
    publishDiagnostics(documentItem.getUri(), errorMessages);

    if (errorMessages.isEmpty() && !openSchemas.containsKey(documentItem.getUri())) {
      // Load XSD Schema model
      Optional<SchemaDocument> schemaDocumentOptional = resolver.resolve(documentItem.getText());
      schemaDocumentOptional
          .ifPresent(document -> openSchemas.put(documentItem.getUri(), document));
    }
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

    // clear diagnostics
    publishDiagnostics(params.getTextDocument().getUri(), Collections.emptyMap());

    // Remove entries from global maps
    openDocumentItems.remove(params.getTextDocument().getUri());
    openSchemas.remove(params.getTextDocument().getUri());
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
    TextDocumentItem documentItem = openDocumentItems.get(params.getTextDocument().getUri());
    documentItem.setText(params.getText());

    Map<Position, String> errorMessages = XmlUtil.checkWellFormedXml(documentItem.getText());
    publishDiagnostics(documentItem.getUri(), errorMessages);

    if (errorMessages.isEmpty() && !openSchemas.containsKey(documentItem.getUri())) {
      // Load XSD Schema model
      Optional<SchemaDocument> schemaDocumentOptional = resolver.resolve(documentItem.getText());
      schemaDocumentOptional
          .ifPresent(document -> openSchemas.put(documentItem.getUri(), document));
    }
  }

  private void publishDiagnostics(String textDocumentIdUri, Map<Position, String> errorMap) {
    TextDocumentItem documentItem = openDocumentItems.get(textDocumentIdUri);
    DocumentManager manager = docManagerFactory.create(documentItem);
    String[] lines = manager.getDocumentLines();
    PublishDiagnosticsParams diagnostics =
        XmlDiagnosticService.getDiagnostics(lines, documentItem.getUri(), errorMap);
    server.getClient().publishDiagnostics(diagnostics);
  }
}
