/*-
 * #%L
 * Triple2NL
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
package org.aksw.triple2nl.functionality;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dllearner.reasoning.SPARQLReasoner;
import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;


/**
 * @author Lorenz Buehmann
 *
 */
public class SPARQLFunctionalityDetector implements FunctionalityDetector{
	
	private SPARQLReasoner sparqlReasoner;

	public SPARQLFunctionalityDetector(QueryExecutionFactory qef) {
		sparqlReasoner = new SPARQLReasoner(qef);
	}

	/* (non-Javadoc)
	 * @see org.aksw.triple2nl.functionality.FunctionalityDetector#isFunctional(java.lang.String)
	 */
	@Override
	public boolean isFunctional(String uri) {
		try {
			return sparqlReasoner.isFunctional(new OWLObjectPropertyImpl(IRI.create(uri)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
