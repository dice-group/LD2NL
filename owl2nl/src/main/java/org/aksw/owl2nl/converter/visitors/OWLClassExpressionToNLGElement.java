package org.aksw.owl2nl.converter.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.data.IInput;
import org.aksw.triple2nl.nlp.stemming.PlingStemmer;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.semanticweb.owlapi.model.IRI;
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
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.phrasespec.NPPhraseSpec;
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

  // holds parameter
  protected Parameter parameter;

  /**
   * Holds parameters used in the OWLPropertyExpressiontoNLGElement class.
   */
  public class Parameter {

    public boolean noun, isSubClassExpression;
    public int modalDepth;
    public OWLClassExpression root;
  }

  public void setParameter(final Parameter parameter) {
    this.parameter = parameter;
  }

  protected OWLDataFactory df = new OWLDataFactoryImpl();

  protected OWLDataRangeVisitorEx<NLGElement> converterOWLDataRange;
  protected OWLIndividualVisitorEx<NLGElement> converterOWLIndividual;
  protected OWLPropertyExpressionVisitorEx<NLGElement> converterOWLPropertyExpression;

  private final PropertyVerbalizer propertyVerbalizer;
  private final Realiser realiser;

  /**
   *
   * @param nlgFactory
   * @param realiser
   * @param iriConverter
   * @param converterOWLIndividual
   * @param converterOWLDataRange
   */
  public OWLClassExpressionToNLGElement(final NLGFactory nlgFactory, final Realiser realiser,
      final OWLIndividualVisitorEx<NLGElement> converterOWLIndividual,
      final OWLDataRangeVisitorEx<NLGElement> converterOWLDataRange,
      final OWLPropertyExpressionVisitorEx<NLGElement> converterOWLPropertyExpression,
      final IInput input) {

    super(nlgFactory, input);

    this.realiser = realiser;
    this.converterOWLIndividual = converterOWLIndividual;
    this.converterOWLDataRange = converterOWLDataRange;
    this.converterOWLPropertyExpression = converterOWLPropertyExpression;
    propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);
  }

  @Override
  public NLGElement visit(final OWLClass ce) {
    parameter.noun = true;

    // get the lexical form
    String lexicalForm = getLexicalForm(ce).toLowerCase();

    // we always start with the singular form and if necessary pluralize later
    lexicalForm = PlingStemmer.stem(lexicalForm);

    if (parameter.isSubClassExpression) {// subclass expression
      if (ce.isOWLThing()) {
        if (parameter.modalDepth == 1) {
          final NLGElement word = nlgFactory.createWord("everything", LexicalCategory.NOUN);
          word.setFeature(InternalFeature.NON_MORPH, true);
          return nlgFactory.createNounPhrase(word);
        } else {
          final NLGElement word = nlgFactory.createWord("something", LexicalCategory.NOUN);
          word.setFeature(InternalFeature.NON_MORPH, true);
          return nlgFactory.createNounPhrase(word);
        }
      }
      final NPPhraseSpec nounPhrase = nlgFactory.createNounPhrase(lexicalForm);
      if (parameter.modalDepth > 1 && !ce.equals(parameter.root)) {
        nounPhrase.setDeterminer("a");
      } else {
        nounPhrase.setPreModifier("every");
      }
      return nounPhrase;
    } else {// superclass expression
      if (ce.isOWLThing()) {
        return nlgFactory.createNounPhrase("something");
      }
      return nlgFactory.createNounPhrase("a", lexicalForm);
    }
  }

  @Override
  public NLGElement visit(final OWLObjectIntersectionOf ce) {
    final List<OWLClassExpression> operands = getOperandsByPriority(ce);

    // process first class
    final OWLClassExpression first = operands.remove(0);
    final SPhraseSpec phrase = nlgFactory.createClause();
    final NPPhraseSpec firstElement = (NPPhraseSpec) first.accept(this);
    phrase.setSubject(firstElement);
    if (operands.size() >= 2) {
      final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();

      // process the classes
      final Iterator<OWLClassExpression> iterator = operands.iterator();
      final List<OWLClass> classes = new ArrayList<>();
      while (iterator.hasNext()) {
        final OWLClassExpression operand = iterator.next();
        if (!operand.isAnonymous()) {
          classes.add(operand.asOWLClass());
          iterator.remove();
        }
      }
      for (final OWLClass cls : classes) {
        final SPhraseSpec clause = nlgFactory.createClause("that", "is");
        clause.setObject(cls.accept(this));
        cc.addCoordinate(clause);
      }

      // process the rest
      for (final OWLClassExpression operand : operands) {
        final SPhraseSpec clause = nlgFactory.createClause();
        final NLGElement el = operand.accept(this);
        if (parameter.noun) {
          clause.setSubject("whose");
          clause.setVerbPhrase(el);
        } else {
          clause.setSubject("that");
          clause.setVerbPhrase(el);
        }
        cc.addCoordinate(clause);
      }

      phrase.setVerbPhrase(cc);
    } else {
      final OWLClassExpression operand = operands.get(0);
      final SPhraseSpec clause = nlgFactory.createClause();
      final NLGElement el = operand.accept(this);
      if (parameter.noun) {
        if (operand instanceof OWLObjectSomeValuesFrom
            && ((OWLObjectSomeValuesFrom) operand).getFiller().isOWLThing()) {
          // clause.setFeature(Feature.COMPLEMENTISER, "that");
          clause.setVerb("have");
          clause.setObject(el);
        } else {
          clause.setFeature(Feature.COMPLEMENTISER, "whose");
          clause.setVerbPhrase(el);
        }

      } else {
        clause.setFeature(Feature.COMPLEMENTISER, "that");
        clause.setVerbPhrase(el);
      }
      phrase.setComplement(clause);
    }

    // LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectUnionOf ce) {
    final List<OWLClassExpression> operands = getOperandsByPriority(ce);

    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    cc.setConjunction("or");

    for (final OWLClassExpression operand : operands) {
      final NLGElement el = operand.accept(this);
      cc.addCoordinate(el);
    }

    LOG.debug(ce + " = " + realiser.realise(cc));

    return cc;
  }

  @Override
  public NLGElement visit(final OWLObjectComplementOf ce) {
    final OWLClassExpression op = ce.getOperand();

    NLGElement phrase = op.accept(this);
    if (!op.isAnonymous()) {
      phrase = nlgFactory.createClause(null, "is", phrase);
    }

    phrase.setFeature(Feature.NEGATED, true);

    parameter.noun = false;

    // LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectSomeValuesFrom ce) {
    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      String verbalizationText = propertyVerbalization.getVerbalizationText();
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase =
            nlgFactory.createNounPhrase(PlingStemmer.stem(verbalizationText));

        if (filler.isOWLThing()) {
          propertyNounPhrase.setDeterminer("a");
          return propertyNounPhrase;
        }
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = filler.accept(this);
        fillerElement.setPlural(false);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        // phrase.setVerb(propertyVerbalization.getVerbalizationText());

        final String[] posTags = propertyVerbalization.getPOSTags().split(" ");
        final String firstTag = posTags[0];
        final String secondTag = posTags[1];

        if (firstTag.startsWith("V") && secondTag.startsWith("N")) {
          // if(tokens[0].equals("has") || tokens[0].equals("have")){
          final String[] tokens = verbalizationText.split(" ");

          verbalizationText = tokens[0];

          if (!filler.isOWLThing()) {
            verbalizationText += " as";
          } else {
            verbalizationText += " a";
          }

          // stem the noun
          // TODO to absolutely correct we have to stem the noun phrase
          String nounToken = tokens[1];
          nounToken = PlingStemmer.stem(nounToken);
          verbalizationText += " " + nounToken;

          // append rest of the tokens
          for (int i = 2; i < tokens.length; i++) {
            verbalizationText += " " + tokens[i];
          }
          verbalizationText = verbalizationText.trim();

          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          phrase.setVerb(verb);

          if (!filler.isOWLThing()) {
            final NLGElement fillerElement = filler.accept(this);
            phrase.setObject(fillerElement);
            fillerElement.setFeature(Feature.COMPLEMENTISER, null);
          }
        } else {
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          phrase.setVerb(verb);

          final NLGElement fillerElement = filler.accept(this);
          phrase.setObject(fillerElement);
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        parameter.noun = false;
      } else {

      }

    } else {
      // TODO handle inverse properties
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectAllValuesFrom ce) {
    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      String verbalizationText = propertyVerbalization.getVerbalizationText();
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = filler.accept(this);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        final String[] posTags = propertyVerbalization.getPOSTags().split(" ");
        final String firstTag = posTags[0];
        final String secondTag = posTags[1];

        if (firstTag.startsWith("V") && secondTag.startsWith("N")) {
          // if(tokens[0].equals("has") || tokens[0].equals("have")){
          final String[] tokens = verbalizationText.split(" ");

          verbalizationText = tokens[0];

          if (!filler.isOWLThing()) {
            verbalizationText += " as";
          } else {
            verbalizationText += " a";
          }

          // stem the noun
          // TODO to be absolutely correct, we have to stem the noun phrase
          String nounToken = tokens[1];
          nounToken = PlingStemmer.stem(nounToken);
          verbalizationText += " " + nounToken;

          // append rest of the tokens
          for (int i = 2; i < tokens.length; i++) {
            verbalizationText += " " + tokens[i];
          }
          verbalizationText += " only";
          verbalizationText = verbalizationText.trim();

          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          phrase.setVerb(verb);

          if (!filler.isOWLThing()) {
            final NLGElement fillerElement = filler.accept(this);
            phrase.setObject(fillerElement);
            fillerElement.setFeature(Feature.COMPLEMENTISER, null);
          }
        } else {
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          verb.addModifier("only");
          phrase.setVerb(verb);

          final NLGElement fillerElement = filler.accept(this);
          phrase.setObject(fillerElement);
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        parameter.noun = false;
      } else {

      }
    } else {

    }
    LOG.debug(ce + " = " + realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasValue ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLIndividual value = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = value.accept(converterOWLIndividual);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(propertyVerbalization.getVerbalizationText());

        final NLGElement fillerElement = value.accept(converterOWLIndividual);
        phrase.setObject(fillerElement);

        parameter.noun = false;
      } else {

      }
    } else {
      // TODO handle inverse properties
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectMinCardinality ce) {
    return processObjectCardinalityRestriction(ce, "at least");
  }

  @Override
  public NLGElement visit(final OWLObjectMaxCardinality ce) {
    return processObjectCardinalityRestriction(ce, "at most");
  }

  @Override
  public NLGElement visit(final OWLObjectExactCardinality ce) {
    return processObjectCardinalityRestriction(ce, "exactly");
  }

  private NLGElement processObjectCardinalityRestriction(final OWLObjectCardinalityRestriction ce,
      String modifier) {

    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    modifier += " " + cardinality;

    if (!property.isAnonymous()) {

      final PropertyVerbalization propertyVerbalization = verbalize(property);

      if (propertyVerbalization.isNounType()) {

        final NLGElement word = nlgFactory.createWord(
            PlingStemmer.stem(propertyVerbalization.getVerbalizationText()), LexicalCategory.NOUN);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(word);
        if (cardinality > 1) {
          word.setPlural(true);
          propertyNounPhrase.setPlural(true);
        }
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase("have");
        verb.addModifier(modifier);

        phrase.setVerb(verb);
        phrase.setObject(propertyNounPhrase);

        final NLGElement fillerElement = filler.accept(this);

        final SPhraseSpec clause = nlgFactory.createClause();
        if (cardinality > 1) {
          clause.setPlural(true);
        }
        clause.setVerb("be");
        clause.setObject(fillerElement);
        if (fillerElement.isA(PhraseCategory.CLAUSE)) {
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        phrase.setComplement(clause);

        parameter.noun = false;
      } else if (propertyVerbalization.isVerbType()) {

        String verbalizationText = propertyVerbalization.getVerbalizationText();

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
        final String[] posTags = propertyVerbalization.getPOSTags().split(" ");
        final String firstTag = posTags[0];
        final String secondTag = posTags[1];

        if (firstTag.startsWith("V") && secondTag.startsWith("N")) {
          // if(tokens[0].equals("has") || tokens[0].equals("have")){
          final String[] tokens = verbalizationText.split(" ");

          verbalizationText = tokens[0];
          verbalizationText += " " + modifier;

          // stem the noun if card == 1
          String nounToken = tokens[1];
          if (cardinality == 1) {
            nounToken = PlingStemmer.stem(nounToken);
          }
          verbalizationText += " " + nounToken;
          for (int i = 2; i < tokens.length; i++) {
            verbalizationText += " " + tokens[i];
          }
          verbalizationText = verbalizationText.trim();
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          phrase.setVerb(verb);
        } else {
          final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
          verb.addModifier(modifier);
          phrase.setVerb(verb);
        }

        if (!filler.isOWLThing()) {
          final NLGElement fillerElement = filler.accept(this);
          if (cardinality > 1) {
            fillerElement.setPlural(true);
          }
          phrase.setObject(fillerElement);
        }
        parameter.noun = false;
      } else {
        LOG.debug("TODO: handle other types");
      }
    } else {
      LOG.debug("property is anonymous and not handled yet.");
      // final OWLObjectPropertyExpression e = property.getInverseProperty();
      // LOG.info("{},{}", e.getInverseProperty(), property);
      // e.accept(converterOWLPropertyExpression);
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasSelf ce) {
    LOG.debug("TODO: handle OWLObjectHasSelf");
    return null;
  }

  @Override
  public NLGElement visit(final OWLObjectOneOf ce) {
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
    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {

      if (!filler.isTopDatatype()) {
        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        phrase.setObject(fillerElement);
      }

      final PropertyVerbalization propertyVerbalization = verbalize(property);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("be");

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(propertyVerbalization.getVerbalizationText());

        parameter.noun = false;
      } else {
        LOG.debug("TODO: handle other types");
      }
    } else {
      LOG.info("TODO: handle inverse properties");
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataAllValuesFrom ce) {
    parameter.modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        phrase.setObject(fillerElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {

        if (filler.isDatatype()
            && filler.asOWLDatatype().getIRI().equals(OWL2Datatype.XSD_BOOLEAN.getIRI())) {
          // "either VERB or not"
          final VPPhraseSpec verb =
              nlgFactory.createVerbPhrase(propertyVerbalization.getVerbalizationText());
          phrase.setVerb(verb);
          verb.addFrontModifier("either");
          verb.addPostModifier("or not");
        } else {
          final VPPhraseSpec verb =
              nlgFactory.createVerbPhrase(propertyVerbalization.getVerbalizationText());
          verb.addModifier("only");
          phrase.setVerb(verb);

          final NLGElement fillerElement = filler.accept(converterOWLDataRange);
          phrase.setObject(fillerElement);
        }

        parameter.noun = false;
      } else {
        LOG.debug("TODO: handle other types");
      }

    } else {
      LOG.info("TODO: handle inverse properties");
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));
    parameter.modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataHasValue ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLLiteral value = ce.getFiller();

    LOG.info("value: {}", value.toString());

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      final String verbalizationText = propertyVerbalization.getVerbalizationText();
      if (propertyVerbalization.isNounType()) {
        // verbalizationText = PlingStemmer.stem(verbalizationText);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(verbalizationText);
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement valueElement =
            nlgFactory.createNounPhrase(literalConverter.convert(value));
        phrase.setObject(valueElement);

        parameter.noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        // if phrase starts with something like 'is' and value is a Boolean
        final String[] tokens = verbalizationText.split(" ");
        if (value.getDatatype().isBoolean() && tokens[0].equals("is")) {
          if (!value.parseBoolean()) {
            phrase.setFeature(Feature.NEGATED, true);
          }
        } else {
          final NLGElement valueElement =
              nlgFactory.createNounPhrase(literalConverter.convert(value));
          phrase.setObject(valueElement);
        }

        phrase.setVerb(verbalizationText);

        parameter.noun = false;
      } else {
        LOG.debug("TODO: handle other types");
      }
    } else {
      LOG.info("TODO: handle inverse properties");
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataMinCardinality ce) {
    return processDataCardinalityRestriction(ce, "at least");
  }

  @Override
  public NLGElement visit(final OWLDataExactCardinality ce) {
    return processDataCardinalityRestriction(ce, "exactly");
  }

  @Override
  public NLGElement visit(final OWLDataMaxCardinality ce) {
    return processDataCardinalityRestriction(ce, "at most");
  }

  private NLGElement processDataCardinalityRestriction(final OWLDataCardinalityRestriction ce,
      final String modifier) {

    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization = verbalize(property);
      if (propertyVerbalization.isNounType()) {
        final NLGElement word = nlgFactory.createWord(
            PlingStemmer.stem(propertyVerbalization.getVerbalizationText()), LexicalCategory.NOUN);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(word);
        if (cardinality > 1) {
          word.setPlural(true);
          propertyNounPhrase.setPlural(true);
        }
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase("have");
        verb.addModifier(modifier + " " + cardinality);

        phrase.setVerb(verb);
        phrase.setObject(propertyNounPhrase);

        final NLGElement fillerElement = filler.accept(converterOWLDataRange);

        final SPhraseSpec clause = nlgFactory.createClause();
        if (cardinality > 1) {
          clause.setPlural(true);
        }
        clause.setVerb("be");
        clause.setObject(fillerElement);
        if (fillerElement.isA(PhraseCategory.CLAUSE)) {
          fillerElement.setFeature(Feature.COMPLEMENTISER, null);
        }

        phrase.setComplement(clause);

        parameter.noun = false;
      } else if (propertyVerbalization.isVerbType()) {
        final VPPhraseSpec verb =
            nlgFactory.createVerbPhrase(propertyVerbalization.getVerbalizationText());
        verb.addModifier(modifier + " " + cardinality);
        phrase.setVerb(verb);

        final NLGElement fillerElement = filler.accept(converterOWLDataRange);
        fillerElement.setPlural(true);
        phrase.setObject(fillerElement);

        parameter.noun = false;
      } else {
        LOG.debug("TODO: handle other types");
      }
    } else {
      LOG.debug("TODO: handle inverse properties");
    }
    LOG.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  /**
   * Returns a list of operands ordered by class expressions types, starting with the "more easy"
   * first.
   *
   * @param ce the class expression
   * @return a list of operands
   */
  private List<OWLClassExpression> getOperandsByPriority(final OWLNaryBooleanClassExpression ce) {
    return ce.getOperandsAsList();
  }

  private PropertyVerbalization verbalize(final OWLObjectPropertyExpression p) {
    return verbalize(p.asOWLObjectProperty().getIRI());
  }

  private PropertyVerbalization verbalize(final OWLDataPropertyExpression p) {
    return verbalize(p.asOWLDataProperty().getIRI());
  }

  private PropertyVerbalization verbalize(final IRI p) {
    return propertyVerbalizer.verbalize(p.toString());
  }
}
