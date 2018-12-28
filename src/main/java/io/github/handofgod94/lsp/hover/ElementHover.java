package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.ElementAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.util.Optional;
import javax.xml.namespace.QName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.xs.XSElementDeclaration;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Hover information for tags.
 */
public class ElementHover implements XmlHover {

  private static final Logger logger = LogManager.getLogger(ElementHover.class.getName());

  private final String wordHovered;
  private final SchemaDocument schemaDocument;
  private final TextDocumentItem documentItem;
  private final Position position;
  private final PositionalHandler.Factory handlerFactory;

  @Inject
  ElementHover(@Assisted String wordHovered,
               @Assisted SchemaDocument schemaDocument,
               @Assisted TextDocumentItem documentItem,
               @Assisted Position position,
               PositionalHandler.Factory handlerFactory) {
    this.wordHovered = wordHovered;
    this.schemaDocument = schemaDocument;
    this.documentItem = documentItem;
    this.position = position;
    this.handlerFactory = handlerFactory;
  }

  @Override
  public Hover getHover() {
    MarkupContent content = new MarkupContent();
    content.setKind(MarkupKind.PLAINTEXT);
    content.setValue("");

    // parse using positional handler
    PositionalHandler handler = handlerFactory.create(position);

    // getCompletionItems current element and its attributes
    XmlUtil.positionalParse(handler, documentItem.getText());
    QName currentElement = handler.getCurrentElement();

    Optional<XSElementDeclaration> element =
        XmlUtil.searchElement(schemaDocument.getXsModel(), currentElement);


    // check if word hovered is the possible element at current position or not.
    if (element.isPresent() && element.get().getName().equals(wordHovered)) {
      XsAdapter elementAdapter = new ElementAdapter(element.get());
      content = elementAdapter.toMarkupContent();
    }

    // Add all the documentation text to hover text.
    Hover hover = new Hover(content);
    return hover;
  }
}
