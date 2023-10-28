package org.aksw.owl2nl.pipeline.data.output;

import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import org.aksw.owl2nl.pipeline.io.RakiIO;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Writes to the log and returns true.
 * 
 * @author rspeck
 *
 */
public class OutputTerminal implements IOutput<String> {
  StringBuilder sb = new StringBuilder();
  protected Path file = null;

  public OutputTerminal() {

  }

  public OutputTerminal(final Path file) {
    this.file = file;
  }

  /**
   * Writes to the log and returns true.
   */
  @Override
  public String write(final Map<OWLAxiom, String> verb) {
    sb = new StringBuilder();
    for (final Entry<OWLAxiom, String> entry : verb.entrySet()) {
      sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(System.lineSeparator());
    }
    if (file != null) {
      RakiIO.write(file, sb.toString().getBytes());
    }
    return getResults();
  }

  @Override
  public String getResults() {
    return sb.toString();
  }
}
