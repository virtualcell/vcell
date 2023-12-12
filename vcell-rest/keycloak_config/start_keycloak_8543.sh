#!/usr/bin/env bash

# get script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# get hostname
host=$(hostname -f)
https_port=8543
admin_password=password # <<change this before deploying to production>>

key_dir="${DIR}/.." # key_dir is one level up from this script

keystore_file="${key_dir}/${host}.jks"
keystore_password=$(cat "${key_dir}/${host}-keystorepswd.txt")

docker rm keycloak || true

echo docker run \
  --name keycloak \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=${admin_password} \
  -p "${https_port}:8443" \
  -v "${keystore_file}:/etc/keycloak.jks" \
  -v "${DIR}/quarkus-realm.json:/opt/keycloak/data/import/quarkus-realm.json" \
  quay.io/keycloak/keycloak:22.0.3 \
  start  \
      --hostname-strict=false \
      --https-key-store-file=/etc/keycloak.p12 \
      --https-key-store-password="${keystore_password}" \
      --import-realm
