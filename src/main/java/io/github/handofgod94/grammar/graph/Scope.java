package io.github.handofgod94.grammar.graph;

import java.util.Map;
import java.util.Objects;

public class Scope {

  private String name;
  private String beginRegEx;
  private String endRegEx;
  private Map<Integer, String> beginCaptures;
  private Map<Integer, String> endCaptures;
  private Map<Integer, String> captures;
  private String matchPattern;


  public String getBeginRegEx() {
    return beginRegEx;
  }

  public void setBeginRegEx(String beginRegEx) {
    this.beginRegEx = beginRegEx;
  }

  public String getEndRegEx() {
    return endRegEx;
  }

  public void setEndRegEx(String endRegEx) {
    this.endRegEx = endRegEx;
  }

  public Map<Integer, String> getBeginCaptures() {
    return beginCaptures;
  }

  public void setBeginCaptures(Map<Integer, String> beginCaptures) {
    this.beginCaptures = beginCaptures;
  }

  public Map<Integer, String> getEndCaptures() {
    return endCaptures;
  }

  public void setEndCaptures(Map<Integer, String> endCaptures) {
    this.endCaptures = endCaptures;
  }

  public Map<Integer, String> getCaptures() {
    return captures;
  }

  public void setCaptures(Map<Integer, String> captures) {
    this.captures = captures;
  }

  public String getMatchPattern() {
    return matchPattern;
  }

  public void setMatchPattern(String matchPattern) {
    this.matchPattern = matchPattern;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Scope scope = (Scope) o;
    return Objects.equals(name, scope.name)
      && Objects.equals(beginRegEx, scope.beginRegEx)
      && Objects.equals(endRegEx, scope.endRegEx)
      && Objects.equals(beginCaptures, scope.beginCaptures)
      && Objects.equals(endCaptures, scope.endCaptures)
      && Objects.equals(captures, scope.captures)
      && Objects.equals(matchPattern, scope.matchPattern);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(name, beginRegEx, endRegEx, beginCaptures, endCaptures, captures, matchPattern);
  }

  @Override
  public String toString() {
    return "ScopeNode{"
      + "name='" + name + '\''
      + ", beginRegEx='" + beginRegEx + '\''
      + ", endRegEx='" + endRegEx + '\''
      + ", beginCaptures=" + beginCaptures
      + ", endCaptures=" + endCaptures
      + ", captures=" + captures
      + ", matchPattern='" + matchPattern + '\''
      + '}';
  }
}
