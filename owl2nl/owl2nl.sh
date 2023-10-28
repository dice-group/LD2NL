#!/bin/bash
#
# examples:
#
# ./cli.sh -t json -a ./src/test/resources/test_axioms.owl -o ./src/test/resources/test_ontology.owl -s test_out.json -m rule -u false
#
# ./cli.sh -t html -a ./src/test/resources/test_axioms.owl -o https://raw.githubusercontent.com/dice-group/LD2NL/master/owl2nl/src/test/resources/test_ontology.owl -s test_out.html -m rule -u true
#
####################################################################################
usage () {
    cat <<HELP_USAGE

Usage: $0  -t <txt|html|json> -a <axioms file> -o <ontology file or url> -s <file> -m <rule|model> -u <true|false>

  -t Output type: txt, html, json
  -a File input path with axioms in OWL format to verbalize
  -o File input path with an ontology to use for additional labels
  -s File output path to store the results depending on type
  -m Verbalization type to use: rule or model
  -u is the ontology given by file (false) or url (true) 

HELP_USAGE
exit 0;
}

####################################################################################
while getopts ":t:a:o:s:m:u:" x; do
    case "${x}" in
        t)
            t=${OPTARG}
            [[ $t == "txt" || $t == "html" || $t == "json" ]] || usage
            ;;
        a)
            a=${OPTARG}
            ;;
        o)
            o=${OPTARG}
            ;;
        s)
            s=${OPTARG}
            ;;
        u)
            u=${OPTARG}
            ;;
        m)
            m=${OPTARG}
            [[ $m == "rule" || $m == "model" ]] || usage
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${t}" ] || [ -z "${a}" ]; then
    usage
fi

####################################################################################
#
# Run
#
export MAVEN_OPTS="-Xmx4G"
args="-t $t -a $a -o $o -s $s -m $m -u $u"
mainClass="org.aksw.owl2nl.pipeline.ui.RAKICommandLineInterface"
#nohup mvn exec:java -q -B \
#	-Dexec.args="$args" \
#	-Dexec.mainClass=$mainClass \
#	> $0.log 2>&1 </dev/null &
mvn exec:java -q -B \
	-Dexec.args="$args" \
	-Dexec.mainClass=$mainClass
