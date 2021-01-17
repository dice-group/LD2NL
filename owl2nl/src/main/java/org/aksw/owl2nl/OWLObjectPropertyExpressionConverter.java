package org.aksw.owl2nl;

import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.aksw.triple2nl.nlp.stemming.PlingStemmer;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplenlg.features.Feature;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLObjectPropertyExpressionConverter implements OWLPropertyExpressionVisitorEx<NLGElement> {
    private static final Logger logger = LoggerFactory.getLogger(OWLClassExpressionConverter.class);

    NLGFactory nlgFactory;
    Realiser realiser;

    IRIConverter iriConverter = new SimpleIRIConverter();
    PropertyVerbalizer propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);

    boolean noun;

    int modalDepth;

    OWLObjectPropertyExpression root;

    private boolean isSubObjectPropertyExpression;

    public OWLObjectPropertyExpressionConverter(Lexicon lexicon) {
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    public OWLObjectPropertyExpressionConverter() {
        this(Lexicon.getDefaultLexicon());
    }


    public String convert(OWLObjectPropertyExpression pe) {
        // process
        NLGElement nlgElement = asNLGElement(pe);

        // realise
        nlgElement = realiser.realise(nlgElement);

        return nlgElement.getRealisation();
    }
    public NLGElement asNLGElement(OWLObjectPropertyExpression pe) {
        return asNLGElement(pe, false);
    }

    public NLGElement asNLGElement(OWLObjectPropertyExpression pe, boolean isSubObjectPropertyExpression) {
        this.root = pe;
        this.isSubObjectPropertyExpression = isSubObjectPropertyExpression;

        // reset modal depth
        modalDepth = 1;

        NLGElement nlgElement = pe.accept(this);

        return nlgElement;
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLObjectProperty owlObjectProperty) {
        logger.warn("Object property: " + owlObjectProperty);
        modalDepth++;
        SPhraseSpec phrase = nlgFactory.createClause();

        if(!owlObjectProperty.isAnonymous()){
            PropertyVerbalization propertyVerbalization = propertyVerbalizer.verbalize(owlObjectProperty.asOWLObjectProperty().getIRI().toString());
            String verbalizationText = propertyVerbalization.getVerbalizationText();
            if(propertyVerbalization.isNounType()){
                NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(PlingStemmer.stem(verbalizationText));

                phrase.setSubject(propertyNounPhrase);

                phrase.setVerb("is");
                noun = true;
            } else if(propertyVerbalization.isVerbType()){

                String[] posTags = propertyVerbalization.getPOSTags().split(" ");
                String firstTag = posTags[0];
                String secondTag = posTags[1];

                if(firstTag.startsWith("V") && secondTag.startsWith("N")){
                    String[] tokens = verbalizationText.split(" ");

                    verbalizationText = tokens[0];

                    verbalizationText += " a"; //TODO: use a or an based on noun

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

                    VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
                    phrase.setVerb(verb);

                } else {
                    VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
                    phrase.setVerb(verb);
                }

                noun = false;
            }
        }
        logger.debug(owlObjectProperty +  " = " + realiser.realise(phrase));
        modalDepth--;
        return phrase;
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLObjectInverseOf owlObjectInverseOf) {
        return null;
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLDataProperty owlDataProperty) {
        return null;
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLAnnotationProperty owlAnnotationProperty) {
        return null;
    }
}
