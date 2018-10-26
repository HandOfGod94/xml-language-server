package io.github.handofgod94.common;

import java.util.function.Function;

/**
 * Utility class for various xml related operations.
 * Similar to other utility classes, all the functions are lambdas
 * to keep functions pure.
 */
public class XmlUtil {
  /**
   * Initializes document lines.
   *
   * @param text DocumentText having next lines
   * @return Array of strings for each line
   */
  public static Function<String, String[]> getDocumentLines = text -> text.split("\\r?\\n");
}
