#!/bin/sh

export MAVEN_OPTS="-Xmx8G"

ontology="http://www.ling.helsinki.fi/kit/2004k/ctl310semw/Protege/koala.owl"
log="main.log"

mainClass="org.aksw.owl2nl.ui.RAKICommandLineInterface"

nohup mvn exec:java \
	-Dexec.mainClass=$mainClass \
	-Dexec.args="-o $ontology"\
	> $log &
