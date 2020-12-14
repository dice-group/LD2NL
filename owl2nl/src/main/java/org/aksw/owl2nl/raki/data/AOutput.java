package org.aksw.owl2nl.raki.data;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 *
 * @author Rene Speck
 *
 */
public abstract class AOutput implements IOutput {

  @Override
  public boolean write(final byte[] bytes) {
    return false;
  }

  @Override
  public boolean write(final Object object) {
    return false;
  }

  @Override
  public boolean write(final Map<OWLAxiom, SimpleEntry<String, String>> verb) {
    return false;
  }
}
