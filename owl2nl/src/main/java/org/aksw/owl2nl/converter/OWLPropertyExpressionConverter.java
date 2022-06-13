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
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.realiser.english.Realiser;

public class OWLPropertyExpressionConverter {

  protected OWLPropertyExpressionVisitorEx<NLGElement> propertyVisitor;

  private final Realiser realiser;

  public OWLPropertyExpressionConverter(final IInput in) {
    realiser = new Realiser(in.getLexicon());
    propertyVisitor = new OWLPropertyExpressiontoNLGElement(//
        new NLGFactory(in.getLexicon()), in//
    );
  }

  public String convert(final OWLPropertyExpression pe) {
    return realiser.realise(asNLGElement(pe)).getRealisation();
  }

  public NLGElement asNLGElement(final OWLPropertyExpression pe) {
    return asNLGElement(pe, false);
  }

  public NLGElement asNLGElement(final OWLPropertyExpression pe, final boolean isTransitive) {
    return pe.accept(propertyVisitor);
  }
}
