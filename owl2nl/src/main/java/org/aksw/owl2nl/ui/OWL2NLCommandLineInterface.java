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
package org.aksw.owl2nl.ui;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.aksw.owl2nl.converter.OWLAxiomConverter;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import gnu.getopt.Getopt;

public class OWL2NLCommandLineInterface {
  protected static final Logger LOG = LogManager.getLogger(OWL2NLCommandLineInterface.class);

  public static void main(final String[] args) throws IllegalArgumentException {

    LOG.info("\n==============================\nParsing arguments...");
    String ontologyParameter = null;
    {
      final Getopt g = new Getopt("OWL Axiom Converter", args, "o:x");
      int c;
      while ((c = g.getopt()) != -1) {
        switch (c) {
          case 'o':
            ontologyParameter = String.valueOf(g.getOptarg());
            break;
          default:
            LOG.info("getopt() returned " + c + "\n");
        }
      }
    }

    LOG.info("\n==============================\nChecking arguments...");
    IRI iri = null;
    {
      if (ontologyParameter == null || ontologyParameter.trim().isEmpty()) {
        throw new IllegalArgumentException("Missing parameter");
      } else {
        try {
          iri = IRI.create(Paths.get(ontologyParameter).toUri().toURL());
        } catch (final Exception e) {
          throw new IllegalArgumentException(//
              "Wrong input parameter. Could not create IRI. ");
        }
      }
    }
    final Map<OWLAxiom, String> verbalizations = new HashMap<>();
    {
      final OWLAxiomConverter converter = new OWLAxiomConverter(new OWL2NLInput().setOntology(iri));
      if (converter.getInput().getAxioms() == null) {
        LOG.info("\n==============================\nNo input axioms ...");
      } else {
        LOG.info("\n==============================\nStarting verbalizations...");
        for (final OWLAxiom axiom : converter.getInput().getAxioms()) {
          final String verbalization = converter.convert(axiom);
          if (verbalization != null) {
            verbalizations.put(axiom, verbalization);
          } else {
            LOG.trace("Could not verbalize axiom: " + axiom);
          }
        }
      }
    }

    verbalizations.entrySet().forEach(LOG::info);
  }
}
