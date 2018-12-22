package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.graph.LanguageGraphContext;
import java.util.Optional;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGrammarProcessor {

  private static final String line = "<foo:bar fizz=\"buzz\" foo:attr=\"bar\">    ";
  private static final String comment = "<!--<foo:bar foo:attr=\"bar\" fizz=\"buzz\">-->";


  @Test
  public void testElementScope() {
    Position namespacePosition = new Position(0, 3);
    Position localNamePosition = new Position(0, 6);

    GrammarProcessor namespace = new GrammarProcessor(namespacePosition, line, new LanguageGraphContext());
    GrammarProcessor localName = new GrammarProcessor(localNamePosition, line, new LanguageGraphContext());

    Optional<String> namespaceScope = namespace.processScope();
    Optional<String> localNameScope = localName.processScope();


    assertTrue(namespaceScope.isPresent());
    assertEquals("entity.name.tag.namespace.xml", namespaceScope.get());

    assertTrue(localNameScope.isPresent());
    assertEquals("entity.name.tag.localname.xml", localNameScope.get());
  }

  @Test
  public void testAttributeScope() {
    Position namespacePosition = new Position(0, 22);
    Position attributePosition = new Position(0, 27);

    GrammarProcessor namespace = new GrammarProcessor(namespacePosition, line, new LanguageGraphContext());
    GrammarProcessor attribute = new GrammarProcessor(attributePosition, line, new LanguageGraphContext());

    Optional<String> namespaceScope = namespace.processScope();
    Optional<String> attributeScope = attribute.processScope();

    assertTrue(namespaceScope.isPresent());
    assertEquals("entity.other.attribute-name.namespace.xml", namespaceScope.get());

    assertTrue(attributeScope.isPresent());
    assertEquals("entity.other.attribute-name.localname.xml", attributeScope.get());
  }

  @Test
  public void testCommentScope() {
    Position position = new Position(0, 10);
    GrammarProcessor commentProcessor = new GrammarProcessor(position, comment, new LanguageGraphContext());

    Optional<String> commentScope = commentProcessor.processScope();

    assertTrue(commentScope.isPresent());
    assertEquals("comment.block.xml", commentScope.get());
  }

  @Test
  public void testEmptyScope() {
    Position position = new Position(0, 37);
    GrammarProcessor emptyProcessor = new GrammarProcessor(position, line, new LanguageGraphContext());
    Optional<String> emptyScope = emptyProcessor.processScope();
    assertTrue(!emptyScope.isPresent());
  }

  @Test
  public void testStringScope() {
    Position position = new Position(0, 16);
    GrammarProcessor processor = new GrammarProcessor(position, line, new LanguageGraphContext());
    Optional<String> stringScope = processor.processScope();

    assertTrue(stringScope.isPresent());
    assertEquals("string.quoted.double.xml", stringScope.get());
  }
}
