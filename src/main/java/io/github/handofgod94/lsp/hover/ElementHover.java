package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.common.parser.PositionalHandlerFactory;
import io.github.handofgod94.schema.ElementInfo;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.xs.XSElementDeclaration;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
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
  private final PositionalHandlerFactory handlerFactory;

  @Inject
  ElementHover(@Assisted String wordHovered,
               @Assisted SchemaDocument schemaDocument,
               @Assisted TextDocumentItem documentItem,
               @Assisted Position position,
               PositionalHandlerFactory handlerFactory) {
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

    // get current element and its attributes
    XmlUtil.positionalParse(handler, documentItem.getText());
    QName qname = handler.getCurrentElement();
    Optional<XSElementDeclaration> optElement =
      Stream
        .of(
          XmlUtil.checkInElement(schemaDocument.getXsModel(), qname),
          XmlUtil.checkInModelGroup(schemaDocument.getXsModel(), qname)
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();


    // TODO: Guice injections
    if (optElement.isPresent() && optElement.get().getName().equals(wordHovered)) {
      ElementInfo elementInfo = new ElementInfo(optElement.get());
      content = elementInfo.toMarkupContent();
    }

    // Add all the documentation text to hover text.
    Hover hover = new Hover(content);
    return hover;
  }
}
