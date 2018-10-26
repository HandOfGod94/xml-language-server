package io.github.handofgod94.lsp.diagnostic;

import io.github.handofgod94.main.XmlLanguageServer;
import io.github.handofgod94.schema.SchemaDocument;
import org.eclipse.lsp4j.TextDocumentItem;

/**
 * Factory for XmlDiagnosticService.
 * This is used for injecting {@link XmlDiagnosticService} instance.
 */
public interface XmlDiagnosticServiceFactory {

  /**
   * Factory method to create service.
   * @param documentItem currently working text document
   * @param server XmlLanguageServer object
   * @param schemaDocument Instance of schema document, could be XSD or DTD
   * @return XmlDiagnosticService instance
   * @see io.github.handofgod94.schema.XsdDocument
   */
  XmlDiagnosticService create(TextDocumentItem documentItem,
                              XmlLanguageServer server,
                              SchemaDocument schemaDocument);
}
