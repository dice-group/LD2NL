package org.aksw.owl2nl.raki.data;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Holds the given input file in {@link axioms} and {@link ontology}.
 */
public class Input {

  protected static final Logger LOG = LogManager.getLogger(Input.class);

  protected List<OWLAxiom> axioms;

  public List<OWLAxiom> getAxioms() {
    return axioms;
  }

  protected Model model;

  /**
   *
   * @param axiomsFile
   * @param ontologyFile could be null
   */
  public Input(final String axiomsFile, final String ontologyFile) {
    axioms = new ArrayList<>();

    // reads given ontology
    if (ontologyFile != null) {
      readRDFXML(ontologyFile);
    } else {
      model = null;
    }

    // reads given axioms
    try {
      axioms.addAll(loadOntology(axiomsFile).getAxioms());
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  protected Model readRDFXML(final String file) {
    model = ModelFactory.createDefaultModel();
    model.read(file, Lang.RDFXML.getName());
    return model;
  }

  /**
   * Loads OWL file.
   *
   * @param ontologyFile
   * @return
   * @throws OWLOntologyCreationException
   */
  protected OWLOntology loadOntology(final String ontologyFile)
      throws OWLOntologyCreationException {

    final File file = Paths.get(ontologyFile).toFile();
    LOG.debug("load ontology: {} ", file.getName());

    final OWLOntology ontology = OWLManager//
        .createOWLOntologyManager()//
        .loadOntology(IRI.create(file));

    return ontology;
  }

  /**
   * Gets the English label from the given ontology.
   *
   * @param iri
   * @return label or null
   */
  public String getEnglishLabel(final IRI iri) {
    return getlabel(iri, "en");
  }

  /**
   * Gets the label from the given ontology.
   *
   * @param iri
   * @return label or null
   */
  public String getlabel(final IRI iri, final String lang) {
    String label = null;
    final Resource resource = model.getResource(iri.toString());
    if (resource != null && resource.hasProperty(RDFS.label)) {
      final NodeIterator ni = model.listObjectsOfProperty(resource, RDFS.label);
      while (ni.hasNext()) {
        final RDFNode n = ni.next();
        if (lang.equals(n.asLiteral().getLanguage())) {
          label = n.asLiteral().getLexicalForm();
          break;
        }
      }
    }
    return label;
  }
}
