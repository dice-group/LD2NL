OWL2NL
=========

### About

OWL2NL - Converts OWL into natural language

### Compile OWL2NL
In the root folder (parent of `owl2nl`) run: `mvn -T 1C clean install test -pl owl2nl -am`


### Convert OWL into Natural Language:
In the `owl2nl` folder add your owl file (`test.owl`) and run:

```
mvn exec:java -B -Dexec.mainClass=org.aksw.owl2nl.ui.OWL2NLCommandLineInterface -Dexec.args="-o test.owl"

```

For more information take a look at `cli.sh` and `cli2html.sh` as well as at the java tests in `src/test/java/org/aksw/owl2nl/converter`

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
