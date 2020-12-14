package org.aksw.owl2nl.raki.data;

/**
 *
 * @author Rene Speck
 *
 */
public class OutputTerminal extends AOutput {

  @Override
  public boolean write(final byte[] bytes) {
    System.out.println(new String(bytes));
    return true;
  }
}
