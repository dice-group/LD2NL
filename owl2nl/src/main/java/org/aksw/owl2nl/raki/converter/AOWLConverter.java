package org.aksw.owl2nl.raki.converter;

import org.aksw.owl2nl.raki.data.input.Input;
import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

import simplenlg.framework.NLGFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 *
 * @author Rene Speck
 */
abstract class AOWLConverter {

  protected static final Logger LOG = LogManager.getLogger(AOWLConverter.class);

  protected OWLDataFactory df = new OWLDataFactoryImpl();

  protected NLGFactory nlgFactory;
  protected IRIConverter iriConverter;
  protected LiteralConverter literalConverter;
  protected Input input;

  /**
   * Abstract constructor.
   *
   * @param nlgFactory
   * @param input
   */
  public AOWLConverter(final NLGFactory nlgFactory, final Input input) {

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
