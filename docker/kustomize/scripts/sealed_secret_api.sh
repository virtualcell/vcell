#!/usr/bin/env bash

# This script is used to create a sealed secret for the database and jms passwords
# this script should take 5 arguments as input:
#   namespace
#   db_password
#   jms_password
#   mongo_user
#   mongo_pswd
#
#   and outputs a sealed secret to stdout
# Example: ./sealed_secret_api.sh devjim pswd12345 pswd39393 mongo_user pswd292929 > output.yaml

# validate the number of arguments
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_db.sh <namespace> <db_password> <jms_password> <mongo_user> <mongo_pswd>"
    exit 1
fi

SECRET_NAME="api-secrets"
NAMESPACE=$1
DATABASE_PASSWORD=$2
JMS_PASSWORD=$3
MONGO_USERNAME=$4
MONGO_PASSWORD=$5

kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-literal=dbpswd="${DATABASE_PASSWORD}" \
      --from-literal=jmspswd="${JMS_PASSWORD}" \
      --from-literal=mongo-username="${MONGO_USERNAME}" \
      --from-literal=mongo-password="${MONGO_PASSWORD}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
