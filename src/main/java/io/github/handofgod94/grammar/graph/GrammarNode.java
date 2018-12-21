package io.github.handofgod94.grammar.graph;

import java.util.Objects;

public class GrammarNode {

  public enum Color {
    WHITE, GRAY, BLACK;
  }

  private ScopeBuilder builder;
  private Color color;

  public GrammarNode(ScopeBuilder builder) {
    this.builder = builder;
    this.color = Color.WHITE;
  }

  public Scope getScope() {
    return builder.getScope();
  }

  public void generateScope(){
    builder.create();
    builder.addName();
    builder.addBeginRegEx();
    builder.addEndRegEx();
    builder.addBeginCaptures();
    builder.addEndCaptures();
    builder.addCaptures();
    builder.addMatchPattern();
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GrammarNode that = (GrammarNode) o;
    return Objects.equals(builder, that.builder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(builder);
  }

  @Override
  public String toString() {
    return "GrammarNode{" +
      "builder=" + builder +
      ", color=" + color +
      '}';
  }
}
