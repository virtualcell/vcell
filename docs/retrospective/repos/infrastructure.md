# Infrastructure, deployment & test-data repos

Supporting repositories (all org `virtualcell`) that carry VCell's Kubernetes
deployment, operational tooling, and regression test-data — distinct from the
application source. The substantive member is **`vcell-fluxcd`**, the GitOps
configuration that deploys VCell to Kubernetes; the rest are small,
single-purpose, or historical.

**Group · PRs (non-bot) · Releases · active span · key contributors**
`infrastructure/test-data · 21 PRs · 12 releases (test_suite only) · 2020-12 → 2026-06 · jcschaff, Ezequiel-Valencia, danv61`

---

## `vcell-fluxcd` — Kubernetes GitOps deployment

> FluxCD + Kustomize configuration that deploys the entire VCell server stack to Kubernetes. The operational backbone of modern (post-Swarm) deployment.
> **19 PRs · 0 releases · 2024-05 → 2026-06 · jcschaff (294 commits), Ezequiel-Valencia (119)** · languages: Shell + HCL (Terraform).

### Project background (code-first)

The repo is a Kustomize tree under `kustomize/`: a `base/` of Deployment/Service
manifests for every VCell server component (`rest`, `api`, `data`, `db`,
`webapp`, `sched`, `submit`, `export`, `s3proxy`, `mongodb`, ActiveMQ
`activemqint`/`activemqsim` and Artemis), per-environment `config/<env>/*.env`
ConfigMap sources, and `overlays/<env>/` that layer in secrets, persistent
volumes, ingress, and TLS issuers. FluxCD watches the repo and continuously
reconciles cluster state; `clusters/` holds the Flux bootstrap (GitRepository +
Kustomization CRs) for each cluster (`vxrails` for prod/stage/dev, `jim-minikube`
for local). The ingress (`overlays/<env>/vcell-ingress.yaml`) is the canonical
routing map: `/api/v1/` → `rest`, `/api/v1/export` → `export`, `/api/v0/` and
`/swversion` → legacy `api`, `/q/openapi` + `/q/swagger-ui` → `rest`, `/n5Data` →
`s3proxy`, and `/` → `webapp`, all behind nginx + Let's Encrypt TLS. There are no
releases or tags — the repo *is* the live desired-state, and "deployment" is a
git commit that Flux picks up.

### Timeline (themed milestones)

