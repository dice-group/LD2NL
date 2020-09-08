package org.aksw.owl2nl.raki.data;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Holds the given input file in {@link aximos} and {@link ontology}.
 */
public class Input {

  protected static final Logger LOG = LogManager.getLogger(Input.class);

  public List<OWLAxiom> axioms;
  public OWLOntology ontology;

  public Input(final String axiomsFile) {
    axioms = new ArrayList<>();

    try {
      ontology = loadOntology(axiomsFile);
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }

    axioms.addAll(ontology.getAxioms());

    if (LOG.isDebugEnabled()) {
      LOG.debug("axioms to verbalize: {}", axioms.size());
      LOG.debug("ontology axioms: {} ", ontology.getAxiomCount());

      LOG.debug("Given axioms to verbalize: ");
      axioms.forEach(LOG::debug);
      LOG.info("Given ontology: ");
      LOG.debug(ontology.getAxioms());
    }
  }

  /**
   * Loads OWL file.
   *
   * @param ontologyFile
   * @return
   * @throws OWLOntologyCreationException
   */
  protected static OWLOntology loadOntology(final String ontologyFile)
      throws OWLOntologyCreationException {

    final File file = Paths.get(ontologyFile).toFile();
    LOG.debug("load ontology: {} ", file.getName());

    final OWLOntology ontology = OWLManager//
        .createOWLOntologyManager()//
        .loadOntology(IRI.create(file));
    return ontology;
  }

  public void print() {

    if (LOG.isDebugEnabled()) {
      LOG.debug("ontology logical axioms: {}", ontology.getLogicalAxiomCount());

      LOG.debug("ontology axiom types:");
      final Set<String> types = ontology.getAxioms()//
          .stream().map(p -> p.getAxiomType().getName())//
          .collect(Collectors.toSet());
      types.forEach(LOG::debug);
    }
  }
}
