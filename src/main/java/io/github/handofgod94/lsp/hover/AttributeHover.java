package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.common.parser.PositionalHandlerFactory;
import io.github.handofgod94.common.wrappers.AttributeInfo;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Hover information for attributes.
 */
public class AttributeHover implements XmlHover {

  private final String wordHovered;
  private final SchemaDocument schemaDocument;
  private final Position position;
  private final PositionalHandlerFactory handlerFactory;
  private final TextDocumentItem documentItem;

  @Inject
  AttributeHover(@Assisted String wordHovered,
                 @Assisted SchemaDocument schemaDocument,
                 @Assisted TextDocumentItem documentItem,
                 @Assisted Position position,
                 PositionalHandlerFactory handlerFactory) {
    this.wordHovered = wordHovered;
    this.schemaDocument = schemaDocument;
    this.position = position;
    this.handlerFactory = handlerFactory;
    this.documentItem = documentItem;
  }

  @Override
  public Hover getHover() {
    MarkupContent content = new MarkupContent();

    // parse using positional handler
    PositionalHandler handler = handlerFactory.create(position);

    // get current element and its attributes
    XmlUtil.positionalParse(handler, documentItem.getText());
    QName qname = handler.getCurrentElement();
    // TODO: could throw error if qname is absent
    Optional<XSElementDeclaration> optElement =
      XmlUtil.checkInElement(schemaDocument.getXsModel(), qname);

    XSElementDeclaration element = optElement.isPresent() ? optElement.get(): XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), qname).get();

    // check in if any attributes or elements matches to wordHovered
    List<AttributeInfo> attrList = getAllAttributes(element);

    // Get XSObject of the wordHovered if its present in elements or attributes.
    for (AttributeInfo attribute : attrList) {
      if (attribute.getName().equals(wordHovered)) {
        content = attribute.toMarkupContent();
      }
    }

    // Get annotation and documentation of XSObject and show it in hover.

    Hover hover = new Hover(content);
    return hover;
  }

  private List<AttributeInfo> getAllAttributes(XSElementDeclaration element) {
    List<AttributeInfo> attrList = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, get all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
        (XSComplexTypeDefinition) typeDefinition;

      for (Object attrObject: complexTypeDefinition.getAttributeUses()) {
        XSAttributeUse attributeUse = (XSAttributeUse) attrObject;

        // TODO: guice injections
        attrList.add(new AttributeInfo(attributeUse));
      }
    } else {
      // TODO: for simple types
    }

    return attrList;
  }

}
