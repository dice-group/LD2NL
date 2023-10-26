package org.aksw.owl2nl.pipeline.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
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
    try {
      return ModelFactory.createDefaultModel().read(
          new ByteArrayInputStream(Files.readAllBytes(Paths.get(file))), Lang.RDFXML.getName());
    } catch (final IOException e) {
      LOG.error("Could not read: " + file);
      LOG.error(e.getLocalizedMessage(), e);
      return null;
    }
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
