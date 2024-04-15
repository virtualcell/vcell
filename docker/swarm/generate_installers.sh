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


mkdir -p ./generated_installers
# remove old installers
if [ -e "./generated_installers" ]; then
	cmd="rm -f ./generated_installers/*"
	echo "$cmd"
	$cmd
fi


local_config_file=$1
echo "reading configuration from $local_config_file"

VCELL_SITE_CAMEL=$(grep VCELL_SITE_CAMEL "$local_config_file" | cut -d"=" -f2)
VCELL_VERSION_NUMBER=$(grep VCELL_VERSION_NUMBER "$local_config_file" | cut -d"=" -f2)
VCELL_BUILD_NUMBER=$(grep VCELL_BUILD_NUMBER "$local_config_file" | cut -d"=" -f2)
VCELL_VERSION=$(grep VCELL_VERSION\= "$local_config_file" | cut -d"=" -f2)

VCELL_UPDATE_SITE=$(grep VCELL_UPDATE_SITE "$local_config_file" | cut -d"=" -f2)
VCELL_API_HOST_EXTERNAL=$(grep VCELL_API_HOST_EXTERNAL "$local_config_file" | cut -d"=" -f2)
VCELL_API_PORT_EXTERNAL=$(grep VCELL_API_PORT_EXTERNAL "$local_config_file" | cut -d"=" -f2)
VCELL_API_PREFIX_V0=$(grep VCELL_API_PREFIX_V0 "$local_config_file" | cut -d"=" -f2)
VCELL_API_PREFIX_V1=$(grep VCELL_API_PREFIX_V1 "$local_config_file" | cut -d"=" -f2)
VCELL_BIOFORMATS_JAR_FILE=$(grep VCELL_BIOFORMATS_JAR_FILE "$local_config_file" | cut -d"=" -f2)
VCELL_BIOFORMATS_JAR_URL=$(grep VCELL_BIOFORMATS_JAR_URL "$local_config_file" | cut -d"=" -f2)
VCELL_CLIENT_APPID=$(grep VCELL_CLIENT_APPID "$local_config_file" | cut -d"=" -f2)

VCELL_REPO_NAMESPACE=$(grep VCELL_REPO_NAMESPACE "$local_config_file" | cut -d"=" -f2)
VCELL_TAG=$(grep VCELL_TAG "$local_config_file" | cut -d"=" -f2)

VCELL_DEPLOY_SECRETS_DIR=$(grep VCELL_DEPLOY_SECRETS_DIR "$local_config_file" | cut -d"=" -f2)

echo "sudo docker run --rm ... ${VCELL_REPO_NAMESPACE}/vcell-clientgen:${VCELL_TAG}"

echo "sudo docker run --rm --cpus=\"1.0\"\\"
echo "    -e compiler_updateSiteBaseUrl=$VCELL_UPDATE_SITE \\"
echo "    -e compiler_Site=$VCELL_SITE_CAMEL \\"
echo "    -e compiler_vcellVersion=$VCELL_VERSION_NUMBER \\"
echo "    -e compiler_vcellBuild=$VCELL_BUILD_NUMBER \\"
echo "    -e compiler_softwareVersionString=$VCELL_VERSION \\"
echo "    -e compiler_rmiHosts=${VCELL_API_HOST_EXTERNAL}:$VCELL_API_PORT_EXTERNAL \\"
echo "    -e compiler_serverPrefixV0=$VCELL_API_PREFIX_V0 \\"
echo "    -e compiler_serverPrefixV1=$VCELL_API_PREFIX_V1 \\"
echo "    -e compiler_bioformatsJarFile=$VCELL_BIOFORMATS_JAR_FILE \\"
echo "    -e compiler_bioformatsJarDownloadURL=$VCELL_BIOFORMATS_JAR_URL \\"
echo "    -e compiler_applicationId=$VCELL_CLIENT_APPID \\"
echo "    -e winCodeSignKeystore_pfx=/buildsecrets/VCELL_UCONN_MS_2017.pfx \\"
echo "    -e winCodeSignKeystore_pswdfile=/buildsecrets/VCELL_UCONN_MS_2017_pswd.txt \\"
echo "    -e macCodeSignKeystore_p12=/buildsecrets/VCELL_APPLE_2015.p12 \\"
echo "    -e macCodeSignKeystore_pswdfile=/buildsecrets/VCELL_APPLE_2015_pswd.txt \\"
echo "    -e Install4J_product_key_file=/buildsecrets/Install4J_product_key_10.txt \\"
echo "    -v $PWD/generated_installers:/outputdir \\"
echo "    -v ${VCELL_DEPLOY_SECRETS_DIR}:/buildsecrets \\"
echo "    ${VCELL_REPO_NAMESPACE}/vcell-clientgen:${VCELL_TAG}"



if ! sudo docker run --rm --cpus="1.0" \
    -e compiler_updateSiteBaseUrl="$VCELL_UPDATE_SITE" \
    -e compiler_Site="$VCELL_SITE_CAMEL" \
    -e compiler_vcellVersion="$VCELL_VERSION_NUMBER" \
    -e compiler_vcellBuild="$VCELL_BUILD_NUMBER" \
    -e compiler_softwareVersionString="$VCELL_VERSION" \
    -e compiler_rmiHosts="${VCELL_API_HOST_EXTERNAL}:$VCELL_API_PORT_EXTERNAL" \
    -e compiler_serverPrefixV0="$VCELL_API_PREFIX_V0" \
    -e compiler_serverPrefixV1="$VCELL_API_PREFIX_V1" \
    -e compiler_bioformatsJarFile="$VCELL_BIOFORMATS_JAR_FILE" \
    -e compiler_bioformatsJarDownloadURL="$VCELL_BIOFORMATS_JAR_URL" \
    -e compiler_applicationId="$VCELL_CLIENT_APPID" \
    -e winCodeSignKeystore_pfx=/buildsecrets/VCELL_UCONN_MS_2017.pfx \
    -e winCodeSignKeystore_pswdfile=/buildsecrets/VCELL_UCONN_MS_2017_pswd.txt \
    -e macCodeSignKeystore_p12=/buildsecrets/VCELL_APPLE_2015.p12 \
    -e macCodeSignKeystore_pswdfile=/buildsecrets/VCELL_APPLE_2015_pswd.txt \
    -e Install4J_product_key_file=/buildsecrets/Install4J_product_key_10.txt \
    -v "$PWD"/generated_installers:/outputdir \
    -v "${VCELL_DEPLOY_SECRETS_DIR}":/buildsecrets \
    "${VCELL_REPO_NAMESPACE}/vcell-clientgen":"${VCELL_TAG}";
then
    echo "docker run failed while generating clients"
    exit 1
fi
