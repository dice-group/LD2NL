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

/**
 * @author Lorenz Buehmann
 *
 */
@XmlRootElement
public class RESTQuestion {

	private String question;
	private String questionType;
	private List<RESTAnswer> correctAnswers;
	private List<RESTAnswer> wrongAnswers;
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<RESTAnswer> getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(List<RESTAnswer> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public List<RESTAnswer> getWrongAnswers() {
		return wrongAnswers;
	}

	public void setWrongAnswers(List<RESTAnswer> wrongAnswers) {
		this.wrongAnswers = wrongAnswers;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Question: " + question + "\nCorrect answers: " + correctAnswers + "\nWrong answers: " + wrongAnswers;
	}
}
