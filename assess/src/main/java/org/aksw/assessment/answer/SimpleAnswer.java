/*
 * #%L
 * ASSESS
 * %%
 * Copyright (C) 2015 Agile Knowledge Engineering and Semantic Web (AKSW)
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.assessment.answer;


/**
 *
 * @author ngonga
 */
public class SimpleAnswer implements Answer{
    
	String text;
    String hint;
    
    public SimpleAnswer(String answer){
        this(answer, null);
    }
    
    public SimpleAnswer(String answer, String hint){
        text = answer;
        this.hint = hint;
    }

    public String getText() {
     return text;
    }
    
    /* (non-Javadoc)
	 * @see org.aksw.assessment.question.answer.Answer#getHint()
	 */
	@Override
	public String getHint() {
		return null;
	}
	
	/**
	 * @param hint the hint to set
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}
    
    @Override
    public String toString()
    {
        return text;
    }

	
}
