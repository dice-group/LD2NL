package org.aksw.owl2nl.raki.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.raki.data.Input;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
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

/**
 * @author Rene Speck
 *
 */
public class OWLDataRangeToNLGElement extends AOWLConverter
    implements OWLDataRangeVisitorEx<NLGElement> {

  /**
   *
   * @param nlgFactory
   * @param iriConverter
   */
  public OWLDataRangeToNLGElement(final NLGFactory nlgFactory, final Input in) {
    super(nlgFactory, in);

  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDatatype)
   */
  @Override
  public NLGElement visit(final OWLDatatype node) {
    return nlgFactory.createNounPhrase(getLexicalForm(node));
  }

  @Override
  public NLGElement visit(final OWLDataOneOf node) {
    // if it contains more than one value, i.e. oneOf(v1_,...,v_n) with n > 1, we rewrite it as
    // unionOf(oneOf(v_1),...,oneOf(v_n))
    final Set<OWLLiteral> values = node.getValues();

    if (values.size() > 1) {
      final Set<OWLDataRange> operands = new HashSet<>(values.size());
      for (final OWLLiteral value : values) {
        operands.add(df.getOWLDataOneOf(value));
      }
      return df.getOWLDataUnionOf(operands).accept(this);
    }
    return nlgFactory.createNounPhrase(literalConverter.convert(values.iterator().next()));
  }

  @Override
  public NLGElement visit(final OWLDataComplementOf node) {
    final NLGElement nlgElement = node.getDataRange().accept(this);
    nlgElement.setFeature(Feature.NEGATED, true);
    return nlgElement;
  }

  @Override
  public NLGElement visit(final OWLDataIntersectionOf node) {
    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();

    for (final OWLDataRange op : node.getOperands()) {
      cc.addCoordinate(op.accept(this));
    }

    return cc;
  }

  @Override
  public NLGElement visit(final OWLDataUnionOf node) {
    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    cc.setConjunction("or");

    for (final OWLDataRange op : node.getOperands()) {
      cc.addCoordinate(op.accept(this));
    }

    return cc;
  }

  @Override
  public NLGElement visit(final OWLDatatypeRestriction node) {
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
