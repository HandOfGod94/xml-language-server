package io.github.handofgod94.lsp.completion.element;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

public class ElementCompletionItem {

  private String name;
  private String namespace;
  private String documentation;

  public ElementCompletionItem(String name, String namespace, String documentation) {
    this.name = name;
    this.namespace = namespace;
    this.documentation = documentation;
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

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

  public CompletionItem toCompletionItem() {
    CompletionItem item = new CompletionItem();
    item.setLabel(name);
    item.setDetail(namespace);
    item.setDocumentation(documentation);
    item.setKind(CompletionItemKind.Field);
    return item;
  }
}
