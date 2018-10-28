package io.github.handofgod94.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.SaveOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * Language server for XML.
 */
public class XmlLanguageServer implements LanguageServer,LanguageClientAware {

  public static final String LANGUAGE_ID = "xml";

  private LanguageClient client;
  private Injector injector = Guice.createInjector(new XmlLanguageServerModule());

  @Override
  public void connect(LanguageClient client) {
    this.client = client;
    client.logMessage(new MessageParams(MessageType.Info, "Connected to language Server for XML"));
  }

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    // initialize server capabilities
    client.logMessage(new MessageParams(MessageType.Info, "Initializing Server capabilities"));
    ServerCapabilities capabilities = createServerCapabilities();
    InitializeResult result = new InitializeResult(capabilities);
    return CompletableFuture.completedFuture(result);
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    return null;
  }

  @Override
  public void exit() {
    // TODO: perform cleanup on exit
    System.exit(0);
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    return new XmlDocumentService(this);
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return new XmlWorkspaceService();
  }

  private ServerCapabilities createServerCapabilities() {
    // We need changes to be include on each save
    SaveOptions saveOptions = new SaveOptions();
    saveOptions.setIncludeText(true);

    // TextDocument sync options
    TextDocumentSyncOptions syncOptions = new TextDocumentSyncOptions();
    syncOptions.setOpenClose(true);
    syncOptions.setChange(TextDocumentSyncKind.Full);
    syncOptions.setSave(saveOptions);

    // Set capabilities
    ServerCapabilities capabilities = new ServerCapabilities();
    capabilities.setTextDocumentSync(syncOptions);
    capabilities.setHoverProvider(true);

    return capabilities;
  }

  /**
   * Language client for the server.
   * @return Client object used by the server
   */
  public LanguageClient getClient() {
    return client;
  }

  /**
   * Dependency Injector.
   * All the class creation should be done through
   * this injector
   * @return Google Guice Injector
   */
  public Injector getInjector() {
    return injector;
  }
}
