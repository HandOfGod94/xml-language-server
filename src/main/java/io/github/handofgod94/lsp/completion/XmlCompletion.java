package io.github.handofgod94.lsp.completion;

import java.util.List;
import org.eclipse.lsp4j.CompletionItem;

public interface XmlCompletion {
  List<CompletionItem> getCompletions();
}
