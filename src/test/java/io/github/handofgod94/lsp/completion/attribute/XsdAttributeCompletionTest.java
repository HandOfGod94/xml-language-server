package io.github.handofgod94.lsp.completion.attribute;

import io.github.handofgod94.AbstractXmlUnitTest;
import io.github.handofgod94.schema.SchemaDocument;
import java.io.IOException;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XsdAttributeCompletionTest extends AbstractXmlUnitTest {

  private SchemaDocument schemaDocument;

  @BeforeEach
  public void setup() throws IOException, SAXException {
    schemaDocument = createDummyXsdSchema();
  }

  @Test
  public void testValidCurrentElement() {
    QName currentElement = new QName("shipto");
    XsdAttributeCompletion attr = new XsdAttributeCompletion(currentElement, schemaDocument);
    List<CompletionItem> actual = attr.get();

    assertEquals(1, actual.size());
    assertEquals("locationId", actual.get(0).getLabel());
    assertEquals("integer", actual.get(0).getDetail());
  }

  @Test
  public void testInvalidCurrentElement() {
    QName invalidElement = new QName("dummy");
    QName emptyElement = new QName("shipper");
    XsdAttributeCompletion invalidAttr = new XsdAttributeCompletion(invalidElement, schemaDocument);
    XsdAttributeCompletion emptyAttr = new XsdAttributeCompletion(emptyElement, schemaDocument);

    assertEquals(0, invalidAttr.get().size());
    assertEquals(0, emptyAttr.get().size());
  }
}
