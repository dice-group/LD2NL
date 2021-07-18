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

import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lorenz Buehmann
 *         created on 10/27/15
 */
public class DBpediaPropertyBlackListTest {

    DBpediaPropertyBlackList blackList;

    @Before
    public void setUp() throws Exception {
        blackList = new DBpediaPropertyBlackList();
    }

    @Test
    public void testContains() throws Exception {
        Assert.assertTrue(blackList.contains("http://dbpedia.org/ontology/abstract"));
        Assert.assertFalse(blackList.contains("http://dbpedia.org/ontology/birthPlace"));
    }

    @Test
    public void testContains1() throws Exception {
        Assert.assertTrue(blackList.contains(ResourceFactory.createResource("http://dbpedia.org/ontology/abstract")));
        Assert.assertFalse(blackList.contains(ResourceFactory.createResource("http://dbpedia.org/ontology/birthPlace")));
    }
}
