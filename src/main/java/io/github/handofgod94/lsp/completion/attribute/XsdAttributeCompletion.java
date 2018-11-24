package io.github.handofgod94.lsp.completion.attribute;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;

public class XsdAttributeCompletion implements AttributeCompletion {

  private final QName currentElement;
  private final SchemaDocument schemaDocument;
  private final XsAdapterFactory adapterFactory;

  @Inject
  XsdAttributeCompletion(@Assisted QName currentElement, @Assisted SchemaDocument schemaDocument, XsAdapterFactory adapterFactory) {
    this.currentElement = currentElement;
    this.schemaDocument = schemaDocument;
    this.adapterFactory = adapterFactory;
  }


  @Override
  public List<CompletionItem> getAttrCompletions() {

    Optional<XSElementDeclaration> element = XmlUtil.checkInElement(schemaDocument.getXsModel(), currentElement);
    element = element.isPresent() ? element : XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), currentElement);

    List<CompletionItem> attributes =
      element
        .map(this::findPossibleAttributes).orElse(new ArrayList<>())
        .stream()
        .map(XsAdapter::toCompletionItem)
        .collect(Collectors.toList());


    return attributes;
  }

  /**
   * Finds all the possible attributes for element.
   *
   * @param element current element obtained from xs models
   * @return list of {@link CompletionItem}
   */
  private List<XsAdapter> findPossibleAttributes(XSElementDeclaration element) {
    List<XsAdapter> attributeCompletionItems = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, getCompletionItems all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
        (XSComplexTypeDefinition) typeDefinition;

      // Traverse through all the attributes and add it to list
      for (Object attrObject : complexTypeDefinition.getAttributeUses()) {
        // TODO: Monitor max and minoccurs in an element.
        // TODO: Add * for required attributes

        attributeCompletionItems.add(adapterFactory.getAttributeAdapter((XSObject) attrObject));
      }

    } else {
      // TODO: for SIMPLE TYPE Definition
    }

    return attributeCompletionItems;
  }
}
