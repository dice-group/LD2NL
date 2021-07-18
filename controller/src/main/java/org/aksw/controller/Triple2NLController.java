/*-
 * #%L
 * controller
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
package org.aksw.controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.apache.jena.graph.impl.LiteralLabel;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.aksw.triple2nl.TripleConverter;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.vocabulary.RDF;


public class Triple2NLController {
    private static final SparqlEndpoint ENDPOINT_DBPEDIA = SparqlEndpoint.getEndpointDBpedia();
    private static final SparqlEndpointKS KS = new SparqlEndpointKS(ENDPOINT_DBPEDIA);

    private static TripleConverter converter;

    public static void init() throws Exception {
        KS.init();
    }

    public String TriplesToText(List<Triple> triples){
        converter = new TripleConverter();
        return converter.convert(triples);
    }

    public String convertLiteral(LiteralLabel lit){
        LiteralConverter conv = new LiteralConverter(new DefaultIRIConverter(
                SparqlEndpoint.getEndpointDBpediaLiveAKSW()));
        return conv.convert(lit);
    }

    public static void main(String[] args) throws Exception {
        Triple2NLController tc = new Triple2NLController();

        // literal converter
        System.out.println("Converting literal..");
        LiteralLabel lit = NodeFactory.createLiteral("1869-06-27", null, XSDDatatype.XSDdate).getLiteral();
        System.out.println(lit + " --> " + tc.convertLiteral(lit));

        // triples to text
        System.out.println("Converting triple to text..");
        List<Triple> triples = new ArrayList<Triple>();
        Node subject = NodeFactory.createURI("http://dbpedia.org/resource/Albert_Einstein");
        triples.add(Triple.create(
                subject,
                RDF.type.asNode(),
                NodeFactory.createURI("http://dbpedia.org/ontology/Person")));
        triples.add(Triple.create(
                subject,
                NodeFactory.createURI("http://dbpedia.org/ontology/birthPlace"),
                NodeFactory.createURI("http://dbpedia.org/resource/Ulm")));
        triples.add(Triple.create(
                subject,
                NodeFactory.createURI("http://dbpedia.org/ontology/birthDate"),
                NodeFactory.createLiteral("1879-03-14", XSDDatatype.XSDdate)));

        String text = tc.TriplesToText(triples);
        System.out.println(triples + "\n-> " + text);
    }

}
