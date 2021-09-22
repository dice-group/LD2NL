package org.aksw.owl2nl.util.grammar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.Resources;

/**
 * This class reads lines of a file with labels of all subclasses of Person in DBpedia. Those labels
 * where retrieved with the following SPARQL query:<br>
 * <br>
 * <code>
    SELECT ?y
    WHERE {
      ?x rdfs:subClassOf* dbo:Person .
      ?x rdfs:label ?y .
      FILTER (lang(?y) = 'en')
    }
</code>
 *
 * TODO: Add synonyms to the labels, because, e.g.: `girl` is not a label in the class label file.
 */
public class DBPedia {

  private static Set<String> classLabels = null;

  /**
   * reads "DBpediaPersonClassLabels.txt"
   **/
  public static final Set<String> readClassLabels() {
    if (classLabels == null) {
      try {
        classLabels =
            new HashSet<>(Resources.readLines(Resources.getResource("DBpediaPersonClassLabels.txt"),
                StandardCharsets.UTF_8));

      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    return classLabels;
  }

  public static boolean isPerson(final String word) {
    readClassLabels();
    return classLabels.contains(word);
  }
}
