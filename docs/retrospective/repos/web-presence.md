# Web presence — `CompCellBio` + `vcellwordpress`

VCell's public-facing web properties: the Computational Cell Biology community/course
site (an Angular SPA) and the Kubernetes deployment artifacts for the vcell.org
WordPress site. Two small, mostly single-maintainer repos grouped here because both
serve the project's outward web presence rather than the simulation platform itself.

**Group:** Web presence · **PRs (non-bot):** 4 (all in `CompCellBio`) · **Releases:** 0 ·
**Active span:** Dec 2020 → Apr 2026 (`CompCellBio`); Feb 2026 (`vcellwordpress`) ·
**Key contributors:** smstaurovsky, vcellmike, AlexPatrie (`CompCellBio`); jcschaff (`vcellwordpress`)

---

## `CompCellBio` — Computational Cell Biology community & workshop site

Angular SPA (generated with Angular CLI 10.0.1, later carried on Angular 11 / Node 14)
that serves as the public website for the Computational Cell Biology (CCB) community and
its training workshops. Despite the brief's "2-month burst" framing, the repo is in fact
long-lived: created **2020-12-10** and still being edited in **2026-04**. The 4 PRs all
fall in a narrow Dec 2023–Jan 2024 window; the rest of the work — the overwhelming
majority — happens via direct commits to `master` (149 by smstaurovsky, 39 by vcellmike,
6 by AlexPatrie).

The site is a static, content-driven SPA with no backend. Routes (from
`src/app/app-routing.module.ts`) cover `home`, `team`, `news`, `standards`, `networks`,
`educational-resources`, `database-resources`, `software-resources`, `publications`,
`upcoming-workshops`/`previous-workshops`, the `ccbworkshop` course page, and an `adboard`
(advisory board) page. Language mix is HTML-heavy (133 KB HTML, 54 KB SCSS, 26 KB
TypeScript) — i.e. it is essentially a hand-authored content site wrapped in Angular
component shells, not an application with significant logic.

### Timeline

**2020–2023 (background, pre-retrospective relevance is light).** GMarupilla scaffolds the
Angular app in Dec 2020; smstaurovsky and vcellmike then maintain it through 2021–2023 as
a rolling content site (team, publications, resources, workshop announcements), almost
entirely via direct commits.

