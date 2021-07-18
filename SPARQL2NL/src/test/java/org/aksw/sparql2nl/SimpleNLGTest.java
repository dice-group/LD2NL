/*-
 * #%L
 * SPARQL2NL
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
/**
 * 
 */
package org.aksw.sparql2nl;

import static org.junit.Assert.*;

import org.aksw.sparql2nl.naturallanguagegeneration.TriplePatternConverter;
import org.aksw.triple2nl.TripleConverter;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.junit.BeforeClass;
import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.NIHDBLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

/**
 * @author Lorenz Buehmann
 *
 */
public class SimpleNLGTest {
	
	
	private static Lexicon lexicon;
	private static NLGFactory nlgFactory;
	private static Realiser realiser;

	@BeforeClass
	public static void setUp(){
		lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);
	}
	

	@Test
	public void testDefaultLexicon() {
		String cls = "airport";
		NLGElement word = nlgFactory.createWord(cls, LexicalCategory.NOUN);
		NLGElement nounPhrase = nlgFactory.createNounPhrase(word);
		System.out.println(nounPhrase.getAllFeatures());
		System.out.println(nounPhrase.getRealisation());
		nounPhrase.setFeature(Feature.POSSESSIVE, true);
		nounPhrase = realiser.realise(nounPhrase);
		System.out.println(nounPhrase.getAllFeatures());
		System.out.println(nounPhrase.getRealisation());
		
		word = nlgFactory.createWord(cls, LexicalCategory.NOUN);
		nounPhrase = nlgFactory.createNounPhrase(word);
		nounPhrase = realiser.realise(nounPhrase);
		System.out.println(nounPhrase.getAllFeatures());
		System.out.println(nounPhrase.getRealisation());
	}
	
	@Test
	public void testNIHLexicon() {
		String cls = "airport";
		Lexicon lexicon = new NIHDBLexicon("../SPARQL2NL/src/main/resources/NIHLexicon/lexAccess2013.data");
		NLGFactory nlgFactory = new NLGFactory(lexicon);
		Realiser realiser = new Realiser(lexicon);
		NLGElement word = nlgFactory.createWord(cls, LexicalCategory.NOUN);
		NLGElement nounPhrase = nlgFactory.createNounPhrase(word);
		nounPhrase = realiser.realise(nounPhrase);
		System.out.println(nounPhrase.getAllFeatures());
		System.out.println(nounPhrase.getRealisation());
	}
	
	@Test
	public void testRelativeClause(){
		SPhraseSpec cl1 = nlgFactory.createClause(null, "have", "system of government Republic");
		NPPhraseSpec np1 = nlgFactory.createNounPhrase("states");
		np1.addComplement(cl1);
		System.out.println(realiser.realise(np1));
		
		SPhraseSpec cl2 = nlgFactory.createClause(null, "be located in", nlgFactory.createClause(np1, null));
		NPPhraseSpec np2 = nlgFactory.createNounPhrase("cities");
		np2.addComplement(cl2);
		System.out.println(realiser.realise(np2));
		
		Triple t = new Triple(
				NodeFactory.createVariable("city"),
				NodeFactory.createURI("http://dbpedia.org/ontology/locatedIn"),
				NodeFactory.createVariable("state"));
		
		TriplePatternConverter conv = new TriplePatternConverter(SparqlEndpoint.getEndpointDBpedia(), "cache", null);
		NPPhraseSpec cl3 = conv.convertTriplePattern(t, nlgFactory.createNounPhrase("cities"), np1, true, false, false);
		System.out.println(realiser.realise(cl3));
        
        NPPhraseSpec np = nlgFactory.createNounPhrase(nlgFactory.createWord("darts player", LexicalCategory.NOUN));
        np.setPlural(true);
        System.out.println(realiser.realise(np));
	}
	
	@Test
	public void testPossessive() throws Exception {
		NPPhraseSpec sisterNP = nlgFactory.createNounPhrase("sister");
		NLGElement word = nlgFactory.createWord("Albert Einstein", LexicalCategory.NOUN);
		word.setFeature(LexicalFeature.PROPER, true);
		NPPhraseSpec possNP = nlgFactory.createNounPhrase(word);
		possNP.setFeature(Feature.POSSESSIVE, true);
		sisterNP.setSpecifier(possNP);
		System.out.println(realiser.realise(sisterNP));
		sisterNP.setPlural(true);
		System.out.println(realiser.realise(sisterNP));
		sisterNP.setPlural(false);
		possNP.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        possNP.setFeature(Feature.PRONOMINAL, true);
        System.out.println(realiser.realise(sisterNP));
        possNP.setPlural(false);
        sisterNP.setPlural(true);
        System.out.println(realiser.realise(sisterNP));
	}

}
