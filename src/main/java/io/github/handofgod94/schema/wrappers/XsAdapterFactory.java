package io.github.handofgod94.schema.wrappers;

import com.google.inject.name.Named;
import org.apache.xerces.xs.XSObject;

public interface XsAdapterFactory {

  @Named("Element") XsAdapter getElementAdapter(XSObject xsObject);

  @Named("Attribute") XsAdapter getAttributeAdapter(XSObject xsObject);
}
