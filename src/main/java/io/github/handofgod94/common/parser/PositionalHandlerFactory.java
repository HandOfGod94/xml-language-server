package io.github.handofgod94.common.parser;

import org.eclipse.lsp4j.Position;

/**
 * Guice factory for Positional Handler.
 */
public interface PositionalHandlerFactory {

  /**
   * Create SAX Handler having position related information.
   * Based on the position provided, it will store current
   * and parent elements and its related information.
   * @param position current position in document
   * @return SAXHandler having information related to position provided
   */
  PositionalHandler create(Position position);
}
