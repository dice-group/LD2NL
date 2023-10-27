package org.aksw.owl2nl.pipeline;

import java.nio.file.Path;
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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import simplenlg.lexicon.Lexicon;

public class Example {

  protected static final Logger LOG = LogManager.getLogger(RAKICommandLineInterface.class);
  protected static final String file = "father.owl";

  /**
   * 
   * @param args
   * @throws OWLOntologyCreationException
   */
  public static void main(final String[] args) throws OWLOntologyCreationException {
    // exampleA();
    exampleC();
  }

  /**
   * @throws OWLOntologyCreationException
   * 
   */
  public static void exampleC() throws OWLOntologyCreationException {
    // final IRI axioms = IRI.create(Example.class.getClassLoader().getResource("test.owl"));
    final String axioms = file;
    final String ontology = file;
    // final IOutput<JSONArray> out = new OutputJsonTrainingData();
    IOutput<String> out = new OutputTerminal();

    RAKIInput in = new RAKIInput();
    in.setType(Type.RULES)//
        .setAxioms(Paths.get(file))//

        .setOntology(Paths.get(file))//
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
   *
   */
  public static void exampleA() throws OWLOntologyCreationException {
    final String ontology = file;
    final String axioms = file;
    final String output = "out.txt";

    final RAKIInput in = new RAKIInput();
    in//
        .setAxioms(Paths.get(axioms))//
        .setOntology(Paths.get(ontology))//
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
   *
   */
  public static void exampleB() throws OWLOntologyCreationException {

    final RAKIInput in = new RAKIInput();
    {
      final Path axioms = Paths.get(file);
      in.setAxioms(axioms);
      in.setOntology(axioms);
    }

    final IOutput<Map<OWLAxiom, String>> out = new OutputJavaObjects();
    Pipeline.getInstance().setInput(in).setOutput(out).run();//
    // .getOutput()//
    // .getResults();
    out.getResults().entrySet().forEach(LOG::info);
  }
}
