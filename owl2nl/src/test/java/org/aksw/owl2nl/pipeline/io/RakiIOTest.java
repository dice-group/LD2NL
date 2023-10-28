package org.aksw.owl2nl.pipeline.io;

import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class RakiIOTest {
  protected static final Logger LOG = LogManager.getLogger(RakiIOTest.class);

  @Test
  public void test() {
    Model model = RakiIO.readRDFXML(//
        IRI.create(RakiIOTest.class.getClassLoader().getResource("test_ontology.owl")//
        ).toURI().getPath());

    Assert.assertNotNull(model);
    Assert.assertFalse(model.isEmpty());
  }
}
