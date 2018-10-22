package io.github.handofgod94.schema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Schema holder class for the XMLs.
 * The XSD document from the schema location defined in the root element of XML will be
 * fetched and loaded in this container class.
 */
public class XsdDocument implements SchemaDocument {
  // TODO: handle multiple schema files
  private static final Logger logger = LogManager.getLogger(XsdDocument.class.getName());

  // XSD Schema files fetched from the URL
  private XSModel xsModel;
  private Schema schema;

  /**
   * Loads XSD schema into XSModel object.
   * The XSModel is the model object provided by Xerces. This class is the
   * container for all the XSD elements.
   * @param schemaText XSD file in string format.
   */
  @Override
  public void loadSchema(String schemaText) {
    try {
      XSLoader schemaLoader = new XMLSchemaLoader();
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      this.schema = factory.newSchema(new StreamSource(new StringReader(schemaText)));
      // TODO: Find out better way to load model then writing to a temporary file.
      File xsdFile = File.createTempFile("cache", ".tmp");
      this.xsModel = schemaLoader.loadURI(xsdFile.toURI().toString());
    } catch (SAXException | IOException ex) {
      logger.error("Error while loading schema", ex);
    }
  }

  @Override
  public XSModel getXsModel() {
    return xsModel;
  }

  @Override
  public Schema getSchema() {
    return schema;
  }
}
