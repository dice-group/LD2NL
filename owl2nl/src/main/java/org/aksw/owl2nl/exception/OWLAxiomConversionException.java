/*-
 * #%L
 * OWL2NL
 * %%
 * Copyright (C) 2015 - 2021 Data and Web Science Research Group (DICE)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
