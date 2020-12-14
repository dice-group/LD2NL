package org.aksw.owl2nl.raki.data;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 *
 * @author Rene Speck
 *
 */
public interface IOutput {
  Logger LOG = LogManager.getLogger(IOutput.class);

  boolean write(final byte[] bytes);

  boolean write(final Object object);

  boolean write(Map<OWLAxiom, SimpleEntry<String, String>> verb);
}
