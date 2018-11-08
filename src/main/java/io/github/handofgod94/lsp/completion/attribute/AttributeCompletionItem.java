package io.github.handofgod94.lsp.completion.attribute;

import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

public class AttributeCompletionItem {
  private String name;
  private XSSimpleTypeDefinition type;
  private int maxOccurs;
  private int minOccurs;
  private boolean isRequired;

  public AttributeCompletionItem(String name, XSSimpleTypeDefinition type, int maxOccurs, int minOccurs, boolean isRequired) {
    this.name = name;
    this.type = type;
    this.maxOccurs = maxOccurs;
    this.minOccurs = minOccurs;
    this.isRequired = isRequired;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public XSSimpleTypeDefinition getType() {
    return type;
  }

  public void setType(XSSimpleTypeDefinition type) {
    this.type = type;
  }

  public int getMaxOccurs() {
    return maxOccurs;
  }

  public void setMaxOccurs(int maxOccurs) {
    this.maxOccurs = maxOccurs;
  }

  public int getMinOccurs() {
    return minOccurs;
  }

  public void setMinOccurs(int minOccurs) {
    this.minOccurs = minOccurs;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }

  public CompletionItem toCompletionItem() {
    // Initialize AttributeCompletionItem
    CompletionItem info = new CompletionItem();
    info.setLabel(name);
    info.setDetail(type.getName());
    info.setDocumentation(buildDoc());
    info.setInsertTextFormat(InsertTextFormat.Snippet);
    info.setInsertText(name + "=\"$1\"");
    info.setKind(CompletionItemKind.Property);
    return info;
  }

  private MarkupContent buildDoc() {
    MarkupContent doc = new MarkupContent();
    doc.setKind(MarkupKind.MARKDOWN);
    String typeDoc = String.format("**Type**: %s  \n", type.getName());
    String namespaceDoc = String.format("**Namespace**: %s  \n", type.getNamespace());
    String requiredDoc = String.format("**Required**: %b  \n", isRequired);
    doc.setValue(typeDoc + namespaceDoc + requiredDoc);

    return doc;
  }
}
