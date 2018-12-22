package io.github.handofgod94.grammar.graph.nodes;

import io.github.handofgod94.grammar.graph.ScopeBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

public class SingleQuotedString extends ScopeBuilder {
  @Override
  protected void addName() {
    scope.setName("string.quoted.single.xml");
  }

  @Override
  protected void addBeginRegEx() {

  }

  @Override
  protected void addEndRegEx() {

  }

  @Override
  protected void addMatchPattern() {
    scope.setMatchPattern("('([^']|'')*'?)");
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
    captures.put(0, "string.quoted.single.xml");
    scope.setCaptures(captures);
  }
}
