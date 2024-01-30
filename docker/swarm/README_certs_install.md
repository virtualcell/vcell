# Install TLS Certs for vcell-api service

## Contents
* [Background](#background)
  * [Installation in Dockerfiles and Docker Compose files](#installation-in-dockerfiles-and-docker-compose-files)
  * [Installation in Swarm Stack](#installation-in-swarm-stack)
* [Procedure](#procedure)
  * [1. Set up environment variables](#1-set-up-environment-variables)
  * [2. Export cert and key as pkcs12 file](#2-export-cert-and-key-as-pkcs12-file)
  * [3. Import pkcs12 file into JKS keystore and backup old JKS keystore](#3-import-pkcs12-file-into-jks-keystore-and-backup-old-jks-keystore)
  * [4. Copy keystore to other nodes (vcell-node5, vcell-node6)](#4-copy-keystore-to-other-nodes-vcell-node5-vcell-node6)
  * [5. Set up symbolic links to new keystore and keystore password files](#5-set-up-symbolic-links-to-new-keystore-and-keystore-password-files)
  * [6. Shutdown, remove and reinstall stack to remove/replace secrets](#6-shutdown-remove-and-reinstall-stack-to-removereplace-secrets)


## Background
The TLS keystore and keystore password are treated as docker swarm secrets, so they are each mapped to a file within the containers.
### Installation in Dockerfiles and Docker Compose files
* The `keystore` and `keystorepswdfile` are mapped to the secrets in [Dockerfile-api-dev](../build/Dockerfile-api-dev) file.
  ```
  ENV keystore=/run/secrets/keystorefile \  
      keystorepswdfile=/run/secrets/keystorepswd  
  ```
* The `keystorefile` and `keystorepswd` secrets are defined in [docker-compose.yml](docker-compose.yml) file.
   ```
   secrets:
      storepswd:  
         file: ${VCELL_SECRETS_DIR}/keystorepswd.txt  
      keystorefile:  
         file: ${VCELL_SECRETS_DIR}/keystore.jks  
	```
  
### Installation in Swarm Stack
* when a swarm stack is installed, the secret values are copied to the swarm nodes and the secrets are
  made available to the containers.
* **_In order to update the secrets within a swarm cluster (e.g. when a cert is renewed),
  the swarm stack must be destroyed and recreated_**.


## Procedure
### 1. set up environment variables
```bash
export host=vcellapi-test.cam.uchc.edu
# create keystore password file if it doesn't already exist
# echo "my-store-password" > /usr/local/deploy/${host}-keystorepswd.txt
export storepass=$(cat /usr/local/deploy/${host}-keystorepswd.txt) || echo "file not found"
export certname=${host}
echo "host=${host}, pass=${storepass}, certname=${certname}"
```
### 2. export cert and key as pkcs12 file
```bash
sudo openssl pkcs12 -export -out /tmp/${host}_fullchain_and_key.p12 \
  -in /etc/letsencrypt/live/${host}/fullchain.pem \
  -inkey /etc/letsencrypt/live/${host}/privkey.pem \
  -password pass:${storepass} \
  -name ${certname}
```
### 3. import pkcs12 file into JKS keystore and backup old JKS keystore
```bash
sudo mv /usr/local/deploy/${host}.jks \
  /usr/local/deploy/${host}_`date +'%Y_%m_%d'`.jks
  
sudo keytool -importkeystore -deststorepass ${storepass} \
  -destkeypass ${storepass} \
  -destkeystore /usr/local/deploy/${host}.jks \
  -srckeystore /tmp/${host}_fullchain_and_key.p12 \
  -srcstoretype PKCS12 \
  -srcstorepass ${storepass} \
  -alias ${certname} \
  -noprompt
```
### 4. copy keystore to other nodes in swarm cluster
copy keystores to /usr/local/deploy directory on (e.g. vcell-node5 and vcell-node6 for vcellapi-test cluster)

```bash
scp /usr/local/deploy/${host}.jks vcell@vcell-node5:/usr/local/deploy/${host}.jks
scp /usr/local/deploy/${host}-keystorepswd.txt vcell@vcell-node5:/usr/local/deploy/${host}-keystorepswd.txt
scp /usr/local/deploy/${host}.jks vcell@vcell-node6:/usr/local/deploy/${host}.jks
scp /usr/local/deploy/${host}-keystorepswd.txt vcell@vcell-node6:/usr/local/deploy/${host}-keystorepswd.txt
```

### 5. set up symbolic links to new keystore and keystore password files
on each machine in the swarm cluster (e.g. vcellapi-test, vcell-node5, vcell-node6) link the new keystore and keystore 
password files to the generic names used by the docker swarm stack.
```bash
ln -s /usr/local/deploy/${host}.jks /usr/local/deploy/keystore.jks
ln -s /usr/local/deploy/${host}-keystorepswd.txt /usr/local/deploy/keystorepswd.txt
```

### 6. shutdown, remove and reinstall stack to remove/replace secrets
shutdown the stack
```bash
vcell-services shutdown test --all
```
remove (A.) just the secrets and corresponding services, or (B.) the entire stack.
- A. Either - remove the secrets and the services which use them
   ```bash
   sudo docker service rm vcell-api
   sudo docker service rm vcell-data
   sudo docker secret rm keystorefile
   sudo docker secret rm storepswd
   ```
- B. Or - remove the entire stack (if step A does not work)
   ```bash
  sudo docker stack rm vcelltest
   ```  
redeploy the stack using the new secrets but the original config and compose files.
```bash
export config_file=/share/apps/vcell3/deployed_github/config/server_test_7.5.0_80.1_41f2c4a.config
export compose_file=/share/apps/vcell3/deployed_github/config/docker-compose_41f2c4a.yml
sudo env $(cat ${config_file} | xargs) \
   docker stack deploy -c ${compose_file} vcelltest --with-registry-auth
```
