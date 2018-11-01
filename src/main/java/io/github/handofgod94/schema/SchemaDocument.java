package io.github.handofgod94.schema;

import org.apache.xerces.xs.XSModel;
import org.dom4j.Document;
import org.dom4j.Namespace;
import javax.xml.validation.Schema;
import java.util.List;

public class SchemaDocument {

  // Required params
  private XSModel xsModel;
  private Schema schema;
  private SchemaDocumentType documentType;
  private List<Document> parsedSchemaDocs;

  // Optional params
  private Namespace namespace;

  private SchemaDocument(Builder builder) {
    this.xsModel = builder.xsModel;
    this.documentType =builder.documentType;
    this.namespace = builder.namespace;
    this.schema = builder.schema;
    this.parsedSchemaDocs = builder.parsedSchemaDocs;
  }

  public static class Builder {

    private XSModel xsModel;
    private Schema schema;
    private SchemaDocumentType documentType;
    private Namespace namespace;
    private List<Document> parsedSchemaDocs;

    public Builder(XSModel xsModel, Schema schema, SchemaDocumentType documentType) {
      this.xsModel = xsModel;
      this.schema = schema;
      this.documentType = documentType;
    }

    public Builder addNamespace(Namespace namespace) {
      this.namespace = namespace;
      return this;
    }

    public Builder addParsedSchemaDocs(List<Document> parsedSchemas) {
      this.parsedSchemaDocs = parsedSchemas;
      return this;
    }

    public SchemaDocument build() {
      return new SchemaDocument(this);
    }
  }

  // Getters

  public XSModel getXsModel() {
    return xsModel;
  }

  public Schema getSchema() {
    return schema;
  }

  public SchemaDocumentType getDocumentType() {
    return documentType;
  }

  public Namespace getNamespace() {
    return namespace;
  }

  public List<Document> getParsedSchemaDocs() {
    return parsedSchemaDocs;
  }
}
