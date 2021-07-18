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
package org.aksw.assessment.rest;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.aksw.assessment.question.QuestionType;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author Lorenz Buehmann
 *
 */
public class RESTServiceTest {

private RESTService restService = new RESTService();
	
	public RESTServiceTest() throws Exception {
		HierarchicalINIConfiguration config = new HierarchicalINIConfiguration();
		try(InputStream is = RESTService.class.getClassLoader().getResourceAsStream("assess_config_dsa.ini")){ // missing file
			config.load(is);

		}
		
		RESTService.loadConfig(config);
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#getQuestionsJSON(javax.servlet.ServletContext, java.lang.String, java.util.List, int)}.
	 */
	@Test
	public void testGetQuestionsJSON() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#getQuestionsJSON2(javax.servlet.ServletContext, org.codehaus.jettison.json.JSONArray, java.util.List, int)}.
	 * @throws JSONException 
	 */
	@Test
	public void testGetQuestionsJSON2() throws JSONException {
		JSONArray domain = new JSONArray();
		JSONObject entry = new JSONObject();
		entry.put("className", "http://dbpedia.org/ontology/Airport");
		JSONArray properties = new JSONArray();
		properties.put("http://dbpedia.org/ontology/owner");
		entry.put("properties", properties);
		domain.put(entry);
		List<String> questionTypes = Lists.newArrayList(QuestionType.MC.getName(), QuestionType.JEOPARDY.getName());
		RESTQuestions restQuestions = restService.getQuestionsJSON2(null, domain, questionTypes, 3);
		System.out.println(restQuestions);
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#getApplicableProperties(javax.servlet.ServletContext, java.lang.String)}.
	 */
	@Test
	public void testGetApplicableProperties() {
		restService.getApplicableProperties(null, "http://dbpedia.org/ontology/SoccerClub");
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#getClasses(javax.servlet.ServletContext)}.
	 */
	@Test
	public void testGetClasses() {
		List<String> classes = restService.getClasses(null);
		System.out.println(classes.size());
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#getEntities(javax.servlet.ServletContext)}.
	 */
	@Test
	public void testGetEntities() {
		restService.getEntities(null);
	}

	/**
	 * Test method for {@link org.aksw.assessment.question.rest.RESTService#precomputeGraphs()}.
	 */
	@Test
	public void testPrecomputeGraphs() {
		restService.precomputeGraphs(null);
	}

}
