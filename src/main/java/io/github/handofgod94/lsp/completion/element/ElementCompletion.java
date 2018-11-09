package io.github.handofgod94.lsp.completion.element;

import org.eclipse.lsp4j.CompletionItem;
import java.util.List;

public interface ElementCompletion {

  /**
   * Get all the completion item for the element.
   *
   * @return List of completion item at current position
   */
  List<CompletionItem> get();

}
