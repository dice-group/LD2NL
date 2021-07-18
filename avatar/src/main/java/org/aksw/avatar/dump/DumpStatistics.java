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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.avatar.dump;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ngonga
 */
public class DumpStatistics {
    // 1. Get statistics on properties ergo Map<Node, Map<Set<Node>, Integer>>> 
    // maps each class to a map that contains property pairs and the frequency 
    // with which they occur
    
    public Set<Set<Node>> clusterProperties(Map<Set<Node>, Integer> properties)
    {
        return new HashSet<Set<Node>>();
        
//        Model m = ModelFactory.createDefaultModel();
//        Property m.createProperty(null, null);
    }
}
