/*-
 * #%L
 * OWL2NL
 * %%
 * Copyright (C) 2015 - 2021 Data and Web Science Research Group (DICE)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.aksw.owl2nl.converter.visitors;

import org.aksw.owl2nl.data.IInput;
import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import simplenlg.framework.NLGFactory;

/**
 *
 * @author Rene Speck
 */
abstract class AToNLGElement {

  protected static final Logger LOG = LogManager.getLogger(AToNLGElement.class);

  protected NLGFactory nlgFactory;

  private final IRIConverter iriConverter;
  private final PropertyVerbalizer propertyVerbalizer;
  private final LiteralConverter literalConverter;
  private final IInput input;

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
    propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);
  }

  /**
   * Calls {@link #getLexicalFormFromOntology(OWLPropertyExpression)} to set a new label for a new
   * {@link org.aksw.triple2nl.property.PropertyVerbalization } object or uses a fall back to create
   * a label.
   *
   * @param OWLPropertyExpression
   * @return PropertyVerbalization
   */
  public PropertyVerbalization propertyVerbalizer(final OWLPropertyExpression property) {

    final String l = getLexicalFormFromOntology(property);
    LOG.debug(l);
    IRI iri = null;
    if (property.isDataPropertyExpression()) {
      iri = ((OWLDataPropertyExpression) property)//
          .asOWLDataProperty().getIRI();
    } else if (property.isObjectPropertyExpression()) {
      iri = ((OWLObjectPropertyExpression) property)//
          .asOWLObjectProperty().getIRI();
    }
    final PropertyVerbalization p = propertyVerbalizer(iri);
    if (l != null && !l.trim().isEmpty()) {
      p.setExpandedVerbalizationText(l);
      // TODO: update the rest after setting a new text?
      // p.getTense()
      // p.getPOSTags()
      LOG.debug("new: {}", p.getVerbalizationText());
    }
    LOG.debug(p.getExpandedVerbalizationText());
    return p;
  }

  /**
   *
   * @param propertyVerbalization
   * @return
   */
  public String propertyVerbalizerText(final PropertyVerbalization propertyVerbalization) {
    return propertyVerbalization.getExpandedVerbalizationText();
  }

  /**
   * Gets the lexical value of this literal by using the
   * {@link org.aksw.triple2nl.converter.LiteralConverter#convert(OWLLiteral)} or the
   * {@link org.semanticweb.owlapi.model.OWLLiteral#getLiteral()} method.
   *
   * @param literal
   * @return
   */
  protected String literalConverter(final OWLLiteral literal) {
    String literalString = literalConverter.convert(literal);

    if (literalString == null || literalString.trim().isEmpty()) {
      literalString = literal.getLiteral();
      LOG.debug("No literal with the LiteralConverter. Using OWLLiteral#getLiteral(): {}, {}",
          literalString, literal.toString());
    }
    return literalString;
  }

  /**
   *
   * Calls { @link {@link #getLexicalFormFromOntology(OWLEntity)} to get a label otherwise converts
   * the IRI of the given OWLEntity into natural language.
   *
   * @param entity
   * @return natural language or null
   */
  protected String getLexicalForm(final OWLEntity entity) {

    String label = getLexicalFormFromOntology(entity);
    if (label == null) {
      LOG.trace("Could not find label in ontology: {}", entity.toStringID());
      label = getLexicalFormFromIRIConverter(entity);
    }
    if (label == null) {
      LOG.trace("Could not find label with the IRI converter: {}", entity.toStringID());
    }
    return label;
  }

  // private helpers
  /**
   *
   * @param IRI
   * @return PropertyVerbalization object
   */
  private PropertyVerbalization propertyVerbalizer(final IRI iri) {
    return propertyVerbalizer.verbalize(iri.toString());
  }

  /**
   * Calls the {@link org.aksw.triple2nl.converter.IRIConverter#convert(String) }.
   *
   * @param entity
   * @return a natural language representation
   */
  private String getLexicalFormFromIRIConverter(final OWLEntity entity) {
    return iriConverter.convert(entity.toStringID());
  }

  /**
   * Overloaded method calls {@link #getLexicalFormFromOntology(IRI)}.
   *
   * @param entity
   * @return label or null
   */
  private String getLexicalFormFromOntology(final OWLEntity entity) {
    return getLexicalFormFromOntology(entity.getIRI());
  }

  /**
   * Gets the label from the given ontology by calling
   * {@link org.aksw.owl2nl.data.IInput#getEnglishLabel(IRI)}.
   *
   * @param iri
   * @return label or null
   */
  private String getLexicalFormFromOntology(final IRI iri) {
    return input != null ? input.getEnglishLabel(iri) : null;
  }

  private String getLexicalFormFromOntology(final OWLPropertyExpression property) {
    IRI iri = null;
    if (property.isDataPropertyExpression()) {
      iri = ((OWLDataPropertyExpression) property)//
          .asOWLDataProperty().getIRI();
    } else if (property.isObjectPropertyExpression()) {
      iri = ((OWLObjectPropertyExpression) property)//
          .asOWLObjectProperty().getIRI();
    }
    String l = null;
    if (iri != null) {
      l = getLexicalFormFromOntology(iri);
    }
    return l;
  }
}
