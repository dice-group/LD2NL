package org.aksw.owl2nl.raki.data;

import java.nio.file.Paths;

import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.junit.Assert;
import org.junit.Test;

public class InputTest {

  @Test
  public void test() {
    InputTest.class.getClassLoader().getResource("test.owl").getPath().toString();

    final IInput input = new OWL2NLInput().setOntology(//
        Paths.get(InputTest.class.getClassLoader().getResource("test.owl").getPath())//
    );

    Assert.assertNotNull(input);
    Assert.assertEquals(16, input.getAxioms().size());
  }
}
