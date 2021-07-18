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
package org.aksw.triple2nl.gender;

import java.util.ArrayList;
import java.util.List;

/**
 * A delegating gender detector that goes through all given detectors until a gender was found.
 *
 * @author Lorenz Buehmann
 */
public class DelegateGenderDetector implements GenderDetector{

	private List<GenderDetector> detectors = new ArrayList<>();

	/**
	 * @param detectors a list of gender detectors which are used in the given order
	 */
	public DelegateGenderDetector(List<GenderDetector> detectors) {
		this.detectors = detectors;
	}

	@Override
	public Gender getGender(String name) {
		for (GenderDetector detector : detectors) {
			Gender gender = detector.getGender(name);
			if(gender != Gender.UNKNOWN) {
				return gender;
			}
		}
		return Gender.UNKNOWN;
	}
}
