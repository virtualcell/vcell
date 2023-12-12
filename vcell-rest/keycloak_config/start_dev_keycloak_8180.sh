#!/usr/bin/env bash

# get script directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

#docker run --name keycloak_dev -p 8180:8080 \
#        -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
#        quay.io/keycloak/keycloak:22.0.3 \
#        start-dev

https_port=8543
http_port=8180

docker rm keycloak

docker run \
  --name keycloak \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -p ${https_port}:8443 \
  -p ${http_port}:8080 \
  -v ${DIR}/keycloak.p12:/etc/keycloak.p12 \
  -v ${DIR}/quarkus-realm.json:/opt/keycloak/data/import/quarkus-realm.json \
#  quay.io/keycloak/keycloak:22.0.3 \
  ghcr.io/jcschaff/keycloak-sha1:master \
  start  \
      --hostname-strict=false \
      --https-key-store-file=/etc/keycloak.p12 \
      --https-key-store-password=changeit \
      --import-realm
