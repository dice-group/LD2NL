package org.aksw.owl2nl.pipeline.data.input;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import org.aksw.owl2nl.data.AInput;
import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.pipeline.io.RakiIO;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

abstract class AInputExtended extends AInput {
  Path ontologyFile;
  Path axiomsFile;

  @Override
  public IInput setOntology(final IRI ontology)
      throws OWLOntologyCreationException, OWLOntologyStorageException {
    super.setOntology(ontology);

    final File file = new File(ontology.getShortForm());
    owlOntology.saveOntology(IRI.create(file.toURI()));
    ontologyFile = file.toPath();
    return this;
  }

  @Override
  public IInput setOntology(final Path ontology) throws OWLOntologyCreationException {
    ontologyFile = ontology;
    return super.setOntology(ontology);
  }
}


/**
 *
 * @author rspeck
 *
 */
public class RAKIInput extends AInputExtended implements IRAKIInput {

  protected Set<OWLAxiom> axioms = null;

  protected Model tboxModel = null;

  protected Model axiomsModel = null;
  protected Type type = Type.NOTSET;

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

  private void init() {
    if (axiomsFile != null) {
      axiomsModel = RakiIO.readRDFXML(axiomsFile.toFile().getPath());
      if (axiomsModel == null) {
        LOG.error("Could not read axioms.");
      }
    }
    if (ontologyFile != null) {
      tboxModel = RakiIO.readRDFXML(ontologyFile.toFile().getPath());
      if (tboxModel == null) {
        LOG.error("Could not read tbox.");
      }
    }
  }

  @Override
  public String getEnglishLabel(final IRI iri) {
    final String lang = "en";
    if (tboxModel == null) {
      init();
    }

    String label = getlabel(iri, lang, tboxModel);

    if (axiomsModel != null && label == null) {
      label = getlabel(iri, lang, axiomsModel);
    }
    return label;
  }

  @Override
  public Set<OWLAxiom> getAxioms() {
    return axioms;
  }

  @Override
  public IRAKIInput setAxioms(final Path axiomsPath) {
    axiomsFile = axiomsPath;
    try {
      axioms = OWLManager//
          .createOWLOntologyManager()//
          .loadOntologyFromOntologyDocument(axiomsPath.toFile())//
          .getAxioms();
    } catch (final OWLOntologyCreationException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return this;
  }

  @Override
  public IRAKIInput setAxioms(final IRI axiomsIRI) {
    try {
      final OWLOntology axiomsOnto = OWLManager.createOWLOntologyManager().loadOntology(axiomsIRI);
      axioms = axiomsOnto.getAxioms();

      final File file = new File(axiomsIRI.getShortForm());
      OWLManager.createOWLOntologyManager().saveOntology(axiomsOnto, IRI.create(file.toURI()));
      axiomsFile = file.toPath();

    } catch (final OWLOntologyCreationException | OWLOntologyStorageException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return this;
  }

  @Override
  public IRAKIInput setType(final Type type) {
    this.type = type;
    return this;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "type: " + type.name() + "; ontology file: " + ontologyFile.toString()
        + "; axioms file: " + axiomsFile.toString();
  }
}
