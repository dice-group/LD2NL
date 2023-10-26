package org.aksw.owl2nl.pipeline.verbalization;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Verbalizing interface.
 *
 * @author Rene Speck
 */
public interface IRakiVerbalization {

  /**
   * Verbalizes given axioms if possible else adds null for the specific axiom to the result list.
   *
   * @param axioms
   * @return result map with DL and verbalized axioms
   */
  Map<OWLAxiom, String> verbalize(final Set<OWLAxiom> axioms);
}
