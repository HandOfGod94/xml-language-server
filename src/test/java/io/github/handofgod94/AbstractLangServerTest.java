package io.github.handofgod94;


import io.github.handofgod94.main.XmlLanguageServerModule;
import io.github.handofgod94.schema.SchemaDocument;
import io.github.handofgod94.schema.SchemaDocumentType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.xml.sax.SAXException;

public class AbstractLangServerTest {

  protected XmlLanguageServerModule guiceModule = new XmlLanguageServerModule();

  protected static final String DUMMY_URI = "dummyUri.xml";
  protected static final int DUMMY_VERSION = 1;
  protected static final String MOCK_XML_TEXT =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<beans xmlns = \"http://www.springframework.org/schema/beans\"\n"
      + "\t xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"\n"
      + "\t xsi:schemaLocation = \"http://www.springframework.org/schema/beans \n"
      + "\t http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\">\n"
      + "\t <bean id = \"helloWorld\" class = \"io.github.handofgod94.HelloWorld\""
      + "scope = \"prototype\"></bean>\n"
      + "</beans>";

  protected static final String MOCK_XSD_TEXT =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
      + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n"
      + "<xs:element name=\"shipper\" type=\"xs:string\" />\n"
      + "<xs:element name=\"shipto\">\n"
      + "<xs:complexType>\n"
      + "<xs:sequence>\n"
      + "<xs:element name=\"name\" type=\"xs:string\"/>\n"
      + "<xs:element name=\"address\" type=\"xs:string\"/>\n"
      + "<xs:element name=\"city\" type=\"xs:string\"/>\n"
      + "<xs:element name=\"country\" type=\"xs:string\"/>\n"
      + "</xs:sequence>\n"
      + "<xs:attribute name=\"locationId\" type=\"xs:integer\" />\n"
      + "</xs:complexType>\n"
      + "</xs:element>"
      + "</xs:schema>";

  protected final SchemaDocument createDummyXsdSchema() throws IOException, SAXException {
    XSLoader loader = new XMLSchemaLoader();
    File temp = File.createTempFile("temp", ".xsd");
    temp.deleteOnExit();
    Files.write(Paths.get(temp.toURI()), MOCK_XSD_TEXT.getBytes(StandardCharsets.UTF_8));

    XSModel model = loader.loadURI(temp.toURI().toString());
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(temp);

    SchemaDocument document = new SchemaDocument.Builder(model, SchemaDocumentType.XSD).addSchema(schema).build();

    return document;
  }
}
