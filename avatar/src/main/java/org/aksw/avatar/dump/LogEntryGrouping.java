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
package org.aksw.avatar.dump;

import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * @author Lorenz Buehmann
 *
 */
public class LogEntryGrouping {

	public static Multimap<String, LogEntry> groupByIPAddress(Collection<LogEntry> entries){
		Multimap<String, LogEntry> ip2Entries = TreeMultimap.create();
		for (LogEntry entry : entries) {
			ip2Entries.put(entry.getIp(), entry);
		}
		return ip2Entries;
	}
	
	public static Multimap<Date, LogEntry> groupByTime(Collection<LogEntry> entries){
		Multimap<Date, LogEntry> time2Entries = TreeMultimap.create();
		for (LogEntry entry : entries) {
			time2Entries.put(entry.getDate(), entry);
		}
		return time2Entries;
	}
	
	public static Multimap<String, LogEntry> groupByUserAgent(Collection<LogEntry> entries){
		Multimap<String, LogEntry> userAgent2Entries = TreeMultimap.create();
		for (LogEntry entry : entries) {
			userAgent2Entries.put(entry.getUserAgent(), entry);
		}
		return userAgent2Entries;
	}

}
