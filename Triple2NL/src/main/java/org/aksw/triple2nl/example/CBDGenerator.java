package org.aksw.triple2nl.example;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.aksw.triple2nl.DocumentGenerator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CBDGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(CBDGenerator.class);

  protected DocumentGenerator documentGenerator;
  protected String inputFile;
  protected String outputFolder;
  protected Model model;
  protected Set<Resource> resources;

  boolean removeBlankNodes = true;
  boolean skolemizeBlankNodes = false;
  // TODO: use the variable
  int cbdDepth = Integer.MAX_VALUE;


  /**
   * Generates CBDs for each resource and verbalizes each CBD.
   * 
   * @param inputFile
   * @param outputfolder
   */
  public CBDGenerator(String inputFile, String outputfolder) {
    this(inputFile, outputfolder, Integer.MAX_VALUE);
  }

  /**
   * Generates CBDs for each resource and verbalizes each CBD.
   * 
   * @param inputFile
   * @param outputFolder
   * @param cbdDepth
   */
  public CBDGenerator(String inputFile, String outputFolder, int cbdDepth) {
    this.inputFile = inputFile;
    this.outputFolder = outputFolder;

    documentGenerator = new DocumentGenerator(SparqlEndpoint.getEndpointDBpedia(), "cache");

    model = ModelFactory.createDefaultModel().read(inputFile);
    write(this.outputFolder.concat("inputFile.ttl"), model);
    resources = getResources(model);
  }

  public void generate() {
    for (Resource resource : resources) {

      if (resource.isAnon())
        continue;

      Model cbdModel = generateCBD(model, resource, new HashSet<>());

      boolean wrote = this.write(this.getOutputFile(resource).concat(".ttl"), cbdModel);
      if (wrote) {
        try {
          String str;
          if (this.skolemizeBlankNodes) {
            str = verb(this.skolemizeModel(cbdModel));
          } else if (this.removeBlankNodes) {
            str = verb(this.removeBlankNodes(cbdModel));
          } else {
            str = verb((cbdModel));
          }

          BufferedWriter writer;
          writer = new BufferedWriter(new FileWriter(this.getOutputFile(resource).concat(".txt")));
          writer.write(str);
          writer.close();
        } catch (Exception e) {
          LOG.error(e.getLocalizedMessage());
        }
      }
    }
  }


  public String verb(Model model) {
    return documentGenerator.generateDocument(model);
  }

  public Set<Resource> getResources(Model model) {
    Set<Resource> set = new HashSet<>();
    ResIterator rIter = model.listSubjects();
    while (rIter.hasNext()) {
      Resource r = rIter.next();
      set.add(r);
    }
    return set;
  }

  private static Model generateCBD(Model model, Resource resource, Set<Resource> visited) {

    Model cbdModel = ModelFactory.createDefaultModel();
    cbdModel.setNsPrefixes(model);

    if (!visited.contains(resource)) {
      visited.add(resource);

      StmtIterator stmtIterator = model.listStatements(resource, (Property) null, (RDFNode) null);
      while (stmtIterator.hasNext()) {
        Statement statement = stmtIterator.next();
        cbdModel.add(statement);

        if (statement.getObject().isResource()) {
          Resource nestedResource = statement.getObject().asResource();
          Model nestedCBD = generateCBD(model, nestedResource, visited);
          cbdModel.add(nestedCBD);
        } else if (statement.getObject().isAnon()) {
          Resource blankNode = statement.getObject().asResource();
          StmtIterator blankNodeStmtIterator =
              model.listStatements(blankNode, (Property) null, (RDFNode) null);
          while (blankNodeStmtIterator.hasNext()) {
            Statement blankNodeStatement = blankNodeStmtIterator.next();
            cbdModel.add(blankNodeStatement);
          }
        }
      }
    }

    return cbdModel;
  }



  protected String getOutputFile(Resource resource) {
    String outFile = outputFolder.concat(resource.getLocalName());
    LOG.info("outFile: ".concat(outFile));
    return outFile;
  }

  protected boolean write(String outFile, Model cbdModel) {
    try {
      RDFDataMgr.write(new FileOutputStream(outFile), cbdModel, RDFFormat.TURTLE_PRETTY);
    } catch (FileNotFoundException e) {
      LOG.error(e.getLocalizedMessage());
      return false;
    }
    return true;
  }

  protected Model removeBlankNodes(Model model) {
    Model modelWithoutBlankNodes = ModelFactory.createDefaultModel();
    modelWithoutBlankNodes.setNsPrefixes(model);
    Set<Resource> resourcesWithBlankNodes = new HashSet<>();

    StmtIterator stmtIterator = model.listStatements();
    while (stmtIterator.hasNext()) {
      Statement statement = stmtIterator.next();
      Resource subject = statement.getSubject();
      Property predicate = statement.getPredicate();
      RDFNode object = statement.getObject();

      if (subject.isAnon()) {
        resourcesWithBlankNodes.add(subject);
      }

      if (object.isResource() && object.asResource().isAnon()) {
        resourcesWithBlankNodes.add(object.asResource());
      }

      if (!subject.isAnon() && !(object.isResource() && object.asResource().isAnon())) {
        modelWithoutBlankNodes.add(subject, predicate, object);
      }
    }

    StmtIterator stmtIteratorNoBlankNodes = model.listStatements();
    while (stmtIteratorNoBlankNodes.hasNext()) {
      Statement statement = stmtIteratorNoBlankNodes.next();
      Resource subject = statement.getSubject();
      RDFNode object = statement.getObject();

      if (!resourcesWithBlankNodes.contains(subject)
          && (!object.isResource() || !resourcesWithBlankNodes.contains(object.asResource()))) {
        modelWithoutBlankNodes.add(statement);
      }
    }

    return modelWithoutBlankNodes;
  }

  /**
   * skolemize
   * 
   * @param model
   * @return
   */
  protected Model skolemizeModel(Model model) {
    Model skolemizedModel = ModelFactory.createDefaultModel();

    StmtIterator stmtIterator = model.listStatements();
    while (stmtIterator.hasNext()) {
      Statement statement = stmtIterator.next();
      Resource subject = statement.getSubject();
      Property predicate = statement.getPredicate();
      RDFNode object = statement.getObject();

      Resource skolemizedSubject = skolemizeResource(subject, skolemizedModel);

      RDFNode skolemizedObject;
      if (object.isResource()) {
        skolemizedObject = skolemizeResource(object.asResource(), skolemizedModel);
      } else {
        skolemizedObject = object;
      }
      skolemizedModel.add(skolemizedSubject, predicate, skolemizedObject);
    }

    return skolemizedModel;
  }

  private Resource skolemizeResource(Resource originalResource, Model skolemizedModel) {
    if (originalResource.isAnon()) {
      return skolemizedModel.createResource(getSkolemizedURI());
    } else {
      return originalResource;
    }
  }

  private String getSkolemizedURI() {
    return "http://example.org/skolem/" + UUID.randomUUID();
  }

  public CBDGenerator setRemoveBlankNodes(boolean removeBlankNodes) {
    this.removeBlankNodes = removeBlankNodes;
    return this;
  }

  public CBDGenerator setSkolemizeBlankNodes(boolean skolemizeBlankNodes) {
    this.skolemizeBlankNodes = skolemizeBlankNodes;
    return this;
  }
}
