#!/usr/bin/env bash

# This script is used to create a sealed secret for the database and jms passwords
# this script should take 5 arguments as input:
#   namespace
#   db_password
#   oidc_client_id
#   oidc_client_secret
#   swagger_client_id
#   swagger_client_secret
#
#   and outputs a sealed secret to stdout
# Example: ./sealed_secret_rest.sh devjim db-pass oidc-client-id oidc-secret swagger-client-id swagger-client-secret > output.yaml

# validate the number of arguments
if [ "$#" -ne 6 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_rest.sh <namespace> <db_password> <oidc_client_id> <oidc_client_secret> <swagger_client_id> <swagger_client_secret>"
    exit 1
fi

SECRET_NAME="rest-secrets"
NAMESPACE=$1
DATABASE_PASSWORD=$2
OIDC_CLIENT_ID=$3
OIDC_CLIENT_SECRET=$4
SWAGGER_CLIENT_ID=$5
SWAGGER_CLIENT_SECRET=$6


kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-literal=quarkus.datasource.password="${DATABASE_PASSWORD}" \
      --from-literal=quarkus.oidc.client-id="${OIDC_CLIENT_ID}" \
      --from-literal=quarkus.oidc.credentials.secret="${OIDC_CLIENT_SECRET}" \
      --from-literal=quarkus.swagger-ui.oauth-client-id="${SWAGGER_CLIENT_ID}" \
      --from-literal=quarkus.swagger-ui.oauth-client-secret="${SWAGGER_CLIENT_SECRET}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
