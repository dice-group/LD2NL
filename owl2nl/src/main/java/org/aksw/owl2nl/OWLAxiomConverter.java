/*
 * #%L
 * OWL2NL
 * %%
 * Copyright (C) 2015 Agile Knowledge Engineering and Semantic Web (AKSW)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.aksw.owl2nl;

import java.util.List;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.features.Feature;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
//import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
/**
 * Converts OWL axioms into natural language.
 * @author Lorenz Buehmann
 *
 */
public class OWLAxiomConverter implements OWLAxiomVisitor{
	
	private static final Logger logger = LoggerFactory.getLogger(OWLAxiomConverter.class);
	
	private NLGFactory nlgFactory;
	private Realiser realiser;

	private OWLClassExpressionConverter ceConverter;
	private OWLPropertyExpressionConverter peConverter;
	
	private OWLDataFactory df = new OWLDataFactoryImpl();
	
	private String nl;
	
	public OWLAxiomConverter(Lexicon lexicon) {
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);
		
		ceConverter = new OWLClassExpressionConverter(lexicon);
		peConverter = new OWLPropertyExpressionConverter(lexicon);
	}
	
	public OWLAxiomConverter() {
		this(Lexicon.getDefaultLexicon());
	}
	
	/**
	 * Converts the OWL axiom into natural language. Only logical axioms are 
	 * supported, i.e. declaration axioms and annotation axioms are not 
	 * converted and <code>null</code> will be returned instead.
	 * @param axiom the OWL axiom
	 * @return the natural language expression
	 */
	public String convert(OWLAxiom axiom) throws OWLAxiomConversionException {
		reset();
		
		if (axiom.isLogicalAxiom()) {
			logger.debug("Converting " + axiom.getAxiomType().getName() + " axiom: " + axiom);
			try {
				axiom.accept(this);
				Optimizer opt = new Optimizer();
				System.out.println("Before Optimise :"+ nl);
				nl = opt.Optimise(nl);
				System.out.println("After Optimise :"+ nl);
				return nl;
			} catch (Exception e) {
				throw new OWLAxiomConversionException(axiom, e);
			}
		}
		logger.warn("Conversion of non-logical axioms not supported yet!");
		return null;
	}
	
	private void reset() {
		nl = null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubClassOfAxiom)
	 */
	@Override
	public void visit(OWLSubClassOfAxiom axiom) {
		logger.debug("Converting SubClassOf axiom {}", axiom);
		// convert the subclass
		OWLClassExpression subClass = axiom.getSubClass();
		NLGElement subClassElement = ceConverter.asNLGElement(subClass, true);
		logger.debug("SubClass: " + realiser.realise(subClassElement));
//		((PhraseElement)subClassElement).setPreModifier("every");
		
		// convert the superclass
		OWLClassExpression superClass = axiom.getSuperClass();
		NLGElement superClassElement = ceConverter.asNLGElement(superClass);
		logger.debug("SuperClass: " + realiser.realise(superClassElement));
		
		SPhraseSpec clause = nlgFactory.createClause(subClassElement, "be", superClassElement);
		superClassElement.setFeature(Feature.COMPLEMENTISER, null);

		nl = realiser.realise(clause).toString();
		logger.debug(axiom + " = " + nl);
	}
	
	@Override
	public void visit(OWLEquivalentClassesAxiom axiom) {
		List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();
		
		for (int i = 0; i < classExpressions.size(); i++) {
			for (int j = i + 1; j < classExpressions.size(); j++) {
				OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(
						classExpressions.get(i), 
						classExpressions.get(j));
				subClassAxiom.accept(this);
			}
		}
	}

	/*
	 * We rewrite DisjointClasses(C_1,...,C_n) as SubClassOf(C_i, ObjectComplementOf(C_j)) for each subset {C_i,C_j} with i != j 
	 */
	@Override
	public void visit(OWLDisjointClassesAxiom axiom) {
		List<OWLClassExpression> classExpressions = axiom.getClassExpressionsAsList();
		
		for (int i = 0; i < classExpressions.size(); i++) {
			for (int j = i + 1; j < classExpressions.size(); j++) {
				OWLSubClassOfAxiom subClassAxiom = df.getOWLSubClassOfAxiom(
						classExpressions.get(i), 
						df.getOWLObjectComplementOf(classExpressions.get(j)));
				subClassAxiom.accept(this);
			}
		}
	}
	
	@Override
	public void visit(OWLDisjointUnionAxiom axiom) {
	}

	
	//#########################################################
	//################# object property axioms ################
	//#########################################################

	@Override
	public void visit(OWLSubObjectPropertyOfAxiom axiom) {
		logger.debug("Converting SubObjectPropertyOf axiom {}", axiom);
		// convert the sub property
		OWLObjectPropertyExpression subProperty = axiom.getSubProperty();
		NLGElement subPropertyElement = peConverter.asNLGElement(subProperty, true);
		logger.debug("subProperty: " + realiser.realise(subPropertyElement));

		// convert the super property
		OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
		NLGElement superPropertyElement = peConverter.asNLGElement(superProperty);
		logger.debug("SuperObjectProperty: " + realiser.realise(superPropertyElement));

		SPhraseSpec clause = nlgFactory.createClause(subPropertyElement, "imply", superPropertyElement);
		superPropertyElement.setFeature(Feature.COMPLEMENTISER, null);

		nl = realiser.realise(clause).toString();
		logger.debug(axiom + " = " + nl);
	}
	
	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
	}
	
	@Override
	public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
	}

	@Override
	public void visit(OWLObjectPropertyDomainAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLObjectPropertyRangeAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLInverseObjectPropertiesAxiom axiom) {
	}

	@Override
	public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
	}

	@Override
	public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
	}
	
	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
	}

	@Override
	public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	//#########################################################
	//################# data property axioms ##################
	//#########################################################
	
	@Override
	public void visit(OWLSubDataPropertyOfAxiom axiom) {
	}
	
	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
	}
	
	@Override
	public void visit(OWLDisjointDataPropertiesAxiom axiom) {
	}
	
	@Override
	public void visit(OWLDataPropertyDomainAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLDataPropertyRangeAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	@Override
	public void visit(OWLFunctionalDataPropertyAxiom axiom) {
		axiom.asOWLSubClassOfAxiom().accept(this);
	}
	
	//#########################################################
	//################# individual axioms #####################
	//#########################################################
	
	@Override
	public void visit(OWLClassAssertionAxiom axiom) {
	}
	
	@Override
	public void visit(OWLObjectPropertyAssertionAxiom axiom) {
	}
	
	@Override
	public void visit(OWLDataPropertyAssertionAxiom axiom) {
	}

	@Override
	public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
	}

	@Override
	public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
	}

	@Override
	public void visit(OWLDifferentIndividualsAxiom axiom) {
	}

	@Override
	public void visit(OWLSameIndividualAxiom axiom) {
	}

	//#########################################################
	//################# other logical axioms ##################
	//#########################################################

	@Override
	public void visit(OWLSubPropertyChainOfAxiom axiom) {
	}
	
	@Override
	public void visit(OWLHasKeyAxiom axiom) {
	}

	@Override
	public void visit(OWLDatatypeDefinitionAxiom axiom) {
	}

	@Override
	public void visit(SWRLRule axiom) {
	}
	
	//#########################################################
	//################# non-logical axioms ####################
	//#########################################################
	
	@Override
	public void visit(OWLAnnotationAssertionAxiom axiom) {
	}

	@Override
	public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
	}

	@Override
	public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
	}

	@Override
	public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
	}

	@Override
	public void visit(OWLDeclarationAxiom axiom) {
	}
	
	public static void main(String[] args) throws Exception {
		ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
		String ontologyURL = "http://www.cs.man.ac.uk/~stevensr/ontology/family.rdf.owl";// subproperties of the form 'isSomething'
		//ontologyURL = "https://protege.stanford.edu/ontologies/pizza/pizza.owl"; // subproperties of the form 'hasSomething'

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = man.loadOntology(IRI.create(ontologyURL));
		
		OWLAxiomConverter converter = new OWLAxiomConverter();
		for (OWLAxiom axiom : ontology.getAxioms()) {
			converter.convert(axiom);
		}
	}
}
