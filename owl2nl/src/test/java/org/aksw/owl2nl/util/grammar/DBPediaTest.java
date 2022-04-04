package org.aksw.owl2nl.util.grammar;

import org.junit.Assert;
import org.junit.Test;

public class DBPediaTest {

  @Test
  public void test() {
    Assert.assertTrue(DBPedia.isPerson("Person"));
    Assert.assertTrue(DBPedia.isPerson("PerSON"));
    Assert.assertFalse(DBPedia.isPerson("Animal"));
    Assert.assertFalse(DBPedia.isPerson(""));
    Assert.assertFalse(DBPedia.isPerson(" "));
    Assert.assertFalse(DBPedia.isPerson(null));
  }
}
