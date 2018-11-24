package io.github.handofgod94.common.document;

import io.github.handofgod94.AbstractLangServerTest;
import io.github.handofgod94.main.XmlLanguageServer;
import java.util.Optional;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DocumentManagerTest extends AbstractLangServerTest {

  private DocumentManager manager;

  @BeforeEach
  public void setup() {
    TextDocumentItem documentItem =
      new TextDocumentItem(DUMMY_URI, XmlLanguageServer.LANGUAGE_ID, DUMMY_VERSION, MOCK_XML_TEXT);
    manager = new DocumentManager(documentItem);
  }

  @Test
  public void testGetStringBetweenRange() {
    Range validRange = manager.getWordRangeAt(new Position(3, 10)).get();
    Range wrongRange = new Range(new Position(3, 10), new Position(2, 9));
    Range invalidRange = new Range(new Position(-1, -1), new Position(0, 0));

    String validWord = manager.getStringBetweenRange(validRange);
    String wrongWord = manager.getStringBetweenRange(wrongRange);
    String invalidWord = manager.getStringBetweenRange(invalidRange);

    assertEquals("schemaLocation", validWord);
    assertEquals("", wrongWord);
    assertEquals("", invalidWord);
  }

  @Test
  public void testGetWordRangeAt() {
    Position invalidPosition = new Position(-1, -1);
    Position validPosition = new Position(3, 10);
    Position overflowPosition = new Position(100, 100);

    Optional<Range> invalid = manager.getWordRangeAt(invalidPosition);
    Optional<Range> valid = manager.getWordRangeAt(validPosition);
    Optional<Range> overflow = manager.getWordRangeAt(overflowPosition);

    assertFalse(invalid.isPresent());
    assertEquals(new Range(new Position(3, 6), new Position(3, 20)), valid.get());
    assertFalse(overflow.isPresent());
  }

  @Test
  public void testGetCharAt() {
    Position invalidPosition = new Position(-1, -1);
    Position validPosition = new Position(3, 10);
    Position overflowPosition = new Position(100, 100);

    char invalid = manager.getCharAt(invalidPosition);
    char valid = manager.getCharAt(validPosition);
    char overflow = manager.getCharAt(overflowPosition);

    assertEquals(' ', invalid, "Should print empty char");
    assertEquals('m', valid, "Should print 'm' from 'schemaLocation' word");
    assertEquals(' ', overflow, " Should print empty char");
  }
}
