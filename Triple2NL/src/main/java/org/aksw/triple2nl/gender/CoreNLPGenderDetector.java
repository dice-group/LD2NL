/*-
 * #%L
 * Triple2NL
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
package org.aksw.triple2nl.gender;
//package org.aksw.sw2pt.triple2nl.gender;
//
//import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.GenderAnnotator;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.util.CoreMap;
//import org.aksw.sw2pt.triple2nl.nlp.StanfordCoreNLPWrapper;
//
//import java.util.Properties;
//
///**
// * @author Lorenz Buehmann
// */
//public class CoreNLPGenderDetector implements GenderDetector {
//
//	private final StanfordCoreNLPWrapper pipeline;
//
//	public CoreNLPGenderDetector(StanfordCoreNLPWrapper pipeline) {
//		this.pipeline = pipeline;
//		pipeline.addAnnotator(new GenderAnnotator());
//	}
//
//	public CoreNLPGenderDetector() {
//		Properties props = new Properties();
//		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,gender");
//		props.put("ssplit.isOneSentence","true");
//
//		pipeline = new StanfordCoreNLPWrapper(new StanfordCoreNLP(props));
//	}
//
//
//
//	@Override
//	public Gender getGender(String name) {
//		Annotation document = new Annotation(name);
//
//		pipeline.annotate(document);
//
//		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//				String gender = token.get(MachineReadingAnnotations.GenderAnnotation.class);
////				System.out.println(token + ":" + gender);
//				if(gender != null) {
//					if(gender.equals("MALE")) {
//						return Gender.MALE;
//					} else if(gender.equals("FEMALE")) {
//						return Gender.FEMALE;
//					}
//				}
//			}
//		}
//		return Gender.UNKNOWN;
//	}
//}
