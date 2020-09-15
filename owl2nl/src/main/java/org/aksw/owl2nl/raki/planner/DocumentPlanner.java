package org.aksw.owl2nl.raki.planner;

import org.aksw.owl2nl.raki.data.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class defines the template of a document and provides all information for the
 * SentencePlanner {@link sentencePlanner}. This class decides about the information and the order
 * of the information to be verbalized.
 *
 * @author Rene Speck
 *
 */
public class DocumentPlanner implements IPlanner<String> {

  protected static final Logger LOG = LogManager.getLogger(DocumentPlanner.class);

  protected SentencePlanner sentencePlanner;

  /**
   *
   */
  public DocumentPlanner(final Input input) {
    // axioms to verbalize
    // final List<OWLAxiom> axioms = input.axioms;
    // final OWLOntology ontology = input.ontology;

    sentencePlanner = new SentencePlanner(input);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#build()
   */
  @Override
  public IPlanner<String> build() {
    sentencePlanner.build();
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#results()
   */
  @Override
  public String results() {
    return sentencePlanner.results();
  }
}
