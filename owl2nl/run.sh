#!/bin/sh

export MAVEN_OPTS="-Xmx8G"

mainClass="org.aksw.owl2nl.raki.Pipeline"
axioms="examples/exampleA.owl"
output="examples/exampleA.json"
log="example.log"

nohup mvn exec:java  -Dexec.mainClass=$mainClass -Dexec.args="$axioms $output" > $log &
