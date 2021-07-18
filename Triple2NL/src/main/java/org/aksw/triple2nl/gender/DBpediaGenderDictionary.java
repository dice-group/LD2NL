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
package org.aksw.triple2nl.gender;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 * A gender dictionary for DBpedia based on a dataset provided at
 * http://wiki.dbpedia.org/services-resources/documentation/datasets#genders .
 * Example data:
 *
 * <http://dbpedia.org/resource/Algol> <http://xmlns.com/foaf/0.1/gender> "male"@en .
 * <http://dbpedia.org/resource/Abraham> <http://xmlns.com/foaf/0.1/gender> "male"@en .
 *
 * @author Lorenz Buehmann
 */
public class DBpediaGenderDictionary extends GenderDictionary{

	public static String GENDER_FILE_LOCATION = "gender/dbpedia/genders_en.ttl";

	private static final String GENDER_PROPERTY = "http://xmlns.com/foaf/0.1/gender";
	private static final String VALUE_MALE = "male";
	private static final String VALUE_FEMALE = "female";

	public DBpediaGenderDictionary() {
		Model model = ModelFactory.createDefaultModel();

		Literal maleLit = model.createLiteral(VALUE_MALE, "en");
		Literal femaleLit = model.createLiteral(VALUE_FEMALE, "en");

		RDFDataMgr.read(model, getClass().getClassLoader().getResourceAsStream(GENDER_FILE_LOCATION), Lang.TURTLE);
		StmtIterator iter = model.listStatements(null, model.createProperty(GENDER_PROPERTY), (RDFNode) null);
		while(iter.hasNext()) {
			Statement st = iter.next();
			Literal lit = st.getObject().asLiteral();
			if(lit.equals(maleLit)) {
				male.add(st.getSubject().getURI());
			} else if(lit.equals(femaleLit)){
				female.add(st.getSubject().getURI());
			}
		}
	}
}
