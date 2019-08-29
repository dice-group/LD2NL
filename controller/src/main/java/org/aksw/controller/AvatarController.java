package org.aksw.controller;

import org.aksw.avatar.Verbalizer;
import org.aksw.avatar.gender.Gender;
import org.aksw.avatar.gender.GenderAPIGenderDetector;
import org.aksw.avatar.gender.GenderDetector;
import org.dllearner.kb.sparql.SparqlEndpoint;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class AvatarController {
    private static final SparqlEndpoint ENDPOINT = SparqlEndpoint.getEndpointDBpedia();

    public Gender getGender(String name){
        GenderDetector genderDetector = new GenderAPIGenderDetector();
        return genderDetector.getGender(name);
    }

    /**
     * Returns a textual summary of the given entity.
     *
     * @return
     */
    public String summarizeNamedClass(OWLIndividual individual, OWLClass cls){
        Verbalizer verbalizer = new Verbalizer(ENDPOINT, "cache", null);
        return verbalizer.summarize(individual, cls);
    }

    /**
     * Returns a textual summary of the given entity.
     *
     * @return
     */
    public String summarizeIndvidual(OWLIndividual individual) {
        Verbalizer verbalizer = new Verbalizer(ENDPOINT, "cache", null);
        return verbalizer.summarize(individual);
    }

    public static void main(String[] args){
        // gender detector
        System.out.println("gender detection..");
        AvatarController ac = new AvatarController();
        Gender gender = ac.getGender("bob");

        // entity summarization
        System.out.println("entity summarisation..");
        OWLIndividual ind = new OWLNamedIndividualImpl(IRI.create("http://dbpedia.org/resource/Albert_Einstein"));
        String summary = ac.summarizeIndvidual(ind);
        System.out.println(summary);

        // summarization with named class
        OWLClass cls = new OWLClassImpl(IRI.create("http://dbpedia.org/ontology/Scientist"));
        summary = ac.summarizeNamedClass(ind, cls);
        System.out.println(summary);


    }

}
