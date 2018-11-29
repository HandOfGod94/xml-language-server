package io.github.handofgod94.lsp.completion;

import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.schema.SchemaDocument;
import javax.annotation.Nonnull;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.CompletionTriggerKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

public class XmlCompletionFactory {

  private static final String ELEMENT_TRIGGER_CHAR = "<";
  private static final String ATTRIBUTE_TRIGGER_CHARACTER = " ";

  public XmlCompletion create(@Nonnull SchemaDocument schemaDocument,
                       @Nonnull CompletionParams params,
                       @Nonnull TextDocumentItem documentItem) {

    Position position = params.getPosition();
    CompletionTriggerKind triggerKind = params.getContext().getTriggerKind();
    String triggerChar = params.getContext().getTriggerCharacter();
    String text = documentItem.getText();

    // TODO: improve with guice
    PositionalHandler handler = new PositionalHandler(position);
    XmlUtil.positionalParse(handler, text);

    if (triggerKind.equals(CompletionTriggerKind.TriggerCharacter)) {
      switch (triggerChar) {
        case ELEMENT_TRIGGER_CHAR:
          return new ElementCompletion(schemaDocument, handler);
        case ATTRIBUTE_TRIGGER_CHARACTER:
          return new AttributeCompletion(schemaDocument, handler);
      }
    }

    return null;
  }

}
