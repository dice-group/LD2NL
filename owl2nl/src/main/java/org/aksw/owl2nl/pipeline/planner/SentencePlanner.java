package org.aksw.owl2nl.pipeline.planner;

import java.util.HashMap;
import java.util.Map;

import org.aksw.owl2nl.data.IInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aksw.owl2nl.pipeline.verbalization.IRakiVerbalization;
import org.aksw.owl2nl.pipeline.verbalization.RakiVerbalization;
import org.semanticweb.owlapi.model.OWLAxiom;

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
  private IInput input = null;

  /**
   *
   * @param input
   */
  public SentencePlanner(final IInput input) {
    this.input = input;
    rakiVerbalization = new RakiVerbalization(input);
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

  public Map<OWLAxiom, String> results() {
    return results;
  }
}
