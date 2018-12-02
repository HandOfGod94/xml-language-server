package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import org.eclipse.lsp4j.Position;

public class CommentsContext extends LanguageContext implements GrammarElement {

  public static final int PUNCTUATION_DEFINITION_COMMENT_XML = 0;

  private final String begRegEx = "<!--";
  private final String endRegEx = "-->";

  CommentsContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      PUNCTUATION_DEFINITION_COMMENT_XML
    );
  }

  @Override
  public Type nextContextType() {
    // No context info is required.
    // Since we don't need any completions when we are typing
    // inside comments.
    return null;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public LanguageContext getLanguageContext() {
    return this;
  }

  public String getBegRegEx() {
    return begRegEx;
  }

  public String getEndRegEx() {
    return endRegEx;
  }

  @Override
  public String toString() {
    return "CommentsContext{" +
      "begRegEx='" + begRegEx + '\'' +
      ", endRegEx='" + endRegEx + '\'' +
      ", line='" + line + '\'' +
      ", position=" + position +
      ", capturedGroups=" + capturedGroups +
      ", isMatched=" + isMatched +
      ", isInScope=" + isInScope +
      ", matchStart=" + matchStart +
      ", matchEnd=" + matchEnd +
      '}';
  }
}
