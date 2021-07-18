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
package org.aksw.avatar.gender;

import java.util.HashSet;
import java.util.Set;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.web.HttpSC;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * @author Lorenz Buehmann
 *
 */
public class TypeAwareGenderDetector implements GenderDetector{
	
	private static final Logger logger = LoggerFactory.getLogger(TypeAwareGenderDetector.class);

	private QueryExecutionFactory qef;
	
	private GenderDetector genderDetector;
	private Set<String> personTypes = new HashSet<>();
	
	private boolean useInference = true;

	public TypeAwareGenderDetector(QueryExecutionFactory qef, GenderDetector genderDetector) {
		this.qef = qef;
		this.genderDetector = genderDetector;
	}
	
	public TypeAwareGenderDetector(SparqlEndpoint endpoint, GenderDetector genderDetector) {
		this(new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs()), genderDetector);
	}
	
	public void setPersonTypes(Set<String> personTypes){
		this.personTypes = personTypes;
		
		//get the inferred sub types as well
		if(useInference){
			Set<String> inferredTypes = new HashSet<>();
			String queryTemplate = "select ?sub where{?sub <http://www.w3.org/2000/01/rdf-schema#subClassOf>* <%s>.}";
			for (String type : personTypes) {
				String query = String.format(queryTemplate, type);
				try(QueryExecution qe = qef.createQueryExecution(query)) {
					ResultSet rs = qe.execSelect();
					while(rs.hasNext()){
						inferredTypes.add(rs.next().getResource("sub").getURI());
					}
				}
			}
			personTypes.addAll(inferredTypes);
		}
	}
	
	public Gender getGender(String uri, String label) {
		if(isPerson(uri)){
			return genderDetector.getGender(label);
		}
		return Gender.UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see org.aksw.sparql2nl.entitysummarizer.gender.GenderDetector#getGender(java.lang.String)
	 */
	@Override
	public Gender getGender(String name) {
		return genderDetector.getGender(name);
	}
	
	private boolean isPerson(String uri){
		if(personTypes.isEmpty()){
			return true;
		} else {
			//g et types of URI
			Set<String> types = new HashSet<>();
			try {
				String query = "SELECT ?type WHERE {<" + uri + "> a ?type.}";
				try(QueryExecution qe = qef.createQueryExecution(query)) {
					ResultSet rs = qe.execSelect();
					while(rs.hasNext()){
						types.add(rs.next().getResource("type").getURI());
					}
				}
			} catch (Exception e) {
				int code = ((QueryExceptionHTTP)e.getCause()).getResponseCode();
				logger.warn("SPARQL query execution failed: " + code + " - " + HttpSC.getCode(code).getMessage());
			}
			// check for overlap between types of entity and person types
			return !Sets.intersection(personTypes, types).isEmpty();
		}
	}
}
