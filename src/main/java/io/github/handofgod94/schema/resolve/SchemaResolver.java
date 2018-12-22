package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
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
   *
   * @param xml String having xml content loaded in the editor.
   * @return schema if resolution is successful.
   */
  Optional<SchemaDocument> resolve(String xml);

}
