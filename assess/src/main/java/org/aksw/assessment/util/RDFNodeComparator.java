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

import java.util.Comparator;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.util.NodeUtils;

/**
 * A comparator for RDFNode objects.
 * @author Lorenz Buehmann
 *
 */
public class RDFNodeComparator implements Comparator<RDFNode> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(RDFNode o1, RDFNode o2) {
		return NodeUtils.compareRDFTerms(o1.asNode(), o2.asNode());
	}

}
