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
package org.aksw.assessment.util;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class PermutationsOfN {

  public static <T> List<List<T>> getSubsetsOfSizeN( List<T> set, int k ) {
    if ( k > set.size() ) {
      k = set.size();
    }
    List<List<T>> result = Lists.newArrayList();
    List<T> subset = Lists.newArrayListWithCapacity( k );
    for ( int i = 0; i < k; i++ ) {
      subset.add( null );
    }
    return processLargerSubsets( result, set, subset, 0, 0 );
  }

  private static <T> List<List<T>> processLargerSubsets( List<List<T>> result, List<T> set, List<T> subset, int subsetSize, int nextIndex ) {
    if ( subsetSize == subset.size() ) {
      result.add( ImmutableList.copyOf( subset ) );
    } else {
      for ( int j = nextIndex; j < set.size(); j++ ) {
        subset.set( subsetSize, set.get( j ) );
        processLargerSubsets( result, set, subset, subsetSize + 1, j + 1 );
      }
    }
    return result;
  }

  public static <T> Collection<List<T>> getPermutationsOfSizeN( List<T> list, int size ) {
    Collection<List<T>> all = Lists.newArrayList();
    if ( list.size() < size ) {
      size = list.size();
    }
    if ( list.size() == size ) {
      all.addAll( Collections2.permutations( list ) );
    } else {
      for ( List<T> p : getSubsetsOfSizeN( list, size ) ) {
        all.addAll( Collections2.permutations( p ) );
      }
    }
    return all;
  }
}
