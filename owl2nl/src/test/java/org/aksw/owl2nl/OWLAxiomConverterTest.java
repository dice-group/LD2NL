package org.aksw.owl2nl;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLAxiomConverterTest {

    private static OWLAxiomConverter converter;
    private static OWLDataFactoryImpl df;

    private static OWLObjectProperty knows;
    private static OWLObjectProperty isFatherOf;
    private static OWLObjectProperty isInLawOf;
    private static OWLObjectProperty isUncleInLawOf;
    private static OWLObjectProperty isMotherOf;
    private static OWLObjectProperty hasSex;
    private static OWLObjectProperty hasMother;
    private static OWLObjectProperty hasBrother;
    private static OWLObjectProperty hasMaleSibling;

    private static OWLDataProperty hasBirthYear;
    private static OWLDataRange dataRange;

    private static OWLClass man;
    private static OWLClass woman;
    private static OWLClass person;
    private static OWLClass male;
    private static OWLClass female;
    private static OWLClass sex;

    private OWLAxiom axiom;
    String text;


    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        converter = new OWLAxiomConverter();

        df = new OWLDataFactoryImpl();
        PrefixManager pm = new DefaultPrefixManager("http://dbpedia.org/ontology/");

        knows = df.getOWLObjectProperty("knows", pm);
        isFatherOf = df.getOWLObjectProperty("isFatherOf", pm);
        isUncleInLawOf = df.getOWLObjectProperty("isUncleInLawOf", pm);
        isInLawOf = df.getOWLObjectProperty("isInLawOf", pm);
        isMotherOf = df.getOWLObjectProperty("isMotherOf", pm);
        hasSex = df.getOWLObjectProperty("hasSex", pm);
        hasMother = df.getOWLObjectProperty("hasMother", pm);
        hasBrother = df.getOWLObjectProperty("hasBrother", pm);
        hasMaleSibling = df.getOWLObjectProperty("hasMaleSibling", pm);

        hasBirthYear = df.getOWLDataProperty("hasBirthYear", pm);
//        dataRange = df.getOWLDatatype("integer", pm);

        man = df.getOWLClass("man", pm);
        woman = df.getOWLClass("woman", pm);
        person = df.getOWLClass("person", pm);
        male = df.getOWLClass("male", pm);
        female = df.getOWLClass("female", pm);
        sex = df.getOWLClass("sex", pm);

        ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
    }

    @Test
    public void testSubClass() throws OWLAxiomConversionException {
        axiom = df.getOWLSubClassOfAxiom(man, person);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("man ⊑ person", axiom.toString());
        Assert.assertEquals("every man is a person", text);
    }

    @Test
    public void testEquivalentClasses() throws OWLAxiomConversionException {
        axiom = df.getOWLEquivalentClassesAxiom(man,
                df.getOWLObjectIntersectionOf(person, df.getOWLObjectSomeValuesFrom(hasSex, male)));
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("man ≡ person ⊓ (∃ hasSex.male)", axiom.toString());
        Assert.assertEquals("every man is a person that has as sex a male", text);
    }

    @Test
    public void testDisjointClasses() throws OWLAxiomConversionException {
        axiom = df.getOWLDisjointClassesAxiom(male, female);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("female ⊑ ¬ male", axiom.toString());
        Assert.assertEquals("every female is something that is not a male", text);
    }

    @Test
    public void testSubObjectProperty() throws OWLAxiomConversionException {
        axiom = df.getOWLSubObjectPropertyOfAxiom(isUncleInLawOf, isInLawOf);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("isUncleInLawOf ⊑ isInLawOf", axiom.toString());
        Assert.assertEquals("X's being uncle in law of Y implies X is in law of Y", text);
    }

    @Test
    public void testSymmetricObjectProperty() throws OWLAxiomConversionException {
        axiom = df.getOWLSymmetricObjectPropertyAxiom(isInLawOf);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("isInLawOf ≡ isInLawOf⁻", axiom.toString());
        Assert.assertEquals("X's being in law of Y implies Y is in law of X", text);
    }

    @Test
    public void testTransitiveObjectProperty() throws OWLAxiomConversionException {
        axiom = df.getOWLTransitiveObjectPropertyAxiom(knows);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("knows ∈ R⁺", axiom.toString());
        Assert.assertEquals("X knows Z if X knows Y and Y knows Z", text);
    }

    @Test
    public void testObjectPropertyDomain() throws OWLAxiomConversionException {
        axiom = df.getOWLObjectPropertyDomainAxiom(hasMother, person);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("∃ hasMother.⊤ ⊑ person", axiom.toString());
        Assert.assertEquals("everything that has a mother is a person", text);
    }

    @Test
    public void testObjectPropertyRange() throws OWLAxiomConversionException {
        axiom = df.getOWLObjectPropertyRangeAxiom(hasMother, woman);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("⊤ ⊑ ∀ hasMother.woman", axiom.toString());
        Assert.assertEquals("everything is something that has as mother only a woman", text);
    }

    @Test
    public void testFunctionalObjectProperty() throws OWLAxiomConversionException {
        axiom = df.getOWLFunctionalObjectPropertyAxiom(hasMother);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("⊤ ⊑ ≤ 1 hasMother", axiom.toString());
        Assert.assertEquals("everything is something that has at most 1 mother", text);
    }

    @Test
    public void testEquivalentObjectProperties() throws OWLAxiomConversionException {
        axiom = df.getOWLEquivalentObjectPropertiesAxiom(hasBrother, hasMaleSibling);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("hasBrother ≡ hasMaleSibling", axiom.toString());
        Assert.assertEquals("X's having brother Y is equivalent to that X has male sibling Y", text);
    }

    @Test
    public void testInverseObjectProperties() throws OWLAxiomConversionException {
        axiom = df.getOWLInverseObjectPropertiesAxiom(hasMother, isMotherOf);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("hasMother ≡ isMotherOf⁻", axiom.toString());
        Assert.assertEquals("X's having mother Y is equivalent to that Y is mother of X", text);
    }

