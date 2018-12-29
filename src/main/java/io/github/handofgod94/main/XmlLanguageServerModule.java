package io.github.handofgod94.main;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import io.github.handofgod94.common.document.DocumentManager;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.grammar.GrammarProcessor;
import io.github.handofgod94.grammar.graph.LanguageGraphContext;
import io.github.handofgod94.lsp.completion.AbstractXmlCompletion;
import io.github.handofgod94.lsp.completion.AttributeCompletion;
import io.github.handofgod94.lsp.completion.ElementCompletion;
import io.github.handofgod94.lsp.completion.XmlCompletionFactory;
import io.github.handofgod94.lsp.hover.AttributeHover;
import io.github.handofgod94.lsp.hover.ElementHover;
import io.github.handofgod94.lsp.hover.XmlHover;
import io.github.handofgod94.lsp.hover.XmlHoverFactory;
import io.github.handofgod94.lsp.hover.provider.XmlHoverProviderFactory;
import io.github.handofgod94.schema.resolve.SchemaResolver;
import io.github.handofgod94.schema.resolve.XsdSchemaResolver;
import io.github.handofgod94.schema.wrappers.AttributeAdapter;
import io.github.handofgod94.schema.wrappers.ElementAdapter;
import io.github.handofgod94.schema.wrappers.XsAdapter;

/**
 * Google guice module for Language server.
 * It defines all the binding configuration required for object
 * instantiation.
 */
public class XmlLanguageServerModule extends AbstractModule {

  @Override
  protected void configure() {

    // Includes bindings for DTD and XSD documents.
    bind(SchemaResolver.class).annotatedWith(Names.named("Xsd")).to(XsdSchemaResolver.class);

    // Bindings for concrete class
    bind(XmlCompletionFactory.class);
    bind(LanguageGraphContext.class);

    // FactoryBuilders for assisted injections
    install(new FactoryModuleBuilder().build(DocumentManager.Factory.class));
    install(new FactoryModuleBuilder().build(PositionalHandler.Factory.class));
    install(new FactoryModuleBuilder().build(XmlHoverProviderFactory.class));
    install(new FactoryModuleBuilder()
        .implement(XmlHover.class, Names.named("Element"), ElementHover.class)
        .implement(XmlHover.class, Names.named("Attribute"), AttributeHover.class)
        .build(XmlHoverFactory.class));
    install(new FactoryModuleBuilder()
        .implement(XsAdapter.class, Names.named("Element"), ElementAdapter.class)
        .implement(XsAdapter.class, Names.named("Attribute"), AttributeAdapter.class)
        .build(XsAdapter.Factory.class));
    install(new FactoryModuleBuilder()
        .implement(AbstractXmlCompletion.class, Names.named("Element"), ElementCompletion.class)
        .implement(AbstractXmlCompletion.class, Names.named("Attribute"), AttributeCompletion.class)
        .build(AbstractXmlCompletion.Factory.class));
    install(new FactoryModuleBuilder().build(GrammarProcessor.Factory.class));
  }
}
