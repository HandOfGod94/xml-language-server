package io.github.handofgod94.lsp.hover;

import com.google.inject.name.Named;
import io.github.handofgod94.schema.SchemaDocument;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Guice factory for element and attribute hovers.
 */
public interface XmlHoverFactory {

  /**
   * Creates hover instances for Elements.
   * Elements documentation will then be parsed and read
   * and will be provided as hover
   * @param wordHovered word being hovered. It is assumed that it will
   *     name of element for ElementHover
   * @param document schema document having xsModel and schema for current document
   * @param documentItem current text document loaded in editor
   * @param position current hover position
   * @return ElementHover instance to obtain hover.
   */
  @Named("Element")
  XmlHover getElementHover(String wordHovered,
                           SchemaDocument document,
                           TextDocumentItem documentItem,
                           Position position);

  /**
   * Creates hover instances for Attributes.
   * This is useful for attributes of elements.
   * Each attribute will also be associated with a parent element.
   * Lookup for documentation will consider both parent and attribute
   * name to obtain documentation to display on hover.
   * @param wordHovered word being hovered. It is assumed that it will be name of the attribute
   * @param schemaDocument schema document having xsModel and schema for current document
   * @param position current hover position
   * @param documentItem current text document
   * @return AttributeHover instances to obtain hover.
   */
  @Named("Attribute")
  XmlHover getAttributeHover(String wordHovered,
                             SchemaDocument schemaDocument,
                             TextDocumentItem documentItem,
                             Position position);
}
