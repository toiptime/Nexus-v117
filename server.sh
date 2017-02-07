#!/bin/sh
export CLASSPATH=".:dist/v117.jar:mina-core.jar:slf4j-api.jar:slf4j-jdk14.jar:mysql-connector-java-bin.jar"
echo "Starting Server"
java server.Start