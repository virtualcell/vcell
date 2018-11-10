#!/usr/bin/env bash

CLASSPATH=./vcell-client/target/maven-jars/*:./vcell-client/target/*
MAIN_CLASS=cbit.vcell.client.test.VCellClientTest

props="-Dvcell.installDir=$PWD"
props="${props} -Dvcell.softwareVersion=standalone_VCell_7.0"
props="${props} -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.8-jar-with-dependencies.jar"
props="${props} -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.8-jar-with-dependencies.jar"

echo java ${props} -cp $CLASSPATH $MAIN_CLASS vcellapi.cam.uchc.edu:443
java ${props} -cp $CLASSPATH $MAIN_CLASS vcellapi.cam.uchc.edu:443
