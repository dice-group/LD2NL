/*-
 * #%L
 * OWL2NL
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
package org.aksw.owl2nl.data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.aksw.owl2nl.pipeline.data.input.RAKIInput;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class OWL2NLRAKIInputTest {

  @Test
  public void test() throws OWLOntologyCreationException, OWLOntologyStorageException {
    IRI iri = IRI.create(OWL2NLRAKIInputTest.class.getClassLoader().getResource("test_axioms.owl"));
    final IInput input = new RAKIInput()//
        .setAxioms(iri)//
        .setOntology(iri);

    Assert.assertNotNull(input);
    Assert.assertEquals(5, ((RAKIInput) input).getAxioms().size());
  }

  @Test
  public void testIRI() throws OWLOntologyCreationException, OWLOntologyStorageException,
      MalformedURLException, URISyntaxException {

    String str =
        "https://raw.githubusercontent.com/dice-group/LD2NL/master/owl2nl/src/test/resources/test_axioms.owl";
    URL url = new URI(str).toURL();

    IRI iri = IRI.create(url);
    final IInput input = new RAKIInput()//
        .setAxioms(iri)//
        .setOntology(iri);

    Assert.assertNotNull(input);
    Assert.assertEquals(5, input.getAxioms().size());
  }

}
