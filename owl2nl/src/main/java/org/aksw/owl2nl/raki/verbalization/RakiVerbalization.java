package org.aksw.owl2nl.raki.verbalization;

import java.util.ArrayList;
import java.util.List;

import org.aksw.owl2nl.OWLAxiomConverter;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.aksw.owl2nl.raki.data.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

import simplenlg.lexicon.Lexicon;

public class RakiVerbalization extends OWLAxiomConverter implements IRakiVerbalization {

  public RakiVerbalization(final Lexicon lexicon, final Input in) {
    super(lexicon, in);
  }

  protected static final Logger LOG = LogManager.getLogger(RakiVerbalization.class);

  // OWLAxiomConverter converter = new OWLAxiomConverter();

  @Override
  public List<String> verbalize(final List<OWLAxiom> axioms) throws OWLAxiomConversionException {

    final List<String> verbalizations = new ArrayList<>();

    for (final OWLAxiom axiom : axioms) {
      final String verbalization = convert(axiom);
      verbalizations.add(verbalization);
    }

    return verbalizations;
  }

}
