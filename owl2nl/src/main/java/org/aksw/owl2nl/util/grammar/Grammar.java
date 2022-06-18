package org.aksw.owl2nl.util.grammar;

public class Grammar {

  public static IGrammar getEN() {
    return new English();
  }

  public static IGrammar getDE() {
    return new German();
  }
}
