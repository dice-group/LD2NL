package org.aksw.owl2nl;

import org.aksw.owl2nl.converter.OWLClassExpressionConverter;
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
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
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

  private static OWLClassExpressionConverter converter;

  private static OWLObjectProperty birthPlace;
  private static OWLObjectProperty worksFor;
  private static OWLObjectProperty ledBy;
  private static OWLObjectProperty hasWorkPlace;
  private static OWLObjectProperty plays;
  private static OWLObjectProperty owner;

  private static OWLDataProperty nrOfInhabitants;

  private static OWLDataRange dataRange;

  private static OWLClass place;
  private static OWLClass company;
  private static OWLClass person;
  private static OWLClass animal;

  private static OWLNamedIndividual leipzig;
  private static OWLNamedIndividual bob;
  private static OWLNamedIndividual chess;

  private static OWLLiteral literal;

  private static OWLDataFactoryImpl df;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    converter = new OWLClassExpressionConverter(new OWL2NLInput());
    df = new OWLDataFactoryImpl();
    final PrefixManager pm = new DefaultPrefixManager();
    pm.setDefaultPrefix("http://dbpedia.org/ontology/");
    final PrefixManager resource = new DefaultPrefixManager();
    pm.setDefaultPrefix(" http://dbpedia.org/resource/");

    birthPlace = df.getOWLObjectProperty("birthPlace", pm);
    worksFor = df.getOWLObjectProperty("worksFor", pm);
    ledBy = df.getOWLObjectProperty("isLedBy", pm);
    plays = df.getOWLObjectProperty("play", pm);
    owner = df.getOWLObjectProperty("owner", pm);
    hasWorkPlace = df.getOWLObjectProperty("hasWorkPlace", pm);

    nrOfInhabitants = df.getOWLDataProperty("nrOfInhabitants", pm);
    dataRange = df.getOWLDatatypeMinInclusiveRestriction(10000000);

    place = df.getOWLClass("Place", pm);
    company = df.getOWLClass("Company", pm);
    person = df.getOWLClass("Person", pm);
    animal = df.getOWLClass("Animal", pm);

    leipzig = df.getOWLNamedIndividual("Leipzig_University", resource);
    bob = df.getOWLNamedIndividual("Albert_Einstein", resource);
    chess = df.getOWLNamedIndividual("Chess", resource);
    literal = df.getOWLLiteral(1000000);

    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  @Test
  public void testNamedClass() {
    final OWLClassExpression ce = person;
    final String text = converter.convert(person);
    final String expected = //
        "a person";
    LOG.info(ce + " = " + text);
    Assert.assertEquals(expected, text);
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
}
