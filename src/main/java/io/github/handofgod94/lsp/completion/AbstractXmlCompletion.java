package io.github.handofgod94.lsp.completion;

import com.google.inject.name.Named;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSElementDeclaration;
import org.eclipse.lsp4j.CompletionItem;

public abstract class AbstractXmlCompletion implements XmlCompletion {

  protected final SchemaDocument schemaDocument;
  protected final QName qname;


  public interface Factory {

    @Named("Element")
    ElementCompletion createEdeCompleter(SchemaDocument schemaDocument, QName qname);

    @Named("Attribute")
    AttributeCompletion createAttrCompleter(SchemaDocument schemaDocument, QName qname);
  }

  public AbstractXmlCompletion(SchemaDocument schemaDocument, QName qname) {
    this.schemaDocument = schemaDocument;
    this.qname = qname;
  }

  @Override
  public List<CompletionItem> getCompletions() {
    List<CompletionItem> items = new ArrayList<>();
    Optional<XSElementDeclaration> element =
        XmlUtil.searchElement(schemaDocument.getXsModel(), qname);

    element.ifPresent(e -> {
      items.addAll(
          findPossibleChildren(e)
          .stream()
          .map(XsAdapter::toCompletionItem)
          .collect(Collectors.toList())
      );
    });

    return items;
  }

  protected abstract List<XsAdapter> findPossibleChildren(XSElementDeclaration element);
}
