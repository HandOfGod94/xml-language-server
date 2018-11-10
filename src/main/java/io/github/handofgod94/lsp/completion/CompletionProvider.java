package io.github.handofgod94.lsp.completion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import io.github.handofgod94.common.XmlUtil;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.common.document.DocumentManagerFactory;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.common.parser.PositionalHandlerFactory;
import io.github.handofgod94.lsp.completion.attribute.AttributeCompletion;
import io.github.handofgod94.lsp.completion.attribute.AttributeCompletionFactory;
import io.github.handofgod94.lsp.completion.element.ElementCompletion;
import io.github.handofgod94.lsp.completion.element.ElementCompletionFactory;
import io.github.handofgod94.schema.SchemaDocument;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.CompletionTriggerKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;
import org.xml.sax.SAXException;

public class CompletionProvider implements Provider<List<CompletionItem>> {

  private static final Logger logger = LogManager.getLogger(CompletionProvider.class.getName());

  private static final String TAG_AUTOCOMPLETE_TRIGGER_CHARACTER = "<";
  private static final String ATTRIBUTE_AUTOCOMPLETE_TRIGGER_CHARACTER = " ";
  private static final String EXPRESSION_AUTOCOMPLETE_TRIGGER_CHARACTER = "{";

  private final CompletionParams params;
  private final TextDocumentItem textDocumentItem;
  private final SchemaDocument schemaDocument;
  private final ElementCompletionFactory elementCompletionFactory;
  private final AttributeCompletionFactory attrCompletionFactory;
  private final PositionalHandlerFactory handlerFactory;
  private final DocumentManagerFactory documentManagerFactory;

  @Inject
  CompletionProvider(@Assisted CompletionParams params,
                     @Assisted TextDocumentItem textDocumentItem,
                     @Assisted SchemaDocument schemaDocument,
                     ElementCompletionFactory elementCompletionFactory,
                     AttributeCompletionFactory attrCompletionFactory,
                     PositionalHandlerFactory handlerFactory,
                     DocumentManagerFactory documentManagerFactory) {
    this.params = params;
    this.textDocumentItem = textDocumentItem;
    this.schemaDocument = schemaDocument;
    this.elementCompletionFactory = elementCompletionFactory;
    this.attrCompletionFactory = attrCompletionFactory;
    this.handlerFactory = handlerFactory;
    this.documentManagerFactory = documentManagerFactory;
  }

  @Override
  public List<CompletionItem> get() {

    Position position = params.getPosition();
    CompletionTriggerKind triggerKind = params.getContext().getTriggerKind();
    String triggerChar = params.getContext().getTriggerCharacter();
    DocumentManager documentManager = documentManagerFactory.create(textDocumentItem);


    PositionalHandler posInfo = handlerFactory.create(position);
    parse(posInfo);
    String currentLine = documentManager.getLineAt(position.getLine());

    if (triggerKind.equals(CompletionTriggerKind.TriggerCharacter)) {
      switch (triggerChar) {
        case TAG_AUTOCOMPLETE_TRIGGER_CHARACTER:
          ElementCompletion elementCompletion =
              elementCompletionFactory.create(posInfo.getParentTag(), schemaDocument);
          return elementCompletion.get();
        default:
          return new ArrayList<>();
      }
    } else if (triggerKind.equals(CompletionTriggerKind.Invoked)) {
      // TODO: Check for validation for current postion and show accordingly.
      if (!XmlUtil.isInsideString(currentLine, position.getCharacter())) {
        AttributeCompletion attrCompletionItem =
            attrCompletionFactory.create(posInfo.getCurrentTag(), schemaDocument);
        return attrCompletionItem.get();
      }
    }

    return new ArrayList<>();
  }

  private void parse(PositionalHandler handler) {
    // parse using the custom handler
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();
      String documentText = textDocumentItem.getText();
      InputStream documentStream =
          new ByteArrayInputStream(documentText.getBytes(StandardCharsets.UTF_8));
      parser.parse(documentStream, handler);
    } catch (SAXException | IOException e) {
      // FIXME: Too much noise in debug mode while
      logger.debug("Exception while parsing the document", e);
    } catch (ParserConfigurationException e) {
      logger.error("Exception while setting up parser", e);
    }
  }
}
