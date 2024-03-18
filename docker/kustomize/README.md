# local minikube config
## ArgoCD setup
according to https://argo-cd.readthedocs.io/en/stable/getting_started/
```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

make the following a table


| technology                             | description                                                            |
|----------------------------------------|------------------------------------------------------------------------|
| Kubespray (on prem cluster)            | ArgoCD (GitOps), <br/>Sealed Secrets, <br/>Certificate Manager         |
| Lens                                   | nice visual tool for Kubernetes clusters |
| minikube  (local dev cluster)          | kubectl (manual deploy), <br/>plain secrets, <br/>self-signed certs |
| Kustomize                              | to organize k8s manifests for multiple environments                    |
| ArgoCD                                 | for continuous deployment and GitOps                                   |
| Sealed Secrets                         | for secret management of encrypted secrets in Git per each cluster     |
| Certificate Manager with Let's Encrypt | for automatic refresh of SSL certificates                              |
| Ingress controller                     | for reverse proxies and CORS handling                                  |
| Persistent Volumes/Claims              | to map NFS mounts to pods                                              |

# local minikube config

### install Lens

### install and start minikube on macos
```bash
brew install qemu
brew install socket_vmnet
brew tap homebrew/services
HOMEBREW=$(which brew) && sudo ${HOMEBREW} services start socket_vmnet
# minikube start --driver qemu --network socket_vmnet --memory=8g --cpus=2
minikube start --base-image gcr.io/k8s-minikube/kicbase-builds:v0.0.42-1703092832-17830 --driver docker  --memory=8g --cpus=2
minikube addons enable metrics-server

brew install kubectl
brew install helm
```

### install kube-prometheus-stack
see https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

kubectl create namespace monitoring
helm install prometheus --namespace monitoring prometheus-community/kube-prometheus-stack
```
in Lens, you can see the prometheus pods and services in the monitoring namespace.  
Log into Grafana with admin and the password from the following command.
```bash
kubectl get secret --namespace monitoring prometheus-grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
```

### set up ingress controller
```bash
minikube addons enable ingress
kubectl get pods -n ingress-nginx
```

### Sealed Secrets setup
install sealed secrets and the controller
```bash
brew install kubeseal
helm repo add sealed-secrets https://bitnami-labs.github.io/sealed-secrets
helm install sealed-secrets -n kube-system \
     --set-string fullnameOverride=sealed-secrets-controller sealed-secrets/sealed-secrets
```
create a secret and seal it
```bash
kubectl create secret generic secret-name --dry-run=client --from-literal=foo=bar -o yaml | \
    kubeseal \
      --controller-name=sealed-secrets-controller \
      --controller-namespace=kube-system \
      --format yaml > mysealedsecret.yaml

kubectl apply -f mysealedsecret.yaml
```

### ArgoCD setup

### Certificate Manager setup
```bash
brew install cmctl
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml
cmctl check api
```


# application configuration
### verify the kustomization scripts
```bash
kubectl create namespace devjim
kubectl kustomize overlays/devjim | kubectl apply --dry-run=client --validate=true -f -
```
### apply the kustomization scripts
```bash
kubectl kustomize overlays/devjim | kubectl apply -f -
```

### create sealed secrets (see [scripts/README.md](scripts/README.md))

# setting up minikube.local for local development with ingress
from https://github.com/kubernetes/minikube/issues/13510.  "Hi, I can confirm that running minikube tunnel works for me on m1 with the docker driver. 
Keep in mind that your etc/hosts file needs to map to 127.0.0.1, instead of the output 
of minikube ip or kubectl get ingress - this is an important gotcha."
```bash
#echo "$(minikube ip) minikube.local" | sudo tee -a /etc/hosts
echo "127.0.0.1 minikube.local" | sudo tee -a /etc/hosts
sudo minikube tunnel
✅  Tunnel successfully started
📌  NOTE: Please do not close this terminal as this process must stay alive for the tunnel to be accessible ...
❗  The service/ingress nginx-ingress requires privileged ports to be exposed: [80 443]
🔑  sudo permission will be asked for it.
🏃  Starting tunnel for service nginx-ingress.
```
when running VCell client with self-signed cert, set the following flags
```
-Dvcell.ssl.ignoreHostMismatch=true
-Dvcell.ssl.ignoreCertProblems=true
```

### lightweight local log tailing with logtail
```bash
brew tap johanhaleby/kubetail
brew install kubetail
kubetail -n devjim
```