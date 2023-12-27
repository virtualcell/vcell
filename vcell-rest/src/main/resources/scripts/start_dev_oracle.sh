#!/usr/bin/env bash

# Enable error handling
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

CONTAINER_NAME=oracle-dev-container

# Define an error handling function
handle_err() {
    echo "Error occurred in script at line: $1."
    echo "Line exited with status: $2"
}

# Assign the error handling function to the ERR signal
trap 'handle_err ${LINENO} $?' ERR

# stop and remove container if it exists
if docker stop ${CONTAINER_NAME}; then
    echo "Successfully stopped ${CONTAINER_NAME} container"
else
    echo "Failed to stop ${CONTAINER_NAME} container, maybe it does not exist"
fi

if docker rm ${CONTAINER_NAME}; then
    echo "Successfully removed ${CONTAINER_NAME} container"
else
    echo "Failed to remove ${CONTAINER_NAME} container, maybe it does not exist"
fi

# run container-registry.oracle.com/database/free:latest and mount /Users/schaff/Documents/workspace/vcell/vcell-rest/src/main/resources/scripts/init-oracle.sql to /home/oracle/scripts/init-oracle.sql
docker run -d -p 1521:1521 \
      --name ${CONTAINER_NAME} \
      --env "ORACLE_PASSWORD=quarkus" \
      --env "ORACLE_DATABASE=quarkus" \
      --env "APP_USER=quarkus" \
      --env "APP_USER_PASSWORD=quarkus" \
      --env "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/oracle/product/23c/dbhomeFree/bin:/opt/oracle" \
      --env "ORACLE_BASE=/opt/oracle" \
      --env "ORACLE_BASE_CONFIG=/opt/oracle/product/23c/dbhomeFree" \
      --env "ORACLE_BASE_HOME=/opt/oracle/product/23c/dbhomeFree" \
      --env "ORACLE_HOME=/opt/oracle/product/23c/dbhomeFree" \
      --env "ORACLE_SID=FREE" \
      --env "NLS_LANG=.AL32UTF8" \
      --entrypoint "container-entrypoint.sh" \
      --detach \
      -v ${SCRIPT_DIR}/init-oracle.sql:/home/oracle/scripts/init-oracle.sql \
      gvenzl/oracle-free:23-slim-faststart
retcode=$?

if [ $retcode -eq 0 ] ; then
    echo "Successfully started ${CONTAINER_NAME} docker container"
else
    echo "Failed to start ${CONTAINER_NAME} docker container"
    exit 1
fi

# wait for container to start
echo "Waiting 15s for ${CONTAINER_NAME} container to start"
sleep 15

# run init script to create tables and bootstrap data
if docker exec -it ${CONTAINER_NAME} bash -c "source /home/oracle/.bashrc; echo exit | sqlplus system/quarkus @/home/oracle/scripts/init-oracle.sql" ; then
    echo "Successfully ran init script"
else
    echo "Failed to run the init script"
    exit 1
fi

