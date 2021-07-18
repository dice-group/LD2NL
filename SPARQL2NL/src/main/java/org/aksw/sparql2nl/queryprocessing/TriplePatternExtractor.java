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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.ListUtils;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;

import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.util.VarUtils;

public class TriplePatternExtractor extends ElementVisitorBase {
	
	private Set<Triple> triplePattern;
	private Set<Triple> optionalTriplePattern;
	
	private boolean inOptionalClause = false;
	
	private int unionCount = 0;
	private int optionalCount = 0;
	private int filterCount = 0;
	
	/**
	 * Returns all triple patterns in given SPARQL query that have the given node in subject position, i.e. the outgoing
	 * triple patterns.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Set<Triple> extractOutgoingTriplePatterns(Query query, Node node){
		Set<Triple> triplePatterns = extractTriplePattern(query, false);
		//remove triple patterns not containing triple patterns with given node in subject position
		for (Iterator<Triple> iterator = triplePatterns.iterator(); iterator.hasNext();) {
			Triple triple = iterator.next();
			if(!triple.subjectMatches(node)){
				iterator.remove();
			}
		}
		return triplePatterns;
	}
	
	/**
	 * Returns all triple patterns in given SPARQL query that have the given node in object position, i.e. the incoming
	 * triple patterns.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Set<Triple> extractIncomingTriplePatterns(Query query, Node node){
		Set<Triple> triplePatterns = extractTriplePattern(query, false);
		//remove triple patterns not containing triple patterns with given node in subject position
		for (Iterator<Triple> iterator = triplePatterns.iterator(); iterator.hasNext();) {
			Triple triple = iterator.next();
			if(!triple.objectMatches(node)){
				iterator.remove();
			}
		}
		return triplePatterns;
	}
	
	/**
	 * Returns all triple patterns in given SPARQL query that have the given node in object position, i.e. the ingoing
	 * triple patterns.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Set<Triple> extractIngoingTriplePatterns(Query query, Node node){
		Set<Triple> triplePatterns = extractTriplePattern(query, false);
		//remove triple patterns not containing triple patterns with given node in object position
		for (Iterator<Triple> iterator = triplePatterns.iterator(); iterator.hasNext();) {
			Triple triple = iterator.next();
			if(!triple.objectMatches(node)){
				iterator.remove();
			}
		}
		return triplePatterns;
	}
	
	/**
	 * Returns all triple patterns in given SPARQL query that have the given node either in subject or in object position, i.e. 
	 * the ingoing and outgoing triple patterns.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Set<Triple> extractTriplePatterns(Query query, Node node){
		Set<Triple> triplePatterns = new HashSet<>();
		triplePatterns.addAll(extractIngoingTriplePatterns(query, node));
		triplePatterns.addAll(extractOutgoingTriplePatterns(query, node));
		return triplePatterns;
	}
	
	/**
	 * Returns all triple patterns in given SPARQL query that have the given node either in subject or in object position, i.e. 
	 * the ingoing and outgoing triple patterns.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Set<Triple> extractNonOptionalTriplePatterns(Query query, Node node){
		Set<Triple> triplePatterns = new HashSet<>();
		triplePatterns.addAll(extractIngoingTriplePatterns(query, node));
		triplePatterns.addAll(extractOutgoingTriplePatterns(query, node));
		triplePatterns.removeAll(optionalTriplePattern);
		return triplePatterns;
	}
	
	/**
	 * Returns triple patterns for each projection variable v such that v is either in subject or object position.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Map<Var,Set<Triple>> extractTriplePatternsForProjectionVars(Query query){
		Map<Var,Set<Triple>> var2TriplePatterns = new HashMap<>();
		for (Var var : query.getProjectVars()) {
			Set<Triple> triplePatterns = new HashSet<>();
			triplePatterns.addAll(extractIngoingTriplePatterns(query, var));
			triplePatterns.addAll(extractOutgoingTriplePatterns(query, var));
			var2TriplePatterns.put(var, triplePatterns);
		}
		return var2TriplePatterns;
	}
	
	/**
	 * Returns triple patterns for each projection variable v such that v is in subject position.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Map<Var,Set<Triple>> extractOutgoingTriplePatternsForProjectionVars(Query query){
		Map<Var,Set<Triple>> var2TriplePatterns = new HashMap<>();
		for (Var var : query.getProjectVars()) {
			Set<Triple> triplePatterns = new HashSet<>();
			triplePatterns.addAll(extractOutgoingTriplePatterns(query, var));
			var2TriplePatterns.put(var, triplePatterns);
		}
		return var2TriplePatterns;
	}
	
	/**
	 * @return the optionalTriplePattern
	 */
	public Set<Triple> getOptionalTriplePatterns() {
		return optionalTriplePattern;
	}
	
