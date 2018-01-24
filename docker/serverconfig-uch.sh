#!/usr/bin/env bash

shopt -s -o nounset

if [ "$#" -ne 5 ]; then
    echo "usage: config.sh SITE TAG VCELL_VERSION_NUMBER VCELL_BUILD_NUMBER OUTPUTFILE"
    exit -1
fi

_site=$1
_tag=$2
_version_number=$3
_build_number=$4
_outputfile=$5

_site_lower=`echo $_site | tr '[:upper:]' '[:lower:]'`
_site_upper=`echo $_site | tr '[:lower:]' '[:upper:]'`
_site_camel="${_site_upper:0:1}${_site_lower:1:100}"


VCELL_SITE="${_site_upper}"
VCELL_TAG=$_tag

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


cat <<EOF >$_outputfile
VCELL_SITE=$VCELL_SITE
VCELL_TAG=$VCELL_TAG
VCELL_VERSION=VCell_${VCELL_TAG}
VCELL_APIHOST=vcellapi.cam.uchc.edu
VCELL_APIPORT=$((8080 + $_site_port_offset))
VCELL_JMSPORT=$((61616 + $_site_port_offset))
VCELL_JMSRESTPORT=$((8161 + $_site_port_offset))
VCELL_MONGOPORT=$((27017 + $_site_port_offset))
VCELL_CLIENT_APPID=${_applicationId}
VCELL_INSTALLER_SCP_DESTINATION=vcell@apache.cam.uchc.edu:/apache_webroot/htdocs/webstart/${_site_camel}
VCELL_HTCLOGS_EXTERNAL=/share/apps/vcell3/htclogs
VCELL_SINGULARITY_EXTERNAL=/share/apps/vcell3/singularity
VCELL_SIMDATADIR_EXTERNAL=/share/apps/vcell3/users
VCELL_SIMDATADIR_HOST=/opt/vcelldata/users
VCELL_SECRETS_DIR=/usr/local/deploy
VCELL_SITE_CAMEL=${_site_camel}
VCELL_UPDATE_SITE=http://vcell.org/webstart/${_site_camel}
VCELL_VERSION_NUMBER=$_version_number
VCELL_BUILD_NUMBER=$_build_number
BIOFORMATS_JAR_FILE=vcell-bioformats-0.0.3-SNAPSHOT-jar-with-dependencies.jar
BIOFORMATS_JAR_URL=http://vcell.org/webstart/vcell-bioformats-0.0.3-SNAPSHOT-jar-with-dependencies.jar
INSTALLER_JRE_MAC=macosx-amd64-1.8.0_141
INSTALLER_JRE_WIN64=windows-amd64-1.8.0_141
INSTALLER_JRE_WIN32=windows-x86-1.8.0_141
INSTALLER_JRE_LINUX64=linux-amd64-1.8.0_66
INSTALLER_JRE_LINUX32=linux-x86-1.8.0_66
EOF

