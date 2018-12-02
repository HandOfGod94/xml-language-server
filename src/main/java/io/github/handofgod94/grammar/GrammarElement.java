package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;

public interface GrammarElement {
  void accept(Visitor visitor);
}
