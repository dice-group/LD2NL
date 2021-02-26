package org.aksw.owl2nl.raki.data.input;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.owl2nl.raki.data.Statistic;
import org.aksw.owl2nl.raki.io.RakiIO;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Holds the given input file in {@link axioms} and {@link ontology}.
 *
 * @author Rene Speck
 */
public class Input {

  protected static final Logger LOG = LogManager.getLogger(Input.class);

  protected Set<OWLAxiom> axioms = null;
  @Deprecated
  protected Map<OWLAxiom, String> axiomsOfDeclaration = new HashMap<>();

  protected Model tboxModel = null;
  @Deprecated
  protected Model axiomsModel = null; // uses labels from axioms file
  @Deprecated
  private final boolean useLabelsInAxioms;

  protected Statistic statistic = new Statistic();

  /**
   *
   * @param axiomsFile
   */
  public Input(final String axiomsFile) {
    useLabelsInAxioms = false;

    final InputPreprocess preprocess = new InputPreprocess(Paths.get(axiomsFile));
    final Path ontologyFile = preprocess.getOntology();
    final String content = preprocess.getContent();

    LOG.info("loading ontology: {} ", ontologyFile);
    if (ontologyFile != null) {
      tboxModel = RakiIO.readRDFXML(ontologyFile.toFile().getPath());
      if (tboxModel == null) {
        LOG.error("Could not read tbox.");
      }
    }

    LOG.info("loading axioms: {} ", axiomsFile);
    if (useLabelsInAxioms && axiomsFile != null) {
      axiomsModel = RakiIO.readRDFXML(axiomsFile);
    }

    final InputStream in = new ByteArrayInputStream(content.getBytes());
    try {
      axioms = OWLManager//
          .createOWLOntologyManager()//
          .loadOntologyFromOntologyDocument(in).getAxioms();
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }

    // axioms = RakiIO.loadOntology(axiomsFile).getAxioms();
    LOG.info("# axioms:{}", axioms.size());

    init(axioms);

    LOG.info("# axioms with DL label :{}", axiomsOfDeclaration.size());

    statistic.stat(axioms);
    LOG.info(statistic.toString());

  }

  protected void init(final Set<OWLAxiom> axiomsSet) {

    for (final OWLAxiom axiom : axiomsSet) {
      if (axiom.getAxiomType().getName().equals("Declaration")) {
        final Set<OWLEntity> signature = axiom.getSignature();
        if (signature.size() > 1) {
          // TODO: find a better way to get the IRI?
          LOG.warn("We assume a size of 1 here, but in this case it's larger.");
        }
        if (!signature.isEmpty()) {
          final OWLEntity element = signature.iterator().next();
          final String label = getEnglishLabel(element.getIRI());
          if (label != null) {
            axiomsOfDeclaration.put(axiom, label);
          }
        }
      }
    }
  }

  /**
   * Gets the English label from the given ontology.
   *
   * @param iri
   * @return label or null
   */
  public String getEnglishLabel(final IRI iri) {
    final String lang = "en";
    String label = getlabel(iri, lang, tboxModel);
    if (useLabelsInAxioms && label == null) {
      label = getlabel(iri, lang, axiomsModel);
    }
    return label;
  }

  /**
   * Gets the label from the given ontology. Checks the lang tag, if not tag is given the first
   * occurrence of a label will be chosen.
   *
   * @param iri
   * @param lang e.g. de,en,...
   * @return label or null
   */
  protected String getlabel(final IRI iri, final String lang, final Model model) {
    String label = null; // with lang
    String tmplabel = null; // without lang, 1st occurrence

    final Resource resource = model.getResource(iri.toString());

    if (resource != null && resource.hasProperty(RDFS.label)) {
      final NodeIterator ni = model.listObjectsOfProperty(resource, RDFS.label);
      while (ni.hasNext()) {
        final RDFNode n = ni.next();
        if (tmplabel == null) {
          tmplabel = n.asLiteral().getLexicalForm();
        }
        if (lang.equals(n.asLiteral().getLanguage())) {
          label = n.asLiteral().getLexicalForm();
          break;
        }
      } // end while
    } // end if
    return label == null ? tmplabel : label;
  }

  public Set<OWLAxiom> getAxioms() {
    return axioms;
  }

  @Deprecated
  public Map<OWLAxiom, String> getAxiomsMap() {
    return axiomsOfDeclaration;
  }
}
