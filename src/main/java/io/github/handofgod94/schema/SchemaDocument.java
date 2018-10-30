package io.github.handofgod94.schema;

import org.apache.xerces.xs.XSModel;
import javax.xml.validation.Schema;

public class SchemaDocument {

  // Required params
  private XSModel xsModel;
  private Schema schema;
  private SchemaDocumentType documentType;

  // Optional params
  private String namespace;

  private SchemaDocument(Builder builder) {
    this.xsModel = builder.xsModel;
    this.documentType =builder.documentType;
    this.namespace = builder.namespace;
    this.schema = builder.schema;
  }

  public static class Builder {

    private XSModel xsModel;
    private Schema schema;
    private SchemaDocumentType documentType;
    private String namespace;

    public Builder(XSModel xsModel, Schema schema, SchemaDocumentType documentType) {
      this.xsModel = xsModel;
      this.schema = schema;
      this.documentType = documentType;
    }

    public Builder addNamespace(String namespace) {
      this.namespace = namespace;
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

  public String getNamespace() {
    return namespace;
  }
}
