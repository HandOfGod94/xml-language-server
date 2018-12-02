package io.github.handofgod94.grammar;

import io.github.handofgod94.grammar.visitors.Visitor;
import java.util.Arrays;
import org.eclipse.lsp4j.Position;

public class AttributeContext extends LanguageContext implements GrammarElement {

  public static final int ENTITY_OTHER_ATTRIBUTE_NAME_NAMESPACE_XML = 1;
  public static final int PUNCTUATION_SEPARATOR_NAMESPACE_XML = 2;
  public static final int ENTITY_OTHER_ATTRIBUTE_NAME_LOCALNAME_XML = 3;

  private final String regEx = "(?:^|\\s+)(?:([-\\w.]+)(:))?([-\\w.:]+)\\s*=";

  AttributeContext(String line, Position position) {
    super(line, position);

    groupsToMatch = Arrays.asList(
      ENTITY_OTHER_ATTRIBUTE_NAME_NAMESPACE_XML,
      PUNCTUATION_SEPARATOR_NAMESPACE_XML,
      ENTITY_OTHER_ATTRIBUTE_NAME_LOCALNAME_XML
    );
  }

  @Override
  public Type nextContextType() {
    // An attribute can be followed by attributes.
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

  public String getRegEx() {
    return regEx;
  }

  @Override
  public String toString() {
    return "AttributeContext{" +
      "regEx='" + regEx + '\'' +
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
