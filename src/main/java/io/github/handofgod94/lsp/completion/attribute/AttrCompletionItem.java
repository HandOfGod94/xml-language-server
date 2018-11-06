package io.github.handofgod94.lsp.completion.attribute;

import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import java.util.ArrayList;
import java.util.List;

public interface AttrCompletionItem {

  /**
   * Get list of attributes for current tag.
   * @return list of {@link CompletionItem}.
   */
  List<CompletionItem> get();

  /**
   * Finds all the possible attributes for element
   * @param element current element obtained from xs models
   * @return list of {@link CompletionItem}
   */
  default List<CompletionItem> findPossibleAttributes(XSElementDeclaration element) {
    List<CompletionItem> attributeCompletionItems = new ArrayList<>();

    // Get type definitions from the element
    XSTypeDefinition typeDefinition = element.getTypeDefinition();

    if (typeDefinition.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
      // if its a complex type, get all the attributes in that element
      XSComplexTypeDefinition complexTypeDefinition =
        (XSComplexTypeDefinition) typeDefinition;

      // Traverse through all the attributes and add it to list
      for (Object attrObject : complexTypeDefinition.getAttributeUses()) {
        XSAttributeUse attr = (XSAttributeUse) attrObject;

        // Initialize AttributeCompletionItem
        CompletionItem info = new CompletionItem();
        String label = attr.getAttrDeclaration().getName();
        info.setLabel(label);
        info.setInsertTextFormat(InsertTextFormat.Snippet);
        info.setInsertText(label + "=\"$1\"");
        info.setKind(CompletionItemKind.Property);

        // TODO: Typechecking or variable references
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
