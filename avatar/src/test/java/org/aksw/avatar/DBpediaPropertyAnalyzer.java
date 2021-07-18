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

import java.util.Set;
import java.util.TreeSet;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.dllearner.kb.sparql.SparqlEndpoint;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 * @author Lorenz Buehmann
 *
 */
public class DBpediaPropertyAnalyzer {
	
	public static void main(String[] args) throws Exception {
		SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
		QueryExecutionFactory qef = new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs());
		//get all object properties
		Set<String> properties = new TreeSet<>();
		String query = "SELECT ?p WHERE {?p a <http://www.w3.org/2002/07/owl#ObjectProperty>}";
		QueryExecution qe = qef.createQueryExecution(query);
		ResultSet rs = qe.execSelect();
		QuerySolution qs;
		while(rs.hasNext()){
			qs = rs.next();
			properties.add(qs.getResource("p").getURI());
		}
		qe.close();
		//get a value for each property
		for (String property : properties) {
			query = "SELECT ?o WHERE {?s <" + property + "> ?o.} LIMIT 1";
			qe = qef.createQueryExecution(query);
			rs = qe.execSelect();
			if(rs.hasNext()){
				qs = rs.next();
				if(qs.get("o").isURIResource()){
					String objectURI = qs.getResource("o").getURI();
					if(objectURI.contains("__")){
						System.out.println(property);
					}
				}
			}
			qe.close();
		}
		
	}

}
