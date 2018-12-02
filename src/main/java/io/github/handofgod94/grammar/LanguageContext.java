package io.github.handofgod94.grammar;

import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.Position;

public abstract class LanguageContext {

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
