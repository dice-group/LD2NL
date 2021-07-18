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
package org.aksw.triple2nl.nlp.relation;

import java.util.HashMap;
import java.util.Map;

/**
 * Only used inside this class to encapsulate the Solr query results.
 */
public class Pattern {
    
    public Map<String,Double> features = new HashMap<>();
    public String naturalLanguageRepresentationWithoutVariables = "";
    public String naturalLanguageRepresentation = "";
    public Double boaScore = 0D;
    public Double naturalLanguageScore = 0D;
    public String posTags = "";
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		return "Pattern [features=" + features + ", naturalLanguageRepresentation=" + naturalLanguageRepresentation
				+ ", boaScore=" + boaScore + ", naturalLanguageScore=" + naturalLanguageScore + ", POS=" + posTags
				+ "]";
	}
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((naturalLanguageRepresentation == null) ? 0 : naturalLanguageRepresentation.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pattern other = (Pattern) obj;
        if (naturalLanguageRepresentation == null) {
            if (other.naturalLanguageRepresentation != null)
                return false;
        }
        else
            if (!naturalLanguageRepresentation.equals(other.naturalLanguageRepresentation))
                return false;
        return true;
    }
}
