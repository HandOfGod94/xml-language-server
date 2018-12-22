package io.github.handofgod94.grammar.graph;

import io.github.handofgod94.grammar.graph.nodes.Comments;
import io.github.handofgod94.grammar.graph.nodes.DoubleQuotedString;
import io.github.handofgod94.grammar.graph.nodes.Element;
import io.github.handofgod94.grammar.graph.nodes.SingleQuotedString;
import io.github.handofgod94.grammar.graph.nodes.TagStuff;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates Graph for the XML Language Grammar.
 * The grammar is referenced from xml.tmLang.json.
 * Each node(vertex) will be initialized with its respective regular expression
 * and other scoping realted information such as name of the scope, captures etc.
 *
 * <p>
 * This will then be used in {@link io.github.handofgod94.lsp.completion.XmlCompletionFactory}
 * and {@link io.github.handofgod94.lsp.hover.provider.XmlHoverProvider} to control their triggering
 * based on the scope.
 * </p>
 *
 * <p>
 * Right now it's not Singleton, since the graph needs to be cleaned up
 * after visiting the nodes on each iteration. This could lead to performance
 * issue.
 * </p>
 */
public class LanguageGraphContext {

  private final List<GrammarNode> traverseOrder;
  private final Map<GrammarNode, List<GrammarNode>> adjList;
  private final Comments comments = new Comments();
  private final Element element = new Element();
  private final TagStuff tagStuff = new TagStuff();
  private final DoubleQuotedString doubleQuoteString = new DoubleQuotedString();
  private final SingleQuotedString singleQuoteString = new SingleQuotedString();

  private final GrammarNode commentNode = new GrammarNode(comments);
  private final GrammarNode elementNode = new GrammarNode(element);
  private final GrammarNode tagStuffNode = new GrammarNode(tagStuff);
  private final GrammarNode doubleQuoteStringNode = new GrammarNode(doubleQuoteString);
  private final GrammarNode singleQuoteStringNode = new GrammarNode(singleQuoteString);

  /**
   * Generates the graph for XML Grammar.
   * It loads and initializes all the scoping information related
   * to various elements of xml.tmLang grammar and stores it in-memory.
   *
   *
   */
  public LanguageGraphContext() {
    commentNode.generateScope();
    elementNode.generateScope();
    tagStuffNode.generateScope();
    doubleQuoteStringNode.generateScope();
    singleQuoteStringNode.generateScope();

    // Generate graph using adjacency list
    Map<GrammarNode, List<GrammarNode>> graph = new LinkedHashMap<>();
    graph.put(commentNode, Collections.emptyList());
    graph.put(elementNode, Collections.singletonList(tagStuffNode));
    graph.put(tagStuffNode, Arrays.asList(doubleQuoteStringNode, singleQuoteStringNode));

    adjList = Collections.unmodifiableMap(graph);
    traverseOrder = Arrays.asList(commentNode, elementNode);
  }

  public Map<GrammarNode, List<GrammarNode>> getAdjList() {
    return adjList;
  }

  public List<GrammarNode> getTraverseOrder() {
    return traverseOrder;
  }
}
