# local minikube config
## ArgoCD setup
according to https://argo-cd.readthedocs.io/en/stable/getting_started/
```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

make the following a table


| technology                             | description                                                         |
|----------------------------------------|---------------------------------------------------------------------|
| Kubespray (on prem cluster)            | ArgoCD (GitOps), <br/>Sealed Secrets, <br/>Certificate Manager      |
| minikube  (local dev cluster)          | kubectl (manual deploy), <br/>plain secrets, <br/>self-signed certs |
| Kustomize                              | to organize k8s manifests for multiple environments                 |
| ArgoCD                                 | for continuous deployment and GitOps                                |
| Sealed Secrets                         | for secret management of encrypted secrets in Git per each cluster  |
| Certificate Manager with Let's Encrypt | for automatic refresh of SSL certificates                           |
| Ingress controller                     | for reverse proxies and CORS handling                               |
| Persistent Volumes/Claims              | to map NFS mounts to pods                                           |

# local minikube config
## ArgoCD setup

## Prometheus setup
```bash
kubectl create namespace monitoring
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm install prometheus prometheus-community/prometheus -n monitoring
```
