package org.aksw.owl2nl.raki.planner;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.owl2nl.raki.data.input.Input;
import org.aksw.owl2nl.raki.data.output.IOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * This class defines the template of a document and provides all information for the
 * SentencePlanner {@link sentencePlanner}. This class decides about the information and the order
 * of the information to be verbalized.
 *
 * @author Rene Speck
 *
 */
public class DocumentPlanner implements IPlanner<String> {

  protected static final Logger LOG = LogManager.getLogger(DocumentPlanner.class);

  protected SentencePlanner sentencePlanner = null;
  protected IOutput output = null;
  protected Input input = null;

  /**
   *
   */
  public DocumentPlanner(final Input input, final IOutput output) {
    this.output = output;
    this.input = input;

    sentencePlanner = new SentencePlanner(input);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#build()
   */
  @Override
  public IPlanner<String> build() {
    sentencePlanner.build();
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.aksw.owl2nl.raki.planner.IPlanner#results()
   */
  @Override
  public String results() {
    final Map<OWLAxiom, SimpleEntry<String, String>> map = new HashMap<>();

    for (final Entry<OWLAxiom, String> entry : sentencePlanner.results().entrySet()) {
      final SimpleEntry<String, String> se = new SimpleEntry<>(//
          input.getAxiomsMap().get(entry.getKey()), //
          entry.getValue()//
      );
      map.put(entry.getKey(), se);
    }

    // write verbalized axioms to file success
    final Object success = output.write(map);
    if (success == null) {
      LOG.error("Couldn't write results.");
    }

    // TODO:
    /*
     * final StringBuilder sb = new StringBuilder(); for (final Entry<OWLAxiom, String> result :
     * results.entrySet()) { sb.append(result.getValue()).append("\t"); // sb.append("\n");
     * sb.append(result.getKey().toString()); sb.append("\n"); }
     */

    return "";
  }
}
