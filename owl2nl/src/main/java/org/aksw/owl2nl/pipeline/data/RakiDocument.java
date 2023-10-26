package org.aksw.owl2nl.pipeline.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aksw.owl2nl.pipeline.data.output.IOutput;

/**
 * Creates a documents handed out to domain experts to give feedback. The document might contain
 * answers. it uses the RDF stored in {@link output}.
 *
 * @author Rene Speck
 */
public class RakiDocument {

  protected static final Logger LOG = LogManager.getLogger(RakiDocument.class);

  protected IOutput output;

  /**
   * Initializes parameters.
   *
   * @param renderer
   * @param output
   */
  public RakiDocument(final IOutput output) {
    this.output = output;
  }

  /**
   * Writes given results to outputFilePaths file.
   *
   * @param outputFilePaths
   * @param restuls
   */
  public void writeResults(final String outputFilePaths, final String restuls) {
    try {
      Files.write(Paths.get(outputFilePaths), restuls.getBytes());
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
