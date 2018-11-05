package io.github.handofgod94.lsp.completion.tag;

import io.github.handofgod94.schema.SchemaDocument;
import javax.xml.namespace.QName;

/**
 * Guice factory for TagCompletion.
 */
public interface TagCompletionItemFactory {

  /**
   * Creates instance for tag completion item.
   * @param parentTag parent tag for current position.
   * @param schemaDocument SchemaDocument having all the parsed docs and xsds.
   * @return TagCompletionItem
   */
  TagCompletionItem create(QName parentTag, SchemaDocument schemaDocument);

}
