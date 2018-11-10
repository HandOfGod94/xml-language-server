package io.github.handofgod94.lsp.completion.attribute;

import java.util.List;
import org.eclipse.lsp4j.CompletionItem;

public interface AttributeCompletion {

  /**
   * Get list of attributes for current element.
   *
   * @return list of {@link CompletionItem}.
   */
  List<CompletionItem> get();
}
