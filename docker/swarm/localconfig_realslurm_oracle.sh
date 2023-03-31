#!/usr/bin/env bash

shopt -s -o nounset

if [ "$#" -ne 6 ]; then
    echo "usage: config.sh SITE (REPO/NAMESPACE | NAMESPACE) TAG VCELL_VERSION_NUMBER VCELL_BUILD_NUMBER OUTPUTFILE"
    exit 1
fi

_site=$1
_repo=$2
_tag=$3
_version_number=$4
_build_number=$5
_outputfile=$6

echo "args: | $1 | $2 | $3 | $4 | $5 | $6 |"

_site_lower=$(echo "$_site" | tr '[:upper:]' '[:lower:]')
_site_upper=$(echo "$_site" | tr '[:lower:]' '[:upper:]')
_site_camel="${_site_upper:0:1}${_site_lower:1:100}"


VCELL_SITE="${_site_upper}"
VCELL_REPO_NAMESPACE=$_repo
VCELL_TAG=$_tag
VCELL_VERSION_NUMBER=$_version_number
VCELL_BUILD_NUMBER=$_build_number
#SLURM parameter values
vcell_slurm_partition=vcell
vcell_slurm_reservation=
vcell_slurm_qos=vcell
vcell_slurm_partition_pu=vcellpu
vcell_slurm_reservation_pu=vcellpu
vcell_slurm_qos_pu=vcellpu

VCELL_API_HOST_EXTERNAL=$(hostname)
VCELL_API_PUBLICATION_HOST=$(hostname)
DATABASE_HOST=$(hostname)

case $VCELL_SITE in
	REL)
		_site_port_offset=0
		_applicationId="1471-8022-1038-5553"
		;;
	BETA)
		_site_port_offset=1
		_applicationId="1471-8022-1038-5552"
		;;
	ALPHA)
		_site_port_offset=2
		_applicationId="1471-8022-1038-5554"
		;;
	TEST)
		_site_port_offset=3
		# VCELL_API_PORT_EXTERNAL=8081
		_applicationId="1471-8022-1038-5555"
		;;
	TEST2)
		_site_port_offset=4
		_applicationId="1471-8022-1038-5556"
		;;
	TEST3)
		_site_port_offset=5
		_applicationId="1471-8022-1038-5557"
		;;
	TEST4)
		_site_port_offset=6
		_applicationId="1471-8022-1038-5558"
		;;
	TEST5)
		_site_port_offset=7
		_applicationId="1471-8022-1038-5559"
		;;
	*)
		printf 'ERROR: Unknown site: %s\n' "$1" >&2
		;;
esac

VCELL_DB_URL="jdbc:oracle:thin:@VCELL-DB.cam.uchc.edu:1521/vcelldborcl.cam.uchc.edu"
VCELL_DB_DRIVER="oracle.jdbc.driver.OracleDriver"
VCELL_DB_USER="vcell"
VCELL_DEBUG_PORT_BASE=5000
VCELL_JMS_SIM_HOST_EXTERNAL=vcell-node3.cam.uchc.edu
VCELL_MONGO_HOST_EXTERNAL=$VCELL_API_HOST_EXTERNAL
VCELL_BATCH_HOST="hpc-ext-1.cam.uchc.edu,hpc-ext-2.cam.uchc.edu,hpc-ext-3.cam.uchc.edu,hpc-ext-4.cam.uchc.edu"
#VCELL_BATCH_HOST=vcell-service.cam.uchc.edu
VCELL_SLURM_CMD_SBATCH=sbatch
VCELL_SLURM_CMD_SACCT=sacct
VCELL_SLURM_CMD_SQUEUE=squeue
VCELL_SLURM_CMD_SCANCEL=scancel
VCELL_SLURM_PARTITION=$vcell_slurm_partition
VCELL_SLURM_RESERVATION=$vcell_slurm_reservation
VCELL_SLURM_QOS=$vcell_slurm_qos
VCELL_SLURM_PARTITION_PU=$vcell_slurm_partition_pu
VCELL_SLURM_RESERVATION_PU=$vcell_slurm_reservation_pu
VCELL_SLURM_QOS_PU=$vcell_slurm_qos_pu
VCELL_SLURM_TMPDIR=/scratch/vcell
VCELL_SLURM_LOCAL_SINGULARITY_DIR=/state/partition1/singularityImages
VCELL_SLURM_CENTRAL_SINGULARITY_DIR=/share/apps/vcell3/singularityImages

#
# VCELL_API_PORT_EXTERNAL uses 443 for Beta and Rel (but on different machines/swarm clusters)
#     ALPHA, TEST, TEST2, TEST3, etc. uses ports starting with 8080
#     Note: port 8080 is currently needed by vcell.org website.
#