**Dec 2023 – Jan 2024 — the only PR window (Angular/Node hygiene + content).** AlexPatrie
opened all four of the repo's PRs here. [#1](https://github.com/virtualcell/CompCellBio/pull/1)
pinned the toolchain to Node 14 / Angular 11: it added a `.nvmrc`, rewrote
`package-lock.json` to express Angular-11-compatible dependencies (the +561/-20527 line
delta is the lockfile regeneration, not feature work), and fixed a failing
`app.component.spec.ts`. [#2](https://github.com/virtualcell/CompCellBio/pull/2) and
[#3](https://github.com/virtualcell/CompCellBio/pull/3) were branch-hygiene follow-ups —
reconciling `master`'s `package.json`/lockfile state with #1 and re-applying a workshop
text edit that had been made directly on `master` (#3 is an empty 0-file diff, a
merge/branch artifact). [#4](https://github.com/virtualcell/CompCellBio/pull/4) is a
3-line content edit to `ccbworkshop.component.html`, changing the course-application
contact from Dr. Michael Blinov to Dr. Leslie Loew and the deadline from Jan 12 to Jan 15.
This window is best understood as a one-time dependency-cleanup effort grafted onto an
otherwise direct-commit content workflow.

**2024 – 2026 — back to direct-commit content maintenance.** Activity continued steadily
(7 commits Jul 2024, 15 in Sep 2024, surges of 36 in Dec 2025 and 55 in Jan 2026), all by
smstaurovsky. The Dec 2025 / Jan 2026 surge was a substantial styling and accessibility
pass — repeated "WCAG-compliant typography," `h1`→`h2` heading-level corrections, and SCSS
color/layout refactors across the resources, standards, and workshop components — plus
ongoing content updates (COPASI 4.46 release news, the 2026 workshop schedule). The latest
edits (Apr 2026) rotate the workshop content: adding the June 2026 CCB workshop, archiving
prior workshops, and updating the announcement iframe.

### Notable PRs/commits

| Link | Date | Author | Why it matters |
|------|------|--------|----------------|
| [#1](https://github.com/virtualcell/CompCellBio/pull/1) | 2023-12-12 | AlexPatrie | Pinned toolchain to Node 14 / Angular 11 (`.nvmrc` + lockfile rewrite), fixed failing spec — the one substantive maintenance PR |
| [#2](https://github.com/virtualcell/CompCellBio/pull/2) | 2024-01-12 | AlexPatrie | Reconciled `master` lockfile/`package.json` with #1; re-applied a master-side workshop text change |
| [#4](https://github.com/virtualcell/CompCellBio/pull/4) | 2024-01-12 | AlexPatrie | Workshop application contact changed Blinov→Loew, deadline Jan 12→Jan 15 (typical content edit) |
| direct commits | 2026-01 | smstaurovsky | WCAG/accessibility pass: heading-level fixes, SCSS typography & color refactors across components |
| direct commits | 2026-04 | smstaurovsky | Rotated workshop content for the June 2026 CCB workshop; archived previous workshops |

### Key contributors

- **smstaurovsky** (149 commits) — dominant maintainer; nearly all content updates and the
  2026 accessibility/styling overhaul, almost entirely via direct commits.
- **vcellmike** (39 commits) — early and ongoing content/structure contributions.
- **AlexPatrie** (6 commits) — the Dec 2023–Jan 2024 dependency-hygiene PRs (Node/Angular pinning).

### Tech & stack notes

Angular CLI 10.0.1 scaffold, carried on Angular 11 / Node 14 (pinned in #1). Karma unit
tests, Protractor e2e (per the boilerplate README; not meaningfully exercised). No backend,
no releases, no CI/CD evident in-repo — it ships as a static SPA. Content lives directly in
component HTML, which is why the workflow is edit-and-commit rather than PR-driven.

---

## `vcellwordpress` — Kubernetes deployment for the vcell.org WordPress site

Deployment artifacts (Kustomize + Bitnami WordPress Helm chart + sealed-secrets) that move
the vcell.org WordPress site off a legacy VM-based install and onto Kubernetes, following
the same Kustomize/FluxCD conventions used elsewhere in VCell's infrastructure (the "cam"
conventions). The repo is jcschaff-only, direct-commit (0 PRs), and was created and
populated over a few days in **Feb 2026**. Languages reported are trivial (Shell 2.4 KB,
Python 0.5 KB) — the substance is YAML manifests and a migration runbook, not code. `main.py`
is an untouched PyCharm stub and `pyproject.toml` an empty scaffold; they are noise, not
deployment logic.

### Layout (from README + tree)

- `kustomize/base/` — `kustomization.yaml` references the Bitnami WordPress Helm chart
  (`charts.bitnami.com/bitnami`, pinned `version: "24.0.4"`) plus a shared `values.yaml`
  (resources, MariaDB subchart, 10 Gi persistence, TLS ingress).
- `kustomize/overlays/vcell-wordpress-dev` and `…-prod` — per-environment overlays, each
  with its own `values.yaml` (prod sets `hostname: vcell.org`, larger 20 Gi/10 Gi PVCs,
  `existingSecret: wordpress-secrets`), a `secrets.sh` generator, a `secrets.template.dat`,
  and a committed `wordpress-sealed-secret.yaml`.
- `migration/MIGRATION_NOTES.md` — the migration runbook/checklist (DB export-import with
  URL search-replace, wp-content/uploads/themes/plugins inventory, NFS path remapping,
  PHP-compatibility audit of plugins).

### Timeline (Feb 2026)

The whole repo is four commits over three days:

- **2026-02-13** `d52401c5f` (Jim Schaff) — *WIP: Initial project structure for VCell
  WordPress K8s deployment*: lays down the base/overlay Kustomize tree, the Bitnami chart
  reference, dev/prod `values.yaml`, and `MIGRATION_NOTES.md`.
- **2026-02-13** `8cb60bc61` — *Adds sealed secrets for credentials*: introduces the
  `secrets.sh` / `secrets.template.dat` flow that pipes `kubectl create secret … | kubeseal`
  to produce committable `wordpress-sealed-secret.yaml` files (WordPress admin password,
  MariaDB user + root passwords), so no plaintext secrets land in git.
- **2026-02-15** `071e46672` — *rename k8s top level directory to kustomize to follow cam
  convensions* [sic]: aligns the repo layout with the org's FluxCD/Kustomize conventions
  (the `vcell-fluxcd` `kustomize/` pattern).
- **2026-02-15** `40123b8a3` — *sealed secrets for mariadb and wordpress*: finalizes the
  sealed-secret material for both the WordPress and MariaDB credentials.

The migration itself (DB dump, search-replace of hardcoded URLs, wp-content/NFS migration,
plugin compatibility) is captured as an open checklist in `MIGRATION_NOTES.md` rather than
executed in-repo — i.e. as of this snapshot the repo is the deployment scaffold and runbook,
not a completed cutover.

### Notable commits

| Link | Date | Author | Why it matters |
|------|------|--------|----------------|
| [`d52401c`](https://github.com/virtualcell/vcellwordpress/commit/d52401c5f) | 2026-02-13 | jcschaff | Initial Kustomize/Bitnami-Helm structure + migration runbook for moving vcell.org WordPress off-VM to K8s |
| [`8cb60bc`](https://github.com/virtualcell/vcellwordpress/commit/8cb60bc61) | 2026-02-13 | jcschaff | sealed-secrets workflow (`secrets.sh` → `kubeseal`) so credentials are committable, not plaintext |
| [`071e466`](https://github.com/virtualcell/vcellwordpress/commit/071e46672) | 2026-02-15 | jcschaff | Renamed top-level dir to `kustomize/` to match org FluxCD conventions |
| [`40123b8`](https://github.com/virtualcell/vcellwordpress/commit/40123b8a3) | 2026-02-15 | jcschaff | Finalized sealed secrets for WordPress + MariaDB |

### Key contributors

- **jcschaff** (4 commits) — sole author; the entire deployment scaffold and migration plan.

### Tech & stack notes

Kustomize (`kustomize.config.k8s.io/v1beta1`) with `--enable-helm`; Bitnami WordPress Helm
chart pinned to `24.0.4` (bundles a MariaDB subchart); Bitnami Sealed Secrets via `kubeseal`
for credential management; per-environment dev/prod overlays with TLS ingress (prod hostname
`vcell.org`). No CI/CD or releases in-repo; intended to be applied via `kubectl apply -k …`
(and presumably reconciled by the org's FluxCD setup). The Python files are inert scaffolding.
