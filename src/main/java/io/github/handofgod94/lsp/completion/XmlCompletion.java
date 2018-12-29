package io.github.handofgod94.lsp.completion;

import java.util.List;
import org.eclipse.lsp4j.CompletionItem;

/**
 * Interface to get completions for various xml structs.
 */
public interface XmlCompletion {

  List<CompletionItem> getCompletions();
}
