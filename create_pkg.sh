#!/bin/bash

mvn clean package
mvn javadoc:javadoc

mkdir deliveru deliveru/{code,documentation}
cp -r src checkstyle.xml checkstyleSuppressions.xml pom.xml README.md USER_MANUAL.md deliveru/code
cp -r target/site/apidocs deliveru/doc
cp target/deliveru.jar deliveru
pandoc -f gfm -s -o deliveru/documentation/README.html --metadata title="Readme"  README.md
pandoc -f gfm -s -o deliveru/documentation/USER_MANUAL.html --metadata title="User Manual" USER_MANUAL.md


