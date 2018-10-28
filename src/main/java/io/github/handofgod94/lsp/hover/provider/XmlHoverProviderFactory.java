package io.github.handofgod94.lsp.hover.provider;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Guice factory for hover provider.
 */
public interface XmlHoverProviderFactory {

  /**
   * Create hover provider for current document.
   * @param position position being hovered
   * @param documentItem current document
   * @return XmlHoverProvider instance through which we can hove hover instances.
   */
  XmlHoverProvider create(Position position, TextDocumentItem documentItem);
}
