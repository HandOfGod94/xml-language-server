package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

/**
 * Hover information for tags.
 */
public class ElementHover implements XmlHover {

  private static final Logger logger = LogManager.getLogger(ElementHover.class.getName());

  private final String wordHovered;
  private final SchemaDocument schemaDocument;

  @Inject
  ElementHover(@Assisted String wordHovered, @Assisted SchemaDocument schemaDocument) {
    this.wordHovered = wordHovered;
    this.schemaDocument = schemaDocument;
  }

  @Override
  public Hover getHover() {
    // TODO: Namespace should come from wordHovered, and only those documents should
    // be queried who has this namespace
    // Get annotations
    String namespace = schemaDocument.getNamespace().getText();

    // Get all the documentation node append the text to hover text
    List<Node> docNodes = searchInParsedDocs(wordHovered);

    // Add all the documentation text to hover text.
    MarkupContent content = formDocStrings(docNodes);
    Hover hover = new Hover(content);
    return hover;
  }

  private List<Node> searchInParsedDocs(String elementName) {
    List<Document> parsedDocs = this.schemaDocument.getParsedSchemaDocs();
    List<Node> docNodes = new ArrayList<>();
    String annotationExpr =
        String.format(
          "//*[local-name()='element'][@name='%s']/*[local-name()='annotation']",
          elementName
        );

    for (Document doc : parsedDocs) {
      Node node = doc.selectSingleNode(annotationExpr);
      if (node != null) {
        // Get all the children for a given node
        docNodes = node.selectNodes("*");
      }
    }
    return docNodes;
  }

  private MarkupContent formDocStrings(List<Node> nodes) {
    MarkupContent docs = new MarkupContent();
    StringBuffer buffer = new StringBuffer();

    // Add element info
    String markedTag = String.format("**TAG** : %s  \n", wordHovered);
    buffer.append(markedTag);

    if (nodes.size() == 1) {
      // if only CDATA is present then add it under Description
      String descStr = nodes.get(0).getText().trim();
      String markedDesc = String.format("**DESCRIPTION**: %s  \n", descStr.trim());
      buffer.append(markedDesc);
    } else {
      // else get all the nodes and add "source" and its value as "Description"
      for (Node node : nodes) {
        Element element = (Element) node;
        String source = element.attributeValue("source").toUpperCase();
        String value = element.getTextTrim();
        String markedStr = String.format("**%s**: %s  \n", source, value);
        buffer.append(markedStr);
      }
    }
    docs.setKind(MarkupKind.MARKDOWN);
    docs.setValue(buffer.toString());
    return docs;
  }
}
