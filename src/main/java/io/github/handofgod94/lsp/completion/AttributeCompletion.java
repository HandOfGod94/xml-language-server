package io.github.handofgod94.lsp.completion;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.AttributeAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSTypeDefinition;

public class AttributeCompletion extends AbstractXmlCompletion {

  private final XsAdapter.Factory factory;

  @Inject
  AttributeCompletion(@Assisted SchemaDocument schemaDocument,
                      @Assisted QName qname,
                      XsAdapter.Factory factory) {
    super(schemaDocument, qname);
    this.factory = factory;
  }

  @Override
  protected List<XsAdapter> findPossibleChildren(XSElementDeclaration element) {
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

        attributeCompletionItems.add(factory.cretAttributeAdatper((XSObject) attrObject));
      }

    } else {
      // TODO: for SIMPLE TYPE Definition
    }

    return attributeCompletionItems;
  }
}
