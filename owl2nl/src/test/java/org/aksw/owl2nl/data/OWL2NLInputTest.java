package org.aksw.owl2nl.data;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class InputTest {

  @Test
  public void test() {
    final IInput input = new OWL2NLInput().setOntology(//
        Paths.get(InputTest.class.getClassLoader().getResource("test.owl").getPath())//
    );

    Assert.assertNotNull(input);
    Assert.assertEquals(16, input.getAxioms().size());
  }
}
