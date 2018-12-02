package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import org.eclipse.lsp4j.Position;

public class ElementContext extends LanguageContext implements GrammarElement {

  public static final int PUNCTUATION_DEFINITION_TAG_XML = 1;
  public static final int ENTITY_NAME_TAG_NAMESPACE_XML = 2;
  public static final int ENTITY_NAME_TAG_XML = 3;
  public static final int PUNCTUATION_SEPARATOR_NAMESPACE_XML = 4;
  public static final int ENTITY_NAME_TAG_LOCALNAME_XML = 5;

  private final String begRegEx = "(<\\/?)(?:([-\\w\\.]+)((:)))?([-\\w\\.:]+)";
  private final String endRegEx = "(\\/?>)";

  ElementContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      PUNCTUATION_DEFINITION_TAG_XML,
      ENTITY_NAME_TAG_NAMESPACE_XML,
      ENTITY_NAME_TAG_XML,
      PUNCTUATION_SEPARATOR_NAMESPACE_XML,
      ENTITY_NAME_TAG_LOCALNAME_XML
    );
  }

  @Override
  public Type nextContextType() {
    return Type.ATTRIBUTE;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public LanguageContext getLanguageContext() {
    return this;
  }

  public String getBegRegEx() {
    return begRegEx;
  }

  public String getEndRegEx() {
    return endRegEx;
  }

  @Override
  public String toString() {
    return "ElementContext{" +
      "begRegEx='" + begRegEx + '\'' +
      ", endRegEx='" + endRegEx + '\'' +
      ", line='" + line + '\'' +
      ", position=" + position +
      ", capturedGroups=" + capturedGroups +
      ", isMatched=" + isMatched +
      ", isInScope=" + isInScope +
      ", matchStart=" + matchStart +
      ", matchEnd=" + matchEnd +
      '}';
  }
}
