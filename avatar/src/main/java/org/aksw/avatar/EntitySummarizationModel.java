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

import java.util.Set;

import com.google.common.base.Joiner;

/**
 * This class represents a entity summarization model for a given knowledge base, i.e. it contains
 * summarization templates for each class in the knowledge base, if exists.
 * @author Lorenz Buehmann
 *
 */
public class EntitySummarizationModel {

	private Set<EntitySummarizationTemplate> templates;

	public EntitySummarizationModel(Set<EntitySummarizationTemplate> templates) {
		this.templates = templates;
	}
	
	/**
	 * @return the templates
	 */
	public Set<EntitySummarizationTemplate> getTemplates() {
		return templates;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Joiner.on("\n").join(templates);
	}

}
