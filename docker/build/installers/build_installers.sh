#!/usr/bin/env bash

#
# build_installers.sh must be run within Docker (see Dockerfile and docker-compose.yml in /vcell/docker/installers)
# 

shopt -s -o nounset

# NOT_USED (replace with install4j 'scan dir' functionality in I4j launcher config) gather classpath (filenames only), Install4J will add the correct separator
#compiler_vcellClasspathColonSep=`ls -m /vcellclient/vcell-client/target/maven-jars | tr -d '[:space:]' | tr ',' ':'`

cd /config

PATH=/installer/install4j8.0.5/bin:$PATH

#
# retrieve text secrets from file (docker-compose 'secrets' mounts these files in memory at runtime)
#
Install4J_product_key=`cat $Install4J_product_key_file`
winCodeSignKeystore_pswd=`cat $winCodeSignKeystore_pswdfile`
macCodeSignKeystore_pswd=`cat $macCodeSignKeystore_pswdfile`

#
# install Install4J product key (only really needed first time when running this Docker container)
#
/installer/install4j8.0.5/bin/install4jc -L $Install4J_product_key

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
#       - macJre=macosx-amd64-1.8.0_141
#       - win64Jre=windows-amd64-1.8.0_141
#       - win32Jre=windows-x86-1.8.0_141


#
# run install4jc to create installers for VCell Client on supported platforms.
#
#Separate build of win, linux and mac installers to avoid random failure due to threading

#Generate Windows 64bit installers
/installer/install4j8.0.5/bin/install4jc \
	-b 349 \
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
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j
	
mv /outputdir/updates.xml /outputdir/updates_win64.xml

#Generate Windows 32bit installers
/installer/install4j8.0.5/bin/install4jc \
	-b 450 \
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
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j
	
mv /outputdir/updates.xml /outputdir/updates_win32.xml

#Generate linux 64bit installers
/installer/install4j8.0.5/bin/install4jc \
	-b 652 \
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
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j

mv /outputdir/updates.xml /outputdir/updates_linux64.xml

#Generate linux 32bit installers
/installer/install4j8.0.5/bin/install4jc \
	-b 547 \
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
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j

mv /outputdir/updates.xml /outputdir/updates_linux32.xml


#Generate mac 64bit installer
/installer/install4j8.0.5/bin/install4jc \
	-b 105 \
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
rmiHosts=$compiler_rmiHosts,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL\
	VCell.install4j

mv /outputdir/updates.xml /outputdir/updates_mac64.xml


#reconstruct combined updates.xml from fragments (used by VCell client executable to detect if update needed)
win64c=$(wc -l < /outputdir/updates_win64.xml)
win32c=$(wc -l < /outputdir/updates_win32.xml)
linux64c=$(wc -l < /outputdir/updates_linux64.xml)
linux32c=$(wc -l < /outputdir/updates_linux32.xml)
mac64c=$(wc -l < /outputdir/updates_mac64.xml)

sed -n -e "1,$(($win64c-1))p" /outputdir/updates_win64.xml >/outputdir/updates.xml
sed -n -e "3,$(($win32c-1))p" /outputdir/updates_win32.xml >/outputdir/updates.xml
sed -n -e "3,$(($linux64c-1))p" /outputdir/updates_linux64.xml >>/outputdir/updates.xml
sed -n -e "3,$(($linux32c-1))p" /outputdir/updates_linux32.xml >>/outputdir/updates.xml
sed -n -e "3,$(($mac64c))p" /outputdir/updates_mac64.xml >>/outputdir/updates.xml

