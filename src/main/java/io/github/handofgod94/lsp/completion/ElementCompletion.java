package io.github.handofgod94.lsp.completion;

import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.ElementAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;

public class ElementCompletion extends AbstractXmlCompletion {

  ElementCompletion(SchemaDocument schemaDocument,
                    QName qName) {
    super(schemaDocument, qName);
  }

  @Override
  protected List<XsAdapter> findPossibleChildren(XSElementDeclaration element) {
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
          tags.add(new ElementAdapter(ele));
        }
      }
    } else {
      // TODO: for simple type
    }
    return tags;
  }
}
