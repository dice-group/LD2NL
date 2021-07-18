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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

/**
 * @author Lorenz Buehmann
 */
public class GeneralGenderDictionary extends GenderDictionary {

	public static String MALE_GENDER_FILE_LOCATION = "gender/male.txt";
	public static String FEMALE_GENDER_FILE_LOCATION = "gender/female.txt";

	public GeneralGenderDictionary() {
		try {
			ClassPathResource maleResource = new ClassPathResource(MALE_GENDER_FILE_LOCATION);
			ClassPathResource femaleResource = new ClassPathResource(FEMALE_GENDER_FILE_LOCATION);

			male = new BufferedReader(new InputStreamReader(
					maleResource.getInputStream(), StandardCharsets.UTF_8))
					.lines().map(name -> name.toLowerCase()).collect(Collectors.toSet());

			female = new BufferedReader(new InputStreamReader(
					femaleResource.getInputStream(), StandardCharsets.UTF_8))
					.lines().map(name -> name.toLowerCase()).collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}


		setCaseSensitive(false);
	}
}
