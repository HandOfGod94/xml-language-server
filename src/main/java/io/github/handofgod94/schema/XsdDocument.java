package io.github.handofgod94.schema;

import java.io.StringReader;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.xml.sax.SAXException;

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
   * @param schemaTextList list of string having content of xsds.
   */
  @Override
  public void loadSchema(List<String> schemaTextList) {
    try {
      XSLoader schemaLoader = new XMLSchemaLoader();
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      StreamSource[] sources = getSchemaSoruces(schemaTextList);
      this.schema = factory.newSchema(sources);
      // Load XSModel for multiple URI List
      // this.xsModel = schemaLoader.loadURI(xsdFile.toURI().toString());
    } catch (SAXException ex) {
      logger.error("Error while loading schema", ex);
    }
  }

  private StreamSource[] getSchemaSoruces(List<String> schemaStringList) {
    StreamSource[] sources = new StreamSource[schemaStringList.size()];
    for (int i = 0; i < sources.length; i++) {
      sources[i] = new StreamSource(new StringReader(schemaStringList.get(i)));
    }
    return sources;
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
