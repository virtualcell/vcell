# Apptainer Image Build & Distribution

## Problem

SLURM jobs on the mantis cluster run VCell workloads inside Apptainer
containers. Four container images are used:

| Image | Purpose | Version tracks |
|-------|---------|---------------|
| vcell-opt | Parameter estimation (COPASI) | VCell release tag |
| vcell-batch | Batch solvers (Langevin, RK, etc.) | VCell release tag |
| vcell-solvers | Native solvers (CombinedSundials, CVODE, NFSim, etc.) | Independent (e.g. v0.8.2) |
| vcell-fvsolver | Finite volume solvers (Smoldyn, SundialsPDE, etc.) | Independent (e.g. 0.9.7) |

Previously, every SLURM job invocation ran:

```
singularity run --containall ... docker://ghcr.io/virtualcell/vcell-opt:TAG
```

Apptainer caches OCI layer blobs but **rebuilds a temporary SIF by
unpacking all layers on every job** (~5 minutes on mantis). Parameter
estimation jobs were being cancelled by SLURM before the solver started
(GitHub issue #1653). Regular simulations were latently affected but
masked by longer wallclock budgets.

## Approach

Build SIF images, publish them to GHCR as ORAS artifacts, and pre-pull
them onto the cluster's shared filesystem during deploy. Runtime jobs
execute the pre-existing SIF file directly — no per-job unpack.

Three stages, each owned by the party that already has the relevant
context:

1. **CI (GitHub Actions)** builds SIFs for `vcell-opt` and `vcell-batch`
   (the two images CI just built) and publishes them to GHCR via ORAS.
2. **Solver SIFs** (`vcell-solvers`, `vcell-fvsolver`) are built
   separately — via a standalone workflow_dispatch or manually — when
   those images are updated. These images change infrequently and have
   independent version tags.
3. **Deploy (FluxCD Kubernetes Job)** triggers a one-shot Job per
   release. The Job pulls **all 4** SIFs from GHCR via ORAS (based on
   the site's `submit.env` configuration) and scp's them to the SLURM
   submit node. This is the only place all 4 images converge.

### Property rename

The 4 container image properties are renamed from `*_docker_name` to
`*_apptainer_image`. Values change from docker image references to
ORAS URLs:

```
# Before (docker image reference)
htc_vcellopt_docker_name=ghcr.io/virtualcell/vcell-opt:7.7.0.71

# After (ORAS URL pointing to pre-built SIF)
htc_vcellopt_apptainer_image=oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71
```

`SlurmProxy.java` derives the local SIF filename deterministically
from the ORAS URL: strip `oras://`, replace `/:` with `_`, append
`.img`. Example:
`oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71`
→ `ghcr.io_virtualcell_vcell-opt_singularity_7.7.0.71.img`

### Why this shape

- **Single writer, bounded window.** Only the post-deploy Job writes
  SIFs, and only during deploy.
- **No new secrets crossing boundaries.** CI pushes to GHCR (existing
  `GITHUB_TOKEN`). The k8s Job uses the existing SSH key to scp to the
  submit node. No new credentials.
- **No custom Docker image.** The k8s Job uses
  `ghcr.io/apptainer/apptainer:1.4` (Debian bookworm, Apptainer 1.4.5)
  with an inline command that installs `openssh-client` at startup.
- **No hardcoded hostnames.** The `batchhost` env var comes from the
  site's kustomize `submit.env` ConfigMap — same source `vcell-submit`
  already uses.
- **CI only builds what it knows.** The release CI builds SIFs for
  vcell-opt and vcell-batch (same tag). Solver SIFs (vcell-solvers,
  vcell-fvsolver) are built separately when those images change. The
  deploy-time k8s Job pulls all 4 based on the site's config.
- **Failures surface where someone is watching.** CI publish fails →
  GHA blocks. Deploy Job fails → FluxCD surfaces. Runtime SIF missing →
  SLURM script fails fast.

## Pipeline overview

```
VCell release
     │
     ▼
CI-full.yml
     │
     ├─ docker-build (matrix) ──► ghcr.io/virtualcell/vcell-opt:TAG
     │                             ghcr.io/virtualcell/vcell-batch:TAG
     │
     ├─ tag-and-push ──► adds friendly tag + :latest
     │
     └─ build-and-publish-sif (vcell-opt + vcell-batch only)
           │
           ├─ apptainer build → SIF
           ├─ apptainer inspect → validate
           └─ apptainer push oras://ghcr.io/virtualcell/vcell-opt_singularity:TAG
                              oras://ghcr.io/virtualcell/vcell-batch_singularity:TAG


Solver image update (independent, infrequent)
     │
     ▼
Standalone workflow_dispatch or manual build
     │
     └─ apptainer push oras://ghcr.io/virtualcell/vcell-solvers_singularity:v0.8.2
                        oras://ghcr.io/virtualcell/vcell-fvsolver_singularity:0.9.7


FluxCD reconcile (deploy)
     │
     ▼
vcell-sif-prepull-<tag> (Kubernetes Job)
     │
     ├─ image: ghcr.io/apptainer/apptainer:1.4
     ├─ apt-get install openssh-client
     ├─ reads ALL 4 ORAS URLs from submit.env (via ConfigMap)
     ├─ for each: apptainer pull oras://... → SIF in pod
     ├─ scp SIF.tmp vcell@${batchhost}:<SIF_DIR>/
     ├─ ssh vcell@${batchhost} 'mv -n SIF.tmp SIF'
     └─ ssh vcell@${batchhost} 'find ... -mtime +90 -delete' (GC)
           │
           ▼
     /share/apps/vcell3/singularityImages/  (NFS, shared across nodes)
           │
           ▼
     SLURM compute nodes read SIF directly
```

## Filename convention

The local SIF filename is derived deterministically from the ORAS URL:

```
oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71
  → ghcr.io_virtualcell_vcell-opt_singularity_7.7.0.71.img

oras://ghcr.io/virtualcell/vcell-solvers_singularity:v0.8.2
  → ghcr.io_virtualcell_vcell-solvers_singularity_v0.8.2.img
```

Both `SlurmProxy.java` and the post-deploy k8s Job use this same
convention. The ORAS URL is the single source of truth; the filename
is a deterministic derivation.

## Directory layout on the cluster

```
/share/apps/vcell3/singularityImages/       ← NFS, shared across nodes,
                                              post-deploy Job writes here
  ghcr.io_virtualcell_vcell-opt_singularity_7.7.0.71.img
  ghcr.io_virtualcell_vcell-batch_singularity_7.7.0.71.img
  ghcr.io_virtualcell_vcell-solvers_singularity_v0.8.2.img
  ghcr.io_virtualcell_vcell-fvsolver_singularity_0.9.7.img
  ...older versions until GC
```

## Configuration

Each deploy site has a `submit.env` in kustomize that sets the ORAS
URLs:

```
htc_vcellopt_apptainer_image=oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71
htc_vcellbatch_apptainer_image=oras://ghcr.io/virtualcell/vcell-batch_singularity:7.7.0.71
htc_vcellsolvers_apptainer_image=oras://ghcr.io/virtualcell/vcell-solvers_singularity:v0.8.2
htc_vcellfvsolver_apptainer_image=oras://ghcr.io/virtualcell/vcell-fvsolver_singularity:0.9.7
```

These flow through `docker-compose.yml` → `Dockerfile-submit-dev`
→ Java system properties → `PropertyLoader` → `SlurmProxy.java`.

The solver-to-container mapping in `SlurmProxy.java` (lines 728-737)
maps each solver name to the appropriate `*_apptainer_image` property,
then derives the local SIF filename from that ORAS URL.

## Atomic write

The post-deploy k8s Job writes each SIF with:

```
scp <SIF> vcell@${batchhost}:/share/apps/vcell3/singularityImages/${SIF}.tmp
ssh vcell@${batchhost} "mv -n '${SIF}.tmp' '${SIF}'"
```

`-n` (no-clobber) ensures re-cutting the same tag with different
content fails visibly rather than silently replacing a SIF that
running jobs may have mapped.

## Garbage collection

The post-deploy Job trims SIFs older than 90 days:

```
ssh vcell@${batchhost} "find /share/apps/vcell3/singularityImages \
  -maxdepth 1 -name 'ghcr.io_virtualcell_vcell-*.img' -mtime +90 -delete"
```

## Runtime usage in SLURM scripts

`SlurmProxy.java` generates SLURM scripts containing:

```bash
# Derived from oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71
solver_sif=/share/apps/vcell3/singularityImages/ghcr.io_virtualcell_vcell-opt_singularity_7.7.0.71.img
if [ ! -f "$solver_sif" ]; then
    echo "ERROR: missing SIF $solver_sif" >&2
    exit 1
fi
solver_container_prefix="singularity run --containall ${container_bindings} ${container_env} $solver_sif"
```

**Fails fast** if the SIF is missing. No silent fallback to `docker://`
in production.

## Solver SIF bootstrap (one-time setup)

Before the first deploy using pre-built SIFs, the `vcell-solvers` and
`vcell-fvsolver` SIFs must exist in GHCR. CI only builds vcell-opt and
vcell-batch SIFs (they track the VCell release tag). The solver images
have independent version tags and must be bootstrapped manually once:

```bash
# vcell-solvers
apptainer build vcell-solvers.img docker://ghcr.io/virtualcell/vcell-solvers:v0.8.2
apptainer push vcell-solvers.img oras://ghcr.io/virtualcell/vcell-solvers_singularity:v0.8.2

# vcell-fvsolver
apptainer build vcell-fvsolver.img docker://ghcr.io/virtualcell/vcell-fvsolver:0.9.7
apptainer push vcell-fvsolver.img oras://ghcr.io/virtualcell/vcell-fvsolver_singularity:0.9.7
```

This only needs to be done once per solver version. After this, the
post-deploy k8s Job will pull these SIFs alongside the VCell release
SIFs. When solver images are updated to new versions, repeat the
process for the new tag (see "Updating solver images" below).

## Updating solver images

When `vcell-solvers` or `vcell-fvsolver` releases a new version:

1. Build and publish the SIF (standalone workflow_dispatch or manual):
   ```
   apptainer build vcell-solvers.sif docker://ghcr.io/virtualcell/vcell-solvers:v0.8.3
   apptainer push vcell-solvers.sif oras://ghcr.io/virtualcell/vcell-solvers_singularity:v0.8.3
   ```
2. Update `submit.env` in vcell-fluxcd to point to the new ORAS URL.
3. Deploy. The post-deploy Job pulls the new SIF.

## Developer fallback

For dev/test clusters where the post-deploy Job does not run, see
`docker/swarm/dev_singularity_build.sh`. Builds SIFs locally from
docker images and copies them to a specified directory.

## Rollback

Each SIF is uniquely named by release tag. Rollback does not require
deleting the current SIF — redeploying the previous release points
`vcell-submit` at the previous tag's ORAS URLs, whose SIFs are still
on disk.

If a SIF is corrupted or bad:

1. Delete it on the cluster:
   `ssh vcell@${batchhost} 'rm /share/apps/vcell3/singularityImages/<SIF>.img'`
2. Redeploy the previous working release.

## Failure modes

| Mode | Handling |
|------|----------|
| Partial scp | Atomic `mv -n` from `.tmp`: nodes never see partial SIF |
| Tag reuse with different content | `mv -n` fails; Job surfaces error via FluxCD |
| CI publishes docker but fails SIF | Release blocks at `build-and-publish-sif` GHA job |
| Post-deploy Job fails | FluxCD surfaces; manual retry or re-reconcile |
| SIF missing at runtime | Fail-fast in SLURM script with clear error |
| Cluster filesystem fills up | 90-day GC in the post-deploy Job |
| SIF format mismatch | CI uses Apptainer (same as cluster); validated via `apptainer inspect` |

## Related files

| File | Role |
|------|------|
| `.github/workflows/CI-full.yml` → `build-and-publish-sif` | CI build + publish SIF (opt + batch only) |
| `docker/swarm/serverconfig-uch.sh:91-104` | Path convention |
| `docker/swarm/docker-compose.yml` | Env-var plumbing into vcell-submit |
| `vcell-core/.../PropertyLoader.java` | `htc_*_apptainer_image` property definitions |
| `vcell-server/.../SlurmProxy.java` | Derives SIF filename, emits SLURM script |
| `vcell-server/.../HtcSimulationWorker.java` | Validates required properties at startup |
| `docker/build/Dockerfile-submit-dev` | Env var defaults + `-D` system properties |
| `docker/swarm/dev_singularity_build.sh` | Developer manual fallback |
| `vcell-fluxcd/kustomize/config/*/submit.env` | Per-site ORAS URLs (separate repo) |
| `vcell-fluxcd/kustomize/*/vcell-sif-prepull.yaml` | Kubernetes Job manifest (separate repo) |
