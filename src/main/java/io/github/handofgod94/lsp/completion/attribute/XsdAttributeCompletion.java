package io.github.handofgod94.lsp.completion.attribute;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.wrappers.AttributeInfo;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;

public class XsdAttributeCompletion implements AttributeCompletion {

  private final QName currentElement;
  private final SchemaDocument schemaDocument;

  @Inject
  XsdAttributeCompletion(@Assisted QName currentElement, @Assisted SchemaDocument schemaDocument) {
    this.currentElement = currentElement;
    this.schemaDocument = schemaDocument;
  }


  @Override
  public List<CompletionItem> get() {
    // TODO: Check in only one (element or model group) not in both always
    List<CompletionItem> attributes =
        Stream.of(
            XmlUtil.checkInElement(schemaDocument.getXsModel(), currentElement),
            XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), currentElement)
          )
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst()
          .map(this::findPossibleAttributes)
          .orElse(new ArrayList<>())
          .stream()
          .map(AttributeInfo::toCompletionItem)
          .collect(Collectors.toList());

    return attributes;
  }

  /**
   * Finds all the possible attributes for element.
   *
   * @param element current element obtained from xs models
   * @return list of {@link CompletionItem}
   */
  private List<AttributeInfo> findPossibleAttributes(XSElementDeclaration element) {
    List<AttributeInfo> attributeCompletionItems = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, get all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
          (XSComplexTypeDefinition) typeDefinition;

      // Traverse through all the attributes and add it to list
      for (Object attrObject : complexTypeDefinition.getAttributeUses()) {
        XSAttributeUse attr = (XSAttributeUse) attrObject;
        AttributeInfo info = new AttributeInfo(attr);
        // TODO: Guice injection
        // TODO: Monitor max and minoccurs in an element.
        // TODO: Add * for required attributes

        attributeCompletionItems.add(info);
      }

    } else {
      // TODO: for SIMPLE TYPE Definition
    }

    return attributeCompletionItems;
  }
}
