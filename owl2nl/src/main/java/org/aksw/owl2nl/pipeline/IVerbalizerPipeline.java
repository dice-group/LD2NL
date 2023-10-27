package org.aksw.owl2nl.pipeline;

import org.aksw.owl2nl.pipeline.data.input.IRAKIInput;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IVerbalizerPipeline {

  Logger LOG = LogManager.getLogger(IVerbalizerPipeline.class);

  IVerbalizerPipeline setInput(final IRAKIInput input);

  IVerbalizerPipeline setOutput(final IOutput<?> output);

  IVerbalizerPipeline run();

  IOutput<?> getOutput();
}
