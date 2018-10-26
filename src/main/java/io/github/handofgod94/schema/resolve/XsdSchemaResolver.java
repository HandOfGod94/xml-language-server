package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.XsdDocument;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

/**
 * Lookup xsd schema for an xml.
 * The XSD schema lookup will first happen in local repository,
 * if it's absent a remote lookup will take place.
 */
public class XsdSchemaResolver implements SchemaResolver {

  private static final Logger logger = LogManager.getLogger(XsdSchemaResolver.class.getName());

  // TODO: figure our a way to make this dynamic
  private static final String XSD_NAMEPSACE_DEFAULT_PREFIX = "xsi";

  @Override
  public Optional<SchemaDocument> resolve(String xmlText) {
    try {
      SAXReader reader = new SAXReader();
      Document xmlDocument = reader.read(new StringReader(xmlText));
      String schemaUrl = findSchemaUrlFromXml(xmlDocument);

      // fetch xsd document from url.
      String xsdContent = fetchSchema(schemaUrl);
      SchemaDocument document = new XsdDocument();
      document.loadSchema(xsdContent);
      return Optional.ofNullable(document);
    } catch (DocumentException | IOException e) {
      // TODO: It can produce too much of noise if document is continuously in editing state.
      logger.error("Unable to parse document", e);
    }
    return Optional.empty();
  }

  private String findSchemaUrlFromXml(Document xmlDocument) {
    String url = "";
    String schemaLocation = "";
    Element rootElement = xmlDocument.getRootElement();
    if (rootElement != null) {
      // TODO: Think of something robust. We need to handle
      schemaLocation = rootElement.attributeValue(new QName("schemaLocation",
          new Namespace(XSD_NAMEPSACE_DEFAULT_PREFIX, "http://www.w3.org/2001/XMLSchema-instance")));
    }
    String[] locations = schemaLocation.split(" +");
    for (String loc : locations) {
      if (loc.endsWith(".xsd")) {
        // TODO: Check for multiple xsd schema urls.
        url = loc;
        break;
      }
    }
    return url;
  }

  private String fetchSchema(String url) throws IOException {
    // TODO: Move it to default interface method
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).get().build();
    // TODO: Check if we can make this async, or is it even required?
    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}
