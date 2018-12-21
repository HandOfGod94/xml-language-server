package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Element extends ScopeBuilder {
  @Override
  protected void addName() {
    scope.setName("meta.tag.xml");
  }

  @Override
  protected void addBeginRegEx() {
    scope.setBeginRegEx("(<\\/?)(?:([-\\w\\.]+)((:)))?([-\\w\\.:]+)");
  }

  @Override
  protected void addEndRegEx() {
    scope.setEndRegEx("(/?>)");
  }

  @Override
  protected void addMatchPattern() {

  }

  @Override
  protected void addBeginCaptures() {
    Map<Integer, String> captures = new LinkedHashMap<>();
    captures.put(1, "punctuation.definition.tag.xml");
    captures.put(2, "entity.name.tag.namespace.xml");
    captures.put(3, "entity.name.tag.xml");
    captures.put(4, "punctuation.separator.namespace.xml");
    captures.put(5, "entity.name.tag.localname.xml");
    scope.setBeginCaptures(captures);
  }

  @Override
  protected void addEndCaptures() {

  }

  @Override
  protected void addCaptures() {

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
