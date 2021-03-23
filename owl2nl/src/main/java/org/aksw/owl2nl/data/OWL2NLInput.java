package org.aksw.owl2nl.data;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import simplenlg.lexicon.Lexicon;

public class OWL2NLInput implements IInput {

  protected Lexicon lexicon = Lexicon.getDefaultLexicon();
  protected OWLOntology owlOntology = null;

  /**
   * Uses default Lexicon.
   */
  public OWL2NLInput() {}

  /**
   *
   * @param lexicon
   */
  public OWL2NLInput(final Lexicon lexicon) {
    this.lexicon = lexicon;
  }

  @Override
  public IInput setOntologyIRI(final IRI ontology) {
    try {
      owlOntology = OWLManager.createOWLOntologyManager().loadOntology(ontology);
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return this;
  }

  @Override
  public Set<OWLAxiom> getAxioms() {
    return owlOntology.getAxioms();
  }

  @Override
  public IInput setLexicon(final Lexicon lexicon) {
    this.lexicon = lexicon;
    return this;
  }

  @Override
  public Lexicon getLexicon() {
    return lexicon;
  }

  @Override
  public String getEnglishLabel(final IRI iri) {
    // TODO Auto-generated method stub
    return null;
  }
}
