package org.aksw.owl2nl;

import java.util.HashSet;
import java.util.Set;

import org.aksw.owl2nl.converter.OWLAxiomConverter;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLAxiomConverterTest {

  private static final Logger LOG = LogManager.getLogger(OWLAxiomConverterTest.class);

  private final OWLAxiomConverter converter = new OWLAxiomConverter();
  private final OWLDataFactoryImpl df = new OWLDataFactoryImpl();
  private final PrefixManager pm =
      new DefaultPrefixManager(null, null, "http://dbpedia.org/ontology/");

  private final OWLClassExpression boy = df.getOWLClass("Boy", pm);
  private final OWLClassExpression girl = df.getOWLClass("Girl", pm);
  private final OWLClassExpression child = df.getOWLClass("Child", pm);
  private final OWLClassExpression man = df.getOWLClass("Man", pm);
  private final OWLClassExpression place = df.getOWLClass("Place", pm);
  private final OWLClassExpression person = df.getOWLClass("Person", pm);
  private final OWLClassExpression thing = df.getOWLClass("Thing", pm);

  private void assertEquals(final Pair<String, String> pair) {
    Assert.assertEquals(pair.getLeft(), pair.getRight());
  }

  @Test
  public void test() throws OWLAxiomConversionException {

    for (final AxiomType<?> a : AxiomType.AXIOM_TYPES) {

      // LOG.info("{} isLogical {} isOWL2Axiom {} ", a.getName(), a.isLogical(), a.isOWL2Axiom());

      // TBOX
      if (AxiomType.TBoxAxiomTypes.contains(a)) {
        if (a.equals(AxiomType.EQUIVALENT_CLASSES)) {
          LOG.info("=== starts testOWLEquivalentClassesAxiom()");
          assertEquals(testOWLEquivalentClassesAxiom());
          LOG.info("=== end testOWLEquivalentClassesAxiom()");
        } else if (a.equals(AxiomType.SUBCLASS_OF)) {
          LOG.info("=== starts testOWLSubClassOfAxiom()");
          assertEquals(testOWLSubClassOfAxiom());
          LOG.info("=== ends testOWLSubClassOfAxiom()");
        } else if (a.equals(AxiomType.DISJOINT_CLASSES)) {
          LOG.info("=== starts testOWLDisjointClasses()");
          assertEquals(testOWLDisjointClasses());
          LOG.info("=== ends testOWLDisjointClasses()");
        } else if (a.equals(AxiomType.DISJOINT_UNION)) {
          LOG.info("=== starts testOWLDisjointUnion()");
          assertEquals(testOWLDisjointUnion());
          assertEquals(testOWLDisjointUnion2());
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
          // TODO: implement me
          // assertEquals(testOWLDatatypeDefinition());
          LOG.info("=== ends testOWLDatatypeDefinition()");
        } else if (a.equals(AxiomType.HAS_KEY)) {
          LOG.info("=== starts testOWLHasKey()");
          // TODO: implement me
          // assertEquals(testOWLHasKey());
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
          // TODO: implement me
          // assertEquals(testOWLInverseFunctionalObjectProperty());
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
        } else {
          // should never be reached
          LOG.info("Test not handles: {}", a);
        }
      }
    }
  }

  public Pair<String, String> testOWLEquivalentClassesAxiom() throws OWLAxiomConversionException {

    final OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(//
        boy, //
        df.getOWLObjectIntersectionOf(child, man)//
    );
    return Pair.of(//
        "Every boy is a child whose a man. ", //
        converter.convert(axiom)//
    );
  }

  public Pair<String, String> testOWLSubClassOfAxiom() throws OWLAxiomConversionException {

    final OWLAxiom axiom = df.getOWLSubClassOfAxiom(//
        place, thing//
    );
    return Pair.of(//
        "Every place is a thing. ", //
        converter.convert(axiom)//
    );
  }

  public Pair<String, String> testOWLDisjointClasses() throws OWLAxiomConversionException {

    final OWLAxiom axiom = df.getOWLDisjointClassesAxiom(//
        boy, girl);

    return Pair.of(//
        "Every boy is something that is not a girl. ", //
        converter.convert(axiom)//
    );
  }

  public Pair<String, String> testOWLDisjointUnion2() throws OWLAxiomConversionException {
    final Set<OWLClassExpression> classExpressions = new HashSet<>();
    classExpressions.add(df.getOWLClass("GermanFootballPlayer", pm));
    classExpressions.add(df.getOWLClass("AmericanFootballPlayer", pm));
    classExpressions.add(df.getOWLClass("CanadianFootballPlayer", pm));

    final String converted = converter.convert(//
        df.getOWLDisjointUnionAxiom(//
            df.getOWLClass("FootballPlayer", pm), //
            classExpressions //
        ));

    return Pair.of(//
        "Every football player is an american football player, "
            + "a canadian football player or a german football player."
            + " Every american football player is something that is not a canadian football player."
            + " Every american football player is something that is not a german football player."
            + " Every canadian football player is something that is not a german football player. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLDisjointUnion() throws OWLAxiomConversionException {

    final Set<OWLClassExpression> classExpressions = new HashSet<>();
    classExpressions.add(boy);
    classExpressions.add(girl);

    final String converted = converter.convert(//
        df.getOWLDisjointUnionAxiom(//
            df.getOWLClass("Child", pm), //
            classExpressions //
        ));

    return Pair.of(//
        "Every child is a boy or a girl. Every boy is something that is not a girl. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLObjectPropertyDomain() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLObjectPropertyDomainAxiom(//
            df.getOWLObjectProperty("hasDog", pm), person)//
    );

    return Pair.of(//
        "Everything that has a dog is a person. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLObjectPropertyRange() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLObjectPropertyRangeAxiom(//
            df.getOWLObjectProperty("hasDog", pm), person)//
    );

    return Pair.of(//
        "Everything is something that has as dog only a person. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLDataPropertyDomain() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLDataPropertyDomainAxiom(//
            df.getOWLDataProperty("hasName", pm), person)//
    );

    return Pair.of(//
        "Everything that has name is a person. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLDataPropertyRange() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLDataPropertyRangeAxiom(//
            df.getOWLDataProperty("hasName", pm), //
            df.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"))//
        )//
    );

    return Pair.of(//
        "Everything is something that has name only string. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLDatatypeDefinition() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLDatatypeDefinitionAxiom(//
            df.getIntegerOWLDatatype(), //
            df.getOWLDatatypeMinInclusiveRestriction(10000000) //
        )//
    );
    return Pair.of(//
        "null", //
        converted//
    );
  }

  public Pair<String, String> testOWLHasKey() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLHasKeyAxiom(thing, df.getOWLDataProperty("hasName", pm)//
        )//
    );

    return Pair.of(//
        "null", //
        converted//
    );
  }

  public Pair<String, String> testOWLFunctionalObjectProperty() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLFunctionalObjectPropertyAxiom(//
            df.getOWLObjectProperty("hasFather", pm))//
    );

    return Pair.of(//
        "Everything is something that has at most 1 father. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLFunctionalDataProperty() throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLFunctionalDataPropertyAxiom(//
            df.getOWLDataProperty("hasAge", pm))//
    );

    return Pair.of(//
        "Everything is something that has age at most 1 Literals. ", //
        converted//
    );
  }

  public Pair<String, String> testOWLInverseFunctionalObjectProperty()
      throws OWLAxiomConversionException {

    final String converted = converter.convert(//
        df.getOWLInverseFunctionalObjectPropertyAxiom(//
            df.getOWLObjectProperty("fatherOf", pm)//
        )//
    );

    return Pair.of(//
        "null", //
        converted//
    );
  }
}
