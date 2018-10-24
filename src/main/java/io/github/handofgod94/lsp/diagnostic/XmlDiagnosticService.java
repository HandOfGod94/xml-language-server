package io.github.handofgod94.lsp.diagnostic;

import io.github.handofgod94.common.DocumentManager;
import io.github.handofgod94.main.XmlLanguageServer;
import io.github.handofgod94.schema.SchemaDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlDiagnosticService {

  private static final Logger logger = LogManager.getLogger(XmlDiagnosticService.class.getName());

  private SchemaDocument schemaDocument;
  private XmlLanguageServer server;
  private TextDocumentItem documentItem;

  // Error Handler to get all the exceptions during the parsing
  private DiagnosticErrorHandler errorHandler = new DiagnosticErrorHandler();

  @Inject
  public XmlDiagnosticService(@Assisted TextDocumentItem documentItem, @Assisted XmlLanguageServer server, @Assisted SchemaDocument schemaDocument) {
    this.documentItem = documentItem;
    this.server = server;
    this.schemaDocument = schemaDocument;
  }

  /**
   * Gets all the errors in the XML file and push it to client. This needs to
   * invoked on load/save of document.
   */
  public void compute() {
    String text = documentItem.getText();
    Validator validator = schemaDocument.getSchema().newValidator();
    validator.setErrorHandler(errorHandler);

    try {
      validator.validate(new StreamSource(new StringReader(text)));
    } catch (SAXException | IOException ex) {
      logger.error("Error occurred while parsing/reading the document", ex);
    }

    // Error Map
    Map<Position, String> errorMap = errorHandler.getErrorMap();

    // Create diagnostic collection and get data from error map
    List<Diagnostic> diagnostics = new ArrayList<>(getDiagnosticsFromErrorMap(errorMap));

    // Publish diagnostics to client
    PublishDiagnosticsParams diagnosticsParams = new PublishDiagnosticsParams(this.documentItem.getUri(), diagnostics);
    server.getClient().publishDiagnostics(diagnosticsParams);
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
