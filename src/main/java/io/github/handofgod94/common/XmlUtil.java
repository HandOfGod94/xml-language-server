package io.github.handofgod94.common;

import java.io.StringReader;
import java.util.Optional;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Utility class for various xml related operations. Similar to other utility
 * classes, all the functions are lambdas to keep functions pure.
 */
public class XmlUtil {

  private static final Logger logger = LogManager.getLogger(XmlUtil.class.getName());

  /**
   * Initializes document lines.
   *
   * @param text DocumentText having next lines
   * @return Array of strings for each line
   */
  public static Function<String, String[]> getDocumentLines = text -> text.split("\\r?\\n");

  /**
   * Create dom4j Document object for a give a string.
   *
   * @param line string containing full xml
   * @return Document object if parsed successfully
   */
  public static Function<String, Optional<Document>> createParsedDoc = line -> {
    try {
      SAXReader reader = new SAXReader();
      Document document = reader.read(new StringReader(line));
      return Optional.of(document);
    } catch (DocumentException e) {
      logger.debug("Unable to parse document, it could be malformed", e);
    }
    return Optional.empty();
  };

  /**
   * Attempts to partially complete the line and create a parsed doc out of it.
   *
   * @param line String which needs to be parsed
   * @return String containing value of attribute
   */
  public static Function<String, Optional<Document>> getPartialDoc = line -> {
    // TODO: improve implmentation to handle cases.
    // TODO: it's not always single element per line.
    String trimmedLine = line.trim();

    // first try directly parsing it, if its not successful
    // we will get empty value
    Optional<Document> optDocument = createParsedDoc.apply(trimmedLine);
    if (!optDocument.isPresent()) {
      if (trimmedLine.startsWith("</")) {
        // if its an ending line, as starting statement
        String tag = trimmedLine.substring(2, trimmedLine.lastIndexOf(">"));
        trimmedLine = "<" + tag + ">" + trimmedLine;
      } else if (!trimmedLine.endsWith("/>") && trimmedLine.endsWith(">")) {
        // If its a starting line, add an ending statement
        trimmedLine = trimmedLine.substring(0, trimmedLine.lastIndexOf(">"));
        trimmedLine += "/>";
      }
      return createParsedDoc.apply(trimmedLine);
    }

    return optDocument;
  };
}
