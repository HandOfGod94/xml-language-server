package io.github.handofgod94.lsp.completion;

import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.grammar.GrammarProcessor;
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
    DocumentManager manager = new DocumentManager(documentItem);
    PositionalHandler handler = new PositionalHandler(position);
    XmlUtil.positionalParse(handler, text);
    int line = position.getLine();
    GrammarProcessor processor = new GrammarProcessor(position, manager.getLineAt(line));
    String currentScope = processor.processScope();
    if (triggerKind.equals(CompletionTriggerKind.TriggerCharacter)) {
      switch (triggerChar) {
        case ELEMENT_TRIGGER_CHAR:
          if (currentScope != null && !currentScope.contains("meta")) {
            return null;
          } else {
            return new ElementCompletion(schemaDocument, handler.getParentElement());
          }
        case ATTRIBUTE_TRIGGER_CHARACTER:
          if (currentScope != null && currentScope.contains("meta")) {
            return new AttributeCompletion(schemaDocument, handler.getCurrentElement());
          }
      }
    }

    if (triggerKind.equals(CompletionTriggerKind.Invoked)) {
      if (currentScope != null && currentScope.contains("tag") && currentScope.contains("entity"))
        return new ElementCompletion(schemaDocument, handler.getParentElement());
      if (currentScope != null
        && (currentScope.contains("attribute") || currentScope.contains("meta")))
        return new AttributeCompletion(schemaDocument, handler.getCurrentElement());
    }

    return null;
  }

}
