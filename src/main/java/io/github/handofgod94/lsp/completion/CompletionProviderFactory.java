package io.github.handofgod94.lsp.completion;

import io.github.handofgod94.schema.SchemaDocument;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Guice factory for completion provider.
 */
public interface CompletionProviderFactory {

  CompletionProvider create(CompletionParams params,
                            TextDocumentItem textDocumentItem,
                            SchemaDocument schemaDocument);
}
