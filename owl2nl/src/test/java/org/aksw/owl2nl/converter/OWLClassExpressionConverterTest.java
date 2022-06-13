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

import static org.aksw.owl2nl.converter.DataHelper.LOG;
import static org.aksw.owl2nl.converter.DataHelper.df;
import static org.aksw.owl2nl.converter.DataHelper.literal;
import static org.aksw.owl2nl.converter.DataHelper.minInclusive;
import static org.aksw.owl2nl.converter.DataHelper.salary;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.animal;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.company;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.narcisticPerson;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.person;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.place;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.professor;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.softwareCompany;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.university;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.woman;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.amountOfSalary;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.earns;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.nrOfInhabitants;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.cricket;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.football;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.golf;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.hiphop;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.hockey;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.jazz;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.karaoke;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.leipzig;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.paderborn;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.rock;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.tennis;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.birthPlace;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.hasChild;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.ledBy;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.love;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.plays;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.sings;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.workPlace;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.worksFor;

import org.aksw.owl2nl.data.OWL2NLInput;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public class OWLClassExpressionConverterTest {

  private final OWLClassExpressionConverter converter =
      new OWLClassExpressionConverter(new OWL2NLInput());

  @Test
  public void testSelf() {

    Assert.assertEquals(//
        "a narcissistic person that loves oneself", //
        // a narcissistic person is someone who loves himself.
        converter.convert(//
            df.getOWLObjectIntersectionOf(narcisticPerson, df.getOWLObjectHasSelf(love)))//
    );
  }

  // Person u ≥3 hasChild.(Woman u Professor)
  @Test
  public void testQualifiedNumberRestrictions() {
    LOG.info("testQualifiedNumberRestrictions");
    Assert.assertEquals(//
        "a person that has at least three children that a professor whose a woman",
        // TODO: pluralize!
        // a person who has at least three children who are professors and females

        // a person who has at least three children who are professors and females
        // class of all persons with 3 or more daughters who are professors

        converter.convert(//
            df.getOWLObjectIntersectionOf(person, df.getOWLObjectMinCardinality(3, hasChild,
                df.getOWLObjectIntersectionOf(professor, woman))))//
    );
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
        "something that has at least three birth places that are a place that is led by a person", //
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
        "something that works for at least five that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  @Test
  public void testNestedD() {
    final OWLObjectMinCardinality ce =
        df.getOWLObjectMinCardinality(1, worksFor, df.getOWLObjectIntersectionOf(company,
            df.getOWLObjectSomeValuesFrom(ledBy, df.getOWLObjectUnionOf(company, person))));
    Assert.assertEquals(//
        "something that works for at least one that a company that is led by a company or a person", //
        converter.convert(ce)//
    );
  }

  /**
   * ≥ 3 birthPlace.Place
   */
  @Test
  public void testMinCardinalityA() {
    Assert.assertEquals(//
        "something that has at least three birth places that are a place", //
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
        "something that works for at least three some companies", //
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
        "something that has at least three nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataMinCardinality(3, nrOfInhabitants, minInclusive))//
    );
  }

  /**
   * ≤ 3 birthPlace.Place
   */
  @Test
  public void testMaxCardinalityA() {
    Assert.assertEquals(//
        "something that has at most three birth places that are a place", //
        converter.convert(df.getOWLObjectMaxCardinality(3, birthPlace, place))//
    );
  }

  @Test
  public void testMaxCardinalityB() {
    Assert.assertEquals(//
        "something that works for at most three some companies", //
        // "something that works for at most 3 some companies"
        converter.convert(df.getOWLObjectMaxCardinality(3, worksFor, company))//
    );
  }

  @Test
  public void testMaxCardinalityC() {
    Assert.assertEquals(//
        "something that has at most three nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataMaxCardinality(3, nrOfInhabitants, minInclusive))//
    );
  }

  /**
   * 3 birthPlace.Place
   */
  @Test
  public void testExactCardinalityA() {
    Assert.assertEquals(//
        "something that has exactly three birth places that are a place", //
        converter.convert(df.getOWLObjectExactCardinality(3, birthPlace, place))//
    );
  }

  /**
   * 3 worksFor.Company
   */
  @Test
  public void testExactCardinalityB() {
    Assert.assertEquals(//
        "something that works for exactly three some companies", //
        // "something that works for exactly 3 companies"
        converter.convert(df.getOWLObjectExactCardinality(3, worksFor, company))//
    );
  }

  @Test
  public void testExactCardinalityC() {
    Assert.assertEquals(//
        "something that has exactly three nr of inhabitants that are greater than or equals to 10000000", //
        converter.convert(df.getOWLDataExactCardinality(3, nrOfInhabitants, minInclusive))//
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
        converter.convert(df.getOWLDataAllValuesFrom(nrOfInhabitants, minInclusive))//
    );
  }

  /**
   * ∀ earns.()
   */
  @Test
  public void testAllValuesFromC() {
    final String expected = "something that earns only greater than or equals to 10000000";
    // final String expected = "everyone who earns greater than or equal to 10000000"
    Assert.assertEquals(expected,
        converter.convert(df.getOWLDataAllValuesFrom(earns, minInclusive)));
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
    Assert.assertEquals(//
        "something that works not for a company", //
        converter
            .convert(df.getOWLObjectComplementOf(df.getOWLObjectSomeValuesFrom(worksFor, company)))//
    );
  }

  @Test
  public void simpleNegation() {
    /* someone who does not work for a person or a company */
    Assert.assertEquals("something that works not for a company or a person", //
        converter.convert(df.getOWLObjectComplementOf(
            df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectUnionOf(person, company))))//
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
            df.getOWLDataMaxCardinality(500000, nrOfInhabitants, minInclusive));
    ce = df
        .getOWLObjectIntersectionOf(
            df.getOWLObjectIntersectionOf(df.getOWLObjectComplementOf(
                df.getOWLObjectSomeValuesFrom(worksFor, df.getOWLObjectUnionOf(person, company)))),
            ce);

    Assert.assertEquals(
        "something that something that works not for a company"
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
        "something that works for at least five that a company that are not a software company and that are led by a company or a person", //
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
        "something that works for at least one that a company that is not a software company and that is led by a company or a person", //
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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, jazz), person));

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
        df.getOWLObjectHasValue(sings, karaoke), df.getOWLObjectHasValue(sings, jazz)), person);

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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, jazz), person));

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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, cricket), person),
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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, cricket), person),
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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, jazz), person),
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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, jazz), person));
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
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, jazz), person));
    final OWLObjectUnionOf ce = df.getOWLObjectUnionOf(
        df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(plays, cricket), person), ce2);

    Assert.assertEquals(//
        "a person that plays cricket or something that a person that sings jazz and that a person that sings karaoke",
        // "a person that plays cricket or sings jazz and sings karaoke"
        converter.convert(ce)//
    );
  }
}
