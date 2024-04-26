#!/usr/bin/env bash

# Enable error handling
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

CONTAINER_NAME=postgresql-dev-container

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

# run postgresql container and mount /Users/schaff/Documents/workspace/vcell/vcell-rest/src/main/resources/scripts/init-postgresql.sql to /docker-entrypoint-initdb.d/init-postgresql.sql
docker run -p 5432:5432 \
      --detach \
      --name ${CONTAINER_NAME} \
      --env "POSTGRES_PASSWORD=quarkus" \
      --env "POSTGRES_DB=postgres" \
      --env "POSTGRES_USER=quarkus" \
      -v "${SCRIPT_DIR}"/init.sql:/docker-entrypoint-initdb.d/init.sql \
      postgres:14.2
retcode=$?

if [ $retcode -eq 0 ] ; then
    echo "Successfully started ${CONTAINER_NAME} docker container"
else
    echo "Failed to start ${CONTAINER_NAME} docker container"
    exit 1
fi
