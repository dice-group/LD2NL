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
package org.aksw.assessment.util;

import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.jena.rdf.model.Resource;

/**
 * This class contains basically a set of defined properties that are meaningless for the generation of questions
 * in the ASSESS project. 
 * @author Lorenz Buehmann
 *
 */
public class GeneralPropertyBlackList implements BlackList{

	public static Set<String> blacklist = Sets.newHashSet(
		"http://www.w3.org/ns/prov#was", 
	    "http://www.w3.org/2002/07/owl#sameAs", 
	    "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
	    "http://www.w3.org/2000/01/rdf-schema#label",
	    "http://www.w3.org/2000/01/rdf-schema#comment",
	    "http://www.w3.org/ns/prov#wasDerivedFrom", 
	    "http://xmlns.com/foaf/0.1/isPrimaryTopicOf", 
	    "http://xmlns.com/foaf/0.1/depiction", 
	    "http://xmlns.com/foaf/0.1/homepage", 
	    "http://purl.org/dc/terms/subject",
	    "http://xmlns.com/foaf/0.1/givenName",
	    "http://xmlns.com/foaf/0.1/name",
	    "http://xmlns.com/foaf/0.1/surname"
	    );
	
	private static final BlackList instance = new GeneralPropertyBlackList();
	
	private GeneralPropertyBlackList(){}
	
	public static BlackList getInstance(){
		return instance;
	}
	
	public boolean contains(Resource resource){
		return blacklist.contains(resource.getURI());
	}
	
	public boolean contains(String uri){
		return blacklist.contains(uri);
	}
}
