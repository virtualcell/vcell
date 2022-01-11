#Certs (Complete tasks (when certs expire) before building VCell)



#(TLS/SSL website for (vcellapi.cam.uchc.edu,vcell-api.cam.uchc.edu)
Look in Dockerfile-api-dev under ENV block (key-value pairs defining TLA keystore and password)  

```
ENV dbpswdfile=/run/secrets/dbpswd \  
    jmspswdfile=/run/secrets/jmspswd \  
    keystore=/run/secrets/keystorefile_20220105 \  
    keystorepswdfile=/run/secrets/keystorepswd  
```

Look in [docker\swarm\docker-compose.yml](./docker/swarm/docker-compose.yml)  
--look in services->api->secrets to find which secrets are used (matches values defined above)  

```
  keystorepswd:
    file: ${VCELL_SECRETS_DIR}/vcellapi-beta-keystorepswd.txt
  keystorefile_20220105:
    file: ${VCELL_SECRETS_DIR}/vcellapi-beta_20220105.jks
```

#---------- BEGIN Create SSL certificate for VCell API docker service (view as markdown source, not preview)  
See C:\Users\frm\Desktop\NewCerificates\certs_2022
look in {vcellroot}\docker\swarm\serverconfig-uch.sh for var iable definition "VCELL_SECRETS_DIR=xxx" e.g. VCELL_SECRETS_DIR=/usr/local/deploy
--Copy "vcellapi-beta.jks" and "vcellapi-beta-keystorepswd.tx"t into ${VCELL_SECRETS_DIR} on each node(host) of the swarm
  
//
//Create SSL certificate for VCell API docker service
//
  
Start digicert on windows->SSL->'Create CSR'
create csr (do not use previous cert as template because it may have challenge phrase)->
  
type
	SSL
commonname
	vcellapi.cam.uchc.edu
subjectAlternativeName
	vcellapi.cam.uchc.edu
	vcellapi-beta.cam.uchc.edu
Organization
	University of Connecticut
Department
	UCH
City
	Storrs
State
	Connecticut
Country
	USA
KeySize
	4096

->Generate->Save csrFile.txt
  
Email csrFile.txt to peoples@uchc.edu as an attachment
	The VCell project needs to renew it's expired SSL web certificate...
  
Rich Peoples will upload the csrFile.txt to InCommon and receive back a xxx.p7b certificate which he will send to you
Save the .p7b file to local disk
In DigiCert->import->.p7b filename->certificate will be imported
  
In DigiCert click newly imported cert->Export Certificate->
Yes export private key
	pfx format, include all certificates...->
password->Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt->
Save vcellapi_cam_uchc_edu.pfx
  
In windows command prompt:
cd to directory where you save .pfx file, give command
keytool -importkeystore -srckeystore vcellapi_cam_uchc_edu.pfx -srcstoretype pkcs12 -destkeystore  vcellapi-beta_20210125.jks -deststoretype JKS
New Desitnation password
	Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt
Source Password
	Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt
//Copy (gitbash cmd-line) new java keystore file from Windows to all hosts
scp  ./vcellapi_cam_uchc_edu.jks vcell@{vcellapi-beta,vcell-node3,vcell-node4,vcellapi,vcell-node1,vcell-node2}:/usr/local/deploy/vcellapi-beta_20220105.jks
  
//Check .jks on vcellapi and vcellapi-beta /*NOT PART OF DEPLOYMENT*/ (see https://stackoverflow.com/questions/652916/converting-a-java-keystore-into-pem-format)
Find Alias Name
	keytool -v -list -keystore ./vcellapi-beta_20220105.jks->Copy Aliasname (e.g. 27097e574ce44806b1c7974adc57bce1)
Generate .p12 file
	keytool -importkeystore -srckeystore ./vcellapi-beta_20220105.jks -destkeystore foo.p12 -srcalias 27097e574ce44806b1c7974adc57bce1 -srcstoretype jks -deststoretype pkcs12
	New Desitnation password
		Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt
	Source Password
		Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt
	
Generate .pem file
	openssl pkcs12 -in foo.p12 -out foo.pem
	pem passpharase
		Use text found in vcell-node1:/usr/local/deploy/vcellapi-beta-keystorepswd.txt
Copy .pem file to the host you want to test on
	scp ./foo.pem vcell@{vcellapi,vcellapi-beta}.cam.uchc.edu:/tmp/foo.pem
ssh to vcellapi or vcellapi-beta and run openssl https server
	openssl s_server -cert /tmp/foo.pem -accept 44330 (watch console output for connections info)
	Use web browser from other computer, goto https://{vcellapi,vcellapi-beta}.cam.uchc.edu:44330
  
//Make changes to vcell code for new secret before deploying
see github/vcell/masterBranch commit 95252f455347f349842810e788a3d86a8d576e06, update VCell src with new vcellapi-beta_20210125.jks name
README_certs.md
docker/build/Dockerfile-api-dev
docker/swarm/docker-compose.yml
  
//After deployment
//View certificate file in running Docker api container
ssh vcellapi-beta
sudo docker stack ps vcellalpha | grep I running
    Find HOST api container is running on
ssh api container HOST (e.g vcell-node3, vcell-node4 or vcellapi-beta)
sudo docker ps to find container id of running api container
sudo docker exec -it apiContainerID /bin/bash
    cd /run/secrets
    keytool -v -list -keystore ./vcellapi-beta_202201015.jks


#---------- END Create SSL certificate for VCell API docker service (view as markdown source, not preview) 


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

