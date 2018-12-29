package io.github.handofgod94.schema.wrappers;

import com.google.inject.name.Named;
import org.apache.xerces.xs.XSObject;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.MarkupContent;

/**
 * Adapter interface to work with complex Xerces objects.
 */
public interface XsAdapter {

  interface Factory {
    @Named("Element")
    ElementAdapter createElementAdapter(XSObject object);

    @Named("Attribute")
    AttributeAdapter cretAttributeAdatper(XSObject object);
  }

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
