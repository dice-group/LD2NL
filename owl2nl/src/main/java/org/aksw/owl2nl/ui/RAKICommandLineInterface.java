package org.aksw.owl2nl.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.owl2nl.converter.OWLAxiomConverter;
import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.data.OWL2NLInput;
import org.aksw.owl2nl.exception.OWLAxiomConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import gnu.getopt.Getopt;

public class RAKICommandLineInterface {
  protected static final Logger LOG = LogManager.getLogger(RAKICommandLineInterface.class);

  /**
   *
   * @param args
   * @throws IllegalArgumentException
   */
  public static void main(final String[] args) throws IllegalArgumentException {
    LOG.info("\n==============================\nParsing arguments...");

    // "https://its-wiki.no/images/0/05/Travel.owl";
    // "http://www.ling.helsinki.fi/kit/2004k/ctl310semw/Protege/koala.owl";

    String ontologyParameter = null;
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

    LOG.info("\n==============================\nChecking arguments...");
    if (ontologyParameter == null) {
      LOG.warn("Missing parameter");
      throw new IllegalArgumentException("Missing parameter");
    }

    LOG.info("\n==============================\nPerparing inputs...");
    final IInput input = new OWL2NLInput().setOntologyIRI(IRI.create(ontologyParameter));

    // output
    final Map<OWLAxiom, String> verbalizations = new HashMap<>();
    {
      LOG.info("\n==============================\nStarting OWL axiom converter...");
      final OWLAxiomConverter converter = new OWLAxiomConverter(input);

      final Set<OWLAxiom> axioms = input.getAxioms();

      LOG.info("\n==============================\nStarting verbalizations...");
      for (final OWLAxiom axiom : axioms) {
        try {
          final String verbalization = converter.convert(axiom);
          if (verbalization != null) {
            verbalizations.put(axiom, verbalization);
          } else {
            LOG.trace("Could not verbalize axiom: " + axiom);
          }
        } catch (final OWLAxiomConversionException e) {
          verbalizations.put(axiom, "");
          LOG.error("Could not verbalize axiom: " + axiom);
        }
      }
    }
    LOG.info("\n==============================\nPerparing output...");
    verbalizations.entrySet().forEach(LOG::info);
  }
}
