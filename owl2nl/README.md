OWL2NL
=========

### About

OWL2NL - Converts OWL into natural language

### Compile OWL2NL
In the root folder (parent of `owl2nl`) run: `mvn -T 1C clean install test -pl owl2nl -am`


### Convert OWL into Natural Language:

```
Usage: owl2nl.sh  -t <txt|html|json> \
                  -a <axioms file> \
                  -o <ontology file or url> \
                  -s <file> \
                  -m <rule|model> \
                  -u <true|false>

  -t Output type: txt, html, json
  -a File input path with axioms in OWL format to verbalize
  -o File input path with an ontology to use for additional labels
  -s File output path to store the results depending on type
  -m Verbalization type to use: rule or model
  -u is the ontology given by file (false) or url (true)

```
#### Examples:
```
./owl2nl.sh -a ./src/test/resources/test_axioms.owl \
            -u false -o ./src/test/resources/test_ontology.owl \
            -t json -s test_out.json -m rule
```

```
./owl2nl.sh -a ./src/test/resources/test_axioms.owl \
            -u true -o https://raw.githubusercontent.com/dice-group/LD2NL/master/owl2nl/src/test/resources/test_ontology.owl \
            -t html -s test_out.html -m rule
```
### Example Verbalization

#### DL:

GraduateStudent ⊑ ∃ hasDegree.({BA} ⊔ {BS})

#### OWL:
```
<owl:Class rdf:ID="GraduateStudent">
  <rdfs:subClassOf>
    <owl:Restriction>
      <owl:onProperty>
        <owl:ObjectProperty rdf:about="#hasDegree"/>
      </owl:onProperty>
      <owl:someValuesFrom>
        <owl:Class>
          <owl:oneOf rdf:parseType="Collection">
            <Degree rdf:ID="BA"/>
            <Degree rdf:ID="BS"/>
          </owl:oneOf>
        </owl:Class>
      </owl:someValuesFrom>
    </owl:Restriction>
  </rdfs:subClassOf>
  <rdfs:subClassOf rdf:resource="#Student"/>
</owl:Class>
```
#### Natural Language:
Every graduate student is something that has as degree BA or BS.
