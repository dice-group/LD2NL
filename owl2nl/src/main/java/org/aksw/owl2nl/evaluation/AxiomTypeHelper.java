package org.aksw.owl2nl.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.owl2nl.evaluation.visual.HTMLTableGenerator;
import org.semanticweb.owlapi.model.AxiomType;

public class PrintAXIOM_TYPES {

  public static void main(final String[] args) throws IOException {

    final List<List<String>> data = new ArrayList<>();
    {
      int i = 1;
      for (final AxiomType<?> a : AxiomType.AXIOM_TYPES) {
        data.add(Arrays.asList(//
            String.valueOf(i++), //
            a.getName(), //
            AxiomType.TBoxAxiomTypes.contains(a) ? "X" : " ", //
            a.isLogical() ? "X" : " ", //
            a.isNonSyntacticOWL2Axiom() ? "X" : " ", //
            a.isOWL2Axiom() ? "X" : " "//
        ));
      }
    }
    Files.write(//
        Paths.get("axioms.html"), //
        HTMLTableGenerator.generateHTMLTable(
            Arrays.asList("id", "name", "TBox", "logical", "nonsyntac", "owl2"), data//
        ).getBytes()//
    );
  }
}
