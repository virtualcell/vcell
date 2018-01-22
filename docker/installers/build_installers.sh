#!/usr/bin/env bash

#
# build_installers.sh must be run within Docker (see Dockerfile and docker-compose.yml in /vcell/docker/installers)
# 

shopt -s -o nounset

# gather classpath (filenames only), Install4J will add the correct separator
compiler_vcellClasspathColonSep=`ls -m /vcellclient/vcell-client/target/maven-jars | tr -d '[:space:]' | tr ',' ':'`

cd /config

PATH=/installer/install4j6/bin:$PATH

#
# retrieve text secrets from file (docker-compose 'secrets' mounts these files in memory at runtime)
#
Install4J_product_key=`cat $Install4J_product_key_file`
winCodeSignKeystore_pswd=`cat $winCodeSignKeystore_pswdfile`
macCodeSignKeystore_pswd=`cat $macCodeSignKeystore_pswdfile`

#
# install Install4J product key (only really needed first time when running this Docker container)
#
/installer/install4j6.1.6/bin/install4jc -L $Install4J_product_key

#echo "jres found in /jres are `ls /jres`"
#cp /jres/* /config
#echo "jres found in /config are `ls /config`"
# if [ -e /config/macosx-amd64-1.8.0_66.tar.gz ] ; then
# 	echo
# 	echo "found /config/macosx-amd64-1.8.0_66.tar.gz"
# 	echo
# else
# 	echo
# 	echo "could not find /config/macosx-amd64-1.8.0_66.tar.gz"
# 	echo
# fi	

# cd /jres
#       - macJre=macosx-amd64-1.8.0_141.tar.gz
#       - win64Jre=windows-amd64-1.8.0_141.tar.gz
#       - win32Jre=windows-x86-1.8.0_141.tar.gz


#
# run install4jc to create installers for VCell Client on supported platforms.
#
/installer/install4j6.1.6/bin/install4jc \
	--win-keystore-password=$winCodeSignKeystore_pswd \
	--mac-keystore-password=$macCodeSignKeystore_pswd \
	-D \
vcellIcnsFile=/config/icons/vcell.icns,\
outputDir=/outputdir,\
mavenRootDir=/vcellclient,\
macJrePath=/jres/$macJre,\
win64JrePath=/jres/$win64Jre,\
win32JrePath=/jres/$win32Jre,\
linux64JrePath=/jres/$linux64Jre,\
linux32JrePath=/jres/$linux32Jre,\
macKeystore=$macCodeSignKeystore_p12,\
winKeystore=$winCodeSignKeystore_pfx,\
applicationId=$compiler_applicationId,\
SoftwareVersionString=$compiler_softwareVersionString,\
Site=$compiler_Site,\
vcellVersion=$compiler_vcellVersion,\
vcellBuild=$compiler_vcellBuild,\
updateSiteBaseUrl=$compiler_updateSiteBaseUrl,\
vcellClasspathColonSep=$compiler_vcellClasspathColonSep,\
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j

