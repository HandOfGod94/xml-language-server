package io.github.handofgod94.common;

import org.eclipse.lsp4j.TextDocumentItem;

/**
 * DocumentManagerFactory for guice.
 * This helps in injecting {@link DocumentManager} instance.
 */
public interface DocumentManagerFactory {

  /**
   * Creates beans manager for text beans.
   * The beans here refers to any beans and not specific to XML documents.
   * @param documentItem text beans item.
   * @return DocumentManager beans manager instance.
   */
  DocumentManager create(TextDocumentItem documentItem);
}
