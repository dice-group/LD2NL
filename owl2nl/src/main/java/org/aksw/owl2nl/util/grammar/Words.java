package org.aksw.owl2nl.util.grammar;

/**
 * This class contains all words that are using to create sentences.
 *
 * @author rspeck
 *
 */
public class Words {

  public static String a = "a";
  public static String also = "also";
  public static String and = "and";
  public static String another = "another";
  public static String as = "as";
  public static String at = "at";
  public static String be = "be";
  public static String by = "by";
  public static String can = "can";
  public static String connect = "connect";
  public static String data = "data";
  public static String datatype = "datatype";
  public static String domain = "domain";
  public static String either = "either";
  public static String every = "every";
  public static String everything = "everything";
  public static String exactly = "exactly";
  public static String forr = "for";
  public static String have = "have";
  public static String has = "has";
  public static String identify = "identify";

  public static String iff = "if";
  public static String imply = "imply";
  public static String individual = "individual";
  @Deprecated
  // public static String is = "is";
  public static String least = "least";
  @Deprecated
  public static String most = "most";

  public static String its = "its";

  public static String much = "much";
  public static String no = "no";
  public static String not = "not";
  public static String object = "object";
  public static String of = "of";
  public static String one = "one";
  public static String oneself = "oneself";
  public static String only = "only";
  public static String opposite = "opposite";
  public static String or = "or";
  public static String other = "other";
  public static String pairwiseDisjoint = "pairwise disjoint";
  public static String property = "property";
  public static String range = "range";
  public static String sequence = "sequence";
  public static String something = "something";
  public static String synonym = "synonym";
  public static String that = "that";
  public static String the = "the";
  public static String then = "then";
  public static String thiss = "this";
  public static String to = "to";
  public static String uniquely = "uniquely";
  public static String value = "value";
  public static String who = "who";
  public static String whose = "whose";
  public static String with = "with";

  /**
   * Writes out numbers with just one digit.
   *
   * @param i > 0
   * @return one, two,..., nine, 10, 11,...
   */
  public static String number(final int i) {
    String r;
    switch (i) {
      case 1:
        r = "one";
        break;
      case 2:
        r = "two";
        break;
      case 3:
        r = "three";
        break;
      case 4:
        r = "four";
        break;
      case 5:
        r = "five";
        break;
      case 6:
        r = "six";
        break;
      case 7:
        r = "seven";
        break;
      case 8:
        r = "eight";
        break;
      case 9:
        r = "nine";
        break;
      default:
        r = String.valueOf(i);
    }
    return r;
  }

}
