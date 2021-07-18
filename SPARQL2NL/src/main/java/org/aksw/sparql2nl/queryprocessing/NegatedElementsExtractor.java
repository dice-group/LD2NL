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
package org.aksw.sparql2nl.queryprocessing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Bound;
import org.apache.jena.sparql.expr.E_LogicalNot;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.util.VarUtils;

public class NegatedElementsExtractor extends ElementVisitorBase{
	
	ElementGroup currentGroup;
	
	Map<ElementGroup, Var> elementGroup2Var = new HashMap<>();
	
	public void getNegatedElements(Query query){
		query.getQueryPattern().visit(this);
		
		TriplePatternExtractor tpExtractor = new TriplePatternExtractor();
		
		for(Entry<ElementGroup, Var> entry : elementGroup2Var.entrySet()){
			Var var = entry.getValue();
			Set<Triple> triples = tpExtractor.extractTriplePattern(entry.getKey());
			for(Triple t : triples){
				if(VarUtils.getVars(t).contains(var)){
					System.out.println(t);
				}
			}
		}
		
	}
	
	@Override
	public void visit(ElementGroup el) {
		currentGroup = el;
		for (Iterator<Element> iterator = el.getElements().iterator(); iterator.hasNext();) {
			Element e = iterator.next();
			e.visit(this);
		}
			
	}

	@Override
	public void visit(ElementTriplesBlock el) {
		for (Iterator<Triple> iter = el.patternElts(); iter.hasNext();) {
			Triple t = iter.next();
			
		}
	}

	@Override
	public void visit(ElementPathBlock el) {
		for (Iterator<TriplePath> iter = el.patternElts(); iter.hasNext();) {
			TriplePath tp = iter.next();
			
		}
	}
	
	@Override
	public void visit(ElementUnion el) {
		for (Iterator<Element> iterator = el.getElements().iterator(); iterator.hasNext();) {
			Element e = iterator.next();
			e.visit(this);
			
		}
	}
	
	@Override
	public void visit(ElementOptional el) {
		el.getOptionalElement().visit(this);
	}
	
	@Override
	public void visit(ElementFilter el) {
		Expr expr = el.getExpr();
		if(expr instanceof E_LogicalNot){
			Expr not = ((E_LogicalNot) expr).getArg(1);
			if( not instanceof E_Bound){
				elementGroup2Var.put(currentGroup, ((E_Bound) not).getArg().asVar());
			}
		}
		
	}
	
	public static void main(String[] args) {
		String querya = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX  res:  <http://dbpedia.org/resource/> "
                + "PREFIX  dbo:  <http://dbpedia.org/ontology/> "
                + "PREFIX  dbp:  <http://dbpedia.org/property/> "
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " "
                + "ASK "
                + "WHERE "
                + "  {   { res:Batman_Begins dbo:starring res:Christian_Bale. FILTER(!BOUND(?b)) } "
                + "    UNION "
                + "      { res:Batman_Begins dbp:starring ?b. res:Batman_Begins dbo:starring ?c. FILTER(!BOUND(?b)) } "
                + "  }";
		
		querya = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX  res:  <http://dbpedia.org/resource/> "
                + "PREFIX  dbo:  <http://dbpedia.org/ontology/> "
                + "PREFIX  dbp:  <http://dbpedia.org/property/> "
                + "PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + " "
                + "ASK "
                + "WHERE "
                + "  {  res:Batman_Begins dbo:starring res:Christian_Bale. FILTER(!BOUND(?b)) } "
                + "  }";
		
		Query q = QueryFactory.create(querya, Syntax.syntaxARQ);
		new NegatedElementsExtractor().getNegatedElements(q);
	}
	
	

}
