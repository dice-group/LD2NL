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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.util.grammar.Grammar;
import org.aksw.owl2nl.util.grammar.IGrammar;
import org.aksw.owl2nl.util.grammar.Words;
import org.aksw.triple2nl.nlp.stemming.PlingStemmer;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.Person;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 * @author Rene Speck
 *
 */
public class OWLClassExpressionToNLGElement extends AToNLGElement
    implements OWLClassExpressionVisitorEx<NLGElement> {

  /**
   * Holds parameters used in the OWLClassExpressionToNLGElement class.
   */
  public class Parameter {
    public boolean noun;
    public boolean isSubClassExpression;
    public int modalDepth;
    public OWLClassExpression root;
    public boolean plural;
  }

  // holds parameter
  protected Parameter parameter;

  protected IGrammar en = Grammar.getEN();

  protected OWLDataFactory df = new OWLDataFactoryImpl();

  protected OWLDataRangeVisitorEx<NLGElement> converterOWLDataRange;
  protected OWLIndividualVisitorEx<NLGElement> converterOWLIndividual;

  private final Realiser realiser;

  /**
   * OWLClassExpressionToNLGElement constructor.
   *
   * @param nlgFactory
   * @param realiser
   * @param iriConverter
   * @param converterOWLIndividual
   * @param converterOWLDataRange
   */
  public OWLClassExpressionToNLGElement(final NLGFactory nlgFactory, final Realiser realiser,
      final OWLIndividualVisitorEx<NLGElement> converterOWLIndividual,
      final OWLDataRangeVisitorEx<NLGElement> converterOWLDataRange, final IInput input) {

    super(nlgFactory, input);

    this.realiser = realiser;
    this.converterOWLIndividual = converterOWLIndividual;
    this.converterOWLDataRange = converterOWLDataRange;
  }

  public void setParameter(final Parameter parameter) {
    this.parameter = parameter;
  }

  /**
   *
   * @param ce
   */
  @Override
  public NLGElement visit(final OWLClass ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final NPPhraseSpec nounPhrase;
    parameter.noun = true;

    String lexicalForm = getLexicalForm(ce).toLowerCase();
    if (lexicalForm.split(" ").length == 1) {
      lexicalForm = PlingStemmer.stem(lexicalForm);
    }

    if (parameter.isSubClassExpression) {
      if (ce.isOWLThing()) {

        final NLGElement word = nlgFactory.createWord(//
            parameter.modalDepth == 1 ? Words.everything : Words.something, LexicalCategory.NOUN//
        );
        word.setFeature(InternalFeature.NON_MORPH, true);
        nounPhrase = nlgFactory.createNounPhrase(word);
      } else {
        nounPhrase = nlgFactory.createNounPhrase(lexicalForm);
        if (parameter.modalDepth > 1 && !ce.equals(parameter.root)) {
          nounPhrase.setDeterminer(Words.a);
        } else {
          nounPhrase.setPreModifier(Words.every);
        }
      }
    } else {
      nounPhrase = nlgFactory.createNounPhrase(//
          ce.isOWLThing() ? Words.something : nlgFactory.createNounPhrase(Words.a, lexicalForm)//
      );
    }
    return nounPhrase;
  }

  private boolean isPerson(final NPPhraseSpec firstElement) {
    // get noun realization
    String realised = firstElement.getNoun().getRealisation();
    if (realised == null || realised.trim().isEmpty()) {
      realised = realiser.realise(firstElement).toString();
    }
    if (realised == null || realised.trim().isEmpty()) {
      LOG.warn("Could not get a label for: {}", firstElement.toString());
    } else {
      return en.isPerson(realised);
    }
    LOG.debug("Realised subject: {}", realised);
    return false;
  }

  private CoordinatedPhraseElement processOperands(final List<OWLClassExpression> operands,
      final boolean isPerson) {
    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    // process all owl classes
    final List<OWLClass> classes = operands.stream()//
        .filter(o -> !o.isAnonymous())//
        .map(o -> o.asOWLClass())//
        .collect(Collectors.toList());
    operands.removeAll(classes);
    for (final OWLClass cls : classes) {
      final SPhraseSpec clause = nlgFactory.createClause(//
          isPerson ? Words.who : Words.that, //
          Words.be//
      );
      clause.getVerb().setFeature(Feature.PERSON, Person.THIRD);
      final NLGElement object = cls.accept(this);
      clause.setObject(object);
      cc.addCoordinate(clause);
    }
    // process the rest
    for (final OWLClassExpression operand : operands) {
      final SPhraseSpec clause = nlgFactory.createClause();
      clause.setSubject(isPerson ? Words.who : Words.that);
      clause.setVerbPhrase(operand.accept(this));
      cc.addCoordinate(clause);
    }
    return cc;
  }

  private SPhraseSpec processOperand(final List<OWLClassExpression> operands,
      final boolean isPerson, final boolean isOWLClass) {

    final OWLClassExpression operand = operands.get(0);
    final SPhraseSpec clause = nlgFactory.createClause();
    final NLGElement el = operand.accept(this);

    if (parameter.noun || isOWLClass && isPerson) {

      final boolean isOWLObjectHasSelf = operand instanceof OWLObjectHasSelf;
      final boolean isOWLObjectAllValuesFrom = operand instanceof OWLObjectAllValuesFrom;
      final boolean isOWLObjectSomeValuesFrom = operand instanceof OWLObjectSomeValuesFrom;

      final boolean isFillerOWLThing = isOWLObjectSomeValuesFrom ? //
          ((OWLObjectSomeValuesFrom) operand).getFiller().isOWLThing() : false;

      if (isOWLObjectHasSelf || isOWLObjectAllValuesFrom) {
        if (isOWLClass && isPerson) {
          clause.setFeature(Feature.COMPLEMENTISER, Words.who);
          clause.setVerbPhrase(el);
        } else {
          clause.setFeature(Feature.COMPLEMENTISER, Words.that);
          clause.setVerbPhrase(el);
        }
      } else if (isOWLObjectSomeValuesFrom && isFillerOWLThing) {
        // clause.setFeature(Feature.COMPLEMENTISER, "that");
        clause.setVerb(Words.have);
        clause.setObject(el);
      } else if (isOWLClass && isPerson) {
        final ElementCategory cat = el.getChildren().get(0).getCategory();
        if (cat.equals(PhraseCategory.NOUN_PHRASE)) {
          clause.setFeature(Feature.COMPLEMENTISER, Words.whose);
        } else if (cat.equals(PhraseCategory.VERB_PHRASE)
            || cat.equals(PhraseCategory.ADVERB_PHRASE)) {
          clause.setFeature(Feature.COMPLEMENTISER, Words.who);
        }
        clause.setVerbPhrase(el);
        if (parameter.plural) {
          // How to make it plural here?
        }
      } else {
        clause.setFeature(Feature.COMPLEMENTISER, Words.whose);
        clause.setVerbPhrase(el);
      }
    } else {
      clause.setFeature(Feature.COMPLEMENTISER, Words.that);
      clause.setVerbPhrase(el);
    }
    return clause;
  }

  @Override
  public NLGElement visit(final OWLObjectIntersectionOf ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final List<OWLClassExpression> operands = getOperandsByPriority(ce);

    final OWLClassExpression first = operands.remove(0);
    final NPPhraseSpec subject = (NPPhraseSpec) first.accept(this);
    final boolean isOWLClass = isOWLClass(first);
    final boolean isPerson = isOWLClass && isPerson(subject);

    LOG.debug("# operands: {}, is {}a person, is {}a OWLClass, parameter {}is noun",
        operands.size(), isPerson ? "" : "not ", isOWLClass ? "" : "not ",
        parameter.noun ? "" : "not ");

    final SPhraseSpec phrase = nlgFactory.createClause();
    phrase.setSubject(subject);

    if (operands.size() > 1) {
      final CoordinatedPhraseElement cc = processOperands(operands, isPerson);
      phrase.setVerbPhrase(cc);
    } else {
      final SPhraseSpec clause = processOperand(operands, isPerson, isOWLClass);
      //
      phrase.setComplement(clause);
    }

    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectUnionOf ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final List<OWLClassExpression> operands = getOperandsByPriority(ce);

    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    cc.setConjunction(Words.or);
    for (final OWLClassExpression operand : operands) {
      cc.addCoordinate(operand.accept(this));
    }

    LOG.debug(ce + " = " + realiser.realise(cc));
    return cc;
  }

  @Override
  public NLGElement visit(final OWLObjectComplementOf ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    boolean neg = true;
    final OWLClassExpression operand = ce.getOperand();
    NLGElement verbal = operand.accept(this);
    if (verbal.getCategory().equals(PhraseCategory.CLAUSE)) {
      final NLGElement verb = ((SPhraseSpec) verbal).getVerb();
      if (verb.getRealisation().isEmpty()) {
        for (final NLGElement e : ((SPhraseSpec) verbal).getVerbPhrase().getChildren()) {

          // TODO: add VERB in if
          if (e.getCategory() != null && e.getCategory().equals(PhraseCategory.VERB_PHRASE)) {
            ((VPPhraseSpec) e).setFeature(Feature.NEGATED, true);
            neg = false;
          }
        }
      } else {
        verb.setFeature(Feature.NEGATED, true);
        neg = false;
      }
    }

    if (operand.isAnonymous()) {
      LOG.debug("Anonymous operand: {}", operand.getClassExpressionType());
    } else {
      final SPhraseSpec clause = nlgFactory.createClause(null, Words.be, verbal);
      clause.getVerb().setFeature(Feature.PERSON, Person.THIRD);
      verbal = clause;
    }
    LOG.debug("neg: {}", neg);

    verbal.setFeature(Feature.NEGATED, neg);
    parameter.noun = false;

    LOG.debug(ce + " = " + realiser.realise(verbal));
    return verbal;
  }

  /**
   *
   * @param pos
   * @return true if pos is of the form "V* N*"
   */
  protected boolean isVN(final String pos) {
    final String[] posTags = pos.split(" ");
    if (posTags.length > 1 && posTags[0].startsWith("V") && posTags[1].startsWith("N")) {
      return true;
    }
    return false;
  }

  @Override
  public NLGElement visit(final OWLObjectSomeValuesFrom ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    parameter.modalDepth++;
    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    final SPhraseSpec element = nlgFactory.createClause();

    if (!property.isAnonymous()) {

      final PropertyVerbalization verbal = propertyVerbalizer(property);
      String verbalText = propertyVerbalizerText(verbal);

      if (verbal.isNounType()) {
        LOG.debug("property is noun POS: {}", verbal.getPOSTags());

        final NPPhraseSpec nounPhrase = nlgFactory.createNounPhrase(//
            PlingStemmer.stem(verbalText)//
        );

        if (filler.isOWLThing()) {
          nounPhrase.setDeterminer(Words.a);
          return nounPhrase;
        }
        element.setSubject(nounPhrase);
        element.setVerb(Words.be);
        element.getVerb().setFeature(Feature.PERSON, Person.THIRD);

        final NLGElement e = setObject(element, filler);
        e.setPlural(false);
        e.setFeature(Feature.COMPLEMENTISER, null);

        LOG.info("realiser: {}, property:{}", realiser.realise(element), verbalText);
        parameter.noun = true;
      } else if (verbal.isVerbType()) {
        LOG.debug("property is verb POS: {}", verbal.getPOSTags());

        if (isVN(verbal.getPOSTags())) {

          final List<String> t = startsWithHave(verbalText);
          if (!t.isEmpty()) {
            LOG.debug("verbalizationText with has or have: {}", verbalText);
          }
          verbalText = t.get(0).concat(" ").concat(filler.isOWLThing() ? Words.a : Words.as);
          // stem the noun
          // TODO to absolutely correct we have to stem the noun phrase
          verbalText = verbalText.concat(" ").concat(PlingStemmer.stem(t.get(1)));

          // append rest of the tokens
          for (int i = 2; i < t.size(); i++) {
            verbalText = verbalText.concat(" ") + t.get(i);
          }
          verbalText = verbalText.trim();

          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalText);
          element.setVerb(verb);

          if (!filler.isOWLThing()) {
            setObject(element, filler).setFeature(Feature.COMPLEMENTISER, null);
          }
        } else {
          LOG.debug(//
              "property: {} POS: {} Tense: {}", verbalText, verbal.getPOSTags(), verbal.getTense()//
          );

          if ("VP IN JJ NP".equals(verbal.getPOSTags())) {
            final List<String> t = startsWithHave(verbalText);
            if (!t.isEmpty()) {
              final VPPhraseSpec verb = nlgFactory.createVerbPhrase(t.remove(0));
              final PPPhraseSpec preposition = nlgFactory.createPrepositionPhrase(t.remove(0));
              final NPPhraseSpec noun = nlgFactory.createNounPhrase();
              noun.setPreModifier(t.remove(0));
              noun.setNoun(t.remove(0));

              preposition.setPostModifier(noun);

              verb.setPostModifier(preposition);

              element.setVerb(verb);
              // verb.setFeature(Feature.PERSON, Person.THIRD);
              setObject(element, filler).setFeature(Feature.COMPLEMENTISER, null);
            }
          } else {
            final List<String> t = startsWithHave(verbalText);
            if (!t.isEmpty()) {

              LOG.debug("verbalizationText with has or have");

              final VPPhraseSpec verb = nlgFactory.createVerbPhrase(t.remove(0));
              verb.setPostModifier(nlgFactory.createVerbPhrase(String.join(" ", t)));
              element.setVerbPhrase(verb);
              // verb.setFeature(Feature.PERSON, Person.THIRD);
              setObject(element, filler).setFeature(Feature.COMPLEMENTISER, null);

            } else {
              final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalText);
              element.setVerb(verb);

              setObject(element, filler).setFeature(Feature.COMPLEMENTISER, null);
            }
          }
        }
        parameter.noun = false;

      } else {
        LOG.warn("property has unspecified type: {}", verbal.isUnspecifiedType());
      }
    }
    LOG.debug(ce + " = " + realiser.realise(element));
    LOG.debug("property is anonymous :{}", property.isAnonymous());
    parameter.modalDepth--;
    return element;
  }

  @Override
  public NLGElement visit(final OWLObjectAllValuesFrom ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    if (!property.isAnonymous()) {
      LOG.debug(property.toString());
      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      String verbalizationText = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase =
            nlgFactory.createNounPhrase(PlingStemmer.stem(verbalizationText));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb(Words.be);
        phrase.getVerb().setFeature(Feature.PERSON, Person.THIRD);
        final NLGElement fillerElement = filler.accept(this);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {

        if (isVN(propertyVerbalization.getPOSTags())) {

          // if(tokens[0].equals("has") || tokens[0].equals(Words.have)){
          final String[] tokens = verbalizationText.split(" ");
          // TODO: How handle properties with 3 tokens?
          LOG.debug("tokens {}", tokens.length);

          verbalizationText = tokens[0];
          if (filler.isOWLNothing()) {
            verbalizationText = verbalizationText + " ".concat(Words.no);
          } else if (!filler.isOWLThing()) {
            verbalizationText = verbalizationText + " ".concat(Words.as);
          } else {
            verbalizationText = verbalizationText + " ".concat(Words.a);
          }
          // stem the noun
          // TODO to be absolutely correct, we have to stem the noun phrase
          String nounToken = tokens[1];
          nounToken = PlingStemmer.stem(nounToken);
          verbalizationText = verbalizationText + " ".concat(nounToken);
          // append rest of the tokens
          for (int i = 2; i < tokens.length; i++) {
            verbalizationText = verbalizationText + " ".concat(tokens[i]);
          }
          if (filler.isOWLNothing()) {
            final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
            phrase.setVerb(verb);

          } else if (filler.isOWLThing()) {
            // ?
          } else if (!filler.isOWLThing()) {
            // adds only
            verbalizationText = (verbalizationText + " ".concat(Words.only)).trim();

            final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
            phrase.setVerb(verb);

            final NLGElement fillerElement = filler.accept(this);
            phrase.setObject(fillerElement);
            fillerElement.setFeature(Feature.COMPLEMENTISER, null);
          }
        } else { // !isVN
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          verb.addModifier(Words.only);
          phrase.setVerb(verb);

          final NLGElement fillerElement = filler.accept(this);
          phrase.setObject(fillerElement);
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        parameter.noun = false;
      } else {
        // propertyVerbalization is not a verb or noun
      }
    } else {
      // property is anonymous: not label exists, mostly for an inverse property
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasValue ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLIndividual value = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      final String verbalizationText = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase =
            nlgFactory.createNounPhrase(PlingStemmer.stem(verbalizationText));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb(Words.be);
        phrase.getVerb().setFeature(Feature.PERSON, Person.THIRD);
        final NLGElement fillerElement = value.accept(converterOWLIndividual);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(verbalizationText);

        final NLGElement fillerElement = value.accept(converterOWLIndividual);
        phrase.setObject(fillerElement);

        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectMinCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processObjectCardinalityRestriction(ce, Words.at.concat(" ").concat(Words.least));
  }

  @Override
  public NLGElement visit(final OWLObjectMaxCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processObjectCardinalityRestriction(ce, Words.at.concat(" ").concat(Words.most));
  }

  @Override
  public NLGElement visit(final OWLObjectExactCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processObjectCardinalityRestriction(ce, Words.exactly);
  }

  // #########################################################
  // ################# helpers ###############################
  // #########################################################

  // protected

  // private

  private NLGElement processObjectCardinalityRestriction(final OWLObjectCardinalityRestriction ce,
      final String modifier) {

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    final NLGElement phrase = processOCRn(property, filler, cardinality, modifier);

    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    return phrase;
  }

  private NLGElement processOCRn(final OWLObjectPropertyExpression property,
      final OWLClassExpression filler, final int cardinality, String modifier) {

    LOG.debug("OWLObjectPropertyExpression ....");
    final SPhraseSpec phrase = nlgFactory.createClause();

    modifier = modifier.concat(" ").concat(Words.number(cardinality));

    if (!property.isAnonymous()) {

      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      String verbalizationText = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {

        final NLGElement word = nlgFactory.createWord(PlingStemmer.stem(//
            verbalizationText), LexicalCategory.NOUN);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(word);
        word.setPlural(cardinality > 1);
        propertyNounPhrase.setPlural(cardinality > 1);
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase(Words.have);
        verb.addModifier(modifier);

        phrase.setVerb(verb);
        phrase.setObject(propertyNounPhrase);

        final NLGElement fillerElement = filler.accept(this);

        final SPhraseSpec clause = nlgFactory.createClause();
        clause.setPlural(cardinality > 1);
        clause.setVerb(Words.be);
        clause.setObject(fillerElement);
        if (fillerElement.isA(PhraseCategory.CLAUSE)) {
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        phrase.setComplement(clause);

        parameter.noun = false;
      } else if (propertyVerbalization.isVerbType()) {

        /*
         * here comes actually one of the most tricky parts Normally, we just use the verbalization
         * as verb, and add a modifier like 'at least n' which works good for phrase like 'works
         * for', such that we get 'works for at least n'. But, if we have something like 'has
         * gender' it would result in a strange construct 'has gender at least n', although it
         * sounds more natural to have 'has at least n gender' We could make use of POS tags to find
         * such cases, although it might be more complex for longer phrases. It's not really clear
         * for which verbs the rules holds, but 'has','include','contains',etc. might be a good
         * starting point.
         */

        /**
         * guess we need to set a flag like: parameter.plural = <true,false>
         */
        // LOG.debug("OWLObjectMinCardinality {}", modifier);
        // LOG.debug("verbalizationText {}", verbalizationText);
        // LOG.debug("getPOSTags {}", propertyVerbalization.getPOSTags());

        if (isVN(propertyVerbalization.getPOSTags())) {
          // if(tokens[0].equals("has") || tokens[0].equals(Words.have)){
          final String[] tokens = verbalizationText.split(" ");
          verbalizationText = tokens[0];
          verbalizationText = verbalizationText.concat(" ") + modifier;
          // stem the noun if card == 1
          String nounToken = tokens[1];
          LOG.debug(tokens);
          if (cardinality == 1) {
            parameter.plural = false;
            nounToken = PlingStemmer.stem(nounToken);
          } else {
            parameter.plural = true;
            nounToken = en.plural(nounToken);
          }
          verbalizationText = verbalizationText.concat(" ") + nounToken;
          for (int i = 2; i < tokens.length; i++) {
            // TODO: here plural too? Or check some order and length of the tokens
            verbalizationText = verbalizationText.concat(" ") + tokens[i];
          }
          phrase.setVerb(nlgFactory.createVerbPhrase(verbalizationText.trim()));
        } else {
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          verb.addModifier(modifier);
          phrase.setVerb(verb);
        }

        if (!filler.isOWLThing()) {
          final NLGElement fillerElement = filler.accept(this);
          fillerElement.setPlural(cardinality > 1);
          phrase.setObject(fillerElement);
        }
        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("OWLObjectPropertyExpression finished.");
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasSelf ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final SPhraseSpec phrase = nlgFactory.createClause();
    final OWLObjectPropertyExpression property = ce.getProperty();
    if (!property.isAnonymous()) {
      // should be pronoun but how i go from "it" to "onself"
      final NLGElement x = nlgFactory.createWord(Words.oneself, LexicalCategory.ADVERB);
      final NPPhraseSpec n = nlgFactory.createNounPhrase();
      n.setNoun(x);
      phrase.setVerb(propertyVerbalizerText(propertyVerbalizer(property)));
      phrase.setObject(n);

    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectOneOf ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    // if it contains more than one value, i.e. oneOf(v1_,...,v_n) with n > 1, we rewrite it as
    // unionOf(oneOf(v_1),...,oneOf(v_n))
    final Set<OWLIndividual> individuals = ce.getIndividuals();

    if (individuals.size() > 1) {
      final Set<OWLClassExpression> operands = new HashSet<>(individuals.size());
      for (final OWLIndividual ind : individuals) {
        operands.add(df.getOWLObjectOneOf(ind));
      }
      return df.getOWLObjectUnionOf(operands).accept(this);
    }
    return individuals.iterator().next().accept(converterOWLIndividual);
  }

  @Override
  public NLGElement visit(final OWLDataSomeValuesFrom ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {

      if (!filler.isTopDatatype()) {
        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        phrase.setObject(fillerElement);
      }

      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      final String t = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(PlingStemmer.stem(t));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb(Words.be);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(t);

        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataAllValuesFrom ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      final String t = propertyVerbalizerText(propertyVerbalization);

      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(PlingStemmer.stem(t));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb(Words.be);
        phrase.getVerb().setFeature(Feature.PERSON, Person.THIRD);
        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {

        if (filler.isDatatype()
            && filler.asOWLDatatype().getIRI().equals(OWL2Datatype.XSD_BOOLEAN.getIRI())) {
          // "either VERB or not"
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(t);
          phrase.setVerb(verb);
          verb.addFrontModifier(Words.either);
          verb.addPostModifier(Words.or.concat(" ").concat(Words.not));
        } else {
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(t);
          verb.addModifier(Words.only);
          phrase.setVerb(verb);

          final NLGElement fillerElement = filler.accept(converterOWLDataRange);
          phrase.setObject(fillerElement);
        }

        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataHasValue ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLLiteral value = ce.getFiller();

    LOG.info("value: {}", value.toString());

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      // final String verbalizationText = propertyVerbalization.getVerbalizationText();
      final String t = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {
        // verbalizationText = PlingStemmer.stem(verbalizationText);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(t);
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb(Words.be);
        phrase.getVerb().setFeature(Feature.PERSON, Person.THIRD);
        final NLGElement valueElement = nlgFactory.createNounPhrase(literalConverter(value));
        phrase.setObject(valueElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        // if phrase starts with something like 'is' and value is a Boolean
        final String[] tokens = t.split(" ");
        if (value.getDatatype().isBoolean() && tokens[0].equals("is")) { // is it working as
                                                                         // expected?
          if (!value.parseBoolean()) {
            phrase.setFeature(Feature.NEGATED, true);
          }
        } else {
          final NLGElement valueElement = nlgFactory.createNounPhrase(literalConverter(value));
          phrase.setObject(valueElement);
        }

        phrase.setVerb(t);

        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataMinCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processDataCardinalityRestriction(ce, Words.at.concat(" ").concat(Words.least));
  }

  @Override
  public NLGElement visit(final OWLDataExactCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processDataCardinalityRestriction(ce, Words.exactly);
  }

  @Override
  public NLGElement visit(final OWLDataMaxCardinality ce) {
    LOG.debug("{} called.", ce.getClass().getSimpleName());

    return processDataCardinalityRestriction(ce, Words.at.concat(" ").concat(Words.most));
  }

  private NLGElement processDataCardinalityRestriction(final OWLDataCardinalityRestriction ce,
      final String modifier) {

    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = propertyVerbalizer(property);
      final String t = propertyVerbalizerText(propertyVerbalization);
      if (propertyVerbalization.isNounType()) {
        final NLGElement word = nlgFactory.createWord(PlingStemmer.stem(t), LexicalCategory.NOUN);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(word);
        if (cardinality > 1) {
          word.setPlural(true);
          propertyNounPhrase.setPlural(true);
        }
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase(Words.have);
        verb.addModifier(modifier.concat(" ").concat(Words.number(cardinality)));

        phrase.setVerb(verb);
        phrase.setObject(propertyNounPhrase);

        final NLGElement fillerElement = filler.accept(converterOWLDataRange);

        final SPhraseSpec clause = nlgFactory.createClause();
        if (cardinality > 1) {
          clause.setPlural(true);
        }
        clause.setVerb(Words.be);
        clause.setObject(fillerElement);
        if (fillerElement.isA(PhraseCategory.CLAUSE)) {
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        phrase.setComplement(clause);

        parameter.noun = false;
      } else if (propertyVerbalization.isVerbType()) {
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase(t);
        verb.addModifier(modifier.concat(" ").concat(Words.number(cardinality)));
        phrase.setVerb(verb);

        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        fillerElement.setPlural(true);
        phrase.setObject(fillerElement);

        parameter.noun = false;
      } else {
      }
    } else {
    }
    LOG.debug("{} = {}", ce, realiser.realise(phrase));

    return phrase;
  }

  /**
   * In case the given text starts with the word "have" or "has" it returns the token list otherwise
   * an empty list.
   *
   * @param text
   * @return
   */
  private List<String> startsWithHave(final String text) {
    final List<String> tokens = new ArrayList<>(Arrays.asList(text.split(" ")));
    if (tokens.get(0).equals(Words.has) || tokens.get(0).equals(Words.have)) {
      return tokens;
    }
    return new ArrayList<>();
  }

  /**
   * Creates an object with the given filler and this visitor class as well as it sets the created
   * object to the given SPhraseSpec instance.
   *
   * @param phrase
   * @param filler
   *
   * @return object The created and set object.
   */
  private NLGElement setObject(final SPhraseSpec phrase, final OWLClassExpression filler) {
    final NLGElement fillerElement = filler.accept(this);
    phrase.setObject(fillerElement);
    return fillerElement;
  }

  /**
   * Returns a list of operands ordered by class expressions types, starting with the "more easy"
   * first.
   *
   * @param ce the class expression
   * @return a list of operands
   */
  protected List<OWLClassExpression> getOperandsByPriority(final OWLNaryBooleanClassExpression ce) {
    return ce.getOperandsAsList();
  }

  private boolean isOWLClass(final OWLClassExpression ce) {
    return ClassExpressionType.OWL_CLASS.getName()//
        .equals(ce.getClassExpressionType().getName());
  }
}
