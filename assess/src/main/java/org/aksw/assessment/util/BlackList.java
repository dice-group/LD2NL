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

/**
 * A blacklist of entities that are not allowed.
 * 
 * @author Lorenz Buehmann
 *
 */
public interface BlackList {

	/**
	 * Checks whether the given URI is contained in the black list.
	 * @param uri the entity URI
	 * @return <code>TRUE</code> if the entity is contained in the black list, i.e. not allowed, otherwise
	 *         <code>FALSE</code>
	 */
	boolean contains(String uri);

	/**
	 * Checks whether the given resource is contained in the black list.
	 * @param resource the resource
	 * @return <code>TRUE</code> if the resource is contained in the black list, i.e. not allowed, otherwise
	 *         <code>FALSE</code>
	 */
	boolean contains(Resource resource);

}
