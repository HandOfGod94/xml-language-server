package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.graph.GrammarNode;
import io.github.handofgod94.grammar.graph.LanguageGraphContext;
import io.github.handofgod94.grammar.graph.Scope;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.lsp4j.Position;

public class GrammarProcessor {
  private List<GrammarNode> traverseOrder;

  private final LanguageGraphContext languageGraphContext;
  private final Map<GrammarNode, List<GrammarNode>> graph;

  private final Position position;
  private final String line;

  public GrammarProcessor(Position position, String line) {
    this.languageGraphContext = new LanguageGraphContext();
    this.graph = languageGraphContext.getAdjList();
    this.position = position;
    this.line = line;
  }

  public Optional<String> processScope() {
    traverseOrder = languageGraphContext.getTraverseOrder();

    for (GrammarNode node : traverseOrder) {
      if (node.getColor() == GrammarNode.Color.WHITE) {
        String caretScope = DFS(node);
        if (caretScope != null) return Optional.of(caretScope);
      }
    }

    return Optional.empty();
  }

  private String DFS(GrammarNode node) {
    Scope scope = node.getScope();
    String scopeName = null;
    node.setColor(GrammarNode.Color.GRAY);

    // Check if position is matched with scope regex and boundaries
    boolean isInScope = isMatchInScope(scope, line);

    // if it's found then which capture groups does it belong to
    if (isInScope) {
      scopeName = getScopeName(scope, line, position.getCharacter());
    }

    // Recurse with DFS.
    if (graph.get(node) != null) {
      for (GrammarNode gN : graph.get(node)) {
        if (gN.getColor() == GrammarNode.Color.WHITE) {
          String childScope = DFS(gN);
          if (childScope != null) return childScope;
        }
      }
    }

    node.setColor(GrammarNode.Color.BLACK);
    return scopeName;
  }

  protected boolean isMatchInScope(Scope scope, String line) {
    int columnPos = position.getCharacter();
    if (scope.getMatchPattern() != null)
      return hasMatchPattern(scope.getMatchPattern(), line, columnPos);
    else if (scope.getBeginRegEx() != null)
      return hasBoundedPattern(scope.getBeginRegEx(), scope.getEndRegEx(), line, columnPos);

    return false;
  }

  protected String getScopeName(Scope scope, String line, int position) {
    String scopeName = scope.getName();
    String regEx = (scope.getMatchPattern() != null) ? scope.getMatchPattern() : scope.getBeginRegEx();
    Matcher matcher = generateMatcher(regEx, line);

    Map<Integer, String> captures = (scope.getCaptures() != null) ? scope.getCaptures() : scope.getBeginCaptures();
    while (matcher.find()) {
      for (int groupNum : captures.keySet()) {
        int start = matcher.start(groupNum);
        int end = matcher.end(groupNum) - 1;
        if (start != -1 && end != -1) {
          if (start <= position && end >= position) scopeName = captures.get(groupNum);
        }
      }
    }

    return scopeName;
  }

  private boolean hasMatchPattern(String regEx, String line, int columnPos) {
    Matcher matcher = generateMatcher(regEx, line);

    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end() - 1;
      if (start <= columnPos && end >= columnPos) return true;
    }

    return false;
  }

  private boolean hasBoundedPattern(String begRegx, String endRegex, String line, int columnPos) {
    Matcher begMatcher = generateMatcher(begRegx, line);
    Matcher endMatcher = generateMatcher(endRegex, line);

    int start = Integer.MAX_VALUE;
    int end = Integer.MIN_VALUE;

    if (begMatcher.find()) start = begMatcher.start();

    if (endMatcher.find()) end = endMatcher.end() - 1;

    return start <= columnPos && end >= columnPos;

  }

  private Matcher generateMatcher(String regex, String line) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(line);
    return matcher;
  }

}
