package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.data.IInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract class AConverter {
  protected static final Logger LOG = LogManager.getLogger(AConverter.class);

  protected IInput input;

  public AConverter(final IInput input) {
    this.input = input;
  }

  public IInput getInput() {
    return input;
  }
}
