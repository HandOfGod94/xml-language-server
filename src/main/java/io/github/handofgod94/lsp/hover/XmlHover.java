package io.github.handofgod94.lsp.hover;

import org.eclipse.lsp4j.Hover;

/**
 * XmlHover interface to provide hover information
 * for elements and attributes.
 */
public interface XmlHover {

  /**
   * Responsible for providing hover information
   * for an element or an attribute.
   */
  Hover getHover();
}
