package io.github.handofgod94.common.document;

import org.eclipse.lsp4j.TextDocumentItem;

/**
 * DocumentManagerFactory for guice.
 * This helps in injecting {@link DocumentManager} instance.
 */
public interface DocumentManagerFactory {

  /**
   * Creates document manager for text document.
   * The document here refers to any document and not specific to XML documents.
   * @param documentItem text document item.
   * @return DocumentManager document manager instance.
   */
  DocumentManager create(TextDocumentItem documentItem);
}