	/**
	 * Returns triple patterns for each projection variable v such that v is in subject position.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Map<Var,Set<Triple>> extractIncomingTriplePatternsForProjectionVars(Query query){
		Map<Var,Set<Triple>> var2TriplePatterns = new HashMap<>();
		for (Var var : query.getProjectVars()) {
			Set<Triple> triplePatterns = new HashSet<>();
			triplePatterns.addAll(extractIncomingTriplePatterns(query, var));
			var2TriplePatterns.put(var, triplePatterns);
		}
		return var2TriplePatterns;
	}
	
	/**
	 * Returns triple patterns for each projection variable v such that v is in object position.
	 * @param query The SPARQL query.
	 * @param node
	 * @return
	 */
	public Map<Var,Set<Triple>> extractIngoingTriplePatternsForProjectionVars(Query query){
		Map<Var,Set<Triple>> var2TriplePatterns = new HashMap<>();
		for (Var var : query.getProjectVars()) {
			Set<Triple> triplePatterns = new HashSet<>();
			triplePatterns.addAll(extractIngoingTriplePatterns(query, var));
			var2TriplePatterns.put(var, triplePatterns);
		}
		return var2TriplePatterns;
	}
	
	public Set<Triple> extractTriplePattern(Query query){
		return extractTriplePattern(query, false);
	}
	
	public Set<Triple> extractTriplePattern(Query query, boolean ignoreOptionals){
		triplePattern = new HashSet<>();
		optionalTriplePattern = new HashSet<>();
		
		query.getQueryPattern().visit(this);
		
		//postprocessing: triplepattern in OPTIONAL clause
		if(!ignoreOptionals){
			if(query.isSelectType()){
				for(Triple t : optionalTriplePattern){
					if(!ListUtils.intersection(new ArrayList<>(VarUtils.getVars(t)), query.getProjectVars()).isEmpty()){
						triplePattern.add(t);
					}
				}
			}
		}
		
		return triplePattern;
	}
	
	public boolean isOptional(Triple triple){
		return optionalTriplePattern.contains(triple);
	}
	
	public Set<Triple> extractTriplePattern(ElementGroup group){
		return extractTriplePattern(group, false);
	}
	
	public Set<Triple> extractTriplePattern(ElementGroup group, boolean ignoreOptionals){
		triplePattern = new HashSet<>();
		optionalTriplePattern = new HashSet<>();
		
		group.visit(this);
		
		//postprocessing: triplepattern in OPTIONAL clause
		if(!ignoreOptionals){
			for(Triple t : optionalTriplePattern){
				triplePattern.add(t);
			}
		}
		
		return triplePattern;
	}
	
	@Override
	public void visit(ElementGroup el) {
		for (Iterator<Element> iterator = el.getElements().iterator(); iterator.hasNext();) {
			Element e = iterator.next();
			e.visit(this);
		}
	}

	@Override
	public void visit(ElementOptional el) {
		optionalCount++;
		inOptionalClause = true;
		el.getOptionalElement().visit(this);
		inOptionalClause = false;
	}

	@Override
	public void visit(ElementTriplesBlock el) {
		for (Iterator<Triple> iterator = el.patternElts(); iterator.hasNext();) {
			Triple t = iterator.next();
			if(inOptionalClause){
				optionalTriplePattern.add(t);
			} else {
				triplePattern.add(t);
			}
		}
	}

	@Override
	public void visit(ElementPathBlock el) {
		for (Iterator<TriplePath> iterator = el.patternElts(); iterator.hasNext();) {
			TriplePath tp = iterator.next();
			if(inOptionalClause){
				optionalTriplePattern.add(tp.asTriple());
			} else {
				if(tp.isTriple()){
					triplePattern.add(tp.asTriple());
				}
			}
		}
	}

	@Override
	public void visit(ElementUnion el) {
		unionCount++;
		for (Iterator<Element> iterator = el.getElements().iterator(); iterator.hasNext();) {
			Element e = iterator.next();
			e.visit(this);
		}
	}
	
	@Override
	public void visit(ElementFilter el) {
		filterCount++;
	}

	public int getUnionCount() {
		return unionCount;
	}

	public int getOptionalCount() {
		return optionalCount;
	}

	public int getFilterCount() {
		return filterCount;
	}
	
	public static void main(String[] args) throws Exception {
		Query q = QueryFactory.create(
				"prefix  dbp:  <http://dbpedia.org/resource/> "
				+ "prefix  dbp2: <http://dbpedia.org/ontology/> "
				+ "select  ?thumbnail where  { dbp:total !dbp2:thumbnail ?thumbnail }");
		TriplePatternExtractor triplePatternExtractor = new TriplePatternExtractor();
		triplePatternExtractor.extractIngoingTriplePatterns(q, q.getProjectVars().get(0));
	}
}
