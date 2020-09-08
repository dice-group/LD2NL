package org.aksw.owl2nl.exception;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An exception which is thrown if the conversion of an OWL axiom into natural language failed.
 *
 * @author Lorenz Buehmann
 */
public class OWLAxiomConversionException extends Exception {

  private static final long serialVersionUID = 8212402057380138127L;

  private final OWLAxiom axiom;

  public OWLAxiomConversionException(final OWLAxiom axiom, final Exception e) {
    super(e);
    this.axiom = axiom;
  }

  /**
   * @return the OWL axiom for which the conversion failed
   */
  public OWLAxiom getAxiom() {
    return axiom;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Throwable#toString()
   */
  @Override
  public String toString() {
    return "The conversion of the axiom " + axiom + " failed.";
  }
}
