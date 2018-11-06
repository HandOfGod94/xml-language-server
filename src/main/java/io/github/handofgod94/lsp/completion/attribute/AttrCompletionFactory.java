package io.github.handofgod94.lsp.completion.attribute;

import io.github.handofgod94.schema.SchemaDocument;
import javax.xml.namespace.QName;

/**
 * Guice factory to generate attribute completions
 */
public interface AttrCompletionFactory {

  /**
   * Generates instances for {@link XsdAttrCompletionItem}.
   * This will be useful to get possible attribute at current location
   * @param currentTag current tag for which we need attributes.
   * @param schemaDocument {@link SchemaDocument} instance object.
   * @return object for {@link XsdAttrCompletionItem}.
   */
  XsdAttrCompletionItem create(QName currentTag, SchemaDocument schemaDocument);
}
