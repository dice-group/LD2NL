package org.aksw.owl2nl.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.owl2nl.evaluation.visual.HTMLTableGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;

public class AxiomTypeHelper {
  protected static final Logger LOG = LogManager.getLogger(AxiomTypeHelper.class);

  /**
   * Prints axiom types (name, TBox, logical, nonsyntac, owl2) to "axioms.html" file.
   *
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    final List<List<String>> data = new ArrayList<>();
    final Set<String> names = new HashSet<>();
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
        if (a.isLogical() && AxiomType.TBoxAxiomTypes.contains(a)) {
          // if (AxiomType.TBoxAxiomTypes.contains(a)) {
          names.add(a.getName());
        }
      }
    }
    LOG.info("{} logical TBox axiom types:", names.size());
    names.stream().sorted().forEach(LOG::info);

    Files.write(//
        Paths.get("axioms.html"), //
        HTMLTableGenerator.generateHTMLTable(
            Arrays.asList("id", "name", "TBox", "logical", "nonsyntac", "owl2"), data//
        ).getBytes()//
    );
  }
}
