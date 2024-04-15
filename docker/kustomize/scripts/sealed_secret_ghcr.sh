#!/usr/bin/env bash

# This script is used to create a sealed secret for the ghcr.io credentials
# this script should take 3 arguments as input: namespace, github username, github user email, and github token and output a sealed secret to stdout
# Example: ./sealed_secret_ghcr.sh devjim GH_USERNAME GH_USER_EMAIL GH_PAT > output.yaml

# validate the number of arguments
if [ "$#" -ne 4 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_ghcr.sh <namespace> <github_user> <github_user_email> <github_token>"
    exit 1
fi

SECRET_NAME="ghcr-secret"
SERVER="ghcr.io"
NAMESPACE=$1
USERNAME=$2
EMAIL=$3
PASSWORD=$4

kubectl create secret docker-registry ${SECRET_NAME} --dry-run=client \
      --docker-server="${SERVER}" \
      --docker-username="${USERNAME}" \
      --docker-email="${EMAIL}" \
      --docker-password="${PASSWORD}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
