---
name: deploy
description: Cut a VCell release and deploy it — pick the next version, write the changelog, tag + GitHub release (triggers the CI-full image build), then CD-sites to a site (alpha→dev namespace, rel→prod, test→stage). Use when asked to "deploy to alpha/dev/prod/stage", "cut a release", or "bump the version and deploy".
---

# VCell release & deploy

**Always present the full plan (version, target site, ordered steps) and get the
user's explicit confirmation BEFORE tagging, releasing, or deploying anything.**
The first three version parts are release-engineering decisions a human makes
(`docs/RELEASING.md`); the target site decides who is affected. Research and
draft autonomously; execute only after sign-off.

## Version scheme

`MAJOR.MINOR.PATCH.BUILD` — `docs/RELEASING.md` is authoritative.

- Current latest: `gh release list --repo virtualcell/vcell --limit 1` (or `git tag --sort=-creatordate | head -1`).
- Bugfix-only release → bump PATCH, reset BUILD to `01` (e.g. 8.0.4.01 → 8.0.5.01).
- New backwards-compatible feature → bump MINOR.
- Iterating on an already-cut line (another build of the same fixes/features) → increment BUILD only.
- The version lives ONLY in the git tag / GitHub release — there is no version file in the repo.

## Site mapping

CD-sites (`.github/workflows/site_deploy.yml`) uses legacy site names; its last
job dispatches `vcell-fluxcd`'s `deploy.yaml`, which flips the kustomize overlay
image tags and lets Flux reconcile the namespace.

| `vcell_site` | K8s overlay / namespace | URL | installer dir (vcellapi.cam.uchc.edu) |
|---|---|---|---|
| `alpha` | `dev` | vcell-dev.cam.uchc.edu | webstart/Alpha |
| `rel` | `prod` | vcell.cam.uchc.edu | webstart/Rel |
| `test` | `stage` | vcell-stage.cam.uchc.edu | webstart/Test |

## Scripts

The gates below are implemented in `tools/release/` (see its README for why each is strict).
Prefer them over ad-hoc commands, and never keep a copy in a scratch directory -- during the
8.0.12.01 cut a scratch copy was deleted by the OS mid-release.

```bash
tools/release/merge-when-green.sh <branch>                       # step 1
tools/release/release-and-deploy.sh <version> <site> <notes.md>  # steps 2-4
tools/release/verify-deploy.sh <version> <site>                  # step 5
```

## Procedure

0. **Preconditions.** `master` is green: CI passed on the merge, and
   `regression.yml` passed on master (after an admin-merge it must be kicked
   manually: `gh workflow run regression.yml --ref master`). Do not tag a
   master that hasn't passed regression.

1. **Release-cut docs** (per `docs/RELEASING.md`): add a `## [X.Y.Z.BB]`
   section to `CHANGELOG.md` (Highlights + categories + "Notes for API
   consumers"), fold notable items into `release-notes/major/<MAJOR.MINOR>.md`,
   and land both on master via PR **before** tagging, so the tag points at a
   self-describing tree.

2. **Tag + release.** `gh release create X.Y.Z.BB --target master
   --title "X.Y.Z.BB (<short description>)" --notes-file <changelog-section.md>`.
   Publishing the release auto-triggers `CI-full.yml`, which builds and pushes
   all Docker images tagged `X.Y.Z.BB` to ghcr.io.

3. **Watch CI-full to completion.** NEVER cancel it before the tag-and-push
   job — that job (needs ALL docker builds) creates the friendly
   `version.build` image tags every deploy pulls; without them the deploy fails
   with "manifest unknown". If a job fails, recover with
   `gh run rerun <id> --failed`. The clientgen image is release-critical:
   desktop installers must match the server version.

4. **Dispatch CD-sites.**
   `gh workflow run site_deploy.yml --ref master -f vcell_version=X.Y.Z
   -f vcell_build=BB -f vcell_site=<site> -f server_only=false`
   (checkout ref is the tag `X.Y.Z.BB`, so the tag must exist first).
   This builds + notarizes desktop installers, publishes them to
   `webstart/<Site>`, then POSTs `vcell-fluxcd/deploy.yaml` with
   `{overlay, tag, swversion}`.

5. **Verify.** A "Changed image tag of <overlay> to X.Y.Z.BB" commit appears in
   vcell-fluxcd; `KUBECONFIG=~/.kube/kubeconfig_vxrails.yaml kubectl -n <ns>
   get pods` shows pods Running on the new tag; the site URL loads; then a
   targeted smoke test of whatever this release changed.

6. **Post-deploy.** Only when PROD rolls to a new `MAJOR.MINOR` line:
   transcribe `release-notes/major/<MAJOR.MINOR>.md` to the vcell.org
   accordion (see `docs/RELEASING.md` "vcell.org transcription").
