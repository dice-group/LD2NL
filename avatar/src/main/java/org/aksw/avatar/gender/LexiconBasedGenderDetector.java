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
/**
 *
 */
package org.aksw.avatar.gender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Lorenz Buehmann
 *
 */
public class LexiconBasedGenderDetector implements GenderDetector {
	
	private String maleNamesPath = "gender/male.txt";
	private String femaleNamesPath = "gender/female.txt";

    private Set<String> male;
    private Set<String> female;

    public LexiconBasedGenderDetector(Set<String> male, Set<String> female) {
        this.male = male;
        this.female = female;
    }

    /*
     * (non-Javadoc) @see
     * org.aksw.sparql2nl.entitysummarizer.gender.GenderDetector#getGender(java.lang.String)
     */
    @Override
    public Gender getGender(String name) {
    	String searchName = name;
    	//check if name is compound
    	String[] words = name.split(" ");
    	if(words.length > 1){
    		searchName = words[0];
    	}
    	
        if (male.contains(searchName)) {
            return Gender.MALE;
        } else if (female.contains(name)) {
            return Gender.FEMALE;
        } else {
            return Gender.UNKNOWN;
        }
    }

    public LexiconBasedGenderDetector() {
        try {
            male = new HashSet<String>();
            female = new HashSet<String>();
            
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(maleNamesPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String l;
            while((l = br.readLine()) != null){
            	l = l.trim();
                if (!l.startsWith("#") && !l.isEmpty()) {
                    male.add(l);
                }
            }
            br.close();
            
            is = this.getClass().getClassLoader().getResourceAsStream(femaleNamesPath);
            br = new BufferedReader(new InputStreamReader(is));
            while((l = br.readLine()) != null){
            	l = l.trim();
                if (!l.startsWith("#") && !l.isEmpty()) {
                	female.add(l);
                }
            }
            br.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * @param maleNamesPath the maleNamesPath to set
	 */
	public void setMaleNamesPath(String maleNamesPath) {
		this.maleNamesPath = maleNamesPath;
	}
	
	/**
	 * @param femaleNamesPath the femaleNamesPath to set
	 */
	public void setFemaleNamesPath(String femaleNamesPath) {
		this.femaleNamesPath = femaleNamesPath;
	}

    public static void main(String[] args) throws Exception {
        LexiconBasedGenderDetector genderDetector = new LexiconBasedGenderDetector();
        System.out.println(genderDetector.getGender("Zinedine Ngonga"));
    }
}
