package io.github.handofgod94.schema.resolve;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Custom LS Resolver to capture all the namespaces defined as part
 * of schemaLocation, which will then be resolved for xsd
 */
public class XsdLsResolver implements LSResourceResolver {

  private List<URI> namespaces = new ArrayList<>();

  @Override
  public LSInput resolveResource(String type, String namespaceUri,
                                 String publicId, String systemId, String baseUri) {
    if (systemId != null) namespaces.add(URI.create(systemId));
    return null;
  }

  public List<URI> getNamespaces() {
    return namespaces;
  }
}
