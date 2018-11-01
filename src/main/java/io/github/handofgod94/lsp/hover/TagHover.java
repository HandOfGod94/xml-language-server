package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.schema.SchemaDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Hover information for tags.
 */
public class TagHover implements XmlHover {

  private static final Logger logger = LogManager.getLogger(TagHover.class.getName());

  private final String wordHovered;
  private final SchemaDocument schemaDocument;

  @Inject
  TagHover(@Assisted String wordHovered, @Assisted SchemaDocument schemaDocument) {
    this.wordHovered = wordHovered;
    this.schemaDocument = schemaDocument;
  }

  @Override
  public Hover getHover() {
    StringBuffer hoverText = new StringBuffer();

    // TODO: Namespace should come from wordHovered, and only those documents should
    // be queried who has this namespace
    // Get annotations
    String namespace = schemaDocument.getNamespace().getText();

    // Get all the documentation node append the text to hover text
    hoverText.append(searchInParsedDocs(wordHovered));

    // Add all the documentation text to hover text.
    MarkupContent content = new MarkupContent();
    content.setKind(MarkupKind.PLAINTEXT);
    content.setValue(hoverText.toString());
    Hover hover = new Hover(content);
    return hover;
  }

  private String searchInParsedDocs(String elementName) {
    List<Document> parsedDocs = this.schemaDocument.getParsedSchemaDocs();
    String annotationExpr = String.format("//*[local-name()='element'][@name='%s']/*[local-name()='annotation']", elementName);
    for (Document doc :parsedDocs) {
      Node node = doc.selectSingleNode(annotationExpr);
      if (node != null) {
        return node.getStringValue();
      }
    }
    return "";
  }
}
