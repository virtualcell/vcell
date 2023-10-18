#!/usr/bin/env bash

CLASSPATH=./vcell-client/target/maven-jars/*:./vcell-client/target/*
MAIN_CLASS=cbit.vcell.client.VCellClientMain

props="-Dvcell.installDir=$PWD"
props="${props} -Dvcell.softwareVersion=standalone_VCell_7.0"
props="${props} -Dvcell.bioformatsJarFileName=vcell-bioformats-0.0.9-jar-with-dependencies.jar"
props="${props} -Dvcell.bioformatsJarDownloadURL=http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar"

echo java ${props} -cp $CLASSPATH $MAIN_CLASS --api-host=vcellapi.cam.uchc.edu:443
java ${props} -cp $CLASSPATH $MAIN_CLASS --api-host=vcellapi.cam.uchc.edu:443
