/*-
 * #%L
 * AVATAR
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
package org.aksw.avatar;

import java.util.List;

import com.google.common.base.Joiner;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;

/**
 * @author Lorenz Buehmann
 *
 */
public class EntitySummarization {
	
	private Resource entity;
	private List<Triple> triples; 
	
	public EntitySummarization(Resource entity, List<Triple> triples) {
		this.entity = entity;
		this.triples = triples;
	}
	
	/**
	 * @return the summarized entity
	 */
	public Resource getEntity() {
		return entity;
	}
	
	/**
	 * @return the triples of the summarization
	 */
	public List<Triple> getTriples() {
		return triples;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((triples == null) ? 0 : triples.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntitySummarization other = (EntitySummarization) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (triples == null) {
			if (other.triples != null)
				return false;
		} else if (!triples.equals(other.triples))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Entity:" + entity + "Triples:\n" + Joiner.on("\n").join(triples);
	}

}
