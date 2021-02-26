package org.aksw.owl2nl.raki.verbalization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.owl2nl.OWLAxiomConverter;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.aksw.owl2nl.raki.data.input.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

import simplenlg.lexicon.Lexicon;

/**
 *
 * @author Rene Speck
 *
 */
public class RakiVerbalization extends OWLAxiomConverter implements IRakiVerbalization {

  protected static final Logger LOG = LogManager.getLogger(RakiVerbalization.class);

  /**
   *
   * @param lexicon
   * @param in
   */
  public RakiVerbalization(final Lexicon lexicon, final Input in) {
    super(lexicon, in);
  }

  @Override
  public Map<OWLAxiom, String> verbalize(final Set<OWLAxiom> axioms) {

    final Map<OWLAxiom, String> verbalizations = new HashMap<>();

    for (final OWLAxiom axiom : axioms) {
      try {
        final String verbalization = convert(axiom);
        if (verbalization != null) {
          verbalizations.put(axiom, verbalization);
        } else {
          // LOG.warn("Could not verbalize axiom: " + axiom);
        }
      } catch (final OWLAxiomConversionException e) {
        verbalizations.put(axiom, "");
        LOG.error("Could not verbalize axiom: " + axiom);
      }
    }
    return verbalizations;
  }
}
