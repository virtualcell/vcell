#!/usr/bin/env bash

shopt -s -o nounset

if [ "$#" -ne 6 ]; then
    echo "usage: config.sh SITE REPO TAG VCELL_VERSION_NUMBER VCELL_BUILD_NUMBER OUTPUTFILE"
    exit -1
fi

_site=$1
_repo=$2
_tag=$3
_version_number=$4
_build_number=$5
_outputfile=$6

_site_lower=`echo $_site | tr '[:upper:]' '[:lower:]'`
_site_upper=`echo $_site | tr '[:lower:]' '[:upper:]'`
_site_camel="${_site_upper:0:1}${_site_lower:1:100}"


VCELL_SITE="${_site_upper}"
VCELL_REPO=$_repo
VCELL_TAG=$_tag
VCELL_VERSION_NUMBER=$_version_number
VCELL_BUILD_NUMBER=$_build_number

case $VCELL_SITE in
	REL)
		_site_port_offset=0
		_applicationId="0000-0000-0000-0000"
		;;
	BETA)
		_site_port_offset=1
		_applicationId="0000-0000-0000-0000"
		;;
	ALPHA)
		_site_port_offset=2
		_applicationId="0000-0000-0000-0000"
		;;
	TEST)
		_site_port_offset=3
		_applicationId="1471-8022-1038-5555"
		;;
	TEST2)
		_site_port_offset=4
		_applicationId="0000-0000-0000-0000"
		;;
	*)
		printf 'ERROR: Unknown site: %s\n' "$1" >&2
		;;
esac

VCELL_API_HOST_EXTERNAL=`hostname`
VCELL_JMS_HOST_EXTERNAL=`hostname`
VCELL_MONGO_HOST_EXTERNAL=`hostname`
VCELL_INSTALLER_SCP_DESTINATION=`hostname`:/Volumes/vcell/installers
VCELL_BATCH_HOST=vcell-service.cam.uchc.edu

VCELL_API_PORT_EXTERNAL=$((8080 + $_site_port_offset))
VCELL_JMS_PORT_EXTERNAL=$((61616 + $_site_port_offset))
VCELL_JMS_URL_EXTERNAL='failover:(tcp://$VCELL_JMS_HOST_EXTERNAL:$VCELL_JMS_PORT_EXTERNAL)'
VCELL_JMS_RESTPORT_EXTERNAL=$((8161 + $_site_port_offset))
VCELL_MONGO_PORT_EXTERNAL=$((27017 + $_site_port_offset))
VCELL_HTC_NODELIST=xanadu-27

cat <<EOF >$_outputfile
VCELL_SITE=$VCELL_SITE
VCELL_REPO=$VCELL_REPO
VCELL_TAG=$VCELL_TAG
VCELL_VERSION_NUMBER=$VCELL_VERSION_NUMBER
VCELL_BUILD_NUMBER=$VCELL_BUILD_NUMBER
VCELL_VERSION=VCell_${VCELL_VERSION_NUMBER}_build_${VCELL_BUILD_NUMBER}
VCELL_API_HOST_EXTERNAL=$VCELL_API_HOST_EXTERNAL
VCELL_API_PORT_EXTERNAL=$VCELL_API_PORT_EXTERNAL
VCELL_JMS_HOST_EXTERNAL=$VCELL_JMS_HOST_EXTERNAL
VCELL_JMS_PORT_EXTERNAL=$VCELL_JMS_PORT_EXTERNAL
VCELL_JMS_URL_EXTERNAL=$VCELL_JMS_URL_EXTERNAL
VCELL_JMS_RESTPORT_EXTERNAL=$VCELL_JMS_RESTPORT_EXTERNAL
VCELL_MONGO_HOST_EXTERNAL=$VCELL_MONGO_HOST_EXTERNAL
VCELL_MONGO_PORT_EXTERNAL=$VCELL_MONGO_PORT_EXTERNAL
VCELL_BATCH_HOST=$VCELL_BATCH_HOST
VCELL_BATCH_USER=vcell
VCELL_CLIENT_APPID=${_applicationId}
VCELL_HTCLOGS_EXTERNAL=/share/apps/vcell3/htclogs
VCELL_HTC_NODELIST=$VCELL_HTC_NODELIST
VCELL_SINGULARITY_EXTERNAL=/share/apps/vcell3/singularity
VCELL_SIMDATADIR_EXTERNAL=/share/apps/vcell3/users
VCELL_SIMDATADIR_HOST=/Volumes/vcell/users
VCELL_SECRETS_DIR=/Users/schaff/vcellkeys
VCELL_DEPLOY_SECRETS_DIR=/Users/schaff/vcellkeys
VCELL_SITE_CAMEL=${_site_camel}
VCELL_UPDATE_SITE=http://vcell.org/webstart/${_site_camel}
BIOFORMATS_JAR_FILE=vcell-bioformats-0.0.3-SNAPSHOT-jar-with-dependencies.jar
BIOFORMATS_JAR_URL=http://vcell.org/webstart/vcell-bioformats-0.0.3-SNAPSHOT-jar-with-dependencies.jar
INSTALLER_JRE_MAC=macosx-amd64-1.8.0_141
INSTALLER_JRE_WIN64=windows-amd64-1.8.0_141
INSTALLER_JRE_WIN32=windows-x86-1.8.0_141
INSTALLER_JRE_LINUX64=linux-amd64-1.8.0_66
INSTALLER_JRE_LINUX32=linux-x86-1.8.0_66
EOF

