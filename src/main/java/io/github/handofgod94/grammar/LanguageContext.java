package io.github.handofgod94.grammar;

import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.Position;

public abstract class LanguageContext {

  public enum Type { ELEMENT, ATTRIBUTE, COMMENTS }

  protected final String line;
  protected final Position position;

  protected List<Integer> groupsToMatch;
  protected Map<Integer, String> capturedGroups;
  protected boolean isMatched = false;
  protected boolean isInScope = false;
  protected int matchStart = -1;
  protected int matchEnd = -1;

  LanguageContext(String line, Position position) {
    this.line = line;
    this.position = position;
  }

  public String getLine() {
    return line;
  }
  public Position getPosition() {
    return position;
  }

  public List<Integer> getGroupsToMatch() {
    return groupsToMatch;
  }

  /**
   * Provides next context in line with current one.
   * It is useful for autocompletion, where we won't know the current context.
   * For auto completion, we need to rely on previous.
   * This provides a enum denoting possible next context for completion,
   * if it's null, it means that no autocomplete is required here.
   * @return enum containing the next context type for current context.
   */
  public abstract Type nextContextType();

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
}
