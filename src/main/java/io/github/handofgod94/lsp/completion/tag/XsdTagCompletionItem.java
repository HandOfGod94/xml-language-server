package io.github.handofgod94.lsp.completion.tag;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.schema.SchemaDocument;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSParticle;
import org.eclipse.lsp4j.CompletionItem;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class XsdTagCompletionItem implements TagCompletionItem {

  private final QName parentTag;
  private final SchemaDocument schemaDocument;

  @Inject
  XsdTagCompletionItem(@Assisted QName parentTag,
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
      .map(this::findPossibleChildren).orElse(new ArrayList<>());

    return tags;
  }

  // TODO: Change it to pure
  private Optional<XSElementDeclaration> checkElement() {
    XSObject xsObject = schemaDocument.getXsModel().getElementDeclaration(parentTag.getLocalPart(), parentTag.getNamespaceURI());
    if (xsObject != null) {
      XSElementDeclaration elementDeclaration = (XSElementDeclaration) xsObject;
      return Optional.of(elementDeclaration);
    }
    return Optional.empty();
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
        if(particleName != null && particleName.equals(parentTag)) {
          // if its equal that means it's present,
          // return XSParticle for ModelGroupDefinition
          XSElementDeclaration elementDeclaration = (XSElementDeclaration) particle.getTerm();
          return Optional.of(elementDeclaration);
        }
      }
    }
    return Optional.empty();
  }
}
