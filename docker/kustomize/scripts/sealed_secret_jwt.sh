#!/usr/bin/env bash

# This script is used to generate a key pair and corresponding sealed secret to sign legacy vcell-api jwt tokens.
# this script should take 1 arguments as input:
#   namespace
#
#   and outputs a sealed secret to stdout
# Example: ./sealed_secret_api.sh devjim > output.yaml

# validate the number of arguments
if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_ssh.sh <namespace>"
    exit 1
fi

SECRET_NAME="jwt-secret"
NAMESPACE=$1

TMP_FILE_PREFIX=$(date +%s)
PRIV_KEY_FILE_NAME="${TMP_FILE_PREFIX}_vcell_rsa.pem"
PUB_KEY_FILE_NAME="${TMP_FILE_PREFIX}_vcell_rsa.pub.pem"

# create a new RSA key pair stored in .pem file format
openssl genrsa -out $PRIV_KEY_FILE_NAME 4096
openssl rsa -pubout -in $PRIV_KEY_FILE_NAME -out $PUB_KEY_FILE_NAME

kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-file=apiprivkey="${PRIV_KEY_FILE_NAME}" \
      --from-file=apipubkey="${PUB_KEY_FILE_NAME}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml

# remove the temporary files
rm $PRIV_KEY_FILE_NAME $PUB_KEY_FILE_NAME
