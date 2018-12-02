package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Position;

public class AttributeContext extends LanguageContext implements GrammarElement {

  public static final int ENTITY_OTHER_ATTRIBUTE_NAME_NAMESPACE_XML = 1;
  public static final int PUNCTUATION_SEPARATOR_NAMESPACE_XML = 2;
  public static final int ENTITY_OTHER_ATTRIBUTE_NAME_LOCALNAME_XML = 3;

  private final String regEx = "(?:^|\\s+)(?:([-\\w.]+)(:))?([-\\w.:]+)\\s*=";
  private final List<Integer> groupsToMatch;

  private List<CompletionList> completionList;
  private boolean isMatched = false;
  private boolean isInScope = false;
  private Map<Integer, String> capturedGroups;
  private int matchStart = -1;
  private int matchEnd = -1;

  AttributeContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      ENTITY_OTHER_ATTRIBUTE_NAME_NAMESPACE_XML,
      PUNCTUATION_SEPARATOR_NAMESPACE_XML,
      ENTITY_OTHER_ATTRIBUTE_NAME_LOCALNAME_XML
    );
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public String getRegEx() {
    return regEx;
  }

  public List<Integer> getGroupsToMatch() {
    return groupsToMatch;
  }

  public List<CompletionList> getCompletionList() {
    return completionList;
  }

  public void setCompletionList(List<CompletionList> completionList) {
    this.completionList = completionList;
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

  public Map<Integer, String> getCapturedGroups() {
    return capturedGroups;
  }

  public void setCapturedGroups(Map<Integer, String> capturedGroups) {
    this.capturedGroups = capturedGroups;
  }

  @Override
  public String toString() {
    return "AttributeContext{" +
      "regEx='" + regEx + '\'' +
      ", groupsToMatch=" + groupsToMatch +
      ", completionList=" + completionList +
      ", isMatched=" + isMatched +
      ", isInScope=" + isInScope +
      ", capturedGroups=" + capturedGroups +
      ", matchStart=" + matchStart +
      ", matchEnd=" + matchEnd +
      '}';
  }
}
