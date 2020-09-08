package org.aksw.owl2nl.raki.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Creates a documents handed out to domain experts to give feedback. The document might contain
 * answers. it uses the RDF stored in {@link output}.
 */
public class RakiDocument {

  protected static final Logger LOG = LogManager.getLogger(RakiDocument.class);

  protected Output output;
  protected OWLObjectRenderer renderer;

  /**
   * Calls constructor {@link #RakiDocument.RakiDocument(OWLObjectRenderer renderer, Output output)}
   * with a ManchesterOWLSyntaxOWLObjectRendererImplExt instance as OWLObjectRenderer.
   *
   * @param output
   */
  public RakiDocument(final Output output) {
    this(new ManchesterOWLSyntaxOWLObjectRendererImplExt(), output);
  }

  /**
   * Initializes parameters.
   *
   * @param renderer
   * @param output
   */
  public RakiDocument(final OWLObjectRenderer renderer, final Output output) {
    this.output = output;
    this.renderer = renderer;
  }

  /**
   * Renders a given axiom with {@link #renderer}.
   *
   * @param axiom
   * @return rendered axiom
   */
  protected String rederAxioms(final OWLAxiom axiom) {
    return renderer.render(axiom);
  }

  /**
   * Writes given results to outputFilePaths file.
   *
   * @param outputFilePaths
   * @param restuls
   */
  public void writeResults(final String outputFilePaths, final String restuls) {
    try {
      Files.write(Paths.get(outputFilePaths), restuls.getBytes());
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
