#!/usr/bin/env bash

# This script is used to create a sealed secret for the database and jms passwords
# this script should take 3 arguments as input: namespace, db_password and jms_password and output a sealed secret to stdout
# Example: ./sealed_secret_api.sh devjim pswd12345 pswd39393 > output.yaml

# validate the number of arguments
if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_db.sh <namespace> <db_password> <jms_password>"
    exit 1
fi

SECRET_NAME="api-secrets"
NAMESPACE=$1
DATABASE_PASSWORD=$2
JMS_PASSWORD=$3

kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-literal=dbpswd="${DATABASE_PASSWORD}" \
      --from-literal=jmspswd="${JMS_PASSWORD}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
