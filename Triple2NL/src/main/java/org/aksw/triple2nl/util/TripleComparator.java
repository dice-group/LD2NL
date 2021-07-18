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
/**
 * 
 */
package org.aksw.triple2nl.util;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.util.NodeComparator;

/**
 * Comparator to sort a list of triples by subject, predicate, and object to
 * ensure a consistent order for human-readable output
 * 
 * @author Lorenz Buehmann
 *
 */
public class TripleComparator implements Comparator<Triple>{
	
	private final NodeComparator nodeComparator = new NodeComparator();

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Triple t1, Triple t2) {
		return ComparisonChain.start()
		.compare(t1.getSubject(), t2.getSubject(), nodeComparator)
		.compare(t1.getPredicate(), t2.getPredicate(), nodeComparator)
		.compare(t1.getObject(), t2.getObject(), nodeComparator)
		.result();
	}

}
