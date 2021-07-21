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
import org.aksw.owl2nl.converter.visitors.OWLPropertyExpressiontoNLGElement;
import org.aksw.owl2nl.data.IInput;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import com.google.common.collect.Sets;

import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 * @author Rene Speck
 */
public class OWLClassExpressionConverter implements //
    OWLClassExpressionVisitorEx<NLGElement>, //
    OWLIndividualVisitorEx<NLGElement>, //
    OWLDataRangeVisitorEx<NLGElement> {

  protected OWLClassExpressionVisitorEx<NLGElement> converterOWLClassExpression;
  protected OWLIndividualVisitorEx<NLGElement> converterOWLIndividual;
  protected OWLDataRangeVisitorEx<NLGElement> converterOWLDataRange;
  protected OWLPropertyExpressionVisitorEx<NLGElement> converterOWLPropertyExpression;

  private final Realiser realiser;
  private final OWLDataFactory df = new OWLDataFactoryImpl();

  /**
   * Converts class expressions.
   */
  public OWLClassExpressionConverter(final IInput in) {

    realiser = new Realiser(in.getLexicon());
    final NLGFactory nlgFactory = new NLGFactory(in.getLexicon());

    converterOWLIndividual = new OWLIndividualToNLGElement(nlgFactory, in);
    converterOWLDataRange = new OWLDataRangeToNLGElement(nlgFactory, in);
    converterOWLPropertyExpression = new OWLPropertyExpressiontoNLGElement(nlgFactory, in);
    converterOWLClassExpression = new OWLClassExpressionToNLGElement(//
        nlgFactory, realiser, converterOWLIndividual, converterOWLDataRange,
        converterOWLPropertyExpression, in//
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
    return rewrite(ce).accept(this);
  }

  private void resetsOWLClassExpressionParameter(final OWLClassExpression ce,
      final boolean isSubClassExpression) {
    final OWLClassExpressionToNLGElement.Parameter parameter;
    parameter = ((OWLClassExpressionToNLGElement) converterOWLClassExpression).new Parameter();
    parameter.isSubClassExpression = isSubClassExpression;
    parameter.root = ce;
    parameter.modalDepth = 1;
    ((OWLClassExpressionToNLGElement) converterOWLClassExpression).setParameter(parameter);
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
    if (!ce.isAnonymous()) {
      return ce;
    } else if (ce instanceof OWLObjectOneOf) {
      return ce;
    } else if (ce instanceof OWLObjectIntersectionOf) {
      final Set<OWLClassExpression> operands = ((OWLObjectIntersectionOf) ce).getOperands();
      final Set<OWLClassExpression> newOperands = Sets.newHashSet();

      for (final OWLClassExpression operand : operands) {
        newOperands.add(rewrite(operand, true));
      }

      if (!containsNamedClass(operands)) {
        newOperands.add(df.getOWLThing());
      }

      return df.getOWLObjectIntersectionOf(newOperands);
    } else if (ce instanceof OWLObjectUnionOf) {
      final Set<OWLClassExpression> operands = ((OWLObjectUnionOf) ce).getOperands();
      final Set<OWLClassExpression> newOperands = Sets.newHashSet();

      for (final OWLClassExpression operand : operands) {
        newOperands.add(rewrite(operand));
      }

      return df.getOWLObjectUnionOf(newOperands);
    } else if (ce instanceof OWLObjectSomeValuesFrom) {
      final OWLClassExpression newCe =
          df.getOWLObjectSomeValuesFrom(((OWLObjectSomeValuesFrom) ce).getProperty(),
              rewrite(((OWLObjectSomeValuesFrom) ce).getFiller()));
      if (inIntersection) {
        return newCe;
      }
      return df.getOWLObjectIntersectionOf(df.getOWLThing(), newCe);
    } else if (ce instanceof OWLObjectAllValuesFrom) {
      final OWLClassExpression newCe =
          df.getOWLObjectAllValuesFrom(((OWLObjectAllValuesFrom) ce).getProperty(),
              rewrite(((OWLObjectAllValuesFrom) ce).getFiller()));
      if (inIntersection) {
        return newCe;
      }
      return df.getOWLObjectIntersectionOf(df.getOWLThing(), newCe);
    }
    if (inIntersection) {
      return ce;
    }
    final Set<OWLClassExpression> operands =
        Sets.<OWLClassExpression>newHashSet(ce, df.getOWLThing());
    return df.getOWLObjectIntersectionOf(operands);
  }

  //
  // OWLClassExpressionVisitorEx
  //
  @Override
  public NLGElement visit(final OWLClass ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectIntersectionOf ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectUnionOf ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectComplementOf ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectSomeValuesFrom ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectAllValuesFrom ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectHasValue ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectMinCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectMaxCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectExactCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectHasSelf ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectOneOf ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataSomeValuesFrom ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataAllValuesFrom ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataHasValue ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataMaxCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataMinCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  @Override
  public NLGElement visit(final OWLDataExactCardinality ce) {
    return converterOWLClassExpression.visit(ce);
  }

  //
  // OWLIndividualVisitorEx
  //
  @Override
  public NLGElement visit(final OWLNamedIndividual individual) {
    return converterOWLIndividual.visit(individual);
  }

  @Override
  public NLGElement visit(final OWLAnonymousIndividual individual) {
    return converterOWLIndividual.visit(individual);
  }

  //
  // OWLDataRangeVisitorEx
  //
  @Override
  public NLGElement visit(final OWLDatatype node) {
    return converterOWLDataRange.visit(node);
  }

  @Override
  public NLGElement visit(final OWLDataOneOf node) {
    return converterOWLDataRange.visit(node);
  }

  @Override
  public NLGElement visit(final OWLDataComplementOf node) {
    return converterOWLDataRange.visit(node);
  }

  @Override
  public NLGElement visit(final OWLDataIntersectionOf node) {
    return converterOWLDataRange.visit(node);
  }

  @Override
  public NLGElement visit(final OWLDataUnionOf node) {
    return converterOWLDataRange.visit(node);
  }

  @Override
  public NLGElement visit(final OWLDatatypeRestriction node) {
    return converterOWLDataRange.visit(node);
  }
}
