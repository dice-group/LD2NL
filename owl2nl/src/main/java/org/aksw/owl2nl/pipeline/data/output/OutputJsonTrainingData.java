package org.aksw.owl2nl.pipeline.data.output;

import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.owl2nl.pipeline.io.RakiIO;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 *
 * @author Rene Speck
 *
 */
public class OutputJsonTrainingData implements IOutput<JSONArray> {

  private final OWLObjectRenderer manchesterR = new ManchesterOWLSyntaxOWLObjectRendererImplExt();
  private final OWLObjectRenderer dlR = new DLSyntaxObjectRenderer();

  protected JSONArray data = null;
  protected Path file = null;

  /**
   *
   */
  public OutputJsonTrainingData() {

  }

  /**
   *
   * @param file
   */
  public OutputJsonTrainingData(final Path file) {
    this.file = file;
    LOG.debug("Wrties output in {}", file.toFile().getAbsoluteFile());
  }

  @Override
  public JSONArray getResults() {
    return data;
  }

  @Override
  public JSONArray write(final Map<OWLAxiom, String> verb) {
    data = new JSONArray();

    int id = 0;
    for (final Entry<OWLAxiom, String> entry : verb.entrySet()) {

      final OWLAxiom axiom = entry.getKey();
      final String nl = entry.getValue();

      final JSONObject o = new JSONObject();
      final JSONArray axioms = new JSONArray();

      axioms.put(manchesterR.render(axiom));
      for (final OWLClassExpression e : axiom.getNestedClassExpressions()) {
        axioms.put(manchesterR.render(e));
      }
      o.put("manchester", axioms);
      o.put("owl", axiom.toString());
      o.put("nl", nl == null ? "" : nl);
      o.put("id", id++);
      o.put("dl", dlR.render(axiom));
      data.put(o);
    }

    if (file != null) {
      RakiIO.write(file, data.toString(2).getBytes());
    }
    return data;
  }
}
