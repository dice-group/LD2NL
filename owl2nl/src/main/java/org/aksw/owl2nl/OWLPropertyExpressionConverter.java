package org.aksw.owl2nl;

import com.github.andrewoma.dexx.collection.internal.base.Break;
import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.converter.SimpleIRIConverter;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class OWLPropertyExpressionConverter implements OWLPropertyExpressionVisitorEx<NLGElement> {
    private static final Logger logger = LoggerFactory.getLogger(OWLClassExpressionConverter.class);

    NLGFactory nlgFactory;
    Realiser realiser;

    IRIConverter iriConverter = new SimpleIRIConverter();
    PropertyVerbalizer propertyVerbalizer = new PropertyVerbalizer(iriConverter, null);

    OWLObjectPropertyExpression root;

    //The flag is true if the transitive object property is getting called
    private boolean isTransitiveObjectProperty;
    //To count the iterations for transitive object property as
    //it requires three different subject-object combinations
    private int countTransitive;

    public OWLPropertyExpressionConverter(Lexicon lexicon) {
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);

        isTransitiveObjectProperty = false;
        countTransitive = 0;
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

    public NLGElement asNLGElement(
            OWLObjectPropertyExpression pe,
            boolean isTransitiveObjectProperty) {
                this.root = pe;
        this.isTransitiveObjectProperty = isTransitiveObjectProperty;

        NLGElement nlgElement = pe.accept(this);

        return nlgElement;
    }

    private String getLexicalForm(OWLEntity entity){

        return iriConverter.convert(entity.toStringID());
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLObjectProperty pe) {
        SPhraseSpec phrase = null;

        if(isTransitiveObjectProperty==true) {
            switch (countTransitive){
                case 0: phrase = getSentencePhraseFromProperty(pe, "X", "Y");
                    break;

                case 1: phrase = getSentencePhraseFromProperty(pe, "Y", "Z");
                    break;

                case 2: phrase = getSentencePhraseFromProperty(pe, "X", "Z");
                    isTransitiveObjectProperty = false;
                    countTransitive = 0;
                    break;
            }
            countTransitive++;
        }
        else {
                phrase = getSentencePhraseFromProperty(pe, "X", "Y");
        }

        return phrase;
    }

    @NotNull
    @Override
    public NLGElement visit(@NotNull OWLObjectInverseOf owlObjectInverseOf) {
        OWLObjectProperty property = owlObjectInverseOf.getInverse().getNamedProperty();
        SPhraseSpec phrase = getSentencePhraseFromProperty(property, "Y", "X");

        return phrase;
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

    private SPhraseSpec getSentencePhraseFromProperty(
            OWLObjectProperty pe,
            String subject,
            String object) {
        SPhraseSpec phrase = nlgFactory.createClause();

        if(!pe.isAnonymous()){
            PropertyVerbalization propertyVerbalization = propertyVerbalizer.verbalize(
                    pe.getIRI().toString());
            String verbalizationText = propertyVerbalization.getVerbalizationText();
            if(propertyVerbalization.isNounType()){
                phrase.setSubject(subject);
                phrase.setVerb("is");
                phrase.setObject(verbalizationText);
//                noun = true;
            } else if(propertyVerbalization.isVerbType()){
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
                phrase.setVerb(verb); // Issues with verbs like 'doneBy'
                phrase.setSubject(subject);
                phrase.setObject(object);
//                noun = false;
            }
        }

        return phrase;
    }
}
