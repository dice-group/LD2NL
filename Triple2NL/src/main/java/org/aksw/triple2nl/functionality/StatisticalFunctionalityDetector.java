/*-
 * #%L
 * Triple2NL
 * %%
 * Copyright (C) 2015 - 2021 Data and Web Science Research Group (DICE)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.aksw.triple2nl.functionality;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

public class StatisticalFunctionalityDetector implements FunctionalityDetector{
	
	private OWLOntology ontology;
	private double threshold;
	private OWLDataFactory dataFactory;
	
	private final IRI confidencePropertyIRI = IRI.create("http://www.dl-learner.org/ontologies/enrichment.owl#confidence");
	
	public StatisticalFunctionalityDetector(File ontologyFile, double threshold) {
		try {
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
			ontology = man.loadOntologyFromOntologyDocument(ontologyFile);
			dataFactory = man.getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		this.threshold = threshold;
	}
	
	public StatisticalFunctionalityDetector(InputStream is, double threshold) {
		try {
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
			ontology = man.loadOntologyFromOntologyDocument(is);
			dataFactory = man.getOWLDataFactory();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		this.threshold = threshold;
	}

	@Override
	public boolean isFunctional(String iri) {
		// check as data property
		Set<? extends OWLAxiom> axioms = ontology.getFunctionalDataPropertyAxioms(dataFactory.getOWLDataProperty(IRI.create(iri)));
		if(!axioms.isEmpty()){
			Double confidence = getConfidenceValue(axioms.iterator().next());

			if(confidence != null && confidence >= threshold){
				return true;
			} 
		}

		//check as object property
		axioms = ontology.getFunctionalObjectPropertyAxioms(dataFactory.getOWLObjectProperty(IRI.create(iri)));
		if(!axioms.isEmpty()){
			Double confidence = getConfidenceValue(axioms.iterator().next());

			if(confidence != null && confidence >= threshold){
				return true;
			}
		}
		return false;
	}

	private Double getConfidenceValue(OWLAxiom axiom) {
		Set<OWLAnnotation> annotations = axiom.getAnnotations(dataFactory.getOWLAnnotationProperty(confidencePropertyIRI));
		if(!annotations.isEmpty()) {
			OWLLiteral val = (OWLLiteral) annotations.iterator().next().getValue();
			return val.parseDouble();
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception{
		
//		String ontologyURL = "resources/dbpedia_3.8_enrichment.owl";
//		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//		OWLDataFactory dataFactory = man.getOWLDataFactory();
//		OWLOntology ontology = man.loadOntologyFromOntologyDocument(new File(ontologyURL));
//		Set<OWLAxiom> functionalAxioms = new HashSet<OWLAxiom>();
//		for(OWLAxiom axiom : ontology.getAxioms(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)){
//			functionalAxioms.add(axiom);
//		}
//		for(OWLAxiom axiom : ontology.getAxioms(AxiomType.FUNCTIONAL_DATA_PROPERTY)){
//			functionalAxioms.add(axiom);
//		}
//		OWLOntology functional = man.createOntology(IRI.create("http://sparql2nl.aksw.org/dbpedia"));
//		man.addAxioms(functional, functionalAxioms);
//		man.saveOntology(functional, new RDFXMLOntologyFormat(), new FileOutputStream(new File("resources/dbpedia_functional_axioms.owl")));
		StatisticalFunctionalityDetector detector = new StatisticalFunctionalityDetector(new File("resources/dbpedia_functional_axioms.owl"), 0.9);
		System.out.println(detector.isFunctional("http://dbpedia.org/ontology/occupation"));
		System.out.println(detector.isFunctional("http://dbpedia.org/ontology/birthDate"));
		System.out.println(detector.isFunctional("http://dbpedia.org/ontology/populationTotal"));
	}
	
	


}
