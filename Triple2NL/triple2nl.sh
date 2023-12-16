#!/bin/bash
#
# examples:
#

####################################################################################
usage () {
    cat <<HELP_USAGE

Usage: $0  -i <input file> -o <output folder> -s <true | false> -r <true | false>

  -i Input file in rdf
  -o output file  folder
  -s skolemize blank nodes
  -r remove blank nodes


HELP_USAGE
exit 0;
}

####################################################################################
while getopts ":i:o:s:r:" x; do
    case "${x}" in
        i)
            i=${OPTARG}
            ;;
        o)
            o=${OPTARG}
            ;;
        s)
            s=${OPTARG}
            ;;
        r)
            u=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${i}" ] || [ -z "${o}" ]; then
    usage
fi

####################################################################################
#
# Run
#
export MAVEN_OPTS="-Xmx4G"
args="-i $i -o $o -s $s -r $r"
mainClass="org.aksw.triple2nl.example.Main"
#nohup mvn exec:java -q -B \
#	-Dexec.args="$args" \
#	-Dexec.mainClass=$mainClass \
#	> $0.log 2>&1 </dev/null &
mvn exec:java -q -B \
	-Dexec.args="$args" \
	-Dexec.mainClass=$mainClass
