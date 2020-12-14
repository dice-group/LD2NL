package org.aksw.owl2nl.raki;

import java.nio.file.Paths;

import org.aksw.owl2nl.raki.data.IOutput;
import org.aksw.owl2nl.raki.data.Input;
import org.aksw.owl2nl.raki.data.OutputJsonTrainingData;
import org.aksw.owl2nl.raki.planner.DocumentPlanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Prototype pipeline.
 *
 * @author Rene Speck
 *
 */
public class Pipeline {

  protected static final Logger LOG = LogManager.getLogger(Pipeline.class);

  protected DocumentPlanner documentPlanner = null;
  protected IOutput output = null;

  private static Pipeline instance;

  private Pipeline() {}

  public static synchronized Pipeline getInstance() {
    if (instance == null) {
      instance = new Pipeline();
    }
    return instance;
  }

  /**
   * Test pipeline. Reads input and verbalizes
   *
   */
  public void run(final String axiomsFile) {
    if (output == null) {
      throw new UnsupportedOperationException("Output not set.");
    }

    _run(axiomsFile);
  }

  private void _run(final String axiomsFile) {

    // reads input
    final Input input = new Input(axiomsFile);

    // verbalized axioms
    documentPlanner = new DocumentPlanner(input, output);
    documentPlanner.build();
    documentPlanner.results();
  }

  // settings
  public Pipeline setOutput(final IOutput output) {
    this.output = output;
    return this;
  }

  /**
   * Test run of the prototype pipeline
   *
   */
  public static void main(final String[] args) {

    final int parameterSize = 2;

    if (args.length == parameterSize) {
      final String axioms = args[0];
      final String output = args[1];

      try {
        Pipeline//
            .getInstance()//
            .setOutput(new OutputJsonTrainingData(Paths.get(output)))//
            // .setOutput(new OutputTerminal())//
            .run(axioms);
      } catch (final Exception e) {
        LOG.error(e.getLocalizedMessage(), e);
      }

    } else {
      LOG.error("Wrong amount of parameters({}/{}).", args.length, parameterSize);
    }
  }
}
