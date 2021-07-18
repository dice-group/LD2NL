/*-
 * #%L
 * SPARQL2NL
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
package org.aksw.sparql2nl.naturallanguagegeneration;

import java.util.Hashtable;
import java.util.Set;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

public class Union {
    
    Set<Set<Sentence>> sentences;
    boolean optional;
    
    public Union(Set<Set<Sentence>> s,boolean o) {
        sentences = s;
        optional = o;
        if (optional) {
            for (Set<Sentence> un : sentences) {
                for (Sentence sent : un) {
                     sent.optional = true;
                }
            }
        }
    }
    
    public Sentence removeRedundancy(Realiser realiser) {
        
        Hashtable<String,Sentence> unionsents = new Hashtable<>();
            
        for (Set<Sentence> un : sentences) {          
             for (Sentence s : un) {
                  if (!unionsents.containsKey(realiser.realise(s.sps).toString().toLowerCase())) 
                       unionsents.put(realiser.realise(s.sps).toString().toLowerCase(),s);
             }
        }
        if (unionsents.size() == 1) { 
            for (String key : unionsents.keySet()) {
                 return unionsents.get(key);
            }
        }
        return null;
    }
    
}
