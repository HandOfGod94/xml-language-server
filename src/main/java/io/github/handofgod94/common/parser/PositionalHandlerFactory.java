package io.github.handofgod94.common.parser;

import org.eclipse.lsp4j.Position;

/**
 * Guice factory for Positional Handler
 */
public interface PositionalHandlerFactory {

  PositionalHandler create(Position position);
}
