package io.github.handofgod94.schema.resolve;

import io.github.handofgod94.schema.SchemaDocument;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.eclipse.lsp4j.TextDocumentItem;

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
   * @param documentItem current text document item.
   * @param schemaLocations list of URI having schemaLocations for current document.
   * @return schema if resolution is successful.
   */
  Optional<SchemaDocument> resolve(TextDocumentItem documentItem, List<URI> schemaLocations);

}
