package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.Position;

public class CommentsContext extends LanguageContext implements GrammarElement {

  public static final int PUNCTUATION_DEFINITION_COMMENT_XML = 0;

  private final String begRegEx = "<!--";
  private final String endRegEx = "-->";
  private final List<Integer> groupsToMatch;
  private Map<Integer, String> capturedGroups;
  private boolean isMatched;
  private boolean isInScope;
  private int matchStart = -1;
  private int matchEnd = -1;

  CommentsContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      PUNCTUATION_DEFINITION_COMMENT_XML
    );
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public String getBegRegEx() {
    return begRegEx;
  }

  public String getEndRegEx() {
    return endRegEx;
  }

  public List<Integer> getGroupsToMatch() {
    return groupsToMatch;
  }

  public Map<Integer, String> getCapturedGroups() {
    return capturedGroups;
  }

  public void setCapturedGroups(Map<Integer, String> capturedGroups) {
    this.capturedGroups = capturedGroups;
  }

  public boolean isMatched() {
    return isMatched;
  }

  public void setMatched(boolean matched) {
    isMatched = matched;
  }

  public boolean isInScope() {
    return isInScope;
  }

  public void setInScope(boolean inScope) {
    isInScope = inScope;
  }

  public int getMatchStart() {
    return matchStart;
  }

  public void setMatchStart(int matchStart) {
    this.matchStart = matchStart;
  }

  public int getMatchEnd() {
    return matchEnd;
  }

  public void setMatchEnd(int matchEnd) {
    this.matchEnd = matchEnd;
  }

  @Override
  public String toString() {
    return "CommentsContext{" +
      "begRegEx='" + begRegEx + '\'' +
      ", endRegEx='" + endRegEx + '\'' +
      ", groupsToMatch=" + groupsToMatch +
      ", capturedGroups=" + capturedGroups +
      ", isMatched=" + isMatched +
      ", isInScope=" + isInScope +
      '}';
  }
}
