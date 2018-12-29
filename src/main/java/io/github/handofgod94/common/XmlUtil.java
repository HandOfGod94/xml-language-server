package io.github.handofgod94.common;

import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.common.parser.XmlState;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSParticle;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.eclipse.lsp4j.TextDocumentItem;
import org.xml.sax.SAXException;

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

  public static Optional<XSElementDeclaration> searchElement(XSModel xsModel, QName qname) {
    if (qname == null) return Optional.empty();

    // if present in global context, then directly send it
    XSElementDeclaration global = xsModel.getElementDeclaration(qname.getLocalPart(), qname.getNamespaceURI());
    if (global != null) return Optional.of(global);

    // other wise search it in children
    XSNamedMap xsMap = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
    for (int i = 0; i < xsMap.getLength(); i++) {
      XSObject object = xsMap.item(i);
      Optional<XSElementDeclaration> element = searchElement(object, object.getType(), qname);
      if (element.isPresent()) return element;
    }

    return Optional.empty();
  }

  private static Optional<XSElementDeclaration> searchElement(XSObject object, short type, QName qname) {
    if (type == XSConstants.ELEMENT_DECLARATION) {
      XSElementDeclaration element = (XSElementDeclaration) object;
      if (element.getName().equals(qname.getLocalPart())
          && element.getNamespace().equals(qname.getNamespaceURI())) return Optional.of(element);

      // TODO: Handle SimpleType elements
      XSComplexTypeDefinition types = (XSComplexTypeDefinition) element.getTypeDefinition();
      return searchElement(types.getParticle(), types.getParticle().getType(), qname);
    }

    if (type == XSConstants.PARTICLE) {
      XSParticle particle = (XSParticle) object;
      return searchElement(particle.getTerm(), particle.getTerm().getType(), qname);
    }

    if (type == XSConstants.MODEL_GROUP) {
      XSModelGroup modelGroup = (XSModelGroup) object;
      List<XSParticle> particles = modelGroup.getParticles();
      for(XSParticle particle: particles) {
        String name = particle.getTerm().getName();
        if (name != null && name.equals(qname.getLocalPart()))
          return searchElement(particle.getTerm(), particle.getTerm().getType(), qname);
      }
    }

    if (type == XSConstants.MODEL_GROUP_DEFINITION) {
      XSModelGroupDefinition groupDef = (XSModelGroupDefinition) object;
      XSModelGroup modelGroup = groupDef.getModelGroup();
      return searchElement(modelGroup, modelGroup.getType(), qname);
    }

    return Optional.empty();
  }

  /**
   * Generates XML Schema instance object.
   * @apiNote  Since we are not providing any sources for schema, then it'll
   *     automatically look for schema location in the document itself and will
   *     load the schema.
   * @return A standard XML Schema object following 2001 XML namespace
   * @throws SAXException If unable to create schema object.
   */
  public static Schema generateSchema() throws SAXException {
    // Generate schema using the stream sources
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema();
    return schema;
  }

  /**
   * Check for well formed XML.
   * The schema validation and other LSP features, could only work with
   * well formed XML documents. This utility provides a helper function
   * to check for XML errors. If any errors are present, then it'll calculate
   * position and error messages present in the XML and will store it in a map.
   * If no errors are present, then it'll return an empty Map.
   * @param documentItem current text document item.
   * @return A Map containing error position and message if present, empty otherwise.
   */
  public static XmlState checkWellFormedXml(TextDocumentItem documentItem) {
    XmlState xmlState = new XmlState();
    try {
      String xmlLocation = URLDecoder.decode(documentItem.getUri(),"UTF-8");
      logger.info("Resolving schema for XML: {}", xmlLocation);
      File file = new File(URI.create(xmlLocation));

      Validator validator = generateSchema().newValidator();
      validator.setErrorHandler(xmlState);
      validator.setResourceResolver(xmlState);
      validator.validate(new StreamSource(file));
      logger.debug("Successfully resolved schemas for {}", xmlLocation);
    } catch (SAXException | IOException e) {
      logger.info("Document seems to be malformed or unable to retrieve schema", e);
    }

    return xmlState;
  }
}
