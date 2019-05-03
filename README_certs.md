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

Reason these are also stored on cluser nodes for convenience when generating Install4j
**See Sophon** to create winCodeSignKeystore_pfx(windows) and macCodeSignKeystore_p12(Mac) and he will give me a keystore password for each keystore to be place in winCodeSignKeystore_pswdfile and macCodeSignKeystore_pswdfile (freetext)
look in {vcellroot}\docker\swarm\serverconfig-uch.sh for variable definition "VCELL_DEPLOY_SECRETS_DIR=xxx" e.g. VCELL_DEPLOY_SECRETS_DIR=/usr/local/deploy
--Copy "vcellapi-beta.jks" and "vcellapi-beta-keystorepswd.tx"t into ${VCELL_SECRETS_DIR} on each node(host) of the swarm


//  
//SSL Certificate Info and VCell Certificate Reference  
//VCell configuration file "vcell-node1.cam.uchc.edu:/usr/local/deploy/vcellapi-beta.jks" matches ssl certificate from "https://vcellapi.cam.uchc.edu/biomodel"  
//  
'Win InternetExplorer Browser'->https://vcellapi.cam.uchc.edu/biomodel->'click security lock'->'View Certificate'  
 "General"->  
     "Valid from 4/5/2018 to 4/5/2020"  
 "Details"->  
     "Serial number"->05 f0 42 2e 59 bb 74 e7 09 64 f0 0c 2f 4c db 61  
     "Signature algorithm"->sha256RSA  
     "Subject"->  
       CN = vcellapi-beta.cam.uchc.edu  
       OU = Enterprise Computing Services  
       O = University of Connecticut Health Center  
       L = Farmington  
       S = Connecticut  
       C = US  
     "Subject Alternative Name"->  
       DNS Name=vcellapi.cam.uchc.edu  
       DNS Name=vcellapi-beta.cam.uchc.edu  

'ssh vcell-node1.cam.uchc.edu' -> 'cd /usr/local/deploy' -> 'keytool -v --list -keystore vcellapi-beta.jks' -> "Certificate[1]"->  
(MATCHES) Valid from: Thu Apr 05 20:00:00 EDT 2018 until: Sun Apr 05 08:00:00 EDT 2020  
(MATCHES) Serial number: 5f0422e59bb74e70964f00c2f4cdb61  
(MATCHES) Signature algorithm name: SHA256withRSA  
(MATCHES) Owner: CN=vcellapi-beta.cam.uchc.edu, OU=Enterprise Computing Services, O=University of Connecticut Health Center, L=Farmington, ST=Connecticut, C=US  
(MATCHES) SubjectAlternativeName [DNSName: vcellapi.cam.uchc.edu DNSName: vcellapi-beta.cam.uchc.edu]  

**Convert .pfx into .jks using java keytool**  
keytool -importkeystore -srckeystore mypfxfile.pfx -srcstoretype pkcs12 -destkeystore clientcert.jks -deststoretype JKS  

**java keystore file info**  
keytool -v -list -keystore myjavakeystore.jks -storetype JKS  

**CodeSigning Certificate Info (ssh vcell-node1.cam.uchc.edu:/usr/local/deploy)**  
//To print out existing certificate info, first convert .pfx or .p12 into .pem, then parse .pem info as follows:   
openssl pkcs12 -in {VCELL_UCONN_MS_2017.pfx -or- VCELL_APPLE_2015.p12} -out temp.pem -nodes  
openssl x509 -in temp.pem -text -noout  
//To check/print .csr info (generated with digicert utility) before submitting (xfer theCSRFile file to vcell-node1)  
openssl req -text -noout -verify -in theCSRFile  
 