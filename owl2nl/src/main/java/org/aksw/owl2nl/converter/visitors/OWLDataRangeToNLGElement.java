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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.util.grammar.Words;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWLFacet;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.NPPhraseSpec;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 * @author Rene Speck
 *
 */
public class OWLDataRangeToNLGElement extends AToNLGElement
    implements OWLDataRangeVisitorEx<NLGElement> {

  protected OWLDataFactory df = new OWLDataFactoryImpl();

  /**
   *
   * @param nlgFactory
   * @param input
   */
  public OWLDataRangeToNLGElement(final NLGFactory nlgFactory, final IInput input) {
    super(nlgFactory, input);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDatatype)
   */
  @Override
  public NLGElement visit(final OWLDatatype node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    return nlgFactory.createNounPhrase(getLexicalForm(node));
  }

  /*
   * If it contains more than one value, i.e. oneOf(v1_,...,v_n) with n > 1, we rewrite it as
   * unionOf(oneOf(v_1),...,oneOf(v_n))
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataOneOf)
   */
  @Override
  public NLGElement visit(final OWLDataOneOf node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    final Set<OWLLiteral> values = node.getValues();
    if (values.size() > 1) {
      final Set<OWLDataRange> operands = new HashSet<>(values.size());
      for (final OWLLiteral value : values) {
        operands.add(df.getOWLDataOneOf(value));
      }
      return df.getOWLDataUnionOf(operands).accept(this);
    }
    final String literal = literalConverter(values.iterator().next());

    return nlgFactory.createNounPhrase(literal);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataComplementOf)
   */
  @Override
  public NLGElement visit(final OWLDataComplementOf node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    final NLGElement nlgElement = node.getDataRange().accept(this);
    nlgElement.setFeature(Feature.NEGATED, true);
    return nlgElement;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataIntersectionOf)
   */
  @Override
  public NLGElement visit(final OWLDataIntersectionOf node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    for (final OWLDataRange op : node.getOperands()) {
      cc.addCoordinate(op.accept(this));
    }
    return cc;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataUnionOf)
   */
  @Override
  public NLGElement visit(final OWLDataUnionOf node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    cc.setConjunction(Words.or);
    for (final OWLDataRange op : node.getOperands()) {
      cc.addCoordinate(op.accept(this));
    }
    return cc;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDatatypeRestriction)
   */
  @Override
  public NLGElement visit(final OWLDatatypeRestriction node) {
    LOG.debug("{} called.", node.getClass().getSimpleName());

    final Set<OWLFacetRestriction> facetRestrictions = node.getFacetRestrictions();

    final List<NPPhraseSpec> phrases = new ArrayList<>(facetRestrictions.size());

    for (final OWLFacetRestriction facetRestriction : facetRestrictions) {
      final OWLFacet facet = facetRestriction.getFacet();
      final OWLLiteral value = facetRestriction.getFacetValue();

      final String valueString = value.getLiteral();

      String keyword = facet.toString();
      switch (facet) {
        case LENGTH:
          keyword = "STRLEN(STR(%s) = %d)";
          break;
        case MIN_LENGTH:
          keyword = "STRLEN(STR(%s) >= %d)";
          break;
        case MAX_LENGTH:
          keyword = "STRLEN(STR(%s) <= %d)";
          break;
        case PATTERN:
          keyword = "REGEX(STR(%s), %d)";
          break;
        case LANG_RANGE:
          break;
        case MAX_EXCLUSIVE:
          keyword = "lower than";
          break;
        case MAX_INCLUSIVE:
          keyword = "lower than or equals to";
          break;
        case MIN_EXCLUSIVE:
          keyword = "greater than";
          break;
        case MIN_INCLUSIVE:
          keyword = "greater than or equals to";
          break;
        case FRACTION_DIGITS:
          break;
        case TOTAL_DIGITS:
          break;
        default:
          break;
      }
      phrases.add(nlgFactory.createNounPhrase(keyword + " " + valueString));
    }

    if (phrases.size() > 1) {
      final CoordinatedPhraseElement coordinatedPhrase = nlgFactory.createCoordinatedPhrase();
      for (final NPPhraseSpec phrase : phrases) {
        coordinatedPhrase.addCoordinate(phrase);
      }
      return coordinatedPhrase;
    }
    return phrases.get(0);
  }
}
