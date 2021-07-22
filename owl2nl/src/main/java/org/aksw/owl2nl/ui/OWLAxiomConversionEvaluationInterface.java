package org.aksw.owl2nl.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.aksw.owl2nl.evaluation.OWLAxiomConversionEvaluation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import gnu.getopt.Getopt;

public class OWLAxiomConversionEvaluationInterface {
  protected static final Logger LOG =
      LogManager.getLogger(OWLAxiomConversionEvaluationInterface.class);

  static {
    ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  public static void main(final String[] args)
      throws IllegalArgumentException, OWLOntologyCreationException, IOException {

    LOG.info("==============================Parsing arguments...");
    String inPath = null;
    String outPath = null;
    boolean isURL = false;
    {
      final Getopt g = new Getopt("OWL Axiom Evaluation", args, "i:x o:x u:x");
      int c;
      while ((c = g.getopt()) != -1) {
        switch (c) {
          case 'i':
            inPath = String.valueOf(g.getOptarg());
            break;
          case 'o':
            outPath = String.valueOf(g.getOptarg());
            break;
          case 'u':
            isURL = Boolean.valueOf(g.getOptarg());
            break;
          default:
            LOG.info("getopt() returned " + c + "");
        }
      }
    }

    LOG.info("==============================Checking arguments...");
    IRI iri = null;
    {
      if (inPath == null || inPath.trim().isEmpty() || //
          outPath == null || outPath.trim().isEmpty()) {
        throw new IllegalArgumentException("Missing parameter");
      } else {
        try {
          IRI.create(Paths.get(outPath).toUri().toURL());
          if (!isURL) {
            iri = null;
            iri = IRI.create(Paths.get(inPath).toUri().toURL());
          } else {
            iri = IRI.create(new URL(inPath));
          }
        } catch (final MalformedURLException e) {
          throw new IllegalArgumentException("Wrong parameter with malformed URL. ");
        }
      }
    }
    OWLAxiomConversionEvaluation.evaluation(iri, Paths.get(outPath));
  }
}
