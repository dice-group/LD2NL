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
package org.aksw.triple2nl.nlp;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.Annotator;

/**
 * A wrapper which allows to use either an in-memory or a server-based pipeline.
 *
 * @author Lorenz Buehmann
 */
public class StanfordCoreNLPWrapper extends AnnotationPipeline {

	private final AnnotationPipeline delegate;

	public StanfordCoreNLPWrapper(AnnotationPipeline delegate) {
		this.delegate = delegate;
	}

	@Override
	public void annotate(Annotation annotation) {
		delegate.annotate(annotation);
	}

	@Override
	public void addAnnotator(Annotator annotator) {
		delegate.addAnnotator(annotator);
	}
}
