package org.aksw.owl2nl.raki.converter;

import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.framework.NLGFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 *
 * @author Rene Speck
 */
abstract class AConverter {

  protected static final Logger logger = LoggerFactory.getLogger(AConverter.class);

  protected OWLDataFactory df = new OWLDataFactoryImpl();
  protected LiteralConverter literalConverter;
  protected NLGFactory nlgFactory;
  protected IRIConverter iriConverter;

  /**
   * Abstract constructor.
   *
   * @param nlgFactory
   * @param iriConverter
   */
  public AConverter(final NLGFactory nlgFactory, final IRIConverter iriConverter) {
    this.nlgFactory = nlgFactory;
    this.iriConverter = iriConverter;

    literalConverter = new LiteralConverter(iriConverter);
  }

  /**
   * Convert the IRI of the given OWLEntity into natural language.
   *
   * @param entity
   * @return natural language
   */
  protected String getLexicalForm(final OWLEntity entity) {
    return iriConverter.convert(entity.toStringID());
  }
}
