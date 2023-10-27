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

import java.nio.file.Path;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import simplenlg.lexicon.Lexicon;

public interface IInput {

  Logger LOG = LogManager.getLogger(IInput.class);

  IInput setLexicon(Lexicon lexicon);

  IInput setOntology(IRI ontology) throws OWLOntologyCreationException, OWLOntologyStorageException;

  IInput setOntology(Path ontology) throws OWLOntologyCreationException;

  Set<OWLAxiom> getAxioms();

  Lexicon getLexicon();

  String getEnglishLabel(final IRI iri);
}
