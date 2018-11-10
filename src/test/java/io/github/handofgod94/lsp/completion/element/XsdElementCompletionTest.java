package io.github.handofgod94.lsp.completion.element;

import io.github.handofgod94.AbstractXmlUnitTest;
import io.github.handofgod94.schema.SchemaDocument;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class XsdElementCompletionTest extends AbstractXmlUnitTest {

  private QName parentElement;
  private SchemaDocument schemaDocument;
  private XsdElementCompletion xsdElementCompletion;

  @BeforeEach
  public void setup() throws IOException, SAXException {
    schemaDocument = createDummyXsdSchema();
  }

  @Test
  public void testValidParent() {
    parentElement = new QName(null, "shipto");
    xsdElementCompletion = new XsdElementCompletion(parentElement, schemaDocument);

    Set<String> expectedLabels =
      new HashSet<>(Arrays.asList("name", "address", "city", "country"));
    Set<String> actual =
        xsdElementCompletion.get().stream().map(CompletionItem::getLabel).collect(Collectors.toSet());

    assertEquals(xsdElementCompletion.get().size(), 4);
    actual.forEach(e -> assertTrue(expectedLabels.contains(e)));
  }

  @Test
  public void testInvalidParent() {
    parentElement = new QName(null, "invalid");
    xsdElementCompletion =new XsdElementCompletion(parentElement, schemaDocument);
    List<CompletionItem> actual = xsdElementCompletion.get();

    assertEquals(actual.size(), 0);
  }
}
