package io.github.handofgod94.grammar.visitors;

import io.github.handofgod94.grammar.AttributeContext;
import io.github.handofgod94.grammar.CommentsContext;
import io.github.handofgod94.grammar.ElementContext;

public interface Visitor {
  void visit(ElementContext context);
  void visit(AttributeContext context);
  void visit(CommentsContext context);
}
