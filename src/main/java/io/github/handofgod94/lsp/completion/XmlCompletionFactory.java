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
  private final DocumentManager.Factory docManagerFactory;
  private final GrammarProcessor.Factory grammarFactory;
  private final PositionalHandler.Factory posHandlerFactory;
  private final AbstractXmlCompletion.Factory completer;

  @Inject
  XmlCompletionFactory(DocumentManager.Factory docManagerFactory,
                       GrammarProcessor.Factory grammarFactory,
                       PositionalHandler.Factory posHandlerFactory,
                       AbstractXmlCompletion.Factory completer) {
    this.docManagerFactory = docManagerFactory;
    this.grammarFactory = grammarFactory;
    this.posHandlerFactory = posHandlerFactory;
    this.completer = completer;
  }

  /**
   * Provides XmlCompletion instances for elements and attributes.
   * It first gets the scope of current position using XML Grammar graph
   * generated by {@link GrammarProcessor}. Based on the scope, it
   * triggers information for elements and attributes
   * @param schemaDocument Parsed XSD SchemaDocument object
   * @param params Completion params, useful in determining the trigger kind.
   * @param documentItem current text document item
   * @return XmlCompletion instance, either ElementCompletion, AttributeCompletion
   *     wrapped around Optional or Optional.empty()
   */
  public Optional<XmlCompletion> create(@Nonnull SchemaDocument schemaDocument,
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
            return Optional.empty();
          }
          return Optional.of(
              completer.createEdeCompleter(schemaDocument, handler.getParentElement())
          );
        case ATTRIBUTE_TRIGGER_CHARACTER:
          if (currentScope.isPresent() && currentScope.get().contains("meta")) {
            return Optional.of(
                completer.createAttrCompleter(schemaDocument, handler.getCurrentElement())
            );
          }
          break;
        default:
          return Optional.empty();
      }

    } else if (triggerKind.equals(CompletionTriggerKind.Invoked)) {

      if (currentScope.isPresent()
          && currentScope.get().contains("tag")
          && currentScope.get().contains("entity")) {
        return Optional.of(
            completer.createEdeCompleter(schemaDocument, handler.getParentElement())
        );
      }

      if (currentScope.isPresent()
          && (currentScope.get().contains("attribute")
            || currentScope.get().contains("meta"))) {
        return Optional.of(
            completer.createAttrCompleter(schemaDocument, handler.getCurrentElement())
        );
      }
    }

    return Optional.empty();
  }

}
