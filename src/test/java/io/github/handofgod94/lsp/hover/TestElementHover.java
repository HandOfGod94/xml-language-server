package io.github.handofgod94.lsp.hover;

import com.google.inject.Guice;
import com.google.inject.Inject;
import io.github.handofgod94.AbstractLangServerTest;
import io.github.handofgod94.common.parser.PositionalHandler;
import io.github.handofgod94.main.XmlLanguageServer;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.SchemaDocumentType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestElementHover extends AbstractLangServerTest {

  private String MOCK_XSD_TEXT =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n"
      + " <xs:element name=\"elementWithDoc\" type=\"xs:string\">\n"
      + "   <xs:annotation>\n"
      + "     <xs:documentation source=\"Short Note\">\n"
      + "       This Schema defines a Foo note!\n"
      + "     </xs:documentation>\n"
      + "   </xs:annotation>\n"
      + " </xs:element>\n"
      + " <xs:element name=\"elementWithoutDoc\" type=\"xs:string\"/>\n"
      + "</xs:schema>";

  private String MOCK_XML_TEXT =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!-- <elementWithDoc>someData in elementWithDoc</elementWithDoc> -->\n"
      + "<elementWithDoc>someData in elementWithDoc</elementWithDoc>\n"
      + "<elementWithoutDoc>some data not in  elementWithDoc</elementWithoutDoc>\n";

  private SchemaDocument schemaDocument;
  private TextDocumentItem textDocumentItem;

  @Inject
  private PositionalHandler.Factory handlerFactory;


  @BeforeEach
  public void setup() throws IOException, SAXException {
    // setup dependencies required for class

    XSLoader loader = new XMLSchemaLoader();
    // TODO: Check if we can mock loadURI
    File temp = File.createTempFile("temp", ".xsd");
    temp.deleteOnExit();
    Files.write(Paths.get(temp.toURI()), MOCK_XSD_TEXT.getBytes(StandardCharsets.UTF_8));

    XSModel model = loader.loadURI(temp.toURI().toString());
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(temp);

    this.schemaDocument =
      new SchemaDocument.Builder(model, schema, SchemaDocumentType.XSD).build();

    this.textDocumentItem =
      new TextDocumentItem(DUMMY_URI, XmlLanguageServer.LANGUAGE_ID, 0, MOCK_XML_TEXT);

    Guice.createInjector(guiceModule).injectMembers(this);
  }

  @Test
  public void testValidElement() {
    Position pos = new Position(2, 3);
    ElementHover hover = new ElementHover("elementWithDoc",
      schemaDocument, textDocumentItem, pos, handlerFactory);

    MarkupContent actualContent = hover.getHover().getContents().getRight();

    assertNotNull(actualContent.getKind());
    assertTrue(actualContent.getValue().contains("SHORT NOTE"), "It should have descriptions with SHORT NOTE");
  }

  static Stream<Arguments> invalidPositionAndWords() {
    return Stream.of(
      Arguments.of(new Position(2, 20), "someData"),
      Arguments.of(new Position(3, 52), "data")
    );
  }

  @ParameterizedTest
  @MethodSource(value = "invalidPositionAndWords")
  public void testInvalidElement(Position position, String wordHovered) {
    ElementHover hover = new ElementHover(wordHovered,
      schemaDocument, textDocumentItem, position, handlerFactory);
    MarkupContent content = hover.getHover().getContents().getRight();

    assertEquals("", content.getValue());
  }

}
