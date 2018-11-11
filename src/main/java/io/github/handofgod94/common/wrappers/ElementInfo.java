package io.github.handofgod94.common.wrappers;

import io.github.handofgod94.common.XmlUtil;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSElementDeclaration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

public class ElementInfo {
  private XSElementDeclaration elementDeclaration;
  private String name;
  private String namespace;

  public ElementInfo(XSElementDeclaration elementDeclaration) {
    this.elementDeclaration = elementDeclaration;
    name = elementDeclaration.getName();
    namespace = elementDeclaration.getNamespace();
  }

  public CompletionItem toCompletionItem() {
    CompletionItem item = new CompletionItem();
    item.setLabel(name);
    item.setDetail(namespace);
    item.setDocumentation(toMarkupContent());
    item.setKind(CompletionItemKind.Field);
    return item;
  }

  public MarkupContent toMarkupContent() {
    MarkupContent content = new MarkupContent();
    content.setKind(MarkupKind.MARKDOWN);

    StringBuffer docBuffer = new StringBuffer();
    String elementStr = String.format("**ELEMENT**: %s  \n", name);
    docBuffer.append(elementStr);

    String annotation =
      Stream
        .of(Optional.ofNullable(elementDeclaration.getAnnotation()))
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

    content.setValue(docBuffer.toString());
    return content;
  }

  // Getters

  public XSElementDeclaration getElementDeclaration() {
    return elementDeclaration;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }
}
