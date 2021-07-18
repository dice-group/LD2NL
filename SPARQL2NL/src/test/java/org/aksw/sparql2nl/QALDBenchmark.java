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
/**
 * 
 */
package org.aksw.sparql2nl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;

/**
 * @author Lorenz Buehmann
 *
 */
public class QALDBenchmark {
	
	private static final Map<Integer, Query> queries = new HashMap<Integer, Query>();
	
	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(QALDBenchmark.class.getClassLoader().getResourceAsStream("qald-4_multilingual_test_withanswers.xml"));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			XPathExpression expr = xpath.compile("/dataset/question[@answertype='resource']");
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i= 0; i < nl.getLength(); i++){
				Node node = nl.item(i);
				Element el = (Element)node;
				int id = Integer.parseInt(el.getAttribute("id"));
				String queryString = el.getElementsByTagName("query").item(0).getTextContent().trim();
				if(!queryString.toUpperCase().equals("OUT OF SCOPE")){
					try {
						Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL_11);
						queries.put(id, query);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Query getQuery(int id){
		return queries.get(id);
	}
	
	public static Collection<Query> getQueries(){
		return queries.values();
	}
	
	public static Collection<Query> getQueries(int... ids){
		Collection<Query> queries = new ArrayList<Query>();
		for (int id : ids) {
			queries.add(getQuery(id));
		}
		return queries;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(QALDBenchmark.getQuery(1));
	}

}
