/*-
 * #%L
 * controller
 * %%
 * Copyright (C) 2015 - 2021 Data and Web Science Research Group (DICE)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
