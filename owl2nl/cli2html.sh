#!/bin/sh
#
#
# input parameters
#
# i: file or url
# u: input type: true for a URL, false for a file
# o: output file
#
i="travel.owl"
#i="https://protege.stanford.edu/ontologies/travel.owl"
o="travel.html"
u="false" 
#
# run the app
#
mainClass="org.aksw.owl2nl.ui.OWLAxiomConversionEvaluationInterface"
args="-i$i -o$o -u$u"

export MAVEN_OPTS="-Xmx4G"

nohup mvn exec:java -B \
	-Dexec.args="$args" \
	-Dexec.mainClass=$mainClass \
	> $0.log 2>&1 </dev/null &
