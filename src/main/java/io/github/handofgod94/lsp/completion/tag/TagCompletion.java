package io.github.handofgod94.lsp.completion.tag;

import org.eclipse.lsp4j.CompletionItem;
import java.util.List;

public interface TagCompletion {

  /**
   * Get all the completion item for the tag.
   *
   * @return List of completion item at current position
   */
  List<CompletionItem> get();

}
