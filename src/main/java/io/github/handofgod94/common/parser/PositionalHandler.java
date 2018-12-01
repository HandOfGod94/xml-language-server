package io.github.handofgod94.common.parser;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Stack;
import javax.xml.namespace.QName;
import org.eclipse.lsp4j.Position;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Custom SAXHandler to getCompletionItems document information based on position.
 * Useful in providing hover, autocompletion etc. based on position.
 */
public class PositionalHandler extends DefaultHandler {

  /**
   * Guice factory to create positional handler instances.
   */
  public interface Factory {
    PositionalHandler create(Position position);
  }

  // Locator for current position
  private Locator locator;

  // Element stack for figuring out parent
  private Stack<QName> elementStack = new Stack<>();

  // Parent element
  private QName parentElement = null;

  // Current element
  private QName currentElement = null;

  // Reference parent start and end position
  private Position parentStart = new Position(Integer.MIN_VALUE, 0);
  private Position parentEnd = new Position(Integer.MAX_VALUE, 0);

  // Current Position
  private final Position position;

  @Inject
  public PositionalHandler(@Assisted Position position) {
    this.position = position;
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  @Override
  public void startElement(String uri, String localName, String qualifiedName,
                           Attributes attributes) throws SAXException {
    // Logic is to find parent when there are no parsing errors in document.
    // TODO: currently only works for 1 element/line scenario.
    Position cursorPosition = new Position();
    cursorPosition.setLine(locator.getLineNumber() - 1);

    if (cursorPosition.getLine() <= position.getLine()) {
      elementStack.push(new QName(uri, localName));
      parentStart.setLine(Math.max(parentStart.getLine(), position.getLine()));
    }

    if (cursorPosition.getLine() == position.getLine()) {
      currentElement = new QName(uri, localName);
    }

  }

  @Override
  public void endElement(String uri, String localName, String qualifiedName) throws SAXException {
    if (!elementStack.isEmpty()) {
      QName peek = elementStack.peek();

      // Get current cursorPosition
      // TODO: currently only works for 1 element/line scenario.
      Position cursorPosition = new Position();
      cursorPosition.setLine(locator.getLineNumber() - 1);

      // update parentEnd position
      if (cursorPosition.getLine() >= position.getLine()) {
        parentEnd.setLine(Math.max(parentEnd.getLine(), position.getLine()));
        QName qname = new QName(uri, localName);
        // if we have peek, and it is equal to qualifiedName, then we got a parent element.
        // Set parent element iff, it is equal to null, otherwise we already have a parent element.
        if ((peek != null)
            && (peek.equals(qname))
            && (parentElement == null)
            && (parentEnd.getLine() >= position.getLine())) {
          parentElement = peek;
          elementStack.pop();
        }
      } else {
        // if we encounter endElement before "position", that means
        // some other element is ending.
        elementStack.pop();
      }
    }
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    // Suppress the parseException while editing.s
    // HACK: While editing if you getCompletionItems parsing error, means the previous element is the parent
    if (!elementStack.isEmpty()) {
      parentElement = elementStack.peek();
    }
    super.fatalError(e);
  }

  // Getters

  public QName getParentElement() {
    return parentElement;
  }

  public QName getCurrentElement() {
    return currentElement;
  }
}
