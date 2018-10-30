package io.github.handofgod94.common;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Optional;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Provides functionality for beans querying.
 * This is for directly working on documentItem and not on actual xml content.
 * Various beans related operations such as getting words, sentences etc. can
 * be accomplished by the methods implemented.
 */
public class DocumentManager {

  private TextDocumentItem documentItem;

  // full beans text as array of strings
  private String[] documentLines = null;

  @Inject
  DocumentManager(@Assisted TextDocumentItem documentItem) {
    this.documentItem = documentItem;
    this.documentLines = XmlUtil.getDocumentLines.apply(this.documentItem.getText());
  }

  /**
   * Get string between a given range.
   *
   * @param range Range describing the start and end position
   * @return string having contents of the beans between range, if range is valid.
   */
  public String getStringBetweenRange(Range range) {
    String word = "";

    // Initialize start and end position
    Position startPosition = range.getStart();
    Position endPosition = range.getEnd();

    int startLine = startPosition.getLine();
    int startColumn = startPosition.getCharacter();
    int endLine = endPosition.getLine();
    int endColumn = endPosition.getCharacter();

    if (startLine != endLine) {
      // TODO: If range is between multiple lines
    } else if (startLine > 0) {
      String line = documentLines[startLine];
      if ((line.length() > 0)
          && (endColumn < line.length())
          && (startColumn < endColumn)) {
        word = line.substring(startColumn, endColumn);
      }
    }

    return word;
  }

  /**
   * Returns a range containing boundary of a word from current position.
   * Inclusive of starting and excludes end
   *
   * @param position position in the beans
   * @return Range having starting position and ending position of word if valid
   *         position is provided.
   */
  public Optional<Range> getWordRangeAt(Position position) {

    if (isValidPosition(position)) {
      // Current position
      String line = documentLines[position.getLine()];
      int column = position.getCharacter();

      int startColumn = column;
      int endColumn = column;

      Position start = new Position(position.getLine(), startColumn);
      Position end = new Position(position.getLine(), endColumn);
      Range range = new Range(start, end);

      if ((column < line.length()) && (Character.isJavaIdentifierPart(line.charAt(column)))) {
        // traverse both left and right side of character to get word
        // left side
        for (int i = column; i > 0 && Character.isJavaIdentifierPart(line.charAt(i)); --i) {
          startColumn = i;
        }

        // right side
        for (int i = column + 1;
            i < line.length() && Character.isJavaIdentifierPart(line.charAt(i));
            ++i) {
          endColumn = i;
        }
        start.setCharacter(startColumn);
        end.setCharacter(endColumn + 1);
      }
      return Optional.of(range);
    }

    return Optional.empty();
  }

  /**
   * Returns character at particular position in the beans.
   *
   * @param position Position in the beans
   * @return Character at that position
   */
  public char getCharAt(Position position) {
    int lineNo = position.getLine();
    int column = position.getCharacter();

    if (isValidPosition(position)) {
      return documentLines[lineNo].charAt(column);
    }
    return ' ';
  }

  /**
   * Get line in string format at given line number from beans.
   * @param lineNo line number in the beans (0 based index)
   * @return String having contents of the line
   */
  public String getLineAt(int lineNo) {
    return documentLines[lineNo];
  }

  /**
   * Get all the lines present in beans as String array.
   * These includes blank lines also
   * @return String array with all the lines.
   */
  public String[] getDocumentLines() {
    return documentLines;
  }

  private boolean isValidPosition(Position position) {
    int lineNo = position.getLine();
    int column = position.getCharacter();
    return lineNo > 0 && column > 0
            && lineNo < documentLines.length
            && column < documentLines[lineNo].length();
  }
}
