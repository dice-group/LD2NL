#!/bin/sh
# input parameters
# i="https://protege.stanford.edu/ontologies/travel.owl"
# i="http://www.ling.helsinki.fi/kit/2004k/ctl310semw/Protege/koala.owl"
i="/media/store/GitRepos/raki-verbalizer/owl2nl/test.owl"
o="eval.html"
u="false" # true for URL, false for File
#
#
#
mainClass="org.aksw.owl2nl.ui.OWLAxiomConversionEvaluationInterface"
args="-i$i -o$o -u$u"

export MAVEN_OPTS="-Xmx4G"

nohup mvn exec:java -B \
	-Dexec.args="$args" \
	-Dexec.mainClass=$mainClass \
	> $0.log 2>&1 </dev/null &
