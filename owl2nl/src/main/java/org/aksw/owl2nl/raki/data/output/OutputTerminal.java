package org.aksw.owl2nl.raki.data.output;

/**
 *
 * @author Rene Speck
 *
 */
public class OutputTerminal extends AOutput {
  /**
   * Prints bytes to the console and returns true.
   */
  @Override
  public Object write(final byte[] bytes) {
    System.out.println(new String(bytes));
    return true;
  }
}
