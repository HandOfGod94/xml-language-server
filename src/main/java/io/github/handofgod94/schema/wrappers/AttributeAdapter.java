package io.github.handofgod94.schema.wrappers;

import io.github.handofgod94.common.XmlUtil;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

/**
 * Wrapper class around {@link XSAttributeUse}.
 * It accepts the {@link XSAttributeUse} and provides additional
 * method to retrieve information required for completions and hovers.
 */
public class AttributeAdapter implements XsAdapter {
  private final XSAttributeDeclaration attributeDeclaration;
  private final String name;
  private final XSAttributeUse attributeUse;
  private final XSSimpleTypeDefinition type;

  /**
   * Creates new object. It will hold necessary the information
   * related attributes
   *
   * @param xsObject {@link XSAttributeUse} object
   */
  public AttributeAdapter(XSObject xsObject) {
    this.attributeUse = (XSAttributeUse) xsObject;
    attributeDeclaration = this.attributeUse.getAttrDeclaration();
    name = attributeDeclaration.getName();
    type = attributeDeclaration.getTypeDefinition();
  }

  /**
   * Generates {@link CompletionItem} for current attribute.
   * This will be used by editors to list completions items for attributes.
   * For attributes, it contains "name", "type" and "documentation".
   *
   * @return CompletionItem object having all the relevant information.
   */
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

  /**
   * Generates documentation markup.
   * Standard format is:
   * <pre>
   *   ATTRIBUTE: (attribute-name)
   *   DESCRIPTION: (documentation for attribute)
   *   TYPE: data type which the attribute accepts
   * </pre>
   *
   * @return MarkupContent object having formatted documentation as described in doc
   */
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

    // certain xsd such as maven-pom.xsd has multiple documentation elements, such as
    // version and other info.
    // In that case, getCompletionItems the one which has attribute "source".
    Optional<Document> annotationDoc = XmlUtil.createParsedDoc(annotation);
    annotationDoc.ifPresent(doc -> {
      String descKey = "DESCRIPTION";
      Element annotations = doc.getRootElement();

      // traverse through all the annotations
      for (Iterator<Element> it = annotations.elementIterator(); it.hasNext(); ) {
        Element documentation = it.next();
        if (documentation.attributeCount() > 0) {
          descKey =
            (documentation.attributeValue("source") != null)
              ? documentation.attributeValue("source").toUpperCase() : descKey;
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

  @Override
  public String getName() {
    return name;
  }

  // Getters

  public XSAttributeDeclaration getAttributeDeclaration() {
    return attributeDeclaration;
  }

  public XSAttributeUse getAttributeUse() {
    return attributeUse;
  }

  public XSSimpleTypeDefinition getType() {
    return type;
  }
}
