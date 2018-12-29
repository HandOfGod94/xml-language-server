package io.github.handofgod94.lsp.completion;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.github.handofgod94.AbstractXmlUnitTest;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.wrappers.XsAdapter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestElementCompletion extends AbstractXmlUnitTest {
  private QName parentElement;
  private SchemaDocument schemaDocument;

  @Inject private XsAdapter.Factory factory;

  @BeforeEach
  public void setup() throws IOException, SAXException {
    schemaDocument = createDummyXsdSchema();
    Guice.createInjector(guiceModule).injectMembers(this);
  }

  @Test
  public void testValidParent() {
    parentElement = new QName(null, "shipto");
    ElementCompletion elementCompletion =
        new ElementCompletion(schemaDocument, parentElement, factory);

    Set<String> expectedLabels =
      new HashSet<>(Arrays.asList("name", "address", "city", "country"));
    Set<String> actual =
      elementCompletion.getCompletions().stream().map(CompletionItem::getLabel).collect(Collectors.toSet());

    assertEquals(4, elementCompletion.getCompletions().size());
    actual.forEach(e -> assertTrue(expectedLabels.contains(e)));
  }
}
