package org.aksw.owl2nl.data;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

public class OWL2NLInput extends AInput implements IInput {

  @Override
  public Set<OWLAxiom> getAxioms() {
    if (owlOntology != null) {
      return owlOntology.getAxioms();
    } else {
      LOG.warn("Ontology not set. ");
      return null;
    }
  }
}
