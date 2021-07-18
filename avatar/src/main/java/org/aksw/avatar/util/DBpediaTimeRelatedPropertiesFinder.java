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
package org.aksw.avatar.util;

import java.util.Set;

import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.reasoning.SPARQLReasoner;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

/**
 * @author Lorenz Buehmann
 *
 */
public class DBpediaTimeRelatedPropertiesFinder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
		SparqlEndpointKS ks = new SparqlEndpointKS(endpoint);
		SPARQLReasoner reasoner = new SPARQLReasoner(ks);
		QueryExecutionFactoryHttp qef = new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs());
		Set<OWLObjectProperty> properties = reasoner.getOWLObjectProperties();
		for (OWLObjectProperty p : properties) {
			String query = "SELECT ?o WHERE {?s <" + p + "> ?o} LIMIT 1"; 
			QueryExecution qe = qef.createQueryExecution(query);
			ResultSet rs = qe.execSelect();
			if(rs.hasNext()){
				Resource object = rs.next().getResource("o");
				if(object.getURI().contains("__")){
					System.out.println(p);
				}
			}
			qe.close();
		}

	}

}
