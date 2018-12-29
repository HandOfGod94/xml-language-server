package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.SchemaDocumentType;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Lookup xsd schema for an xml.
 * TODO:
 * The XSD schema lookup will first happen in local repository,
 * if it's absent a remote lookup will take place.
 */
public class XsdSchemaResolver implements SchemaResolver {

  private static final Logger logger = LogManager.getLogger(XsdSchemaResolver.class.getName());

  @Override
  public Optional<SchemaDocument> resolve(TextDocumentItem documentItem,
                                          List<URI> schemaLocations) {

    try {
      String xmlText = documentItem.getText();
      SAXReader reader = new SAXReader();
      Document xmlDocument = reader.read(new StringReader(xmlText));
      Namespace namespace = xmlDocument.getRootElement().getNamespace();

      XSLoader xsLoader = new XMLSchemaLoader();
      XSModel xsModel = xsLoader.loadURIList(getUriStringList(schemaLocations));

      // TODO: set other information regarding schema.
      SchemaDocument document =
          new SchemaDocument.Builder(xsModel, SchemaDocumentType.XSD)
          .addNamespace(namespace)
          .addParsedSchemaDocs(generateParsedSchemaDocs(schemaLocations))
          .build();

      return Optional.of(document);
    } catch (DocumentException | IOException e) {
      // TODO: It can produce too much of noise if document is continuously in editing state.
      logger.error("Unable to parse or load schema", e);
    }
    return Optional.empty();
  }

  /**
   * Get all the xsd docs retrieved and store it in a List.
   * This is required to get documentation information for the elements.
   *
   * @param schemaUris list of URI present in current document
   * @return List of parsed document objects
   * @throws DocumentException if unable to parse the XSD retried.
   * @throws IOException       Unable to create reader to read XSD.
   */
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

  private StringList getUriStringList(List<URI> uris) {
    String[] str = new String[uris.size()];
    for (int i = 0; i < str.length; i++) {
      str[i] = uris.get(i).toString();
    }
    StringList stringList = new StringListImpl(str, str.length);
    return stringList;
  }
}
