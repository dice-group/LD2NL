/*-
 * #%L
 * Triple2NL
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
/**
 * 
 */
package org.aksw.triple2nl.property;

/**
 * @author Lorenz Buehmann
 *
 */
public enum PredicateAsNounConversionType {

  /**
   * Predicate is combined with possessive form of subject.
   */
  POSSESSIVE,
  /**
   * Relative clause is bound by relative pronoun which.
   */
  RELATIVE_CLAUSE_PRONOUN,
  /**
   * Relative clause is bound by complementizer that.
   */
  RELATIVE_CLAUSE_COMPLEMENTIZER,
  /**
   * Relative clause that is not marked by an explicit relative pronoun or complementizer such as
   * who, which or that.
   */
  REDUCED_RELATIVE_CLAUSE,
}
