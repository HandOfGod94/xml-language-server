package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Entity extends ScopeBuilder {
  @Override
  protected void addName() {
    scope.setName("constant.character.entity.xml");
  }

  @Override
  protected void addBeginRegEx() {

  }

  @Override
  protected void addEndRegEx() {

  }

  @Override
  protected void addMatchPattern() {
    scope.setMatchPattern("(&)([:a-zA-Z_][:a-zA-Z0-9_.-]*|#[0-9]+|#x[0-9a-fA-F]+)(;)");
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
    captures.put(1, "punctuation.definition.constant.xml");
    captures.put(3, "punctuation.definition.constant.xml");
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
