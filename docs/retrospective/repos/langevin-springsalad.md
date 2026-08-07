# LangevinNoVis01 & SpringSaLaD — particle-based spatial-stochastic solver and its GUI app

A solver/application pair under the `cam-center` org: **LangevinNoVis01** is a
particle-based, spatial, stochastic Langevin-dynamics solver (Java), and
**SpringSaLaD** is the Java GUI desktop application that builds models for it and
visualizes results. The solver is also embedded into VCell proper as the
"Langevin" application/solver.

**Combined · 33 non-bot PRs · 40 GitHub releases · 2023-07 → 2026-06 · top contributors danv61, jcschaff, moraru, Ezequiel-Valencia, pjmichalski**

- **LangevinNoVis01** (solver): 23 non-bot PRs, 21 releases (`1.1.2` → `1.4.7`), 2023-07 → 2026-06. Top commit authors: danv61 (81), jcschaff (38).
- **SpringSaLaD** (app): 10 non-bot PRs, 19 releases (`2.2.1` → `2.4.6`; the `2.2.x` tags from 2021–2022 predate the retrospective window and reflect the pre-Maven era), 2024-05 → 2026-02. Top commit authors: danv61 (44), moraru (30), Ezequiel-Valencia (26).

## Project background

LangevinNoVis01 (the "NoVis" = headless, no-visualization fork of the solver
core) simulates biochemical systems as diffusing particles with excluded volume,
linked by harmonic springs, with binding/unbinding, state-transition, allosteric,
and decay reactions — the SpringSaLaD algorithm of Michalski & Loew 2016
(*"SpringSaLaD: A Spatial, Particle-Based Biochemical Simulation Platform with
Excluded Volume"*, PMID 26840718). The code lives under
`edu.uchc.cam.langevin.*` (model objects, reactions, counters, and the
`langevinnovis01` integrator `MySystem`). SpringSaLaD (`org.springsalad.*`) is
the model-building/visualization GUI on top of it: a Jmol-based 3D viewer,
cluster-analysis tables, and a run launcher that shells out to the native solver
binaries it bundles per-platform (`localsolvers/{linux64,macos_arm64,macos_x86_64,win64}`).
Both are pure Java (solver ~458 KB, app ~1.1 MB); the solver additionally ships
as a GraalVM native image.

## Timeline

### 2023 H2 — solver modernized and packaged as native binaries (LangevinNoVis01)

The repo's first tracked work, [#1](https://github.com/cam-center/LangevinNoVis01/pull/1)
(jcschaff, Jul 2023), was a foundational rebuild: it moved the source into the
`edu.uchc.cam.langevin` package, configured Maven for **GraalVM native-image**
builds, added a **picocli** CLI (`langevin simulate ...`, with build-time
annotation processing via `picocli-codegen` so reflection works in native
images), and set up GitHub Actions to compile native executables for macOS,
Windows, and Linux. [#4](https://github.com/cam-center/LangevinNoVis01/pull/4)
([1.1.2](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.1.2), Dec
2023) wired those per-platform binaries to be uploaded as release assets. This is
what lets SpringSaLaD (and VCell) ship a self-contained native solver rather than
requiring a JVM at simulation time.

### 2024 H1 — VCell server integration, then a physics fix (LangevinNoVis01) and the app's Maven rebuild (SpringSaLaD)

[#5](https://github.com/cam-center/LangevinNoVis01/pull/5)
([1.2.0](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.2.0), Feb
2024) is the **VCell integration** milestone. It added an `org.vcell.messaging`
layer: a `VCellMessaging` interface that emits `WorkerEvent` status/progress
either to stdout/stderr (local) or to a JMS message broker (remote), driven by a
`MessagingConfig` JSON file (broker host/port/creds, vcell username, simKey,
taskID, jobIndex) passed via `--vc-send-status-config`. This is exactly the
contract VCell's server uses to run the solver as a remote compute job and stream
progress back — it is why the solver is integrated into VCell as the "Langevin"
application (the main `vcell` repo references Langevin across ~89 Java files,
including dedicated `LangevinOptionsPanel`, cluster/molecule visualization
panels, and `ClientSimManager` wiring). The follow-on point releases hardened the
messaging path: static-linked Linux exe ([1.2.2](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.2.2)),
tolerate an extra `-tid` arg, local data reduction in the solver
([1.2.4](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.2.4)),
`--version` with git detail, and timestamp-throttled progress
([#6](https://github.com/cam-center/LangevinNoVis01/pull/6),
[1.2.6](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.2.6)).

[#7](https://github.com/cam-center/LangevinNoVis01/pull/7)
([1.3.0](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.3.0), May
2024) was a genuine **physics correction**, not a refactor: the spring force used
to compute site position was made to depend on the site's diffusion rate `D`
(`magForce = springConstant·D·(L0−length)` per site), and the spring constant was
correspondingly dropped from `1e8` to `100`. [1.3.1](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.3.1)
split the macOS build into separate Intel and ARM executables (CI matrix gained
`macos-13` + `macos-14`), and [#8](https://github.com/cam-center/LangevinNoVis01/pull/8)
([1.3.2](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.3.2)) fixed
a crash on input files given without an explicit path.

On the app side, SpringSaLaD [#1](https://github.com/cam-center/SpringSaLaD/pull/1)
(jcschaff, May 2024) was the parallel rebuild: it ported the build from
Ant/install4j to **Maven**, refreshed the GUI (`MainGUI`, `DrawPanel3D`, the Jmol
`Integration`, cluster-analysis tables), and bundled the matching per-platform
`localsolvers`. Release [2.3.2](https://github.com/cam-center/SpringSaLaD/releases/tag/2.3.2)
("fix site diffusion bug in Langevin solver, upgrade UI") pairs with the solver's
`1.3.0` diffusion fix.

### 2024 H2 — visualization polish (SpringSaLaD)

[#2](https://github.com/cam-center/SpringSaLaD/pull/2)
([2.3.3](https://github.com/cam-center/SpringSaLaD/releases/tag/2.3.3))
added **MP4 movie export** of the 3D viewer via the `jcodec` library
(`MovieMaker.makeMP4`), and [#3](https://github.com/cam-center/SpringSaLaD/pull/3)
([2.3.4](https://github.com/cam-center/SpringSaLaD/releases/tag/2.3.4)) refreshed
the bundled macOS solvers.

### 2025 — new binding algorithm, reproducibility, and cluster post-processing (LangevinNoVis01)

This was the solver's most active year. [#10](https://github.com/cam-center/LangevinNoVis01/pull/10)
([1.3.3](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.3.3))
added explicit **random-seed** support, making runs reproducible. The pivotal
science change was [#12](https://github.com/cam-center/LangevinNoVis01/pull/12)
(Apr 2025), implementing "Boris' new algorithm" for binding kinetics: binding
probability moved from the linear `λ·dt` to `1 − e^(−λ·dt)` (and likewise for
unbinding with `1 − e^(−kOff·dt)`), with an **intrinsic on-rate** derived from the
diffusion-limited rate `kD = 4πRD` — `kOnIntrinsic = (kon·kD)/(kD − kon)` — so
that user-supplied macroscopic `kon`/`koff` are converted into the microscopic
per-step probabilities the simulation actually needs (it throws "Kon is too
large" when the requested rate exceeds the diffusion limit). [#32](https://github.com/cam-center/LangevinNoVis01/pull/32)
([1.4.6](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.4.6), Jan
2026) later refined that lambda computation and added a tutorial example model.

The other 2025 thread was **cluster analysis / post-processing**:
[#14](https://github.com/cam-center/LangevinNoVis01/pull/14) wrote per-run cluster
info to JSON; [#18](https://github.com/cam-center/LangevinNoVis01/pull/18) added a
new `post` CLI subcommand (`PostCommand`, `ConsolidationPostprocessor`,
`ClusterStatisticsCalculator`) that consolidates cluster statistics across
multiple runs into a `SolverResultSet`; [#20](https://github.com/cam-center/LangevinNoVis01/pull/20)
and [#26](https://github.com/cam-center/LangevinNoVis01/pull/26) (the year's
largest, 77 files) reworked and time-sorted cluster output files. Reliability
fixes landed for JSON serialization ([#21](https://github.com/cam-center/LangevinNoVis01/pull/21))
and, notably, [#23](https://github.com/cam-center/LangevinNoVis01/pull/23) which
added GraalVM **reflection metadata** to fix an image-handling runtime error in
the native build.

Release engineering matured too: [#27](https://github.com/cam-center/LangevinNoVis01/pull/27)
([1.4.3](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.4.3), Sep
2025) added a `macOS-Universal` CD job that downloads the separate `macos-13`
(Intel) and `macos-14` (ARM) native binaries and merges them with `lipo` into a
single universal macOS executable.

SpringSaLaD in 2025 mostly tracked the solver: [#4](https://github.com/cam-center/SpringSaLaD/pull/4)/[#5](https://github.com/cam-center/SpringSaLaD/pull/5)
bumped the bundled Langevin solver 1.3.1 → 1.4.0
([2.4.0](https://github.com/cam-center/SpringSaLaD/releases/tag/2.4.0)),
[#6](https://github.com/cam-center/SpringSaLaD/pull/6) updated the installer
script ([2.4.1](https://github.com/cam-center/SpringSaLaD/releases/tag/2.4.1)),
and [#7](https://github.com/cam-center/SpringSaLaD/pull/7) moved to solver 1.4.2
([2.4.2](https://github.com/cam-center/SpringSaLaD/releases/tag/2.4.2)).

### 2025 H2 → 2026 — CI runner upkeep, automated installers, and edge-case fixes

LangevinNoVis01 kept the cross-platform pipeline alive: [#30](https://github.com/cam-center/LangevinNoVis01/pull/30)
swapped the retired `macos-13` runner for `macos-15-intel`, and [#29](https://github.com/cam-center/LangevinNoVis01/pull/29)
fixed messaging. The latest solver work, [#36](https://github.com/cam-center/LangevinNoVis01/pull/36)
([1.4.7](https://github.com/cam-center/LangevinNoVis01/releases/tag/1.4.7), Jun
2026), fixed a **NullPointerException** when a bound site transitions into a state
that has no defined binding reaction — guarding the bond's reaction-name/length
assignment and keeping the prior bond type rather than dereferencing a null.

SpringSaLaD's final chapter (Ezequiel-Valencia) **automated deployment**:
[#8](https://github.com/cam-center/SpringSaLaD/pull/8) and
[#9](https://github.com/cam-center/SpringSaLaD/pull/9) added a
`deploy-installers.yml` workflow and `generate_client_installers.sh` that drive
**install4j** to build Windows/macOS/Linux installers (version injected from the
release tag, license supplied as a CI secret) and attach them to the GitHub
release — closing the loop from solver build → app build → published installers.
[#10](https://github.com/cam-center/SpringSaLaD/pull/10)
([2.4.6](https://github.com/cam-center/SpringSaLaD/releases/tag/2.4.6)) truncated
displayed diffusion rates to 5 decimals.

## Notable PRs/commits

| PR | Date | Author | Why it matters (verified via diff) |
|----|------|--------|-----------------------------------|
| [L #1](https://github.com/cam-center/LangevinNoVis01/pull/1) | 2023-07-18 | jcschaff | Maven + GraalVM native-image build, picocli CLI, cross-platform CI — foundation of the whole packaging story |
| [L #5](https://github.com/cam-center/LangevinNoVis01/pull/5) | 2024-02-10 | jcschaff | VCell integration: `VCellMessaging`/`MessagingConfig`, JMS WorkerEvents, `--vc-send-status-config` — runs as a VCell remote compute job |
| [L #7](https://github.com/cam-center/LangevinNoVis01/pull/7) | 2024-05-20 | jcschaff | Physics fix: spring force scaled by site diffusion `D`, spring constant 1e8 → 100 |
| [L #12](https://github.com/cam-center/LangevinNoVis01/pull/12) | 2025-04-24 | danv61 | New binding kinetics: `1−e^(−λ·dt)` probability + intrinsic kon/koff from diffusion-limited `kD=4πRD` |
| [L #18](https://github.com/cam-center/LangevinNoVis01/pull/18) | 2025-05-20 | danv61 | `post` CLI subcommand: consolidate cluster statistics across runs |
| [L #23](https://github.com/cam-center/LangevinNoVis01/pull/23) | 2025-06-30 | danv61 | GraalVM reflection metadata fixes native-image image-handling crash |
| [L #27](https://github.com/cam-center/LangevinNoVis01/pull/27) | 2025-09-11 | danv61 | `lipo`-merged universal macOS binary (Intel + ARM) in CD |
| [L #32](https://github.com/cam-center/LangevinNoVis01/pull/32) | 2026-01-09 | danv61 | Lambda-computation refinement + tutorial model |
| [L #36](https://github.com/cam-center/LangevinNoVis01/pull/36) | 2026-06-09 | danv61 | NPE fix when a bound site transitions to a state with no binding reaction |
| [SS #1](https://github.com/cam-center/SpringSaLaD/pull/1) | 2024-05-23 | jcschaff | App rebuild: Ant/install4j → Maven, GUI refresh, bundled per-platform solvers |
| [SS #2](https://github.com/cam-center/SpringSaLaD/pull/2) | 2024-06-25 | danv61 | MP4 movie export of the 3D viewer via `jcodec` |
| [SS #8/#9](https://github.com/cam-center/SpringSaLaD/pull/8) | 2025-12 / 2026-02 | Ezequiel-Valencia | Automated install4j installer build + GitHub release publishing |

## Key contributors

- **danv61** (Dan Vasilescu) — primary maintainer of both repos by far: the new
  binding algorithm and lambda work, cluster-analysis/post-processing, random
  seed, JSON serialization, GraalVM/native-build and macOS-universal CI, and the
  ongoing physics/edge-case fixes.
- **jcschaff** (Jim Schaff) — the foundational rebuilds: Maven + GraalVM native
  build and CLI for the solver, the VCell JMS messaging integration, and the
  Maven port of the SpringSaLaD app.
- **moraru** (Ion Moraru) — 30 commits in SpringSaLaD (GUI/app side; predates much
  of the tracked PR window).
- **Ezequiel-Valencia** — release/deployment automation (install4j installer
  pipeline) for SpringSaLaD.
- **pjmichalski** (Paul Michalski) — original SpringSaLaD algorithm author
  (Michalski & Loew 2016); occasional commits.

## Tech & stack notes

- **Language:** pure Java 17 (solver ~458 KB; app ~1.1 MB Java + a small shell
  helper). Solver packages under `edu.uchc.cam.langevin`, app under
  `org.springsalad`, VCell-integration glue under `org.vcell.{messaging,data}`.
- **Solver build:** Maven + **GraalVM native-image**; **picocli** CLI
  (`simulate` / `post` subcommands) with `picocli-codegen` annotation processing
  for native compatibility. Native binaries built per-platform in GitHub Actions
  and attached to releases; macOS Intel + ARM merged into a universal binary with
  `lipo`.
- **App build:** Maven (`mvn clean install dependency:copy-dependencies`), with
  **Jmol** for 3D molecular visualization and **jcodec** for MP4 export.
  Cross-platform desktop installers produced with **install4j** (license via CI
  secret), automated in `deploy-installers.yml`.
- **VCell integration:** the solver speaks VCell's JMS `WorkerEvent`
  status/progress protocol via a JSON `MessagingConfig`, so it runs as a remote
  compute job; the main `vcell` repo embeds it as the "Langevin" application with
  dedicated option/visualization panels (~89 referencing Java files).
- **Release cadence:** frequent point releases driving in lockstep — solver
  `1.x.y` releases are consumed by the app, which bumps its bundled `localsolvers`
  and re-releases as `2.4.x`.
