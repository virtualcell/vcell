


### 1) add github secret to the Kubernetes Cluster

The github credentials must be manually inserted into 
the Kubernetes cluster   For background material: 
see [authenticating ghcr with Kubernetes](https://dev.to/asizikov/using-github-container-registry-with-kubernetes-38fb)

```bash
./deploy-repo-secret.sh github_user github_token
```

### 2) deploy the VCell application to Kubernetes

```bash
kubectl apply -f base
```
