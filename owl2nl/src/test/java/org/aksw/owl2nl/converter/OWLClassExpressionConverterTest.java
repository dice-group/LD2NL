/*-
 * #%L
 * OWL2NL
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
package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.data.OWL2NLInput;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
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

  private final OWLClass university = df.getOWLClass("University", ontology);
  private final OWLClass softwareCompany = df.getOWLClass("SoftwareCompany", ontology);

  private final OWLDataProperty amountOfSalary = df.getOWLDataProperty("amountOfSalary", ontology);
  private final OWLDataProperty earns = df.getOWLDataProperty("earns", ontology);
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
    Assert.assertEquals("an animal", converter.convert(animal));
    // TODO: a university
    Assert.assertEquals("an university", converter.convert(university));
  }

  @Test
  public void testSomeValuesFromA() {
    Assert.assertEquals(//
        "something whose birth place is a place", //
        converter.convert(df.getOWLObjectSomeValuesFrom(birthPlace, place))//
    );
  }

  @Test
  public void testSomeValuesFromB() {
    Assert.assertEquals(//
        "something that works for a company", //
        converter.convert(df.getOWLObjectSomeValuesFrom(worksFor, company))//
    );
  }

  @Test
  public void testNestedA() {
    final OWLObjectSomeValuesFrom ledByPerson = df.getOWLObjectSomeValuesFrom(ledBy, person);
    Assert.assertEquals(//
        "something that works for something that is led by a person", //
        converter.convert(df.getOWLObjectSomeValuesFrom(worksFor, ledByPerson))//
    );
  }

  @Test
  public void testNestedB() {
    final OWLObjectSomeValuesFrom ledByPerson = df.getOWLObjectSomeValuesFrom(ledBy, person);
    final OWLObjectIntersectionOf intersection = df.getOWLObjectIntersectionOf(place, ledByPerson);
    Assert.assertEquals(//
        "something that has at least 3 birth places that are a place that is led by a person", //
        converter.convert(df.getOWLObjectMinCardinality(3, birthPlace, intersection))//
    );
  }

  @Test
  public void testNestedC() {
    final OWLObjectMinCardinality ce = df.getOWLObjectMinCardinality(//
        5, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person)))//
    );
    Assert.assertEquals(//
        "something that works for at least 5 that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  @Test
  public void testNestedD() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(1, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    Assert.assertEquals(//
        "something that works for at least 1 that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  /**
   * ≥ 3 birthPlace.Place
   */
  @Test
  public void testMinCardinalityA() {
    Assert.assertEquals(//
        "something that has at least 3 birth places that are a place", //
        converter.convert(df.getOWLObjectMinCardinality(3, birthPlace, place))//
    );
  }

  /**
   * ≥ 3 worksFor.Company
   */
  @Test
  public void testMinCardinalityB() {
    // works for at least 3 companies
    Assert.assertEquals(//
        "something that works for at least 3 some companies", //
        // "something that works for at least 3 companies",
        converter.convert(df.getOWLObjectMinCardinality(3, worksFor, company))//
    );
  }

  /**
   * ≥ 3 nrOfInhabitants.()
   */
  @Test
  public void testMinCardinalityC() {
    Assert.assertEquals(//
        "something that has at least 3 nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataMinCardinality(3, nrOfInhabitants, dataRange))//
    );
  }

  /**
   * ≤ 3 birthPlace.Place
   */
  @Test
  public void testMaxCardinalityA() {
    Assert.assertEquals(//
        "something that has at most 3 birth places that are a place", //
        converter.convert(df.getOWLObjectMaxCardinality(3, birthPlace, place))//
    );
  }

  @Test
  public void testMaxCardinalityB() {
    Assert.assertEquals(//
        "something that works for at most 3 some companies", //
        // "something that works for at most 3 some companies"
        converter.convert(df.getOWLObjectMaxCardinality(3, worksFor, company))//
    );
  }

  @Test
  public void testMaxCardinalityC() {
    Assert.assertEquals(//
        "something that has at most 3 nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataMaxCardinality(3, nrOfInhabitants, dataRange))//
    );
  }

  /**
   * 3 birthPlace.Place
   */
  @Test
  public void testExactCardinalityA() {
    Assert.assertEquals(//
        "something that has exactly 3 birth places that are a place", //
        converter.convert(df.getOWLObjectExactCardinality(3, birthPlace, place))//
    );
  }

  /**
   * 3 worksFor.Company
   */
  @Test
  public void testExactCardinalityB() {
    Assert.assertEquals(//
        "something that works for exactly 3 some companies", //
        // "something that works for exactly 3 companies"
        converter.convert(df.getOWLObjectExactCardinality(3, worksFor, company))//
    );
  }

  @Test
  public void testExactCardinalityC() {
    Assert.assertEquals(//
        "something that has exactly 3 nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataExactCardinality(3, nrOfInhabitants, dataRange))//
    );
  }

  /**
   * ∀ worksFor.Company
   */
  @Test
  public void testAllValuesFromA() {
    // works only for a company
    Assert.assertEquals(//
        "something that works for only a company", //
        // "everything that works for a company"
        converter.convert(df.getOWLObjectAllValuesFrom(worksFor, company))//
    );
  }

  /**
   * ∀ nrOfInhabitants.()
   */
  @Test
  public void testAllValuesFromB() {
    Assert.assertEquals(//
        "something whose nr of inhabitant is greater than or equals to 10000000", //
        // final String expected = "everything whose nr of inhabitant is greater than or equal to
        // 10000000"
        converter.convert(df.getOWLDataAllValuesFrom(nrOfInhabitants, dataRange))//
    );
  }

  /**
   * ∀ earns.()
   */
  @Test
  public void testAllValuesFromC() {
    final String expected = "something that earns only greater than or equals to 10000000";
    // final String expected = "everyone who earns greater than or equal to 10000000"
    Assert.assertEquals(expected, converter.convert(df.getOWLDataAllValuesFrom(earns, dataRange)));
  }

  @Test
  public void testHasValueA() {
    Assert.assertEquals(//
        "something whose birth place is Leipzig University", //
        converter.convert(df.getOWLObjectHasValue(birthPlace, leipzig))//
    );
  }

  @Test
  public void testHasValueB() {
    Assert.assertEquals(//
        "something whose nr of inhabitants is 1000000", //
        converter.convert(df.getOWLDataHasValue(nrOfInhabitants, literal))//
    );
  }

  @Test
  public void testhasValueC() {
    Assert.assertEquals(//
        "something that works place Paderborn", //
        converter.convert(df.getOWLObjectHasValue(workPlace, paderborn))//
    );
  }

  // TODO: method is the same aa in testAllValuesFromA
  @Test
  public void testDataHasValue() {
    // works for a company
    Assert.assertEquals(//
        "something that works for only a company", //
        converter.convert(df.getOWLObjectAllValuesFrom(worksFor, company))//
    );
  }

  @Test
  public void testIntersectionA() {
    Assert.assertEquals(//
        "a person that works for a company", //
        converter.convert(
            df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person))//
    );
  }

  @Test
  public void testIntersectionB() {
    Assert.assertEquals(//
        "a place that is led by a person", //
        converter.convert(
            df.getOWLObjectIntersectionOf(place, df.getOWLObjectSomeValuesFrom(ledBy, person)))//
    );
  }

  @Test
  public void testUnion() {
    // works for a company
    Assert.assertEquals(//
        "a person or something that works for a company", //
        converter.convert(
            df.getOWLObjectUnionOf(df.getOWLObjectSomeValuesFrom(worksFor, company), person))//
    );
  }

  @Test
  public void testNegationA() {
    // not a person
    Assert.assertEquals(//
        "something that is not a person", //
        converter.convert(df.getOWLObjectComplementOf(person))//
    );
  }

  @Test
  public void testNegationB() {
    // does not work for a company
    Assert.assertEquals(//
        "something that does not works not for a company", //
        // "everything that does not work for a company"
        converter
            .convert(df.getOWLObjectComplementOf(df.getOWLObjectSomeValuesFrom(worksFor, company)))//
    );
  }

  @Test
  public void simpleNegation() {
    /* someone who does not work for a person or a company */
    Assert.assertEquals("something that does not works not for a company or a person", //
        converter.convert(df.getOWLObjectComplementOf(
            df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectUnionOf(person, company))))//
    );
  }

  @Test
  public void testNestedE() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(5, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));

    Assert.assertEquals(//
        "something that works for at least 5 that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  @Test
  public void testNested2() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(1, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));

    Assert.assertEquals(//
        "something that works for at least 1 that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
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

    Assert.assertEquals(
        "something that something that does not works not for a company"
            + " or a person and that something whose birth place is Paderborn and that has"
            + " at most 500000 nr of inhabitants that are greater than or equals to 10000000",
        converter.convert(ce)//
    );
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
    Assert.assertEquals(
        "something that works for at least 5 that a company that are not a software company and that are led by a company or a person", //
        converter.convert(ce)//
    );
    /*
     * someone who works for at least one company which is not a software company and led by a
     * company or a person
     */
    ce = df.getOWLObjectMinCardinality(1, worksFor,
        df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person)),
            df.getOWLObjectComplementOf(softwareCompany)));

    Assert.assertEquals(//
        "something that works for at least 1 that a company that is not a software company and that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  @Test
  public void testDataHasValueAndObjectHasValue() {
    // works for a company
    /* someone whose work place is paderborn and whose amount of salary is 40000 */
    Assert.assertEquals(//
        "something that works place Paderborn and whose amount of salary is 40000", //
        converter.convert(//
            df.getOWLObjectIntersectionOf(df.getOWLDataHasValue(amountOfSalary, salary),
                df.getOWLObjectHasValue(workPlace, paderborn)))//
    );
  }

  @Test
  public void NestedtesthasValue() {
    // work place is a place and is named paderborn
    final OWLObjectSomeValuesFrom ce = df.getOWLObjectSomeValuesFrom(workPlace,
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(workPlace, paderborn)));

    Assert.assertEquals(//
        "something that works place something that works place Paderborn", //
        converter.convert(ce)//
    );
  }

  public void testComplex() {
    // a person that sings karaoke
    Assert.assertEquals(//
        "a person that sings karaoke", //
        converter
            .convert(df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person))//
    );
  }

  @Test
  public void testComplex8() {
    // a person that sings karaoke or a person that sings jazz
    final OWLObjectUnionOf ce1 = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));

    // "a person that sings jazz or karaoke"
    Assert.assertEquals(//
        "a person that sings jazz or a person that sings karaoke", //
        converter.convert(ce1)//
    );
  }

  @Test
  public void testComplex7() {
    // a person that sings karaoke or jazz
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(df.getOWLObjectIntersectionOf(
        df.getOWLObjectHasValue(sings, karaoke), df.getOWLObjectHasValue(sings, Jazz)), person);

    // "that something that sings jazz and karaoke"
    Assert.assertEquals(//
        "a person that something that sings jazz and that sings karaoke", //
        converter.convert(ce)//
    );
  }

  @Test
  public void testComplex6() {
    // a person that sings karaoke and a person that sings jazz
    final OWLObjectIntersectionOf ce2 = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));

    Assert.assertEquals(//
        "something that a person that sings jazz and that a person that sings karaoke", //
        // "a person that sings jazz and karaoke"
        converter.convert(ce2)//
    );
  }

  @Test
  public void testComplex5() {
    // a person that plays Cricket or a person that plays football or a person that plays hockey.
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, Cricket), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, football), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, hockey), person));

    Assert.assertEquals(//
        "a person that plays cricket, a person that plays football or a person that plays hockey", //
        // "a person that plays cricket, football or hockey"
        converter.convert(ce)//
    );
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

    Assert.assertEquals(//
        "a person that plays cricket, a person that plays football, a person that plays golf, a person that plays hockey or a person that plays tennis", //
        // "a person that plays cricket, football, golf, hockey or tennis"
        converter.convert(ce)//
    );
  }

  @Test
  public void testComplex2() {
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, rock), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, hiphop), person));

    Assert.assertEquals(//
        "something that a person that sings hiphop, that a person that sings jazz, that a person that sings karaoke and that a person that sings rock",
        // "a person that sings hiphop, jazz, karaoke and rock"
        converter.convert(ce)//
    );
  }

  @Test
  public void testComplex3() {
    final OWLObjectUnionOf ce1 = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final OWLObjectIntersectionOf ce = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, rock), person), ce1);

    Assert.assertEquals(//
        "something that a person that sings rock and that a person that sings jazz or a person that sings karaoke",
        // "a person that sings rock and jazz or karaoke"
        converter.convert(ce)//
    );
  }

  @Test
  public void testComplex4() {
    final OWLObjectIntersectionOf ce2 = df.getOWLObjectIntersectionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), person),
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), person));
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, Cricket), person), ce2);

    Assert.assertEquals(//
        "a person that plays cricket or something that a person that sings jazz and that a person that sings karaoke",
        // "a person that plays cricket or sings jazz and sings karaoke"
        converter.convert(ce)//
    );
  }
}
