package org.aksw.owl2nl.raki.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 *
 * @author Rene Speck
 *
 */
public class Statistic {
  protected static final Logger LOG = LogManager.getLogger(Statistic.class);
  Map<AxiomType<?>, Integer> counter = new HashMap<>();

  public void stat(final Set<OWLAxiom> axioms) {
    for (final OWLAxiom axiom : axioms) {
      final AxiomType<?> type = axiom.getAxiomType();
      counter.putIfAbsent(type, 0);
      counter.put(type, counter.get(type) + 1);
    }
  }

  @Override
  public String toString() {
    return "Statitics: " + counter.toString();
  }
}
