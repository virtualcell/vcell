#!/bin/bash

#=========================================#
# For testing local build of Mac installer
#=========================================#

#---------------------#
#      Configure      #
#---------------------#
CONFIG_DIR=/Users/evalencia/Documents/VCell_Repositories/VCell-Installers
MAVEN_ROOT_DIR=/Users/evalencia/Documents/VCell_Repositories/vcell
SOLVERS_PATH=/Applications/VCell_Test.app/Contents/java/app
INSTALL4J_PATH=/Applications/install4j.app/Contents/Resources/app/bin/install4jc

#--------------------------------#

JARS_DIR=${MAVEN_ROOT_DIR}/vcell-client/target
cp ${JARS_DIR}/vcell-client-0.0.1-SNAPSHOT.jar ${JARS_DIR}/maven-jars/vcell-client-0.0.1-SNAPSHOT.jar
COLON_SEP=`ls -m ${JARS_DIR}/maven-jars | tr -d '[:space:]' | tr ',' ':'`

$INSTALL4J_PATH \
--disable-signing \
--media-types=macosFolder \
--faster \
--debug \
-D \
macKeystore=${CONFIG_DIR}/Apple_Dev_Id_Certificate_exp_20270924.p12,\
vcellLicenseFilePath=${CONFIG_DIR}/license.txt,\
vcell5SplashScreenPngFilePath=${CONFIG_DIR}/cat.jpg,\
vcellNativeLibsDirSourcePath=${SOLVERS_PATH}/nativelibs,\
vcellLocalSolversDirPath=${SOLVERS_PATH}/localsolvers,\
vcellbngPerlPath=${SOLVERS_PATH}/bionetgen,\
vcellIcnsFile=${MAVEN_ROOT_DIR}/docker/build/installers/icons/vcell.icns,\
outputDir=${CONFIG_DIR}/output,\
mavenRootDir=${MAVEN_ROOT_DIR},\
winKeystore='',\
applicationId='1471-8022-1038-5555',\
SoftwareVersionString='7_7_0_5',\
Site='Custom',\
vcellVersion='7',\
vcellBuild='7.0.37',\
updateSiteBaseUrl='http://vcell.org/webstart/Test',\
rmiHosts='vcell-stage.cam.uchc.edu',\
serverPrefixV0='/api/v0',\
serverPrefixV1='/api/v1',\
bioformatsJarFile='vcell-bioformats-0.0.9-jar-with-dependencies.jar',\
bioformatsJarDownloadURL='http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar',\
vcellClasspathColonSep=${COLON_SEP} \
${MAVEN_ROOT_DIR}/docker/build/installers/VCell.install4j
