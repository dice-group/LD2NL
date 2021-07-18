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
