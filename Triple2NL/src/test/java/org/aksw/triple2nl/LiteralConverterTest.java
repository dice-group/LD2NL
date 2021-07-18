/*-
 * #%L
 * Triple2NL
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
package org.aksw.triple2nl;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.aksw.triple2nl.converter.LiteralConverter;
import org.apache.jena.riot.Lang;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


/**
 * @author Lorenz Buehmann
 *
 */
public class LiteralConverterTest {

	private static LiteralConverter conv;

	@BeforeClass
	public static void init() {
		String kb = "<http://dbpedia.org/datatypes/squareKilometre> <http://www.w3.org/2000/01/rdf-schema#label> \"square kilometre\"@en .";
		Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(kb), null , Lang.TURTLE.getName());

		conv = new LiteralConverter(new DefaultIRIConverter(model));
	}

	/**
	 * Test method for {@link org.aksw.triple2nl.converter.sparql2nl.naturallanguagegeneration.LiteralConverter#convert(com.hp.hpl.jena.rdf.model.Literal)}.
	 */
	@Test
	public void testConvertDate() {

        LiteralLabel lit = NodeFactory.createLiteral("1869-06-27", null, XSDDatatype.XSDdate).getLiteral();
		String convert = conv.convert(lit);
		System.out.println(lit + " --> " + convert);
		assertEquals("June 27, 1869", convert); // would be with 27 June 1869 in UK

//        lit = NodeFactory.createLiteral("1914-01-01T00:00:00+02:00", null, XSDDatatype.XSDgYear).getLiteral();
//        System.out.println(lit + " --> " + conv.convert(lit));

        lit = NodeFactory.createLiteral("--04", null, XSDDatatype.XSDgMonth).getLiteral();
        System.out.println(lit + " --> " + conv.convert(lit));

//        lit = NodeFactory.createLiteral("1989-01-01+02:00", null, XSDDatatype.XSDgYear).getLiteral();
//        System.out.println(lit + " --> " + conv.convert(lit));


	}

	@Test
	public void testConvertUseDefinedDatatype() throws Exception {
		LiteralLabel lit = NodeFactory.createLiteral("123", null, new BaseDatatype("http://dbpedia.org/datatypes/squareKilometre")).getLiteral();
		System.out.println(lit + " --> " + conv.convert(lit));
	}

}
