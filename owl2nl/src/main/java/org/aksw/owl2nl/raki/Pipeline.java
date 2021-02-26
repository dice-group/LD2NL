package org.aksw.owl2nl.raki;

import java.nio.file.Paths;

import org.aksw.owl2nl.raki.data.input.Input;
import org.aksw.owl2nl.raki.data.output.IOutput;
import org.aksw.owl2nl.raki.data.output.OutputJsonTrainingData;
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
  public Pipeline run(final String axiomsFile) {
    if (output == null) {
      throw new UnsupportedOperationException("Output not set.");
    }

    return _run(axiomsFile);
  }

  private Pipeline _run(final String axiomsFile) {

    // reads input
    final Input input = new Input(axiomsFile);

    // verbalized axioms
    documentPlanner = new DocumentPlanner(input, output);
    documentPlanner.build();
    documentPlanner.results();
    return this;
  }

  // settings
  public Pipeline setOutput(final IOutput output) {
    this.output = output;
    return this;
  }

  public IOutput getOutput() {
    return output;
  }

  /**
   * Test run of the prototype pipeline
   *
   */
  public static void main(final String[] args) {
    LOG.info("\n==============================\nRunning Pipeline ...");

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
    LOG.info("\n==============================\nPipeline exit.");
  }
}
