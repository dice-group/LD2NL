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

import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.kb.sparql.SparqlQuery;

// import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetRewindable;
public class SPARQLDomainExtractor implements DomainExtractor{
	
	private SparqlEndpoint endpoint;
	
	public SPARQLDomainExtractor(SparqlEndpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String getDomain(String propertyURI) {
		String query = String.format("SELECT ?domain WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#domain> ?domain}", propertyURI);
		ResultSetRewindable rs = new SparqlQuery(query, endpoint).send(false);
		while(rs.hasNext()){
			return rs.next().get("domain").asResource().getURI();
		}
		return null;
	}

}