VCELL_API_PORT_EXTERNAL=$((8080 + _site_port_offset))
VCELL_JMS_SIM_PORT_EXTERNAL=$((61616 + _site_port_offset))
VCELL_JMS_SIM_RESTPORT_EXTERNAL=$((8161 + _site_port_offset))
VCELL_MONGO_PORT_EXTERNAL=$((27017 + _site_port_offset))
VCELL_HTC_NODELIST=
VCELL_BATCH_DOCKER_IMAGE="${VCELL_REPO_NAMESPACE}/vcell-batch:${VCELL_TAG}"
VCELL_BATCH_SINGULARITY_FILENAME="${VCELL_BATCH_DOCKER_IMAGE//[\/:]/_}.img"
VCELL_BATCH_SINGULARITY_IMAGE_EXTERNAL="/state/partition1/singularityImages/${VCELL_BATCH_SINGULARITY_FILENAME}"
VCELL_OPT_DOCKER_IMAGE="${VCELL_REPO_NAMESPACE}/vcell-opt:${VCELL_TAG}"
VCELL_OPT_SINGULARITY_FILENAME="${VCELL_OPT_DOCKER_IMAGE//[\/:]/_}.img"
VCELL_OPT_SINGULARITY_IMAGE_EXTERNAL="/state/partition1/singularityImages/${VCELL_OPT_SINGULARITY_FILENAME}"
VCELL_SMTP_HOSTNAME=vdsmtp.cam.uchc.edu
VCELL_SMTP_PORT=25
VCELL_SMTP_EMAILADDRESS=VCell_Support@uchc.edu
VCELL_EXPORT_BASEURL=http://vcell.org/export/
VCELL_EXPORTDIR_HOST=/Volumes/vcell/export/
VCELL_MAX_JOBS_PER_SCAN=100
VCELL_MAX_ODE_JOBS_PER_USER=100
VCELL_MAX_PDE_JOBS_PER_USER=40
VCELL_WEB_DATA_PORT=55556
VCELL_SSH_CMD_TIMEOUT=10000
VCELL_SSH_CMD_RESTORE_TIMEOUT=5

