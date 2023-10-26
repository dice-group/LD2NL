package org.aksw.owl2nl.pipeline;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aksw.owl2nl.data.IInput;
import org.aksw.owl2nl.data.OWL2NLInput;

import org.aksw.owl2nl.pipeline.data.input.RAKIInput;
import org.aksw.owl2nl.pipeline.data.input.IRAKIInput.Type;
import org.aksw.owl2nl.pipeline.data.output.IOutput;
import org.aksw.owl2nl.pipeline.data.output.OutputJavaObjects;
import org.aksw.owl2nl.pipeline.data.output.*;
import org.aksw.owl2nl.pipeline.ui.RAKICommandLineInterface;
import org.json.JSONArray;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import simplenlg.lexicon.Lexicon;

public class Example {

	protected static final Logger LOG = LogManager.getLogger(RAKICommandLineInterface.class);
	protected static final  String file = "father.owl";

	/**
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		// exampleA();
		exampleC();
	}

	/**
	 * 
	 */
	public static void exampleC() {
		
		//final IRI axioms = IRI.create(Example.class.getClassLoader().getResource("test.owl"));
		final String  axioms = file;
		final String ontology = file;
		//final IOutput<JSONArray> out = new OutputJsonTrainingData();
		IOutput<String> out = new OutputTerminal();
		
		RAKIInput in = new RAKIInput();
		in
		.setType(Type.RULES)
		.setAxioms(Paths.get(file))//
		.setOntology(Paths.get(file))//
		//.setLexicon(Lexicon.getDefaultLexicon());
		;

		Pipeline.getInstance()//
		.setInput(in)//
		.setOutput(out)//
		.run();

		LOG.info( out.getResults());
	}
	/**
	 *
	 */
	public static void exampleA() {
		final String ontology = file;
		final String axioms = file;
		final String output = "out.txt";

		final RAKIInput in = new RAKIInput();
		in//
		.setAxioms(Paths.get(axioms))//
		.setOntology(Paths.get(ontology))//
		.setLexicon(Lexicon.getDefaultLexicon());

		final IOutput<JSONArray> out = new OutputJsonTrainingData(Paths.get(output));
		// final IOutput out = new OutputTerminal();
		Pipeline.getInstance().setInput(in).setOutput(out).run();//
		// .getOutput()//
		// .getResults();

		out.getResults().forEach(LOG::info);
	}

	/**
	 *
	 */
	public static void exampleB() {

		final RAKIInput in = new RAKIInput();
		{
			final Path axioms = Paths.get(file);
			in.setAxioms(axioms);
			in.setOntology(axioms);
		}

		final IOutput<Map<OWLAxiom, String>> out = new OutputJavaObjects();
		Pipeline.getInstance().setInput(in).setOutput(out).run();//
		// .getOutput()//
		// .getResults();
		out.getResults().entrySet().forEach(LOG::info);
	}
}
