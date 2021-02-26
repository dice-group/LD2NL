package org.aksw.owl2nl.raki.data.output;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 *
 * @author Rene Speck
 *
 */
public class OutputJavaObjects extends AOutput {

  protected Map<OWLAxiom, SimpleEntry<String, String>> data = null;

  /**
   * returns the given input.
   */
  @Override
  public Object write(final Map<OWLAxiom, SimpleEntry<String, String>> verb) {
    data = verb;
    return verb;
  }

  public Map<OWLAxiom, SimpleEntry<String, String>> getData() {
    return data;
  }
}
