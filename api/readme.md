Module api has been created as a wrapper for the owl2nl module to expose as a restful service.

It is developed on spring-boot and tested using maven.

To run in your local, take the latest of the project from the KG2NL_WS20 branch.

Navigate to the api folder in terminal or console.

_**Mac-**_
`./mvnw spring-boot:run`

**_Windows-_** and **_Linux_**
`mvn spring-boot:run`


Once the application is running, you can browse the webpage in any local browser on port 8080.

1) HTTP Get
`www.localhost:8080/hello`

Endpoint hello has been defined to test the api is running.
it should give a string response on the browser - "Hello from API".
Check that you must get this response in your browser.

Play with this endpoint: Just pass a parameter myName= and response shall return the name.
eg- www.localhost:8080/hello?myName=Rockstar would respond with "Hello Rockstar"

2) HTTP Get
If so, hit the endpoint - `www.localhost:8080/getOntology`
The getOntology endpoint has been defined to accept a path in the request parameter.
   path is optional, if not passed, it will by default take the ontology url of family hardcoded in the code and display the processed data in json format in the browser.
   you can F12 to see the request and response of this API call.
   
In case you want to test it against any other ontology url, pass the url as a **http encoded** value in request parameter.
eg- `http://localhost:8080/getOntology?path=https%3A%2F%2Fprotege.stanford.edu%2Fontologies%2Fpizza%2Fpizza.owl`
to check for the url - 'https://protege.stanford.edu/ontologies/pizza/pizza.owl'

you can use any online tool to http encode the url. example- 'https://meyerweb.com/eric/tools/dencoder/'

optionally, you can just hit the green play button in IntelliJ to test the api once you setup the configuration.
Steps-

1) Go to the edit configuration screen.
2) Hit the + button to add a configuration
3) Select Spring-Boot
4) give a name to the configuration - eg- 'API'
5) select the starting application - 'org.diceresearch.api.ApiApplication'
6) save
7) Hit the green play button. It should start running the spring boot application using the attached apache server.
