package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.data.OWL2NLInput;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

@Deprecated
public class OWLPropertyExpressionConverterTest {

  private final OWLPropertyExpressionConverter converter =
      new OWLPropertyExpressionConverter(new OWL2NLInput());

  private OWLObjectPropertyExpression pe;
  private final OWLDataFactoryImpl df = new OWLDataFactoryImpl();
  private final PrefixManager pm =
      new DefaultPrefixManager(null, null, "http://dbpedia.org/ontology/");

  private final OWLObjectProperty plays = df.getOWLObjectProperty("play", pm);
  private final OWLObjectProperty owner = df.getOWLObjectProperty("owner", pm);
  private final OWLObjectProperty hasWorkPlace = df.getOWLObjectProperty("hasWorkPlace", pm);;
  String text;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  @Test
  public void testWithVerbProperty() {
    // Verbalise the property hasWorkPlace, which is a verb phrase

    pe = hasWorkPlace;
    text = converter.convert(pe);
    System.out.println(pe + " = " + text);
    Assert.assertEquals("hasWorkPlace", pe.toString());
    Assert.assertEquals("X has work place Y", text);
  }

  @Test
  public void testWithNounProperty() {
    // Verbalise the property owner, which is a noun
    pe = owner;
    text = converter.convert(pe);
    System.out.println(pe + " = " + text);
    Assert.assertEquals("owner", pe.toString());
    Assert.assertEquals("X is owner", text);
  }

  @Test
  public void testWithVerbProperty2() {
    // Verbalise the property plays, which is a verb
    pe = plays;
    text = converter.convert(pe);
    System.out.println(pe + " = " + text);
    Assert.assertEquals("play", pe.toString());
    Assert.assertEquals("X plays Y", text);
  }

  @Test
  public void testWithInverse() {
    // Since verbalisation of hasWorkPlace is 'X has work place Y'
    // and its inverse can be represented by X hasWorkPlace⁻ Y,
    // the verbalization of the inverse property will be 'Y has work place X'
    pe = hasWorkPlace.getInverseProperty();
    text = converter.convert(pe);
    System.out.println(pe + " = " + text);
    Assert.assertEquals("hasWorkPlace⁻", pe.toString());
    Assert.assertEquals("Y has work place X", text);
  }
}
