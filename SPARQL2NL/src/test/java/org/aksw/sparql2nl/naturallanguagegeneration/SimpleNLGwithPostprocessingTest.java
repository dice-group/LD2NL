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
package org.aksw.sparql2nl.naturallanguagegeneration;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dllearner.kb.sparql.SparqlEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;

public class SimpleNLGwithPostprocessingTest {
	
	private static final SparqlEndpoint ENDPOINT = SparqlEndpoint.getEndpointDBpedia();
	private static final SimpleNLGwithPostprocessing nlg = new SimpleNLGwithPostprocessing(ENDPOINT);
	private static final File testFile = new File("src/test/resources/sparql_test_queries.txt");
	private static final Map<String, Query> id2Queries = new HashMap<String, Query>();
	
	@BeforeClass
	public static void setUpOnce() throws Exception{
		List<String> lines = Files.readLines(testFile, Charsets.UTF_8);
		Pattern idPattern = Pattern.compile("#([A-Za-z0-9]+)#");

		StringBuilder queryString = null;
		String id = null;
		for (String line : lines) {
			Matcher matcher = idPattern.matcher(line);
			if(matcher.find()){
				if(id != null){
					Query query = QueryFactory.create(queryString.toString(), Syntax.syntaxSPARQL_11);
					id2Queries.put(id, query);
				}
				queryString = new StringBuilder();
				id = matcher.group(1);
			} else {
				queryString.append(line).append("\n");
			}
		}
		Query query = QueryFactory.create(queryString.toString(), Syntax.syntaxSPARQL_11);
		id2Queries.put(id, query);
		
	}
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAggregate() {
		Query query = id2Queries.get("1");
		String nlr = nlg.getNLR(query);
		System.out.println(nlr);
	}
	
	@Test
	public void testMainEntityIsObjectOnly() {
		Query query = id2Queries.get("2");
		String nlr = nlg.getNLR(query);
		System.out.println(nlr);
	}

}