//    @Test
//    public void test_reflexive_object_property() throws OWLAxiomConversionException {
//        axiom = df.getOWLReflexiveObjectPropertyAxiom(knows);
//        text = converter.convert(axiom);
//        System.out.println(axiom + " = " + text);
//    }

//    @Test
//    public void test_irreflexive_object_property() throws OWLAxiomConversionException {
//        axiom = df.getOWLIrreflexiveObjectPropertyAxiom(isInLawOf);
//        text = converter.convert(axiom);
//        System.out.println(axiom + " = " + text);
//    }

//    @Test
//    public void test_inverse_functional_object_property() throws OWLAxiomConversionException {
//        axiom = df.getOWLInverseFunctionalObjectPropertyAxiom(isFatherOf);
//        text = converter.convert(axiom);
//        System.out.println(axiom + " = " + text);
//        Assert.assertEquals("⊤ ⊑ ≤ 1 isFatherOf⁻", axiom.toString());
//        Assert.assertEquals("everything is something whose", text);
//    }

    @Test
    public void testDataPropertyDomain() throws OWLAxiomConversionException {
        axiom = df.getOWLDataPropertyDomainAxiom(hasBirthYear, person);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("∃ hasBirthYear.⊤ ⊑ person", axiom.toString());
        Assert.assertEquals("everything that has birth year is a person", text);
    }

//    @Test
//    public void test_data_property_range() throws OWLAxiomConversionException {
//        axiom = df.getOWLDataPropertyRangeAxiom(hasBirthYear, dataRange);
//        text = converter.convert(axiom);
//        System.out.println(axiom + " = " + text);
//    }

    @Test
    public void testFunctionalDataProperty() throws OWLAxiomConversionException {
        axiom = df.getOWLFunctionalDataPropertyAxiom(hasBirthYear);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
        Assert.assertEquals("⊤ ⊑ ≤ 1 hasBirthYear", axiom.toString());
        Assert.assertEquals("everything is something that has birth year at most 1 Literals", text);
    }
}
