# VCell WordPress Kubernetes Deployment

Kubernetes deployment artifacts for vcell.org WordPress site, migrating from legacy VM-based installation.

## Repository Structure

```
├── kustomize/
│   ├── base/                      # Base Kustomize configuration
│   │   ├── kustomization.yaml     # Helm chart reference
│   │   └── values.yaml            # Common Helm values
│   └── overlays/
│       ├── vcell-wordpress-dev/   # Dev environment
│       │   ├── kustomization.yaml
│       │   ├── values.yaml
│       │   ├── secrets.sh         # Generates sealed secrets
│       │   └── secrets.template.dat
│       └── vcell-wordpress-prod/  # Production environment
│           └── (same structure)
├── migration/                   # Migration tools and documentation
│   ├── scripts/                 # Migration scripts
│   ├── sql/                     # Database dumps and migration SQL
│   └── MIGRATION_NOTES.md       # Migration checklist
└── docs/                        # Additional documentation
```

## Deployment

### Preview manifests
```bash
# Dev environment
kubectl kustomize kustomize/overlays/vcell-wordpress-dev --enable-helm

# Prod environment
kubectl kustomize kustomize/overlays/vcell-wordpress-prod --enable-helm
```

### Apply to cluster
```bash
# Dev environment
kubectl apply -k kustomize/overlays/vcell-wordpress-dev --enable-helm

# Prod environment
kubectl apply -k kustomize/overlays/vcell-wordpress-prod --enable-helm
```

### Secrets
Secrets are managed using sealed-secrets. Each overlay contains:
- `secrets.template.dat` - Template showing required variables
- `secrets.dat` - Actual values (git-ignored, create from template)
- `secrets.sh` - Script to generate sealed secret YAML

```bash
cd kustomize/overlays/vcell-wordpress-dev
cp secrets.template.dat secrets.dat
# Edit secrets.dat with real values
./secrets.sh
# Generates wordpress-sealed-secret.yaml
```

## Migration Overview

This deployment replaces a legacy WordPress installation running directly on a VM with outdated PHP and Apache. Key migration considerations:

- Database export/import with URL search-replace
- wp-content migration (uploads, themes, plugins)
- NFS path remapping
- Plugin compatibility with modern PHP

See `migration/MIGRATION_NOTES.md` for detailed checklist.

## Helm Chart Reference

Using [Bitnami WordPress Helm Chart](https://github.com/bitnami/charts/tree/main/bitnami/wordpress)

```bash
# View all available chart options
helm show values bitnami/wordpress
```
