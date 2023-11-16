package org.aksw.owl2nl.pipeline;

import java.nio.file.Paths;
import java.util.Map;
import org.aksw.owl2nl.pipeline.data.input.IRAKIInput.Type;
import org.aksw.owl2nl.pipeline.data.input.RAKIInput;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.aksw.owl2nl.pipeline.data.output.OutputJavaObjects;
import org.aksw.owl2nl.pipeline.data.output.OutputJsonTrainingData;
import org.aksw.owl2nl.pipeline.data.output.OutputTerminal;
import org.aksw.owl2nl.pipeline.ui.RAKICommandLineInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import simplenlg.lexicon.Lexicon;

public class Example {

  protected static final Logger LOG = LogManager.getLogger(RAKICommandLineInterface.class);
  protected static final String ontology = "src/test/resources/test_ontology.owl";
  protected static final String axioms = "src/test/resources/test_axioms.owl";

  /**
   * 
   * @param args
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyStorageException
   */
  public static void main(final String[] args)
      throws OWLOntologyCreationException, OWLOntologyStorageException {
    exampleA();
    exampleB();
    exampleC();
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyStorageException
   * 
   */
  public static void exampleC() throws OWLOntologyCreationException, OWLOntologyStorageException {
    // final IOutput<JSONArray> out = new OutputJsonTrainingData();
    IOutput<String> out = new OutputTerminal();

    RAKIInput in = new RAKIInput();
    in.setType(Type.RULES)//
        .setAxioms(Paths.get(axioms))//
        .setOntology(IRI.create(Paths.get(ontology).toAbsolutePath().toUri()))//
    // .setLexicon(Lexicon.getDefaultLexicon());
    ;

    Pipeline.getInstance()//
        .setInput(in)//
        .setOutput(out)//
        .run();

    LOG.info(out.getResults());
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyStorageException
   *
   */
  public static void exampleA() throws OWLOntologyCreationException, OWLOntologyStorageException {
    final String output = "out.txt";

    final RAKIInput in = new RAKIInput();
    in//
        .setType(Type.RULES)//
        .setAxioms(Paths.get(axioms))//
        .setOntology(IRI.create(Paths.get(ontology).toAbsolutePath().toUri()))//
        .setLexicon(Lexicon.getDefaultLexicon());

    final IOutput<JSONArray> out = new OutputJsonTrainingData(Paths.get(output));
    // final IOutput out = new OutputTerminal();
    Pipeline.getInstance().setInput(in).setOutput(out).run();//
    // .getOutput()//
    // .getResults();

    out.getResults().forEach(LOG::info);
  }

  /**
   * @throws OWLOntologyCreationException
   * @throws OWLOntologyStorageException
   *
   */
  public static void exampleB() throws OWLOntologyCreationException, OWLOntologyStorageException {

    final RAKIInput in = new RAKIInput();
    {
      in.setType(Type.RULES);
      in.setAxioms(Paths.get(axioms));
      in.setOntology(IRI.create(Paths.get(ontology).toAbsolutePath().toUri()));//
    }

    final IOutput<Map<OWLAxiom, String>> out = new OutputJavaObjects();
    Pipeline.getInstance().setInput(in).setOutput(out).run();//
    // .getOutput()//
    // .getResults();
    out.getResults().entrySet().forEach(LOG::info);
  }
}
