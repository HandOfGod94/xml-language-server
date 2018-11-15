package io.github.handofgod94.schema.wrappers;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.MarkupContent;

/**
 * Adapter interface to work with complex Xerces objects.
 */
public interface XsAdapter {

  String getName();

  /**
   * Generates {@link CompletionItem} for current object.
   * @return CompletionItem having all the required details for completions including
   *     documentation and types(in case of attributes)
   */
  CompletionItem toCompletionItem();

  /**
   * Generates {@link MarkupContent} for documentation for current object.
   * @return MarkupContent having documentation
   */
  MarkupContent toMarkupContent();

}
