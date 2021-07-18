/*-
 * #%L
 * AVATAR
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
package org.aksw.avatar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.aksw.avatar.clustering.hardening.HardeningFactory.HardeningType;
import org.aksw.avatar.dataset.DatasetBasedGraphGenerator.Cooccurrence;
import org.aksw.avatar.rouge.Rouge;
import org.aksw.jena_sparql_api.cache.h2.CacheUtilsH2;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.reasoning.SPARQLReasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;

import simplenlg.framework.NLGElement;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 * @author Lorenz Buehmann
 *
 */
public class Evaluation {
	
	private static Logger logger = Logger.getLogger(Evaluation.class);

	
	SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();;
	String cacheDirectory = "verbalization-evaluation";
	int nrOfClasses = 10;
	int nrOfInstancePerClass = 10;
	Cooccurrence cooccurrence = Cooccurrence.PROPERTIES;
	HardeningType hardeningType = HardeningType.AVERAGE;
	double threshold = 0.5;
	String abstractProperty = "http://dbpedia.org/ontology/abstract";
	String namespace = "http://dbpedia.org/ontology/";
	
	SPARQLReasoner reasoner = new SPARQLReasoner(new SparqlEndpointKS(endpoint, cacheDirectory));
	QueryExecutionFactory qef;
	Verbalizer verbalizer = new Verbalizer(endpoint, cacheDirectory, null);
	
	Rouge rouge = new Rouge();
	int rougeMode = Rouge.MULTIPLE_MAX;
	
	
	public Evaluation() {
		qef = new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs());
		long timeToLive = TimeUnit.DAYS.toMillis(30);
		qef = CacheUtilsH2.createQueryExecutionFactory(qef, cacheDirectory, false, timeToLive);
		
		rouge.setMultipleReferenceMode(rougeMode);
	}
	
	public void run(){
		//get the classes
		Collection<OWLClass> classes = getClasses();
		//for each class
		for (OWLClass cls : classes) {
			System.out.println("Processing class " + cls);
			
			SummaryStatistics pStats = new SummaryStatistics();
			SummaryStatistics rStats = new SummaryStatistics();
			SummaryStatistics fStats = new SummaryStatistics();
			
			//get some instances with abstracts
			Map<OWLIndividual, String> individualsWithAbstract = getIndividualsWithAbstract(cls);
			
			//get the verbalizations
			Map<OWLIndividual, List<NLGElement>> verbalizations = verbalizer.verbalize(individualsWithAbstract.keySet(), cls, namespace, threshold, cooccurrence, hardeningType);
		
			//compare the verbalization with the abstract by using ROUGE
			for (Entry<OWLIndividual, String> entry : individualsWithAbstract.entrySet()) {
				OWLIndividual ind = entry.getKey();
				String abstr = entry.getValue();
				List<NLGElement> verbalization = verbalizations.get(ind);
				String realization = verbalizer.realize(verbalization);
				
				//compute ROUGE
				rouge.evaluate(realization, new String[]{abstr});
				
				double precision = rouge.getPrecision();
				double recall = rouge.getRecall();
				double fScore = rouge.getFScore();
				
				System.out.println("######################################");
				System.out.println("Individual:" + ind);
				System.out.println("Realization:\n" + realization);
				System.out.println("Abstract:\n" + abstr);
				System.out.printf("P=%f|R=%f|F_1=%f\n", precision, recall, fScore);
				
				pStats.addValue(precision);
				rStats.addValue(recall);
				fStats.addValue(fScore);
			}
			
			logger.info("\n*************\nClass: " + cls + "\nAvg. precision: " + pStats.getMean() + "\nAvg. recall: " + rStats.getMean() + "\nAvg. FScore: " + fStats.getMean());
			
		}
		
	}
	
	/**
	 * Get x randomly chosen classes which contain at least y instances
	 * @return
	 */
	private Collection<OWLClass> getClasses(){
		Collection<OWLClass> classes = new TreeSet<OWLClass>();
		
		List<OWLClass> allClasses = new ArrayList<OWLClass>(reasoner.getOWLClasses());
		Collections.shuffle(allClasses, new Random(123));
		
		Iterator<OWLClass> iter = allClasses.iterator();
		OWLClass cls;
		while(iter.hasNext() && classes.size() < nrOfClasses){
			cls = iter.next();
			int cnt = reasoner.getIndividualsCount(cls);
			if(cnt >= nrOfInstancePerClass){
				classes.add(cls);
			}
		}
		return classes;
	}
	
	private Map<OWLIndividual, String> getIndividualsWithAbstract(OWLClass cls){
		Map<OWLIndividual, String> individualsWithAbstract = new HashMap<OWLIndividual, String>();
		ParameterizedSparqlString template = new ParameterizedSparqlString(
				"SELECT ?s ?abstract WHERE {?s a ?type. ?s ?abstractProperty ?abstract. FILTER(LANGMATCHES(LANG(?abstract),'en'))}");
		template.setIri("abstractProperty", abstractProperty);
		template.setIri("type", cls.toStringID());
		
		Query q = template.asQuery();
		q.setLimit(nrOfInstancePerClass);
		
		QueryExecution qe = qef.createQueryExecution(q);
		ResultSet rs = qe.execSelect();
		QuerySolution qs;
		while(rs.hasNext()){
			qs = rs.next();
			individualsWithAbstract.put(new OWLNamedIndividualImpl(IRI.create(qs.getResource("s").getURI())), qs.getLiteral("abstract").getLexicalForm());
		}
		return individualsWithAbstract;
	}
	
	public static void main(String[] args) throws Exception {
		new Evaluation().run();
	}
}
