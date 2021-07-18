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
package org.aksw.triple2nl.converter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.triple2nl.converter.URIDereferencer.DereferencingFailedException;
import org.apache.commons.collections15.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.apache.jena.web.HttpSC;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.utilities.OwlApiJenaUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Converts IRIs into natural language.
 * @author Lorenz Buehmann
 *
 */
public class DefaultIRIConverter implements IRIConverter{
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultIRIConverter.class);
	
	private IRIShortFormProvider sfp = new SimpleIRIShortFormProvider();
	private LRUMap<String, String> uri2LabelCache = new LRUMap<>(200);
	
	private QueryExecutionFactory qef;
	
	private List<String> labelProperties = Lists.newArrayList(
			"http://www.w3.org/2000/01/rdf-schema#label",
			"http://www.w3.org/2004/02/skos/core#prefLabel",
			"http://www.w3.org/2004/02/skos/core#altLabel",
			"http://xmlns.com/foaf/0.1/name");
	
	private String language = "en";

	// normalization options
	private boolean splitCamelCase = true;
	private boolean replaceUnderScores = true;
	private boolean toLowerCase = false;
	private boolean omitContentInBrackets = true;
	
	private URIDereferencer uriDereferencer;
	
	public DefaultIRIConverter(SparqlEndpoint endpoint, String cacheDirectory) {
		this(new QueryExecutionFactoryHttp(endpoint.getURL().toString(), endpoint.getDefaultGraphURIs()), cacheDirectory);
	}
	
	public DefaultIRIConverter(SparqlEndpoint endpoint) {
		this(endpoint, null);
	}
	
	public DefaultIRIConverter(QueryExecutionFactory qef) {
		this(qef, null);
	}
	
	public DefaultIRIConverter(QueryExecutionFactory qef, String cacheDirectory) {
		this.qef = qef;
		
		// use tmp as default cache directory
		if(cacheDirectory == null) {
			cacheDirectory = System.getProperty("java.io.tmpdir") + "/triple2nl/cache";
		}
		
		cacheDirectory += "/dereferenced";
		try {
			Files.createDirectories(Paths.get(cacheDirectory));
		} catch (IOException e) {
			logger.error("Creation of folder + " + cacheDirectory + " failed.", e);
		}
		logger.debug("Using folder " + cacheDirectory + " as cache for IRI converter.");
		
		uriDereferencer = new URIDereferencer(new File(cacheDirectory));
	}
	
	public DefaultIRIConverter(Model model) {
		this(new QueryExecutionFactoryModel(model));
	}
	
	public DefaultIRIConverter(OWLOntology ontology) {
		this(OwlApiJenaUtils.getModel(ontology));
	}
	
	@Override
	public String convert(String iri){
		return convert(iri, false);
	}
	
	@Override
	public String convert(String iri, boolean dereferenceURI){
		
		// handle built-in entities first
		if (iri.equals(RDF.type.getURI())) {
            return "type";
        } else if (iri.equals(RDFS.label.getURI())) {
            return "label";
        }
		
		// check if already cached
		String label = uri2LabelCache.get(iri);
		
		// if not in cache
		if(label == null){
			// 1. check if it's some built-in resource
			try {
				label = getLabelFromBuiltIn(iri);
			} catch (Exception e) {
				logger.error("Getting label for " + iri + " from knowledge base failed.", e);
			}

			// 2. try to get the label from the endpoint
			if(label == null){
				 try {
						label = getLabelFromKnowledgebase(iri);
					} catch (Exception e) {
						logger.error("Getting label for " + iri + " from knowledge base failed.", e);
					}
			}
            
            // 3. try to dereference the IRI and search for the label in the returned triples
            if(dereferenceURI && label == null){
            	try {
					label = getLabelFromLinkedData(iri);
				} catch (Exception e) {e.printStackTrace();
					logger.error("Dereferencing of " + iri + " failed.");
				}
            }
            
            // 4. use the short form of the IRI
            if(label == null){
            	try {
					label = sfp.getShortForm(IRI.create(URLDecoder.decode(iri, "UTF-8")));

		            // do some normalization, e.g. remove underscores
		            label = normalize(label);

            	} catch (UnsupportedEncodingException e) {
					logger.error("Getting short form of " + iri + "failed.", e);
				}
            }
            
            // 5. use the IRI itself
            if(label == null){
            	label = iri;
            }

		}
	    
		// put into cache
		uri2LabelCache.put(iri, label);
		
		return label;
	}
	
	/**
	 * Set a list of properties that return textual representations a IRI, e.g.
	 * rdfs:label, foaf:name, etc.
	 * The first property with a value is used.
	 * @param labelProperties a list of properties
	 */
	public void setLabelProperties(List<String> labelProperties) {
		this.labelProperties = labelProperties;
	}
	
	/**
	 * Set the language of the returned textual representation.
	 * @param language the language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void setSplitCamelCase(boolean splitCamelCase) {
		this.splitCamelCase = splitCamelCase;
	}
	
	public void setReplaceUnderScores(boolean replaceUnderScores) {
		this.replaceUnderScores = replaceUnderScores;
	}
	
	public void setOmitContentInBrackets(boolean omitContentInBrackets) {
		this.omitContentInBrackets = omitContentInBrackets;
	}
	
	public void setToLowerCase(boolean toLowerCase) {
		this.toLowerCase = toLowerCase;
	}
	
	private String getLabelFromBuiltIn(String uri){
		try {
			IRI iri = IRI.create(URLDecoder.decode(uri, "UTF-8"));
			
			// if IRI is built-in entity
			if(iri.isReservedVocabulary()) {
				// use the short form
				String label = sfp.getShortForm(iri);
				
				 // if it is a XSD numeric data type, we attach "value"
	            if(uri.equals(XSD.nonNegativeInteger.getURI()) || uri.equals(XSD.integer.getURI())
	            		|| uri.equals(XSD.negativeInteger.getURI()) || uri.equals(XSD.decimal.getURI())
	            		|| uri.equals(XSD.xdouble.getURI()) || uri.equals(XSD.xfloat.getURI())
	            		|| uri.equals(XSD.xint.getURI()) || uri.equals(XSD.xshort.getURI())
	            		|| uri.equals(XSD.xbyte.getURI()) || uri.equals(XSD.xlong.getURI())
	            		){
	            	label += " value";
	            }
	            
	            return label;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Getting short form of " + uri + "failed.", e);
		}
		return null;
	}
	
	private String getLabelFromKnowledgebase(String iri){
		ParameterizedSparqlString query = new ParameterizedSparqlString(
				"SELECT ?label WHERE {" +
						"?s ?p1 ?o ." +
						"optional {" +
						"		?s ?p ?label. " +
						"		FILTER (LANGMATCHES(LANG(?label),'" + language + "' ))" +
						"	}" +
						"optional {" +
						"     ?s ?p ?label" +
						"   }" +
						"} " +
						"ORDER BY DESC(?label) LIMIT 1");
		query.setIri("s", iri);
		// for each label property
		for (String labelProperty : labelProperties) {
			query.setIri("p", labelProperty);
			try (QueryExecution qe = qef.createQueryExecution(query.toString())){
				ResultSet rs = qe.execSelect();
				if(rs.hasNext()){
					return rs.next().getLiteral("label").getLexicalForm();
				}
			} catch (Exception e) {
				e.printStackTrace();
				int code = -1;
				//cached exception is wrapped in a RuntimeException
				if(e.getCause() instanceof QueryExceptionHTTP){
					code = ((QueryExceptionHTTP)e.getCause()).getResponseCode();
				} else if(e instanceof QueryExceptionHTTP){
					code = ((QueryExceptionHTTP) e).getResponseCode();
				}
				logger.warn("Getting label of " + iri + " from SPARQL endpoint failed: " + code + " - " + HttpSC.getCode(code).getMessage());
			}
		}
		return null;
	}
	
	 /**
     * Dereference the IRI and look for label property value.
     * @param iri the IRI
     * @return the label if exist, otherwise <code>null</code>
     */
    private String getLabelFromLinkedData(String iri){
    	logger.debug("Get label for " + iri + " from Linked Data...");
    	
		try {
			// 1. get triples for the IRI by sending a Linked Data request
			Model model = uriDereferencer.dereference(iri);
			
			// 2. check if we find a label in the triples
			for (String labelProperty : labelProperties) {
				for(Statement st : model.listStatements(model.getResource(iri), model.getProperty(labelProperty), (RDFNode)null).toList()){
					Literal literal = st.getObject().asLiteral();
					
					// language check
					String language = literal.getLanguage();
					if(language != null && language.equals(this.language)){
						return literal.getLexicalForm();
					}
				}
			}
		} catch (DereferencingFailedException e) {
			logger.error(e.getMessage(), e);
		}
    	return null;
    }
    
    private String normalize(String s){
		if(replaceUnderScores){
			s = s.replace("_", " ");
		}
        if(splitCamelCase){
        	s = splitCamelCase(s);
        }
        if(toLowerCase){
        	s = s.toLowerCase();
        }
        if(omitContentInBrackets){
        	s = s.replaceAll("\\(.+?\\)", "").trim();
        }
        return s;
	}
    
    private static String splitCamelCase(String s) {
    	StringBuilder sb = new StringBuilder();
    	for (String token : s.split(" ")) {
			String[] split = StringUtils.splitByCharacterTypeCamelCase(token);
			Deque<String> list = new ArrayDeque<>();
			for (int i = 0; i < split.length; i++) {
				String s1 = split[i];
				if(i > 0 && s1.length() == 1 && !org.apache.commons.lang3.StringUtils.isNumeric(s1)) { // single character -> append to previous token
					list.add(list.pollLast() + s1);
				} else {
					list.add(s1);
				}
			}
			sb.append(StringUtils.join(list, ' ')).append(" ");
		}
    	return sb.toString().trim();
//    	return s.replaceAll(
//    	      String.format("%s|%s|%s",
//    	         "(?<=[A-Z])(?=[A-Z][a-z])",
//    	         "(?<=[^A-Z])(?=[A-Z])",
//    	         "(?<=[A-Za-z])(?=[^A-Za-z])"
//    	      ),
//    	      " "
//    	   );
    	}

    public static void main(String[] args) {
    	DefaultIRIConverter converter = new DefaultIRIConverter(SparqlEndpoint.getEndpointDBpedia());
    	
		String label = converter.convert("http://dbpedia.org/resource/Nuclear_Reactor_Technology");
		System.out.println(label);
		
		label = converter.convert("http://dbpedia.org/resource/Woodroffe_School");
		System.out.println(label);
		
		label = converter.convert("http://dbpedia.org/ontology/isBornIn", true);
		System.out.println(label);
		
		label = converter.convert("http://www.w3.org/2001/XMLSchema#integer");
		System.out.println(label);
    }

}
