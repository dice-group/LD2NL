#!/bin/bash
#
# examples:
#
# ./cli.sh -t html -i ./father.owl -o father.html
#
# ./cli.sh -t txt -i ./father.owl
#
####################################################################################
usage() { echo "Usage: $0 [-t <txt|html>] [-i <input owl axioms file>] [-s <input owl ontology file>] [-o <output file>]" 1>&2; exit 0; }
####################################################################################
while getopts ":t:i:s:o:" x; do
    case "${x}" in
        t)
            t=${OPTARG}
            [[ $t == "txt" || $t == "html" ]] || usage
            ;;
        i)
            i=${OPTARG}
            ;;
        s)
            s=${OPTARG}
            ;;
        o)
            o=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${t}" ] || [ -z "${i}" ]; then
    usage
fi

####################################################################################
# sets argumens
if [ $t == "txt" ]; then
	# input parameters
	# o: ontology
	mainClass="org.aksw.owl2nl.ui.OWL2NLCommandLineInterface"
	args="-o$i"
fi
if [ $t == "html" ]; then
	if [ -z "${o}" ]; then
		usage
	fi
	# input parameters
	#
	# i: file or url
	# u: input type: true for a URL, false for a file
	# o: output file
	mainClass="org.aksw.owl2nl.ui.OWLAxiomConversionEvaluationInterface"
	u="false"
	args="-i$i -o$o -u$u"
fi
####################################################################################
#
# Run
#
export MAVEN_OPTS="-Xmx4G"

#nohup mvn exec:java -q -B \
#	-Dexec.args="$args" \
#	-Dexec.mainClass=$mainClass \
#	> $0.log 2>&1 </dev/null &
mvn exec:java -q -B \
	-Dexec.args="$args" \
	-Dexec.mainClass=$mainClass
