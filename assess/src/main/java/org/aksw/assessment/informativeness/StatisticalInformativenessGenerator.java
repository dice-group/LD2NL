/*
 * #%L
 * ASSESS
 * %%
 * Copyright (C) 2015 Agile Knowledge Engineering and Semantic Web (AKSW)
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
package org.aksw.assessment.informativeness;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.dllearner.kb.sparql.SparqlEndpoint;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;

/**
 * Computes the informativeness of a given triple by executing two SPARQL
 * queries to get the number of incoming and outgoing links. Given a triple t we
 * get informativeness = log(|incomingLinks(t_s)| + |outgoingLinks(t_o)|)
 * 
 * This is based on the work of Giuseppe Pirro in REWOrD: Semantic Relatedness
 * in the Web of Data, AAAI 2012.
 * 
 * @author Lorenz Buehmann
 *
 */
public class StatisticalInformativenessGenerator implements InformativenessGenerator{
	
	private QueryExecutionFactory qef;
	
	private static final ParameterizedSparqlString incomingLinksTemplate = new ParameterizedSparqlString(
			"SELECT (COUNT(*) AS ?cnt) WHERE {?s ?p ?o}");
	private static final ParameterizedSparqlString outgoingLinksTemplate = new ParameterizedSparqlString(
			"SELECT (COUNT(*) AS ?cnt) WHERE {?s ?p ?o}");

	public StatisticalInformativenessGenerator(SparqlEndpoint endpoint) {
		qef = new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs());
	}

	/* (non-Javadoc)
	 * @see org.aksw.assessment.question.informativeness.InformativenessGenerator#computeInformativeness(com.hp.hpl.jena.graph.Triple)
	 */
	@Override
	public double computeInformativeness(Triple triple) {
		double informativeness = 0;
		
		//get the popularity of the subject, i.e. the incoming links
		incomingLinksTemplate.setIri("s", triple.getSubject().getURI());
		Query query = incomingLinksTemplate.asQuery();
		QueryExecution qe = qef.createQueryExecution(query);
		ResultSet rs = qe.execSelect();
		int subjectPropularity = rs.next().getLiteral("cnt").getInt();
		
		//get the popularity of the object, i.e. the incoming links
		outgoingLinksTemplate.setIri("o", triple.getObject().getURI());
		query = outgoingLinksTemplate.asQuery();
		qe = qef.createQueryExecution(query);
		rs = qe.execSelect();
		int objectPropularity = rs.next().getLiteral("cnt").getInt();
		
		informativeness = Math.log(subjectPropularity + objectPropularity);
		
		qe.close();
		
		return informativeness;
	}

}
