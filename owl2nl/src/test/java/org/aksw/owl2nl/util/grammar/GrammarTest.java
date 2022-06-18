package org.aksw.owl2nl.util.grammar;

import org.junit.Assert;
import org.junit.Test;

public class GrammarTest {

  IGrammar en = Grammar.getEN();

  @Test
  public void test() {
    Assert.assertEquals("tests", en.plural("test"));
    Assert.assertEquals("children", en.plural("child"));
    Assert.assertEquals("cats", en.plural("cat"));

    // Assert.assertEquals("test", grammar.singular(grammar.plural("test")));
    // Assert.assertEquals("child", grammar.singular(grammar.plural("child")));
    // Assert.assertEquals("cat", grammar.singular(grammar.plural("cat")));

    Assert.assertEquals(true, en.isNoun("cat"));
    Assert.assertEquals(true, en.isNoun("cats"));

    Assert.assertEquals(false, en.isNoun("is"));
    Assert.assertEquals(false, en.isNoun("concerned"));

    Assert.assertEquals(false, en.isPerson("cats"));
    Assert.assertEquals(true, en.isPerson("Peter"));

    Assert.assertEquals(false, en.isPerson(""));
    Assert.assertEquals(false, en.isPerson(" "));
    Assert.assertEquals(false, en.isPerson("."));
    Assert.assertEquals(false, en.isPerson(". "));
  }
}
