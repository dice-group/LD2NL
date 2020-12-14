package org.aksw.owl2nl.raki.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Rene Speck
 *
 */
public class RakiIO {
  protected static final Logger LOG = LogManager.getLogger(RakiIO.class);

  public static synchronized OWLOntology loadOntology(final String ontologyFile) {
    try {
      return OWLManager//
          .createOWLOntologyManager()//
          .loadOntology(IRI.create(Paths.get(ontologyFile).toFile()));
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  public static synchronized Model readRDFXML(final String file) {
    return ModelFactory.createDefaultModel()//
        .read(file, Lang.RDFXML.getName()//
        );
  }

  public static synchronized boolean write(final Path file, final byte[] bytes) {
    try {
      Files.write(file, bytes);
      return true;
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return false;
    }
  }
}
