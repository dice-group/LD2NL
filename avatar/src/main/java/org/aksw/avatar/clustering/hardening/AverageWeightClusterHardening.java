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

import java.util.Set;

import org.aksw.avatar.clustering.Node;
import org.aksw.avatar.clustering.WeightedGraph;

/**
 * Harderning that prefers clusters with higher average weight
 * @author ngonga
 */
public class AverageWeightClusterHardening extends LargestClusterHardening {
    
    /**
     * Computes the weight of a cluster w.r.t. to a given set of nodes within a
     * weighted graph
     *
     * @param cluster A cluster
     * @param wg A node- and edge-weighted graph
     * @param reference
     * @return Weight of the set of nodes
     */
    @Override
    public double getWeight(Set<Node> cluster, WeightedGraph wg, Set<Node> reference) {
        double w = 0d;

        for (Node n : cluster) {
            if (reference.contains(n)) {
                for (Node n2 : cluster) {
                    if (reference.contains(n2)) {
                        if (n.equals(n2)) {
                            w = w + wg.getNodeWeight(n);
                        } else {
                            w = w + wg.getEdgeWeight(n, n2);
                        }
                    }
                }
            }
        }
        return w/(cluster.size()*cluster.size());        
    }

}
