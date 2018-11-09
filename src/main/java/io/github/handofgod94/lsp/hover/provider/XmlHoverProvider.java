package io.github.handofgod94.lsp.hover.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.common.document.DocumentManagerFactory;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.lsp.hover.XmlHover;
import io.github.handofgod94.lsp.hover.XmlHoverFactory;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.Optional;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Provides hover information only if its a valid element which
 * is being hovered.
 * For comments and constants, there will be no hover information.
 * So it will return empty in that case.
 */
public class XmlHoverProvider implements Provider<Optional<XmlHover>> {

  private final Position position;
  private final TextDocumentItem documentItem;
  private final XmlHoverFactory xmlHoverFactory;
  private final DocumentManagerFactory documentManagerFactory;
  private final SchemaDocument document;

  @Inject
  XmlHoverProvider(@Assisted Position position, @Assisted TextDocumentItem documentItem,
                   @Assisted SchemaDocument document,
                   XmlHoverFactory xmlHoverFactory,
                   DocumentManagerFactory documentManagerFactory) {
    this.position = position;
    this.documentItem = documentItem;
    this.xmlHoverFactory = xmlHoverFactory;
    this.document = document;
    this.documentManagerFactory = documentManagerFactory;
  }

  /**
   * Hover Provider for xml documents.
   * Hover information will contain documentation for
   * word being hovered, if its present in schema.
   * <p>
   * XML Document primarily consists of two things, elements and attributes
   * The required instances based on the word which is being hovered will
   * be provided by this method.
   * </p>
   *
   * @return XmlHover instance having value if documentation
   *     is found.
   */
  @Override
  public Optional<XmlHover> get() {
    DocumentManager docManager = documentManagerFactory.create(documentItem);
    String line = docManager.getLineAt(position.getLine());
    Optional<Document> optPartialDoc = XmlUtil.getPartialDoc.apply(line);

    Optional<Range> optWordRange = docManager.getWordRangeAt(position);
    String wordHovered =
        optWordRange.map(docManager::getStringBetweenRange).orElse("");

    if (optPartialDoc.isPresent()) {
      Document partialDoc = optPartialDoc.get();
      Element root = partialDoc.getRootElement();

      // If root element name is equal to word hovered that means
      // we are looking at element, otherwise it could be attribute or something else altogether
      if (wordHovered.equals(root.getName())) {
        XmlHover hover = xmlHoverFactory.getTagHover(wordHovered, document);
        return Optional.of(hover);
      } else if (root.attribute(wordHovered) != null) {
        // if word hovered is attribute
        XmlHover hover = xmlHoverFactory.getAttributeHover(wordHovered, document, root.getName());
        return Optional.of(hover);
      }
    }

    return Optional.empty();
  }

}
