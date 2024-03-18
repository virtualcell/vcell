# create sealed secrets
Note that the namespace must exist and sealed secrets controller installed in 
kubernetes before creating the sealed secrets.
```bash
DEPLOYMENT=devjim
NAMESPACE=devjim
kubectl create namespace ${NAMESPACE}
./sealed_secret_api.sh ${NAMESPACE} db_pswd jms_pswd mongo_user mongo_pswd > ../overlays/${DEPLOYMENT}/api-secrets.yaml
./sealed_secret_ghcr.sh ${NAMESPACE} gh_user gh_user_email gh_token > ../overlays/${DEPLOYMENT}/secret-ghcr.yaml
./sealed_secret_ssh.sh ${NAMESPACE} /path/to/ssh_priv_key /path/to/ssh_pub_key > ../overlays/${DEPLOYMENT}/vcell-ssh-secret.yaml
```
