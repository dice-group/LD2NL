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

import java.util.*;

import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
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
public class OWLAxiomConverter implements OWLAxiomVisitor {

	private static final Logger logger = LoggerFactory.getLogger(OWLAxiomConverter.class);

	private NLGFactory nlgFactory;
	private Realiser realiser;

	private OWLClassExpressionConverter ceConverter;
	private OWLPropertyExpressionConverter peConverter;

	private OWLDataFactory df = new OWLDataFactoryImpl();

	private String nl;
	private OptimizerDepParse optimiser;

	public OWLAxiomConverter(Lexicon lexicon) {
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);

		ceConverter = new OWLClassExpressionConverter(lexicon);
		peConverter = new OWLPropertyExpressionConverter(lexicon);
		optimiser = new OptimizerDepParse();
	}

	public OWLAxiomConverter() {
		this(Lexicon.getDefaultLexicon());
	}

	/**
	 * Converts the OWL axiom into natural language. Only logical axioms are
	 * supported, i.e. declaration axioms and annotation axioms are not
	 * converted and <code>null</code> will be returned instead.
	 *
	 * @param axiom the OWL axiom
	 * @return the natural language expression
	 */
	public String convert(OWLAxiom axiom) throws OWLAxiomConversionException {
		reset();

		if (axiom.isLogicalAxiom()) {
			logger.debug("Converting " + axiom.getAxiomType().getName() + " axiom: " + axiom);
			try {
				axiom.accept(this);
				if(nl != null) {
					//System.out.println("Before Optimization :" + nl);
					nl = optimiser.optimize(nl);
					//System.out.println("After Optimization :" + nl);
				}
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
		NLGElement subPropertyElement = peConverter.asNLGElement(subProperty);
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
		Set<OWLObjectPropertyExpression> ObjProperty = axiom.getProperties();
		List<OWLObjectPropertyExpression> ObjProp = new ArrayList<>(ObjProperty);
		for (int i = 0; i < ObjProp.size(); i++) {
			for (int j = i + 1; j < ObjProp.size(); j++) {
				NLGElement EquiObjPropertyElement = peConverter.asNLGElement(ObjProp.get(i), false);
				logger.debug("Equivalent Property 1: " + realiser.realise(EquiObjPropertyElement));

				NLGElement Equi2ObjPropertyElement = peConverter.asNLGElement(ObjProp.get(j), false);
				logger.debug("Equivalent Property 2: " + realiser.realise(Equi2ObjPropertyElement));
				SPhraseSpec clause = nlgFactory.createClause(EquiObjPropertyElement, "be equivalent to", Equi2ObjPropertyElement);
				nl = realiser.realise(clause).toString();
				logger.debug(axiom + " = " + nl);
			}
		}
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
		logger.debug("Converting InverseObjectProperties axiom {}", axiom);
		// get the first property
		OWLObjectPropertyExpression firstPropertyExpression = axiom.getFirstProperty();
		//get the second property
		OWLObjectPropertyExpression secondPropertyExpression = axiom.getSecondProperty();
		// get inverse of second property
		OWLObjectPropertyExpression inversePropertyExpression = secondPropertyExpression.getInverseProperty();

		// Express the inverse object properties axiom as the first property
		// being equivalent to the inverse of the second property.
		OWLEquivalentObjectPropertiesAxiom eqObjectPropertiesAxiom = df.getOWLEquivalentObjectPropertiesAxiom(
				firstPropertyExpression, inversePropertyExpression);
		eqObjectPropertiesAxiom.accept(this);
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
		logger.debug("Converting SymmetricObjectProperty axiom {}", axiom);
		// read the property expression
		OWLObjectPropertyExpression propertyExpression = axiom.getProperty();
		// get the inverse of the property expression
		OWLObjectPropertyExpression inversePropertyExpression = propertyExpression.getInverseProperty();

		// Express the symmetric object property axiom as the property
		// being a sub object property of it's inverse.
		OWLSubObjectPropertyOfAxiom subObjPropAxiom = df.getOWLSubObjectPropertyOfAxiom(
				propertyExpression, inversePropertyExpression);
		subObjPropAxiom.accept(this);
	}

	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
		OWLObjectPropertyExpression propertyExpression = axiom.getProperty();

		//transiviteProperty1=X&Y, transiviteProperty2=Y&Z, transiviteProperty3=X&Z
		NLGElement transiviteProperty1 = peConverter.asNLGElement(propertyExpression,true);
		NLGElement transiviteProperty2 = peConverter.asNLGElement(propertyExpression,true);
		NLGElement transiviteProperty3 = peConverter.asNLGElement(propertyExpression,true);

		CoordinatedPhraseElement Phrase1 = nlgFactory.createCoordinatedPhrase();
		CoordinatedPhraseElement Phrase2 = nlgFactory.createCoordinatedPhrase();

		Phrase1.addCoordinate(transiviteProperty1);
		Phrase1.addCoordinate(transiviteProperty2);

		Phrase2.addCoordinate(transiviteProperty3);
		Phrase2.setConjunction("if");
		Phrase2.addCoordinate(Phrase1);

		nl = realiser.realise(Phrase2).toString();
		logger.debug(axiom + " = " + nl);
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

	public String Test() throws Exception {
		System.out.println("test function called");
		return "Hurray !";
	}

	public Map<String, String> readOntology(String path) throws Exception {
		try {
			// read the ontology from the path
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = man.loadOntology(IRI.create(path));

			Map<String, String> axioms = new HashMap<>();
			// convert all the axioms
			for (OWLAxiom axiom : ontology.getAxioms()) {
				String text = convert(axiom);
				axioms.put(axiom.toString(), text);
			}
			// pass all the converted axioms
			return axioms;
		} catch (Exception ex) {
			System.out.println("error in readOntology:" + ex.getMessage());
			throw ex;
		}
	}
}
