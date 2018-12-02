package io.github.handofgod94.grammar;

import org.eclipse.lsp4j.Position;

public abstract class LanguageContext {

  protected final String line;
  protected final Position position;

  LanguageContext(String line, Position position) {
    this.line = line;
    this.position = position;
  }

  public String getLine() {
    return line;
  }
  public Position getPosition() {
    return position;
  }
}
