package org.aksw.owl2nl.data;

import java.nio.file.Path;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import simplenlg.lexicon.Lexicon;

/**
 *
 * @author rspeck
 *
 */
public abstract class AInput implements IInput {

  protected Lexicon lexicon = Lexicon.getDefaultLexicon();
  protected OWLOntology owlOntology = null;

  protected OWLOntology loadOntology(final Path path) {
    try {
      return OWLManager//
          .createOWLOntologyManager()//
          .loadOntologyFromOntologyDocument(path.toFile());
      // .loadOntology(IRI.create(path.toFile()));
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  protected OWLOntology loadOntology(final IRI ontology) {
    try {
      return OWLManager//
          .createOWLOntologyManager()//
          .loadOntology(ontology);
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  @Override
  public IInput setOntologyIRI(final IRI ontology) {
    owlOntology = loadOntology(ontology);
    return this;
  }

  @Override
  public IInput setOntologyPath(final Path ontology) {
    owlOntology = loadOntology(ontology);
    return this;
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
    LOG.trace("Not implemented in ".concat(AInput.class.getName()).concat(" ."));
    return null;
  }
}
