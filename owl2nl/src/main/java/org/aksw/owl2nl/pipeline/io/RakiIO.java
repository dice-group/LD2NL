package org.aksw.owl2nl.pipeline.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rspeck
 *
 */
public class RakiIO {
  protected static final Logger LOG = LogManager.getLogger(RakiIO.class);

  public static synchronized Model readRDFXML(final String file) {
    Model model = null;
    boolean isRDF = false;
    try {
      LOG.debug(file);
      model = ModelFactory.createDefaultModel().read(file);
      isRDF = model.getNsPrefixURI("rdf") != null;

    } catch (final Exception e) {
      LOG.error("Could not read: " + file);
      LOG.error(e.getLocalizedMessage(), e);

    }
    if (!isRDF) {
      throw new IllegalArgumentException(
          "\n\n-> !! The given input seem to be not in RDF/XML format!!\n\n");
    }
    return model;
  }

  public static synchronized boolean write(final Path file, final byte[] bytes) {
    try {
      Files.write(file, bytes);
      return true;
    } catch (final IOException e) {
      LOG.error("Could not write: " + file);
      LOG.error(e.getLocalizedMessage(), e);
      return false;
    }
  }
}
