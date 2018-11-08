package io.github.handofgod94.lsp.completion.attribute;

import io.github.handofgod94.schema.SchemaDocument;
import javax.xml.namespace.QName;

/**
 * Guice factory to generate attribute completions
 */
public interface AttributeCompletionFactory {

  /**
   * Generates instances for {@link XsdAttributeCompletion}.
   * This will be useful to get possible attribute at current location
   * @param currentTag current tag for which we need attributes.
   * @param schemaDocument {@link SchemaDocument} instance object.
   * @return object for {@link XsdAttributeCompletion}.
   */
  AttributeCompletion create(QName currentTag, SchemaDocument schemaDocument);
}
