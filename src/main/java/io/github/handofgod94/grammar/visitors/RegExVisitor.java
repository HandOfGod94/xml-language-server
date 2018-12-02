package io.github.handofgod94.grammar.visitors;

import io.github.handofgod94.grammar.AttributeContext;
import io.github.handofgod94.grammar.CommentsContext;
import io.github.handofgod94.grammar.ElementContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initialize the matching group maps for elements
 * and attributes.
 */
public class RegExVisitor implements Visitor {

  @Override
  public void visit(ElementContext context) {
    String regEx = context.getBegRegEx();
    String line = context.getLine();
    Map<Integer, String> capturedGroups = new LinkedHashMap<>();

    Pattern pattern = Pattern.compile(regEx);
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      context.setMatched(true);
      context.setMatchStart(matcher.start());
      context.setMatchEnd(matcher.end() - 1);
      for (int groupNum : context.getGroupsToMatch()) {
        String value = matcher.group(groupNum);
        if (value != null) capturedGroups.put(groupNum, value);
      }
    }

    context.setCapturedGroups(capturedGroups);
  }

  @Override
  public void visit(AttributeContext context) {
    String regEx = context.getRegEx();
    String line = context.getLine();
    Map<Integer, String> capturedGroups = new LinkedHashMap<>();

    Pattern pattern = Pattern.compile(regEx);
    Matcher matcher = pattern.matcher(line);

    // TODO: handle multiple attributes for same element.
    while (matcher.find()) {
      context.setMatched(true);
      context.setMatchStart(matcher.start());
      context.setMatchEnd(matcher.end() - 1);
      for (int groupNum : context.getGroupsToMatch()) {
        String value = matcher.group(groupNum);
        if (value != null) capturedGroups.put(groupNum, value);
      }
    }
    context.setCapturedGroups(capturedGroups);
  }

  @Override
  public void visit(CommentsContext context) {
    String regEx = context.getBegRegEx();
    String line = context.getLine();
    Map<Integer, String> capturedGroups = new LinkedHashMap<>();

    Pattern pattern = Pattern.compile(regEx);
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      context.setMatched(true);
      context.setMatchStart(matcher.start());
      context.setMatchEnd(matcher.end() - 1);
      for (int groupNum : context.getGroupsToMatch()) {
        String value = matcher.group(groupNum);
        if (value != null) capturedGroups.put(groupNum, value);
      }
    }

    context.setCapturedGroups(capturedGroups);
  }
}
