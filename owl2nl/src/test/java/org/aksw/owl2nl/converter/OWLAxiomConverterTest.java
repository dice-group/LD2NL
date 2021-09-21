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

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLAxiomConverterTest {

  private static final Logger LOG = LogManager.getLogger(OWLAxiomConverterTest.class);

  private final OWLAxiomConverter converter = new OWLAxiomConverter();
  private final OWLDataFactoryImpl df = new OWLDataFactoryImpl();
  private final PrefixManager pm =
      new DefaultPrefixManager(null, null, "http://dbpedia.org/ontology/");

  private final OWLClass boy = df.getOWLClass("Boy", pm);
  private final OWLClass girl = df.getOWLClass("Girl", pm);
  private final OWLClass child = df.getOWLClass("Child", pm);
  private final OWLClass man = df.getOWLClass("Man", pm);
  private final OWLClass place = df.getOWLClass("Place", pm);
  private final OWLClass person = df.getOWLClass("Person", pm);
  private final OWLClass thing = df.getOWLClass("Thing", pm);
  private final OWLDatatype temperature = df.getOWLDatatype("temperature", pm);

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  private void assertEquals(final Pair<String, String> pair) {
    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  @Test
  public void test() {

    for (final AxiomType<?> a : AxiomType.AXIOM_TYPES) {

      // TBOX
      if (AxiomType.TBoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.EQUIVALENT_CLASSES)) {
          LOG.info("=== starts testOWLEquivalentClassesAxiom()");
          assertEquals(testOWLEquivalentClassesAxiom());
          LOG.info("=== end testOWLEquivalentClassesAxiom()");
        } else if (a.equals(AxiomType.SUBCLASS_OF)) {
          LOG.info("=== starts testOWLSubClassOfAxiom()");
          assertEquals(testOWLSubClassOfAxiomA());
          assertEquals(testOWLSubClassOfAxiomB());
          LOG.info("=== ends testOWLSubClassOfAxiom()");
        } else if (a.equals(AxiomType.DISJOINT_CLASSES)) {
          LOG.info("=== starts testOWLDisjointClasses()");
          assertEquals(testOWLDisjointClasses());
          LOG.info("=== ends testOWLDisjointClasses()");
        } else if (a.equals(AxiomType.DISJOINT_UNION)) {
          LOG.info("=== starts testOWLDisjointUnion()");
          assertEquals(testOWLDisjointUnionA());
          assertEquals(testOWLDisjointUnionB());
          LOG.info("=== ends testOWLDisjointUnion()");
        } else if (a.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
          LOG.info("=== starts testOWLObjectPropertyDomain()");
          assertEquals(testOWLObjectPropertyDomain());
          LOG.info("=== ends testOWLObjectPropertyDomain()");
        } else if (a.equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
          LOG.info("=== starts testOWLObjectPropertyRange()");
          assertEquals(testOWLObjectPropertyRange());
          LOG.info("=== ends testOWLObjectPropertyRange()");
        } else if (a.equals(AxiomType.DATA_PROPERTY_DOMAIN)) {
          LOG.info("=== starts testOWLDataPropertyDomain()");
          assertEquals(testOWLDataPropertyDomain());
          LOG.info("=== ends testOWLDataPropertyDomain()");
        } else if (a.equals(AxiomType.DATA_PROPERTY_RANGE)) {
          LOG.info("=== starts testOWLDataPropertyRange()");
          assertEquals(testOWLDataPropertyRange());
          LOG.info("=== ends testOWLDataPropertyRange()");
        } else if (a.equals(AxiomType.DATATYPE_DEFINITION)) {
          LOG.info("=== starts testOWLDatatypeDefinition()");
          assertEquals(testOWLDatatypeDefinitionA());
          assertEquals(testOWLDatatypeDefinitionB());
          LOG.info("=== ends testOWLDatatypeDefinition()");
        } else if (a.equals(AxiomType.HAS_KEY)) {
          LOG.info("=== starts testOWLHasKey()");
          assertEquals(testOWLHasKey());
          LOG.info("=== ends testOWLHasKey()");
        } else if (a.equals(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
          LOG.info("=== starts testOWLFunctionalObjectProperty()");
          assertEquals(testOWLFunctionalObjectProperty());
          LOG.info("=== ends testOWLFunctionalObjectProperty()");
        } else if (a.equals(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
          LOG.info("=== starts testOWLFunctionalDataProperty()");
          assertEquals(testOWLFunctionalDataProperty());
          LOG.info("=== ends testOWLFunctionalDataProperty()");
        } else if (a.equals(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {
          LOG.info("=== starts testOWLInverseFunctionalObjectProperty()");
          assertEquals(testOWLInverseFunctionalObjectProperty());
          LOG.info("=== ends testOWLInverseFunctionalObjectProperty()");
        }
      }
      // ABOX
      else if (AxiomType.ABoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.CLASS_ASSERTION)) {
        } else if (a.equals(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION)) {
        } else if (a.equals(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION)) {
        } else if (a.equals(AxiomType.DIFFERENT_INDIVIDUALS)) {
        } else if (a.equals(AxiomType.SAME_INDIVIDUAL)) {
        } else if (a.equals(AxiomType.DATA_PROPERTY_ASSERTION)) {
        } else if (a.equals(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
        }
      }
      // RBOX
      else if (AxiomType.RBoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
        } else if (a.equals(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
        } else if (a.equals(AxiomType.SUB_DATA_PROPERTY)) {
        } else if (a.equals(AxiomType.DISJOINT_DATA_PROPERTIES)) {
        } else if (a.equals(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
        } else if (a.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
        } else if (a.equals(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
        } else if (a.equals(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
          final String text = converter.convert(
              df.getOWLSymmetricObjectPropertyAxiom(df.getOWLObjectProperty("isInLawOf", pm)));
          Assert.assertEquals("X's being in law of Y implies Y is in law of X. ", text);
        } else if (a.equals(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
        } else if (a.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
        } else if (a.equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
        } else if (a.equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
        } else if (a.equals(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
        }
      }
      // Else
      else {
        if (a.equals(AxiomType.DECLARATION)) {
        } else if (a.equals(AxiomType.SWRL_RULE)) {
        } else if (a.equals(AxiomType.ANNOTATION_ASSERTION)) {
        } else if (a.equals(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
        } else if (a.equals(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
        } else if (a.equals(AxiomType.SUB_ANNOTATION_PROPERTY_OF)) {
          // should never be reached
          LOG.info("Test not handles: {}", a.getName());
        }
      }
    }
  }

  @Test
  public void testNothing() {
    final Pair<String, String> pair = Pair.of(//
        "Every person without children is a person that has no child. ", //
        converter.convert(df.getOWLEquivalentClassesAxiom(//
            df.getOWLClass("personWithoutChildren", pm), //
            df.getOWLObjectIntersectionOf(//
                person, //
                df.getOWLObjectAllValuesFrom(df.getOWLObjectProperty("hasChild", pm),
                    df.getOWLNothing()//
                )))//
        ));

    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  @Test
  public void testSelf() {
    final OWLObjectProperty know = df.getOWLObjectProperty("know", pm);
    final OWLClass animal = df.getOWLClass("animal", pm);

    final Pair<String, String> pair = Pair.of(//
        "Every person is an animal that knows oneself. ", //
        // Every person is an animal that knows itself.
        converter.convert(df.getOWLEquivalentClassesAxiom(//
            person, //
            df.getOWLObjectIntersectionOf(animal, df.getOWLObjectHasSelf(know)))//
        ));

    LOG.info(pair.getRight());
    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  @Test
  public void testSelfB() {
    final OWLObjectProperty love = df.getOWLObjectProperty("love", pm);
    final OWLClass narcisticPerson = df.getOWLClass("NarcisticPerson", pm);

    final Pair<String, String> pair = Pair.of(//
        "Every narcistic person is a person that loves oneself. ",
        // TODO: that is wrong, because the person check fails with: "person" is not Person
        converter.convert(df.getOWLEquivalentClassesAxiom(//
            narcisticPerson, //
            df.getOWLObjectIntersectionOf(person, df.getOWLObjectHasSelf(love)))//
        ));

    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  public Pair<String, String> testOWLEquivalentClassesAxiom() {
    return Pair.of(//
        "Every boy is a child whose a man. ", //
        converter.convert(df.getOWLEquivalentClassesAxiom(//
            boy, //
            df.getOWLObjectIntersectionOf(child, man))//
        ));
  }

  public Pair<String, String> testOWLSubClassOfAxiomA() {
    return Pair.of(//
        "Every place is a thing. ", //
        converter.convert(df.getOWLSubClassOfAxiom(//
            place, thing)//
        ));
  }

  public Pair<String, String> testOWLSubClassOfAxiomB() {
    return Pair.of(//
        "Every place is a place. ", //
        converter.convert(df.getOWLSubClassOfAxiom(//
            place, place)//
        ));
  }

  public Pair<String, String> testOWLDisjointClasses() {
    return Pair.of(""//
        + "Every boy is something that is not a girl. "
        + "Every boy is something that is not a place. "
        + "Every girl is something that is not a place. ",
        converter.convert(df.getOWLDisjointClassesAxiom(//
            boy, girl, place)//
        ));
  }

  public Pair<String, String> testOWLDisjointUnionA() {
    return Pair.of(""//
        + "Every football player is an american football player, "
        + "a canadian football player or a german football player. "
        + "Every american football player is something that is not a canadian football player. "
        + "Every american football player is something that is not a german football player. "
        + "Every canadian football player is something that is not a german football player. ", //
        converter.convert(//
            df.getOWLDisjointUnionAxiom(//
                df.getOWLClass("FootballPlayer", pm), //
                new HashSet<>(Arrays.asList(//
                    df.getOWLClass("GermanFootballPlayer", pm), //
                    df.getOWLClass("AmericanFootballPlayer", pm), //
                    df.getOWLClass("CanadianFootballPlayer", pm)))//
            )//
        ));
  }

  public Pair<String, String> testOWLDisjointUnionB() {
    return Pair.of(//
        "Every child is a boy or a girl. Every boy is something that is not a girl. ", //
        converter.convert(//
            df.getOWLDisjointUnionAxiom(//
                child, //
                new HashSet<>(Arrays.asList(boy, girl)) //
            )//
        ));
  }

  public Pair<String, String> testOWLObjectPropertyDomain() {
    return Pair.of(//
        "Everything that has a dog is a person. ", //
        converter.convert(//
            df.getOWLObjectPropertyDomainAxiom(//
                df.getOWLObjectProperty("hasDog", pm), person)//
        ));
  }

  public Pair<String, String> testOWLObjectPropertyRange() {
    return Pair.of(//
        "Everything is something that has as dog only a person. ", //
        converter.convert(//
            df.getOWLObjectPropertyRangeAxiom(//
                df.getOWLObjectProperty("hasDog", pm), person)//
        ));
  }

  public Pair<String, String> testOWLDataPropertyDomain() {
    return Pair.of(//
        "Everything that has name is a person. ", //
        converter.convert(//
            df.getOWLDataPropertyDomainAxiom(//
                df.getOWLDataProperty("hasName", pm), person)//
        ));
  }

  public Pair<String, String> testOWLDataPropertyRange() {
    return Pair.of(//
        "Everything is something that has name only string. ", //
        converter.convert(//
            df.getOWLDataPropertyRangeAxiom(//
                df.getOWLDataProperty("hasName", pm), //
                df.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"))//
            )//
        ));
  }

  public Pair<String, String> testOWLDatatypeDefinitionA() {
    return Pair.of(//
        "The datatype temperature is lower than or equals to 0.2. ", //
        converter.convert(//
            df.getOWLDatatypeDefinitionAxiom(//
                temperature, //
                df.getOWLDatatypeMaxInclusiveRestriction(0.2) //
            )//
        ));
  }

  public Pair<String, String> testOWLDatatypeDefinitionB() {
    return Pair.of(//
        "The datatype integer is greater than 10000000. ", //
        converter.convert(//
            df.getOWLDatatypeDefinitionAxiom(//
                df.getIntegerOWLDatatype(), //
                df.getOWLDatatypeMinExclusiveRestriction(10000000) //
            )//
        ));
  }

  public Pair<String, String> testOWLHasKey() {
    return Pair.of(//
        "", //
        converter.convert(//
            df.getOWLHasKeyAxiom(thing, df.getOWLDataProperty("hasName", pm)//
            )//
        ));
  }

  public Pair<String, String> testOWLFunctionalObjectProperty() {
    return Pair.of(//
        "Everything is something that has at most one father. ", //
        converter.convert(//
            df.getOWLFunctionalObjectPropertyAxiom(//
                df.getOWLObjectProperty("hasFather", pm))//
        ));
  }

  public Pair<String, String> testOWLFunctionalDataProperty() {
    return Pair.of(//
        "Everything is something that has age at most one Literals. ", //
        converter.convert(//
            df.getOWLFunctionalDataPropertyAxiom(//
                df.getOWLDataProperty("hasAge", pm))//
        ));
  }

  public Pair<String, String> testOWLInverseFunctionalObjectProperty() {
    return Pair.of(//
        "Everything is something whose. ", //
        converter.convert(df.getOWLInverseFunctionalObjectPropertyAxiom(//
            df.getOWLObjectProperty("mother", pm))//
        ));
  }
}
