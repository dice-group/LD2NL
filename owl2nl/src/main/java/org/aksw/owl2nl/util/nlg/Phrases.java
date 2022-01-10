package org.aksw.owl2nl.util.nlg;

import org.aksw.owl2nl.util.grammar.Words;

import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

public class Phrases {

  public static NPPhraseSpec getAnIndividual(final NLGFactory nlgFactory) {
    final NPPhraseSpec s = getIndividual(nlgFactory);
    s.setDeterminer(Words.a);
    return s;
  }

  public static NPPhraseSpec getTheIndividual(final NLGFactory nlgFactory) {
    final NPPhraseSpec s = getIndividual(nlgFactory);
    s.setDeterminer(Words.the);
    return s;
  }

  public static NPPhraseSpec getIndividual(final NLGFactory nlgFactory) {
    return nlgFactory.createNounPhrase(Words.individual);
  }

  public static SPhraseSpec createClause(final NLGFactory nlgFactory, final Object s,
      final Object v, final Object o) {
    return nlgFactory.createClause(s, v, o);
  }

  public static VPPhraseSpec getBe(final NLGFactory nlgFactory) {
    return nlgFactory.createVerbPhrase(Words.be);
  }

  // The {a} of the {b} {c} property
  public static NPPhraseSpec getProperty(final NLGFactory nlgFactory, final String a,
      final String b, final String c) {

    final NPPhraseSpec s = nlgFactory.createNounPhrase();

    final NPPhraseSpec np = nlgFactory.createNounPhrase();
    np.setDeterminer(Words.the);
    np.setNoun(a);

    final PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
    final NPPhraseSpec x = nlgFactory.createNounPhrase();
    pp.setPreModifier(Words.of);
    pp.setPreposition(x);

    x.setDeterminer(Words.the);
    x.setNoun(b);
    x.setPostModifier(c);
    x.addPostModifier(Words.property);

    s.setPreModifier(np);
    s.setNoun(pp);
    return s;

  }

  // the {b} {c} property
  public static NPPhraseSpec getProperty(final NLGFactory nlgFactory, final String b,
      final String c) {

    final NPPhraseSpec x = nlgFactory.createNounPhrase();
    x.setDeterminer(Words.the);
    x.setNoun(b);
    x.setPostModifier(c);
    x.addPostModifier(Words.property);
    return x;
  }
}
