package org.aksw.owl2nl;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.semanticweb.owlapi.apibinding.OWLManager;
// import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.features.Feature;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * Converts OWL logical axioms into natural language.
 *
 * @author Lorenz Buehmann
 * @author Rene Speck
 */
public class OWLAxiomConverter implements OWLAxiomVisitor {

  private static final Logger LOG = LoggerFactory.getLogger(OWLAxiomConverter.class);

  private final NLGFactory nlgFactory;
  private final Realiser realiser;

  private final OWLClassExpressionConverter ceConverter;

  private final OWLDataFactory df = new OWLDataFactoryImpl();

  private String nl;

  /**
   * OWLAxiomConverter class constructor.
   *
   * @param lexicon
   */
  public OWLAxiomConverter(final Lexicon lexicon) {
    nlgFactory = new NLGFactory(lexicon);
    realiser = new Realiser(lexicon);

    ceConverter = new OWLClassExpressionConverter(lexicon);
  }

  /**
   * OWLAxiomConverter class constructor.
   */
  public OWLAxiomConverter() {
    this(Lexicon.getDefaultLexicon());
  }

  /**
   * Converts the OWL axiom into natural language. Only logical axioms are supported, i.e.
   * declaration axioms and annotation axioms are not converted and <code>null</code> will be
   * returned instead.
   *
   * @param axiom the OWL axiom
   * @return the natural language expression
   */
  public String convert(final OWLAxiom axiom) throws OWLAxiomConversionException {
    reset();

    if (axiom.isLogicalAxiom()) {
      try {
        axiom.accept(this);
        return nl;
      } catch (final Exception e) {
        throw new OWLAxiomConversionException(axiom, e);
      }
    }
    return null;
  }

  private void reset() {
    nl = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.semanticweb.owlapi.model.OWLAxiomVisitor#visit(org.semanticweb.owlapi.model.
   * OWLSubClassOfAxiom)
   */
  @Override
  public void visit(final OWLSubClassOfAxiom axiom) {

    LOG.debug("visit: Converting SubClassOf axiom {}", axiom);
    // convert the subclass
    final OWLClassExpression subClass = axiom.getSubClass();
    final NLGElement subClassElement = ceConverter.asNLGElement(subClass, true);
    LOG.debug("SubClass: " + realiser.realise(subClassElement));
    // ((PhraseElement)subClassElement).setPreModifier("every");

    // convert the superclass
    final OWLClassExpression superClass = axiom.getSuperClass();
    final NLGElement superClassElement = ceConverter.asNLGElement(superClass);
    LOG.debug("SuperClass: " + realiser.realise(superClassElement));

    final SPhraseSpec clause = nlgFactory.createClause(subClassElement, "be", superClassElement);
    superClassElement.setFeature(Feature.COMPLEMENTISER, null);

    nl = realiser.realise(clause).toString();
    LOG.debug(axiom + " = " + nl);
  }

  @Override
  public void visit(final OWLEquivalentClassesAxiom axiom) {
    final List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();

    for (int i = 0; i < classExpressions.size(); i++) {
      for (int j = i + 1; j < classExpressions.size(); j++) {
        final OWLSubClassOfAxiom subClassAxiom =
            df.getOWLSubClassOfAxiom(classExpressions.get(i), classExpressions.get(j));
        subClassAxiom.accept(this);
      }
    }
  }

  /*
   * We rewrite DisjointClasses(C_1,...,C_n) as SubClassOf(C_i, ObjectComplementOf(C_j)) for each
   * subset {C_i,C_j} with i != j
   */
  @Override
  public void visit(final OWLDisjointClassesAxiom axiom) {
    final List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();

    for (int i = 0; i < classExpressions.size(); i++) {
      for (int j = i + 1; j < classExpressions.size(); j++) {
        final OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(classExpressions.get(i),
            df.getOWLObjectComplementOf(classExpressions.get(j)));
        subClassAxiom.accept(this);
      }
    }
  }

