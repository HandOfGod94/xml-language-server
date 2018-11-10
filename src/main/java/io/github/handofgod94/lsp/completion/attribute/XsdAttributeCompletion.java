package io.github.handofgod94.lsp.completion.attribute;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSParticle;
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
    List<CompletionItem> attributes =
        Stream.of(checkElement(), checkModelGroup())
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst()
          .map(this::findPossibleAttributes)
          .orElse(new ArrayList<>())
          .stream()
          .map(AttributeCompletionItem::toCompletionItem)
          .collect(Collectors.toList());

    return attributes;
  }

  private Optional<XSElementDeclaration> checkElement() {
    XSElementDeclaration xsObject =
        schemaDocument.getXsModel()
          .getElementDeclaration(currentElement.getLocalPart(),
              currentElement.getNamespaceURI());
    return Optional.ofNullable(xsObject);
  }

  private Optional<XSElementDeclaration> checkModelGroup() {
    // get all the model groups
    XSNamedMap xsMap = schemaDocument.getXsModel()
        .getComponents(XSConstants.MODEL_GROUP_DEFINITION);

    // traverses through it and see if it has element
    for (Object modelGroupName : xsMap.keySet()) {
      QName name = (QName) modelGroupName;
      XSModelGroupDefinition groupDefinition = (XSModelGroupDefinition) xsMap.get(name);
      List<XSParticle> particles = groupDefinition.getModelGroup().getParticles();
      for (XSParticle particle : particles) {
        String particleName = particle.getTerm().getName();
        if (particleName != null && particleName.equals(currentElement.getLocalPart())) {
          // if its equal that means it's present,
          // return XSParticle for ModelGroupDefinition
          XSElementDeclaration elementDeclaration = (XSElementDeclaration) particle.getTerm();
          return Optional.of(elementDeclaration);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Finds all the possible attributes for element.
   *
   * @param element current element obtained from xs models
   * @return list of {@link CompletionItem}
   */
  private List<AttributeCompletionItem> findPossibleAttributes(XSElementDeclaration element) {
    List<AttributeCompletionItem> attributeCompletionItems = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, get all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
          (XSComplexTypeDefinition) typeDefinition;

      // Traverse through all the attributes and add it to list
      for (Object attrObject : complexTypeDefinition.getAttributeUses()) {
        XSAttributeUse attr = (XSAttributeUse) attrObject;
        AttributeCompletionItem item =
            new AttributeCompletionItem(attr.getAttrDeclaration().getName(),
                attr.getAttrDeclaration().getTypeDefinition(),
                0, 0, false);
        // TODO: Monitor max and minoccurs in an element.
        // TODO: Add * for required attributes

        attributeCompletionItems.add(item);
      }

    } else {
      // TODO: for SIMPLE TYPE Definition
    }

    return attributeCompletionItems;
  }
}
