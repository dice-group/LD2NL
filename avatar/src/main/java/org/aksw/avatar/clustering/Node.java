/*-
 * #%L
 * AVATAR
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
package org.aksw.avatar.clustering;

import java.io.Serializable;

/**
 * 
 * @author ngonga
 */
public class Node implements Serializable {

	public String label;
	public boolean outgoing = true;

	public Node(String label) {
		this.label = label;
	}

	public Node(String label, boolean outgoing) {
		this.label = label;
		this.outgoing = outgoing;
	}

	public String toString() {
		return label;
	}
}
