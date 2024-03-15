#!/usr/bin/env bash

# This script is used to create a sealed secret for the ghcr.io credentials
# this script should take 3 arguments as input: namespace, github username, and github password/token and output a sealed secret to stdout
# Example: ./sealed_secret_ghcr.sh devjim GH_USERNAME GH_PAT > output.yaml

# validate the number of arguments
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_jms.sh <namespace> <password>"
    exit 1
fi

SECRET_NAME="ghcr-secret"
SERVER="ghcr.io"
NAMESPACE=$1
USERNAME=$2
PASSWORD=$3

kubectl create secret docker-registry ${SECRET_NAME} --dry-run=client \
      --from-literal=password="${PASSWORD}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
