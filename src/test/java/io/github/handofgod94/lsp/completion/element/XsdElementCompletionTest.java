package io.github.handofgod94.lsp.completion.element;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.github.handofgod94.AbstractLangServerTest;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapterFactory;
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


public class XsdElementCompletionTest extends AbstractLangServerTest {

  private QName parentElement;
  private SchemaDocument schemaDocument;
  private XsdElementCompletion xsdElementCompletion;

  @Inject
  private XsAdapterFactory adapterFactory;

  @BeforeEach
  public void setup() throws IOException, SAXException {
    schemaDocument = createDummyXsdSchema();
    Guice.createInjector(guiceModule).injectMembers(this);
  }

  @Test
  public void testValidParent() {
    parentElement = new QName(null, "shipto");
    xsdElementCompletion = new XsdElementCompletion(parentElement, schemaDocument, adapterFactory);

    Set<String> expectedLabels =
      new HashSet<>(Arrays.asList("name", "address", "city", "country"));
    Set<String> actual =
      xsdElementCompletion.getCompletionItems().stream().map(CompletionItem::getLabel).collect(Collectors.toSet());

    assertEquals(4, xsdElementCompletion.getCompletionItems().size());
    actual.forEach(e -> assertTrue(expectedLabels.contains(e)));
  }

  @Test
  public void testInvalidParent() {
    parentElement = new QName(null, "invalid");
    xsdElementCompletion = new XsdElementCompletion(parentElement, schemaDocument, adapterFactory);
    List<CompletionItem> actual = xsdElementCompletion.getCompletionItems();

    assertEquals(actual.size(), 0);
  }
}
