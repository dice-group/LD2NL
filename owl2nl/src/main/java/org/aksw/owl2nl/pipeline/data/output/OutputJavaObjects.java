package org.aksw.owl2nl.pipeline.data.output;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Simple wrapper returns the input.
 * 
 * @author rspeck
 *
 */
public class OutputJavaObjects implements IOutput<Map<OWLAxiom, String>> {

  protected Map<OWLAxiom, String> verbalizationResults = null;

  public Map<OWLAxiom, String> getResults() {
    return verbalizationResults;
  }

  @Override
  public Map<OWLAxiom, String> write(final Map<OWLAxiom, String> verbalizationResults) {
    this.verbalizationResults = verbalizationResults;
    return verbalizationResults;
  }
}
