package org.aksw.owl2nl.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dllearner.utilities.owl.ManchesterOWLSyntaxOWLObjectRendererImplExt;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class DataHelper {

  protected static final Logger LOG = LogManager.getLogger(DataHelper.class);
  protected final static OWLDataFactoryImpl df = new OWLDataFactoryImpl();

  static {
    // ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());

    ToStringRenderer.getInstance().setRenderer(new ManchesterOWLSyntaxOWLObjectRendererImplExt());
  }

  protected final static PrefixManager dpo = new DefaultPrefixManager(null, null, //
      "http://dbpedia.org/ontology/");
  protected final static PrefixManager dpr = new DefaultPrefixManager(null, null, //
      "http://dbpedia.org/resource/");

  public static class OWLClassHelper {

    public static OWLClass french = df.getOWLClass("French", dpo);
    public static OWLClass place = df.getOWLClass("Place", dpo);
    public static OWLClass company = df.getOWLClass("Company", dpo);
    public static OWLClass person = df.getOWLClass("Person", dpo);
    public static OWLClass woman = df.getOWLClass("Woman", dpo);
    public static OWLClass professor = df.getOWLClass("Professor", dpo);
    public static OWLClass dog = df.getOWLClass("Dog", dpo);
    public static OWLClass animal = df.getOWLClass("Animal", dpo);
    public static OWLClass university = df.getOWLClass("University", dpo);
    public static OWLClass softwareCompany = df.getOWLClass("SoftwareCompany", dpo);
    public static OWLClass boy = df.getOWLClass("Boy", dpo);
    public static OWLClass girl = df.getOWLClass("Girl", dpo);
    public static OWLClass child = df.getOWLClass("Child", dpo);
    public static OWLClass man = df.getOWLClass("Man", dpo);
    public static OWLClass thing = df.getOWLClass("Thing", dpo);
    public static OWLClass narcisticPerson = df.getOWLClass("NarcissisticPerson", dpo);
    public static OWLClass human = df.getOWLClass("Human", dpo);
    public static OWLClass homoSapien = df.getOWLClass("HomoSapien", dpo);

    public static OWLClass placebo = df.getOWLClass("Placebo", dpo);
    public static OWLClass drug = df.getOWLClass("Drug", dpo);
    public static OWLClass activePrinciple = df.getOWLClass("ActivePrinciple", dpo);

  }

  public static class OWLDataPropertyHelper {
    public static OWLDataProperty amountOfSalary = df.getOWLDataProperty("amountOfSalary", dpo);
    public static OWLDataProperty earns = df.getOWLDataProperty("earns", dpo);
    public static OWLDataProperty nrOfInhabitants = df.getOWLDataProperty("nrOfInhabitants", dpo);
    public static OWLDataProperty surname = df.getOWLDataProperty("surname", dpo);
    public static OWLDataProperty name = df.getOWLDataProperty("name", dpo);
    public static OWLDataProperty lastName = df.getOWLDataProperty("last_name", dpo);
    public static OWLDataProperty hasAge = df.getOWLDataProperty("Age", dpo);
    public static OWLDataProperty hairColour = df.getOWLDataProperty("hairColour", dpo);
    public static OWLDataProperty numberOfChildren = df.getOWLDataProperty("numberOfChildren", dpo);
  }

  public static class OWLObjectPropertyHelper {
    public static OWLObjectProperty hasSSN = df.getOWLObjectProperty("socialSecurityNumber", dpo);
    public static OWLObjectProperty sings = df.getOWLObjectProperty("sing", dpo);
    public static OWLObjectProperty workPlace = df.getOWLObjectProperty("workPlace", dpo);
    public static OWLObjectProperty birthPlace = df.getOWLObjectProperty("birthPlace", dpo);
    public static OWLObjectProperty worksFor = df.getOWLObjectProperty("worksFor", dpo);
    public static OWLObjectProperty hasChild = df.getOWLObjectProperty("hasChild", dpo);
    public static OWLObjectProperty ledBy = df.getOWLObjectProperty("isLedBy", dpo);
    public static OWLObjectProperty locatedIn = df.getOWLObjectProperty("locatedIn", dpo);

    public static OWLObjectProperty friend = df.getOWLObjectProperty("friend", dpo);
    public static OWLObjectProperty plays = df.getOWLObjectProperty("play", dpo);
    public static OWLObjectProperty owner = df.getOWLObjectProperty("owner", dpo);
    public static OWLObjectProperty hasWorkPlace = df.getOWLObjectProperty("hasWorkPlace", dpo);
    public static OWLObjectProperty know = df.getOWLObjectProperty("know", dpo);
    public static OWLObjectProperty love = df.getOWLObjectProperty("love", dpo);
    public static OWLObjectProperty hasDog = df.getOWLObjectProperty("hasDog", dpo);
    public static OWLObjectProperty hasFather = df.getOWLObjectProperty("hasFather", dpo);
    public static OWLObjectProperty father = df.getOWLObjectProperty("father", dpo);
    // fatherOf = df.getOWLObjectInverseOf(father).asOWLObjectProperty();
    // public static OWLObjectProperty fathers =
    // df.getOWLInverseObjectPropertiesAxiom(forwardProperty, inverseProperty)
    public static OWLObjectProperty fatherOf = df.getOWLObjectProperty("fatherOf", dpo);
    public static OWLObjectProperty mother = df.getOWLObjectProperty("mother", dpo);
    public static OWLObjectProperty brother = df.getOWLObjectProperty("brother", dpo);
    public static OWLObjectProperty maleSibling = df.getOWLObjectProperty("maleSibling", dpo);
    // public static OWLObjectProperty fatherhood = df.getOWLObjectProperty("fatherhood", dpo);
    // public static OWLObjectProperty motherhood = df.getOWLObjectProperty("motherhood", dpo);
    public static OWLObjectProperty ancestorOf = df.getOWLObjectProperty("ancestorOf", dpo);
    public static OWLObjectProperty parent = df.getOWLObjectProperty("parent", dpo);
    public static OWLObjectProperty sister = df.getOWLObjectProperty("sister", dpo);
    public static OWLObjectProperty aunt = df.getOWLObjectProperty("aunt", dpo);

    public static OWLObjectProperty has_for_active_principle =
        df.getOWLObjectProperty("has_for_active_principle", dpo);
    // df.getOWLObjectProperty("active_principle", dpo);

  }

  public static class OWLNamedIndividualHelper {
    public static OWLNamedIndividual france = df.getOWLNamedIndividual("France", dpr);
    public static OWLNamedIndividual leipzig = df.getOWLNamedIndividual("Leipzig_University", dpr);
    public static OWLNamedIndividual albert = df.getOWLNamedIndividual("Albert_Einstein", dpr);
    public static OWLNamedIndividual bob = df.getOWLNamedIndividual("bob", dpr);
    public static OWLNamedIndividual paderborn = df.getOWLNamedIndividual("Paderborn", dpr);
    public static OWLNamedIndividual karaoke = df.getOWLNamedIndividual("karaoke", dpr);
    public static OWLNamedIndividual jazz = df.getOWLNamedIndividual("jazz", dpr);
    public static OWLNamedIndividual football = df.getOWLNamedIndividual("football", dpr);
    public static OWLNamedIndividual cricket = df.getOWLNamedIndividual("cricket", dpr);
    public static OWLNamedIndividual hockey = df.getOWLNamedIndividual("hockey", dpr);
    public static OWLNamedIndividual tennis = df.getOWLNamedIndividual("tennis", dpr);
    public static OWLNamedIndividual golf = df.getOWLNamedIndividual("golf", dpr);
    public static OWLNamedIndividual hiphop = df.getOWLNamedIndividual("hiphop", dpr);
    public static OWLNamedIndividual rock = df.getOWLNamedIndividual("rock", dpr);
    public static OWLNamedIndividual chess = df.getOWLNamedIndividual("Chess", dpr);
  }

  public static OWLLiteral salary = df.getOWLLiteral(40000);
  public static OWLLiteral literal = df.getOWLLiteral(1000000);

  public static OWLDataRange minInclusive = df.getOWLDatatypeMinInclusiveRestriction(10000000);
  public static OWLDataRange minExclusive = df.getOWLDatatypeMinExclusiveRestriction(10000000);

  public static OWLDatatype temperature = df.getOWLDatatype("temperature", dpo);
}
