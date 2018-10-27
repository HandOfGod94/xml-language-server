package io.github.handofgod94.schema;

import java.util.List;
import javax.xml.validation.Schema;
import org.apache.xerces.xs.XSModel;

/**
 * Interface to download and load schema related documents.
 * Each document loaded in the editor should have either xsd schema location
 * or DTD definition. These documents will then be loaded and stored in repository
 * for future use.
 */
public interface SchemaDocument {

  /**
   * Reads schema text of each document file in string format
   * and loads it to appropriate Xerces objects.
   * @param schemaTextList list of string containing content of schema.
   */
  void loadSchema(List<String> schemaTextList);

  /**
   * Getter for obtaining xsModel after schema is loaded.
   * @return XsModel object containing loaded schema
   */
  XSModel getXsModel();

  /**
   * Getter for obtaining schema definition from the schema document.
   * @return Schema object
   */
  Schema getSchema();
}