**2024 H1 — bootstrap & the local-cluster / island model.** The repo opened in May
2024. The first wave established two ideas at once: a deployable base, and the
ability to stand up *isolated* VCell instances. [#2](https://github.com/virtualcell/vcell-fluxcd/pull/2)
(jcschaff, 42 files) added "island" configurations for minikube testing and made
the Angular webapp URIs dynamic so a cluster could be addressed without
hard-coded hostnames; [#3](https://github.com/virtualcell/vcell-fluxcd/pull/3)
(Ezequiel-Valencia, 31 files) added a one-command local developer k8s cluster.
[#7](https://github.com/virtualcell/vcell-fluxcd/pull/7) was a large refactor
(net −341 lines across 59 files) that consolidated the early sprawl into the
`base` + `config` + `overlays` layout, and introduced the `remote-base` /
`island-base` overlay families with per-developer instances
(`overlays/island-instances/{jim,ezequiel}`, `remote-instances/...`). This
"island vs. remote" split — self-contained NFS/Mongo-on-local-drive instances vs.
cluster-attached remotes — is the structural backbone the rest of the history
builds on.

**2024 H2 — S3 proxy hardening.** Several PRs tightened the `s3proxy` component
that fronts N5/object data: a new S3 URL ([#8](https://github.com/virtualcell/vcell-fluxcd/pull/8)),
regex path allow-listing ([#10](https://github.com/virtualcell/vcell-fluxcd/pull/10)),
and request filtering ([#11](https://github.com/virtualcell/vcell-fluxcd/pull/11),
22 files) so only legitimate S3-style requests reach the proxy. A small but
operationally important change raised the nginx HTTP body-size limit
([#13](https://github.com/virtualcell/vcell-fluxcd/pull/13)) to let large model
uploads through the ingress.

**2025 H1 — storage volumes and Auth0-as-code.** [#14](https://github.com/virtualcell/vcell-fluxcd/pull/14)
implemented volume mounts for the REST pod (22 files), wiring the `rest` service
to the CFS persistent volumes (`cfs07/cfs09/cfs12_vcell_pv`). The notable
architectural addition was [#15](https://github.com/virtualcell/vcell-fluxcd/pull/15):
a Terraform configuration under `terraform/Auth0/` (`apis.tf`, `applications.tf`,
`imports.tf`, `setup.tf`) that brings VCell's Auth0 tenant — the OIDC APIs and
applications the REST service authenticates against — under
infrastructure-as-code, giving "a state of configuration truth, the ability to
create diffs, and high-level abstractions for resource creation" (PR body). This
is the source of the repo's HCL language bytes. [#19](https://github.com/virtualcell/vcell-fluxcd/pull/19)
and [#20](https://github.com/virtualcell/vcell-fluxcd/pull/20) followed with the
Kustomize-edit fix and Terraform README.

**2025 H2 — VCell-AI and the export pipeline.** [#21](https://github.com/virtualcell/vcell-fluxcd/pull/21)
added an `overlays/ai/` family deploying the VCell-AI stack — `ai-backend`,
`ai-frontend`, a `qdrant` vector DB, NFS PVCs, and a SealedSecret helper
(`sealed_secret_ai.sh`). [#22](https://github.com/virtualcell/vcell-fluxcd/pull/22)
("Export deployment", 48 files) was the largest 2025 change: it promoted the data
**export** service to a first-class deployment with its own Artemis broker
(`artemismq.yaml`, `broker.xml`), per-env `exporter.env`, ingress route
(`/api/v1/export`), and a sealed-secret script — and crucially **filled in the
`stage` overlay** (`overlays/stage/*`), completing the prod/stage/dev triad.

**2026 H1 — operational reliability (jcschaff).** The most recent wave is pure ops
hardening driven by production incidents. [#23](https://github.com/virtualcell/vcell-fluxcd/pull/23)
forwarded the ActiveMQ Classic broker logs to container stdout (so they reach
Loki); [#24](https://github.com/virtualcell/vcell-fluxcd/pull/24) mounted a
`/dump` emptyDir for JVM heap dumps and raised the `data` pod memory limit;
[#25](https://github.com/virtualcell/vcell-fluxcd/pull/25) repointed stage/prod
`sched`/`submit` batch-host to `login.hpc.cam.uchc.edu`. The standout is
[#26](https://github.com/virtualcell/vcell-fluxcd/pull/26): a one-file fix
patching `dnsConfig` (`single-request-reopen`, `ndots:2`) onto the `submit`/`sched`
deployments to kill an intermittent ~5s glibc DNS timeout that, stacked twice,
exceeded VCell's 10s `sbatch` submit timeout and produced spurious JOB_FAILED /
Better Stack critical incidents. The in-diff comment carries the full
conntrack/DNAT race diagnosis — a good example of the repo's diffs being more
trustworthy and informative than its PR titles.

### Deploy mechanics (GitOps)

There is one workflow, `.github/workflows/deploy.yaml`, triggered manually
(`workflow_dispatch` with `overlay`, `tag`, `swversion` inputs, default
`stage`). It installs Kustomize, runs `kustomize edit set image
ghcr.io/virtualcell/vcell-<component>:<tag>` for every component, `sed`-patches
`softwareVersion`/`VCELL_SOFTWAREVERSION` and the Apptainer/Singularity solver
image refs into `shared.env`/`submit.env`, then **commits the change back to the
repo** as `jcschaff`. FluxCD on the cluster then reconciles the new desired
state. So a deploy = a workflow-authored git commit; rollback = git revert.
`remote-*` and `island-*` deploys are documented as manual.

### Notable PRs

| PR | Date | Author | Why it matters |
|----|------|--------|----------------|
| [#2](https://github.com/virtualcell/vcell-fluxcd/pull/2) | 2024-05-20 | jcschaff | Island/minikube configs + dynamic Angular URIs — first deployable shape |
| [#3](https://github.com/virtualcell/vcell-fluxcd/pull/3) | 2024-05-20 | Ezequiel-Valencia | One-command local developer k8s cluster |
| [#7](https://github.com/virtualcell/vcell-fluxcd/pull/7) | 2024-05-29 | Ezequiel-Valencia | Big refactor → `base`/`config`/`overlays` + remote/island split |
| [#11](https://github.com/virtualcell/vcell-fluxcd/pull/11) | 2024-09-17 | Ezequiel-Valencia | S3 request filtering on the s3proxy |
| [#14](https://github.com/virtualcell/vcell-fluxcd/pull/14) | 2025-01-02 | Ezequiel-Valencia | REST pod CFS volume mounts |
| [#15](https://github.com/virtualcell/vcell-fluxcd/pull/15) | 2025-04-16 | Ezequiel-Valencia | Terraform-as-code for the Auth0 OIDC tenant |
| [#21](https://github.com/virtualcell/vcell-fluxcd/pull/21) | 2025-10-22 | Ezequiel-Valencia | VCell-AI overlay (backend/frontend/qdrant) |
| [#22](https://github.com/virtualcell/vcell-fluxcd/pull/22) | 2025-10-28 | Ezequiel-Valencia | Export service + Artemis broker; completes `stage` overlay |
| [#24](https://github.com/virtualcell/vcell-fluxcd/pull/24) | 2026-05-07 | jcschaff | `/dump` heap-dump volume + data memory limit |
| [#26](https://github.com/virtualcell/vcell-fluxcd/pull/26) | 2026-06-11 | jcschaff | DNS `single-request-reopen`/`ndots:2` fix for sbatch submit timeouts |

### Tech & stack notes

- **Kustomize** (base + per-env overlays) reconciled by **FluxCD**; nginx ingress
  + cert-manager / Let's Encrypt for TLS.
- **Terraform** (`terraform/Auth0/`) manages the Auth0 OIDC tenant — the only HCL.
- Secrets are **SealedSecrets**; `kustomize/scripts/sealed_secret_*.sh` (Shell)
  generate them per component (api, rest, jwt, ghcr, artemis, ai). This is the
  bulk of the Shell bytes.
- Solvers ship as **Apptainer/Singularity** images (`oras://ghcr.io/...`)
  referenced from `submit.env`, plus a `vcell-sif-prepull-job` to warm them.
- Observability: Prometheus, Grafana, Fluent Bit configs under `clusters/`.
- Confirms repo CLAUDE.md: K8s deployment lives here, not in `docker/swarm/`
  (which is build-time staging only).

---

## `devops` — Ansible service-restart tooling

> Ansible role for restarting VCell's (then Docker-based) services on the deployment hosts. **1 PR · 0 releases · 2023-09 → 2023-10 · Ezequiel-Valencia (21 commits), mpw6 (1)**.

The README is a stub, but the commit history is unambiguous: an Ansible scaffold
(`Scalfolding for Ansible Scripts`, a Python venv for `ansible`/`ansible-lint`, a
Molecule test setup, hosts/vars, and a `restart_services`/`restart_docker_services`
role) developed Sept–Oct 2023 and capped by [#2](https://github.com/virtualcell/devops/pull/2)
"Restart services". It predates `vcell-fluxcd` and the Kubernetes migration; the
restart concern it addressed now lives as `restart_deployments_VCell_{dev,prod}.sh`
inside `vcell-fluxcd/kustomize/scripts/`. Effectively superseded and dormant.

## `vcdb` — published-model export data (regression test data)

> Despite the name and "Shell" language tag, this is **not** database schema/migration tooling — it is a **data repository of exported published BioModels** (OMEX, SBML, VCML; ~2700 files). **0 PRs · direct-commit · 2021-09 → 2025-01 · danv61, jcschaff (Jim Schaff), CodeByDrescher (Logan Drescher)**.

The tree is `published/biomodel/omex/native/extracted/biomodel_<id>/` directories,
each holding a `.sedml`, `diagram.png`, and `metadata.rdf`, plus
`public/biomodel/vcml/modelKeyList.txt`. The commit log tells the story: Moraru
seeded published biomodels (2021-09); danv61 ran repeated CLI `vcml → omex` batch
exports through 2021–2022 ("batch export vcml to omex", "Latest, with diagrams
and all simulations"); jcschaff added "85 valid omex files for vcell published
models" feeding the biosimulations.dev pipeline (2024-02); CodeByDrescher renamed
files to avoid Unicode errors (2025-01). In short: a curated corpus of VCell's
published models in standards formats, used to regression-test SED-ML/OMEX export
and to seed biosimulations.org. The lone Shell bytes are export/helper scripts.
*(Correction to catalog.md, which described this as "schema/migration/backup"
tooling — it is test-data.)*

## `biomodelsdb_mirror` — local mirror of the BioModels database

> A frozen snapshot of the EBI BioModels curated branch, kept locally so VCell SBML-import regression tests avoid latency/bandwidth against the official site (per README). **0 PRs · direct-commit · 2023-02 · jcschaff**.

Two commits, both Feb 2023: `Initial commit` and `initial download of Biomodels
curated branch on 2023.02.18`. Pure data, no code — exactly what the README
states. It exists to make the SBML-import test suite hermetic and deterministic.

## `usermaterials` — tutorial / training assets

> User-facing tutorial materials (PPTX/PDF), not code. **0 PRs · direct-commit · 2022-08 → 2022-09 · vcellmike (Michael Blinov, 13 commits), ACowan0105, mpw6**.

The empty README belies a small, clear scope: Michael Blinov uploaded VCell 7.2
tutorial decks (SimpleFRAP, FRAP-binding) and ACowan0105 added Moving-Boundary
tutorial PPTX/PDF, all in Aug–Sept 2022. A static asset drop for training; no
activity since.

## `test_suite` — historical weekly regression-report generator

> Python/Shell tooling that generated VCell's weekly SBML/OMEX comparison reports, with the reports themselves published as dated GitHub releases. **1 PR · 12 releases · 2020-12 → 2021-03 · gmarupilla (PR), plus earlier contributors**.

Under `report_generation/` it has a `combine/` (OMEX assembly), `comparator/`
(result comparison + report), an `sbml/sbml_fetcher.py`, a `run_script/run_docker.py`
Docker runner, and a `core.py` driver, wired to a `CI.yml` workflow. The 12
releases are weekly snapshots tagged `report_MM_DD_YYYY` from 2020-12-28 through
2021-03-15. [#6](https://github.com/virtualcell/test_suite/pull/6) (gmarupilla,
2021-01) added unit tests and reorganized the code. The cadence stops in March
2021 — this regression-reporting role was later absorbed into the main project's
CI test groups, so the repo is historical.

---

### Key contributors (group)

- **jcschaff (Jim Schaff)** — owns `vcell-fluxcd` (294 commits), authored the
  2026 reliability fixes; seeded `vcdb` export corpus and `biomodelsdb_mirror`.
- **Ezequiel-Valencia** — primary `vcell-fluxcd` engineer (119 commits): the
  base/overlay refactor, s3proxy, Auth0 Terraform, VCell-AI and export overlays;
  also authored the `devops` Ansible tooling.
- **danv61 (Dan Vasilescu)** — drove the `vcdb` published-model OMEX export runs.
- **CodeByDrescher (Logan Drescher)** — `vcdb` maintenance.
- **vcellmike (Michael Blinov)** — `usermaterials` tutorial content.
