package io.github.handofgod94.lsp.completion.attribute;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class AttributeCompletionItem {
  private String name;
  private String namespace;
  private String type;
  private int maxOccurs;
  private int minOccurs;
  private boolean isRequired;

  public AttributeCompletionItem(String name, String namespace, String type, int maxOccurs, int minOccurs, boolean isRequired) {
    this.name = name;
    this.namespace = namespace;
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

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
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
    info.setDetail(namespace);
    info.setInsertTextFormat(InsertTextFormat.Snippet);
    info.setInsertText(name + "=\"$1\"");
    info.setKind(CompletionItemKind.Property);
    return info;
  }
}
