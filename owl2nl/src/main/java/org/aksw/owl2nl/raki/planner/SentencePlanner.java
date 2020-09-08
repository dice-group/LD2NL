package org.aksw.owl2nl.raki.planner;

import java.util.Collections;
import java.util.List;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.aksw.owl2nl.raki.verbalization.IRakiVerbalization;
import org.aksw.owl2nl.raki.verbalization.RakiVerbalization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * This class is responsible for the sentence generation. It chooses, for instance, words and
 * phrases to express information. It decides whenever a Pronoun or the name of the object will be
 * used.
 * 
 * @author Rene Speck
 */
public class SentencePlanner implements IPlanner<String> {

  protected static final Logger LOG = LogManager.getLogger(SentencePlanner.class);

  protected IRakiVerbalization rakiVerbalization = new RakiVerbalization();

  /*
   * Axioms to verbalize.
   */
  private List<OWLAxiom> axioms = null;

  /*
   * Ontology.
   */
  @Deprecated
  private OWLOntology ontology = null;

  private String result = null;

  /**
   *
   * @param rakiVerbalization
   */
  public SentencePlanner(final List<OWLAxiom> axioms, final OWLOntology ontology) {
    this.axioms = axioms;
    this.ontology = ontology;

    LOG.debug("axioms size: {}", this.axioms.size());
    LOG.debug("ontology size: {}", this.ontology.getAxiomCount());
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

      // remove null, for axioms without verbalization
      results.removeAll(Collections.singletonList(null));

      // sort results
      Collections.sort(results);

      // join results
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
