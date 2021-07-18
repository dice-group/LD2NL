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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.avatar.clustering.hardening;

/**
 *
 * @author ngonga
 */
public class HardeningFactory {
    public enum HardeningType { LARGEST, SMALLEST, AVERAGE};
    public static Hardening getHardening(HardeningType type)
    {
        if(type.equals(HardeningType.LARGEST)) return new LargestClusterHardening();
        if(type.equals(HardeningType.SMALLEST)) return new SmallestClusterHardening();
        if(type.equals(HardeningType.AVERAGE)) return new AverageWeightClusterHardening();
        else return new LargestClusterHardening();
    }
}
