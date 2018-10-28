package io.github.handofgod94.lsp.hover;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;

/**
 * Hover information for tags.
 */
public class TagHover implements XmlHover {

  private final String wordHovered;

  @Inject
  TagHover(@Assisted String wordHovered) {
    this.wordHovered = wordHovered;
  }

  @Override
  public Hover getHover() {
    MarkupContent content = new MarkupContent();
    content.setKind(MarkupKind.PLAINTEXT);
    content.setValue(wordHovered);
    Hover hover = new Hover(content);
    return hover;
  }
}
