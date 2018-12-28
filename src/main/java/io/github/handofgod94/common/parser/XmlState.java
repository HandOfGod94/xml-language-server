package io.github.handofgod94.common.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.Position;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A utility class, which as part of java schema validation,
 * captures all the relevant information for current xml document loaded.
 * Some of these include getting schemaLocations, recording errors if any.
 * Overall it will be responsible for storing current XmlState related information.
 * It can be used as SAXErrorHandler as well as LSResourceResolver. The main
 * reason to do this is to have common point to get all the XML Related information.
 */
public class XmlState implements LSResourceResolver, ErrorHandler {
  // TODO: Capture custom prefix URI's to resolve

  private Map<Position, String> errorMap = new HashMap<>();
  private List<URI> schemaLocations = new ArrayList<>();

  @Override
  public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

    if (systemId != null) {
      schemaLocations.add(URI.create(systemId));
    }

    return null;
  }

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

  private Position getPositionOfException(SAXParseException exception) {
    int lineNo = exception.getLineNumber() - 1;
    int endColumn = exception.getColumnNumber();
    return new Position(lineNo, endColumn);
  }

  public Map<Position, String> getErrorMap() {
    return errorMap;
  }

  public List<URI> getSchemaLocations() {
    return schemaLocations;
  }
}
