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
package org.aksw.owl2nl.converter.visitors;

import org.aksw.owl2nl.data.IInput;
import org.aksw.triple2nl.property.PropertyVerbalization;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

public class OWLPropertyExpressiontoNLGElement extends AToNLGElement
    implements OWLPropertyExpressionVisitorEx<NLGElement> {

  // holds parameter
  protected Parameter parameter = new Parameter();

  /**
   * Holds parameters used in the OWLPropertyExpressiontoNLGElement class.
   */
  public class Parameter {

    public OWLPropertyExpression root;

    // True if the transitive object property is getting called
    public boolean isTransitiveObjectProperty = false;

    // Counts transitive object property as it requires 3 different subject-object combinations
    public int countTransitive = 0;
  }

  public void setParameter(final Parameter parameter) {
    this.parameter = parameter;
  }

  public OWLPropertyExpressiontoNLGElement(final NLGFactory nlgFactory, final IInput input) {
    super(nlgFactory, input);

  }

  /**
   * Object properties connect pairs of individuals.<br>
   * <br>
   * Example: <br>
   * ObjectPropertyAssertion( a:parentOf a:Peter a:Chris ) <br>
   * Peter is a parent of Chris.
   */
  @Override
  public NLGElement visit(final OWLObjectProperty pe) {
    SPhraseSpec phrase = null;
    if (parameter.isTransitiveObjectProperty == true) {
      switch (parameter.countTransitive) {
        case 0:
          phrase = getSentencePhraseFromProperty(pe, "X", "Y");
          break;

        case 1:
          phrase = getSentencePhraseFromProperty(pe, "Y", "Z");
          break;

        case 2:
          phrase = getSentencePhraseFromProperty(pe, "X", "Z");
          parameter.isTransitiveObjectProperty = false;
          parameter.countTransitive = 0;
          break;
      }
      parameter.countTransitive++;
    } else {
      phrase = getSentencePhraseFromProperty(pe, "X", "Y");
    }

    return phrase;
  }

  /**
   * An inverse object property expression connects an individual A with B if and only if the object
   * property connects B with A. <br>
   * <br>
   * InverseObjectProperty := 'ObjectInverseOf' '(' ObjectProperty ')'
   */
  @Override
  public NLGElement visit(final OWLObjectInverseOf owlObjectInverseOf) {
    final OWLObjectProperty property = owlObjectInverseOf.getInverse().getNamedProperty();
    return getSentencePhraseFromProperty(property, "Y", "X");
  }

  /**
   * Data properties connect individuals with literals.<br>
   * <br>
   * Example:<br>
   * DataPropertyAssertion( a:hasName a:Peter "Peter Griffin" )<br>
   * Peter's name is "Peter Griffin".
   */
  @Override
  public NLGElement visit(final OWLDataProperty pe) {
    return getSentencePhraseFromProperty(pe, "X", "Y");
  }

  @Override
  public NLGElement visit(final OWLAnnotationProperty owlAnnotationProperty) {
    LOG.warn("Not implemented yet: OWLAnnotationProperty.");
    return null;
  }

  private SPhraseSpec getSentencePhraseFromProperty(final OWLProperty pe, final String subject,
      final String object) {
    LOG.warn("getSentencePhraseFromProperty");
    final SPhraseSpec phrase = nlgFactory.createClause();

    if (!pe.isAnonymous()) {
      final PropertyVerbalization verbal = propertyVerbalizer(pe);
      final String verbalizationText = verbal.getVerbalizationText();
      if (verbal.isNounType()) {
        phrase.setSubject(subject);
        phrase.setVerb("is");
        phrase.setObject(verbalizationText);
        // noun = true;
      } else if (verbal.isVerbType()) {
        final VPPhraseSpec verb = nlgFactory.createVerbPhrase(verbalizationText);
        phrase.setVerb(verb); // Issues with verbs like 'doneBy'
        phrase.setSubject(subject);
        phrase.setObject(object);
        // noun = false;
      }
    }

    return phrase;
  }
}
