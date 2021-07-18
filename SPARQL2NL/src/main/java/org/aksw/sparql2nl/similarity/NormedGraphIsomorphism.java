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
package org.aksw.sparql2nl.similarity;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.aksw.sparql2nl.queryprocessing.Query;
import simpack.accessor.graph.SimpleGraphAccessor;
import simpack.api.IGraphNode;
import simpack.measure.graph.GraphIsomorphism;
import simpack.measure.graph.SubgraphIsomorphism;

/**
 * Computes the similarity between SPARQL queries. If the queries are not isomorphic,
 * the similarity is 0.5*subgraphIsomorphy. If they are isomorphic but do not share 
 * the same set of selected variables, then their similarity is exactly 0.5. If the
 * set of vars is shared, then the similarity function also checks whether the direction
 * of the edges are the same. The similarity s between the sets S1 and S2 of edges is 
 * 2*|intersection (S1, S2)|/(|S1|+|S2|). The final similarity is then 0.5 + s.
 * @author ngonga
 */
public class NormedGraphIsomorphism implements QuerySimilarity {

    /** Computes size of small graph isomorphism and norms it with
     * size of graphs
     * @param q1 First query
     * @param q2 Second query
     * @return Similarity
     */
//    @Override
    public double getSimilarity(Query q1, Query q2) {
        SimpleGraphAccessor g1 = q1.getGraphRepresentation();
        SimpleGraphAccessor g2 = q2.getGraphRepresentation();
        GraphIsomorphism gi = new GraphIsomorphism(g1, g2);
        gi.calculate();
        if (gi.getGraphIsomorphism() == 1) {
            return 0.5 + 0.5 * directionalSimilarity(q1, q2, gi.getCliqueList());
        } 
//        return gi.getSimilarity();

        SubgraphIsomorphism si = new SubgraphIsomorphism(g1, g2);
        Double sim = si.getSimilarity();
        if (sim != null) {
            return 0.5 * sim;
        } else {
            return 0;
        }
    }

    private double directionalSimilarity(Query q1, Query q2, TreeSet<String> cliqueList) {
        SimpleGraphAccessor g1 = q1.getGraphRepresentation();
        SimpleGraphAccessor g2 = q2.getGraphRepresentation();

        HashMap<String, String> nodeMapping = new HashMap<>();
        for (String s : cliqueList) {
            String[] split1 = s.split(Pattern.quote(", "));
            for (int i = 0; i < split1.length; i++) {
                String entry = split1[i];
                String[] split = entry.split(Pattern.quote(":"));
                if (split[0].equals("rdf")) {
                    nodeMapping.put(split[0] + ":" + split[1], split[2]);
                } else if (split[1].equals("rdf")) {
                    nodeMapping.put(split[0], split[1] + ":" + split[2]);
                } else {
                    nodeMapping.put(split[0], split[1]);
                }
            }
        }
        //check whether the same vars are used
        TreeSet<String> vars1 = q1.getSelectedVars();
        TreeSet<String> vars2 = q2.getSelectedVars();
        if (vars1.size() != vars2.size()) {
            return 0;
        }
        if (vars1 != null && vars2 != null) {
            for (String var : vars1) {
                if (!vars2.contains(nodeMapping.get(var))) {
                    return 0;
                }
            }
        }
        //get successor map at string level
        HashMap<String, TreeSet<String>> successors1 = new HashMap<>();
        double edgeCount1 = 0;
        for (IGraphNode n : g1.getNodeSet()) {
            TreeSet<IGraphNode> succ = n.getSuccessorSet();
            TreeSet<String> labels = new TreeSet<>();
            for (IGraphNode ns : succ) {
                labels.add(ns.getLabel());
                edgeCount1++;
            }
            successors1.put(n.getLabel(), labels);
        }

        HashMap<String, TreeSet<String>> successors2 = new HashMap<>();
        double edgeCount2 = 0;
        for (IGraphNode n : g2.getNodeSet()) {
            TreeSet<IGraphNode> succ = n.getSuccessorSet();
            TreeSet<String> labels = new TreeSet<>();
            for (IGraphNode ns : succ) {
                labels.add(ns.getLabel());
                edgeCount2++;
            }
            successors2.put(n.getLabel(), labels);
        }

        //now compare common edges
        double count = 0;
        try {
            for (String node1 : nodeMapping.keySet()) {
                String node2 = nodeMapping.get(node1);
                TreeSet<String> succ2 = successors2.get(node2);
                for (String succ1 : successors1.get(node1)) {
                    if (succ2.contains(nodeMapping.get(succ1))) {
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing");
            e.printStackTrace();
            return 0;
        }

        return 2 * count / (edgeCount1 + edgeCount2);
    }
}
