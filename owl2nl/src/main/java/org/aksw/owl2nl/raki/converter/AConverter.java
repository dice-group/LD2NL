package org.aksw.owl2nl.raki.converter;

import org.aksw.owl2nl.raki.data.Input;
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
abstract class AConverter {

  protected static final Logger LOG = LogManager.getLogger(AConverter.class);

  protected OWLDataFactory df = new OWLDataFactoryImpl();

  protected NLGFactory nlgFactory;
  protected final IRIConverter iriConverter;
  protected LiteralConverter literalConverter;
  protected Input input;

  /**
   * Abstract constructor.
   *
   * @param nlgFactory
   * @param input
   */
  public AConverter(final NLGFactory nlgFactory, final Input input) {
    this.input = input;
    this.nlgFactory = nlgFactory;

    iriConverter = new SimpleIRIConverter();
    literalConverter = new LiteralConverter(iriConverter);
  }

  /**
   * Convert the IRI of the given OWLEntity into natural language. Uses given labels if exist.
   *
   * @param entity
   * @return natural language
   */

  protected String getLexicalForm(final OWLEntity entity) {

    final String label = _getLexicalForm(entity);

    LOG.debug("getLexicalForm from input ontology: {}, label: {}", entity.toStringID(), label);

    return label;
  }

  protected String _getLexicalForm(final OWLEntity entity) {
    if (input != null) {
      LOG.debug("getLexicalForm from input ontology: {}", entity.toStringID());
      final String label = input.getEnglishLabel(entity.getIRI());
      if (label != null) {
        return label;
      } else {
        LOG.debug("Could not find label in ontology");
      }
    }
    LOG.debug("getLexicalForm from iriConverter: {}", entity.toStringID());
    return iriConverter.convert(entity.toStringID());
  }
}
