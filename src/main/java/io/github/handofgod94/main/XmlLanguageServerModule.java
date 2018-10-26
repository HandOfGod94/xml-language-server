package io.github.handofgod94.main;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import io.github.handofgod94.common.DocumentManagerFactory;
import io.github.handofgod94.lsp.diagnostic.DiagnosticErrorHandler;
import io.github.handofgod94.lsp.diagnostic.XmlDiagnosticServiceFactory;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.XsdDocument;
import io.github.handofgod94.schema.resolve.SchemaResolver;
import io.github.handofgod94.schema.resolve.XsdSchemaResolver;

/**
 * Google guice module for Language server.
 * It defines all the binding configuration required for object
 * instantiation.
 */
public class XmlLanguageServerModule extends AbstractModule {

  @Override
  protected void configure() {

    // Includes bindings for DTD and XSD documents.
    bind(SchemaDocument.class).annotatedWith(Names.named("Xsd")).to(XsdDocument.class);
    bind(SchemaResolver.class).annotatedWith(Names.named("Xsd")).to(XsdSchemaResolver.class);

    // Bindings for concrete class
    bind(DiagnosticErrorHandler.class);

    // FactoryBuilders for assisted injections
    install(new FactoryModuleBuilder().build(DocumentManagerFactory.class));
    install(new FactoryModuleBuilder().build(XmlDiagnosticServiceFactory.class));
  }
}
