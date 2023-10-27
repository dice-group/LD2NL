package org.aksw.owl2nl.pipeline.data.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.aksw.owl2nl.evaluation.visual.HTMLTableGenerator;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;

public class OutputHTMLTable implements IOutput<String> {
  StringBuilder sb = new StringBuilder();
  final OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImplExt();

  @Override
  public String write(Map<OWLAxiom, String> verbalizationResults) {
    sb = new StringBuilder();
    final List<List<String>> data = new ArrayList<>();
    int i = 1;
    for (final OWLAxiom axiom : verbalizationResults.keySet()) {
      String renderedAxiom = renderer.render(axiom);
      String nl = verbalizationResults.get(axiom);
      for (final ManchesterOWLSyntax keyword : ManchesterOWLSyntax.values()) {
        if (keyword.isAxiomKeyword() || keyword.isClassExpressionConnectiveKeyword()
            || keyword.isClassExpressionQuantiferKeyword()) {
          final String regex;
          regex = "\\s?(" + keyword.keyword() + "|" + keyword.toString() + ")(\\s|:)";
          renderedAxiom = renderedAxiom.replaceAll(regex, " <b>" + keyword.keyword() + "</b> ");
        }
      }
      data.add(Arrays.asList(String.valueOf(i++), axiom.toString(), renderedAxiom, nl));
    }
    sb = new StringBuilder();
    sb.append(HTMLTableGenerator//
        .generateHTMLTable(Arrays.asList("ID", "Axiom", "Axiom", "NL"), data));
    return getResults();
  }

  @Override
  public String getResults() {
    return sb.toString();
  }
}