#
# write out the environment file to be for:
# 1. deployment actions (not applicable here because localconfig_realslurm_postgres.sh is designed for local computation on a dev machine).
# 2. runtime environment for the docker stack run command
#
cat <<EOF >"$_outputfile"
VCELL_API_HOST_EXTERNAL=$VCELL_API_HOST_EXTERNAL
VCELL_API_PORT_EXTERNAL=$VCELL_API_PORT_EXTERNAL
VCELL_API_PUBLICATION_HOST=$VCELL_API_PUBLICATION_HOST
VCELL_BATCH_DOCKER_IMAGE=$VCELL_BATCH_DOCKER_IMAGE
VCELL_BATCH_HOST=$VCELL_BATCH_HOST
VCELL_BATCH_SINGULARITY_FILENAME=$VCELL_BATCH_SINGULARITY_FILENAME
VCELL_BATCH_SINGULARITY_IMAGE_EXTERNAL=$VCELL_BATCH_SINGULARITY_IMAGE_EXTERNAL
VCELL_BATCH_USER=vcell
VCELL_BIOFORMATS_JAR_FILE=vcell-bioformats-0.0.9-jar-with-dependencies.jar
VCELL_BIOFORMATS_JAR_URL=http://vcell.org/webstart/vcell-bioformats-0.0.9-jar-with-dependencies.jar
VCELL_BUILD_NUMBER=$VCELL_BUILD_NUMBER
VCELL_CLIENT_APPID=${_applicationId}
VCELL_DB_DRIVER=$VCELL_DB_DRIVER
VCELL_DB_URL=$VCELL_DB_URL
VCELL_DB_USER=$VCELL_DB_USER
VCELL_DEBUG_PORT_VCELL_API=$((VCELL_DEBUG_PORT_BASE+1))
VCELL_DEBUG_PORT_VCELL_DATA=$((VCELL_DEBUG_PORT_BASE+2))
VCELL_DEBUG_PORT_VCELL_DB=$((VCELL_DEBUG_PORT_BASE+3))
VCELL_DEBUG_PORT_VCELL_SCHED=$((VCELL_DEBUG_PORT_BASE+4))
VCELL_DEBUG_PORT_VCELL_SUBMIT=$((VCELL_DEBUG_PORT_BASE+5))
VCELL_DEBUG_PORT_VCELL_WEB=$((VCELL_DEBUG_PORT_BASE+6))
VCELL_DEPLOY_SECRETS_DIR=${HOME}/vcellkeys_oracle
VCELL_EXPORT_BASEURL=$VCELL_EXPORT_BASEURL
VCELL_EXPORTDIR_HOST=$VCELL_EXPORTDIR_HOST
VCELL_HTC_NODELIST=$VCELL_HTC_NODELIST
VCELL_HTCLOGS_EXTERNAL=/share/apps/vcell3/dev_htclogs
VCELL_HTCLOGS_HOST=/Volumes/vcell/dev_htclogs
VCELL_INSTALLER_JRE_LINUX32=linux-x86-1.8.0_66
VCELL_INSTALLER_JRE_LINUX64=linux-amd64-1.8.0_66
VCELL_INSTALLER_JRE_MAC=macosx-amd64-1.8.0_141
VCELL_INSTALLER_JRE_WIN32=windows-x86-1.8.0_141
VCELL_INSTALLER_JRE_WIN64=windows-amd64-1.8.0_141
VCELL_INSTALLER_JREDIR=${HOME}/.install4j6/jres
VCELL_JMS_SIM_HOST_EXTERNAL=$VCELL_JMS_SIM_HOST_EXTERNAL
VCELL_JMS_SIM_PORT_EXTERNAL=$VCELL_JMS_SIM_PORT_EXTERNAL
VCELL_JMS_SIM_RESTPORT_EXTERNAL=$VCELL_JMS_SIM_RESTPORT_EXTERNAL
VCELL_MAX_JOBS_PER_SCAN=$VCELL_MAX_JOBS_PER_SCAN
VCELL_MAX_ODE_JOBS_PER_USER=$VCELL_MAX_ODE_JOBS_PER_USER
VCELL_MAX_PDE_JOBS_PER_USER=$VCELL_MAX_PDE_JOBS_PER_USER
VCELL_MONGO_HOST_EXTERNAL=$VCELL_MONGO_HOST_EXTERNAL
VCELL_MONGO_PORT_EXTERNAL=$VCELL_MONGO_PORT_EXTERNAL
VCELL_NATIVESOLVERDIR_EXTERNAL=/share/apps/vcell3/nativesolvers
VCELL_OPT_DOCKER_IMAGE=$VCELL_OPT_DOCKER_IMAGE
VCELL_OPT_SINGULARITY_FILENAME=$VCELL_OPT_SINGULARITY_FILENAME
VCELL_OPT_SINGULARITY_IMAGE_EXTERNAL=$VCELL_OPT_SINGULARITY_IMAGE_EXTERNAL
VCELL_REPO_NAMESPACE=$VCELL_REPO_NAMESPACE
VCELL_SECRETS_DIR=${HOME}/vcellkeys_oracle
VCELL_SIMDATADIR_ARCHIVE_EXTERNAL=/share/apps/vcell12/users
VCELL_SIMDATADIR_ARCHIVE_INTERNAL=/share/apps/vcell12/users
VCELL_SIMDATADIR_ARCHIVE_HOST=/Volumes/vcell-2/users
VCELL_SIMDATADIR_EXTERNAL=/share/apps/vcell3/users
VCELL_SIMDATADIR_HOST=/Volumes/vcell/users
VCELL_SIMDATADIR_SECONDARY_EXTERNAL=/share/apps/vcell10/users
VCELL_SIMDATADIR_SECONDARY_HOST=/Volumes/vcell-1/users
VCELL_SIMDATADIR_PARALLEL_EXTERNAL=/share/apps/vcell3parallel
VCELL_SITE_CAMEL=${_site_camel}
VCELL_SITE=$VCELL_SITE
VCELL_SLURM_CENTRAL_SINGULARITY_DIR=${VCELL_SLURM_CENTRAL_SINGULARITY_DIR}
VCELL_SLURM_CMD_SACCT=$VCELL_SLURM_CMD_SACCT
VCELL_SLURM_CMD_SBATCH=$VCELL_SLURM_CMD_SBATCH
VCELL_SLURM_CMD_SCANCEL=$VCELL_SLURM_CMD_SCANCEL
VCELL_SLURM_CMD_SQUEUE=$VCELL_SLURM_CMD_SQUEUE
VCELL_SLURM_LOCAL_SINGULARITY_DIR=${VCELL_SLURM_LOCAL_SINGULARITY_DIR}
VCELL_SLURM_PARTITION_PU=$VCELL_SLURM_PARTITION_PU
VCELL_SLURM_PARTITION=$VCELL_SLURM_PARTITION
VCELL_SLURM_QOS_PU=$VCELL_SLURM_QOS_PU
VCELL_SLURM_QOS=$VCELL_SLURM_QOS
VCELL_SLURM_RESERVATION_PU=$VCELL_SLURM_RESERVATION_PU
VCELL_SLURM_RESERVATION=$VCELL_SLURM_RESERVATION
VCELL_SLURM_TMPDIR=$VCELL_SLURM_TMPDIR
VCELL_SMTP_EMAILADDRESS=${VCELL_SMTP_EMAILADDRESS}
VCELL_SMTP_HOSTNAME=${VCELL_SMTP_HOSTNAME}
VCELL_SMTP_PORT=${VCELL_SMTP_PORT}
VCELL_SSH_CMD_RESTORE_TIMEOUT=$VCELL_SSH_CMD_RESTORE_TIMEOUT
VCELL_SSH_CMD_TIMEOUT=$VCELL_SSH_CMD_TIMEOUT
VCELL_SSL_IGNORE_CERT_PROBLEMS=true
VCELL_SSL_IGNORE_HOST_MISMATCH=true
VCELL_SUBMIT_SERVICE_HOST=submit
VCELL_TAG=$VCELL_TAG
VCELL_UPDATE_SITE=http://vcell.org/webstart/${_site_camel}
VCELL_VERSION_NUMBER=$VCELL_VERSION_NUMBER
VCELL_VERSION=${_site_camel}_Version_${VCELL_VERSION_NUMBER}_build_${VCELL_BUILD_NUMBER}
VCELL_WEB_DATA_PORT=$VCELL_WEB_DATA_PORT
EOF
