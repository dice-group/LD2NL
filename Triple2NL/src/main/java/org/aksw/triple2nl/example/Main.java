package org.aksw.triple2nl.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gnu.getopt.Getopt;

public class Main {

  public static void main(final String[] args) {
    final Logger LOG = LoggerFactory.getLogger(Main.class);


    LOG.info("\n==============================\nParsing arguments ...");
    String inputFile = null;
    String outputFolder = null;
    boolean removeBlankNodes = true;
    boolean skolemizeBlankNodes = false;
    final Getopt g = new Getopt("Triple Pipeline", args, "i:x o:x s:x r:x");
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'i':
          inputFile = String.valueOf(g.getOptarg());
          break;
        case 'o':
          outputFolder = String.valueOf(g.getOptarg());
          break;
        case 's':
          skolemizeBlankNodes = Boolean.valueOf(g.getOptarg());
          break;
        case 'r':
          removeBlankNodes = Boolean.valueOf(g.getOptarg());
          break;
        default:
          LOG.info("getopt() returned " + c + "\n");
      }
    }

    LOG.info("==============================Checking arguments...");
    {
      if (inputFile == null || inputFile.trim().isEmpty() || //
          outputFolder == null || outputFolder.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "Missing parameter, set at least axioms file and output path");
      }
    }

    LOG.info("\n==============================\nRunning Pipeline ...");



    CBDGenerator cbdGenerator = new CBDGenerator(inputFile, outputFolder);
    cbdGenerator.setRemoveBlankNodes(removeBlankNodes).setSkolemizeBlankNodes(skolemizeBlankNodes)
        .generate();

    LOG.info("\n==============================\nPipeline exit.");
  }
}
