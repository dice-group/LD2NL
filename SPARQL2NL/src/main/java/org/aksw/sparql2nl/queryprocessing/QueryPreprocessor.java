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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.InvalidFormatException;

import org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLGwithPostprocessing;
import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.dllearner.kb.sparql.SparqlEndpoint;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;

public class QueryPreprocessor {
	
	private TypeExtractor2 typeExtr;
	private DefaultIRIConverter uriConverter;
	private Parser parser;
	
	private static final String parseModelFile = "/home/me/Downloads/en-parser-chunking.bin";
	
	public QueryPreprocessor(SparqlEndpoint endpoint, String parseModelFile) {
		typeExtr = new TypeExtractor2(endpoint);
		typeExtr.setInferTypes(true);
		typeExtr.setDomainExtractor(new SPARQLDomainExtractor(endpoint));
		typeExtr.setRangeExtractor(new SPARQLRangeExtractor(endpoint));
		
		uriConverter = new DefaultIRIConverter(endpoint);
		
		try {
			InputStream modelIn = new FileInputStream(parseModelFile);
			ParserModel model = new ParserModel(modelIn);
			parser = ParserFactory.create(model);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public QueryPreprocessor(SparqlEndpoint endpoint) {
		this(endpoint, parseModelFile);
	}
	
	public String replaceVariablesWithTypes(String queryString){
		Map<String, Set<String>> label2ReplacedVars = new HashMap<>();
		
		Map<String, Set<String>> var2TypesMap = typeExtr.extractTypes(QueryFactory.create(queryString, Syntax.syntaxARQ));
		String replacedQuery = queryString;
		for(Entry<String, Set<String>> entry : var2TypesMap.entrySet()){
			String var = entry.getKey();
			Set<String> types = entry.getValue();
			if(!types.isEmpty()){
				//TODO What if more than one types exists? How to handle long labels?
				String uri = types.iterator().next();
				String label = uriConverter.convert(uri);
				//normalize label
				label = processLabel(label);
				label = getHead(label);
				
				if(label2ReplacedVars.containsKey(label)){
					Set<String> oldReplacedVars = label2ReplacedVars.get(label);
					Set<String> newReplacedVars = new HashSet<>();
					// replace the old replaced vars
					int i = 1;
					for(String rV : oldReplacedVars){
						String newVar = label + i++;
						replacedQuery = replacedQuery.replace("?" + rV, "?" + newVar);
						newReplacedVars.add(newVar);
					}
					//replace the new var
					String newVar = label + i;
					replacedQuery = replacedQuery.replace("?" + var, "?" + newVar);
					newReplacedVars.add(newVar);
					label2ReplacedVars.put(label, newReplacedVars);
					
				} else {
					Set<String> replacedVars = new HashSet<>();
					replacedVars.add(label);
					replacedQuery = replacedQuery.replace("?" + var, "?" + label);
					label2ReplacedVars.put(label, replacedVars);
				}
			}
		}
		return replacedQuery;
	}
	
	public org.apache.jena.query.Query replaceVariablesWithTypes(org.apache.jena.query.Query query){
		return QueryFactory.create(replaceVariablesWithTypes(query.toString()), Syntax.syntaxARQ);
	}
	
	private String processLabel(String label){
		String[] tokens = label.split(" ");
		String nLabel = tokens[0].substring(0, 1).toLowerCase() + tokens[0].substring(1);
		for(int i = 1; i < tokens.length; i++){
			nLabel = nLabel + tokens[i].substring(0, 1).toUpperCase() + tokens[i].substring(1);
		}
		return nLabel;
	}
	
	public String getHead(String phrase){
		Parse topParses[] = ParserTool.parseLine(phrase, parser, 1);
		  Parse top = topParses[0];
		  top.show();
		  if(top.getChildCount() == 1){
			  return getHead(top.getChildren()[0]);
		  } else {
			  for(Parse child : top.getChildren()){
				  return getHead(child);
			  }
		  }
		return null;
	}
	
	public String getHead(Parse parse){
		parse.show();
		for(Parse child : parse.getChildren()){
			child.show();
			  if(child.getType().equals("NP")){
				  return child.toString();
			  } else {
				  return getHead(child);
			  }
		}
		return parse.getText();
	}
	
	public static void main(String[] args) {
		QueryPreprocessor qp = new QueryPreprocessor(SparqlEndpoint.getEndpointDBpedia());
		System.out.println(qp.getHead("Host Cities of the Summer Olympic Games"));
		System.out.println(qp.getHead("actors born in 1945"));
		System.out.println(qp.getHead("soccer clubs in Premier League"));
		
		SimpleNLGwithPostprocessing nlg = new SimpleNLGwithPostprocessing(SparqlEndpoint.getEndpointDBpedia());
		
		String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
				"PREFIX dbp: <http://dbpedia.org/property/> " +
				"PREFIX res: <http://dbpedia.org/resource/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"SELECT DISTINCT ?uri ?string WHERE {" +
				"	?uri rdf:type dbo:Film ." +
				"	?uri dbo:director res:Sam_Raimi ." +
				"	{ ?uri dbo:releaseDate ?x . } UNION { ?uri dbp:released ?x . }" +
				"	res:Army_of_Darkness dbo:releaseDate ?y ." +
				"	FILTER (?x > ?y)" +
				"	OPTIONAL { ?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }" +
				"}";
		org.apache.jena.query.Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL_11);
		System.out.println(nlg.getNLR(query));
		String replacedQueryString = new QueryPreprocessor(SparqlEndpoint.getEndpointDBpedia()).replaceVariablesWithTypes(query.toString());
		query = QueryFactory.create(replacedQueryString, Syntax.syntaxSPARQL_11);
		System.out.println(query);
		System.out.println(nlg.getNLR(query));
	}

}
