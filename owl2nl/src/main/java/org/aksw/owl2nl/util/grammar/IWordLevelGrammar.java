package org.aksw.owl2nl.util.grammar;

public interface IWordLevelGrammar {

  String plural(final String word);

  String singular(final String word);

  boolean isNoun(String word);

  boolean isPerson(String word);

  boolean isObject(String word);
}
