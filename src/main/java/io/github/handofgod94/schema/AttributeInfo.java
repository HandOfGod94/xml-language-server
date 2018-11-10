package io.github.handofgod94.schema;

import io.github.handofgod94.common.XmlUtil;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

public class AttributeInfo {
  private XSAttributeDeclaration attributeDeclaration;
  private String name;
  private XSAttributeUse attributeUse;
  private XSSimpleTypeDefinition type;

  public AttributeInfo(XSAttributeUse attributeUse) {
    this.attributeUse = attributeUse;
    attributeDeclaration = attributeUse.getAttrDeclaration();
    name = attributeDeclaration.getName();
    type = attributeDeclaration.getTypeDefinition();
  }

  public CompletionItem toCompletionItem() {
    CompletionItem info = new CompletionItem();
    info.setLabel(name);
    info.setDetail(type.getName());
    info.setDocumentation(toMarkupContent());
    info.setInsertTextFormat(InsertTextFormat.Snippet);
    info.setInsertText(name + "=\"$1\"");
    info.setKind(CompletionItemKind.Property);
    return info;
  }

  public MarkupContent toMarkupContent() {

    MarkupContent content = new MarkupContent();
    content.setKind(MarkupKind.MARKDOWN);

    StringBuffer docBuffer = new StringBuffer();
    String attrStr = String.format("**ATTRIBUTE**: %s  \n", name);
    docBuffer.append(attrStr);

    String annotation =
      Stream
        .of(Optional.ofNullable(attributeDeclaration.getAnnotation()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(XSAnnotation::getAnnotationString)
        .findFirst().orElse("");

    // certain xsd such as maven-pom.xsd has multiple documentation elements, such as version and other info.
    // In that case, get the one which has attribute "source".
    Optional<Document> annotationDoc = XmlUtil.createParsedDoc(annotation);
    annotationDoc.ifPresent(doc -> {
      String descKey = "DESCRIPTION";
      Element annotations = doc.getRootElement();

      // traverse through all the annotations
      for (Iterator<Element> it = annotations.elementIterator(); it.hasNext() ; ) {
        Element documentation = it.next();
        if (documentation.attributeCount() > 0) {
          descKey =
            (documentation.attributeValue("source") != null) ?
                documentation.attributeValue("source").toUpperCase() : descKey;
        }
        String descValue = documentation.getTextTrim();
        String descriptionDoc = String.format("**%s**: %s  \n", descKey, descValue);
        docBuffer.append(descriptionDoc);
      }
    });

    String typeDoc = String.format("**TYPE**: %s  \n", type.getName());
    docBuffer.append(typeDoc);
    content.setValue(docBuffer.toString());

    return content;
  }

  // Getters

  public XSAttributeDeclaration getAttributeDeclaration() {
    return attributeDeclaration;
  }

  public String getName() {
    return name;
  }

  public XSAttributeUse getAttributeUse() {
    return attributeUse;
  }

  public XSSimpleTypeDefinition getType() {
    return type;
  }
}
