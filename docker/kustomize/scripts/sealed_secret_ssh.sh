#!/usr/bin/env bash

# This script is used to create a sealed secret for the vcell ssh key used to interact with Slurm for HPC jobs.
# this script should take 3 arguments as input:
#   namespace
#   priv_key_file
#   pub_key_file
#
#   and outputs a sealed secret to stdout
# Example: ./sealed_secret_api.sh devjim /path/to/vcell_rsa /path/to/vcell_rsa.pub > output.yaml

# validate the number of arguments
if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ./sealed_secret_ssh.sh <namespace> <priv_key_file> <pub_key_file>"
    exit 1
fi

SECRET_NAME="vcell-ssh-secret"
NAMESPACE=$1
PRIV_KEY_FILE=$2
PUB_KEY_FILE=$3

kubectl create secret generic ${SECRET_NAME} --dry-run=client \
      --from-file=ssh-privatekey="${PRIV_KEY_FILE}" \
      --from-file=ssh-publickey="${PUB_KEY_FILE}" \
      --namespace="${NAMESPACE}" -o yaml | kubeseal --format yaml
