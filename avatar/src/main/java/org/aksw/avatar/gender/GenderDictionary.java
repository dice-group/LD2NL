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
package org.aksw.avatar.gender;

import java.util.HashSet;
import java.util.Set;

/**
 * A dictionary that comprises two sets of names for male and female gender.
 *
 * @author Lorenz Buehmann
 */
public class GenderDictionary {

	protected Set<String> male = new HashSet<>();
	protected Set<String> female = new HashSet<>();

	private boolean caseSensitive = true;

	protected GenderDictionary(){}

	/**
	 * @param male   the set of male gender names
	 * @param female the set of female gender names
	 */
	public GenderDictionary(Set<String> male, Set<String> female) {
		this.male = male;
		this.female = female;
	}

	/**
	 * Checks whether the name is contained in the list of male gender names.
	 *
	 * @param name the name
	 * @return whether the name is contained in the list of male gender names
	 */
	public boolean isMale(String name) {
		return male.contains(caseSensitive ? name : name.toLowerCase());
	}

	/**
	 * Checks whether the name is contained in the list of female gender names.
	 *
	 * @param name the name
	 * @return whether the name is contained in the list of female gender names
	 */
	public boolean isFemale(String name) {
		return female.contains(caseSensitive ? name : name.toLowerCase());
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
}
