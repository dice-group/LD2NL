package org.aksw.owl2nl.util.grammar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  protected static final Logger LOG = LogManager.getLogger(DBPedia.class);

  private static Set<String> classLabels = null;
  private static String file = "DBpediaPersonClassLabels.txt";

  /**
   * Is a DBpedia subclass of Person.
   *
   * @param word
   * @return
   */
  public static boolean isPerson(final String word) {
    return readClassLabels().contains(word.trim().toLowerCase());
  }

  private static final Set<String> readClassLabels() {
    if (classLabels == null) {
      try {
        classLabels = new HashSet<>(//
            Resources.readLines(Resources.getResource(file), StandardCharsets.UTF_8)//
                .stream().map(String::toLowerCase).collect(Collectors.toSet())//
        );
      } catch (final IOException e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
    }
    return classLabels;
  }
}
