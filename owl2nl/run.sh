#!/bin/sh

export MAVEN_OPTS="-Xmx8G"

mainClass="org.aksw.owl2nl.raki.Pipeline"
axioms="/path/to/owl/file/axioms.owl"
output="/path/to/owl/file/axioms.json"

nohup mvn exec:java  -Dexec.mainClass=$mainClass -Dexec.args="$axioms $output" > main.log &
