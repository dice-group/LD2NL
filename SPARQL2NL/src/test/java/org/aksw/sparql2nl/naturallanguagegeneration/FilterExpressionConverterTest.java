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
package org.aksw.sparql2nl.naturallanguagegeneration;

import java.util.Calendar;

import org.aksw.triple2nl.converter.DefaultIRIConverter;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.junit.Assert;
import org.junit.Test;

import simplenlg.framework.NLGElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_GreaterThanOrEqual;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.E_LessThanOrEqual;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;

/**
 * @author Lorenz Buehmann
 * 
 */
public class FilterExpressionConverterTest {

	private DefaultIRIConverter uriConverter = new DefaultIRIConverter(SparqlEndpoint.getEndpointDBpedia());
	private FilterExpressionConverter conv = new FilterExpressionConverter(uriConverter);
	private Realiser realiser = new Realiser(Lexicon.getDefaultLexicon());

	/**
	 * Test method for
	 * {@link org.aksw.sparql2nl.naturallanguagegeneration.FilterExpressionConverter#convert(com.hp.hpl.jena.sparql.expr.Expr)}
	 * .
	 */
	@Test
	public void testConvert() {
		Expr var = new ExprVar("s");

		/*
		 * integer literals
		 */

		NodeValue value = NodeValue.makeInteger(1);

		// ?s = value
		Expr expr = new E_Equals(var, value);
		NLGElement element = conv.convert(expr);
		String text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is equal to 1", text);

		// ?s > value
		expr = new E_GreaterThan(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is greater than 1", text);

		// ?s >= value
		expr = new E_GreaterThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is greater than or equal to 1", text);

		// ?s < value
		expr = new E_LessThan(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is less than 1", text);

		// ?s <= value
		expr = new E_LessThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is less than or equal to 1", text);

		/*
		 * date literals
		 */
		Calendar cal = Calendar.getInstance();
		cal.set(1999, 11, 21);
		value = NodeValue.makeDate(cal);
		String valueString = "December 20, 1999";

		// ?s = value
		expr = new E_Equals(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is on " + valueString, text);

		// ?s > value
		expr = new E_GreaterThan(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is after " + valueString, text);

		// ?s >= value
		expr = new E_GreaterThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is after or on " + valueString, text);

		// ?s < value
		expr = new E_LessThan(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is before " + valueString, text);

		// ?s <= value
		expr = new E_LessThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is before or on " + valueString, text);

		/*
		 * date period
		 */
		value = NodeValue.parse("\"1999-12\"^^xsd:gYearMonth");
		valueString = "December 1999";

		// ?s = value
		expr = new E_Equals(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is in " + valueString, text);

		// ?s >= value
		expr = new E_GreaterThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is after or in " + valueString, text);

		// ?s <= value
		expr = new E_LessThanOrEqual(var, value);
		element = conv.convert(expr);
		text = realiser.realise(element).getRealisation();
		System.out.println(expr + " --> " + text);
		Assert.assertEquals("Conversion failed.", "?s is before or in " + valueString, text);
	}

}
