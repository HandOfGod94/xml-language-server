package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Position;

public class ElementContext extends LanguageContext implements GrammarElement {

  public static final int PUNCTUATION_DEFINITION_TAG_XML = 1;
  public static final int ENTITY_NAME_TAG_NAMESPACE_XML = 2;
  public static final int ENTITY_NAME_TAG_XML = 3;
  public static final int PUNCTUATION_SEPARATOR_NAMESPACE_XML = 4;
  public static final int ENTITY_NAME_TAG_LOCALNAME_XML = 5;

  private List<CompletionList> completionList;
  private boolean isMatched = false;
  private boolean isInScope = false;
  private Map<Integer, String> capturedGroups;
  private int matchStart = -1;
  private int matchEnd = -1;

  private final String begRegEx = "(<\\/?)(?:([-\\w\\.]+)((:)))?([-\\w\\.:]+)";
  private final String endRegEx = "(\\/?>)";
  private final List<Integer> groupsToMatch;

  ElementContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      PUNCTUATION_DEFINITION_TAG_XML,
      ENTITY_NAME_TAG_NAMESPACE_XML,
      ENTITY_NAME_TAG_XML,
      PUNCTUATION_SEPARATOR_NAMESPACE_XML,
      ENTITY_NAME_TAG_LOCALNAME_XML
    );
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public List<CompletionList> getCompletionList() {
    return completionList;
  }

  public void setCompletionList(List<CompletionList> completionList) {
    this.completionList = completionList;
  }

  public void setCapturedGroups(Map<Integer, String> capturedGroups) {
    this.capturedGroups = capturedGroups;
  }

  public Map<Integer, String> getCapturedGroups() {
    return capturedGroups;
  }

  public boolean isMatched() {
    return isMatched;
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

  public void setMatched(boolean matched) {
    isMatched = matched;
  }

  public String getBegRegEx() {
    return begRegEx;
  }

  public List<Integer> getGroupsToMatch() {
    return groupsToMatch;
  }

  public String getEndRegEx() {
    return endRegEx;
  }

  @Override
  public String toString() {
    return "ElementContext{" +
      "completionList=" + completionList +
      ", isMatched=" + isMatched +
      ", isInScope=" + isInScope +
      ", capturedGroups=" + capturedGroups +
      ", matchStart=" + matchStart +
      ", matchEnd=" + matchEnd +
      ", begRegEx='" + begRegEx + '\'' +
      ", endRegEx='" + endRegEx + '\'' +
      ", groupsToMatch=" + groupsToMatch +
      '}';
  }
}
