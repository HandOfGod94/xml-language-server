package io.github.handofgod94.grammar.graph;

import java.util.Objects;

public abstract class ScopeBuilder {

  protected Scope scope;

  public Scope getScope() {
    return scope;
  }

  public void create() {
    scope = new Scope();
  }

  protected abstract void addName();

  protected abstract void addBeginRegEx();

  protected abstract void addEndRegEx();

  protected abstract void addMatchPattern();

  protected abstract void addBeginCaptures();

  protected abstract void addEndCaptures();

  protected abstract void addCaptures();

  @Override
  public String toString() {
    return "ScopeNodeBuilder{"
      + "scopeNode=" + scope
      + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScopeBuilder that = (ScopeBuilder) o;
    return Objects.equals(scope, that.scope);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scope);
  }
}
