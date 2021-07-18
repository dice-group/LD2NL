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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Joiner;

/**
 * @author Lorenz Buehmann
 * 
 */
@XmlRootElement(name = "questions")
public class RESTQuestions {

	// @XmlElement(name = "question", type = RESTQuestions.class)
	private List<RESTQuestion> questions;

	public List<RESTQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<RESTQuestion> questions) {
		this.questions = questions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Joiner.on("\n##################\n").join(questions);
	}

}
