/**
 * In order to achieve some of the functionality available
 * in LSP server such as autocompletion, hovers etc. we need
 * contextual information based on the position.
 *
 * <p>
 * Currently VSCode client is not exposing anything related to
 * scope based on the loaded grammar for the current position.
 * This needs to be implemented from scratch.
 * </p>
 *
 * <p>
 * There are various approach to solve the problem.
 * Current implementation is uses the TextMate language available
 * in VSCode Repository ("xml.tmLanguage.json") to match the current line.
 * </p>
 *
 * <p>
 * <b>TMLang Approach:</b>
 * As per the TextMate language specification, it contains patterns defined
 * with set of RegEx pairs(Beginning and end) or a single RegEx to match the
 * current line. The order of matching is important.
 *
 * In PoC implementation the RegEx is contained withing the <code>Context</code>
 * classes. i.e. {@link io.github.handofgod94.grammar.AttributeContext},
 * {@link io.github.handofgod94.grammar.CommentsContext},
 * {@link io.github.handofgod94.grammar.ElementContext} etc.
 * This information will then be used by visitors
 * ({@link io.github.handofgod94.grammar.visitors.RegExVisitor} etc.)
 * to extract the matching and contextual information.
 * </p>
 *
 * <p>
 * The order of matching is important. In current scenario, it's following order:
 * Comments -> Elements -> Attributes
 * </p>
 *
 * <p>
 * <b>Note:</b>
 * The whole implementation is likely to change, as it's not working
 * correctly for auto complete, although it could work for hover.
 * The main problem is the predicting next possible hover based on the
 * current position in line.
 * It becomes even more vital when we think of multiline elements/comments.
 * </p>
 */
package io.github.handofgod94.grammar;
