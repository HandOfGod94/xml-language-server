package io.github.handofgod94.lsp.completion.tag;

import io.github.handofgod94.common.XmlUtil;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public interface TagCompletionItem {

  /**
   * Get all the completion item for the tag.
   * @return List of completion item at current position
   */
  List<CompletionItem> get();

  /**
   * Finds all the possible children for an xsd element
   * @param element XSD Element object pointing to parent tag
   * @return List of completion items for the element i.e. parent tag.
   */
  default List<CompletionItem> findPossibleChildren(XSElementDeclaration element) {
    List<CompletionItem> tagCompletionItems = new ArrayList<>();

    // Get type definitions for current element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if complex type, then get the children model group
      XSComplexTypeDefinition complexTypeDefinition =
        (XSComplexTypeDefinition) typeDefinition;
      XSParticle rootParticle = complexTypeDefinition.getParticle();
      List<String> elements = new ArrayList<>();

      // Recursively look for all the applicable elements
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
          elements.add(ele.getName());
        }
      }

      for (String ele : elements) {
        CompletionItem item = new CompletionItem();
        item.setLabel(ele);
        item.setKind(CompletionItemKind.Field);
        // Only add non empty strings to list and
        if (!XmlUtil.XSD_KEYS.contains(ele)) {
          tagCompletionItems.add(item);
        }
      }
    } else {
      // TODO: for simple type
    }
    return tagCompletionItems;
  }

}
