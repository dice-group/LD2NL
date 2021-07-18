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
/**
 *
 */
package org.aksw.triple2nl.gender;

/**
 * Detects the gender based on two lists of common names for male and female.
 *
 * @author Lorenz Buehmann
 */
public class DictionaryBasedGenderDetector implements GenderDetector {

	private final GenderDictionary dictionary;

	public DictionaryBasedGenderDetector() {
		this(new GeneralGenderDictionary());
	}

	public DictionaryBasedGenderDetector(GenderDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public static void main(String[] args) throws Exception {
		DictionaryBasedGenderDetector genderDetector = new DictionaryBasedGenderDetector();
		System.out.println(genderDetector.getGender("Tarsila do Amaral"));
	}

	/*
	 * (non-Javadoc) @see
	 * org.aksw.sparql2nl.entitysummarizer.gender.GenderDetector#getGender(java.lang.String)
	 */
	@Override
	public Gender getGender(String name) {
		String searchName = name;
		// check if name is compound
		String[] words = name.split(" ");
		if (words.length > 1) {
			searchName = words[0];
		}

		if (dictionary.isMale(searchName)) {
			return Gender.MALE;
		} else if (dictionary.isFemale(searchName)) {
			return Gender.FEMALE;
		} else {
			return Gender.UNKNOWN;
		}
	}
}
