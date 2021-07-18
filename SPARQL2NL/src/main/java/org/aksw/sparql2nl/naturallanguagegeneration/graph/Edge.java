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
package org.aksw.sparql2nl.naturallanguagegeneration.graph;

import org.jgrapht.graph.DefaultEdge;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

/**
 * @author Lorenz Buehmann
 *
 */
public class Edge extends DefaultEdge {

	private Triple triple;
	private boolean reverted = false;;

	public Edge(Triple triple) {
		this(triple, false);
	}

	public Edge(Triple triple, boolean reverted) {
		this.triple = triple;
		this.reverted = reverted;
	}

	/**
	 * @return the triple
	 */
	public Triple asTriple() {
		return triple;
	}

	/**
	 * @return the predicateNode
	 */
	public Node getPredicateNode() {
		return triple.getPredicate();
	}
	
	/**
	 * @return the reverted
	 */
	public boolean isReverted() {
		return reverted;
	}

}
