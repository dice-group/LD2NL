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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlQuery;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.vocabulary.OWL;

public class SPARQLBasedLCS implements LCS{
	
	private SparqlEndpoint endpoint;
	
	public SPARQLBasedLCS(SparqlEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String getLCS(Collection<String> classes) {
		Iterator<String> iter = classes.iterator();
		String lcs = getLCS(iter.next(), iter.next());
		while(iter.hasNext()){
			lcs = getLCS(lcs, iter.next());
		}
		return lcs;
	}
	
	private String getLCS(String cls1, String cls2){
		//check some common cases first
		if(cls1.equals(cls2)){
			return cls1;
		}
		if(cls1.equals(OWL.Thing.getURI()) || cls2.equals(OWL.Thing.getURI())){
			return OWL.Thing.getURI();
		}
		
		
		
		//if not a common case
		return null;
	}
	
	private Set<String> getSuperClasses(String cls){
		Set<String> superClasses = new HashSet<>();
		String query = String.format("SELECT ?sup WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?sup}", cls);
		ResultSetRewindable rs = new SparqlQuery(query, endpoint).send(false);
		QuerySolution qs;
		while(rs.hasNext()){
			qs = rs.next();
			if(qs.get("sup").isURIResource()){
				superClasses.add(qs.get("sup").asResource().getURI());
			}
		}
		return superClasses;
	}

}
