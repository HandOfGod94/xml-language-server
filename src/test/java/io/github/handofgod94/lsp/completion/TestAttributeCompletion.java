package io.github.handofgod94.lsp.completion;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.github.handofgod94.AbstractXmlUnitTest;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.io.IOException;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAttributeCompletion extends AbstractXmlUnitTest {

  private SchemaDocument schemaDocument;

  @Inject private XsAdapter.Factory adapterFactory;

  @BeforeEach
  public void setup() throws IOException, SAXException {
    schemaDocument = createDummyXsdSchema();
    Guice.createInjector(guiceModule).injectMembers(this);
  }

  @Test
  public void testValidCurrentElement() {
    QName currentElement = new QName("shipto");
    AttributeCompletion attr =
        new AttributeCompletion(schemaDocument, currentElement, adapterFactory);
    List<CompletionItem> actual = attr.getCompletions();

    assertEquals(1, actual.size());
    assertEquals("locationId", actual.get(0).getLabel());
    assertEquals("integer", actual.get(0).getDetail());
  }

  @Test
  public void testInvalidCurrentElement() {
    QName invalidElement = new QName("dummy");
    QName emptyElement = new QName("shipper");
    AttributeCompletion invalidAttr = new AttributeCompletion(schemaDocument, invalidElement, adapterFactory);
    AttributeCompletion emptyAttr = new AttributeCompletion(schemaDocument, emptyElement, adapterFactory);

    assertEquals(0, invalidAttr.getCompletions().size());
    assertEquals(0, emptyAttr.getCompletions().size());
  }
}
