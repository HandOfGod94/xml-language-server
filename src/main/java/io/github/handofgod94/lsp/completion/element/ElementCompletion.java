package io.github.handofgod94.lsp.completion.element;

import java.util.List;
import org.eclipse.lsp4j.CompletionItem;

/**
 * Interface to retrieve all the possible element list.
 */
public interface ElementCompletion {

  /**
   * Get all the completion item for the element.
   *
   * @return List of completion item at current position
   */
  List<CompletionItem> get();

}
