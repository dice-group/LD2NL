/*-
 * #%L
 * Evaluation
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
package org.aksw.semweb2nl.evaluation;

import java.util.Random;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class DataValueGenerator {

	private static final OWLDataFactory df = new OWLDataFactoryImpl();
	private static final Random rnd = new Random(123);
	private static final int rangeMax = -100;
	private static final int rangeMin = 100;

	public static OWLLiteral generateValue(OWLDataRange dataRange) {
		if (dataRange.isDatatype()) {
			OWLDatatype datatype = dataRange.asOWLDatatype();

			if (datatype.isInteger()) {
				return df.getOWLLiteral(rnd.nextInt());
			} else if (datatype.isDouble()) {
				return df.getOWLLiteral(rangeMin + (rangeMax - rangeMin) * rnd.nextDouble());
			} else if (datatype.isBoolean()) {
				return df.getOWLLiteral(rnd.nextBoolean());
			}
		}

		throw new UnsupportedOperationException("Data range " + dataRange + " not supported yet.");
	}
}
