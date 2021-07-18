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
package org.aksw.triple2nl.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Class holds a set of prepositions.
 * @author Axel Ngonga
 */
public class Preposition extends HashSet<String> {

	private static final String filename = "preposition_list.txt";

	public Preposition(InputStream is) {
		try (BufferedReader bufRdr = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = bufRdr.readLine()) != null) {
				add(line.toLowerCase().trim());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Preposition() {
		this(Preposition.class.getClassLoader().getResourceAsStream(filename));
	}

	/**
	 * Determines whether the given token is contained in the list of prepositions.
	 * @param s the input token
	 * @return TRUE if the token is a preposition, otherwise FALSE
	 */
	public boolean isPreposition(String s) {
		return contains(s);
	}
}
