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
package org.aksw.triple2nl.converter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.WebContent;
import org.apache.log4j.Logger;

import com.google.common.net.UrlEscapers;
/**
 * Class to retrieve triples based on the Linked Data dereferencing paradigm.
 * @author Lorenz Buehmann
 *
 */
public class URIDereferencer {
	
	private static final Logger logger = Logger.getLogger(URIDereferencer.class.getName());
	
	//the content type used in the accept header
	private String contentType = WebContent.contentTypeRDFXML;
	
	//settings for file based caching
	private File cacheDirectory;
	private boolean useCache = true;
	private Lang cacheFileLanguage = Lang.TURTLE;
	private String cacheFileExtension = cacheFileLanguage.getFileExtensions().get(0);

	public URIDereferencer(File cacheDirectory) {
		this.cacheDirectory = cacheDirectory;
		this.useCache = cacheDirectory != null;
	}
	
	public URIDereferencer() {
		this(null);
	}
	
	/**
	 * Get the triples that describe the entity identified by the URI.
	 * @param uri the URI of the entity
	 * @return JENA model containing the triples
	 * @throws DereferencingFailedException
	 */
	public Model dereference(String uri) throws DereferencingFailedException{
		return dereference(URI.create(uri));
	}
	
	/**
	 * Get the triples that describe the entity identified by the URI.
	 * @param uri the URI of the entity
	 * @return JENA model containing the triples
	 * @throws DereferencingFailedException
	 */
	public Model dereference(URI uri) throws DereferencingFailedException{
		logger.debug("Dereferencing " + uri + "...");
		Model model = null;
		
		// check if already cached
		if(useCache()){
			model = loadFromDisk(uri);
		}
		
		// if we got nothing from cache
		if (model == null) {
			model = ModelFactory.createDefaultModel();
			try {
				URLConnection conn = uri.toURL().openConnection();
				conn.setRequestProperty("Accept", contentType);

				InputStream is = conn.getInputStream();
				model.read(is, null, RDFLanguages.contentTypeToLang(contentType).getLabel());
				is.close();

				if (useCache()) {
					writeToDisk(uri, model);
				}
			} catch (IOException e) {
				throw new DereferencingFailedException(uri, e);
			}
		}
		
    	logger.debug("Done. Got " + model.size() + " triples for " + uri);
    	return model;
	}
	
	/**
	 * Whether to use a file based caching solution, i.e. for each URI the
	 * result is stored in a separate file on disk.
	 * @param useCache use cache or not
	 */
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
	
	private boolean useCache(){
		return useCache && cacheDirectory != null;
	}
	
	private File getCacheFile(URI uri){
		String filename = UrlEscapers.urlPathSegmentEscaper().escape(uri.toString()) + "." + cacheFileExtension;
    	File cacheFile = new File(cacheDirectory, filename);
    	return cacheFile;
	}
	
	private Model loadFromDisk(URI uri){
    	File cachedFile = getCacheFile(uri);
    	
    	if(cachedFile.exists()){
    		Model model = ModelFactory.createDefaultModel();
    		try(InputStream is = new BufferedInputStream(new FileInputStream(cachedFile))){
    			model.read(is, null, cacheFileLanguage.getLabel());
    			return model;
    		} catch (IOException e) {
				logger.error("Failed loading from disk.", e);
			}
    		
    	}
    	return null;
	}
	
	private void writeToDisk(URI uri, Model model){
		logger.debug("Writing model for " + uri + "to disk.");
		
		File cacheFile = getCacheFile(uri);
		
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(cacheFile))) {
			model.write(os, cacheFileLanguage.getLabel());
		} catch (IOException e) {
			logger.error("Could not write to disk.", e);
		}
	}
	
	class DereferencingFailedException extends Exception{
		
		private static final long serialVersionUID = -1830907519484713882L;

		public DereferencingFailedException(URI uri, Exception cause) {
			super("Dereferencing " + uri + " failed.", cause);
		}
	}
}
