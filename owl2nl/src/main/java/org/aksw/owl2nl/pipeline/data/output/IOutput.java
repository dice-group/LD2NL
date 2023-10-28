package org.aksw.owl2nl.pipeline.data.output;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Handles the given results.
 *
 * @author rspeck
 *
 * @param <T>
 */
public interface IOutput<T> {
  Logger LOG = LogManager.getLogger(IOutput.class);


  T write(final Map<OWLAxiom, String> verbalizationResults);

  T getResults();
}
