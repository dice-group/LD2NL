/*-
 * #%L
 * AVATAR
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.avatar.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.avatar.gender.Gender;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import simplenlg.aggregation.Aggregator;
import simplenlg.aggregation.ForwardConjunctionReductionRule;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 *
 * @author ngonga
 */
public class SubjectMergeRule {

   
    Lexicon lexicon;
    NLGFactory nlgFactory;
    Realiser realiser;

    public SubjectMergeRule(Lexicon lexicon, NLGFactory nlgFactory, Realiser realiser) {
		this.lexicon = lexicon;
		this.nlgFactory = nlgFactory;
		this.realiser = realiser;
	}

    /**
     * Checks whether a rule is applicable and returns the number of pairs on
     * which it can be applied
     *
     * @param phrases List of phrases
     * @return Number of mapping pairs
     */
	public int isApplicable(List<SPhraseSpec> phrases) {
        int count = 0;
        SPhraseSpec p1, p2;
        String subj1, subj2;

        for (int i = 0; i < phrases.size(); i++) {
            p1 = phrases.get(i);
            subj1 = p1.getSubject().getRealisation();
            for (int j = i + 1; j < phrases.size(); j++) {
                p2 = phrases.get(j);
                subj2 = p2.getSubject().getRealisation();
                if (subj1.equals(subj2)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Applies this rule to the phrases Returns a list of either
     * CoordinatedPhraseElement or SPhraseSpec
     *
     * @param phrases Set of phrases
     * @return Result of the rule being applied
     */
    public List<NLGElement> apply(List<SPhraseSpec> phrases, Gender male) {

        if (phrases.size() <= 1) {
            List<NLGElement> result = new ArrayList<NLGElement>();
            for (SPhraseSpec s : phrases) {
                result.add(s);
            }
            return result;
        }

        SPhraseSpec p1, p2;
        String subj1, subj2;
        
//        Aggregator aggregator = new Aggregator();
//		aggregator.initialise();
//        ForwardConjunctionReductionRule fcr = new ForwardConjunctionReductionRule();
//        System.err.println(realiser.realise(fcr.apply(phrases.get(0), phrases.get(1))));
        
        
        // get mapping subjects
        Multimap<Integer, Integer> map = TreeMultimap.create();
        for (int i = 0; i < phrases.size(); i++) {
            p1 = phrases.get(i);
            if (p1.getSubject().getFeatureAsElement(InternalFeature.SPECIFIER) != null) {
            	NLGElement specifier1 = p1.getSubject().getFeatureAsElement(InternalFeature.SPECIFIER);
                subj1 = realiser.realiseSentence(specifier1);
                for (int j = i + 1; j < phrases.size(); j++) {
                    p2 = phrases.get(j);
                    if (p2.getSubject().getFeatureAsElement(InternalFeature.SPECIFIER) != null) {
                    	NLGElement specifier2 = p2.getSubject().getFeatureAsElement(InternalFeature.SPECIFIER);
                        subj2 = realiser.realiseSentence(specifier2);
                        if (subj1.equals(subj2)) {
                            map.put(i, j);
                        }
                    }
                }
            }
        }
//        System.out.println(map);

        int maxSize = 0;
        int phraseIndex = -1;

        //find the index with the highest number of mappings
        List<Integer> phraseIndexes = new ArrayList<Integer>(map.keySet());
        for (int key = 0; key < phraseIndexes.size(); key++) {
            if (map.get(key).size() > maxSize) {
                maxSize = map.get(key).size();
                phraseIndex = key;
            }
        }

        if (phraseIndex == -1) {
            List<NLGElement> results = new ArrayList<NLGElement>();
            for (SPhraseSpec phrase : phrases) {
                results.add((NLGElement) phrase);
            }
            return results;
        }

        Collection<Integer> toMerge = map.get(phraseIndex);
        CoordinatedPhraseElement elt = nlgFactory.createCoordinatedPhrase();

        //change subject here
        phrases.get(phraseIndex).getSubject();


        elt.addCoordinate(phrases.get(phraseIndex));
        for (int index : toMerge) {
            if (male.equals(Gender.MALE)) {
                ((NPPhraseSpec) phrases.get(index).getSubject()).setSpecifier("his");

            } else if (male.equals(Gender.FEMALE)) {
                ((NPPhraseSpec) phrases.get(index).getSubject()).setSpecifier("her");
            } else {
                ((NPPhraseSpec) phrases.get(index).getSubject()).setSpecifier("its");
            }
//            np.setFeature(Feature.POSSESSIVE, true);
//            ((NPPhraseSpec) phrases.get(index).getSubject()).setPreModifier("her");
            elt.addCoordinate(phrases.get(index));
        }
        toMerge.add(phraseIndex);


        //now create the final result
        List<NLGElement> result = new ArrayList<NLGElement>();
        result.add(elt);
        for (int index = 0; index < phrases.size(); index++) {
            if (!toMerge.contains(index)) {
                result.add(phrases.get(index));
            }
        }
        return result;
    }

    public static void main(String args[]) {
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        NLGFactory nlgFactory = new NLGFactory(lexicon);
        Realiser realiser = new Realiser(lexicon);

        SPhraseSpec s1 = nlgFactory.createClause();
        NPPhraseSpec np1 = nlgFactory.createNounPhrase("mother");
        NPPhraseSpec subj1 = nlgFactory.createNounPhrase("Mike");
        subj1.setFeature(Feature.POSSESSIVE, true);
        np1.setSpecifier(subj1);
        s1.setSubject(np1);
        s1.setVerb("like");
        s1.setObject("apples");
        s1.getObject().setPlural(true);

        SPhraseSpec s2 = nlgFactory.createClause();
        NPPhraseSpec np2 = nlgFactory.createNounPhrase("father");
        NPPhraseSpec subj2 = nlgFactory.createNounPhrase("Mike");
        subj2.setFeature(Feature.POSSESSIVE, true);
        np2.setSpecifier(subj2);
        s2.setSubject(np2);

        s2.setVerb("eat");
        s2.setObject("apples");
        s2.getObject().setPlural(true);

        SPhraseSpec s3 = nlgFactory.createClause();
        s3.setSubject("John");
        s3.setVerb("be born in");
        s3.setObject("New York");
        s3.getObject().setPlural(true);

        List<SPhraseSpec> phrases = new ArrayList<SPhraseSpec>();
        phrases.add(s1);
        phrases.add(s2);
        phrases.add(s3);

        for (SPhraseSpec p : phrases) {
            System.out.println("=>" + realiser.realiseSentence(p));
        }
        List<NLGElement> phrases2 = (new SubjectMergeRule(lexicon, nlgFactory, realiser)).apply(phrases, Gender.MALE);

        for (NLGElement p : phrases2) {
            System.out.println("=>" + realiser.realiseSentence(p));
        }
        
        Aggregator aggregator = new Aggregator();
		aggregator.initialise();
        ForwardConjunctionReductionRule fcr = new ForwardConjunctionReductionRule();
        aggregator.addRule(fcr);
        NLGElement aggregatedElt = fcr.apply(phrases.get(0), phrases.get(1));
        System.out.println(aggregatedElt);
		System.err.println(realiser.realise(aggregatedElt));

    }
}
