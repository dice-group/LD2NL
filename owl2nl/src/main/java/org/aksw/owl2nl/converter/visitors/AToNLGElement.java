package org.aksw.owl2nl.converter.visitors;

import org.aksw.owl2nl.data.IInput;
import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;

import simplenlg.framework.NLGFactory;

/**
 *
 * @author Rene Speck
 */
abstract class AToNLGElement {

  protected static final Logger LOG = LogManager.getLogger(AToNLGElement.class);

  protected NLGFactory nlgFactory;
  protected IRIConverter iriConverter;
  protected LiteralConverter literalConverter;
  protected IInput input;

  /**
   * Abstract constructor.
   *
   * @param nlgFactory
   * @param input
   */
  public AToNLGElement(final NLGFactory nlgFactory, final IInput input) {

    this.input = input;
    this.nlgFactory = nlgFactory;

    iriConverter = new SimpleIRIConverter();
    literalConverter = new LiteralConverter(iriConverter);
  }

  /**
   * Convert the IRI of the given OWLEntity into natural language. Uses given labels if exist.
   *
   * @param entity
   * @return natural language or null
   */

  protected String getLexicalForm(final OWLEntity entity) {

    String label = null;

    label = getLexicalFormFromOntology(entity);
    if (label == null) {
      LOG.trace("Could not find label in ontology: {}", entity.toStringID());
      label = getLexicalFormFromIRIConverter(entity);
      if (label == null) {
        LOG.trace("Could not find label with the IRI converter: {}", entity.toStringID());
      }
    }
    return label;
  }

  private String getLexicalFormFromIRIConverter(final OWLEntity entity) {
    return iriConverter.convert(entity.toStringID());
  }

  private String getLexicalFormFromOntology(final OWLEntity entity) {
    if (input != null) {
      return input.getEnglishLabel(entity.getIRI());
    } else {
      return null;
    }
  }
}
