package org.aksw.owl2nl.util.grammar;

/**
 *
 */
public class Grammar {

  public static IGrammar en = new English();
  public static IGrammar de = new German();

  public static void main(final String[] a) {
    System.out.println(Grammar.en.plural("child"));
  }
}
