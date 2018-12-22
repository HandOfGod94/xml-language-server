package io.github.handofgod94.lsp.completion;

import com.google.inject.Inject;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.grammar.GrammarProcessor;
import io.github.handofgod94.schema.SchemaDocument;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.CompletionTriggerKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

public class XmlCompletionFactory {

  private static final String ELEMENT_TRIGGER_CHAR = "<";
  private static final String ATTRIBUTE_TRIGGER_CHARACTER = " ";

  @Inject DocumentManager.Factory docManagerFactory;
  @Inject GrammarProcessor.Factory grammarFactory;
  @Inject PositionalHandler.Factory posHandlerFactory;

  public XmlCompletion create(@Nonnull SchemaDocument schemaDocument,
                       @Nonnull CompletionParams params,
                       @Nonnull TextDocumentItem documentItem) {

    Position position = params.getPosition();
    CompletionTriggerKind triggerKind = params.getContext().getTriggerKind();
    String triggerChar = params.getContext().getTriggerCharacter();
    String text = documentItem.getText();
    int line = position.getLine();
    DocumentManager manager = docManagerFactory.create(documentItem);
    PositionalHandler handler = posHandlerFactory.create(position);
    GrammarProcessor processor = grammarFactory.create(position, manager.getLineAt(line));

    XmlUtil.positionalParse(handler, text);

    Optional<String> currentScope = processor.processScope();

    if (triggerKind.equals(CompletionTriggerKind.TriggerCharacter)) {

      switch (triggerChar) {
        case ELEMENT_TRIGGER_CHAR:
          if (currentScope.isPresent() && !currentScope.get().contains("meta")) {
            return null;
          }
          return new ElementCompletion(schemaDocument, handler.getParentElement());
        case ATTRIBUTE_TRIGGER_CHARACTER:
          if (currentScope.isPresent() && currentScope.get().contains("meta")) {
            return new AttributeCompletion(schemaDocument, handler.getCurrentElement());
          }
      }

    } else if (triggerKind.equals(CompletionTriggerKind.Invoked)) {

      if (currentScope.isPresent()
          && currentScope.get().contains("tag")
          && currentScope.get().contains("entity")) {
          return new ElementCompletion(schemaDocument, handler.getParentElement());
      }

      if (currentScope.isPresent()
        && (currentScope.get().contains("attribute")
            || currentScope.get().contains("meta"))) {
          return new AttributeCompletion(schemaDocument, handler.getCurrentElement());
      }
    }

    return null;
  }

}
