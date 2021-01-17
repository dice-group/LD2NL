package org.aksw.owl2nl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLPropertyExpressionConverterTest {
    private static OWLPropertyExpressionConverter converter;

    OWLObjectPropertyExpression pe;
    private static OWLDataFactoryImpl df;
    private static OWLObjectProperty hasWorkPlace;
    private static OWLObjectProperty owner;
    private static OWLObjectProperty plays;
    String text;

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        converter = new OWLPropertyExpressionConverter();

        df = new OWLDataFactoryImpl();
        PrefixManager pm = new DefaultPrefixManager("http://dbpedia.org/ontology/");

        plays = df.getOWLObjectProperty("play",pm);
        owner = df.getOWLObjectProperty("owner", pm);
        hasWorkPlace = df.getOWLObjectProperty("hasWorkPlace", pm);

        ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
    }

    @Test
    public void testWithVerbProperty() {
        // Verbalise the property hasWorkPlace, which is a verb phrase
        pe = hasWorkPlace;
        text = converter.convert(pe);
        System.out.println(pe + " = " + text);
    }

    @Test
    public void testWithNounProperty() {
        // Verbalise the property owner, which is a noun
        pe = owner;
        text = converter.convert(pe);
        System.out.println(pe + " = " + text);
    }

    @Test
    public void testWithVerbProperty2() {
        // Verbalise the property plays, which is a verb
        pe = plays;
        text = converter.convert(pe);
        System.out.println(pe + " = " + text);
    }
}
