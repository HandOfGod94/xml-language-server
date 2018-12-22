/**
 * This package is responsible for processing of XML Grammar.
 * The idea is to load the grammar, based on TextMate Language for XML,
 * and use it derive scope information based on position.
 *
 * <p>
 * Currently VSCode or LSP client doesn't have a standard way to
 * expose scope information based on the position. The positional
 * scope information is useful to derive decision such as
 * <ul>
 *     <li>when to show hovers?</li>
 *     OR
 *     <li>when to show autocomplete?</li>
 * </ul>
 * </p>
 *
 * <p>
 * For e.g. The autocompletion should not be triggered inside a string
 * or a comment for elements, the same thing goes for hovers too.
 * </p>
 *
 * <p>
 * In order to device scope related information based on the position
 * we need a defined order and patterns to match it with.
 * TextMate grammar for XML beautifully describes on what needs to be done,
 * furthermore it's widely adopted by text editors as well to show coloring
 * information.
 * </p>
 *
 * <p>
 * If we closely notice the grammar defined in xml.tmLang.json, we could see
 * that it's a DAG(Directed Acylic Graph), which needs to be traversed in a
 * topologically sorted fashion to get the deepest matching scope.
 * {@link io.github.handofgod94.grammar.GrammarProcessor} is responsible
 * for loading the graph and processing the scope from it.
 * </p>
 */
package io.github.handofgod94.grammar;
