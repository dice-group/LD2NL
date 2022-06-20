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

  public OWLIndividualToNLGElement(final NLGFactory nlgFactory, final IInput in) {
    super(nlgFactory, in);
  }

  @Override
  public NLGElement visit(final OWLNamedIndividual individual) {
    LOG.debug("{} called.", individual.getClass().getSimpleName());
    return nlgFactory.createNounPhrase(getLexicalForm(individual));
  }

  @Override
  public NLGElement visit(final OWLAnonymousIndividual individual) {
    LOG.debug("{} called.", individual.getClass().getSimpleName());
    throw new UnsupportedOperationException(
        "Convertion of anonymous individuals not supported yet!");
  }
}
