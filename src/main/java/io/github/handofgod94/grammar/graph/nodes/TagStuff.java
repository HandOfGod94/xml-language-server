package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class TagStuff extends ScopeBuilder {

  @Override
  protected void addName() {

  }

  @Override
  protected void addBeginRegEx() {

  }

  @Override
  protected void addEndRegEx() {

  }

  @Override
  protected void addMatchPattern() {
    scope.setMatchPattern("(?:^|\\s+)(?:([-\\w.]+)(:))?([-\\w.:]+)\\s*=");
  }

  @Override
  protected void addBeginCaptures() {

  }

  @Override
  protected void addEndCaptures() {

  }

  @Override
  protected void addCaptures() {
    Map<Integer, String> captures = new LinkedHashMap<>();
    captures.put(1, "entity.other.attribute-name.namespace.xml");
    // captures.put(2, "entity.other.attribute-name.xml");
    captures.put(2, "punctuation.separator.namespace.xml");
    captures.put(3, "entity.other.attribute-name.localname.xml");
    scope.setCaptures(captures);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
