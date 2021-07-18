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
package org.aksw.avatar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.aksw.avatar.dump.LogEntry;
import org.aksw.sparql2nl.queryprocessing.TriplePatternExtractor;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.dllearner.reasoning.SPARQLReasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import com.google.common.base.Joiner;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.vocabulary.RDF;

public class SPARQLQueryProcessor {

    private TriplePatternExtractor patternExtractor = new TriplePatternExtractor();
    private SPARQLReasoner reasoner;

    public SPARQLQueryProcessor(SparqlEndpointKS ks) {
        reasoner = new SPARQLReasoner(ks);
    }

    public Map<OWLClass, Set<OWLProperty>> processQuery(String query) {
        return processQuery(QueryFactory.create(query, Syntax.syntaxARQ));
    }

    public Collection<Map<OWLClass, Set<OWLProperty>>> processQueries(Collection<Query> queries) {
        Collection<Map<OWLClass, Set<OWLProperty>>> result = new ArrayList<Map<OWLClass, Set<OWLProperty>>>();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<OWLClass, Set<OWLProperty>>>> futures = new ArrayList<Future<Map<OWLClass, Set<OWLProperty>>>>();
        for (final Query query : queries) {
            futures.add(threadPool.submit(new Callable<Map<OWLClass, Set<OWLProperty>>>() {

                @Override
                public Map<OWLClass, Set<OWLProperty>> call() throws Exception {
                    return processQuery(query);
                }
            }));
        }
        for (Future<Map<OWLClass, Set<OWLProperty>>> future : futures) {
            try {
                result.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();

        return result;
    }
    
    /**
     * Each log entry is processed by getting classes with its used properties.
     * @param entries
     * @return
     */
    public Collection<Map<OWLClass, Set<OWLProperty>>> processEntries(Collection<LogEntry> entries) {
    	List<Query> queries = new ArrayList<Query>();
//    	Set<String> blacklist = Sets.newHashSet("-", "bliss", "ARC" , "[CURL]");
    	for (LogEntry entry : entries) {
    		queries.add(entry.getSparqlQuery());
//    		if(!blacklist.contains(entry.userAgent)){
//    			
//    		}
		}
        return processQueries(queries);
    }

    /**
     * We want to get classes with frequent occurring predicates in the
     * knowledge base.
     *
     * @param query
     */
    public Map<OWLClass, Set<OWLProperty>> processQuery(Query query) {
        TriplePatternExtractor patternExtractor = new TriplePatternExtractor();
        Map<OWLClass, Set<OWLProperty>> result = new HashMap<OWLClass, Set<OWLProperty>>();
        //get all projection variables in the query
        List<Var> vars = query.getProjectVars();

        //we have to filter out variables which are the subject of a rdf:type statement
        for (Iterator<Var> iterator = vars.iterator(); iterator.hasNext();) {
            Var var = iterator.next();
            //get all outgoing triple patterns
            Set<Triple> outgoingTriplePatterns = patternExtractor.extractOutgoingTriplePatterns(query, var);
            for (Triple triple : outgoingTriplePatterns) {
                //check for rdf:type triples
                if (triple.subjectMatches(var) && triple.predicateMatches(RDF.type.asNode())) {
                    iterator.remove();
                    break;
                }
            }
        }

        //now we want to get all types
        for (Var var : vars) {
            //get all ingoing triple patterns
            Set<Triple> ingoingTriplePatterns = patternExtractor.extractIngoingTriplePatterns(query, var);

            //get the types for the subject of the triple pattern
            for (Triple triple : ingoingTriplePatterns) {
                //get the subject
                Node subject = triple.getSubject();
                //get the predicate
                Node predicate = triple.getPredicate();
                //*BUG HERE*. Sometimes the predicates are variables
                if (!predicate.isVariable()) {
                    OWLProperty property = new OWLObjectPropertyImpl(IRI.create(predicate.getURI()));

                    if (subject.isVariable()) {//if the subject s is a variable we can look for outgoing rdf:type triple patterns of s in the query
                        Set<Triple> outgoingTriplePatterns = patternExtractor.extractOutgoingTriplePatterns(query, subject);
                        for (Triple tp : outgoingTriplePatterns) {
                            //check for rdf:type triples
                            if (tp.predicateMatches(RDF.type.asNode()) && tp.getObject().isURI()) {
                                OWLClass nc = new OWLClassImpl(IRI.create(tp.getObject().getURI()));
                                if (!result.containsKey(nc)) {
                                    result.put(nc, new HashSet<OWLProperty>());
                                }
                                result.get(nc).add(property);
                            }
                        }
                    } else if (subject.isURI()) {//if the subject is a URI we can ask the knowledge base for the types
                        Set<OWLClass> types = reasoner.getTypes(new OWLNamedIndividualImpl(IRI.create(subject.getURI())));
                        for (OWLClass nc : types) {
                            if (!result.containsKey(nc)) {
                                result.put(nc, new HashSet<OWLProperty>());
                            }
                            result.get(nc).add(property);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
        SPARQLQueryProcessor processor = new SPARQLQueryProcessor(new SparqlEndpointKS(endpoint));

        Query query = QueryFactory.create(
                "PREFIX dbr: <http://dbpedia.org/resource/> "
                + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "SELECT ?s ?place ?date WHERE {?s a dbo:Person. ?s dbo:birthPlace ?place. ?s dbo:birthDate ?date.}");
        Map<OWLClass, Set<OWLProperty>> occurrences = processor.processQuery(query);
        System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(occurrences));

        query = QueryFactory.create(
                "PREFIX dbr: <http://dbpedia.org/resource/> "
                + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "SELECT ?place ?date WHERE {?s a dbo:Person. ?s dbo:birthPlace ?place. ?s dbo:birthDate ?date.}");
        occurrences = processor.processQuery(query);
        System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(occurrences));

        query = QueryFactory.create(
                "PREFIX dbr: <http://dbpedia.org/resource/> "
                + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "SELECT ?place ?date WHERE {dbr:Brad_Pitt dbo:birthPlace ?place. dbr:Brad_Pitt dbo:birthDate ?date.}");
        occurrences = processor.processQuery(query);
        System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(occurrences));

        query = QueryFactory.create(
                "PREFIX dbr: <http://dbpedia.org/resource/> "
                + "PREFIX dbo: <http://dbpedia.org/ontology/> "
                + "SELECT ?s ?place ?date WHERE {?s a dbo:Book. ?o dbo:birthPlace ?place. ?o dbo:birthDate ?date.}");
        occurrences = processor.processQuery(query);
        System.out.println(Joiner.on("\n").withKeyValueSeparator("=").join(occurrences));
    }
}
