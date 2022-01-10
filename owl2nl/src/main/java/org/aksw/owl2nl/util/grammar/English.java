package org.aksw.owl2nl.util.grammar;

import org.aksw.sparql2nl.naturallanguagegeneration.WordTypeDetector;
import org.aksw.triple2nl.gender.DictionaryBasedGenderDetector;
import org.aksw.triple2nl.gender.Gender;
import org.aksw.triple2nl.gender.GenderDetector;
import org.aksw.triple2nl.gender.TypeAwareGenderDetector;
import org.dllearner.kb.sparql.SparqlEndpoint;

public class English implements IGrammar {
  WordTypeDetector wtd = null;
  TypeAwareGenderDetector tagd = null;

  @Override
  public String plural(final String word) {
    return singularOrPlural(word, 2);
  }

  /**
   *
   * @param word in singular form
   */
  @Override
  public String singular(final String word) {
    return singularOrPlural(word, 1);
  }

  @Override
  public boolean isNoun(final String word) {
    initWTD();
    return wtd.isNoun(word);
  }

  // privates
  private String singularOrPlural(final String word, final int i) {
    return org.atteo.evo.inflector.English.plural(word, i);
  }

  private void initWTD() {
    if (wtd == null) {
      wtd = new WordTypeDetector();
    }
  }

  private void initTAGD() {
    if (tagd == null) {
      final GenderDetector g = new DictionaryBasedGenderDetector();
      final SparqlEndpoint q = SparqlEndpoint.getEndpointDBpediaLiveAKSW();
      tagd = new TypeAwareGenderDetector(q, g);
    }
  }

  @Override
  public boolean isPerson(final String word) {
    return isPersonA(word) || isPersonB(word);
  }

  protected boolean isPersonA(final String word) {
    initTAGD();
    return !tagd.getGender(word).equals(Gender.UNKNOWN);
  }

  protected boolean isPersonB(final String word) {
    return DBPedia.isPerson(word);
  }

  @Override
  public boolean isObject(final String word) {
    throw new UnsupportedOperationException();
  }
}
