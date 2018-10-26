package io.github.handofgod94.main;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * Entry class for Language Server.
 */
public class Main {

  /**
   * Entry point for language server.
   * @param args Command line args, if any
   */
  public static void main(String[] args) {
    XmlLanguageServer server = new XmlLanguageServer();
    Launcher<LanguageClient> launcher =
        Launcher.createLauncher(server, LanguageClient.class, System.in, System.out);
    server.connect(launcher.getRemoteProxy());
    launcher.startListening();
  }
}
