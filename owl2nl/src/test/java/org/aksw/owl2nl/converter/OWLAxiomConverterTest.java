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
import static org.aksw.owl2nl.converter.DataHelper.dpo;
import static org.aksw.owl2nl.converter.DataHelper.minExclusive;
import static org.aksw.owl2nl.converter.DataHelper.temperature;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.animal;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.boy;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.child;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.dog;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.french;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.girl;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.homoSapien;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.human;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.man;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.narcisticPerson;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.person;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.place;
import static org.aksw.owl2nl.converter.DataHelper.OWLClassHelper.thing;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.hasAge;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.lastName;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.name;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.numberOfChildren;
import static org.aksw.owl2nl.converter.DataHelper.OWLDataPropertyHelper.surname;
import static org.aksw.owl2nl.converter.DataHelper.OWLNamedIndividualHelper.france;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.ancestorOf;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.aunt;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.birthPlace;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.brother;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.father;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.fatherOf;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.friend;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.hasChild;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.hasDog;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.hasSSN;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.know;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.locatedIn;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.love;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.maleSibling;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.mother;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.parent;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.plays;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.sings;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.sister;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.worksFor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import org.aksw.owl2nl.converter.DataHelper.OWLClassHelper;
import org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;

public class OWLAxiomConverterTest {

  protected final static OWLAxiomConverter axiomConverter = new OWLAxiomConverter();

