package org.aksw.owl2nl.pipeline.ui;

import java.nio.file.Paths;
import java.util.Map;
import org.aksw.owl2nl.pipeline.Pipeline;
import org.aksw.owl2nl.pipeline.data.input.IRAKIInput.Type;
import org.aksw.owl2nl.pipeline.data.input.RAKIInput;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.aksw.owl2nl.pipeline.data.output.OutputHTMLTable;
import org.aksw.owl2nl.pipeline.data.output.OutputJavaObjects;
import org.aksw.owl2nl.pipeline.data.output.OutputJsonTrainingData;
import org.aksw.owl2nl.pipeline.data.output.OutputTerminal;
import org.aksw.owl2nl.pipeline.io.RakiIOTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import simplenlg.lexicon.Lexicon;

public class RAKICommandLineInterfaceTest {
  protected static final Logger LOG = LogManager.getLogger(RakiIOTest.class);

  final String axioms = this.getClass().getClassLoader()//
      .getResource("test_axioms.owl").getPath();
  final String ontology = this.getClass().getClassLoader()//
      .getResource("test_ontology.owl").getPath();

  /**
   * Prepares input with ontology and new labels
   * 
   * @return an instance of RAKIInput
   * @throws OWLOntologyCreationException
   */
  protected RAKIInput getRAKIInput() throws OWLOntologyCreationException {
    RAKIInput in = new RAKIInput();
    in.setType(Type.RULES)//
        .setAxioms(Paths.get(axioms))//
        .setOntology(Paths.get(ontology))//
        .setLexicon(Lexicon.getDefaultLexicon());
    return in;
  }

  /**
   * Runs the pipeline with the given input and output.
   * 
   * @param an instance of IOutput
   * @throws OWLOntologyCreationException
   */
  protected void run(IOutput<?> out) throws OWLOntologyCreationException {
    Pipeline.getInstance()//
        .setInput(getRAKIInput())//
        .setOutput(out)//
        .run();
  }

  /**
   * Test Terminal/String output with labels from the ontology
   * 
   * @throws OWLOntologyCreationException
   */
  @Test
  public void testOutputTerminal() throws OWLOntologyCreationException {

    IOutput<String> out = new OutputTerminal();
    run(out);

    String s = out.getResults();
    LOG.info(s);

    Assert.assertNotNull(s);
    Assert.assertFalse(s.isEmpty());
    Assert.assertTrue(s.contains("Every man is a human."));
    Assert.assertTrue(s.contains("The range of the has children object property is a human."));
    Assert.assertTrue(s.contains("The domain of the has children object property is a human."));
  }

  /**
   * Test Json output with labels from the ontology
   * 
   * @throws OWLOntologyCreationException
   */
  @Test
  public void testOutputJsonTrainingData() throws OWLOntologyCreationException {

    IOutput<JSONArray> out = new OutputJsonTrainingData();
    run(out);

    JSONArray ja = out.getResults();
    LOG.info(ja.toString(2));


    Assert.assertNotNull(ja);
    Assert.assertFalse(ja.isEmpty());
    Assert.assertTrue(ja.length() == 3);
    String s = ja.toString();
    Assert.assertTrue(s.contains("Every man is a human."));
    Assert.assertTrue(s.contains("The range of the has children object property is a human."));
    Assert.assertTrue(s.contains("The domain of the has children object property is a human."));
  }

  /**
   * Test Java Map output with labels from the ontology
   * 
   * @throws OWLOntologyCreationException
   */
  @Test
  public void testOutputJavaObjects() throws OWLOntologyCreationException {

    IOutput<Map<OWLAxiom, String>> out = new OutputJavaObjects();
    run(out);

    Map<OWLAxiom, String> map = out.getResults();
    map.forEach((k, v) -> {
      LOG.info(k.toString() + ": " + v);
    });

    Assert.assertNotNull(map);
    Assert.assertFalse(map.isEmpty());
    Assert.assertTrue(map.size() == 3);
    String s = map.toString();
    Assert.assertTrue(s.contains("Every man is a human."));
    Assert.assertTrue(s.contains("The range of the has children object property is a human."));
    Assert.assertTrue(s.contains("The domain of the has children object property is a human."));
  }

  /**
   * Test Terminal/String output with labels from the ontology
   * 
   * @throws OWLOntologyCreationException
   */
  @Test
  public void testOutputHTMLTable() throws OWLOntologyCreationException {

    IOutput<String> out = new OutputHTMLTable();
    run(out);

    String s = out.getResults();
    LOG.info(s);

    Assert.assertNotNull(s);
    Assert.assertFalse(s.isEmpty());
    Assert.assertTrue(s.contains("Every man is a human."));
    Assert.assertTrue(s.contains("The range of the has children object property is a human."));
    Assert.assertTrue(s.contains("The domain of the has children object property is a human."));
  }
}
