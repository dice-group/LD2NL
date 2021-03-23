package org.aksw.owl2nl.data;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import simplenlg.lexicon.Lexicon;

public interface IInput {

  Logger LOG = LogManager.getLogger(IInput.class);

  IInput setLexicon(Lexicon lexicon);

  Lexicon getLexicon();

  IInput setOntologyIRI(IRI ontology);

  Set<OWLAxiom> getAxioms();

  // TODO
  String getEnglishLabel(final IRI iri);
}
