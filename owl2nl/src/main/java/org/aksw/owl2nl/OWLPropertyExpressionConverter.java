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
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class OWLPropertyExpressionConverter implements OWLPropertyExpressionVisitorEx<NLGElement> {
    private static final Logger logger = LoggerFactory.getLogger(OWLClassExpressionConverter.class);

    NLGFactory nlgFactory;
    Realiser realiser;

    IRIConverter iriConverter = new SimpleIRIConverter();
    PropertyVerbalizer propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);

    boolean noun;
    boolean verb;

    OWLObjectPropertyExpression root;

    private boolean isSubObjectPropertyExpression;

    public OWLPropertyExpressionConverter(Lexicon lexicon) {
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    public OWLPropertyExpressionConverter() {
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

        NLGElement nlgElement = pe.accept(this);

        return nlgElement;
    }

    private String getLexicalForm(OWLEntity entity){
        return iriConverter.convert(entity.toStringID());
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLObjectProperty pe) {
        SPhraseSpec phrase = nlgFactory.createClause();

        if(!pe.isAnonymous()){
            PropertyVerbalization propertyVerbalization = propertyVerbalizer.verbalize(pe.getIRI().toString());
            String verbalizationText = propertyVerbalization.getVerbalizationText();
            if(propertyVerbalization.isNounType()){
                NPPhraseSpec propertyNounPhrase = nlgFactory.createNounPhrase(PlingStemmer.stem(verbalizationText));

                phrase.setSubject("X");
                phrase.setVerb("is");
                phrase.setObject(verbalizationText);
                noun = true;
            } else if(propertyVerbalization.isVerbType()){

                VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
                phrase.setVerb(verb); // Issues with verbs like 'doneBy'
                phrase.setSubject("X");
                phrase.setObject("Y");
                noun = false;
            }
        }
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
