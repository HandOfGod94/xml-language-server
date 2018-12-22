package io.github.handofgod94.lsp.diagnostic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;

public class XmlDiagnosticService {

  /**
   * Get parameters for diagnostic, based on the error map.
   * The error map should consist the position of error in the document
   * as well as message for that error. These params can be published to client
   * using XML Language server object
   * @param lines array of document split line by line
   * @param documentUri uri of the document for which diagnostics are requested
   * @param errorMap HashMap consisting of position of error and the error message
   * @return PublishDiagnosticsParams instance having all the diagnostics.
   */
  public static PublishDiagnosticsParams getDiagnostics(String[] lines,
                                                        String documentUri,
                                                        Map<Position, String> errorMap) {
    // Create diagnostic collection and getCompletionItems data from error map
    List<Diagnostic> diagnostics = new ArrayList<>(getDiagnosticsFromErrorMap(lines, errorMap));

    // Publish diagnostics to client
    PublishDiagnosticsParams diagnosticsParams =
        new PublishDiagnosticsParams(documentUri, diagnostics);

    return diagnosticsParams;
  }

  /**
   * Traverse errorMap and create list of diagnostic objects.
   *
   * @param lines array of string having content of document line by line.
   * @param errorMap Map of position to error obtained from Xml Document
   * @return List of diagnostics
   */
  private static List<Diagnostic> getDiagnosticsFromErrorMap(String[] lines,
                                                             Map<Position, String> errorMap) {
    // Create diagnostic collection
    List<Diagnostic> diagnostics = new ArrayList<>();

    for (Position position : errorMap.keySet()) {
      int lineNo = position.getLine();
      int endColumn = position.getCharacter();
      String message = errorMap.get(position);
      String line = lines[lineNo];
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
