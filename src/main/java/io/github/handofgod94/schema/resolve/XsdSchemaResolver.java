package io.github.handofgod94.schema.resolve;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github.handofgod94.schema.SchemaDocument;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
  private SchemaDocument document;

  @Inject
  XsdSchemaResolver(@Named("Xsd") SchemaDocument document) {
    this.document = document;
  }

  @Override
  public Optional<SchemaDocument> resolve(String xmlText) {
    try {
      SAXReader reader = new SAXReader();
      Document xmlDocument = reader.read(new StringReader(xmlText));
      List<String> schemaUrls = findSchemaUrlsFromXml(xmlDocument);

      // fetch xsd document from url.
      List<String> xsdContent = fetchSchemas(schemaUrls);
      this.document.loadSchema(xsdContent);
      return Optional.ofNullable(document);
    } catch (DocumentException | IOException e) {
      // TODO: It can produce too much of noise if document is continuously in editing state.
      logger.error("Unable to parse document", e);
    }
    return Optional.empty();
  }

  /**
   * Utility method to get all the schema location for given xml.
   * @param xmlDocument parsed xml document object
   * @return list of string having all the urls for schemas declared.
   */
  private List<String> findSchemaUrlsFromXml(Document xmlDocument) {
    String schemaLocation = "";
    List<String> urls = new ArrayList<>();
    Element rootElement = xmlDocument.getRootElement();
    if (rootElement != null) {
      // TODO: Think of something robust. We need to handle
      schemaLocation = rootElement.attributeValue(new QName("schemaLocation",
          new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")));
    }
    String[] locations = schemaLocation.split(" +");
    for (String loc : locations) {
      if (loc.endsWith(".xsd")) {
        // TODO: Check for multiple xsd schema urls.
        urls.add(loc);
      }
    }
    return urls;
  }

  /**
   * Utility method to fetch xsd content from the urls
   * and save it in list of strings.
   * @param urls list of urls to fetch
   * @return List of string having the content of the urls
   * @throws IOException if unable to make request to remote url
   */
  private List<String> fetchSchemas(List<String> urls) throws IOException {
    // TODO: Move it to default interface method
    OkHttpClient client = new OkHttpClient();
    List<String> schemaTextList = new ArrayList<>();

    for (String url: urls) {
      Request request = new Request.Builder().url(url).get().build();
      // TODO: Check if we can make this async, or is it even required?
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        schemaTextList.add(response.body().string());
      }
    }

    return schemaTextList;
  }
}
