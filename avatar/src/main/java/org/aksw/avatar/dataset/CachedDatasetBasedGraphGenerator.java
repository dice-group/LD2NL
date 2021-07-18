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
package org.aksw.avatar.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.aksw.avatar.clustering.Node;
import org.aksw.avatar.clustering.WeightedGraph;
import org.aksw.avatar.exceptions.NoGraphAvailableException;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.apache.log4j.Logger;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * @author Lorenz Buehmann
 *
 */
public class CachedDatasetBasedGraphGenerator extends DatasetBasedGraphGenerator{
	
	private static final Logger logger = Logger.getLogger(CachedDatasetBasedGraphGenerator.class.getName());
	
	LoadingCache<Configuration, WeightedGraph> graphs = CacheBuilder.newBuilder()
		       .maximumSize(1000)
		       .build(
		           new CacheLoader<Configuration, WeightedGraph>() {
		             public WeightedGraph load(Configuration key) throws Exception {
		               WeightedGraph graph = buildGraph(key);
		               if(graph != null) {
		            	   return graph;
		               } else {
		            	   throw new NoGraphAvailableException(key.cls);
		               }
		             }
		           });
	
	public File graphsFolder;
	public File graphsSubFolder = new File("graphs");
	
	private final HashFunction hf = Hashing.md5();
	private boolean useCache = true;

	/**
	 * @param endpoint
	 * @param cacheDirectory
	 */
	public CachedDatasetBasedGraphGenerator(SparqlEndpoint endpoint, File cacheDirectory) {
		super(endpoint, cacheDirectory);
		
		if(cacheDirectory != null){
			graphsFolder = new File(cacheDirectory, graphsSubFolder.getName());
			graphsFolder.mkdirs();
		}
	}
	
	/**
	 * @param endpoint
	 * @param cacheDirectory
	 */
	public CachedDatasetBasedGraphGenerator(SparqlEndpoint endpoint, String cacheDirectory) {
		super(endpoint, cacheDirectory);
		
		if(cacheDirectory != null){
			graphsFolder = new File(cacheDirectory, graphsSubFolder.getName());
			graphsFolder.mkdirs();
		}
	}
	
	public CachedDatasetBasedGraphGenerator(QueryExecutionFactory qef, File cacheDirectory) {
		super(qef, cacheDirectory);
		
		if(cacheDirectory != null){
			graphsFolder = new File(cacheDirectory, graphsSubFolder.getName());
			graphsFolder.mkdirs();
		}
	}
	
	public CachedDatasetBasedGraphGenerator(QueryExecutionFactory qef, String cacheDirectory) {
		super(qef, cacheDirectory);
		
		if(cacheDirectory != null){
			graphsFolder = new File(cacheDirectory, graphsSubFolder.getName());
			graphsFolder.mkdirs();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.aksw.sparql2nl.entitysummarizer.dataset.DatasetBasedGraphGenerator#generateGraph(org.dllearner.core.owl.NamedClass, double, java.lang.String, org.aksw.sparql2nl.entitysummarizer.dataset.DatasetBasedGraphGenerator.Cooccurrence)
	 */
	@Override
	public WeightedGraph generateGraph(OWLClass cls, double threshold, String namespace, Cooccurrence c) throws NoGraphAvailableException{
		try {
			return graphs.get(new Configuration(cls, threshold, namespace, c));
		} catch (ExecutionException e) {
			if(e.getCause() instanceof NoGraphAvailableException) {
				throw (NoGraphAvailableException)e.getCause();
			} else {
				logger.error(e, e);
			}
		}
		return null;
	}
	
	private WeightedGraph buildGraph(Configuration configuration){
		HashCode hc = hf.newHasher()
		       .putString(configuration.cls.toStringID(), Charsets.UTF_8)
		       .putDouble(configuration.threshold)
		       .putString(configuration.c.name(), Charsets.UTF_8)
		       .hash();
		String filename = hc.toString() + ".graph";
		File file = new File(graphsFolder, filename);
		WeightedGraph g = null;
		if(isUseCache() && file.exists()){
			logger.info("Loading summary graph for " + configuration.cls + " from disk...");
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
				g = (WeightedGraph) ois.readObject();
				
				Set<OWLObjectProperty> outgoingProperties = new HashSet<OWLObjectProperty>();
				if(g != null) {
					for (Node node : g.getNodes().keySet()) {
						if(node.outgoing){
							outgoingProperties.add(new OWLObjectPropertyImpl(IRI.create(node.label)));
						}
					}
				}
				class2OutgoingProperties.put(configuration.cls, outgoingProperties );
			} catch (FileNotFoundException e) {
				logger.error(e, e);
			} catch (IOException e) {
				logger.error(e, e);
			} catch (ClassNotFoundException e) {
				logger.error(e, e);
			}
		} else {
			logger.info("Generating summary graph for type " + configuration.cls + "...");
			try {
				g = super.generateGraph(configuration.cls, configuration.threshold, configuration.namespace, configuration.c);
			} catch (NoGraphAvailableException e1) {
				e1.printStackTrace();
			}
			if(isUseCache()){
				try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
					oos.writeObject(g);
				} catch (FileNotFoundException e) {
					logger.error(e, e);
				} catch (IOException e) {
					logger.error(e, e);
				} 
			}
		}
		logger.info("...done.");
		return g;
	}
	
	/**
	 * @param useCache the useCache to set
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
	
	public void precomputeGraphs(double threshold, String namespace, Cooccurrence c){
		Set<OWLClass> classes = reasoner.getOWLClasses();
		for (OWLClass cls : classes) {
			try {
				generateGraph(cls, threshold, namespace, c);
			} catch (NoGraphAvailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isUseCache(){
		return useCache && graphsFolder != null;
	}
	
	class Configuration{
		OWLClass cls;
		double threshold;
		String namespace;
		Cooccurrence c;
		
		public Configuration(OWLClass cls, double threshold, String namespace, Cooccurrence c) {
			this.cls = cls;
			this.threshold = threshold;
			this.namespace = namespace;
			this.c = c;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			result = prime * result + ((cls == null) ? 0 : cls.hashCode());
			result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
			long temp;
			temp = Double.doubleToLongBits(threshold);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Configuration other = (Configuration) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (c != other.c)
				return false;
			if (cls == null) {
				if (other.cls != null)
					return false;
			} else if (!cls.equals(other.cls))
				return false;
			if (namespace == null) {
				if (other.namespace != null)
					return false;
			} else if (!namespace.equals(other.namespace))
				return false;
			if (Double.doubleToLongBits(threshold) != Double.doubleToLongBits(other.threshold))
				return false;
			return true;
		}

		private CachedDatasetBasedGraphGenerator getOuterType() {
			return CachedDatasetBasedGraphGenerator.this;
		}
		
		
	}

}
