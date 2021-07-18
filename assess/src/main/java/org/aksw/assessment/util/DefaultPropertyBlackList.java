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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.jena.rdf.model.Resource;

/**
 * This class contains set of properties that are meaningless for the generation of questions
 * in the ASSESS project. 
 * @author Lorenz Buehmann
 *
 */
public class DefaultPropertyBlackList implements BlackList{

	private final Set<String> blacklist;
	
	public DefaultPropertyBlackList() {
		this(Collections.EMPTY_SET);
	}
	
	public DefaultPropertyBlackList(File file) throws IOException {
		this(Files.readLines(file, Charsets.UTF_8));
	}
	
	public DefaultPropertyBlackList(Collection<String> blacklist) {
		this.blacklist = Sets.newHashSet(blacklist);
	}
	
	public boolean contains(Resource resource){
		return contains(resource.getURI());
	}
	
	public boolean contains(String uri){
		return blacklist.contains(uri);
	}
}
