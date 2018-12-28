package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.AttributeAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSObject;
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
  private final PositionalHandler.Factory handlerFactory;
  private final TextDocumentItem documentItem;

  @Inject
  AttributeHover(@Assisted String wordHovered,
                 @Assisted SchemaDocument schemaDocument,
                 @Assisted TextDocumentItem documentItem,
                 @Assisted Position position,
                 PositionalHandler.Factory handlerFactory) {
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

    // getCompletionItems current element and its attributes
    XmlUtil.positionalParse(handler, documentItem.getText());
    QName currentElement = handler.getCurrentElement();
    Optional<XSElementDeclaration> element =
        XmlUtil.searchElement(schemaDocument.getXsModel(), currentElement);

    // check in if any attributes or elements matches to wordHovered
    List<XsAdapter> attrList = element.map(this::getAllAttributes).orElse(new ArrayList<>());

    // Get XSObject of the wordHovered if its present in elements or attributes.
    for (XsAdapter attribute : attrList) {
      if (attribute.getName().equals(wordHovered)) {
        content = attribute.toMarkupContent();
      }
    }

    Hover hover = new Hover(content);
    return hover;
  }

  private List<XsAdapter> getAllAttributes(XSElementDeclaration element) {
    List<XsAdapter> attrList = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, getCompletionItems all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
          (XSComplexTypeDefinition) typeDefinition;

      for (Object attrObject : complexTypeDefinition.getAttributeUses()) {
        XSObject attribute = (XSObject) attrObject;
        attrList.add(new AttributeAdapter(attribute));
      }
    } else {
      // TODO: for simple types
    }

    return attrList;
  }

}
