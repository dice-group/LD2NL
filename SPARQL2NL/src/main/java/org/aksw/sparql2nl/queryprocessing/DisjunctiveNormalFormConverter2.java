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

import java.util.Iterator;
import java.util.Stack;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;

public class DisjunctiveNormalFormConverter2 extends ElementVisitorBase {
	
	private boolean inUNION = false;
	private boolean innerUnion = false;
	private boolean outerUnion = true;
	
	private Stack<ElementUnion> unionStack = new Stack<>();
	
	public org.apache.jena.query.Query getDisjunctiveNormalForm(org.apache.jena.query.Query query){
		Query copy = QueryFactory.create(query);
		copy.getQueryPattern().visit(this);
		
		return copy;
	}
	
	@Override
	public void visit(ElementUnion el) {
		unionStack.push(el);
		for(Element e : el.getElements()){
			e.visit(this);
		}
	}
	
	@Override
	public void visit(ElementPathBlock el) {
		
	}
	
	@Override
	public void visit(ElementOptional el) {
		// TODO Auto-generated method stub
		super.visit(el);
	}
	
	@Override
	public void visit(ElementGroup el) {
		Element e;
		ElementUnion removed = null;
		ElementUnion top = null;
		for(Iterator<Element> it = el.getElements().iterator(); it.hasNext();){
			e = it.next();
			e.visit(this);
			if(e instanceof ElementUnion){
				top = unionStack.pop();
				if(!unionStack.isEmpty()){
					it.remove();
					removed = (ElementUnion) e;
				}
			}
		}
		if(removed != null){
			for(Element subEl : removed.getElements()){
				ElementGroup g = new ElementGroup();
				for(Element subEl2 : el.getElements()){
					g.addElement(subEl2);
				}
				g.addElement(subEl);
				el.addElement(g);
			}
		}
	}
	
	public static void main(String[] args) {
		String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
				"PREFIX dbp: <http://dbpedia.org/property/> " +
				"PREFIX res: <http://dbpedia.org/resource/> " +
				"PREFIX yago: <http://dbpedia.org/class/yago/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"SELECT DISTINCT ?uri ?string WHERE {" +
				"        { ?uri rdf:type yago:ArgentineFilms . }        " +
				"UNION	{ ?uri rdf:type dbo:Film .	" +
				"{ ?uri dbo:country res:Argentina . } " +
				"UNION " +
				"{ ?uri dbp:country 'Argentina'@en . } }	" +
				"OPTIONAL { ?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }" +
				"}";
		
//		queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> " +
//				"PREFIX dbp: <http://dbpedia.org/property/> " +
//				"PREFIX res: <http://dbpedia.org/resource/> " +
//				"PREFIX yago: <http://dbpedia.org/class/yago/> " +
//				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
//				"SELECT DISTINCT ?uri ?string WHERE {" +
//				"?uri rdf:type dbo:Automobile .         " +
//				" { ?uri dbp:production res:Germany . } " +
//						"UNION " +
//				"{ ?uri dbp:assembly res:Germany . }        " +
//						"UNION { ?uri dbp:manufacturer ?x .       " +
//								" { ?x dbo:locationCountry res:Germany . } " +
//									"UNION " +
//								"{ ?x rdf:type yago:AutomotiveCompaniesOfGermany . } " +
//								"}        " +
//				"OPTIONAL { ?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }}";
		System.out.println(QueryFactory.create(queryString, Syntax.syntaxARQ));
		Query q = QueryFactory.create(queryString, Syntax.syntaxARQ);
		q = new DisjunctiveNormalFormConverter2().getDisjunctiveNormalForm(q);
		System.out.println(q.toString());
	}
	

}