  private void assertEquals(final String methodName) {
    LOG.info("=== starts {}", methodName);
    try {
      final Method method = OWLAxiomConverterTest.class.getMethod(methodName);
      @SuppressWarnings("unchecked")
      final Pair<String, String> pair = (Pair<String, String>) method.invoke(this);

      LOG.info(pair.getLeft());
      LOG.info(pair.getRight());

      Assert.assertEquals(pair.getLeft(), pair.getRight());

    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void test() {
    for (final AxiomType<?> a : AxiomType.AXIOM_TYPES) {
      // TBOX
      if (AxiomType.TBoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.EQUIVALENT_CLASSES)) {
          assertEquals("testsOWLEquivalentClassesAxiomA");
          assertEquals("testsOWLEquivalentClassesAxiomB");
          assertEquals("testsOWLEquivalentClassesAxiomC");
          assertEquals("testsOWLEquivalentClassesAxiomE");
          assertEquals("testsOWLEquivalentClassesAxiomD");
        } else if (a.equals(AxiomType.SUBCLASS_OF)) {
          assertEquals("testsOWLSubClassOfAxiomA");
          assertEquals("testsOWLSubClassOfAxiomB");
        } else if (a.equals(AxiomType.DISJOINT_CLASSES)) {
          assertEquals("testsOWLDisjointClasses");
        } else if (a.equals(AxiomType.DISJOINT_UNION)) {
          assertEquals("testsOWLDisjointUnionA");
          assertEquals("testsOWLDisjointUnionB");
        } else if (a.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
          assertEquals("testsOWLObjectPropertyDomain");
        } else if (a.equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
          assertEquals("testsOWLObjectPropertyRange");
        } else if (a.equals(AxiomType.DATA_PROPERTY_DOMAIN)) {
          assertEquals("testsOWLDataPropertyDomain");
        } else if (a.equals(AxiomType.DATA_PROPERTY_RANGE)) {
          assertEquals("testsOWLDataPropertyRange");
        } else if (a.equals(AxiomType.DATATYPE_DEFINITION)) {
          assertEquals("testsOWLDatatypeDefinitionA");
          assertEquals("testsOWLDatatypeDefinitionB");
        } else if (a.equals(AxiomType.HAS_KEY)) {
          assertEquals("testsOWLHasKeyA");
          assertEquals("testsOWLHasKeyB");
        } else if (a.equals(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
          assertEquals("testsOWLFunctionalObjectProperty");
        } else if (a.equals(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
          assertEquals("testsOWLFunctionalDataProperty");
          assertEquals("testsOWLFunctionalDataPropertyB");
        } else if (a.equals(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {
          assertEquals("testsOWLInverseFunctionalObjectProperty");
        } else {
          // should never be reached
          LOG.info("Not implemented:{}", a.getName());
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
        } else {
          // should never be reached
          LOG.info("Not implemented:{}", a.getName());
        }
      }
      // RBOX
      else if (AxiomType.RBoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY)) {
          assertEquals("testsOWLIrreflexiveObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
          assertEquals("testsOWLEquivalentDataPropertiesAxiomA");
          assertEquals("testsOWLEquivalentDataPropertiesAxiomB");
        } else if (a.equals(AxiomType.SUB_DATA_PROPERTY)) {
          assertEquals("testsOWLSubDataPropertyOfAxiomA");
          assertEquals("testsOWLSubDataPropertyOfAxiomB");
        } else if (a.equals(AxiomType.DISJOINT_DATA_PROPERTIES)) {
          assertEquals("testsOWLDisjointDataPropertiesAxiom");
        } else if (a.equals(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
          assertEquals("testsOWLEquivalentObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
          assertEquals("testsOWLSubObjectPropertyOfAxiomA");
          assertEquals("testsOWLSubPropertyChainOfAxiom");
        } else if (a.equals(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
          assertEquals("testsOWLInverseObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
          assertEquals("testsOWLSymmetricObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.ASYMMETRIC_OBJECT_PROPERTY)) {
          assertEquals("testsOWLAsymmetricObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
          assertEquals("testsOWLTransitiveObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.REFLEXIVE_OBJECT_PROPERTY)) {
          assertEquals("testsOWLReflexiveObjectPropertiesAxiom");
        } else if (a.equals(AxiomType.DISJOINT_OBJECT_PROPERTIES)) {
          assertEquals("testsOWLDisjointObjectPropertiesAxiom");
          // } else if (a.equals(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
          // TODO:?
          // assertEquals("testsOWLSubPropertyChainOfAxiom");
        } else {
          // should never be reached
          LOG.info("Not implemented:{}", a.getName());
        }
      }
      // Else
      else {
        // if (a.equals(AxiomType.DECLARATION)) {
        // } else if (a.equals(AxiomType.SWRL_RULE)) {
        // } else if (a.equals(AxiomType.ANNOTATION_ASSERTION)) {
        // } else if (a.equals(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
        // } else if (a.equals(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
        // } else if (a.equals(AxiomType.SUB_ANNOTATION_PROPERTY_OF)) {
        // } else {
        // should never be reached
        LOG.info("Not implemented:{}", a.getName());

        // }
      }
    }
  }

  // #########################################################
  // ################# object property axioms ################
  // #########################################################

  public Pair<String, String> testOWLSubObjectPropertyOfAxiomA() {
    return Pair.of(//
        "Singing implies playing. ", //
        axiomConverter.convert(df.getOWLSubObjectPropertyOfAxiom(sings, plays))//
    );
  }

  // TODO: comma before then
  public Pair<String, String> testsOWLSubPropertyChainOfAxiom() {
    return Pair.of(//
        "If an individual is connected by the sequence of the mother and sister object property"
            // + " with another individual, then the individual is also connected by the "
            + " with another individual then the individual is also connected by the "
            + "aunt object property with the other individual. ", //
        axiomConverter.convert(df.getOWLSubPropertyChainOfAxiom(//
            Arrays.asList(mother, sister), aunt))//
    );
  }

  public Pair<String, String> testsOWLEquivalentObjectPropertiesAxiom() {
    return Pair.of(//
        "The two object properties brother and male sibling are synonyms. ", //
        axiomConverter.convert(df.getOWLEquivalentObjectPropertiesAxiom(brother, maleSibling))//
    );
  }

  public Pair<String, String> testsOWLDisjointObjectPropertiesAxiom() {
    return Pair.of(//
        "The two object properties father and mother are pairwise disjoint. ",
        axiomConverter.convert(df.getOWLDisjointObjectPropertiesAxiom(father, mother))//
    );
  }

  public Pair<String, String> testsOWLInverseObjectPropertiesAxiom() {
    return Pair.of(//
        "The father of property is the opposite of the father property. ",
        axiomConverter.convert(df.getOWLInverseObjectPropertiesAxiom(father, fatherOf))//
    );
  }

  public Pair<String, String> testsOWLObjectPropertyDomain() {
    return Pair.of(//
        "The domain of the has dog object property is a person. ", //
        axiomConverter.convert(df.getOWLObjectPropertyDomainAxiom(hasDog, person))//
    );
  }

  public Pair<String, String> testsOWLObjectPropertyRange() {
    return Pair.of(//
        "The range of the has dog object property is a dog. ", //
        axiomConverter.convert(df.getOWLObjectPropertyRangeAxiom(hasDog, dog))//
    );
  }

  public Pair<String, String> testsOWLFunctionalObjectProperty() {
    return Pair.of(//
        "An individual can have at most one value for father. ", //
        axiomConverter.convert(df.getOWLFunctionalObjectPropertyAxiom(father))//
    );
  }

  // TODO:
  public Pair<String, String> testsOWLInverseFunctionalObjectProperty() {
    return Pair.of(//
        // Each object can have at most one father.
        // "Each object can have at most one father. ", //
        "Everything is something whose. ",
        axiomConverter.convert(df.getOWLInverseFunctionalObjectPropertyAxiom(fatherOf))//
    );
  }

  // TODO:
  public Pair<String, String> testsOWLReflexiveObjectPropertiesAxiom() {
    return Pair.of(//
        "Everything is something that knows oneself. ",
        // "Everybody knows themselves. ", //
        axiomConverter.convert(df.getOWLReflexiveObjectPropertyAxiom(know))//
    );
  }

  // TODO: add another test for verbs: "Nobody can be married to themselves."
  public Pair<String, String> testsOWLIrreflexiveObjectPropertiesAxiom() {
    return Pair.of(//
        "Everything is something that does not mother oneself. ",
        // "Nobody can be a mother to themselves. ", //
        axiomConverter.convert(df.getOWLIrreflexiveObjectPropertyAxiom(mother))//
    );
  }

  // TODO:
  public Pair<String, String> testsOWLSymmetricObjectPropertiesAxiom() {
    return Pair.of(//
        "Friend implies friend. ",
        // "If x is a friend of y, then y is a friend of x.", //
        axiomConverter.convert(df.getOWLSymmetricObjectPropertyAxiom(friend))//
    );
  }

  // TODO: comma before then
  public Pair<String, String> testsOWLAsymmetricObjectPropertiesAxiom() {
    return Pair.of(//
        // "If x is a parent of y, then y is not a parent of x. ", //
        "If an individual is connected with the parent object property to another individual "
            + "then this other individual is not connected with the parent object property to the individual. ", //
        axiomConverter.convert(df.getOWLAsymmetricObjectPropertyAxiom(parent))//
    );
  }

  // TODO: comma before then
  public Pair<String, String> testsOWLTransitiveObjectPropertiesAxiom() {
    return Pair.of(//
        "If an individual is connected by the ancestor of object property "//
            + "with another individual then the individual is also connected by the ancestor of "//
            // + "with another individual, then the individual is also connected by the ancestor of
            // "//
            + "object property with the other individual. ",
        // "If x is an ancestor of y and y is an ancestor of z, then x is an ancestor of z. ", //
        axiomConverter.convert(df.getOWLTransitiveObjectPropertyAxiom(ancestorOf))//
    );
  }

  // #########################################################
  // ################# data property axioms ##################
  // #########################################################

  public Pair<String, String> testsOWLSubObjectPropertyOfAxiomA() {
    return Pair.of(//
        "Singing implies playing. ", //
        axiomConverter.convert(df.getOWLSubObjectPropertyOfAxiom(sings, plays))//
    );
  }

  // TODO
  public Pair<String, String> testsOWLSubDataPropertyOfAxiomA() {
    return Pair.of(//
        "Last name is naming. ",
        // "A last name of someone is his/her name as well. ", //
        axiomConverter.convert(df.getOWLSubDataPropertyOfAxiom(lastName, name))//
    );
  }

  // TODO
  public Pair<String, String> testsOWLSubDataPropertyOfAxiomB() {
    return Pair.of(//
        "Last name is naming. ",
        // "Last name is names. ", //
        axiomConverter.convert(df.getOWLSubDataPropertyOfAxiom(lastName, name))//
    );
  }

  public Pair<String, String> testsOWLEquivalentDataPropertiesAxiomB() {
    return Pair.of(//
        "The two data properties last name and name are synonyms. ", //
        axiomConverter.convert(df.getOWLEquivalentDataPropertiesAxiom(lastName, name))//
    );
  }

  public Pair<String, String> testsOWLDisjointDataPropertiesAxiom() {
    return Pair.of(//
        "The three data properties age, last name and name are pairwise disjoint. ",
        // "Someone's name must be different from his age. ", //
        axiomConverter.convert(df.getOWLDisjointDataPropertiesAxiom(name, lastName, hasAge))//
    );
  }

  public Pair<String, String> testsOWLDataPropertyDomain() {
    return Pair.of(//
        "The domain of the name property is a person. ", //
        axiomConverter.convert(df.getOWLDataPropertyDomainAxiom(name, person))//
    );
  }

  public Pair<String, String> testsOWLDataPropertyRange() {
    return Pair.of(//
        "The range of the name property is a string. ", //
        axiomConverter.convert(df.getOWLDataPropertyRangeAxiom(//
            name, df.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"))//
        ))//
    );
  }

  public Pair<String, String> testsOWLDatatypeDefinitionA() {
    return Pair.of(//
        "The datatype temperature is lower than or equals to 0.2. ", //
        axiomConverter.convert(//
            df.getOWLDatatypeDefinitionAxiom(//
                temperature, //
                df.getOWLDatatypeMaxInclusiveRestriction(0.2) //
            ))//
    );
  }

  public Pair<String, String> testsOWLDatatypeDefinitionB() {
    return Pair.of(//
        "The datatype integer is greater than 10000000. ", //
        axiomConverter.convert(//
            df.getOWLDatatypeDefinitionAxiom(df.getIntegerOWLDatatype(), minExclusive))//
    );
  }

  public Pair<String, String> testsOWLEquivalentDataPropertiesAxiomA() {
    return Pair.of(//
        "The three data properties last name, name and surname are synonyms. ", //
        axiomConverter.convert(df.getOWLEquivalentDataPropertiesAxiom(lastName, name, surname))//
    );
  }

  public Pair<String, String> testsOWLFunctionalDataProperty() {
    return Pair.of(//
        "An individual can have at most one value for age. ", //
        axiomConverter.convert(df.getOWLFunctionalDataPropertyAxiom(hasAge))//
    );
  }

  public Pair<String, String> testsOWLFunctionalDataPropertyB() {
    return Pair.of(//
        "An individual can have at most one value for number of children. ", //
        axiomConverter.convert(df.getOWLFunctionalDataPropertyAxiom(numberOfChildren))//
    );
  }

  // #########################################################
  // ################# class axioms ##########################
  // #########################################################

  public Pair<String, String> testsOWLEquivalentClassesAxiomA() {
    return Pair.of(//
        "Every homo sapien is a human. ", //
        axiomConverter.convert(df.getOWLEquivalentClassesAxiom(human, homoSapien))//
    );
  }

  public Pair<String, String> testsOWLEquivalentClassesAxiomB() {
    return Pair.of(//
        "Every boy is a child whose a man. ", //
        axiomConverter.convert(
            df.getOWLEquivalentClassesAxiom(boy, df.getOWLObjectIntersectionOf(child, man)))//
    );
  }

  public Pair<String, String> testsOWLEquivalentClassesAxiomD() {
    return Pair.of(//
        "Every professor is a person that works for an university. ", //
        axiomConverter.convert(
            df.getOWLEquivalentClassesAxiom(OWLClassHelper.professor, df.getOWLObjectIntersectionOf(
                df.getOWLObjectSomeValuesFrom(worksFor, OWLClassHelper.university), person)))//
    );
  }

  public Pair<String, String> testsOWLEquivalentClassesAxiomE() {
    return Pair.of(//
        "Every placebo is a drug that has for active principle an active principle. ",
        axiomConverter.convert(//
            df.getOWLEquivalentClassesAxiom(//
                DataHelper.OWLClassHelper.placebo, //
                df.getOWLObjectIntersectionOf(//
                    DataHelper.OWLClassHelper.drug, //
                    df.getOWLObjectSomeValuesFrom(OWLObjectPropertyHelper.has_for_active_principle,
                        OWLClassHelper.activePrinciple)//
                )//
            )//
        )//
    );
  }

  public Pair<String, String> testsOWLEquivalentClassesAxiomC() {
    return Pair.of(//
        "Every placebo is a drug that does not have for active principle an active principle. ",
        axiomConverter.convert(//
            df.getOWLEquivalentClassesAxiom(//
                DataHelper.OWLClassHelper.placebo, //
                df.getOWLObjectIntersectionOf(//
                    DataHelper.OWLClassHelper.drug, //
                    df.getOWLObjectComplementOf(//
                        df.getOWLObjectSomeValuesFrom(
                            OWLObjectPropertyHelper.has_for_active_principle,
                            OWLClassHelper.activePrinciple//
                        )//
                    )//
                )//
            )//
        )//
    );
  }

  public Pair<String, String> testsOWLSubClassOfAxiomA() {
    return Pair.of(//
        "Every place is a thing. ", //
        axiomConverter.convert(df.getOWLSubClassOfAxiom(place, thing)));
  }

  public Pair<String, String> testsOWLSubClassOfAxiomB() {
    return Pair.of(//
        "Every place is a place. ", //
        axiomConverter.convert(df.getOWLSubClassOfAxiom(place, place)));
  }

  public Pair<String, String> testsOWLDisjointClasses() {
    return Pair.of(""//
        + "Every boy is something that is not a girl and is something that is not a place. " //
        + "Every girl is something that is not a place. ",
        axiomConverter.convert(df.getOWLDisjointClassesAxiom(//
            boy, girl, place)//
        ));
  }

  public Pair<String, String> testsOWLDisjointUnionA() {
    return Pair.of(""//
        + "Every football player is an american football player, a canadian football player or a german football player. "
        + "Every american football player is something that is not a canadian football player and is something that is not a german football player. "
        + "Every canadian football player is something that is not a german football player. ",
        axiomConverter.convert(//
            df.getOWLDisjointUnionAxiom(//
                df.getOWLClass("FootballPlayer", dpo), //
                new HashSet<>(Arrays.asList(//
                    df.getOWLClass("GermanFootballPlayer", dpo), //
                    df.getOWLClass("AmericanFootballPlayer", dpo), //
                    df.getOWLClass("CanadianFootballPlayer", dpo)))//
            )//
        ));
  }

  // TODO:
  public Pair<String, String> testsOWLDisjointUnionB() {
    return Pair.of(//
        // Every child is a boy or a girl, and a boy is never a girl.
        "Every child is a boy or a girl. Every boy is something that is not a girl. ", //
        axiomConverter.convert(//
            df.getOWLDisjointUnionAxiom(//
                child, //
                new HashSet<>(Arrays.asList(boy, girl)) //
            )//
        ));
  }
  // #########################################################
  // ##################### other tests #######################
  // #########################################################

  @Test
  public void testNothing() {
    final Pair<String, String> pair = Pair.of(//
        "Every person without children is a person that has no child. ", //
        axiomConverter.convert(df.getOWLEquivalentClassesAxiom(//
            df.getOWLClass("personWithoutChildren", dpo), //
            df.getOWLObjectIntersectionOf(//
                person, //
                df.getOWLObjectAllValuesFrom(hasChild, df.getOWLNothing()//
                )))//
        ));

    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  @Test
  public void testSelf() {
    final Pair<String, String> pair = Pair.of(//
        "Every person is an animal that knows oneself. ", //
        axiomConverter.convert(df.getOWLEquivalentClassesAxiom(//
            person, //
            df.getOWLObjectIntersectionOf(animal, df.getOWLObjectHasSelf(know)))//
        ));

    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  // TODO: Every narcissistic person is a person who loves oneself.
  @Test
  public void testSelfB() {
    final Pair<String, String> pair = Pair.of(//
        "Every narcissistic person is a person who loves oneself. ",
        axiomConverter.convert(df.getOWLEquivalentClassesAxiom(//
            narcisticPerson, //
            df.getOWLObjectIntersectionOf(person, df.getOWLObjectHasSelf(love)))//
        ));

    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  public Pair<String, String> testsOWLHasKeyA() { // DataPropertyExpression
    return Pair.of(//
        "Every person is uniquely identified by its name. ", //
        axiomConverter.convert(df.getOWLHasKeyAxiom(person, name)));
  }

  public Pair<String, String> testsOWLHasKeyB() { // ObjectPropertyExpression
    return Pair.of(//
        "Every person is uniquely identified by its social security number. ", //
        axiomConverter.convert(df.getOWLHasKeyAxiom(person, hasSSN)));
  }

  /**
   * not explicit in the OWL 2 specification<br>
   * <code>
  public void testsSWRLRule() {

    final Set<SWRLAtom> concequent = new HashSet<>();
    final Set<SWRLAtom> antecedent = new HashSet<>();

    concequent.add(df.getSWRLClassAtom(person, df.getSWRLIndividualArgument(albert)));
    antecedent.add(df.getSWRLClassAtom(professor, df.getSWRLIndividualArgument(albert)));

    final String text = axiomConverter.convert(df.getSWRLRule(antecedent, concequent));

    LOG.info(text);
  }

  public Pair<String, String> testsOWLSubPropertyChainOfAxiom() {
  }
  </code>
   */

  @Test
  public void testA() {
    assertEquals("_testA");
  }

  public Pair<String, String> _testA() {
    // French EquivalentTo Person and (birthPlace some (Place and (locatedIn some France)))
    return Pair.of(//
        // people whose birth place is a city that is located in France.
        "Every french is a person whose birth place is a place that locates in France. ", //
        axiomConverter.convert(//
            df.getOWLEquivalentClassesAxiom(//
                french, df.getOWLObjectIntersectionOf(//
                    person, df.getOWLObjectSomeValuesFrom(//
                        birthPlace, df.getOWLObjectIntersectionOf(//
                            place, df.getOWLObjectHasValue(//
                                locatedIn, france//
                            )//
                        )//
                    )//
                )//
            )//
        )//
    );
  }
}
