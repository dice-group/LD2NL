package org.aksw.triple2nl;

import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.aksw.triple2nl.property.PropertyVerbalizationType;
import org.aksw.triple2nl.property.PropertyVerbalizer;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import simplenlg.features.Tense;

public class PropertyVerbalizerTest {

  private static final Logger LOG = Logger.getLogger(PropertyVerbalizer.class);
  PropertyVerbalizer pp = new PropertyVerbalizer(//
      new QueryExecutionFactoryHttp("http://dbpedia.org/sparql"), //
      "cache", null//
  );
  PropertyVerbalization pv = null;
  String propertyURI = "";

  @Test
  public void restingPlace() {

    propertyURI = "http://dbpedia.org/ontology/restingPlace";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());

    Assert.assertEquals("VP DET NP", pv.getPOSTags());
    Assert.assertEquals("resting place", pv.getVerbalizationText());
    Assert.assertEquals("resting place", pv.getExpandedVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.NOUN, pv.getVerbalizationType());
  }

  @Test
  public void birthPlace() {
    propertyURI = "http://dbpedia.org/ontology/birthPlace";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals(PropertyVerbalizationType.NOUN, pv.getVerbalizationType());
    Assert.assertEquals("NP NP", pv.getPOSTags());
    Assert.assertEquals("birth place", pv.getVerbalizationText());
    Assert.assertEquals("birth place", pv.getExpandedVerbalizationText());
  }

  @Test
  public void hasColor() {
    propertyURI = "http://dbpedia.org/ontology/hasColor";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("has color", pv.getExpandedVerbalizationText());
    Assert.assertEquals("has color", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("VP NP", pv.getPOSTags());
  }

  @Test
  public void isHardWorking() {
    propertyURI = "http://dbpedia.org/ontology/isHardWorking";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("is hard working", pv.getExpandedVerbalizationText());
    Assert.assertEquals("is hard working", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("BE JJ NP", pv.getPOSTags());
  }

  @Test
  public void bornIn() {
    propertyURI = "http://dbpedia.org/ontology/bornIn";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("born in", pv.getExpandedVerbalizationText());
    Assert.assertEquals("born in", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("VP IN", pv.getPOSTags());
  }

  @Test
  public void cross() {
    propertyURI = "http://dbpedia.org/ontology/cross";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("cross", pv.getExpandedVerbalizationText());
    Assert.assertEquals("cross", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("NP", pv.getPOSTags());
  }

  @Test
  public void producedBy() {
    propertyURI = "http://dbpedia.org/ontology/producedBy";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("produced by", pv.getExpandedVerbalizationText());
    Assert.assertEquals("produced by", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("VP IN", pv.getPOSTags());
  }

  @Test
  public void worksFor() {
    propertyURI = "http://dbpedia.org/ontology/worksFor";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("works for", pv.getExpandedVerbalizationText());
    Assert.assertEquals("works for", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("NP IN", pv.getPOSTags());
  }

  @Test
  public void workedFor() {
    propertyURI = "http://dbpedia.org/ontology/workedFor";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());
    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("worked for", pv.getExpandedVerbalizationText());
    Assert.assertEquals("worked for", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("VP IN", pv.getPOSTags());
  }

  @Test
  public void knownFor() {
    propertyURI = "http://dbpedia.org/ontology/knownFor";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("known for", pv.getExpandedVerbalizationText());
    Assert.assertEquals("known for", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("VP IN", pv.getPOSTags());
  }

  @Test
  public void name() {
    propertyURI = "http://dbpedia.org/ontology/name";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("name", pv.getExpandedVerbalizationText());
    Assert.assertEquals("name", pv.getVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("NP", pv.getPOSTags());
  }

  @Test
  public void isGoldMedalWinner() {
    propertyURI = "http://dbpedia.org/ontology/isGoldMedalWinner";
    pv = pp.verbalize(propertyURI);
    Assert.assertEquals(propertyURI, pv.getProperty());

    Assert.assertEquals(Tense.PRESENT, pv.getTense());
    Assert.assertEquals("is gold medal winner", pv.getVerbalizationText());
    Assert.assertEquals("is a gold medal winner", pv.getExpandedVerbalizationText());
    Assert.assertEquals(PropertyVerbalizationType.VERB, pv.getVerbalizationType());
    Assert.assertEquals("BE DET JJ NP NP", pv.getPOSTags());
  }
}
