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

import java.io.File;

import org.aksw.avatar.clustering.WeightedGraph;
import org.aksw.avatar.dataset.CachedDatasetBasedGraphGenerator;
import org.aksw.avatar.dataset.DatasetBasedGraphGenerator;
import org.aksw.avatar.exceptions.NoGraphAvailableException;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.semanticweb.owlapi.model.OWLClass;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Lorenz Buehmann
 *
 */
public class EntitySummarizationGenerator {
	
	private File cacheDirectory = new File("cache");
	private DatasetBasedGraphGenerator graphGenerator;
	
	private double propertyFrequencyThreshold;

	public EntitySummarizationGenerator(SparqlEndpoint endpoint, File cacheDirectory, double propertyFrequencyThreshold) {
		this.cacheDirectory  = cacheDirectory;
		this.propertyFrequencyThreshold = propertyFrequencyThreshold;
		
		graphGenerator = new CachedDatasetBasedGraphGenerator(endpoint, cacheDirectory);
	}
	
	public EntitySummarizationGenerator(QueryExecutionFactory qef, File cacheDirectory) {
		this.cacheDirectory  = cacheDirectory;
		graphGenerator = new CachedDatasetBasedGraphGenerator(qef, cacheDirectory);
	}
	
	public EntitySummarization generateEntitySummarization(Resource entity){
		//determine the most specific class of the entity
		OWLClass cls = null;
		
		return generateEntitySummarization(entity, cls);
	}
	
	public EntitySummarization generateEntitySummarization(Resource entity, OWLClass cls){
		//generate a graph with the most interesting properties
		try {
			WeightedGraph graph = graphGenerator.generateGraph(cls, propertyFrequencyThreshold);
		} catch (NoGraphAvailableException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
