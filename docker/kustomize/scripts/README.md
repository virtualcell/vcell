# create sealed secrets
Note that the namespace must exist and sealed secrets controller installed in 
kubernetes before creating the sealed secrets.
```bash
DEPLOYMENT=devjim
NAMESPACE=devjim
kubectl create namespace ${NAMESPACE}
./sealed_secret_db.sh ${NAMESPACE} db_pswd > ../overlays/${DEPLOYMENT}/secret-db.yaml
./sealed_secret_jms.sh ${NAMESPACE} jms_pswd > ../overlays/${DEPLOYMENT}/secret-jms.yaml
./sealed_secret_ghcr.sh ${NAMESPACE} gh_user gh_user_email gh_token > ../overlays/${DEPLOYMENT}/secret-ghcr.yaml
```
