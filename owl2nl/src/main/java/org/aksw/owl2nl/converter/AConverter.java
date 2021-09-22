package org.aksw.owl2nl.converter;

import org.aksw.owl2nl.data.IInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLDataFactory;

import simplenlg.framework.NLGFactory;
import simplenlg.realiser.english.Realiser;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

abstract class AConverter {
  protected static final Logger LOG = LogManager.getLogger(AConverter.class);

  protected final OWLDataFactory df = new OWLDataFactoryImpl();

  protected final Realiser realiser;
  protected final NLGFactory nlgFactory;
  protected final IInput input;

  public AConverter(final IInput input) {
    this.input = input;
    realiser = new Realiser(input.getLexicon());
    nlgFactory = new NLGFactory(input.getLexicon());
  }

  public IInput getInput() {
    return input;
  }
}
