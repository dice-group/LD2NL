package org.aksw.owl2nl.pipeline.planner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aksw.owl2nl.pipeline.PipelineHelper;
import org.aksw.owl2nl.pipeline.data.input.IRAKIInput;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.aksw.owl2nl.pipeline.io.CommandLineBridge;
import org.aksw.owl2nl.pipeline.io.RakiIO;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * This class defines the template of a document and provides all information for the
 * SentencePlanner {@link sentencePlanner}. This class decides about the information and the order
 * of the information to be verbalized.
 *
 * @author Rene Speck
 *
 */
public class DocumentPlanner implements IPlanner<String> {

  protected static final Logger LOG = LogManager.getLogger(DocumentPlanner.class);

  protected SentencePlanner sentencePlanner = null;
  protected IOutput<?> output = null;
  protected IRAKIInput input = null;
  public static final Path tmp;
  protected Properties prop = null;
  protected Map<OWLAxiom, String> resutls = null;
  static {
    tmp = Paths.get(System.getProperty("java.io.tmpdir").concat(File.separator).concat("raki"));
    if (!tmp.toFile().exists()) {
      tmp.toFile().mkdirs();
    }
  }

  public DocumentPlanner(final IRAKIInput input, final IOutput<?> output) {
    this.output = output;
    this.input = input;
    try (InputStream in = new FileInputStream("config.properties")) {

      prop = new Properties();
      prop.load(in);
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  @Override
  public IPlanner<String> build() {
    switch (input.getType()) {
      case MODEL:
        resutls = run();
        break;
      case RULES:
        sentencePlanner = new SentencePlanner(input);
        sentencePlanner.build();
        break;
      case NOTSET:
        throw new UnsupportedOperationException("Input type not set.");
    }
    return this;
  }

  public void results() {
    switch (input.getType()) {
      case MODEL:
        if (output.write(resutls) == null) {
          LOG.error("Couldn't write results.");
        }
        break;
      case RULES:
        resutls = sentencePlanner.results();
        if (output.write(resutls) == null) {
          LOG.error("Couldn't write results.");
        }
        break;
      case NOTSET:
        throw new UnsupportedOperationException("Input type not set.");
    }
  }

  /***
   * calls spm_encode
   */
  protected boolean encode(final String src) {

    try {
      final String rtn = new CommandLineBridge()//
          .setCommand(prop.getProperty("spmEncodeCMD"))// )//
          .setArguments(" " + prop.getProperty("spmEncodeARGS") + " < " + src + " > " + src + ".sp")
          .run();

      LOG.debug("command log: {}", rtn);
      return true;
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
      return false;
    }
  }

  /***
   * calls spm_decode
   */
  protected boolean decode(final String src) {

    try {
      final String rtn = new CommandLineBridge()//
          .setCommand(prop.getProperty("spmDecodeCMD"))// )//
          .setArguments(" " + prop.getProperty("spmDecodeARGS") + " < " + src + ".pre.sp" + " > "
              + src + ".pre")
          .run();

      LOG.debug("command log: {}", rtn);
      return true;
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
      return false;
    }
  }

  protected boolean translate(final String src) {
    final String currentPath = Paths.get("").toAbsolutePath().toString();
    final String model = currentPath.concat(prop.getProperty("model"));

    try {
      final String rtn = new CommandLineBridge()//
          .setCommand(prop.getProperty("translateCMD"))// )//
          .setArguments(" " + prop.getProperty("translateARGS")//
              .concat(" -model ").concat(model)//
              .concat(" -src ").concat(src + ".sp")//
              .concat(" -output ").concat(src + ".pre.sp")//
          ).run();

      LOG.debug("command log: {}", rtn);
      return true;
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
      return false;
    }
  }

  protected Map<OWLAxiom, String> run() {
    final Path file;
    {
      file = Paths.get(tmp.toFile().getAbsolutePath()//
          .concat("/")//
          .concat(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()))//
          .concat(".txt")//
      );
    }
    final SimpleEntry<List<OWLAxiom>, List<String>> in = PipelineHelper//
        .modelInput(input.getAxioms());
    final String lines = String.join(System.lineSeparator(), in.getValue());

    RakiIO.write(file, lines.getBytes());
    final String currentPath = Paths.get("").toAbsolutePath().toString();
    final String src = file.toFile().getAbsolutePath();
    file.toFile().getAbsolutePath().concat(".out");
    currentPath.concat(prop.getProperty("model"));
    if (!encode(src)) {
      LOG.error("Encoding failed!");
    } else {
      if (!translate(src)) {
        LOG.error("Translation failed!");
      } else {
        if (!decode(src)) {
          LOG.error("Decoding failed!");
        } else {
          List<String> verblines = new ArrayList<>();
          try {
            verblines = Files.readAllLines(Paths.get(src + ".pre"));
          } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
          }
          final List<OWLAxiom> axioms = in.getKey();
          final Map<OWLAxiom, String> map = new HashMap<>();
          for (int i = 0; i < axioms.size() && i < verblines.size(); i++) {
            map.put(axioms.get(i), verblines.get(i));
          }
          return map;
        }
      }
    }
    return null;
  }
}
