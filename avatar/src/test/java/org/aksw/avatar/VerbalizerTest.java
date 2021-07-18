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
/**
 * 
 */
package org.aksw.avatar;

import org.dllearner.kb.sparql.SparqlEndpoint;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * @author Lorenz Buehmann
 *
 */
public class VerbalizerTest {
	//set up the SPARQL endpoint, in our case it's DBpedia
	private static final SparqlEndpoint endpoint = SparqlEndpoint.getEndpointDBpedia();
	
	//create the verbalizer used to generate the textual summarization
	private static final Verbalizer verbalizer = new Verbalizer(endpoint, "cache", null);

	/**
	 * Test method for {@link org.aksw.avatar.Verbalizer#summarize(org.dllearner.core.owl.Individual)}.
	 */
	@Test
	public void testSummarizeIndividual() {
		//define the entity to summarize
		OWLIndividual ind = new OWLNamedIndividualImpl(IRI.create("http://dbpedia.org/resource/Albert_Einstein"));

		//compute summarization of the entity and verbalize it
		String summary = verbalizer.summarize(ind);
		System.out.println(summary);
	}

	/**
	 * Test method for {@link org.aksw.avatar.Verbalizer#summarize(org.dllearner.core.owl.Individual, org.dllearner.core.owl.NamedClass)}.
	 */
	@Test
	public void testSummarizeIndividualNamedClass() {
		//define the class of the entity
		OWLClass cls = new OWLClassImpl(IRI.create("http://dbpedia.org/ontology/Scientist"));

		//define the entity to summarize
		OWLIndividual ind = new OWLNamedIndividualImpl(IRI.create("http://dbpedia.org/resource/Albert_Einstein"));

		//compute summarization of the entity and verbalize it
		String summary = verbalizer.summarize(ind, cls);
		System.out.println(summary);
	}

}
