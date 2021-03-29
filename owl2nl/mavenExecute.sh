#!/bin/sh

export MAVEN_OPTS="-Xmx4G"

ontology="http://www.ling.helsinki.fi/kit/2004k/ctl310semw/Protege/koala.owl"

mainClass="org.aksw.owl2nl.ui.OWL2NLCommandLineInterface"
nohup mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="-o $ontology" > $0.log 2>&1 </dev/null &
