package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;

import java.util.Optional;

@FunctionalInterface
public interface SchemaResolver {

  /**
   * Reads xml and try to fetch the document.
   * The fetched document will be stored in repository for future reference.
   * The lookup will first happen in local repository and then will try to fetch
   * it from web, if it's a url. If the schema location is in local disk, then
   * fetching won't take place, it'll just store it in local reposiory
   * @param xml String having xml content loaded in the editor.
   * @return SchemaDocument object if retrieving of schema is successful.
   */
  Optional<SchemaDocument> resolve(String xml);

}
