package io.github.handofgod94.lsp.completion.attribute;

import org.eclipse.lsp4j.CompletionItem;
import java.util.List;

public interface AttributeCompletion {

  /**
   * Get list of attributes for current element.
   *
   * @return list of {@link CompletionItem}.
   */
  List<CompletionItem> get();
}
