package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.SchemaDocumentType;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * Lookup xsd schema for an xml.
 * TODO:
 * The XSD schema lookup will first happen in local repository,
 * if it's absent a remote lookup will take place.
 */
public class XsdSchemaResolver implements SchemaResolver {

  private static final Logger logger = LogManager.getLogger(XsdSchemaResolver.class.getName());

  @Override
  public Optional<SchemaDocument> resolve(String xmlText) {
    try {
      SAXReader reader = new SAXReader();
      Document xmlDocument = reader.read(new StringReader(xmlText));
      Namespace namespace = xmlDocument.getRootElement().getNamespace();
      List<URI> schemaUris = searchSchemaUris(xmlDocument);

      // generate schema and models
      StreamSource[] sources = generateSources(schemaUris);
      Schema schema = generateSchema(sources);
      XSLoader xsLoader = new XMLSchemaLoader();
      XSModel xsModel = xsLoader.loadURIList(getUriStringList(schemaUris));

      // TODO: set other information regarding schema.
      SchemaDocument document =
          new SchemaDocument.Builder(xsModel, schema, SchemaDocumentType.XSD)
            .addNamespace(namespace)
            .addParsedSchemaDocs(generateParsedSchemaDocs(schemaUris))
            .build();

      return Optional.of(document);
    } catch (DocumentException | IOException | SAXException e) {
      // TODO: It can produce too much of noise if document is continuously in editing state.
      logger.error("Unable to parse or load schema", e);
    }
    return Optional.empty();
  }

  @Override
  public List<URI> searchSchemaUris(Document xmlDocument) {
    String schemaLocation = "";
    List<URI> uris = new ArrayList<>();
    Element root = xmlDocument.getRootElement();

    // find out all the schema location
    if (root != null) {
      // TODO: Think of something robust. It should handle any prefix.
      schemaLocation = root.attributeValue(new QName("schemaLocation",
        new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")));
    }
    String[] locations = schemaLocation.split(" +");

    // create uri's for all the strings ending .xsd
    for (String loc : locations) {
      if (loc.endsWith(".xsd")) {
        URI uri = URI.create(loc);
        uris.add(uri);
      }
    }

    return uris;
  }

  private List<Document> generateParsedSchemaDocs(List<URI> schemaUris)
      throws DocumentException, IOException {
    StreamSource[] sources = generateSources(schemaUris);
    SAXReader reader = new SAXReader();
    List<Document> documents = new ArrayList<>();
    for (StreamSource source : sources) {
      Document document = reader.read(source.getInputStream());
      documents.add(document);
    }
    return documents;
  }

  private StreamSource[] generateSources(List<URI> schemaUris) throws IOException {

    // Generate stream source for uris
    StreamSource[] sources = new StreamSource[schemaUris.size()];
    for (int i = 0; i < sources.length; i++) {
      sources[i] = new StreamSource(schemaUris.get(i).toURL().openStream());
    }
    return sources;
  }

  private Schema generateSchema(StreamSource[] sources) throws SAXException {
    // Generate schema using the stream sources
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(sources);
    return schema;
  }

  private StringList getUriStringList(List<URI> uris) {
    String[] str = new String[uris.size()];
    for (int i = 0; i < str.length; i++) {
      str[i] = uris.get(i).toString();
    }
    StringList stringList = new StringListImpl(str, str.length);
    return stringList;
  }
}
