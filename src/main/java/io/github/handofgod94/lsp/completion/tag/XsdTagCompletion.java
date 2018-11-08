package io.github.handofgod94.lsp.completion.tag;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.schema.SchemaDocument;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XsdTagCompletion implements TagCompletion {

  private final QName parentTag;
  private final SchemaDocument schemaDocument;

  @Inject
  XsdTagCompletion(@Assisted QName parentTag,
                   @Assisted SchemaDocument schemaDocument) {
    this.parentTag = parentTag;
    this.schemaDocument = schemaDocument;
  }

  @Override
  public List<CompletionItem> get() {
    // Search if the parent tag is in element declaration or model group definitions
    List<CompletionItem> tags =
      Stream.of(checkElement(), checkModelGroup())
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .map(this::findPossibleChildren).orElse(new ArrayList<>())
      .stream()
      .map(TagCompletionItem::toCompletionItem)
      .collect(Collectors.toList());

    return tags;
  }

  private Optional<XSElementDeclaration> checkElement() {
    XSElementDeclaration xsObject =
        schemaDocument.getXsModel()
        .getElementDeclaration(parentTag.getLocalPart(), parentTag.getNamespaceURI());
    return Optional.ofNullable(xsObject);
  }

  // TODO: Change it to pure
  private Optional<XSElementDeclaration> checkModelGroup() {
    // get all the model groups
    XSNamedMap xsMap = schemaDocument.getXsModel().getComponents(XSConstants.MODEL_GROUP_DEFINITION);

    // traverses through it and see if it has element
    for (Object modelGroupName: xsMap.keySet()) {
      QName name = (QName) modelGroupName;
      XSModelGroupDefinition groupDefinition = (XSModelGroupDefinition) xsMap.get(name);
      List<XSParticle> particles = groupDefinition.getModelGroup().getParticles();
      for (XSParticle particle : particles) {
        String particleName = particle.getTerm().getName();
        if(particleName != null && particleName.equals(parentTag.getLocalPart())) {
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
   * Finds all the possible children for an xsd element
   * @param element XSD Element object pointing to parent tag
   * @return List of completion items for the element i.e. parent tag.
   */
  private List<TagCompletionItem> findPossibleChildren(XSElementDeclaration element) {
    List<TagCompletionItem> tags = new ArrayList<>();

    // Get type definitions for current element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if complex type, then get the children model group
      XSComplexTypeDefinition complexTypeDefinition =
        (XSComplexTypeDefinition) typeDefinition;
      XSParticle rootParticle = complexTypeDefinition.getParticle();

      // Recursively look for all the applicable tags
      // and add it to completion list.
      Stack<XSTerm> xsObjects = new Stack<>();
      xsObjects.push(rootParticle.getTerm());
      while (!xsObjects.isEmpty()) {
        XSTerm term = xsObjects.pop();
        if (term.getType() == XSConstants.MODEL_GROUP) {
          XSModelGroup modelGroup = (XSModelGroup) term;
          List<XSParticle> particles = modelGroup.getParticles();
          particles.forEach(p -> xsObjects.push(p.getTerm()));
        } else if (term.getType() == XSConstants.ELEMENT_DECLARATION) {
          XSElementDeclaration ele = (XSElementDeclaration) term;
          tags.add(new TagCompletionItem(ele.getName(), ele.getNamespace(), ""));
        }
      }
    } else {
      // TODO: for simple type
    }
    return tags;
  }
}
