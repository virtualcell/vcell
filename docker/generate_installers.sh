#!/usr/bin/env bash

shopt -s -o nounset

show_help() {
  echo "Generates client installers for multiple platforms "
  echo "   runs vcell-clientgen Docker container from VCell Build "
  echo "   containing all vcell artifacts and Install4J software"
  echo ""
  echo "usage: generate_clients.sh [OPTIONS] config-file"
  echo ""
  echo "    config-file           config file for deployment containing all vcell client deployment settings"
  echo ""
  echo "  [OPTIONS]"
  echo "    -h | --help           show this message"
  echo ""
  exit 1
}

if [[ $# -lt 1 ]] ; then
    show_help
fi

while :; do
  case $1 in
    -h|--help)
      show_help
      exit
      ;;
    -?*)
      printf 'ERROR: Unknown option: %s\n' "$1" >&2
      echo ""
      show_help
      ;;
    *)               # Default case: No more options, so break out of the loop.
      break
  esac
  shift
done

if [[ $# -lt 1 ]] ; then
    show_help
fi

local_config_file=$1
echo "reading configuration from $local_config_file"

VCELL_SITE_CAMEL=`cat $local_config_file | grep VCELL_SITE_CAMEL | cut -d"=" -f2`
VCELL_VERSION_NUMBER=`cat $local_config_file | grep VCELL_VERSION_NUMBER | cut -d"=" -f2`
VCELL_BUILD_NUMBER=`cat $local_config_file | grep VCELL_BUILD_NUMBER | cut -d"=" -f2`
VCELL_VERSION=`cat $local_config_file | grep VCELL_VERSION\= | cut -d"=" -f2`

VCELL_UPDATE_SITE=`cat $local_config_file | grep VCELL_UPDATE_SITE | cut -d"=" -f2`
VCELL_API_HOST_EXTERNAL=`cat $local_config_file | grep VCELL_API_HOST_EXTERNAL | cut -d"=" -f2`
VCELL_API_PORT_EXTERNAL=`cat $local_config_file | grep VCELL_API_PORT_EXTERNAL | cut -d"=" -f2`
VCELL_BIOFORMATS_JAR_FILE=`cat $local_config_file | grep VCELL_BIOFORMATS_JAR_FILE | cut -d"=" -f2`
VCELL_BIOFORMATS_JAR_URL=`cat $local_config_file | grep VCELL_BIOFORMATS_JAR_URL | cut -d"=" -f2`
VCELL_CLIENT_APPID=`cat $local_config_file | grep VCELL_CLIENT_APPID | cut -d"=" -f2`

VCELL_INSTALLER_JRE_MAC=`cat $local_config_file | grep VCELL_INSTALLER_JRE_MAC | cut -d"=" -f2`
VCELL_INSTALLER_JRE_WIN64=`cat $local_config_file | grep VCELL_INSTALLER_JRE_WIN64 | cut -d"=" -f2`
VCELL_INSTALLER_JRE_WIN32=`cat $local_config_file | grep VCELL_INSTALLER_JRE_WIN32 | cut -d"=" -f2`
VCELL_INSTALLER_JRE_LINUX64=`cat $local_config_file | grep VCELL_INSTALLER_JRE_LINUX64 | cut -d"=" -f2`
VCELL_INSTALLER_JRE_LINUX32=`cat $local_config_file | grep VCELL_INSTALLER_JRE_LINUX32 | cut -d"=" -f2`

VCELL_REPO_NAMESPACE=`cat $local_config_file | grep VCELL_REPO_NAMESPACE | cut -d"=" -f2`
VCELL_TAG=`cat $local_config_file | grep VCELL_TAG | cut -d"=" -f2`

VCELL_DEPLOY_SECRETS_DIR=`cat $local_config_file | grep VCELL_DEPLOY_SECRETS_DIR | cut -d"=" -f2`

echo "sudo docker run --rm ... ${VCELL_REPO_NAMESPACE}/vcell-clientgen:${VCELL_TAG}"

echo "sudo docker run --rm \\"
echo "    -e compiler_updateSiteBaseUrl=$VCELL_UPDATE_SITE \\"
echo "    -e compiler_Site=$VCELL_SITE_CAMEL \\"
echo "    -e compiler_vcellVersion=$VCELL_VERSION_NUMBER \\"
echo "    -e compiler_vcellBuild=$VCELL_BUILD_NUMBER \\"
echo "    -e compiler_softwareVersionString=$VCELL_VERSION \\"
echo "    -e compiler_rmiHosts=${VCELL_API_HOST_EXTERNAL}:$VCELL_API_PORT_EXTERNAL \\"
echo "    -e compiler_bioformatsJarFile=$VCELL_BIOFORMATS_JAR_FILE \\"
echo "    -e compiler_bioformatsJarDownloadURL=$VCELL_BIOFORMATS_JAR_URL \\"
echo "    -e compiler_applicationId=$VCELL_CLIENT_APPID \\"
echo "    -e macJre=$VCELL_INSTALLER_JRE_MAC \\"
echo "    -e win64Jre=$VCELL_INSTALLER_JRE_WIN64 \\"
echo "    -e win32Jre=$VCELL_INSTALLER_JRE_WIN32 \\"
echo "    -e linux64Jre=$VCELL_INSTALLER_JRE_LINUX64 \\"
echo "    -e linux32Jre=$VCELL_INSTALLER_JRE_LINUX32 \\"
echo "    -e winCodeSignKeystore_pfx=/buildsecrets/VCELL_UCONN_MS_2017.pfx \\"
echo "    -e winCodeSignKeystore_pswdfile=/buildsecrets/VCELL_UCONN_MS_2017_pswd.txt \\"
echo "    -e macCodeSignKeystore_p12=/buildsecrets/VCELL_APPLE_2015.p12 \\"
echo "    -e macCodeSignKeystore_pswdfile=/buildsecrets/VCELL_APPLE_2015_pswd.txt \\"
echo "    -e Install4J_product_key_file=/buildsecrets/Install4J_product_key.txt \\"
echo "    -v $HOME/.install4j6/jres:/jres \\"
echo "    -v $PWD/generated_installers:/outputdir \\"
echo "    -v ${VCELL_DEPLOY_SECRETS_DIR}:/buildsecrets \\"
echo "    ${VCELL_REPO_NAMESPACE}/vcell-clientgen:${VCELL_TAG}"



sudo docker run --rm \
    -e compiler_updateSiteBaseUrl=$VCELL_UPDATE_SITE \
    -e compiler_Site=$VCELL_SITE_CAMEL \
    -e compiler_vcellVersion=$VCELL_VERSION_NUMBER \
    -e compiler_vcellBuild=$VCELL_BUILD_NUMBER \
    -e compiler_softwareVersionString=$VCELL_VERSION \
    -e compiler_rmiHosts=${VCELL_API_HOST_EXTERNAL}:$VCELL_API_PORT_EXTERNAL \
    -e compiler_bioformatsJarFile=$VCELL_BIOFORMATS_JAR_FILE \
    -e compiler_bioformatsJarDownloadURL=$VCELL_BIOFORMATS_JAR_URL \
    -e compiler_applicationId=$VCELL_CLIENT_APPID \
    -e macJre=$VCELL_INSTALLER_JRE_MAC \
    -e win64Jre=$VCELL_INSTALLER_JRE_WIN64 \
    -e win32Jre=$VCELL_INSTALLER_JRE_WIN32 \
    -e linux64Jre=$VCELL_INSTALLER_JRE_LINUX64 \
    -e linux32Jre=$VCELL_INSTALLER_JRE_LINUX32 \
    -e winCodeSignKeystore_pfx=/buildsecrets/VCELL_UCONN_MS_2017.pfx \
    -e winCodeSignKeystore_pswdfile=/buildsecrets/VCELL_UCONN_MS_2017_pswd.txt \
    -e macCodeSignKeystore_p12=/buildsecrets/VCELL_APPLE_2015.p12 \
    -e macCodeSignKeystore_pswdfile=/buildsecrets/VCELL_APPLE_2015_pswd.txt \
    -e Install4J_product_key_file=/buildsecrets/Install4J_product_key.txt \
    -v $HOME/.install4j6/jres:/jres \
    -v $PWD/generated_installers:/outputdir \
    -v ${VCELL_DEPLOY_SECRETS_DIR}:/buildsecrets \
    ${VCELL_REPO_NAMESPACE}/vcell-clientgen:${VCELL_TAG}
    
if [[ $? -ne 0 ]]; then
    echo "docker run failed while generating clients"
    exit 1
fi
