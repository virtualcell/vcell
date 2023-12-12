#!/usr/bin/env bash

#keytool -genkeypair -alias keycloak-server \
#  -keyalg RSA -keysize 2048 -dname "CN=localhost,OU=VCell,O=VCell,L=Princeton,S=NJ,C=US" \
#  -keypass changeit -keystore keycloak-keystore.jks -storepass changeit

# Generate a self-signed certificate:
openssl req -new -newkey rsa:4096 -x509 -sha256 -days 365 -nodes -out keycloak.crt -keyout keycloak.key \
   -subj "/C=US/ST=NJ/L=Princeton/O=VCell/OU=CCAM/CN=localhost/emailAddress=schaff@uchc.edu" \
   -passin pass:changeit -passout pass:changeit

# Combine the certificate and private key into one file (PKCS12):
openssl pkcs12 -export -out keycloak.p12 -inkey keycloak.key -in keycloak.crt

# import the keycloak server certificate into the trust store
keytool -import -alias keycloak -keystore truststore.jks -file keycloak.crt -storepass changeit -noprompt

# Configure Quarkus to trust this certificate:
# https://quarkus.io/guides/security-https#configuring-the-server
# quarkus.oidc.tls.trust-store-file=keycloak_config/truststore.jks
# quarkus.oidc.tls.trust-store-password=changeit
# quarkus.oidc.tls.key-store-file=keycloak_config/keycloak.p12
# quarkus.oidc.tls.key-store-password=changeit
