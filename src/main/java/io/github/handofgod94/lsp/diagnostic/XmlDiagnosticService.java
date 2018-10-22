package io.github.handofgod94.lsp.diagnostic;

import io.github.handofgod94.main.XmlLanguageServer;
import io.github.handofgod94.schema.SchemaDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.TextDocumentItem;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XmlDiagnosticService extends AbstractXmlDiagnosticService {

  private static final Logger logger = LogManager.getLogger(XmlDiagnosticService.class.getName());

  private SchemaDocument schemaDocument;

  // Error Handler to get all the exceptions during the parsing
  private DiagnosticErrorHandler errorHandler = new DiagnosticErrorHandler();

  // TODO: Evaluate guice for di
  public XmlDiagnosticService(TextDocumentItem documentItem, XmlLanguageServer server, SchemaDocument schemaDocument) {
    super(server, documentItem);
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

}
