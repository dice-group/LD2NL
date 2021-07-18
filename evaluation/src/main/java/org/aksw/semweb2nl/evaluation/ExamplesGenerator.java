/*-
 * #%L
 * Evaluation
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
/**
 * 
 */
package org.aksw.semweb2nl.evaluation;

import java.util.*;
import java.util.Map.Entry;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import org.semanticweb.owlapi.search.EntitySearcher;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;

/**
 * @author Lorenz Buehmann
 *
 */
public class ExamplesGenerator {
	
	private OWLOntology ontology;
	private OWLDataFactory df = new OWLDataFactoryImpl();
	private IndividualGenerator individualGenerator = new IndividualGenerator();
	private ABoxGenerator aboxGenerator = new ABoxGenerator(individualGenerator);

	public ExamplesGenerator(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
	/**
	 * Generate some synthetic instances of the given class and the corresponding
	 * Abox data.
	 * @param cls
	 */
	public Map<OWLIndividual, Set<OWLIndividualAxiom>> generatePositiveExamples(OWLClass cls, int nrOfExamples) {
		Map<OWLIndividual, Set<OWLIndividualAxiom>> result = new HashMap<OWLIndividual, Set<OWLIndividualAxiom>>();
		
		individualGenerator.reset();
		
		// get the class expressions that describe the given class
		Collection<OWLClassExpression> superClassExpressions = EntitySearcher.getSuperClasses(cls, ontology);
		superClassExpressions.addAll(EntitySearcher.getEquivalentClasses(cls, ontology));
//		superClassExpressions.addAll(cls.getSubClasses(ontology));

		// rewrite as intersection for simplicity
		Set<OWLClassExpression> conjuncts = new TreeSet<OWLClassExpression>();
		for (OWLClassExpression supExpr : superClassExpressions) { // avoid A and (A and B)
			if(supExpr instanceof OWLObjectIntersectionOf){
				conjuncts.addAll(((OWLObjectIntersectionOf) supExpr).getOperands());
			} else {
				conjuncts.add(supExpr);
			}
		}
		superClassExpressions = Collections.<OWLClassExpression>singleton(df.getOWLObjectIntersectionOf(conjuncts));
		
		// generate ABox data based on contained information in the class expressions
		for (OWLClassExpression supExpr : superClassExpressions) {
			System.out.println(supExpr);
			for (int i = 0; i < nrOfExamples; i++) {
				OWLIndividual ind = individualGenerator.generateIndividual(cls);
				Set<OWLIndividualAxiom> instanceData = aboxGenerator.generateInstanceData(ind, supExpr);
				result.put(ind, instanceData);
//				System.out.println(instanceData);
			}
		}
		
		return result;
	}
	
	public Map<OWLIndividual, Set<OWLIndividualAxiom>> generateNegativeExamples(OWLClass cls, int nrOfExamples) {
		// generate minimal ABox data to be a positive example
		Map<OWLIndividual, Set<OWLIndividualAxiom>> positiveExamples = generatePositiveExamples(cls, nrOfExamples);
		
		// modify the instance data for each individual such that it
		// does not satisfy the description of the class
		for (Entry<OWLIndividual, Set<OWLIndividualAxiom>> entry : positiveExamples.entrySet()) {
			OWLIndividual ind = entry.getKey();
			Set<OWLIndividualAxiom> instanceData = entry.getValue();
			
			// easiest way: remove at least 1 and at most n-1 axioms randomly with probability p
			double probability = 0.5;
			Random rnd = new Random(123);
			for (Iterator<OWLIndividualAxiom> iterator = instanceData.iterator(); iterator.hasNext();) {
				OWLIndividualAxiom axiom = iterator.next();
				if(rnd.nextDouble() < probability) {
					iterator.remove();
				}
			}
		}
		
		return positiveExamples;
	}
	
	public static void main(String[] args) throws Exception {
		ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
		String ontologyURL = "http://protege.cim3.net/file/pub/ontologies/koala/koala.owl";
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = man.loadOntology(IRI.create(ontologyURL));
		
		ExamplesGenerator examplesGenerator = new ExamplesGenerator(ontology);
		
		for (OWLClass cls : ontology.getClassesInSignature()) {
			System.out.println("########### " + cls + " #############");
			
			Map<OWLIndividual, Set<OWLIndividualAxiom>> positiveExamples = examplesGenerator.generatePositiveExamples(cls, 3);
			for (Entry<OWLIndividual, Set<OWLIndividualAxiom>> entry : positiveExamples.entrySet()) {
				OWLIndividual ind = entry.getKey();
				Set<OWLIndividualAxiom> instanceData = entry.getValue();
				System.out.println("+(" + ind + ")::" + instanceData);
			}
			
			Map<OWLIndividual, Set<OWLIndividualAxiom>> negativeExamples = examplesGenerator.generateNegativeExamples(cls, 3);
			for (Entry<OWLIndividual, Set<OWLIndividualAxiom>> entry : negativeExamples.entrySet()) {
				OWLIndividual ind = entry.getKey();
				Set<OWLIndividualAxiom> instanceData = entry.getValue();
				System.out.println("-(" + ind + "::" + instanceData);
			}
		}
		
	}
}
