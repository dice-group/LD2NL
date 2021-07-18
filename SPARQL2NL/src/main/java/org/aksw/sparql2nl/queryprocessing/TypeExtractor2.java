/*-
 * #%L
 * SPARQL2NL
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
package org.aksw.sparql2nl.queryprocessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dllearner.kb.sparql.SparqlEndpoint;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.vocabulary.RDF;

public class TypeExtractor2 {
	
//	private static final Node TYPE_NODE = Node.createURI(RDF.type.getURI());
	private static final Node TYPE_NODE = NodeFactory.createURI(RDF.type.getURI());


	private Map<String, Set<String>> var2AssertedTypesMap;
	private Map<String, Set<String>> var2InferredTypesMap;
	
	private DomainExtractor domainExtractor;
	private RangeExtractor rangeExtractor;
	
	private boolean inferTypes = false;
	private boolean preferAssertedTypes = true;
	
	private SparqlEndpoint endpoint;
	
	public TypeExtractor2(SparqlEndpoint endpoint) {
		this.endpoint = endpoint;
	}

       
	public Map<String, Set<String>> extractTypes(Query query) {
		var2AssertedTypesMap = new HashMap<>();
		var2InferredTypesMap = new HashMap<>();
		
		TriplePatternExtractor extr = new TriplePatternExtractor();
		Set<Triple> triples = extr.extractTriplePattern((ElementGroup)query.getQueryPattern());
		
		for(Triple t : triples){
			processTriple(t);
		}
		
		Map<String, Set<String>> var2TypesMap = new HashMap<>();
		if(preferAssertedTypes){
			var2TypesMap.putAll(var2AssertedTypesMap);
			for(Entry<String, Set<String>> entry : var2InferredTypesMap.entrySet()){
				if(!var2AssertedTypesMap.containsKey(entry.getKey())){
					var2TypesMap.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			var2TypesMap.putAll(var2AssertedTypesMap);
			for(Entry<String, Set<String>> entry : var2InferredTypesMap.entrySet()){
				String key = entry.getKey();
				Set<String> additionalTypes = entry.getValue();
				if(!var2TypesMap.containsKey(key)){
					var2TypesMap.put(key, additionalTypes);
				} else {
					Set<String> existingTypes = var2TypesMap.get(entry.getKey());
					existingTypes.addAll(additionalTypes);
				}
			}
		}
		
		return var2TypesMap;
	}
	
	public void setDomainExtractor(DomainExtractor domainExtractor) {
		this.domainExtractor = domainExtractor;
	}
	
	public void setRangeExtractor(RangeExtractor rangeExtractor) {
		this.rangeExtractor = rangeExtractor;
	}
	
	public void setInferTypes(boolean inferTypes) {
		this.inferTypes = inferTypes;
	}
	
	private void processTriple(Triple triple){
		Node subject = triple.getSubject();
		Node object = triple.getObject();
		
		if (triple.predicateMatches(TYPE_NODE)) {//process rdf:type triples	
			if(subject.isVariable()){
				if(object.isURI()){
					addAssertedType(subject.getName(), object.getURI());
				} 
			}
		} else if(inferTypes && triple.getPredicate().isURI()){//process triples where predicate is not rdf:type, i.e. use rdfs:domain and rdfs:range for inferencing the type
			Node predicate = triple.getPredicate();
			if(subject.isVariable()){
				if(domainExtractor != null){
					String domain = domainExtractor.getDomain(predicate.getURI());
					if(domain != null){
						addInferredType(subject.getName(), domain);
					}
				}
			} 
			if(object.isVariable()){
				if(rangeExtractor != null){
					String range = rangeExtractor.getRange(predicate.getURI());
					if(range != null){
						addInferredType(object.getName(), range);
					}
				}
			}
		} 
	}
	
	private void addAssertedType(String variable, String type){
		Set<String> types = var2AssertedTypesMap.get(variable);
		if(types == null){
			types = new HashSet<>();
			var2AssertedTypesMap.put(variable, types);
		}
		types.add(type);
	}
	
	private void addInferredType(String variable, String type){
		Set<String> types = var2InferredTypesMap.get(variable);
		if(types == null){
			types = new HashSet<>();
			var2InferredTypesMap.put(variable, types);
		}
		types.add(type);
	}
	
		
}
