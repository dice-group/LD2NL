package org.aksw.owl2nl;

import java.util.Collections;
import java.util.Set;

import org.aksw.owl2nl.converter.OWLAxiomConverter;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

public class OWLAxiomTypeTest {

  OWLAxiomConverter c = new OWLAxiomConverter(new OWL2NLInput());

  @Test
  public void testOWLSubClassOfAxiom() throws OWLAxiomConversionException {
    final OWLDataFactoryImpl df = new OWLDataFactoryImpl();
    final PrefixManager pm = new DefaultPrefixManager(null, null, "http://dbpedia.org/ontology/");

    final OWLClass place = df.getOWLClass("Place", pm);
    final OWLClass thing = df.getOWLClass("Thing", pm);
    /**
     * <code>
    OWLAnonymousClassExpressionImpl, OWLCardinalityRestrictionImpl, OWLClassExpressionImpl,
    OWLClassImpl, OWLDataAllValuesFromImpl, OWLDataCardinalityRestrictionImpl,
    OWLDataExactCardinalityImpl, OWLDataHasValueImpl, OWLDataMaxCardinalityImpl,
    OWLDataMinCardinalityImpl, OWLDataSomeValuesFromImpl, OWLNaryBooleanClassExpressionImpl,
    OWLObjectAllValuesFromImpl, OWLObjectCardinalityRestrictionImpl, OWLObjectComplementOfImpl,
    OWLObjectExactCardinalityImpl, OWLObjectHasSelfImpl, OWLObjectHasValueImpl,
    OWLObjectIntersectionOfImpl, OWLObjectMaxCardinalityImpl, OWLObjectMinCardinalityImpl,
    OWLObjectOneOfImpl, OWLObjectSomeValuesFromImpl, OWLObjectUnionOfImpl,
    OWLQuantifiedDataRestrictionImpl, OWLQuantifiedObjectRestrictionImpl,
    OWLQuantifiedRestrictionImpl, OWLRestrictionImpl, OWLValueRestrictionImpl
     </code>
     */
    final OWLClassExpression a = thing;
    final OWLClassExpression b = place;

    final Set<OWLAnnotation> NO_ANNOTATIONS = Collections.emptySet();
    final OWLSubClassOfAxiom axiom = new OWLSubClassOfAxiomImpl(b, a, NO_ANNOTATIONS);
    final String text = c.convert(axiom);
    final String expected = "every place is a thing";
    Assert.assertEquals(expected, text);
  }
}
