#!/usr/bin/env bash

# This script is used to create a sealed secret for the database password
# this script should take 2 arguments as input: namespace and password and output a sealed secret to stdout
# Example: ./sealed_secret_db.sh devjim pswd12345 > output.yaml

# validate the number of arguments
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_db.sh <namespace> <password>"
    exit 1
fi

SECRET_NAME="db-secret"
NAMESPACE=$1
PASSWORD=$2

#kubectl create secret generic ${SECRET_NAME} --dry-run=client \
#      --from-literal=user="${USER}" --from-literal=password="${PASSWORD}" \
#      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-literal=password="${PASSWORD}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
