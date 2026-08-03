#!/usr/bin/env bash

#
# build_installers.sh must be run within Docker (see Dockerfile and docker-compose.yml in /vcell/docker/installers)
#

shopt -s -o nounset

# gather classpath (filenames only), Install4J will add the correct separator
compiler_vcellClasspathColonSep=`ls -m /vcellclient/vcell-client/target/maven-jars | tr -d '[:space:]' | tr ',' ':'`

cd /config

PATH=/installer/install4j10.0.5/bin:$PATH
INSTALL4JC=/installer/install4j10.0.5/bin/install4jc

#
# retrieve text secrets from file (docker-compose 'secrets' mounts these files in memory at runtime)
#
Install4J_product_key=`cat $Install4J_product_key_file`
winCodeSignKeystore_pswd=`cat $winCodeSignKeystore_pswdfile`
macCodeSignKeystore_pswd=`cat $macCodeSignKeystore_pswdfile`

#
# install Install4J product key (only really needed first time when running this Docker container)
#
$INSTALL4JC -L $Install4J_product_key

#
# Which install4j media (build ids) to generate. Defaults to all five supported platforms.
# Set BUILD_IDS to a subset (e.g. "349") to build a single installer per invocation, so the
# CD-sites workflow can fan the platforms out across parallel runners/containers. Each media is a
# separate install4jc process — the historical reason the builds were kept separate was to avoid
# random threading failures when building them concurrently in one install4j run.
#
BUILD_IDS="${BUILD_IDS:-349 450 652 547 105}"

# map install4j build id -> platform suffix used in the updates_<suffix>.xml fragment name
media_suffix() {
	case "$1" in
		349) echo win64   ;;   # Windows 64-bit
		450) echo win32   ;;   # Windows 32-bit
		652) echo linux64 ;;   # Linux 64-bit
		547) echo linux32 ;;   # Linux 32-bit
		105) echo mac64   ;;   # macOS 64-bit
		*)   echo "" ;;
	esac
}

for build_id in $BUILD_IDS; do
	suffix=$(media_suffix "$build_id")
	if [ -z "$suffix" ]; then
		echo "build_installers.sh: unknown install4j build id '$build_id'" >&2
		exit 1
	fi

	echo "Generating installer for build id $build_id ($suffix)"
	$INSTALL4JC \
		-b "$build_id" \
		--win-keystore-password=$winCodeSignKeystore_pswd \
		--mac-keystore-password=$macCodeSignKeystore_pswd \
		-D \
vcellIcnsFile=/config/icons/vcell.icns,\
outputDir=/outputdir,\
mavenRootDir=/vcellclient,\
macKeystore=$macCodeSignKeystore_p12,\
winKeystore=$winCodeSignKeystore_pfx,\
applicationId=$compiler_applicationId,\
SoftwareVersionString=$compiler_softwareVersionString,\
Site=$compiler_Site,\
vcellVersion=$compiler_vcellVersion,\
vcellBuild=$compiler_vcellBuild,\
updateSiteBaseUrl=$compiler_updateSiteBaseUrl,\
rmiHosts=$compiler_rmiHosts,\
serverPrefixV0=$compiler_serverPrefixV0,\
serverPrefixV1=$compiler_serverPrefixV1,\
bioformatsJarFile=$compiler_bioformatsJarFile,\
bioformatsJarDownloadURL=$compiler_bioformatsJarDownloadURL,\
vcellClasspathColonSep=$compiler_vcellClasspathColonSep\
		VCell.install4j

	mv /outputdir/updates.xml "/outputdir/updates_${suffix}.xml"
done

#
# Reconstruct the combined updates.xml (used by the VCell client to detect if an update is needed)
# only when all five per-platform fragments are present, i.e. a full single-container build. When
# BUILD_IDS is a subset (parallel/sharded build), the fan-in CD job runs combine_updates.sh after
# gathering every platform's fragment.
#
if [ -f /outputdir/updates_win64.xml ]   && [ -f /outputdir/updates_win32.xml ] && \
   [ -f /outputdir/updates_linux64.xml ] && [ -f /outputdir/updates_linux32.xml ] && \
   [ -f /outputdir/updates_mac64.xml ]; then
	/config/combine_updates.sh /outputdir
fi
