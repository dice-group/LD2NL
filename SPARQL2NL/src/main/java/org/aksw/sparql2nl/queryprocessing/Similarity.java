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
package org.aksw.sparql2nl.queryprocessing;

import org.aksw.sparql2nl.similarity.NormedGraphIsomorphism;
import org.aksw.sparql2nl.similarity.TypeAwareGraphIsomorphism;

import simpack.measure.external.simmetrics.JaccardSimilarity;
import simpack.measure.external.simmetrics.Levenshtein;
import simpack.measure.external.simmetrics.QGramsDistance;

/**
 * Computes the similarity between two queries
 * @author ngonga
 */
public class Similarity {

	public enum SimilarityMeasure{
		LEVENSHTEIN, QGRAMS, JACCARD, GRAPH_ISOMORPHY, TYPE_AWARE_ISOMORPHY
	}

    public static double getSimilarity(Query q1, Query q2, SimilarityMeasure measure)
    {
        //check whether queries use the same features
        //if they don't return 0
        if(q1.getUsesCount() != q2.getUsesCount() ||
                q1.getUsesGroupBy() != q2.getUsesGroupBy() ||
                q1.getUsesLimit() != q2.getUsesLimit() ||
                q1.getUsesSelect() != q2.getUsesSelect()) return 0;
        //if they do then compute similarity
        if(measure == SimilarityMeasure.LEVENSHTEIN)
            return (new Levenshtein(q1.getQueryWithOnlyVars(), q2.getQueryWithOnlyVars()).getSimilarity());
        else if(measure == SimilarityMeasure.QGRAMS)
            return (new QGramsDistance(q1.getQueryWithOnlyVars(), q2.getQueryWithOnlyVars()).getSimilarity());
        else if(measure == SimilarityMeasure.JACCARD)
            return (new JaccardSimilarity(q1.getQueryWithOnlyVars(), q2.getQueryWithOnlyVars()).getSimilarity());
        //add graph based-metric here
        else if(measure == SimilarityMeasure.GRAPH_ISOMORPHY)
            return (new NormedGraphIsomorphism().getSimilarity(q1, q2));
        else if(measure == SimilarityMeasure.TYPE_AWARE_ISOMORPHY)
            return (new TypeAwareGraphIsomorphism().getSimilarity(q1, q2));
        //default
        return (new Levenshtein(q1.getQueryWithOnlyVars(), q2.getQueryWithOnlyVars()).getSimilarity());
    }
}
