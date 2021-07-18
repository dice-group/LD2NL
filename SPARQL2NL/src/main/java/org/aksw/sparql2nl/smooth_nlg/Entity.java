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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.sparql2nl.smooth_nlg;

import java.util.Set;

/**
 *
 * @author christina
 */
public class Entity {
    
    String var;
    boolean count;
    String type;
    Set<Predicate> properties;
    
    public Entity(String v,boolean c,String t,Set<Predicate> ps) {
        var = v;
        count = c;
        type = t;
        properties = ps;
    }
    
    public String toString() {
        String out = "";
        if (count) { out += " number of "; } 
        out += var + " (" + type + ") : {";
        for (Predicate p : properties) {
            out += p.toString() + ", ";
        }
        out = out.substring(0,out.length()-2);
        out += "}";
        return out;
    }
}
