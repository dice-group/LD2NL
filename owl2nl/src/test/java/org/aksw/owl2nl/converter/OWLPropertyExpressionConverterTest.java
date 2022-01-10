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
package org.aksw.owl2nl.converter;

import static org.aksw.owl2nl.converter.DataHelper.LOG;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.hasWorkPlace;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.owner;
import static org.aksw.owl2nl.converter.DataHelper.OWLObjectPropertyHelper.plays;

import org.aksw.owl2nl.data.OWL2NLInput;
import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class OWLPropertyExpressionConverterTest {

  private final OWLPropertyExpressionConverter converter =
      new OWLPropertyExpressionConverter(new OWL2NLInput());

  @Test
  public void testWithVerbProperty() {

    final OWLObjectPropertyExpression pe = hasWorkPlace;
    final String text = converter.convert(pe);
    Assert.assertEquals("hasWorkPlace", pe.toString());
    Assert.assertEquals("X has work place Y", text);
  }

  @Test
  public void testWithNounProperty() {
    // verbalizes the property owner, which is a noun

    final OWLObjectPropertyExpression pe = owner;
    final String text = converter.convert(pe);
    Assert.assertEquals("owner", pe.toString());
    Assert.assertEquals("X is owner", text);
  }

  @Test
  public void testWithVerbProperty2() {
    // verbalizes the property plays, which is a verb

    final OWLObjectPropertyExpression pe = plays;
    final String text = converter.convert(pe);
    Assert.assertEquals("play", pe.toString());
    Assert.assertEquals("X plays Y", text);
  }

  @Test
  public void testWithInverse() {
    // Since verbalization of hasWorkPlace is 'X has work place Y'
    // and its inverse can be represented by X hasWorkPlace⁻ Y,
    // the verbalization of the inverse property will be 'Y has work place X'

    final OWLObjectPropertyExpression pe = hasWorkPlace.getInverseProperty();
    if (!pe.isAnonymous()) {
      final String text = converter.convert(pe);
      Assert.assertEquals("hasWorkPlace⁻", pe.toString());
      Assert.assertEquals("Y has work place X", text);
    } else {
      LOG.warn("anonymous not supported {}", pe);
    }
  }
}
