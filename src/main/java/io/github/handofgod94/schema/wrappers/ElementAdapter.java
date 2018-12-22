package io.github.handofgod94.schema.wrappers;

import io.github.handofgod94.common.XmlUtil;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

/**
 * Wrapper class around {@link XSElementDeclaration}.
 * It accepts the {@link XSElementDeclaration} and provides additional
 * method to retrieve information required for completions and hovers.
 */
public class ElementAdapter implements XsAdapter {
  private final XSElementDeclaration elementDeclaration;
  private final String name;
  private final String namespace;

  /**
   * Creates new object. It will hold necessary the information
   * related elements.
   *
   * @param xsObject {@link XSElementDeclaration} object
   */
  public ElementAdapter(XSObject xsObject) {
    this.elementDeclaration = (XSElementDeclaration) xsObject;
    name = elementDeclaration.getName();
    namespace = elementDeclaration.getNamespace();
  }

  /**
   * Generates {@link CompletionItem} for current attribute.
   * This will be used by editors to list completions items for elements.
   *
   * @return CompletionItem object having all the relevant information.
   */
  public CompletionItem toCompletionItem() {
    CompletionItem item = new CompletionItem();
    item.setLabel(name);
    item.setDetail(namespace);
    item.setDocumentation(toMarkupContent());
    item.setKind(CompletionItemKind.Field);
    return item;
  }

  /**
   * Generates documentation markup.
   * For element, it contains "name" and "documentation".
   * Certain XSD (e.g. maven's pom.xsd) has extra attributes in annotations.
   * This will also be displayed as "source" attribute of "documentation" in xsd
   * as Key and the text of "documentation" as it's value
   * Standard format is :
   * <code>
   * ELEMENT:  (element-name)
   * DESCRIPTION: (documentation of element)
   * [OPTIONAL-ANNOTATION-DOCUMENTATION]: (descriptions of optional documentation)
   * </code>
   *
   * @return MarkupContent object having formatted documentation as described in doc
   */
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

    content.setValue(docBuffer.toString());
    return content;
  }

  @Override
  public String getName() {
    return name;
  }

  // Getters

  public XSElementDeclaration getElementDeclaration() {
    return elementDeclaration;
  }

  public String getNamespace() {
    return namespace;
  }
}
