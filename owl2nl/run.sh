#!/bin/sh

export MAVEN_OPTS="-Xmx8G"

axioms="examples/exampleB.owl"

mainClass="org.aksw.owl2nl.raki.Pipeline"
output="$axioms.json"
log="$axioms.log"

nohup mvn exec:java  -Dexec.mainClass=$mainClass -Dexec.args="$axioms $output" > $log &
