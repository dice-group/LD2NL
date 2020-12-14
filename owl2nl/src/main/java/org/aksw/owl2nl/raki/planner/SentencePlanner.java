package org.aksw.owl2nl.raki.planner;

import java.util.HashMap;
import java.util.Map;

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
public class SentencePlanner implements IPlanner<Map<OWLAxiom, String>> {

  protected static final Logger LOG = LogManager.getLogger(SentencePlanner.class);

  protected IRakiVerbalization rakiVerbalization;

  /*
   * Axioms to verbalize.
   */
  private Map<OWLAxiom, String> results = new HashMap<>();
  private Input input = null;

  /**
   *
   * @param input
   */
  public SentencePlanner(final Input input) {
    this.input = input;
    rakiVerbalization = new RakiVerbalization(Lexicon.getDefaultLexicon(), input);

    LOG.debug("axioms size: {}", results.keySet().size());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#build()
   */

  @Override
  public IPlanner<Map<OWLAxiom, String>> build() {

    results = rakiVerbalization.verbalize(input.getAxioms());

    // removes null, for axioms without verbalization
    // results.removeAll(Collections.singletonList(null));

    // sorts results
    // Collections.sort(results);

    // joins results
    // result = String.join(System.lineSeparator(), results);
    // result = results.toString();
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#results()
   */
  @Override
  public Map<OWLAxiom, String> results() {
    return results;
  }
}
