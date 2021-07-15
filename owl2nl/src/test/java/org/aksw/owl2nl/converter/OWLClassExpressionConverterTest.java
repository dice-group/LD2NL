package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.data.OWL2NLInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 * @author rspeck
 *
 */
public class OWLClassExpressionConverterTest {

  private static final Logger LOG = LogManager.getLogger(OWLClassExpressionConverterTest.class);

  private static OWLDataFactoryImpl df = new OWLDataFactoryImpl();
  private static OWLClassExpressionConverter converter =
      new OWLClassExpressionConverter(new OWL2NLInput());
  final PrefixManager ontology =
      new DefaultPrefixManager(null, null, "http://dbpedia.org/ontology/");
  final PrefixManager resource =
      new DefaultPrefixManager(null, null, "http://dbpedia.org/resource/");

  private final OWLClass place = df.getOWLClass("Place", ontology);
  private final OWLClass company = df.getOWLClass("Company", ontology);
  private final OWLClass person = df.getOWLClass("Person", ontology);
  private final OWLClass animal = df.getOWLClass("Animal", ontology);
  private final OWLClass softwareCompany = df.getOWLClass("SoftwareCompany", ontology);

  private final OWLDataProperty amountOfSalary = df.getOWLDataProperty("amountOfSalary", ontology);
  private final OWLDataProperty nrOfInhabitants =
      df.getOWLDataProperty("nrOfInhabitants", ontology);

  private final OWLDataRange dataRange = df.getOWLDatatypeMinInclusiveRestriction(10000000);

  private final OWLObjectProperty sings = df.getOWLObjectProperty("sing", ontology);
  private final OWLObjectProperty workPlace = df.getOWLObjectProperty("workPlace", ontology);
  private final OWLObjectProperty birthPlace = df.getOWLObjectProperty("birthPlace", ontology);
  private final OWLObjectProperty worksFor = df.getOWLObjectProperty("worksFor", ontology);
  private final OWLObjectProperty ledBy = df.getOWLObjectProperty("isLedBy", ontology);
  private final OWLObjectProperty plays = df.getOWLObjectProperty("play", ontology);
  private final OWLObjectProperty owner = df.getOWLObjectProperty("owner", ontology);
  private final OWLObjectProperty hasWorkPlace = df.getOWLObjectProperty("hasWorkPlace", ontology);

  private final OWLNamedIndividual leipzig =
      df.getOWLNamedIndividual("Leipzig_University", resource);
  private final OWLNamedIndividual bob = df.getOWLNamedIndividual("Albert_Einstein", resource);
  private final OWLNamedIndividual paderborn = df.getOWLNamedIndividual("Paderborn", resource);
  private final OWLNamedIndividual karaoke = df.getOWLNamedIndividual("karaoke", resource);
  private final OWLNamedIndividual Jazz = df.getOWLNamedIndividual("jazz", resource);
  private final OWLNamedIndividual football = df.getOWLNamedIndividual("football", resource);
  private final OWLNamedIndividual Cricket = df.getOWLNamedIndividual("cricket", resource);
  private final OWLNamedIndividual hockey = df.getOWLNamedIndividual("hockey", resource);
  private final OWLNamedIndividual tennis = df.getOWLNamedIndividual("tennis", resource);
  private final OWLNamedIndividual golf = df.getOWLNamedIndividual("golf", resource);
  private final OWLNamedIndividual hiphop = df.getOWLNamedIndividual("hiphop", resource);
  private final OWLNamedIndividual rock = df.getOWLNamedIndividual("rock", resource);
  private final OWLNamedIndividual chess = df.getOWLNamedIndividual("Chess", resource);

  private final OWLLiteral salary = df.getOWLLiteral(40000);
  private final OWLLiteral literal = df.getOWLLiteral(1000000);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  @Test
  public void testNamedClass() {
    Assert.assertEquals("a person", converter.convert(person));
  }