  @Override
  public void visit(final OWLDisjointUnionAxiom axiom) {}

  // #########################################################
  // ################# object property axioms ################
  // #########################################################

  @Override
  public void visit(final OWLSubObjectPropertyOfAxiom axiom) {}

  @Override
  public void visit(final OWLEquivalentObjectPropertiesAxiom axiom) {}

  @Override
  public void visit(final OWLDisjointObjectPropertiesAxiom axiom) {}

  @Override
  public void visit(final OWLObjectPropertyDomainAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLObjectPropertyRangeAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLInverseObjectPropertiesAxiom axiom) {}

  @Override
  public void visit(final OWLFunctionalObjectPropertyAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {}

  @Override
  public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLSymmetricObjectPropertyAxiom axiom) {}

  @Override
  public void visit(final OWLTransitiveObjectPropertyAxiom axiom) {}

  @Override
  public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  // #########################################################
  // ################# data property axioms ##################
  // #########################################################

  @Override
  public void visit(final OWLSubDataPropertyOfAxiom axiom) {}

  @Override
  public void visit(final OWLEquivalentDataPropertiesAxiom axiom) {}

  @Override
  public void visit(final OWLDisjointDataPropertiesAxiom axiom) {}

  @Override
  public void visit(final OWLDataPropertyDomainAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLDataPropertyRangeAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  @Override
  public void visit(final OWLFunctionalDataPropertyAxiom axiom) {
    axiom.asOWLSubClassOfAxiom().accept(this);
  }

  // #########################################################
  // ################# individual axioms #####################
  // #########################################################

  @Override
  public void visit(final OWLClassAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLObjectPropertyAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLDataPropertyAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLDifferentIndividualsAxiom axiom) {}

  @Override
  public void visit(final OWLSameIndividualAxiom axiom) {}

  // #########################################################
  // ################# other logical axioms ##################
  // #########################################################

  @Override
  public void visit(final OWLSubPropertyChainOfAxiom axiom) {}

  @Override
  public void visit(final OWLHasKeyAxiom axiom) {}

  @Override
  public void visit(final OWLDatatypeDefinitionAxiom axiom) {}

  @Override
  public void visit(final SWRLRule axiom) {}

  // #########################################################
  // ################# non-logical axioms ####################
  // #########################################################

  @Override
  public void visit(final OWLAnnotationAssertionAxiom axiom) {}

  @Override
  public void visit(final OWLSubAnnotationPropertyOfAxiom axiom) {}

  @Override
  public void visit(final OWLAnnotationPropertyDomainAxiom axiom) {}

  @Override
  public void visit(final OWLAnnotationPropertyRangeAxiom axiom) {}

  @Override
  public void visit(final OWLDeclarationAxiom axiom) {}

  public static void main(final String[] args) throws Exception {
    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
    // "http://130.88.198.11/2008/iswc-modtut/materials/koala.owl";
    // "http://rpc295.cs.man.ac.uk:8080/repository/download?ontology=http://reliant.teknowledge.com/DAML/Transportation.owl&format=RDF/XML";
    // "http://protege.cim3.net/file/pub/ontologies/travel/travel.owl";
    // "https://raw.githubusercontent.com/pezra/pretty-printer/master/Jenna-2.6.3/testing/ontology/bugs/koala.owl";

    final String filename = "Process.owl";
    final String base = "/home/rspeck/Desktop/Usecases" + "/";

    final File ontologyURL = Paths.get(base, filename).toFile();

    final OWLAxiomConverter converter = new OWLAxiomConverter();
    final Set<OWLAxiom> axioms =
        OWLManager.createOWLOntologyManager().loadOntology(IRI.create(ontologyURL)).getAxioms();

    for (final OWLAxiom axiom : axioms) {
      final String s = converter.convert(axiom);
      if (s != null) {
        LOG.info(axiom.toString().concat(" -> ").concat(s));
      }
    }
  }
}
