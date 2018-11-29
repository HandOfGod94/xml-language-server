package io.github.handofgod94.lsp.completion;

import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
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
  protected final PositionalHandler handler;



  public AbstractXmlCompletion(SchemaDocument schemaDocument, PositionalHandler handler) {
    this.schemaDocument = schemaDocument;
    this.handler = handler;
  }

  protected Optional<XSElementDeclaration> getXSElementDeclaration(QName qName) {
    Optional<XSElementDeclaration> element =
        XmlUtil.checkInElement(schemaDocument.getXsModel(), qName);
    element =
        element.isPresent() ?
          element : XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), qName);

    return element;
  }

  @Override
  public List<CompletionItem> getCompletions() {
    List<CompletionItem> items = new ArrayList<>();
    Optional<XSElementDeclaration> element =
        getXSElementDeclaration(searchInElement());

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

  protected abstract QName searchInElement();
  protected abstract List<XsAdapter> findPossibleChildren(XSElementDeclaration element);
}