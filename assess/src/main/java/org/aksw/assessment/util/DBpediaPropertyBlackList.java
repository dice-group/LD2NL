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

import org.apache.jena.rdf.model.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains set of DBpedia properties that are meaningless for the generation of questions
 * in the ASSESS project. The list of properties is contained in the file
 * <code>src/main/resources/property_blacklist_dbpedia.txt</code>.
 * @author Lorenz Buehmann
 *
 */
public class DBpediaPropertyBlackList implements BlackList {

	private static final String FILE_NAME = "property_blacklist_dbpedia.txt";

	private final Set<String> blacklist;
	
	public static boolean onlyOntologyNamespace = true;

	public DBpediaPropertyBlackList() throws IOException {
		Stream<String> lines = Files.lines(Paths.get(getClass().getClassLoader().getResource(FILE_NAME).getPath()));
		blacklist = lines.collect(Collectors.toSet());
	}

	@Override
	public boolean contains(Resource resource){
		return contains(resource.getURI());
	}

	@Override
	public boolean contains(String uri) {
		return onlyOntologyNamespace && !uri.startsWith("http://dbpedia.org/ontology/") || blacklist.contains(uri);
	}
}
