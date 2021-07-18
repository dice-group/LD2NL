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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.avatar.clustering.BorderFlowX;
import org.aksw.avatar.clustering.Node;
import org.aksw.avatar.clustering.WeightedGraph;
import org.aksw.avatar.clustering.hardening.Hardening;

/**
 * Hardening that prefers clusters with larger weight
 * @author ngonga
 */
public class LargestClusterHardening implements Hardening{

    public List<Set<Node>> harden(Set<Set<Node>> clusters, WeightedGraph wg) {
        Set<Node> nodes = new HashSet<Node>(wg.getNodes().keySet());
        double max, weight;
        Set<Node> bestCluster;
        List<Set<Node>> result = new ArrayList<Set<Node>>();
        while (!nodes.isEmpty()) {
            max = 0d;
            bestCluster = null;
            //first get weights            
            for (Set<Node> c : clusters) {
                if (!result.contains(c)) {
                    weight = getWeight(c, wg, nodes);
                    System.out.println(c +" -> "+weight);
                    if (weight > max) {
                        max = weight;
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
        return result;
    }

    /**
     * Computes the weight of a cluster w.r.t. to a given set of nodes within a
     * weighted graph
     *
     * @param cluster A cluster
     * @param wg A node- and edge-weighted graph
     * @param reference
     * @return Weight of the set of nodes
     */
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
        return w;
    }

    public static void main(String args[]) {
        WeightedGraph wg = new WeightedGraph();
        Node n1 = wg.addNode("a", 2.0);
        Node n2 = wg.addNode("b", 2.0);
        Node n3 = wg.addNode("c", 2.0);
        Node n4 = wg.addNode("d", 4.0);
        wg.addEdge(n1, n2, 1.0);
        wg.addEdge(n2, n3, 1.0);
        wg.addEdge(n2, n4, 1.0);

        BorderFlowX bf = new BorderFlowX(wg);
        Set<Set<Node>> clusters = bf.cluster();
//        System.out.println(clusters +"=>"+(new LargestClusterHardening()).harden(clusters, wg));
        System.out.println(clusters +"=>"+(new AverageWeightClusterHardening()).harden(clusters, wg));
    }
}
