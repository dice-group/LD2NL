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
package org.aksw.triple2nl.property;

import static org.aksw.triple2nl.util.PennTreebankTagSet.DETERMINER;
import static org.aksw.triple2nl.util.PennTreebankTagSet.FRAGMENT;
import static org.aksw.triple2nl.util.PennTreebankTagSet.NOUN_PHRASE;
import static org.aksw.triple2nl.util.PennTreebankTagSet.S;
import static org.aksw.triple2nl.util.PennTreebankTagSet.SBAR;
import static org.aksw.triple2nl.util.PennTreebankTagSet.SBARQ;
import static org.aksw.triple2nl.util.PennTreebankTagSet.SINV;
import static org.aksw.triple2nl.util.PennTreebankTagSet.VERB_PHRASE;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.aksw.triple2nl.converter.IRIConverter;
import org.aksw.triple2nl.nlp.StanfordCoreNLPWrapper;
import org.aksw.triple2nl.util.Preposition;
import org.apache.log4j.Logger;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;
import simplenlg.features.Tense;

/**
 * Verbalize a property.
 * 
 * @author Lorenz Buehmann
 *
 */
public class PropertyVerbalizer {

  private static final Logger logger = Logger.getLogger(PropertyVerbalizer.class);

  private double threshold = 2.0;
  private Preposition preposition;
  private Dictionary database;

  private final String VERB_PATTERN = "^((VP)|(have NP)|(be NP P)|(be VP P))";
  private StanfordCoreNLPWrapper pipeline;
  private boolean useLinguisticalAnalysis = true;

  private final List<String> auxiliaryVerbs =
      Lists.newArrayList("do", "have", "be", "shall", "can", "may");

  private IRIConverter uriConverter;

  public PropertyVerbalizer(QueryExecutionFactory qef, String cacheDirectory,
      Dictionary wordnetDictionary) {
    this(new DefaultIRIConverter(qef, cacheDirectory), wordnetDictionary);
  }

  public PropertyVerbalizer(IRIConverter uriConverter, Dictionary wordnetDictionary) {
    this.uriConverter = uriConverter;
    try {
      this.database =
          wordnetDictionary == null ? Dictionary.getDefaultResourceInstance() : wordnetDictionary;
    } catch (JWNLException e) {
      throw new RuntimeException("Failed to create WordNet instance.", e);
    }

    preposition = new Preposition();

    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
    props.put("ssplit.isOneSentence", "true");
    pipeline = new StanfordCoreNLPWrapper(new StanfordCoreNLP(props));
    // pipeline = new StanfordCoreNLPWrapper(new StanfordCoreNLPClient(props,
    // "titan.informatik.uni-leipzig.de", 9000));
  }

  public PropertyVerbalization verbalize(String propertyURI) {
    logger.debug("Getting lexicalization type for \"" + propertyURI + "\"...");

    // get textual representation for the property URI
    String propertyText = uriConverter.convert(propertyURI);

    // normalize the text, e.g. to lower case
    propertyText = normalize(propertyText);

    // try to use linguistic information
    PropertyVerbalization propertyVerbalization = null;
    if (useLinguisticalAnalysis) {
      propertyVerbalization = getTypeByLinguisticAnalysis(propertyURI, propertyText);
    }

    // if this failed use WordNet heuristic
    if (propertyVerbalization == null
        || propertyVerbalization.getVerbalizationType() == PropertyVerbalizationType.UNSPECIFIED
        || propertyText.split(" ").length == 1) {
      logger.debug("...using WordNet-based analysis...");
      PropertyVerbalizationType verbalizationType = getTypeByWordnet(propertyText);
      propertyVerbalization.setVerbalizationType(verbalizationType);
    }

    // compute expanded form
    computeExpandedVerbalization(propertyVerbalization);

    // determine tense
    Tense tense = getTense(propertyText);
    propertyVerbalization.setTense(tense);

    logger.debug("Done.");

    return propertyVerbalization;
  }

