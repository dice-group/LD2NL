#!/bin/sh

#export MAVEN_OPTS="-Xmx8G -Dlog4j.configuration=file:data/fox/log4j.properties"
export MAVEN_OPTS="-Xmx8G"

nohup mvn exec:java  -Dexec.mainClass="org.aksw.owl2nl.raki.Pipeline" -Dexec.args="privateData/Process.owl privateData/Process.owl privateData/Process.owl.txt" > main.log &
