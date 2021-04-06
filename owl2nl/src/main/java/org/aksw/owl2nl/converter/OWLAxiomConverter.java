package org.aksw.owl2nl.converter;

import java.util.List;

import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.*;

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

  private static final Logger LOG = LogManager.getLogger(OWLAxiomConverter.class);

  private final NLGFactory nlgFactory;
  private final Realiser realiser;

  private final OWLClassExpressionConverter ceConverter;
  private OWLPropertyExpressionConverter peConverter;

  private final OWLDataFactory df = new OWLDataFactoryImpl();

  private String nl;

  /**
   * OWLAxiomConverter class constructor.
   *
   * @param input
   */
  public OWLAxiomConverter(final IInput input) {
    nlgFactory = new NLGFactory(input.getLexicon());
    realiser = new Realiser(input.getLexicon());

    ceConverter = new OWLClassExpressionConverter(input);
  }

  /**
   * OWLAxiomConverter class constructor.
   *
   * @param lexicon
   */
  public OWLAxiomConverter(final Lexicon lexicon) {
    this(new OWL2NLInput().setLexicon(lexicon));
  }

  /**
   * OWLAxiomConverter class constructor.
   *
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
        Optimizer opt = new Optimizer();
        nl = opt.Optimise(nl);
        return nl;
      } catch (final Exception e) {
        LOG.debug("axion: " + axiom);
        LOG.debug("this: " + this);
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

    LOG.debug("Converting SubClassOf axiom {}, {}", axiom, axiom.getSubClass());

    final OWLClassExpression subClass = axiom.getSubClass();
    final NLGElement subClassElement = ceConverter.asNLGElement(subClass, true);

    LOG.debug("SubClass: " + realiser.realise(subClassElement));
    // ((PhraseElement)subClassElement).setPreModifier("every");

    LOG.debug("Converting SuperClass axiom {}, {}", axiom, axiom.getSuperClass());

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
        final OWLSubClassOfAxiom subClassAxiom;
        subClassAxiom = df.getOWLSubClassOfAxiom(classExpressions.get(i), classExpressions.get(j));
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

  @Override // PG-student's code
  public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
    LOG.debug("Converting SubObjectPropertyOf axiom {}", axiom);
    // convert the sub property
    OWLObjectPropertyExpression subProperty = axiom.getSubProperty();
    NLGElement subPropertyElement = peConverter.asNLGElement(subProperty, true);
    LOG.debug("subProperty: " + realiser.realise(subPropertyElement));

    // convert the super property
    OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
    NLGElement superPropertyElement = peConverter.asNLGElement(superProperty);
    LOG.debug("SuperObjectProperty: " + realiser.realise(superPropertyElement));

    SPhraseSpec clause = nlgFactory.createClause(subPropertyElement, "imply", superPropertyElement);
    superPropertyElement.setFeature(Feature.COMPLEMENTISER, null);

    nl = realiser.realise(clause).toString();
    LOG.debug(axiom + " = " + nl);
  }

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
  public void visit(final OWLAnnotationAssertionAxiom axiom) {
    LOG.debug("OWLAnnotationAssertionAxiom: {}", axiom.toString());
  }

  @Override
  public void visit(final OWLSubAnnotationPropertyOfAxiom axiom) {}

  @Override
  public void visit(final OWLAnnotationPropertyDomainAxiom axiom) {}

  @Override
  public void visit(final OWLAnnotationPropertyRangeAxiom axiom) {}

  @Override
  public void visit(final OWLDeclarationAxiom axiom) {
    LOG.debug("OWLDeclarationAxiom: {}", axiom.toString());
  }
}
