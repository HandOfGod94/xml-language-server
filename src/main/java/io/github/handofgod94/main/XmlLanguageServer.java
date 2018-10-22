package io.github.handofgod94.main;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.*;
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

    return capabilities;
  }

  /**
   * @return the client
   */
  public LanguageClient getClient() {
    return client;
  }
}
