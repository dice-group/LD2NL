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
package org.aksw.sparql2nl.naturallanguagegeneration;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import java.util.ArrayList;
import java.util.List;

import org.dllearner.kb.sparql.SparqlEndpoint;
import simplenlg.framework.DocumentElement;

import simplenlg.framework.NLGElement;

/**
 *
 * @author ngonga
 */
public class GenericNLGTest {

    public static void main(String args[]) {
        String query = "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX res: <http://dbpedia.org/resource/> "
                + "SELECT DISTINCT ?uri ?x "
                + "WHERE { "
                + "{res:Abraham_Lincoln dbo:deathPlace ?uri} "
                + "UNION {res:Abraham_Lincoln dbo:birthPlace ?uri} . "
                + "?uri rdf:type dbo:Place. "
                + "FILTER regex(?uri, \"France\").  "
                + "OPTIONAL { ?uri dbo:description ?x }. "
                + "}";

        try {
            GenericNLG nlg = new GenericNLG();
            SimpleNLG snlg = new SimpleNLG(SparqlEndpoint.getEndpointDBpediaLiveAKSW());
            Query sparqlQuery = QueryFactory.create(query);
            DocumentElement de = snlg.convert2NLE(sparqlQuery);
            for (NLGElement nlge : de.getComponents()) {
                System.out.println("<-------------------\n" + nlge.toString() + "\n--------------------->");
            }
            System.out.println("Simple NLG: "+snlg.getNLR(sparqlQuery));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /** Fetches all elements of the query body, i.e., of the WHERE clause of a 
     * query
     * @param query Input query
     * @return List of elements from the WHERE clause
     */
    private static List<Element> getWhereElements(Query query) {
        List<Element> result = new ArrayList<>();
        ElementGroup elt = (ElementGroup) query.getQueryPattern();
        for(int i=0; i<elt.getElements().size(); i++)
        {
            if(!elt.getElements().get(i).getClass().equals(ElementOptional.class))
                result.add(elt.getElements().get(i));
        }
        return result;
    }

    /** Fetches all elements of the optional, i.e., of the OPTIONAL clause. 
     * query
     * @param query Input query
     * @return List of elements from the OPTIONAL clause if there is one, else null
     */
    private static List<Element> getOptionalElements(Query query) {
        ElementGroup elt = (ElementGroup) query.getQueryPattern();
        for(int i=0; i<elt.getElements().size(); i++)
        {
            if(elt.getElements().get(i).getClass().equals(ElementOptional.class))
                return ((ElementGroup)((ElementOptional)elt.getElements().get(i)).getOptionalElement()).getElements();
        }
        return null;
    }
    
    private static List<Element> getElements(Query query) {
        List<Element> result = new ArrayList<>();
        ElementGroup elt = (ElementGroup) query.getQueryPattern();
        return elt.getElements();
    }
}

