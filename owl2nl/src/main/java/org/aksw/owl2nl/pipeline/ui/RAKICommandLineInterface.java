package org.aksw.owl2nl.pipeline.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import org.aksw.owl2nl.pipeline.Pipeline;
import org.aksw.owl2nl.pipeline.data.input.RAKIInput;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.aksw.owl2nl.pipeline.data.output.OutputHTMLTable;
import org.aksw.owl2nl.pipeline.data.output.OutputJsonTrainingData;
import org.aksw.owl2nl.pipeline.data.output.OutputTerminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import gnu.getopt.Getopt;

public class RAKICommandLineInterface {
  protected static final Logger LOG = LogManager.getLogger(RAKICommandLineInterface.class);
  static {
    // ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
  }

  public static void main(final String[] args)
      throws OWLOntologyCreationException, OWLOntologyStorageException {
    LOG.info("\n==============================\nParsing arguments ...");
    String axioms = null;
    String output = null;
    String ontology = null;
    String type = null;
    boolean isURL = false;
    String outputType = null;
    final Getopt g = new Getopt("Verbalizer Pipeline", args, "a:x o:x s:x m:x u:x t:x");
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'a':
          axioms = String.valueOf(g.getOptarg());
          break;
        case 'o':
          ontology = String.valueOf(g.getOptarg());
          break;
        case 's':
          output = String.valueOf(g.getOptarg());
          break;
        case 'm':
          type = String.valueOf(g.getOptarg());
          break;
        case 'u':
          isURL = Boolean.valueOf(g.getOptarg());
          break;
        case 't':
          outputType = String.valueOf(g.getOptarg());
          break;
        default:
          LOG.info("getopt() returned " + c + "\n");
      }
    }

    LOG.info("==============================Checking arguments...");
    Object oOntology = null;
    {
      if (axioms == null || axioms.trim().isEmpty() || //
          output == null || output.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "Missing parameter, set at least axioms file and output path");
      } else {
        try {
          IRI.create(Paths.get(output).toUri().toURL());
          if (isURL) {
            oOntology = IRI.create(new URL(ontology));
          } else {
            oOntology = ontology;
            // oOntology = IRI.create(Paths.get(ontology).toUri().toURL());
          }
        } catch (final MalformedURLException e) {
          throw new IllegalArgumentException("Wrong parameter with malformed URL. ");
        }
      }
    }

    LOG.info("\n==============================\nRunning Pipeline ...");

    final RAKIInput in = new RAKIInput();
    in//
        .setType(type.startsWith("m") ? RAKIInput.Type.MODEL : RAKIInput.Type.RULES) //
        .setAxioms(Paths.get(axioms));
    if (oOntology instanceof IRI) {
      in.setOntology((IRI) oOntology);
    } else if (oOntology instanceof String) {
      // IRI iri = IRI.create(Paths.get(((String) oOntology)).toAbsolutePath().toUri());
      in.setOntology(Paths.get(((String) oOntology)));
    }

    // sets the output type
    IOutput<?> out = null;
    if (outputType.startsWith("txt")) {
      out = new OutputTerminal(Paths.get(output));
    } else if (outputType.startsWith("html")) {
      out = new OutputHTMLTable(Paths.get(output));
    } else {
      // default json
      out = new OutputJsonTrainingData(Paths.get(output));
    }

    // run
    Pipeline.getInstance().setInput(in).setOutput(out).run();

    LOG.info("\n==============================\nPipeline exit.");
  }
}
