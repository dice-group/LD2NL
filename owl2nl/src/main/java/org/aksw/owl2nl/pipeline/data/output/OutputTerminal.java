package org.aksw.owl2nl.pipeline.data.output;

import java.util.Map;
import java.util.Map.Entry;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Writes to the log and returns true.
 * 
 * @author rspeck
 *
 */
public class OutputTerminal implements IOutput<String> {
  StringBuilder sb = new StringBuilder();

  /**
   * Writes to the log and returns true.
   */
  @Override
  public String write(final Map<OWLAxiom, String> verb) {
    sb = new StringBuilder();
    for (final Entry<OWLAxiom, String> entry : verb.entrySet()) {
      sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(System.lineSeparator());
    }
    return sb.toString();
  }

  @Override
  public String getResults() {
    return sb.toString();
  }
}
