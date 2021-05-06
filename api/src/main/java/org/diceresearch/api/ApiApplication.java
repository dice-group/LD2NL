package org.diceresearch.api;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.aksw.owl2nl.OWLAxiomConverter;

import javax.json.Json;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SpringBootApplication
@RestController
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "from OwlAPI") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/getOntology")
	public String GetOntology(@RequestParam(value = "path", defaultValue = "http://www.cs.man.ac.uk/~stevensr/ontology/family.rdf.owl") String path) {
		String response;
		try {
			if (path.isEmpty())
				path = "http://www.cs.man.ac.uk/~stevensr/ontology/family.rdf.owl";
			path = URLDecoder.decode(path, StandardCharsets.UTF_8.toString());
			OWLAxiomConverter converter = new OWLAxiomConverter();
			Map<String, String> json = converter.readOntology(path);
			Gson gson = new Gson();
			response = gson.toJson(json);
			System.out.println(String.format("reading from %s \n Ontologies :\n %s", path, response));
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return String.format("Internal Error. Please try later! \n \n %s", e.getMessage());
		}
	}
}
