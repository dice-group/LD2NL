package org.aksw.owl2nl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OptimizerDepParseTest {
    private static OWLClassExpressionConverter converter;
    private static OWLDataFactoryImpl df;

    private static OWLClass company;
    private static OWLClass man;
    private static OWLClass softwareCompany;

    private static OWLNamedIndividual paderborn;
    private static OWLNamedIndividual karaoke;
    private static OWLNamedIndividual Jazz;
    private static OWLNamedIndividual Cricket;
    private static OWLNamedIndividual football;
    private static OWLNamedIndividual hockey;
    private static OWLNamedIndividual tennis;
    private static OWLNamedIndividual golf;
    private static OWLNamedIndividual hiphop;
    private static OWLNamedIndividual rock;

    private static OWLObjectProperty worksFor;
    private static OWLObjectProperty ledBy;
    private static OWLDataProperty amountOfSalary;
    private static OWLObjectProperty sings;
    private static OWLObjectProperty plays;
    private static OWLObjectProperty workPlace;
    private static OWLObjectProperty birthPlace;
    private static OWLLiteral salary;
    private static OWLDataProperty nrOfInhabitants;
    private static OWLDataRange dataRange;

    OWLClassExpression ce;

    String text;
    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        converter = new OWLClassExpressionConverter();

        df = new OWLDataFactoryImpl();
        PrefixManager pm = new DefaultPrefixManager("http://dbpedia.org/ontology/");

        worksFor = df.getOWLObjectProperty("worksFor", pm);
        ledBy = df.getOWLObjectProperty("isLedBy", pm);
        sings = df.getOWLObjectProperty("sing", pm);
        plays = df.getOWLObjectProperty("play", pm);
        company = df.getOWLClass("Company", pm);
        man = df.getOWLClass("Man", pm);
        softwareCompany = df.getOWLClass("SoftwareCompany", pm);
        salary = df.getOWLLiteral(40000);
        amountOfSalary = df.getOWLDataProperty("amountOfSalary", pm);
        birthPlace = df.getOWLObjectProperty("birthPlace", pm);
        worksFor = df.getOWLObjectProperty("worksFor", pm);
        ledBy = df.getOWLObjectProperty("isLedBy", pm);

        workPlace = df.getOWLObjectProperty("workPlace", pm);
        paderborn = df.getOWLNamedIndividual("Paderborn", pm);
        karaoke = df.getOWLNamedIndividual("karaoke", pm);
        Jazz = df.getOWLNamedIndividual("jazz", pm);
        football = df.getOWLNamedIndividual("football", pm);
        Cricket = df.getOWLNamedIndividual("cricket", pm);
        hockey = df.getOWLNamedIndividual("hockey", pm);
        tennis= df.getOWLNamedIndividual("tennis", pm);
        golf= df.getOWLNamedIndividual("golf", pm);
        hiphop= df.getOWLNamedIndividual("hiphop", pm);
        rock=df.getOWLNamedIndividual("rock", pm);


        nrOfInhabitants = df.getOWLDataProperty("nrOfInhabitants", pm);
        dataRange = df.getOWLDatatypeMinInclusiveRestriction(10000000);

        ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
    }

    @Test
    public void testSubjectAndVerbAggregation() {

        // a person that sings karaoke or a person that sings  jazz

        ce = df.getOWLObjectUnionOf(df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, karaoke), man),
                df.getOWLObjectIntersectionOf(df.getOWLObjectHasValue(sings, Jazz), man));
        text = converter.convert(ce);
        System.out.println(ce + "=" + text);
    }
}
