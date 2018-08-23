#Certs (Complete tasks (when certs expire) before building VCell)


#(TLS/SSL website for (vcellapi.cam.uchc.edu,vcell-api.cam.uchc.edu)
Look in Dockerfile-api-dev under ENV block (key-value pairs defining TLA keystore and password)  

```
ENV dbpswdfile=/run/secrets/dbpswd \  
    jmspswdfile=/run/secrets/jmspswd \  
    keystore=/run/secrets/keystorefile \  
    keystorepswdfile=/run/secrets/keystorepswd  
```

Look in [docker\swarm\docker-compose.yml](./docker/swarm/docker-compose.yml)  
--look in services->api->secrets to find which secrets are used (matches values defined above)  

```
  keystorepswd:
    file: ${VCELL_SECRETS_DIR}/vcellapi-beta-keystorepswd.txt
  keystorefile:
    file: ${VCELL_SECRETS_DIR}/vcellapi-beta.jks
```

**See Sophon** to create vcellapi-beta.jks, he will give me a password that I put vcellapi-beta-keystorepswd.txt (freetext)  
look in {vcellroot}\docker\swarm\serverconfig-uch.sh for var iable definition "VCELL_SECRETS_DIR=xxx" e.g. VCELL_SECRETS_DIR=/usr/local/deploy
--Copy "vcellapi-beta.jks" and "vcellapi-beta-keystorepswd.tx"t into ${VCELL_SECRETS_DIR} on each node(host) of the swarm

#"Microsoft authenticode" and "Apple developer" codesigning (to prevent install4j from being rejected on windows clients)
Look in Dockerfile-clientgen-dev under ENV block (key-value pairs defining codesigning certs, keystore and keystorepassword)  

```
ENV winCodeSignKeystore_pfx=Expecting_this_to_be_defined_runtime_for_winCodeSignKeystore_pfx \
    macCodeSignKeystore_p12=Expecting_this_to_be_defined_runtime_for_macCodeSignKeystore_p12 \
    winCodeSignKeystore_pswdfile=Expecting_this_to_be_defined_runtime_for_winCodeSignKeystore_pswdfile \
    macCodeSignKeystore_pswdfile=Expecting_this_to_be_defined_runtime_for_macCodeSignKeystore_pswdfile \
    Install4J_product_key_file=Expecting_this_to_be_defined_runtime_for_Install4J_product_key_file
```
Look in generate_installers.sh for definition of 

```
    -e winCodeSignKeystore_pfx=/buildsecrets/VCELL_UCONN_MS_2017.pfx \
    -e winCodeSignKeystore_pswdfile=/buildsecrets/VCELL_UCONN_MS_2017_pswd.txt \
    -e macCodeSignKeystore_p12=/buildsecrets/VCELL_APPLE_2015.p12 \
    -e macCodeSignKeystore_pswdfile=/buildsecrets/VCELL_APPLE_2015_pswd.txt \
    -e Install4J_product_key_file=/buildsecrets/Install4J_product_key.txt \
    ...
    -v ${VCELL_DEPLOY_SECRETS_DIR}:/buildsecrets \
    
```

Reaseon these are also stored on cluser nodes for convenience when generating Install4j
**See Sophon** to create winCodeSignKeystore_pfx(windows) and macCodeSignKeystore_p12(Mac) and he will give me a keystore password for each keystore to be place in winCodeSignKeystore_pswdfile and macCodeSignKeystore_pswdfile (freetext)
look in {vcellroot}\docker\swarm\serverconfig-uch.sh for variable definition "VCELL_DEPLOY_SECRETS_DIR=xxx" e.g. VCELL_DEPLOY_SECRETS_DIR=/usr/local/deploy
--Copy "vcellapi-beta.jks" and "vcellapi-beta-keystorepswd.tx"t into ${VCELL_SECRETS_DIR} on each node(host) of the swarm

