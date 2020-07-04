package org.aksw.owl2nl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.aksw.triple2nl.nlp.stemming.PlingStemmer;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseCategory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 *
 */
public class OWLClassExpressionConverter implements OWLClassExpressionVisitorEx<NLGElement>,
    OWLIndividualVisitorEx<NLGElement>, OWLDataRangeVisitorEx<NLGElement> {

  private static final Logger logger = LoggerFactory.getLogger(OWLClassExpressionConverter.class);

  NLGFactory nlgFactory;
  Realiser realiser;

  IRIConverter iriConverter = new SimpleIRIConverter();
  PropertyVerbalizer propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);
  LiteralConverter literalConverter = new LiteralConverter(iriConverter);
  OWLDataFactory df = new OWLDataFactoryImpl();

  boolean noun;

  NLGElement object;
  NLGElement complement;

  int modalDepth;

  OWLClassExpression root;

  private boolean isSubClassExpression;

  public OWLClassExpressionConverter(final Lexicon lexicon) {
    nlgFactory = new NLGFactory(lexicon);
    realiser = new Realiser(lexicon);
  }

  public OWLClassExpressionConverter() {
    this(Lexicon.getDefaultLexicon());
  }

  public String convert(final OWLClassExpression ce) {
    // process
    NLGElement nlgElement = asNLGElement(ce);

    // realise
    nlgElement = realiser.realise(nlgElement);

    return nlgElement.getRealisation();
  }

  public NLGElement asNLGElement(final OWLClassExpression ce) {
    return asNLGElement(ce, false);
  }

  public NLGElement asNLGElement(OWLClassExpression ce, final boolean isSubClassExpression) {
    root = ce;
    this.isSubClassExpression = isSubClassExpression;
    // reset modal depth
    modalDepth = 1;

    // rewrite class expression
    ce = rewrite(ce);

    // process
    final NLGElement nlgElement = ce.accept(this);

    return nlgElement;
  }

  private String getLexicalForm(final OWLEntity entity) {
    return iriConverter.convert(entity.toStringID());
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

  @Override
  public NLGElement visit(final OWLClass ce) {
    noun = true;

    // get the lexical form
    String lexicalForm = getLexicalForm(ce).toLowerCase();

    // we always start with the singular form and if necessary pluralize later
    lexicalForm = PlingStemmer.stem(lexicalForm);

    if (isSubClassExpression) {// subclass expression
      if (ce.isOWLThing()) {
        if (modalDepth == 1) {
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
      if (modalDepth > 1 && !ce.equals(root)) {
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
        if (noun) {
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
      if (noun) {
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

    logger.debug(ce + " = " + realiser.realise(phrase));

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

    logger.debug(ce + " = " + realiser.realise(cc));

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

    noun = false;

    logger.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectSomeValuesFrom ce) {
    modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLObjectProperty().getIRI().toString());
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

        noun = true;
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

        noun = false;
      } else {

      }

    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));
    modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectAllValuesFrom ce) {
    modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLObjectProperty().getIRI().toString());
      String verbalizationText = propertyVerbalization.getVerbalizationText();
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = filler.accept(this);
        phrase.setObject(fillerElement);

        noun = true;
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

        noun = false;
      } else {

      }
    } else {

    }
    logger.debug(ce + " = " + realiser.realise(phrase));
    modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasValue ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLIndividual value = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLObjectProperty().getIRI().toString());
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = value.accept(this);
        phrase.setObject(fillerElement);

        noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(propertyVerbalization.getVerbalizationText());

        final NLGElement fillerElement = value.accept(this);
        phrase.setObject(fillerElement);

        noun = false;
      } else {

      }
    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectMinCardinality ce) {
    return processObjectCardinalityRestriction(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectMaxCardinality ce) {
    return processObjectCardinalityRestriction(ce);
  }

  @Override
  public NLGElement visit(final OWLObjectExactCardinality ce) {
    return processObjectCardinalityRestriction(ce);
  }

  private NLGElement processObjectCardinalityRestriction(final OWLObjectCardinalityRestriction ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();
    String modifier;
    if (ce instanceof OWLObjectMinCardinality) {
      modifier = "at least";
    } else if (ce instanceof OWLObjectMaxCardinality) {
      modifier = "at most";
    } else {
      modifier = "exactly";
    }

    final OWLObjectPropertyExpression property = ce.getProperty();
    final OWLClassExpression filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    modifier += " " + cardinality;

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLObjectProperty().getIRI().toString());

      if (propertyVerbalization.isNounType()) { // if the verbalization of the property is a noun
                                                // phrase
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

        noun = false;
      } else if (propertyVerbalization.isVerbType()) { // if the verbalization of the property is a
                                                       // verb phrase

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
        noun = false;
      } else {

      }
    } else {

    }
    logger.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLObjectHasSelf ce) {
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
    return individuals.iterator().next().accept(this);
  }

  @Override
  public NLGElement visit(final OWLDataSomeValuesFrom ce) {
    modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {

      if (!filler.isTopDatatype()) {
        final NLGElement fillerElement = filler.accept(this);
        phrase.setObject(fillerElement);
      }

      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLDataProperty().getIRI().toString());
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("be");

        noun = true;
      } else if (propertyVerbalization.isVerbType()) {
        phrase.setVerb(propertyVerbalization.getVerbalizationText());

        noun = false;
      } else {

      }
    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));
    modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataAllValuesFrom ce) {
    modalDepth++;
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLDataProperty().getIRI().toString());
      if (propertyVerbalization.isNounType()) {
        final NPPhraseSpec propertyNounPhrase = nlgFactory
            .createNounPhrase(PlingStemmer.stem(propertyVerbalization.getVerbalizationText()));
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement fillerElement = filler.accept(this);
        phrase.setObject(fillerElement);

        noun = true;
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

          final NLGElement fillerElement = filler.accept(this);
          phrase.setObject(fillerElement);
        }

        noun = false;
      } else {

      }

    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));
    modalDepth--;
    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataHasValue ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLLiteral value = ce.getFiller();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLDataProperty().getIRI().toString());
      final String verbalizationText = propertyVerbalization.getVerbalizationText();
      if (propertyVerbalization.isNounType()) {
        // verbalizationText = PlingStemmer.stem(verbalizationText);
        final NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(verbalizationText);
        phrase.setSubject(propertyNounPhrase);

        phrase.setVerb("is");

        final NLGElement valueElement =
            nlgFactory.createNounPhrase(literalConverter.convert(value));
        phrase.setObject(valueElement);

        noun = true;
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

        noun = false;
      } else {

      }

    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  @Override
  public NLGElement visit(final OWLDataMinCardinality ce) {
    return processDataCardinalityRestriction(ce);
  }

  @Override
  public NLGElement visit(final OWLDataExactCardinality ce) {
    return processDataCardinalityRestriction(ce);
  }

  @Override
  public NLGElement visit(final OWLDataMaxCardinality ce) {
    return processDataCardinalityRestriction(ce);
  }

  private NLGElement processDataCardinalityRestriction(final OWLDataCardinalityRestriction ce) {
    final SPhraseSpec phrase = nlgFactory.createClause();
    String modifier;
    if (ce instanceof OWLDataMinCardinality) {
      modifier = "at least";
    } else if (ce instanceof OWLDataMaxCardinality) {
      modifier = "at most";
    } else {
      modifier = "exactly";
    }

    final OWLDataPropertyExpression property = ce.getProperty();
    final OWLDataRange filler = ce.getFiller();
    final int cardinality = ce.getCardinality();

    if (!property.isAnonymous()) {
      final PropertyVerbalization propertyVerbalization =
          propertyVerbalizer.verbalize(property.asOWLDataProperty().getIRI().toString());
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

        noun = false;
      } else if (propertyVerbalization.isVerbType()) {
        final VPPhraseSpec verb =
            nlgFactory.createVerbPhrase(propertyVerbalization.getVerbalizationText());
        verb.addModifier(modifier + " " + cardinality);
        phrase.setVerb(verb);

        final NLGElement fillerElement = filler.accept(this);
        fillerElement.setPlural(true);
        phrase.setObject(fillerElement);

        noun = false;
      } else {

      }

    } else {
      // TODO handle inverse properties
    }
    logger.debug(ce + " = " + realiser.realise(phrase));

    return phrase;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLIndividualVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLNamedIndividual)
   */
  @Override
  public NLGElement visit(final OWLNamedIndividual individual) {
    return nlgFactory.createNounPhrase(getLexicalForm(individual));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLIndividualVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLAnonymousIndividual)
   */
  @Override
  public NLGElement visit(final OWLAnonymousIndividual individual) {
    throw new UnsupportedOperationException(
        "Convertion of anonymous individuals not supported yet!");
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

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataOneOf)
   */
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

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLDataRangeVisitorEx#visit(org.semanticweb.owlapi.model.
   * OWLDataComplementOf)
   */
  @Override
  public NLGElement visit(final OWLDataComplementOf node) {
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
    final CoordinatedPhraseElement cc = nlgFactory.createCoordinatedPhrase();
    cc.setConjunction("or");

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
