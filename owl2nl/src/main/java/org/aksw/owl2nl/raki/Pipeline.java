package org.aksw.owl2nl.raki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.aksw.owl2nl.raki.data.Input;
import org.aksw.owl2nl.raki.planner.DocumentPlanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;

/*
 * Prototype pipeline.
 * 
 * @author Rene Speck
 *
 */
public class Pipeline {

  protected static final Logger LOG = LogManager.getLogger(Pipeline.class);

  /**
   * Test pipeline. Reads input and verbalizes
   *
   * @throws IOException
   */
  protected static void run(final String axiomsFile, final String out) {

    // reads input
    final Input input = new Input(axiomsFile);

    // verbalized axioms
    final DocumentPlanner documentPlanner;
    documentPlanner = new DocumentPlanner(input);

    // results
    final String results = documentPlanner.build().results();

    // write verbalized axioms to file
    writeResults(out, results);
  }

  protected String rederAxioms(final OWLAxiom axiom) {
    final OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImplExt();
    return renderer.render(axiom);
  }

  public static void writeResults(final String out, final String restuls) {

    try {
      Files.write(Paths.get(out), restuls.getBytes());
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * Test run of the prototype pipeline
   *
   */
  public static void main(final String[] args) {

    final String output;
    final String axiomsFile;
    {
      final String baseFolder = "privateData/";
      final String input = "smallTest.owl";

      axiomsFile = baseFolder.concat(input);
      output = baseFolder.concat(input).concat(".txt");
    }

    try {
      Pipeline.run(axiomsFile, output);
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
