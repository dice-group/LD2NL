package org.aksw.owl2nl.converter.visitors;

import org.aksw.owl2nl.data.IInput;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;

/**
 * @author Lorenz Buehmann
 * @author Rene Speck
 *
 */
public class OWLIndividualToNLGElement extends AToNLGElement
    implements OWLIndividualVisitorEx<NLGElement> {

  /**
   *
   * @param nlgFactory
   * @param iriConverter
   */
  public OWLIndividualToNLGElement(final NLGFactory nlgFactory, final IInput in) {
    super(nlgFactory, in);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLIndividualVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLNamedIndividual)
   */
  @Override
  public NLGElement visit(final OWLNamedIndividual individual) {
    return nlgFactory.createNounPhrase(getLexicalForm(individual));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLIndividualVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLAnonymousIndividual)
   */
  @Override
  public NLGElement visit(final OWLAnonymousIndividual individual) {
    throw new UnsupportedOperationException(
        "Convertion of anonymous individuals not supported yet!");
  }
}
