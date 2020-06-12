package org.aksw.owl2nl;

import org.junit.BeforeClass;
import org.junit.Test;
// import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Lorenz Buehmann
 *
 */
public class OWLClassExpressionConverterTest {

  private static OWLClassExpressionConverter converter;
  private static OWLObjectProperty birthPlace;
  private static OWLObjectProperty worksFor;
  private static OWLObjectProperty ledBy;
  private static OWLDataProperty nrOfInhabitants;
  private static OWLClass place;
  private static OWLClass company;
  private static OWLClass person;
  private static OWLDataFactoryImpl df;
  private static OWLNamedIndividual leipzig;
  private static OWLLiteral literal;

  OWLClassExpression ce;
  String text;
  private static OWLDataRange dataRange;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    converter = new OWLClassExpressionConverter();

    df = new OWLDataFactoryImpl();
    final PrefixManager pm = new DefaultPrefixManager("http://dbpedia.org/ontology/");

    birthPlace = df.getOWLObjectProperty("birthPlace", pm);
    worksFor = df.getOWLObjectProperty("worksFor", pm);
    ledBy = df.getOWLObjectProperty("isLedBy", pm);

    nrOfInhabitants = df.getOWLDataProperty("nrOfInhabitants", pm);
    dataRange = df.getOWLDatatypeMinInclusiveRestriction(10000000);

    place = df.getOWLClass("Place", pm);
    company = df.getOWLClass("Company", pm);
    person = df.getOWLClass("Person", pm);

    leipzig = df.getOWLNamedIndividual("Leipzig", pm);
    literal = df.getOWLLiteral(1000000);

    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  @Test
  public void testNamedClass() {
    // person
    ce = person;
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testSomeValuesFrom() {
    // birth place is a place
    ce = df.getOWLObjectSomeValuesFrom(birthPlace, place);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // works for a company
    ce = df.getOWLObjectSomeValuesFrom(worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testNested() {

    ce = df.getOWLObjectMinCardinality(3, birthPlace,
        df.getOWLObjectIntersectionOf(place, df.getOWLObjectSomeValuesFrom(ledBy, person)));
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectSomeValuesFrom(ledBy, person));
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testMinCardinality() {
    // has at least 3 birth places that are a place
    ce = df.getOWLObjectMinCardinality(3, birthPlace, place);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // works for at least 3 companies
    ce = df.getOWLObjectMinCardinality(3, worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLDataMinCardinality(3, nrOfInhabitants, dataRange);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testMaxCardinality() {
    // has at most 3 birth places that are a place
    ce = df.getOWLObjectMaxCardinality(3, birthPlace, place);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // works for at most 3 companies
    ce = df.getOWLObjectMaxCardinality(3, worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLDataMaxCardinality(3, nrOfInhabitants, dataRange);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testExactCardinality() {
    // has exactly 3 birth places that are a place
    ce = df.getOWLObjectExactCardinality(3, birthPlace, place);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // works for exactly 3 companies
    ce = df.getOWLObjectExactCardinality(3, worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLDataExactCardinality(3, nrOfInhabitants, dataRange);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testAllValuesFrom() {
    // works only for a company
    ce = df.getOWLObjectAllValuesFrom(worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLDataAllValuesFrom(nrOfInhabitants, dataRange);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testHasValue() {
    // works for a company
    ce = df.getOWLObjectHasValue(birthPlace, leipzig);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // nr of inhabitants is 1000000
    ce = df.getOWLDataHasValue(nrOfInhabitants, literal);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testDataHasValue() {
    // works for a company
    ce = df.getOWLObjectAllValuesFrom(worksFor, company);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testIntersection() {
    // works for a company
    ce = df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    ce = df.getOWLObjectIntersectionOf(place, df.getOWLObjectSomeValuesFrom(ledBy, person));
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testUnion() {
    // works for a company
    ce = df.getOWLObjectUnionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }

  @Test
  public void testNegation() {
    // not a person
    ce = df.getOWLObjectComplementOf(person);
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);

    // does not work for a company
    ce = df.getOWLObjectComplementOf(df.getOWLObjectSomeValuesFrom(worksFor, company));
    text = converter.convert(ce);
    System.out.println(ce + " = " + text);
  }
}
