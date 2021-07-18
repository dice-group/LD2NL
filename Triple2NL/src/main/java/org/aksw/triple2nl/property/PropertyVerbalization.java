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
import simplenlg.features.Tense;

/**
 * @author Lorenz Buehmann
 *
 */
public class PropertyVerbalization {
	
	private PropertyVerbalizationType verbalizationType;
	private String propertyURI;
	private String propertyText;
	private String expandedVerbalization;
	private String posTags;
	private Tense tense = Tense.PRESENT;
	
	
	public PropertyVerbalization(String propertyURI, String propertyText, PropertyVerbalizationType verbalizationType) {
		this(propertyURI, propertyText, null, verbalizationType);
	}
	
	public PropertyVerbalization(String propertyURI, String propertyText, String posTags, PropertyVerbalizationType verbalizationType) {
		this.propertyURI = propertyURI;
		this.propertyText = propertyText;
		this.posTags = posTags;
		this.verbalizationType = verbalizationType;
		this.expandedVerbalization = propertyText;
	}
	
	/**
	 * @return the property URI
	 */
	public String getProperty() {
		return propertyURI;
	}
	
	/**
	 * @return the propertyText
	 */
	public String getVerbalizationText() {
		return propertyText;
	}
	
	/**
	 * @return the expanded verbalization text
	 */
	public String getExpandedVerbalizationText() {
		return expandedVerbalization;
	}
	
	/**
	 * @return the verbalizationType
	 */
	public PropertyVerbalizationType getVerbalizationType() {
		return verbalizationType;
	}

	public Tense getTense() {
		return tense;
	}

	public void setTense(Tense tense) {
		this.tense = tense;
	}

	/**
	 * @param verbalizationType the verbalizationType to set
	 */
	public void setVerbalizationType(PropertyVerbalizationType verbalizationType) {
		this.verbalizationType = verbalizationType;
	}
	
	/**
	 * @return the POS tags
	 */
	public String getPOSTags() {
		return posTags;
	}
	
	/**
	 * Set the expanded verbalization text.
	 * @param expandedVerbalization the expanded verbalization text
	 */
	public void setExpandedVerbalizationText(String expandedVerbalization) {
		this.expandedVerbalization = expandedVerbalization;
	}
	
	public boolean isNounType(){
		return verbalizationType == PropertyVerbalizationType.NOUN;
	}
	
	public boolean isVerbType(){
		return verbalizationType == PropertyVerbalizationType.VERB;
	}
	
	public boolean isUnspecifiedType(){
		return !(isVerbType() || isNounType());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "URI:" + propertyURI + 
				"\nText:" + propertyText + 
				"\nExpanded Text:" + expandedVerbalization + 
				"\nType: " + verbalizationType;
	}

}
