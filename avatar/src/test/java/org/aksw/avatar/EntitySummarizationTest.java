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
/**
 * 
 */
package org.aksw.avatar;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import org.aksw.avatar.dump.DBpediaDumpProcessor;
import org.aksw.avatar.dump.DumpProcessor;
import org.aksw.avatar.dump.LogEntry;
import org.aksw.avatar.dump.LogEntryGrouping;
import org.dllearner.kb.SparqlEndpointKS;
import org.dllearner.kb.sparql.SparqlEndpoint;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import org.junit.Test;

/**
 * @author Lorenz Buehmann
 *
 */
public class EntitySummarizationTest {
	
	SparqlEndpointKS ks = new SparqlEndpointKS(SparqlEndpoint.getEndpointDBpediaLiveAKSW());
	String queryLog = "resources/dbpediaLog/dbpedia.log-valid-select.gz";
	DumpProcessor dumpProcessor = new DBpediaDumpProcessor();
	Collection<LogEntry> logEntries;
	int maxNrOfLogEntries = -1;//-1 means load all entries
	EntitySummarizationModelGenerator generator = new EntitySummarizationModelGenerator(ks);

	/**
	 * @throws java.lang.Exception
	 */
//	@Before
	public void setUp() throws Exception {
		if(maxNrOfLogEntries == -1){
			logEntries = dumpProcessor.processDump(queryLog);
		} else {
			logEntries = dumpProcessor.processDump(queryLog, maxNrOfLogEntries);
		}
		new File("summarization").mkdir();
	}

	/**
	 * Test method for {@link org.aksw.avatar.EntitySummarizationModelGenerator#generateModel(java.util.Collection)}.
	 */
//	@Test
	public void testGenerateModel() {
		EntitySummarizationModel model = generator.generateModel(logEntries);
		System.out.println(model);
	}
	
	/**
	 * Test method for {@link org.aksw.avatar.EntitySummarizationModelGenerator#generateModel(java.util.Collection)},
	 * but this time generating the model for each user agent occurring in the query log dump
	 */
//	@Test
	public void testGenerateModelByUserAgent() {
		Multimap<String, LogEntry> groupedByUserAgent = LogEntryGrouping.groupByUserAgent(logEntries);
		
		//generate an entity summarization model for each user agent that occurs in the query log dump
		for (Entry<String, Collection<LogEntry>> entry : groupedByUserAgent.asMap().entrySet()) {
			String userAgent = entry.getKey();
			Collection<LogEntry> entries = entry.getValue();
			
			EntitySummarizationModel model = generator.generateModel(entries);
			try {
				Files.write(model.toString(), new File("summarization/" + userAgent + ".txt"), Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println(userAgent + "\n" + model);
		}
	}

}
