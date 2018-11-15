package io.github.handofgod94.lsp.completion.element;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapterFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;

public class XsdElementCompletion implements ElementCompletion {

  private final QName parentElement;
  private final SchemaDocument schemaDocument;
  private final XsAdapterFactory adapterFactory;

  @Inject
  XsdElementCompletion(@Assisted QName parentElement,
                       @Assisted SchemaDocument schemaDocument,
                       XsAdapterFactory adapterFactory) {
    this.parentElement = parentElement;
    this.schemaDocument = schemaDocument;
    this.adapterFactory = adapterFactory;
  }

  @Override
  public List<CompletionItem> getCompletionItems() {
    Optional<XSElementDeclaration> element = XmlUtil.checkInElement(schemaDocument.getXsModel(), parentElement);
    element = element.isPresent() ? element : XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), parentElement);

    List<CompletionItem> elements =
      element
        .map(this::findPossibleChildren).orElse(new ArrayList<>())
        .stream()
        .map(XsAdapter::toCompletionItem)
        .collect(Collectors.toList());

    return elements;
  }

  /**
   * Finds all the possible children for an xsd element.
   *
   * @param element XSD Element object pointing to parent element
   * @return List of completion items for the element i.e. parent element.
   */
  private List<XsAdapter> findPossibleChildren(XSElementDeclaration element) {
    List<XsAdapter> tags = new ArrayList<>();

    // Get type definitions for current element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if complex type, then getCompletionItems the children model group
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
          tags.add(adapterFactory.getElementAdapter(ele));
        }
      }
    } else {
      // TODO: for simple type
    }
    return tags;
  }
}
