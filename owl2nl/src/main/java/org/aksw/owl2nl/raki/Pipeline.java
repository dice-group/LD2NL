package org.aksw.owl2nl.raki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.aksw.owl2nl.raki.data.Input;
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

  /**
   * Test pipeline. Reads input and verbalizes
   *
   * @throws Exception
   *
   * @throws IOException
   */
  protected static void run(final String axiomsFile, final String ontology, final String out)
      throws Exception {

    // reads input
    final Input input = new Input(axiomsFile, ontology);

    // verbalized axioms
    final DocumentPlanner documentPlanner;
    documentPlanner = new DocumentPlanner(input);

    // results
    final String results = documentPlanner.build().results();

    // write verbalized axioms to file success
    final boolean success = writeResults(Paths.get(out), results.getBytes());
    if (!success) {
      throw new Exception("Couldn't write results to file");
    }
  }

  /**
   */
  public static boolean writeResults(final Path path, final byte[] bytes) {
    try {
      Files.write(path, bytes);
      return true;
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return false;
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
      final String input = "Process.owl";

      axiomsFile = baseFolder.concat(input);
      output = baseFolder.concat(input).concat(".txt");
    }
    String ontologyFile =
        "/media/store/Data/private/raki-data/Siemens-Usecase/Ontologies/PPP_Ontologies/Process.owl";

    ontologyFile = axiomsFile;
    try {
      Pipeline.run(axiomsFile, ontologyFile, output);
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