  @Test
  public void testSomeValuesFromA() {
    final OWLClassExpression ce = df.getOWLObjectSomeValuesFrom(birthPlace, place);
    final String text = converter.convert(ce);
    final String expected = //
        "something whose birth place is a place";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testSomeValuesFromB() {
    // works for a company
    final OWLClassExpression ce = df.getOWLObjectSomeValuesFrom(worksFor, company);

    final String text = converter.convert(ce);
    final String expected = //
        "something that works for a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testNestedD() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(1, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 1 that a company that is"
        + " led by a company or a person", text);

  }

  @Test
  public void testNestedC() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(5, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 5 that a company that is"
        + " led by a company or a person", text);

  }

  @Test
  public void testNestedA() {
    final OWLObjectSomeValuesFrom ledByPerson = df.getOWLObjectSomeValuesFrom(ledBy, person);
    final OWLObjectIntersectionOf intersection = df.getOWLObjectIntersectionOf(place, ledByPerson);
    final OWLObjectMinCardinality ce = df.getOWLObjectMinCardinality(3, birthPlace, intersection);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has at least 3 birth places that are a place that is led by a person";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testNestedB() {
    final OWLObjectSomeValuesFrom ledByPerson = df.getOWLObjectSomeValuesFrom(ledBy, person);
    final OWLObjectSomeValuesFrom ce = df.getOWLObjectSomeValuesFrom(worksFor, ledByPerson);
    final String expected = //
        "something that works for something that is led by a person";
    final String text = converter.convert(ce);
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ≥ 3 birthPlace.Place
   */
  @Test
  public void testMinCardinalityA() {
    final OWLObjectMinCardinality ce = df.getOWLObjectMinCardinality(3, birthPlace, place);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has at least 3 birth places that are a place";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ≥ 3 worksFor.Company
   */
  @Test
  public void testMinCardinalityB() {
    // works for at least 3 companies
    final OWLObjectMinCardinality ce = df.getOWLObjectMinCardinality(3, worksFor, company);
    final String text = converter.convert(ce);
    final String expected = //
        "something that works for at least 3 some companies";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ≥ 3 nrOfInhabitants.()
   */
  @Test
  public void testMinCardinalityC() {
    final OWLDataMinCardinality ce = df.getOWLDataMinCardinality(3, nrOfInhabitants, dataRange);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has at least 3 nr of inhabitants that are greater than or equals to 10000000";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ≤ 3 birthPlace.Place
   */
  @Test
  public void testMaxCardinalityA() {
    final OWLClassExpression ce = df.getOWLObjectMaxCardinality(3, birthPlace, place);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has at most 3 birth places that are a place";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testMaxCardinalityB() {
    final OWLObjectMaxCardinality ce = df.getOWLObjectMaxCardinality(3, worksFor, company);
    final String text = converter.convert(ce);
    final String expected = //
        "something that works for at most 3 some companies";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testMaxCardinalityC() {
    final OWLDataMaxCardinality ce = df.getOWLDataMaxCardinality(3, nrOfInhabitants, dataRange);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has at most 3 nr of inhabitants that are greater than or equals to 10000000";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * 3 birthPlace.Place
   */
  @Test
  public void testExactCardinalityA() {
    final OWLClassExpression ce = df.getOWLObjectExactCardinality(3, birthPlace, place);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has exactly 3 birth places that are a place";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * 3 worksFor.Company
   */
  @Test
  public void testExactCardinalityB() {
    final OWLClassExpression ce = df.getOWLObjectExactCardinality(3, worksFor, company);
    final String text = converter.convert(ce);
    final String expected = //
        "something that works for exactly 3 some companies";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testExactCardinalityC() {
    final OWLClassExpression ce = df.getOWLDataExactCardinality(3, nrOfInhabitants, dataRange);
    final String text = converter.convert(ce);
    final String expected = //
        "something that has exactly 3 nr of inhabitants that are greater than or equals to 10000000";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ∀ worksFor.Company
   */
  @Test
  public void testAllValuesFromA() {
    // works only for a company
    final OWLObjectAllValuesFrom ce = df.getOWLObjectAllValuesFrom(worksFor, company);
    final String text = converter.convert(ce);
    final String expected = //
        "something that works for only a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  /**
   * ∀ nrOfInhabitants.()
   */
  @Test
  public void testAllValuesFromB() {
    final OWLDataAllValuesFrom ce = df.getOWLDataAllValuesFrom(nrOfInhabitants, dataRange);
    final String text = converter.convert(ce);
    final String expected = //
        "something whose nr of inhabitant is greater than or equals to 10000000";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testHasValueA() {
    final OWLClassExpression ce = df.getOWLObjectHasValue(birthPlace, leipzig);
    final String text = converter.convert(ce);
    final String expected = //
        "something whose birth place is Leipzig University"; // ;)
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testHasValueB() {
    final OWLDataHasValue ce = df.getOWLDataHasValue(nrOfInhabitants, literal);
    final String text = converter.convert(ce);
    final String expected = //
        "something whose nr of inhabitants is 1000000";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testhasValueC() {
    final OWLObjectHasValue ce = df.getOWLObjectHasValue(workPlace, paderborn);
    final String text = converter.convert(ce);
    LOG.info(ce + " = " + text);
    Assert.assertEquals("something that works place Paderborn", text);
  }

  @Test
  public void testDataHasValue() {
    // works for a company
    final OWLClassExpression ce = df.getOWLObjectAllValuesFrom(worksFor, company);
    final String text = converter.convert(ce);
    final String expected = //
        "something that works for only a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testIntersectionA() {
    final OWLClassExpression ce =
        df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person);
    final String text = converter.convert(ce);
    final String expected = //
        "a person that works for a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testIntersectionB() {

    final OWLObjectIntersectionOf ce =
        df.getOWLObjectIntersectionOf(place, df.getOWLObjectSomeValuesFrom(ledBy, person));
    final String text = converter.convert(ce);
    final String expected = //
        "a place that is led by a person";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testUnion() {
    // works for a company
    final OWLClassExpression ce =
        df.getOWLObjectUnionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person);
    final String text = converter.convert(ce);
    final String expected = //
        "a person or something that works for a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testNegationA() {
    // not a person
    final OWLClassExpression ce = df.getOWLObjectComplementOf(person);
    final String text = converter.convert(ce);
    final String expected = //
        "something that is not a person";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testNegationB() {
    // does not work for a company
    final OWLObjectComplementOf ce =
        df.getOWLObjectComplementOf(df.getOWLObjectSomeValuesFrom(worksFor, company));
    final String text = converter.convert(ce);
    final String expected = //
        "something that does not works not for a company";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
  }

  @Test
  public void simpleNegation() {
    /* someone who does not work for a person or a company */
    final OWLObjectComplementOf ce = df.getOWLObjectComplementOf(
        df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectUnionOf(person, company)));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that does not works not for a company or a person", text);
  }

  @Test
  public void testNestedE() {

    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(5, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 5 that a company that is"
        + " led by a company or a person", text);
  }

  @Test
  public void testNested2() {

    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(1, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 1 that a company that is"
        + " led by a company or a person", text);
  }

  @Test
  public void complexNegationWithMaxCardinality() {
    /*
     * someone who does not work for a person or a company and whose birthplace is paderborn that
     * has not more than 50000 inhabitants
     */
    OWLObjectIntersectionOf ce =
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(birthPlace, paderborn),
            df.getOWLDataMaxCardinality(500000, nrOfInhabitants, dataRange));
    ce = df
        .getOWLObjectIntersectionOf(
            df.getOWLObjectIntersectionOf(df.getOWLObjectComplementOf(
                df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectUnionOf(person, company)))),
            ce);

    final String text = converter.convert(ce);
    Assert.assertEquals(
        "something that something that does not works not for a company"
            + " or a person and that something whose birth place is Paderborn and that has"
            + " at most 500000 nr of inhabitants that are greater than or equals to 10000000",
        text);
  }

  @Test
  public void testNested1WithNegation() {

    /*
     * someone who works for at least 5 companies which are not software companies and are ledby a
     * company or a person
     */
    OWLObjectMinCardinality ce = df.getOWLObjectMinCardinality(5, worksFor,
        df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person)),
            df.getOWLObjectComplementOf(softwareCompany)));
    String text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 5 that a company that are"
        + " not a software company and that are led by a company or a person", text);
    /*
     * someone who works for at least one company which is not a software company and led by a
     * company or a person
     */
    ce = df.getOWLObjectMinCardinality(1, worksFor,
        df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person)),
            df.getOWLObjectComplementOf(softwareCompany)));
    text = converter.convert(ce);
    Assert.assertEquals("something that works for at least 1 that a company that is"
        + " not a software company and that is led by a company or a person", text);
  }

  @Test
  public void testDataHasValueAndObjectHasValue() {
    // works for a company
    /* someone whose work place is paderborn and whose amount of salary is 40000 */
    final OWLObjectIntersectionOf ce =
        df.getOWLObjectIntersectionOf(df.getOWLDataHasValue(amountOfSalary, salary),
            df.getOWLObjectHasValue(workPlace, paderborn));
    final String text = converter.convert(ce);
    Assert.assertEquals(
        "something that works place Paderborn" + " and whose amount of salary is 40000", text);
  }

  @Test
  public void NestedtesthasValue() {
    // work place is a place and is named paderborn
    final OWLObjectSomeValuesFrom ce = df.getOWLObjectSomeValuesFrom(workPlace,
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(workPlace, paderborn)));
    final String text = converter.convert(ce);
    Assert.assertEquals("something that works place something that works place Paderborn", text);
  }

  public void testComplex() {
    // a person that sings karaoke
    final OWLObjectIntersectionOf ce =
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person);
    final String text = converter.convert(ce);
    Assert.assertEquals("a person that sings karaoke", text);
  }

  @Test
  public void testComplex8() {
    // a person that sings karaoke or a person that sings jazz
    final OWLObjectUnionOf ce1 = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final String text = converter.convert(ce1);
    final String expected = "a person that sings jazz or a person that sings karaoke";
    // "a person that sings jazz or karaoke"
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex7() {
    // a person that sings karaoke or jazz
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(df.getOWLObjectIntersectionOf(
        df.getOWLObjectHasValue(sings, karaoke), df.getOWLObjectHasValue(sings, Jazz)), person);
    final String text = converter.convert(ce);
    final String expected = "a person that something that sings jazz and that sings karaoke";
    // "that something that sings jazz and karaoke"
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex6() {
    // a person that sings karaoke and a person that sings jazz
    final OWLObjectIntersectionOf ce2 = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final String text = converter.convert(ce2);
    final String expected =
        "something that a person that sings jazz and that a person that sings karaoke";
    // "a person that sings jazz and karaoke"
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex5() {
    // a person that plays Cricket or a person that plays football or a person that plays hockey.
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, Cricket), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, football), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, hockey), person));
    final String text = converter.convert(ce);
    final String expected =
        "a person that plays cricket, a person that plays football or a person that plays hockey";
    // expected ="a person that plays cricket, football or hockey"
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testmultipleConnectors() {
    // sentences for multiple connectors
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, Cricket), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, football), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, hockey), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, tennis), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, golf), person));
    final String text = converter.convert(ce);
    final String expected =
        "a person that plays cricket, a person that plays football, a person that plays golf, a person that plays hockey or a person that plays tennis";
    // expected = "a person that plays cricket, football, golf, hockey or tennis";
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex2() {
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, rock), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, hiphop), person));
    final String text = converter.convert(ce);
    final String expected =
        "something that a person that sings hiphop, that a person that sings jazz, that a person that sings karaoke and that a person that sings rock";
    // expected = "a person that sings hiphop, jazz, karaoke and rock";
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex3() {
    final OWLObjectUnionOf ce1 = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, rock), person), ce1);
    final String text = converter.convert(ce);
    final String expected =
        "something that a person that sings rock and that a person that sings jazz or a person that sings karaoke";
    // expected = "a person that sings rock and jazz or karaoke"
    Assert.assertEquals(expected, text);
  }

  @Test
  public void testComplex4() {
    final OWLObjectIntersectionOf ce2 = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, Cricket), person), ce2);
    final String text = converter.convert(ce);
    final String expected =
        "a person that plays cricket or something that a person that sings jazz and that a person that sings karaoke";
    // expected = "a person that plays cricket or sings jazz and sings karaoke"
    Assert.assertEquals(expected, text);
  }
}
