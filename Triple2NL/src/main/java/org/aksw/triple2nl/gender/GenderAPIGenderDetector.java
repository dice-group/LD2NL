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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Returns the gender of a name by using Gender API at https://gender-api.com/
 *
 * @author Lorenz Buehmann
 */
public class GenderAPIGenderDetector implements GenderDetector {

	private static final String API_URL = "https://gender-api.com/get?name=";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aksw.avatar.gender.GenderDetector#getGender(java.lang.String)
	 */
	@Override
	public Gender getGender(String name) {
		try {
			URL url = new URL(API_URL + name);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Error: " + conn.getResponseCode());
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				Gson gson = new Gson();
				//JSON structure:{"name":"bob","gender":"male","samples":15549,"accuracy":99,"duration":"39ms"}
				JsonObject json = gson.fromJson(reader, JsonObject.class);
				System.out.println(json);
				//get the gender value
				String gender = json.get("gender").getAsString();
				//parse one of the possible values male, female, unknown
				return Gender.valueOf(gender.toUpperCase());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Gender.UNKNOWN;
	}
}
