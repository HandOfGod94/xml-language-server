package io.github.handofgod94.lsp.hover;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import io.github.handofgod94.schema.SchemaDocument;

/**
 * Guice factory for element and attribute hovers.
 */
public interface XmlHoverFactory {

  /**
   * Creates hover instances for Tags.
   * This is useful for xml elements
   * Tags documentation will then be parsed and read
   * and will be provided as hover
   * @param wordHovered word being hovered. It is assumed that it will
   *     name of element for ElementHover
   * @return ElementHover instance to obtain hover.
   */
  @Named("Element")
  XmlHover getTagHover(String wordHovered, SchemaDocument document);

  /**
   * Creates hover instances for Attributes.
   * This is useful for attributes of elements.
   * Each attribute will also be associated with a parent element.
   * Lookup for documentation will consider both parent and attribute
   * name to obtain documentation to display on hover.
   * @param wordHovered word being hovered. It is assumed that it will be
   *     name of the attribute.
   * @param parentName name of the parent element of current attribute.
   * @return AttributeHover instances to obtain hover.
   */
  @Named("Attribute")
  XmlHover getAttributeHover(@Assisted("Element") String wordHovered,
                            @Assisted("Element") SchemaDocument schemaDocument,
                            @Assisted("Attribute") String parentName);
}
