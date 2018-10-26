package io.github.handofgod94.lsp.diagnostic;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.lsp4j.Position;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * SAXCustomHandler to parse XML File using SAX. Since general SAX parsing will
 * stop at first error, we will not know all the errors that are present in the
 * document. This class extends the base ErrorHandler class and add all the
 * errors and warnings to the list of errors.
 */
public class DiagnosticErrorHandler implements ErrorHandler {

  // Error map to store line number and errors description and severity.
  private Map<Position, String> errorMap = new HashMap<>();

  @Override
  public void warning(SAXParseException exception) throws SAXException {

  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    String message = exception.getMessage();
    Position position = getPositionOfException(exception);
    errorMap.put(position, message);
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    String message = exception.getMessage();
    Position position = getPositionOfException(exception);
    errorMap.put(position, message);
  }

  /**
   * Get line number and column number from exception message.
   *
   * @param exception SAXParseException with line and col nos in the message
   * @return Position of where the error is present
   */
  private Position getPositionOfException(SAXParseException exception) {
    int lineNo = exception.getLineNumber() - 1;
    int endColumn = exception.getColumnNumber();
    return new Position(lineNo, endColumn);
  }

  public Map<Position, String> getErrorMap() {
    return errorMap;
  }
}
