package org.aksw.owl2nl.raki.data.input;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads a given rdf/xml file as xml and removes imported ontology files. It stores the 1st imported
 * ontology and the file content without imports.
 *
 * @author Rene Speck
 *
 */
public class InputPreprocess {

  protected static final Logger LOG = LogManager.getLogger(InputPreprocess.class);

  private Path ontologyFile = null;
  private String rdfContent = null;

  private Path tmpFolder = null;

  /**
   *
   * @param file
   */
  public InputPreprocess(final Path file) {

    try {
      tmpFolder = Files.createTempDirectory("raki_tmp");
    } catch (final IOException e1) {
      LOG.error(e1.getLocalizedMessage(), e1);
    }

    try {
      final Document doc = loadXML(file);
      final URI uri = export(doc);
      ontologyFile = ontologyFile(uri);

      removeImports(doc);
      rdfContent = printXMLtoString(doc);

    } catch (ParserConfigurationException | SAXException | IOException
        | TransformerFactoryConfigurationError | TransformerException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  protected Path ontologyFile(final URI ontologyURI) {
    Path path = null;
    final String scheme = ontologyURI.getScheme();
    if (scheme == null) {
      // relative path to a file
      final String workingDir = System.getProperty("user.dir");
      path = Paths.get(workingDir, ontologyURI.getPath());
    } else if (scheme.equals("file")) {
      // absolute path to a file
      path = Paths.get(ontologyURI);
    } else if (scheme.startsWith("http")) {
      // http or https
      URL url = null;
      try {
        url = ontologyURI.toURL();
        path = downloadFile(url);
      } catch (final MalformedURLException e) {
        LOG.error("Could not find the  ontology:" + ontologyURI);
      }
    } else {
      LOG.error("Could not find the  ontology.");
    }
    LOG.info("Imported Ontology:" + path.toAbsolutePath().toString());
    return path;
  }

  protected Path downloadFile(final URL ontologyURL) {
    String uriAuthority = null;
    String uriPath = null;
    try {
      uriAuthority = ontologyURL.toURI().getAuthority();
      uriPath = ontologyURL.toURI().getPath();
    } catch (final URISyntaxException e1) {
      LOG.error(e1.getLocalizedMessage(), e1);
    }

    final Path path = Paths.get(//
        tmpFolder.toAbsolutePath().toString()//
            .concat("/")//
            .concat(uriAuthority)//
            .concat("/")//
            .concat(uriPath)//
    );

    try {
      final File f = path.toFile();
      if (f.exists() && !f.isDirectory()) {
        // We already downloaded the file so do nothing here.
      } else {
        FileUtils.copyURLToFile(ontologyURL, path.toFile());
      }

      return path;
    } catch (final IOException e) {
      LOG.error("Could not download the  ontology:" + ontologyURL);
      return null;
    }
  }

  protected Document loadXML(final Path xml)
      throws ParserConfigurationException, SAXException, IOException {

    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    final DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(xml.toFile().getAbsolutePath());
  }

  protected void removeImports(final Document doc) {
    final String t = "owl:Ontology";
    while (doc.getElementsByTagName(t).getLength() > 0) {
      final NodeList nodeList = doc.getElementsByTagName(t);
      for (int i = 0; i < nodeList.getLength(); i++) {
        final Node node = nodeList.item(i);
        node.getParentNode().removeChild(node);
      }
    }
    doc.normalize();
  }

  protected URI export(final Document doc) {

    final String tag = "owl:imports";
    final NodeList imports = doc.getElementsByTagName(tag);

    if (imports.getLength() < 1) {
      LOG.warn("The given document does not import an ontology.");
      return null;
    }
    if (imports.getLength() > 1) {
      LOG.warn("The given document imports more than one ontology. "
          + "We ignore all except for the first one. ");
    }

    final String attribute = "rdf:resource";
    final Element element = (Element) imports.item(0);
    final String attri = element.getAttribute(attribute);

    URI uri = null;
    try {
      uri = new URI(attri);
    } catch (final URISyntaxException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return uri;
  }

  protected static String printXMLtoString(final Document document)
      throws TransformerFactoryConfigurationError, TransformerException {
    final StringWriter stringWriter = new StringWriter();
    final StreamResult streamResult = printXML(document, stringWriter);
    return streamResult.getWriter().toString();
  }

  protected static StreamResult printXML(final Document document, final Writer writer)
      throws TransformerFactoryConfigurationError, TransformerException {
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    final StreamResult result = new StreamResult(writer);
    final DOMSource source = new DOMSource(document);
    transformer.transform(source, result);
    return result;
  }

  //
  public Path getOntology() {
    return ontologyFile;
  }

  public String getContent() {
    return rdfContent;
  }

  @Override
  public String toString() {
    return "content: " + getContent() + " \nOntology: " + getOntology();
  }
}
