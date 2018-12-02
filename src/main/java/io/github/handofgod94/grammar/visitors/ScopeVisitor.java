package io.github.handofgod94.grammar.visitors;

import io.github.handofgod94.grammar.AttributeContext;
import io.github.handofgod94.grammar.CommentsContext;
import io.github.handofgod94.grammar.ElementContext;
import org.eclipse.lsp4j.Position;

public class ScopeVisitor implements Visitor {
  @Override
  public void visit(ElementContext context) {
    Position position = context.getPosition();
    int currentPosition = position.getCharacter();
    int matchStart = context.getMatchStart();
    int matchEnd = context.getMatchEnd();

    if (context.isMatched()
        && currentPosition >= matchStart
        && currentPosition <= matchEnd) {
      context.setInScope(true);
    }
  }

  @Override
  public void visit(AttributeContext context) {
    Position position = context.getPosition();
    int currentPosition = position.getCharacter();
    int matchStart = context.getMatchStart();
    int matchEnd = context.getMatchEnd();

    if (context.isMatched()
        && currentPosition >= matchStart
        && currentPosition <= matchEnd) {
      context.setInScope(true);
    }
  }

  @Override
  public void visit(CommentsContext context) {
    Position position = context.getPosition();
    int currentPosition = position.getCharacter();
    int matchEnd = context.getMatchEnd();

    if (context.isMatched() && currentPosition >= matchEnd) {
      context.setInScope(true);
    }
  }
}
