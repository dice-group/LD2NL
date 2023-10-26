package org.aksw.owl2nl.pipeline;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class PipelineHelper {
  protected static final Logger LOG = LogManager.getLogger(PipelineHelper.class);

  public static boolean isSupported(final OWLAxiom axiom) {
    return axiom.isLogicalAxiom() && !axiom.isAnnotationAxiom() && //
        AxiomType.TBoxAxiomTypes.contains(axiom.getAxiomType());
  }

  public static SimpleEntry<List<OWLAxiom>, List<String>> modelInput(final Set<OWLAxiom> o) {
    return inputToDLSyntax(o);
  }

  protected static SimpleEntry<List<OWLAxiom>, List<String>> inputToDLSyntax(
      final Set<OWLAxiom> o) {
    final OWLObjectRenderer oor = new DLSyntaxObjectRenderer();

    final List<OWLAxiom> axiomsList = new ArrayList<>();
    final List<String> renderedAxioms = new ArrayList<>();

    for (final OWLAxiom axiom : o) {
      if (isSupported(axiom)) {
        axiomsList.add(axiom);
        renderedAxioms.add(preprocess(oor.render(axiom)));
      }
    }
    return new SimpleEntry<>(axiomsList, renderedAxioms);
  }

  @Deprecated
  protected static SimpleEntry<List<OWLAxiom>, List<String>> inputToManchesterOWLSyntax(
      final Set<OWLAxiom> o) {

    final List<OWLAxiom> axiomsList = new ArrayList<>();
    final List<String> renderedAxioms = new ArrayList<>();

    final OWLObjectRenderer manchesterR = new ManchesterOWLSyntaxOWLObjectRendererImplExt();
    for (final OWLAxiom axiom : o) {
      if (isSupported(axiom)) {
        final StringBuilder line = new StringBuilder();
        final List<String> axioms = new ArrayList<>();
        axioms.add(manchesterR.render(axiom));
        for (final OWLClassExpression e : axiom.getNestedClassExpressions()) {
          axioms.add(manchesterR.render(e));
        }
        line.append(String.join("; ", axioms));
        axiomsList.add(axiom);
        renderedAxioms.add(line.toString());
      }
    }
    return new SimpleEntry<>(axiomsList, renderedAxioms);
  }

  // TODO: remove this method and use the command line to call the pre-processing training script
  @Deprecated
  public static String preprocess(final String rendered) {
    return rendered//
        .replaceAll("\\[", " \\[ ")//
        .replaceAll("\\]", " \\] ")//
        .replaceAll("\\(", " \\( ")//
        .replaceAll("\\)", " \\) ")//
        .replaceAll("\\{", " \\{ ")//
        .replaceAll("\\}", " \\} ")//
        .replaceAll("\\.", " \\.")//
        .replaceAll("  ", " ")//
    ;
  }
}
