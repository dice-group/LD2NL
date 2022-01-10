package org.aksw.owl2nl.util.grammar;

import org.junit.Assert;
import org.junit.Test;

public class GrammarTest {

  @Test
  public void test() {
    Assert.assertEquals("tests", Grammar.en.plural("test"));
    Assert.assertEquals("children", Grammar.en.plural("child"));
    Assert.assertEquals("cats", Grammar.en.plural("cat"));

    // Assert.assertEquals("test", Grammar.en.singular(Grammar.en.plural("test")));
    // Assert.assertEquals("child", Grammar.en.singular(Grammar.en.plural("child")));
    // Assert.assertEquals("cat", Grammar.en.singular(Grammar.en.plural("cat")));

    Assert.assertEquals(true, Grammar.en.isNoun("cat"));
    Assert.assertEquals(true, Grammar.en.isNoun("cats"));
  }
}
