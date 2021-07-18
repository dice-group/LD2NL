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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.assessment.question;

import java.util.List;

import org.aksw.assessment.answer.Answer;

import org.apache.jena.query.Query;

/**
 *
 * @author ngonga
 */
public class SimpleQuestion implements Question {
	String text;
	List<Answer> correctAnswers;
	List<Answer> wrongAnswers;
	int difficulty;
	Query query;
	QuestionType type;

	public SimpleQuestion(String text, List<Answer> correctAnswers, List<Answer> wrongAnswers, int difficulty, Query q,
			QuestionType type) {
		this.text = text;
		this.correctAnswers = correctAnswers;
		this.wrongAnswers = wrongAnswers;
		this.difficulty = difficulty;
		this.query = q;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public QuestionType getType() {
		return type;
	}

	public List<Answer> getCorrectAnswers() {
		return correctAnswers;
	}

	public List<Answer> getWrongAnswers() {
		return wrongAnswers;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public Query getQuery() {
		return query;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
