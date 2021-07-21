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
package org.aksw.owl2nl.evaluation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.owl2nl.converter.OWLAxiomConverter;
import org.aksw.owl2nl.evaluation.visual.HTMLTableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Lorenz Buehmann created on 11/4/15
 */
public class OWLAxiomConversionEvaluation {
  private static final Logger LOG = LogManager.getLogger(OWLAxiomConversionEvaluation.class);

  public static void evaluation(final InputStream is, final Path out)
      throws OWLOntologyCreationException, IOException {

    final OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImplExt();
    final OWLOntology ontology = OWLManager.createOWLOntologyManager()//
        .loadOntologyFromOntologyDocument(is);
    final OWLAxiomConverter converter = new OWLAxiomConverter();

    final List<List<String>> data = new ArrayList<>();
    int i = 1;
    for (final OWLAxiom axiom : ontology.getAxioms()) {
      final String s = converter.convert(axiom);
      String renderedAxiom = renderer.render(axiom);
      if (s != null) {
        for (final ManchesterOWLSyntax keyword : ManchesterOWLSyntax.values()) {
          if (keyword.isAxiomKeyword() || keyword.isClassExpressionConnectiveKeyword()
              || keyword.isClassExpressionQuantiferKeyword()) {
            final String regex;
            regex = "\\s?(" + keyword.keyword() + "|" + keyword.toString() + ")(\\s|:)";
            renderedAxiom = renderedAxiom.replaceAll(regex, " <b>" + keyword.keyword() + "</b> ");
          }
        }
        data.add(Arrays.asList(String.valueOf(i++), renderedAxiom, s));
      } else {
        LOG.warn("Could not convert {}", renderedAxiom);
      }
    }

    Files.write(//
        out, //
        HTMLTableGenerator//
            .generateHTMLTable(Arrays.asList("ID", "Axiom", "NL"), data).getBytes()//
    );
  }
}
