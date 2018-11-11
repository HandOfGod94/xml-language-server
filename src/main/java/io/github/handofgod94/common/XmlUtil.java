package io.github.handofgod94.common;

import io.github.handofgod94.common.parser.PositionalHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSParticle;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

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

  /**
   * Parses XML text using PositionalHandler provided.
   * It's assumed that handler is initialized and is not null.
   * After parsing parent and current elements can be accessed using getters.
   * @param handler PositionalHandler instance initialized with a position
   * @param text text which needs to be parsed
   */
  public static void positionalParse(PositionalHandler handler, String text) {
    // parse using the custom handler
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();
      InputStream documentStream =
        new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
      parser.parse(documentStream, handler);
    } catch (SAXException | IOException e) {
      // FIXME: Too much noise in debug mode while
      logger.debug("Exception while parsing the document", e);
    } catch (ParserConfigurationException e) {
      logger.error("Exception while setting up parser", e);
    }
  }

  /**
   * Checks given element in the loaded xerces schema model.
   * In other words, it checks for qname in xsd whether it's declared as element or not.
   * If it is present then it's returned.
   * @param xsModel xsd xerces schema model
   * @param element lookup element
   * @return XSElementDeclaration object if is declared as element
   */
  public static Optional<XSElementDeclaration> checkInElement(XSModel xsModel, QName element) {
    if (xsModel != null && element != null) {
      XSElementDeclaration xsObject = xsModel.getElementDeclaration(element.getLocalPart(),
        element.getNamespaceURI());
      return Optional.ofNullable(xsObject);
    }
    return Optional.empty();
  }

  /**
   * Checks given element in the loaded xerces schema model.
   * Element declaration can be in two places in XSD. It could be
   * declared under global element declaration or could be inside
   * model groups under complex type definitions. This method will check
   * for element in all the complex type definitions
   * @param xsModel xsd xerces schema model
   * @param element lookup element
   * @return XSElementDeclaration objec it it is declared in the ComplexType definitions
   */
  public static Optional<XSElementDeclaration> checkInModelGroup(XSModel xsModel, QName element) {

    if (xsModel != null && element != null) {
      // get all the model groups
      XSNamedMap xsMap = xsModel.getComponents(XSConstants.MODEL_GROUP_DEFINITION);

      // traverses through it and see if it has element
      for (Object modelGroupName : xsMap.keySet()) {
        QName name = (QName) modelGroupName;
        XSModelGroupDefinition groupDefinition = (XSModelGroupDefinition) xsMap.get(name);
        List<XSParticle> particles = groupDefinition.getModelGroup().getParticles();
        for (XSParticle particle : particles) {
          String particleName = particle.getTerm().getName();
          if (particleName != null && particleName.equals(element.getLocalPart())) {
            // if its equal that means it's present,
            // return XSParticle for ModelGroupDefinition
            XSElementDeclaration elementDeclaration = (XSElementDeclaration) particle.getTerm();
            return Optional.of(elementDeclaration);
          }
        }
      }
    }
    return Optional.empty();
  }
}
