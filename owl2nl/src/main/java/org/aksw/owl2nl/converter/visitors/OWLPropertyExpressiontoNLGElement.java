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
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;

public class OWLPropertyExpressiontoNLGElement extends AToNLGElement
    implements OWLPropertyExpressionVisitorEx<NLGElement> {
  /**
   * OWLPropertyExpressiontoNLGElement constructor.
   *
   * @param nlgFactory
   * @param input
   */
  public OWLPropertyExpressiontoNLGElement(final NLGFactory nlgFactory, final IInput input) {
    super(nlgFactory, input);
  }

  /**
   * Operation of the OWLAnnotationProperty is not supported yet!
   */
  @Override
  public NLGElement visit(final OWLAnnotationProperty ap) {
    throw new UnsupportedOperationException(
        "Operation of the OWLAnnotationProperty is not supported yet!");
  }

  /**
   * Gets the inverse of the given OWLObjectInverseOf object and calls its to accept this visitor.
   */
  @Override
  public NLGElement visit(final OWLObjectInverseOf oio) {
    LOG.debug("{} called.", oio.getClass().getSimpleName());
    return oio.getInverse().accept(this);
  }

  @Override
  public NLGElement visit(final OWLObjectProperty pe) {
    LOG.debug("{} called.", pe.getClass().getSimpleName());
    return visitOWLProperty(pe);
  }

  @Override
  public NLGElement visit(final OWLDataProperty pe) {
    LOG.debug("{} called.", pe.getClass().getSimpleName());
    return visitOWLProperty(pe);
  }

  protected NLGElement visitOWLProperty(final OWLProperty pe) {

    final PropertyVerbalization verbal = propertyVerbalizer(pe);
    final String verbalizationText = verbal.getVerbalizationText();

    NLGElement phrase = null;
    if (verbal.isNounType()) {
      phrase = nlgFactory.createNounPhrase(getLexicalForm(pe));
    } else if (verbal.isVerbType()) {
      phrase = nlgFactory.createVerbPhrase(verbalizationText);
      phrase.setFeature(Feature.FORM, Form.PRESENT_PARTICIPLE);
    } else {
      LOG.warn("verbal type not found!");
    }
    LOG.debug("Verbal:\"{}\" type:{}, tense:{}", //
        verbalizationText, verbal.getVerbalizationType().name(), verbal.getTense().name()//
    );
    return phrase;
  }
}
