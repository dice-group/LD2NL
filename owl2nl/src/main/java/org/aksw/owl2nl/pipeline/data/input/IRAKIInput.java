package org.aksw.owl2nl.pipeline.data.input;

import java.nio.file.Path;
import org.aksw.owl2nl.data.IInput;
import org.semanticweb.owlapi.model.IRI;

public interface IRAKIInput extends IInput {

  public enum Type {
    NOTSET, //
    RULES, // use owl2nl
    MODEL // use trained model
  }

  IRAKIInput setAxioms(Path axioms);

  IRAKIInput setAxioms(IRI axioms);

  IRAKIInput setType(Type type);

  Type getType();
}
