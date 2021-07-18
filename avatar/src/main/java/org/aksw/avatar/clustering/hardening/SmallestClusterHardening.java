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
package org.aksw.avatar.clustering.hardening;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.avatar.clustering.Node;
import org.aksw.avatar.clustering.WeightedGraph;

/**
 * Hardening that prefers clusters with smaller weights
 * @author ngonga
 */
public class SmallestClusterHardening extends LargestClusterHardening{
     public List<Set<Node>> harden(Set<Set<Node>> clusters, WeightedGraph wg) {
        Set<Node> nodes = new HashSet<Node>(wg.getNodes().keySet());
        double min, weight;
        Set<Node> bestCluster;
        List<Set<Node>> result = new ArrayList<Set<Node>>();
        while (!nodes.isEmpty()) {
            min = Double.MAX_VALUE;
            bestCluster = null;
            //first get weights            
            for (Set<Node> c : clusters) {
                if (!result.contains(c)) {
                    weight = getWeight(c, wg, nodes);
                    if (weight < min) {
                        min = weight;
                        bestCluster = c;
                    }
                }
            }
            // no more clusters available
            if (bestCluster == null) {
                return result;
            }
            //in all other cases       
            clusters.remove(bestCluster);
            bestCluster.retainAll(nodes);
            result.add(bestCluster);
            nodes.removeAll(bestCluster);
        }
        result = Lists.reverse(result);
        return result;
    }
}
