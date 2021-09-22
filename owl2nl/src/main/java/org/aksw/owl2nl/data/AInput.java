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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import simplenlg.lexicon.Lexicon;

public abstract class AInput implements IInput {

  protected Lexicon lexicon = Lexicon.getDefaultLexicon();
  protected OWLOntology owlOntology = null;

  @Deprecated
  protected OWLOntology loadOntology(final Path path) {
    try {
      return OWLManager//
          .createOWLOntologyManager()//
          .loadOntologyFromOntologyDocument(path.toFile());
      // .loadOntology(IRI.create(path.toFile()));
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  protected OWLOntology loadOntology(final IRI ontology) {
    try {
      return OWLManager//
          .createOWLOntologyManager()//
          .loadOntology(ontology);
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  @Override
  public IInput setOntology(final IRI ontology) {
    owlOntology = loadOntology(ontology);
    return this;
  }

  @Deprecated
  @Override
  public IInput setOntology(final Path ontology) {
    owlOntology = loadOntology(ontology);
    return this;
  }

  @Override
  public IInput setLexicon(final Lexicon lexicon) {
    this.lexicon = lexicon;
    return this;
  }

  @Override
  public Lexicon getLexicon() {
    return lexicon;
  }

  @Override
  public String getEnglishLabel(final IRI iri) {
    LOG.trace("Not implemented in ".concat(AInput.class.getName()).concat(" ."));
    return null;
  }
}