  /**
   * Determine the verbalization type of a property, i.e. whether it is a verb or a noun, by using
   * WordNet statistics.
   * 
   * @param property the property
   * @return the type of verbalization
   */
  public PropertyVerbalizationType getTypeByWordnet(String property) {
    // length is > 1
    if (property.contains(" ")) {
      String split[] = property.split(" ");
      String lastToken = split[split.length - 1];
      // first check if the ending is a preposition
      // if yes, then the type is that of the first word
      if (preposition.isPreposition(lastToken)) {
        String firstToken = split[0];
        if (getTypeByWordnet(firstToken) == PropertyVerbalizationType.NOUN) {
          return PropertyVerbalizationType.NOUN;
        } else if (getTypeByWordnet(firstToken) == PropertyVerbalizationType.VERB) {
          return PropertyVerbalizationType.VERB;
        }
      }
      if (getTypeByWordnet(lastToken) == PropertyVerbalizationType.NOUN) {
        return PropertyVerbalizationType.NOUN;
      } else if (getTypeByWordnet(split[0]) == PropertyVerbalizationType.VERB) {
        return PropertyVerbalizationType.VERB;
      } else {
        return PropertyVerbalizationType.NOUN;
      }
    } else {
      double score = getScore(property);
      if (score < 0) {// some count did not work
        return PropertyVerbalizationType.UNSPECIFIED;
      }
      if (score >= threshold) {
        return PropertyVerbalizationType.NOUN;
      } else if (score < 1 / threshold) {
        return PropertyVerbalizationType.VERB;
      } else {
        return PropertyVerbalizationType.NOUN;
      }
    }
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  /**
   * Whether to apply an analysis based in linguistic features in addition to WordNet.
   * 
   * @param useLinguisticalAnalysis the useLinguisticalAnalysis to set
   */
  public void setUseLinguisticalAnalysis(boolean useLinguisticalAnalysis) {
    this.useLinguisticalAnalysis = useLinguisticalAnalysis;
  }

  /**
   * Returns log(nounCount/verbCount), i.e., positive for noun, negative for verb
   *
   * @param token Input token
   * @return "Typicity"
   */
  public double getScore(String token) {
    logger.debug("Checking " + token);

    double nounCount = 0;
    double verbCount = 0;

    List<Synset> synsets;

    try {
      // number of occurrences as noun
      IndexWord iw = database.lookupIndexWord(POS.NOUN, token);
      if (iw != null) {
        synsets = iw.getSenses();

        for (Synset synset : synsets) {
          List<Word> words = synset.getWords();

          for (Word word : words) {// System.out.println(s[j] + ":" + synsets[i].getTagCount(s[j]));
            nounCount += Math.log(word.getUseCount() + 1.0);
          }
        }
      }


      // number of occurrences as verb
      iw = database.lookupIndexWord(POS.VERB, token); // does morphological processing, otherwise
                                                      // use getIndexWord
      if (iw != null) {
        synsets = iw.getSenses();
        for (Synset synset : synsets) {
          List<Word> words = synset.getWords();

          for (Word word : words) {// System.out.println(s[j] + ":" + synsets[i].getTagCount(s[j]));
            verbCount += Math.log(word.getUseCount() + 1.0);
          }
        }
      }

      logger.debug("Noun count = " + nounCount);
      logger.debug("Verb count = " + verbCount);
    } catch (JWNLException e) {
      logger.error("WordNet lookup failed.", e);
    }

    if (verbCount == 0 && nounCount == 0) {
      return 1.0;
    }
    if (verbCount == 0) {
      return Double.MAX_VALUE;
    }
    if (nounCount == 0) {
      return 0.0;
    } else {
      return nounCount / verbCount;
    }
  }

  /**
   * Returns the infinitive form for a given word.
   * 
   * @param word the word
   * @return the infinitive form
   */
  public String getInfinitiveForm(String word) {

    String[] split = word.split(" ");
    String verb = split[0];

    if (verb.endsWith("ed") && split.length == 1) {
      // probably past tense

    } else if (verb.endsWith("ed") || verb.endsWith("un") || verb.endsWith("wn")
        || verb.endsWith("en")) {
      // check for past construction that simply need an auxiliary
      return "be " + word;
    }

    try {
      IndexWord iw = database.getIndexWord(POS.VERB, word);
      if (iw != null) {
        List<Synset> synsets = iw.getSenses();
        // double min = verb.length();
        String result = verb;
        for (Synset synset : synsets) {
          for (Word w : synset.getWords()) {
            if (verb.contains(w.getLemma())) {
              result = w.getLemma();
              if (split.length > 1) {
                for (int k = 1; k < split.length; k++) {
                  result = result + " " + split[k];
                }
              }
              return result;
            }
          }
        }
      }
    } catch (JWNLException e) {
      logger.error("WordNet lookup failed.", e);
    }
    return word;
  }

  private Tense getTense(String word) {
    String[] split = word.split(" ");
    String verb = split[0];

    if (verb.endsWith("ed") && split.length == 1) {
      // probably past tense
      return Tense.PAST;
    } else {
      return Tense.PRESENT;
    }
  }

  private PropertyVerbalization getTypeByLinguisticAnalysis(String propertyURI,
      String propertyText) {
    logger.debug("...using linguistical analysis...");
    Annotation document = new Annotation(propertyText);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    String pattern = "";
    PropertyVerbalizationType verbalizationType = PropertyVerbalizationType.UNSPECIFIED;
    boolean firstTokenAuxiliary = false;
    for (CoreMap sentence : sentences) {
      List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
      // get the first word and check if it's 'is' or 'has'
      CoreLabel token = tokens.get(0);
      String word = token.get(TextAnnotation.class);
      String pos = token.get(PartOfSpeechAnnotation.class);
      String lemma = token.getString(LemmaAnnotation.class);

      firstTokenAuxiliary = auxiliaryVerbs.contains(lemma);

      if (lemma.equals("be") || word.equals("have")) {
        pattern += lemma.toUpperCase();
      } else {
        if (pos.startsWith("N")) {
          pattern += "NP";
        } else if (pos.startsWith("V")) {
          pattern += "VP";
        } else {
          pattern += pos;
        }
      }
      if (tokens.size() > 1) {
        pattern += " ";
        for (int i = 1; i < tokens.size(); i++) {
          token = tokens.get(i);
          pos = token.get(PartOfSpeechAnnotation.class);
          if (pos.startsWith("N")) {
            pattern += "NP";
          } else if (pos.startsWith("V")) {
            pattern += "VP";
          } else {
            pattern += pos;
          }
          pattern += " ";
        }
      }
      // get the parse tree
      Tree tree = sentence.get(TreeAnnotation.class);
      // skip ROOT tag
      tree = tree.skipRoot();
      logger.debug("Parse tree:" + tree.pennString());
      // tree.pennPrint();
      // check if VP is directly followed by NP
      // sometimes parent node is S,SINV,etc.
      if (tree.value()
          .matches(Joiner.on('|').join(Lists.newArrayList(S, SBAR, SBARQ, SINV, FRAGMENT)))) {
        tree = tree.getChild(0);
      }
      boolean useDeterminer = false;
      if (tree.value().equals(VERB_PHRASE.getTag())) {
        for (Tree child : tree.getChildrenAsList()) {
          // check if first non terminal is NP and not contains a determiner
          if (!child.isPreTerminal()) {
            if (child.value().equals(NOUN_PHRASE.getTag())
                && !child.getChild(0).value().equals(DETERMINER.getTag())) {
              useDeterminer = true;
            }
            break;
          }
        }
      }
      // add determiner tag
      if (useDeterminer) {
        String[] split = pattern.split(" ");
        pattern =
            split[0] + " DET " + Joiner.on(" ").join(Arrays.copyOfRange(split, 1, split.length));
      }
    }
    pattern = pattern.trim();

    // if first token is an auxiliary can return verb
    if (firstTokenAuxiliary) {
      verbalizationType = PropertyVerbalizationType.VERB;
    }

    // check if pattern matches
    if (pattern.matches(VERB_PATTERN)) {
      logger.debug("...successfully determined type.");
      verbalizationType = PropertyVerbalizationType.VERB;
    }
    return new PropertyVerbalization(propertyURI, propertyText, pattern, verbalizationType);
  }

  private String normalize(String propertyText) {
    // lower case
    propertyText = propertyText.toLowerCase();

    return propertyText;
  }

  private void computeExpandedVerbalization(PropertyVerbalization propertyVerbalization) {

    String text = propertyVerbalization.getVerbalizationText();
    String expandedForm = text;

    // get POS tag of property verbalization
    String pos = propertyVerbalization.getPOSTags();

    // VBN IN
    if (pos.equals("VBN IN")) {
      expandedForm = "is" + " " + text;
    } else if (pos.startsWith("BE DET")) {
      String[] split = text.split(" ");
      expandedForm =
          "is" + " a " + Joiner.on(" ").join(Arrays.copyOfRange(text.split(" "), 1, split.length));
    }

    propertyVerbalization.setExpandedVerbalizationText(expandedForm);
  }
}
