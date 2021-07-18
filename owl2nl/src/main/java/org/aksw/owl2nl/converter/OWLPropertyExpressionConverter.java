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
package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.converter.visitors.OWLPropertyExpressiontoNLGElement;
import org.aksw.owl2nl.data.IInput;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.realiser.english.Realiser;

/**
 * Converts property expression.
 */
public class OWLPropertyExpressionConverter implements //
    OWLPropertyExpressionVisitorEx<NLGElement> {

  protected OWLPropertyExpressionVisitorEx<NLGElement> converterOWLPropertyExpression;

  private final Realiser realiser;

  /**
   * Converts property expression.
   */
  public OWLPropertyExpressionConverter(final IInput in) {
    realiser = new Realiser(in.getLexicon());
    converterOWLPropertyExpression = new OWLPropertyExpressiontoNLGElement(//
        new NLGFactory(in.getLexicon()), in//
    );

  }

  public String convert(final OWLPropertyExpression pe) {
    return realiser.realise(asNLGElement(pe)).getRealisation();
  }

  public NLGElement asNLGElement(final OWLPropertyExpression pe) {
    return asNLGElement(pe, false);
  }

  public NLGElement asNLGElement(final OWLPropertyExpression pe,
      final boolean isTransitiveObjectProperty) {

    resetsOWLPropertyExpressionParameter(pe, isTransitiveObjectProperty);

    return pe.accept(this);
  }

  private void resetsOWLPropertyExpressionParameter(final OWLPropertyExpression pe,
      final boolean isTransitive) {

    final OWLPropertyExpressiontoNLGElement.Parameter parameter =
        ((OWLPropertyExpressiontoNLGElement) converterOWLPropertyExpression).new Parameter();
    parameter.root = pe;
    parameter.isTransitiveObjectProperty = isTransitive;
    parameter.countTransitive = 0;
    ((OWLPropertyExpressiontoNLGElement) converterOWLPropertyExpression).setParameter(parameter);
  }

  @Override
  public NLGElement visit(final OWLObjectProperty property) {
    return converterOWLPropertyExpression.visit(property);
  }

  @Override
  public NLGElement visit(final OWLObjectInverseOf property) {
    return converterOWLPropertyExpression.visit(property);
  }

  @Override
  public NLGElement visit(final OWLDataProperty property) {
    return converterOWLPropertyExpression.visit(property);
  }

  @Override
  public NLGElement visit(final OWLAnnotationProperty property) {
    return converterOWLPropertyExpression.visit(property);
  }
}
