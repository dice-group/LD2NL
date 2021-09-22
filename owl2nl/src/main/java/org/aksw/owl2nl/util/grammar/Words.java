package org.aksw.owl2nl.util.grammar;

/**
 * This class contains all words that are using to create sentences.
 *
 * @author rspeck
 *
 */
public class Words {

  public static String a = "a";
  public static String and = "and";
  public static String as = "as";
  public static String at = "at";
  public static String be = "be";
  public static String either = "either";
  public static String every = "every";
  public static String everything = "everything";
  public static String exactly = "exactly";
  public static String have = "have";
  public static String is = "is";
  public static String least = "least";
  public static String most = "most";
  public static String no = "no";
  public static String not = "not";
  public static String oneself = "oneself";
  public static String only = "only";
  public static String or = "or";
  public static String something = "something";
  public static String that = "that";
  public static String whose = "whose";

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
