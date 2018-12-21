package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Comments extends ScopeBuilder {

  @Override
  protected void addName() {
    scope.setName("comment.block.xml");
  }

  @Override
  protected void addBeginRegEx() {
    scope.setBeginRegEx("<!--");
  }

  @Override
  protected void addEndRegEx() {
    scope.setEndRegEx("-->");
  }

  @Override
  protected void addMatchPattern() {

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
    captures.put(0, "punctuation.definition.comment.xml");
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
