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
package org.aksw.owl2nl.evaluation.visual;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;

/**
 * A generator for HTML tables.
 *
 * @author Lorenz Buehmann
 *
 */
public class HTMLTableGenerator {

  public static String getStyle() {
    try {
      return Resources.toString(Resources.getResource("HTMLTableTemplate.html"),
          StandardCharsets.UTF_8);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Generates an HTML table based on the given column names and data.
   *
   * @param columnNames the column names
   * @param data the data as a list of row entry values
   * @return an HTML table
   */
  public static String generateHTMLTable(final List<String> columns,
      final List<List<String>> data) {
    String html = getStyle();
    for (final String name : columns) {
      html = html//
          .concat("<th data-align=\"left\" data-sortable=\"true\" data-valign='middle'>")
          .concat(name)//
          .concat("</th>");
    }
    // the data
    html = html.concat("<tbody>\n");
    for (final List<String> row : data) {
      html = html.concat("<tr>\n");
      for (final String entry : row) {
        html = html.concat("<td>").concat(entry).concat("</td>\n");
        // class='number'
      }
      html = html.concat("</tr>\n");
    }
    return html.concat("</tbody>\n").concat("</table>\n").concat("</body>\n").concat("</html>\n");
  }
}
