## Generate TLS Certs for vcell-api service using Let's Encrypt
run the following procedure on the external node of the cluster (e.g. vcellapi-test).

### Install Let's Encrypt Certbot
```bash
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
```

### Run Certbot to generate TLS cert
Ask Let's Encrypt to generate a renewable TLS cert.  Certbot's temporary web server 
should use port 80 to avoid conflict with vcell-api running on port 443.  The certs
are stored in /etc/letsencrypt/live/${host}/fullchain.pem and privkey.pem.
- the certbot command below will fail if port 80 is not open on the firewall
- the --deploy-hook option is not used because the docker swarm stack must be destroyed 
  and recreated anyway to use the new cert (this will be more graceful in Kubernetes).

```bash
export host=vcellapi-test.cam.uchc.edu
sudo certbot certonly --standalone --domain ${host} --preferred-challenges http
# sudo certbot certonly --standalone --domain ${host} --preferred-challenges http \
#      --deploy-hook "sudo /usr/local/deploy/install_cert.sh"
```
example output:
```
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/vcellapi-test.cam.uchc.edu/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/vcellapi-test.cam.uchc.edu/privkey.pem
This certificate expires on 2024-03-05.
These files will be updated when the certificate renews.
Certbot has set up a scheduled task to automatically renew this certificate in the background.
````
### Manually install cert following renewal via Let's Encrypt.

1. set up environment variables
```bash
export host=vcellapi-test.cam.uchc.edu
# create keystore password file if it doesn't already exist
# echo "my-store-password" > /usr/local/deploy/${host}-keystorepswd.txt
export storepass=$(cat /usr/local/deploy/${host}-keystorepswd.txt) || echo "file not found"
export certname=${host}
echo "host=${host}, pass=${storepass}, certname=${certname}"
```
2. export cert and key as pkcs12 file
```bash
sudo openssl pkcs12 -export -out /tmp/${host}_fullchain_and_key.p12 \
  -in /etc/letsencrypt/live/${host}/fullchain.pem \
  -inkey /etc/letsencrypt/live/${host}/privkey.pem \
  -password pass:${storepass} \
  -name ${certname}
```
3. import pkcs12 file into JKS keystore and backup old JKS keystore
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
4. copy keystore to other nodes (vcell-node5, vcell-node6)
```bash
scp /usr/local/deploy/${host}.jks vcell@vcell-node5:/usr/local/deploy/${host}.jks
scp /usr/local/deploy/${host}-keystorepswd.txt vcell@vcell-node5:/usr/local/deploy/${host}-keystorepswd.txt
scp /usr/local/deploy/${host}.jks vcell@vcell-node6:/usr/local/deploy/${host}.jks
scp /usr/local/deploy/${host}-keystorepswd.txt vcell@vcell-node6:/usr/local/deploy/${host}-keystorepswd.txt
```
5. shutdown, remove and reinstall stack to remove/replace secrets
```bash
# shutdown stack and remove stack
vcell-services shutdown test --all
sudo docker stack rm vcelltest

# redeploy stack (generates new secrets with new cert)
export config_file=/share/apps/vcell3/deployed_github/config/server_test_7.5.0_80.1_41f2c4a.config
export compose_file=/share/apps/vcell3/deployed_github/config/docker-compose_41f2c4a.yml
sudo env $(cat ${config_file} | xargs) \
   docker stack deploy -c ${compose_file} vcelltest --with-registry-auth
```

