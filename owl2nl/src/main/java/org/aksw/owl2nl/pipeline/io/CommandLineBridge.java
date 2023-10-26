package org.aksw.owl2nl.pipeline.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author rspeck
 *
 */
public class CommandLineBridge {

  protected static final Logger LOG = LogManager.getLogger(CommandLineBridge.class);

  private String arguments = "";
  private String command = "";

  public String run() {

    final String cmd = new StringBuilder()//
        .append(getCommand()).append(" ")//
        .append(getArguments()).toString();

    final CommandLine cmdLine = new CommandLine("sh");
    cmdLine.addArguments("-c");
    cmdLine.addArguments("'" + cmd + "'", false);
    LOG.info("cmd:{}", cmdLine.toString());

    final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

    final DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValue(0);
    executor.setStreamHandler(streamHandler);
    int exitCode = -1;
    try {
      executor.execute(cmdLine, getEnvironment(), resultHandler);
      resultHandler.waitFor();

      exitCode = resultHandler.getExitValue();
    } catch (final IOException | InterruptedException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }

    if (exitCode == 0) {
      final String rtn = outputStream.toString().trim();
      try {
        outputStream.close();
      } catch (final IOException e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
      return rtn;
    } else {
      LOG.error("Errors ({})", exitCode);
      // resultHandler == null ? "resultHandler is NULL" : resultHandler.toString());
      return null;
    }
  }

  protected Map<String, String> getEnvironment() {
    Map<String, String> env = null;
    try {
      env = EnvironmentUtils.getProcEnvironment();
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    if (env == null) {
      env = new HashMap<>();
    }

    return env;
  }

  public CommandLineBridge setCommand(final String command) {
    this.command = command;
    return this;
  }

  public String getCommand() {
    return command;
  }

  public CommandLineBridge setArguments(final String arguments) {
    this.arguments = arguments;
    return this;
  }

  public String getArguments() {
    return arguments;
  }
}
