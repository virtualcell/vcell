# Generate TLS certs for vcell-api services

## Contents
- [1) Generate certs using Let's Encrypt](#1-generate-certs-using-lets-encrypt)
  - [1.A) Install Let's Encrypt Certbot](#1a-install-lets-encrypt-certbot)
  - [1.B) Run Certbot to generate TLS cert](#1b-run-certbot-to-generate-tls-cert)
  - [1.C) Manually Install Certs in JKS keystore](#1c-manually-install-certs-in-jks-keystore)
  - [1.D) Manually install certs in swarm](#1d-manually-install-certs-in-swarm)
- [2) Generate certs using InCommon CA](#2-generate-certs-using-incommon-ca)
  - [2.A) Generate Private Key and CSR](#2a-generate-private-key-and-csr)
  - [2.B) Submit CSR to Certificate Authority (e.g. InCommon)](#2b-submit-csr-to-certificate-authority-eg-incommon)
  - [2.C) Create a PKCS12 file](#2c-create-a-pkcs12-file)
  - [2.D) Convert the PKCS12 file to a JKS](#2d-convert-the-pkcs12-file-to-a-jks)
  - [2.D) Manually install certs in swarm](#2d-manually-install-certs-in-swarm)

## 1) Generate certs using Let's Encrypt
run the following procedure on the external node of the cluster (e.g. vcellapi-test).  
For use of traditional certs (e.g. InCommon CA), please see [README_certs_install.md](README_certs_install.md)

### 1.A) Install Let's Encrypt Certbot
```bash
sudo snap install core; sudo snap refresh core
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
```

### 1.B) Run Certbot to generate TLS cert
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
```text
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/vcellapi-test.cam.uchc.edu/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/vcellapi-test.cam.uchc.edu/privkey.pem
This certificate expires on 2024-03-05.
These files will be updated when the certificate renews.
Certbot has set up a scheduled task to automatically renew this certificate in the background.
```

### 1.C) Manually Install Certs in JKS keystore

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

### 1.D) Manually install certs in swarm (see [README_certs_install.md](README_certs_install.md))

## 2) Generate certs using InCommon CA

### 2.A) Generate Private Key and CSR
set up environment variables (for use in all steps below)
```bash
export host=vcellapi-beta
export host=vcellapi
export datestr=20231212
export store_pass=$(cat ${host}-keystorepswd.txt)
export key_pass=${storepass}
export key_fname=${host}-${datestr}.key
export csr_fname=${host}-${datestr}.csr
export cert_fname=${host}_cam_uchc_edu.cer
export p12_fname=${host}-${datestr}.p12
export keystore_fname=${host}-${datestr}.jks
```
generate private key and code signing request (e.g. `vcellapi-beta-20231212.key` and `vcellapi-beta-20231212.csr`).  Keep 
the key file private and send the csr file to the CA.
```bash
### didn't verify the following commands !!!!
openssl genrsa -out ${key_fname} 4096 -passout pass:${key_pass}
openssl req -new -key ${key_fname} -out ${csr_fname} -passin pass:${key_pass}
```
at the prompt, enter the following information similar to this:
```
Country [C]: US
State [ST]: CT
Location [L]: Farmington
Organization [O]: UConn Health
Organizational Unit [OU]: CCAM
Common Name [CN]: vcellapi-beta.cam.uchc.edu
Email Address [emailAddress]: vcell_support@uchc.edu
```

### 2.B) Submit CSR to Certificate Authority (e.g. InCommon)
retrieve a certificate file with PEM encoding (e.g. `vcellapi-beta_cam_uchc_edu.cer`) from the CA with full chain of trust

### 2.C) Create a PKCS12 file
Convert your existing certificate and private key into a PKCS12 (.p12) file.  You'll be prompted to enter the private key password.
```bash
sudo openssl pkcs12 -export \
     -in ${cert_fname} \
     -inkey ${key_fname} \
     -out ${p12_fname} \
     -password pass:${store_pass} \
     -name ${host}
```

### 2.D) Convert the PKCS12 file to a JKS
Now use keytool, which comes with the Java SDK, to generate a Java KeyStore file:
```bash
keytool -importkeystore \
        -deststorepass ${store_pass} \
        -destkeypass ${store_pass} \
        -destkeystore ${keystore_fname} \
        -srckeystore ${p12_fname} \
        -srcstoretype PKCS12 \
        -srcstorepass ${store_pass} \
        -alias ${host}
```

### 2.D) Manually install certs in swarm (see [README_certs_install.md](README_certs_install.md))
