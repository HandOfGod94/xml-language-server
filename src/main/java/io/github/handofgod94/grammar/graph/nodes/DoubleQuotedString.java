package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class DoubleQuotedString extends ScopeBuilder {
  @Override
  protected void addName() {
    scope.setName("string.quoted.double.xml");
  }

  @Override
  protected void addBeginRegEx() {
  }

  @Override
  protected void addEndRegEx() {

  }

  @Override
  protected void addMatchPattern() {
    scope.setMatchPattern("(\"([^\"]|\"\")*\"?)");
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
    captures.put(0, "string.quoted.double.xml");
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
