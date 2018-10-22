package io.github.handofgod94.lsp.diagnostic;


import io.github.handofgod94.common.DocumentManager;
import io.github.handofgod94.main.XmlLanguageServer;
import org.eclipse.lsp4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class AbstractXmlDiagnosticService {

  protected XmlLanguageServer server;
  protected TextDocumentItem documentItem;

  protected AbstractXmlDiagnosticService(XmlLanguageServer server, TextDocumentItem documentItem) {
    // TODO: Evalute the injection using google guice
    this.server = server;
    this.documentItem = documentItem;
  }

  /**
   * Traverse errorMap and create list of diagnostic objects.
   *
   * @return List of diagnostics
   */
  protected List<Diagnostic> getDiagnosticsFromErrorMap(Map<Position, String> errorMap) {
    // Create diagnostic collection
    List<Diagnostic> diagnostics = new ArrayList<>();

    // Create DocumentManager for querying document
    // TODO: evaluate injection using google guice
    DocumentManager manager = DocumentManager.getInstance();
    manager.init(documentItem);

    for (Position position : errorMap.keySet()) {
      int lineNo = position.getLine();
      int endColumn = position.getCharacter();
      String message = errorMap.get(position);
      String line = manager.getDocumentLines()[lineNo];
      int startColumn = line.length() - line.trim().length();

      Position startPosition = new Position(lineNo, startColumn);
      Position endPosition = new Position(lineNo, endColumn);

      Diagnostic diagnostic = new Diagnostic();

      // By default everything in xml will be an error.
      diagnostic.setSeverity(DiagnosticSeverity.Error);
      diagnostic.setMessage(message);
      diagnostic.setCode("");
      diagnostic.setRange(new Range(startPosition, endPosition));
      diagnostic.setSource("");

      diagnostics.add(diagnostic);
    }

    return diagnostics;
  }

}
