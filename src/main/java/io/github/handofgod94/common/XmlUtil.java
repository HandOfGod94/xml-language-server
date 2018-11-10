package io.github.handofgod94.common;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

  // XSD Constants
  public static final String XSD_UNBOUNDED = "UNBOUNDED";
  public static final String XSD_ANY = "any";
  public static final String XSD_WC = "WC";
  public static final Set<String> XSD_KEYS =
      new HashSet<>(Arrays.asList("", XSD_UNBOUNDED, XSD_ANY, XSD_WC));

  /**
   * Initializes document lines.
   *
   * @param text DocumentText having next lines
   * @return Array of strings for each line
   */
  public static String[] getDocumentLines(String text) {
    return text.split("\\r?\\n");
  }

  /**
   * Create dom4j Document object for a give a string.
   *
   * @param line string containing full xml
   * @return Document object if parsed successfully
   */
  public static Optional<Document> createParsedDoc(String line) {
    try {
      SAXReader reader = new SAXReader();
      Document document = reader.read(new StringReader(line));
      return Optional.of(document);
    } catch (DocumentException e) {
      logger.debug("Unable to parse document, it could be malformed", e);
    }
    return Optional.empty();
  }

  /**
   * Attempts to partially complete the line and create a parsed doc out of it.
   *
   * @param line String which needs to be parsed
   * @return String containing value of attribute
   */
  public static Optional<Document> getPartialDoc(String line) {
    // TODO: improve implementation to handle cases.
    // TODO: it's not always single element per line.
    String trimmedLine = line.trim();

    // first try directly parsing it, if its not successful
    // we will get empty value
    Optional<Document> optDocument = createParsedDoc(trimmedLine);
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
      return createParsedDoc(trimmedLine);
    }

    return optDocument;
  }

  /**
   * Checks whether the given position in a give string
   * is inside string or not.
   *
   * @param str   string to check in.
   * @param index position in the string/
   * @return true, if it's inside string, false otherwise
   */
  public static boolean isInsideString(String str, int index) {
    // flag to check if we have a matching quote or not
    boolean isInsideString = false;

    for (int i = 0; i < str.length(); i++) {
      Character character = str.charAt(i);
      // if index becomes less then i, then return it
      // it means we have found the result
      if (index <= i) {
        return isInsideString;
      }

      // otherwise whenever we encounter quotes(")
      // toggle the flag.
      if (character == '"') {
        isInsideString = !isInsideString;
      }
    }
    return isInsideString;
  }
}
