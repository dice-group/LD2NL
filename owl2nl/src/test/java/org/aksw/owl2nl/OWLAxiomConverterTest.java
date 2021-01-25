package org.aksw.owl2nl;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
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
    private static OWLObjectProperty isInLawOf;
    private static OWLObjectProperty isUncleInLawOf;
    private static OWLObjectProperty hasSex;
    private static OWLClass man;
    private static OWLClass person;
    private static OWLClass male;
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

        isUncleInLawOf = df.getOWLObjectProperty("isUncleInLawOf", pm);
        isInLawOf = df.getOWLObjectProperty("isInLawOf", pm);
        hasSex = df.getOWLObjectProperty("hasSex", pm);
        man = df.getOWLClass("man", pm);
        person = df.getOWLClass("person", pm);
        male = df.getOWLClass("male", pm);
        sex = df.getOWLClass("sex", pm);

        ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
    }

    @Test
    public void test_sub_class() throws OWLAxiomConversionException {
        axiom = df.getOWLSubClassOfAxiom(male, sex);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
    }

    @Test
    public void test_equivalent_class() throws OWLAxiomConversionException {
        axiom = df.getOWLEquivalentClassesAxiom(man,
                df.getOWLObjectIntersectionOf(person, df.getOWLObjectSomeValuesFrom(hasSex, male)));
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
    }

    @Test
    public void test_sub_object_property() throws OWLAxiomConversionException {
        axiom = df.getOWLSubObjectPropertyOfAxiom(isUncleInLawOf, isInLawOf);
        text = converter.convert(axiom);
        System.out.println(axiom + " = " + text);
    }
}