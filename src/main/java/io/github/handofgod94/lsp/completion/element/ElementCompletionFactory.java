package io.github.handofgod94.lsp.completion.element;

import io.github.handofgod94.schema.SchemaDocument;
import javax.xml.namespace.QName;

/**
 * Guice factory for ElementCompletion.
 */
public interface ElementCompletionFactory {

  /**
   * Creates instance for element completion item.
   * @param parentElement parent element for current position.
   * @param schemaDocument SchemaDocument having all the parsed docs and xsds.
   * @return ElementCompletion
   */
  ElementCompletion create(QName parentElement, SchemaDocument schemaDocument);

}
