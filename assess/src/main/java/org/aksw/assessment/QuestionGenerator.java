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
package org.aksw.assessment;

import java.util.Map;
import java.util.Set;

import org.aksw.assessment.question.Question;

import org.apache.jena.graph.Triple;

/**
 * A generator for questions.
 * @author Axel Ngonga
 */
public interface QuestionGenerator {
    Set<Question> getQuestions(Map<Triple, Double> informativenessMap, int difficulty, int number);
}
