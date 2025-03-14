# Generate TLS certs for vcell.org

## 1) Generate certs using InCommon CA

### 1.A) Generate Private Key and CSR
generate private key and code signing request (e.g. `vcell_org.key` and `vcell_org.csr`).  Keep 
the key file private and send the csr file to the CA.
```bash
openssl req -new -newkey rsa:2048 -nodes -keyout vcell_org.key -out vcell_org.csr

```
at the prompt, enter the following information similar to this:
```
Country [C]: US
State [ST]: CT
Location [L]: Farmington
Organization [O]: UConn Health
Organizational Unit [OU]: CCAM
Common Name [CN]: vcell.org
Email Address [emailAddress]: mikewilson@uchc.edu
```

### 1.B) Submit CSR to Certificate Authority (e.g. InCommon)
retrieve a certificate file with PEM encoding (e.g. `vcell_org.cer`) from the CA with full chain of trust

### 1.C) Create a PKCS12 file (or other format???)
Convert your existing certificate and private key into a PKCS12 (.p12) file.  You'll be prompted to enter the private key password.
```bash
sudo openssl pkcs12 -export \
     -in ${cert_fname} \
     -inkey ${key_fname} \
     -out ${p12_fname} \
     -password pass:${store_pass} \
     -name ${host}
```

### 1.C) Install in web server
```bash
cd /etc/pki/tls/certs
mv vcell_org.crt vcell_org_2024.crt
mv /tmp/vcell_org.crt .
systemctl restart httpd.service 
```
