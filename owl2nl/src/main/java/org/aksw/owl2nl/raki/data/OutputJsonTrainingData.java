package org.aksw.owl2nl.raki.data;

import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.owl2nl.raki.io.RakiIO;
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
public class OutputJsonTrainingData extends AOutput {

  private final OWLObjectRenderer manchesterRenderer =
      new ManchesterOWLSyntaxOWLObjectRendererImplExt();
  private final OWLObjectRenderer dlRenderer = new DLSyntaxObjectRenderer();

  private Path file = null;

  public OutputJsonTrainingData(final Path file) {
    this.file = file;
  }

  @Override
  public boolean write(final Map<OWLAxiom, SimpleEntry<String, String>> verb) {
    final JSONArray ja = new JSONArray();

    int id = 0;
    for (final Entry<OWLAxiom, SimpleEntry<String, String>> entry : verb.entrySet()) {

      final OWLAxiom axiom = entry.getKey();
      // final String dl = entry.getValue().getKey();
      final String nl = entry.getValue().getValue();

      final JSONObject o = new JSONObject();
      final JSONArray axioms = new JSONArray();
      axioms.put(manchesterRenderer.render(axiom));
      for (final OWLClassExpression e : axiom.getNestedClassExpressions()) {
        axioms.put(manchesterRenderer.render(e));
      }
      o.put("manchester", axioms);
      o.put("owl", axiom.toString());
      // o.put("dl", dl == null ? "" : dl);
      o.put("nl", nl == null ? "" : nl);
      o.put("id", id++);
      o.put("dl", dlRenderer.render(axiom));

      ja.put(o);
    }

    return RakiIO.write(file, ja.toString(2).getBytes());
  }
}
