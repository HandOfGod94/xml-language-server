package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
import org.dom4j.Document;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Interface to support resolution of schema from the url
 * specified.
 */
public interface SchemaResolver {

  /**
   * Reads xml and try to read all the source URLs.
   * These URLs can then be used to generate models or schemas
   * using xerces or javax.
   * @param xml String having xml content loaded in the editor.
   * @return schema if resolution is successful.
   */
  Optional<SchemaDocument> resolve(String xml);

  /**
   * Searches schema URls and should return a list.
   * Currently this lookup will only happen in schemaLocation attribute
   * and will not take place in noSchemaLocation
   * @param document parsed xml document, possibly the current textDocumentItem text
   * @return list of urls
   */
  List<URI> searchSchemaUris(Document document);
}
