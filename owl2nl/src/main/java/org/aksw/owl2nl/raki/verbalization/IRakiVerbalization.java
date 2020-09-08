package org.aksw.owl2nl.raki.verbalization;

import java.util.List;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Verbalizing interface.
 */
public interface IRakiVerbalization {

  /**
   * Verbalizes given axioms if possible else adds null for the specific axiom to the result list.
   *
   * @param axioms
   * @return result list with verbalized axioms
   */
  List<String> verbalize(final List<OWLAxiom> axioms) throws OWLAxiomConversionException;
}
