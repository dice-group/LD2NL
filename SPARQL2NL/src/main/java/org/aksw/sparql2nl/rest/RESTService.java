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
/**
 * 
 */
package org.aksw.sparql2nl.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.aksw.triple2nl.TripleConverter;
import org.apache.jena.riot.Lang;
import org.apache.log4j.Logger;
import org.dllearner.kb.sparql.SparqlEndpoint;

import simplenlg.lexicon.Lexicon;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * @author Lorenz Buehmann
 *
 */
@Path("/sparql2nl")
public class RESTService {
	
	private static final Logger logger = Logger.getLogger(RESTService.class.getName());
	private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
	
	public RESTService() {
		
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "test";
	}
	
	@POST
	@Context
	@Path("/triple2nl")
	@Produces(MediaType.APPLICATION_JSON)
	public String convertTriples(@Context ServletContext context, @FormParam("endpoint") String endpointURL, @FormParam("data") String data) {
		logger.info("REST Request - Converting triples");
		logger.info("Endpoint:" + endpointURL);
		logger.info("data:" + data);
		try {
			SparqlEndpoint endpoint = new SparqlEndpoint(new URL(endpointURL));
			
			//load triples
			Model model = ModelFactory.createDefaultModel();
			try(InputStream is = new ByteArrayInputStream(data.getBytes())){
				model.read(is, null, Lang.TURTLE.getLabel());
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info(model.size() + " triples:");
			try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
				model.write(baos, "TURTLE");
				logger.info(new String(baos.toString("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			List<Triple> triples = new ArrayList<>((int) model.size());
			StmtIterator iterator = model.listStatements();
			while (iterator.hasNext()) {
				Statement statement = (Statement) iterator.next();
				triples.add(statement.asTriple());
			}
			
			//convert to text
			if(!model.isEmpty()){
				TripleConverter converter = new TripleConverter(endpoint, context.getRealPath("cache"), null, lexicon);
				String text = converter.convert(triples);
				logger.info("Text:" + text);
				return text;
			}
			
		} catch (MalformedURLException e) {
			logger.error("Malformed endpoint URL " + endpointURL);
		}
		
		logger.info("Done.");
		return null;
 
	}
	
	@GET
	@Context
	@Path("/sparql2nl")
	@Produces(MediaType.APPLICATION_JSON)
	public String convertSPARQL(@Context ServletContext context, @QueryParam("endpoint") String endpointURL, @QueryParam("query") String sparqlQuery) {
		logger.info("REST Request");
		
		
		logger.info("Done.");
		return null;
	}
}
