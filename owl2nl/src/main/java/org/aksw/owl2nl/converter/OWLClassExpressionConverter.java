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

import java.util.Set;

import org.aksw.owl2nl.converter.visitors.OWLClassExpressionToNLGElement;
import org.aksw.owl2nl.converter.visitors.OWLDataRangeToNLGElement;
import org.aksw.owl2nl.converter.visitors.OWLIndividualToNLGElement;
import org.aksw.owl2nl.data.IInput;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import com.google.common.collect.Sets;

import simplenlg.framework.NLGElement;

/**
 * @author Lorenz Buehmann
 * @author Rene Speck
 */
public class OWLClassExpressionConverter extends AConverter {

  protected OWLClassExpressionVisitorEx<NLGElement> owlClassExpression;
  protected OWLIndividualVisitorEx<NLGElement> owlIndividual;
  protected OWLDataRangeVisitorEx<NLGElement> owlDataRange;

  /**
   * Converts class expressions.
   */
  public OWLClassExpressionConverter(final IInput in) {
    super(in);

    owlIndividual = new OWLIndividualToNLGElement(nlgFactory, in);
    owlDataRange = new OWLDataRangeToNLGElement(nlgFactory, in);
    owlClassExpression = new OWLClassExpressionToNLGElement(//
        nlgFactory, realiser, owlIndividual, owlDataRange, in//
    );
  }

  /**
   * Converts a OWLClassExpression to a NLGElement by calling asNLGElement and realizes a
   * verbalization.
   *
   * @param ce a OWLClassExpression
   * @return verbalization
   */
  public String convert(final OWLClassExpression ce) {
    return realiser.realise(asNLGElement(ce)).getRealisation();
  }

  public NLGElement asNLGElement(final OWLClassExpression ce) {
    return asNLGElement(ce, false);
  }

  /**
   * Transforms a OWLClassExpression to a NLGElement
   *
   * @param ce
   * @param isSubClassExpression
   * @return NLGElement
   */
  public NLGElement asNLGElement(final OWLClassExpression ce, final boolean isSubClassExpression) {

    resetsOWLClassExpressionParameter(ce, isSubClassExpression);

    // rewrite class expression and process
    return rewrite(ce).accept(owlClassExpression);
  }

  private void resetsOWLClassExpressionParameter(final OWLClassExpression ce,
      final boolean isSubClassExpression) {

    final OWLClassExpressionToNLGElement.Parameter parameter;
    parameter = ((OWLClassExpressionToNLGElement) owlClassExpression).new Parameter();
    parameter.isSubClassExpression = isSubClassExpression;
    parameter.root = ce;
    parameter.modalDepth = 1;
    ((OWLClassExpressionToNLGElement) owlClassExpression).setParameter(parameter);
  }

  private boolean containsNamedClass(final Set<OWLClassExpression> classExpressions) {
    for (final OWLClassExpression ce : classExpressions) {
      if (!ce.isAnonymous()) {
        return true;
      }
    }
    return false;
  }

  private OWLClassExpression rewrite(final OWLClassExpression ce) {
    return rewrite(ce, false);
  }

  private OWLClassExpression rewrite(final OWLClassExpression ce, final boolean inIntersection) {
    OWLClassExpression rtn = ce;

    if (ce.isAnonymous()) {
      LOG.info("rewrite before: {}", rtn.toString());
      rtn = rewriteAnonymous(ce, inIntersection);
      LOG.info("rewrite after: {}", rtn.toString());
    }
    return rtn;
  }

  private OWLClassExpression rewriteAnonymous(final OWLClassExpression ce,
      final boolean inIntersection) {

    OWLClassExpression rtn = ce;

    if (ce instanceof OWLObjectIntersectionOf) {
      LOG.debug("OWLObjectIntersectionOf");
      rtn = rewriteAnonymousOWLObjectIntersectionOf(ce);
    } else if (ce instanceof OWLObjectUnionOf) {
      LOG.debug("OWLObjectUnionOf");
      rtn = rewriteAnonymousOWLObjectUnionOf(ce);
    } else if (ce instanceof OWLObjectSomeValuesFrom) {
      LOG.debug("OWLObjectSomeValuesFrom");
      rtn = rewriteAnonymousOWLObjectSomeValuesFrom(ce, inIntersection);
    } else if (ce instanceof OWLObjectAllValuesFrom) {
      LOG.debug("OWLObjectAllValuesFrom");
      rtn = rewriteAnonymousOWLObjectAllValuesFrom(ce, inIntersection);
    } else if (!inIntersection) {
      rtn = df.getOWLObjectIntersectionOf(//
          Sets.<OWLClassExpression>newHashSet(ce, df.getOWLThing())//
      );
    }
    return rtn;
  }

  private OWLClassExpression rewriteAnonymousOWLObjectIntersectionOf(final OWLClassExpression ce) {

    final Set<OWLClassExpression> operands = ((OWLObjectIntersectionOf) ce).getOperands();
    final Set<OWLClassExpression> newOperands = Sets.newHashSet();

    for (final OWLClassExpression operand : operands) {
      newOperands.add(rewrite(operand, true));
    }

    if (!containsNamedClass(operands)) {
      newOperands.add(df.getOWLThing());
    }

    return df.getOWLObjectIntersectionOf(newOperands);
  }

  private OWLClassExpression rewriteAnonymousOWLObjectUnionOf(final OWLClassExpression ce) {
    final Set<OWLClassExpression> operands = ((OWLObjectUnionOf) ce).getOperands();
    final Set<OWLClassExpression> newOperands = Sets.newHashSet();

    for (final OWLClassExpression operand : operands) {
      newOperands.add(rewrite(operand));
    }

    return df.getOWLObjectUnionOf(newOperands);
  }

  private OWLClassExpression rewriteAnonymousOWLObjectSomeValuesFrom(final OWLClassExpression ce,
      final boolean inIntersection) {

    final OWLClassExpression newCe =
        df.getOWLObjectSomeValuesFrom(((OWLObjectSomeValuesFrom) ce).getProperty(),
            rewrite(((OWLObjectSomeValuesFrom) ce).getFiller()));

    return inIntersection ? newCe : df.getOWLObjectIntersectionOf(df.getOWLThing(), newCe);
  }

  private OWLClassExpression rewriteAnonymousOWLObjectAllValuesFrom(final OWLClassExpression ce,
      final boolean inIntersection) {

    final OWLClassExpression newCe =
        df.getOWLObjectAllValuesFrom(((OWLObjectAllValuesFrom) ce).getProperty(),
            rewrite(((OWLObjectAllValuesFrom) ce).getFiller()));

    return inIntersection ? newCe : df.getOWLObjectIntersectionOf(df.getOWLThing(), newCe);
  }
}
