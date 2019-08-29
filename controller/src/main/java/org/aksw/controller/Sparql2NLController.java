package org.aksw.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.aksw.sparql2nl.naturallanguagegeneration.FilterExpressionConverter;
import org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLG;
import org.aksw.sparql2nl.naturallanguagegeneration.SimpleNLGwithPostprocessing;
import org.aksw.sparql2nl.naturallanguagegeneration.TriplePatternConverter;
import org.aksw.sparql2nl.smooth_nlg.CardBox;
import org.aksw.sparql2nl.smooth_nlg.NLConstructor;
import org.aksw.sparql2nl.smooth_nlg.SPARQLDeconstructor;
import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.dllearner.kb.sparql.SparqlEndpoint;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import java.io.File;
import java.net.URL;

public class Sparql2NLController {

    private DefaultIRIConverter uriConverter = new DefaultIRIConverter(SparqlEndpoint.getEndpointDBpedia());
    private FilterExpressionConverter conv = new FilterExpressionConverter(uriConverter);
    private static final SparqlEndpoint ENDPOINT = SparqlEndpoint.getEndpointDBpedia();
    private static final SimpleNLGwithPostprocessing nlg = new SimpleNLGwithPostprocessing(ENDPOINT);
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private Realiser realiser = new Realiser(lexicon);


    public String expressionConverter(Expr expr){
        NLGElement element = conv.convert(expr);
        String text = realiser.realise(element).getRealisation();
        return text;
    }

    // sparql to NL
    public String fetchNLR(Query sparqlQuery){
        return nlg.getNLR(sparqlQuery);
    }

    public DocumentElement convert2NLE(Query sparqlQuery){
        SimpleNLG snlg = new SimpleNLG(ENDPOINT);
        return snlg.convert2NLE(sparqlQuery);
    }

    public CardBox sparqlDeconstructor(Query sparqlQuery){
        SPARQLDeconstructor decon = new SPARQLDeconstructor(ENDPOINT);
        CardBox c = decon.deconstruct(sparqlQuery);
        return c;
    }

//    public String triplePatternConverter(){
//        TriplePatternConverter conv = new TriplePatternConverter(SparqlEndpoint.getEndpointDBpedia(), "cache", null);
//    }


    public static void main (String[] args) throws Exception {
        Sparql2NLController cont = new Sparql2NLController();

        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX mo: <http://purl.org/ontology/mo/> "
                + "SELECT DISTINCT ?artisttype "
                + "WHERE {"
                + "?artist foaf:name 'Liz Story'."
                + "?artist rdf:type ?artisttype ."
                + "FILTER (?artisttype != mo:MusicArtist)"
                + "}";

        // expression converter
        System.out.println("Converting expression..");
        Expr var = new ExprVar("s");
        NodeValue value = NodeValue.makeInteger(1);
        Expr expr = new E_Equals(var, value);
        System.out.println(cont.expressionConverter(expr));

        // fetchNLR
        System.out.println("Converting expression..");
        try {
            query = Joiner.on("\n").join(Files.readLines(new File("src/main/resources/sparql_query.txt"), Charsets.UTF_8));
            Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
            System.out.println(sparqlQuery);
            System.out.println("Simple NLG: Query is distinct = " + sparqlQuery.isDistinct());
            System.out.println("Simple NLG: " + cont.fetchNLR(sparqlQuery));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //convertDoc2NLE
        try {
            Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
            DocumentElement elt = cont.convert2NLE(sparqlQuery);
            System.out.println("Simple NLG: " + elt);
            System.out.println("Simple NLG: " + cont.fetchNLR(sparqlQuery));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //sparqlDeconstructor
        CardBox c = cont.sparqlDeconstructor(QueryFactory.create(query, Syntax.syntaxARQ));
        NLConstructor con = new NLConstructor(c);
        con.construct();
        System.out.println("\n----------------------------\n");



    }
}
