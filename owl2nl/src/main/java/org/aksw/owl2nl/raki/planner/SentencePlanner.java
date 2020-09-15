package org.aksw.owl2nl.raki.planner;

import java.util.List;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.aksw.owl2nl.raki.data.Input;
import org.aksw.owl2nl.raki.verbalization.IRakiVerbalization;
import org.aksw.owl2nl.raki.verbalization.RakiVerbalization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

import simplenlg.lexicon.Lexicon;

/**
 * This class is responsible for the sentence generation. It chooses, for instance, words and
 * phrases to express information. It decides whenever a Pronoun or the name of the object will be
 * used.
 *
 * @author Rene Speck
 */
public class SentencePlanner implements IPlanner<String> {

  protected static final Logger LOG = LogManager.getLogger(SentencePlanner.class);

  protected IRakiVerbalization rakiVerbalization;

  /*
   * Axioms to verbalize.
   */
  private List<OWLAxiom> axioms = null;

  private String result = null;

  /**
   *
   * @param input
   */
  public SentencePlanner(final Input input) {
    axioms = input.getAxioms();
    rakiVerbalization = new RakiVerbalization(Lexicon.getDefaultLexicon(), input);
    LOG.debug("axioms size: {}", axioms.size());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#build()
   */

  @Override
  public IPlanner<String> build() {

    List<String> results;
    try {
      results = rakiVerbalization.verbalize(axioms);

      // removes null, for axioms without verbalization
      // results.removeAll(Collections.singletonList(null));

      // sorts results
      // Collections.sort(results);

      // joins results
      result = String.join(System.lineSeparator(), results);

    } catch (final OWLAxiomConversionException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }

    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#results()
   */
  @Override
  public String results() {
    return result;
  }
}
