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
package org.aksw.sparql2nl.smooth_nlg;

import org.apache.jena.sparql.syntax.Element;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author christina
 */
public class CardBox {
    
    List<Entity> primaries;
    List<Entity> secondaries;
    List<Element> filters;
    List<Element> optionals;
    List<OrderBy> orderBys;
    
    public CardBox(List<Entity> primes,List<Entity> secs,List<Element> fils,List<Element> opts,List<OrderBy> obs) {
        primaries = primes;
        secondaries = secs;
        filters = fils;
        optionals = opts;
        orderBys = obs;
    }
    
    public String[][] getMatrix(Entity e) {
        
        String[][] m = new String[e.properties.size()][3];
        int row = 0;
        for (Predicate p : e.properties) {
            m[row][0] = p.subject;
            m[row][1] = p.predicate;
            m[row][2] = p.object;
            row++;
        }       
        return m;
    }
    
    public Set<String> getSecondaryVars() {
        
        Set<String> out = new HashSet<>();
        for (Entity e : secondaries) {
            out.add(e.var);
        }
        return out;
    }
    
    public void print() {
        
        System.out.println("\nPRIMARY ENTITIES:\n");
        for (Entity entity : primaries) {
            System.out.println(entity.toString());
        }
        System.out.println("\nSECONDARY ENTITIES:\n");
        for (Entity entity : secondaries) {
            System.out.println(entity.toString());
        }
        System.out.println("...");
    }
    
}
