# Plan — move VCell to the split vcell-* solver repos (local executables and HPC containers)

**Status:** planning (2026-09-30). This is a living plan: tick PRs off as they merge.

## Context

VCell's native solvers still come from the deprecated `virtualcell/vcell-solvers` monorepo on every path.

- **Desktop / local runs.** `vcell-core/pom.xml` (the `winprofile`, `macprofile` and `unixprofile`
  download executions, lines ≈685-993) fetches `vcell-solvers` release archives and unpacks them into
  `localsolvers/{linux64,mac64,win64}/`:
  - the versions are set in `pom.xml` (`solvers-vcell-{mac,linux,windows}.version`, ≈214-219):
    `v0.0.44-dev4` for mac and linux (2024-02) and `v0.0.40` for Windows (2021);
  - no archive has a checksum;
  - the Mac binaries are x86_64 only, so Apple-silicon Macs run them under Rosetta;
  - Hybrid, Chombo and PETSc are missing on every platform, and MovingBoundary exists only on mac;
  - Langevin comes from cam-center/LangevinNoVis01 and is not part of this plan.
- **HPC runs.** `SlurmProxy.generateScript` (`vcell-server/.../htc/slurm/SlurmProxy.java`, ≈941-961)
  matches the solver's `SolverDescription` name against four solver lists, in order, each paired with an ORAS
  image:
  - the order is fenics, fvsolver, solvers, batch;
  - the pins are in `vcell-fluxcd/kustomize/config/<site>/submit.env`;
  - the job runs the pre-pulled SIF as
    `singularity run --containall <binds> <env> <sif> <bare-executable-name> <args> -tid <n>`, so each image's
    entrypoint receives a bare name.
- **Every site** sends `CombinedSundials, IDA, CVODE, StochGibson, NFSim, HybridEuler, HybridMilstein,
  HybridMilAdaptive, MovingBoundary` to `vcell-solvers_singularity:v0.8.2`, and `Smoldyn, SundialsPDE,
  FiniteVolume, FiniteVolumeStandalone` to `vcell-fvsolver_singularity:0.9.7`. Only dev also sends `FEniCSx`
  to `vcell-fenics_singularity`.
  - The v0.8.2 image is built with Hy3S OFF, so the Hybrid solvers can't actually run there.
  - `vcell-batch` (`docker/build/Dockerfile-batch-dev`) is still `FROM vcell-solvers:v0.8.2`.

Eight split repos now hold the solvers. They are not yet release-ready:

| repo | builds | latest release | gaps |
|---|---|---|---|
| vcell-fvsolver | `FiniteVolume_x64`, `smoldyn_x64` | 0.10.5, 4 platforms, cut from an unmerged branch (PR #18) | the image turns messaging OFF (no `-tid`); the image is `:latest`-only and split per architecture; no SIF since 0.9.7; the Linux archives bundle glibc |
| vcell-ode | `SundialsSolverStandalone_x64` | 0.9.4, 4 platforms + win-arm, cut from a branch that diverged from `master` | amd64-only image; no entrypoint; bundles glibc and test libraries |
| vcell-stochastic | `VCellStoch` | `ci-<timestamp>` zips; mac is arm64 only | name ≠ `VCellStoch_x64`; the mac build links absolute Homebrew HDF5 paths; no image |
| vcell-nfsim | `NFsim` (built in CI only) | Python wheels only | no executable archives; name ≠ `NFsim_x64`; no image |
| vcell-mbsolver | `MovingBoundarySolver` | v1.0.4: bare, unbundled binaries | name ≠ `MovingBoundary_x64`; links the runner's or Homebrew's HDF5; no image |
| vcell-hy3s | `Hybrid_EM_x64`, `Hybrid_MIL_x64`, `Hybrid_MIL_Adaptive_x64` | none (the CI PR #1 is still open) | nothing published |
| vcell-chombo | `VCellChombo2D_x64`, `VCellChombo3D_x64` | none (CI artifacts only) | nothing published |
| vcell-fenics | `vcell-fenics` | image + SIF `sha-*`, good entrypoint | no version tags; under `--containall` its JIT cache falls back to a small `/tmp` |

The goal:
1. **Local.** The desktop downloads each solver's own release for every platform, replacing the legacy
   `vcell-solvers` binaries.
2. **HPC.** HPC runs use each repo's latest image, as a SIF. The image is chosen per solver by vcell-fluxcd's
   submit config, and every image has a correct entrypoint.

After that, `vcell-solvers` can be archived.

## Decisions taken

| # | Question | Decision |
|---|---|---|
| 1 | How HPC images are organised | **One image per solver repo.** SlurmProxy becomes table-driven, and moving a solver to its image is configuration only. |
| 2 | Where the release fixes happen | **In each solver repo**, one release-hygiene PR each, before VCell consumes it. |
| 3 | macOS on Apple silicon | **Universal binaries** (x86_64 + arm64) in the single `localsolvers/mac64/` directory. The install4j mac media stays `universal`, and `OperatingSystemInfo` doesn't change. |
| 4 | Executable names | **VCell's existing names** (`SolverExecutable` base + `_x64`, `.exe` on Windows), applied by each repo when it packages. Neither the Java lookup nor the Slurm command lines change. |

## Conventions

- Each PR below is its own branch off the repo's default branch and merges with a merge commit.
- VCell PRs: build and test per [`BUILDING.md`](BUILDING.md) (Java 17), `mvn test -pl <module> -Dtest=…`.
- vcell-fluxcd PRs: work from a temporary worktree off `origin/main`. Dev only, unless the user decides
  otherwise; stage and prod are the user's call.
- Line numbers are approximate, from `master` at `21d8aeec3a` and the solver repos on 2026-09-30.

---

## 1. The contract every solver repo meets

Written once, as a short `SOLVER-RELEASE.md` section in each repo's README, and applied by each Phase A PR.

1. **Versioned releases from the default branch.** A tag `vX.Y.Z` on `main`/`master`; merge any release
   branch first. A GitHub Actions release workflow attaches:

   | asset | contents |
   |---|---|
   | `linux64.tgz` | x86_64. Built on a manylinux_2_28-class base, so it runs on the cluster's and users' glibc. **Don't bundle glibc** (`libc`, `libm`, `libpthread`, `libdl`); do bundle the non-system libraries (HDF5, zip, gfortran, libc++) with a `$ORIGIN` rpath. |
   | `linux64arm.tgz` | aarch64, when the repo can build it |
   | `mac64.tgz` | **Universal** binaries and dylibs, referring to each other through `@loader_path` / `@rpath` only (no `/opt/homebrew` paths), ad-hoc signed |
   | `win64.zip` | the `.exe` files and their DLLs |
   | `SHA256SUMS` | a checksum for every asset above |

2. **Archive layout uses VCell's names.** At the archive root:
   - the executables under the names VCell resolves, for example `MovingBoundary_x64`, `VCellStoch_x64`,
     `NFsim_x64`, `Hybrid_EM_x64` and `SundialsSolverStandalone_x64`;
   - the bundled libraries;
   - `LICENSE`;
   - a `VERSION` file.

   No test binaries and no static libraries.
3. **Container.**
   - `ghcr.io/virtualcell/<repo>:<X.Y.Z>`, multi-arch (amd64, plus arm64 where it builds), and `:latest`.
   - A slim runtime stage: a distro base plus the release archive's contents on `PATH`, not the build image.
   - Built with **messaging ON**: the solver accepts a trailing `-tid <n>` and reports status to the broker,
     as the 0.9.7 fvsolver image does.
4. **SIF.** `ghcr.io/virtualcell/<repo>_singularity:<X.Y.Z>` (amd64), pushed with ORAS by the same workflow, as
   vcell-fenics' `.github/workflows/container.yml` does.
5. **Standard entrypoint** (`/usr/local/bin/vcell-solver-entrypoint`, `ENTRYPOINT [...]`, `CMD ["--help"]`),
   modelled on vcell-fenics' `docker/entrypoint.sh`:
   - `set -eu`;
   - no argument or `--help`: print the version and the executables the image provides, exit 0;
   - the first argument names a provided executable: `exec "$@"`, so exit codes and SIGTERM pass through;
   - anything else: print usage and exit 2.

   It must:
   - write nowhere but `$TMPDIR`;
   - run as any uid;
   - work from a read-only SIF under `singularity run --containall`, with argv exactly as SlurmProxy writes
     it: a bare executable name, container paths under `/simdata`, and a trailing `-tid <n>`.
6. **CI smoke test** on every release:
   - `apptainer run --containall --bind $tmp:/simdata <sif> <exe> <tiny input>`, run as a non-root uid, must
     reproduce a committed reference output within tolerance;
   - `<sif> --help` exits 0.

---

## 2. Phase A — one PR per solver repo (independent; can run in parallel)

Each applies the contract (§1). The table lists what each repo needs beyond it.

| PR | Repo | Beyond the contract | Done when |
|---|---|---|---|
| **A1** | vcell-fvsolver | Merge PR #18 (`release_auto_attach`) into main, then release 0.10.6 from main. Messaging ON in the image (currently `-DOPTION_TARGET_MESSAGING=OFF`). One multi-arch `vcell-fvsolver` package, replacing `vcell_fvsolver_{x86_64,aarch64}`. Drop glibc from the Linux archives. Fix or remove the broken Windows-ARM step. | 0.10.6 has the contract's assets, image and SIF; the smoke test passes with `-tid`; FV and Smoldyn reference outputs match 0.9.7's |
| **A2** | vcell-ode | Reconcile `stabilize-new-build` with `master`, then release 0.9.5 from `master`. arm64 in the image, the entrypoint, a SIF. Strip glibc, `libgtest*.a` and `unit_tests` from the archives. | 0.9.5 has the assets, image and SIF; CVODE and IDA reference outputs match `vcell-solvers` v0.0.44-dev4 |
| **A3** | vcell-stochastic | Semver releases in place of `ci-<timestamp>`. A universal mac build with HDF5 bundled through `@rpath`. Package the executable as `VCellStoch_x64`. Image and SIF. | A `vX.Y.Z` release with every asset; Gibson reference statistics match the legacy binary within sampling tolerance |
| **A4** | vcell-nfsim | Executable archives (`NFsim_x64`) next to the wheels. Image and SIF. | Release assets and SIF; an NFsim reference run matches the legacy binary within sampling tolerance |
| **A5** | vcell-mbsolver | Archives with HDF5 bundled (or statically linked), plus a mac universal and a linux arm64 build. Package the executable as `MovingBoundary_x64`. Image and SIF. Fix the wheels workflow (its publish trigger is commented out; the CMake version says 1.0.3 for v1.0.4). | Portable archives on all platforms; MovingBoundary reference output matches the legacy mac binary and the v0.8.2 image |
| **A6** | vcell-hy3s | Merge CI PR #1, then a first release with `Hybrid_EM_x64`, `Hybrid_MIL_x64` and `Hybrid_MIL_Adaptive_x64`. Image and SIF. | Release assets and SIF; each Hybrid variant runs a reference model (there is no working legacy image to compare with, so check against a statistical reference) |
| **A7** | vcell-chombo | A first release of `VCellChombo2D_x64` / `VCellChombo3D_x64` (linux and mac; Windows if it builds). Image and SIF, serial first; MPI (parallel Chombo) is a follow-up. | Release assets and SIF; a 2D and a 3D Chombo reference model run |
| **A8** | vcell-fenics | Already meets the contract except for version tags. Add `vX.Y.Z` tags that trigger the same container workflow, so VCell can pin versions and not only `sha-*`. | `vcell-fenics:vX.Y.Z` and `_singularity:vX.Y.Z` exist |

---

## 3. Phase B — VCell

### PR B1 ✅ [#2131](https://github.com/virtualcell/vcell/pull/2131) — SlurmProxy chooses the image from a table (no dependency on Phase A)

Done: `SolverImageFamily` holds the ordered registry. A solver on several lists is allowed: the first family wins, with a warning, because existing sites already overlap their lists.


- Replace the four hard-coded image and solver-list pairs in `SlurmProxy.generateScript` with an **ordered
  registry of solver families**: fenics, fvsolver, ode, stochastic, nfsim, mbsolver, hy3s, chombo, solvers
  (legacy), batch.
- Each family reads `htc_vcell<family>_apptainer_image` and `htc_vcell<family>_solver_list`, declared in
  `PropertyLoader`. `EnvironmentConfigProvider` already maps these to `VCELL_HTC_VCELL<FAMILY>_*`.
- An unset family is skipped, as fenics is today.
- Moving a solver to its new image is then configuration only: take its name off the legacy
  `VCELL_HTC_VCELLSOLVERS_SOLVER_LIST` and put it on its family's list. The legacy family stays as the
  fallback until every solver has moved.
- Pass `--env TMPDIR=/solvertmp` (the Slurm tmp bind) to solver containers. That also fixes vcell-fenics' JIT
  cache under `--containall`.
- Reuse `sifFilenameFromOrasUrl` / `writeSifContainerPrefix` (SIF naming and the missing-SIF guard).
- **Done when:** `SlurmProxyTest` has a golden fixture per family
  (`vcell-server/src/test/resources/slurm_fixtures/`), and the existing fixtures are unchanged apart from the
  added `TMPDIR`.

### PR B2 — local executables from the split repos (after Phase A)

- Replace the three `vcell-solvers` download executions in `vcell-core/pom.xml` with one execution per repo
  and platform:
  - per-repo version properties in `pom.xml` (`solvers-fvsolver.version`, `solvers-ode.version`,
    `solvers-stochastic.version`, `solvers-nfsim.version`, `solvers-mbsolver.version`,
    `solvers-hy3s.version`, `solvers-chombo.version`), removing `solvers-vcell-*.version`;
  - the download plugin's `<sha256>` on every asset.
- Unpack flat into `localsolvers/{linux64,mac64,win64}/`. Langevin is unchanged.
- Refactor the three OS profiles so the CI (unix) build downloads every platform from one shared list of
  executions.
- Fix `HybridSolver.java` (≈395): it resolves `Hybrid_EM` for all three Hybrid variants.
- `docker/build/installers/VCell.install4j`: extend `macAdditionalBinaries` to every Mach-O file in `mac64`,
  so notarization passes. That includes `MovingBoundary_x64`, `langevin_x64`, `Hybrid_*`, `VCellChombo*` and
  the bundled dylibs.
- Docs: the `pom.xml` comment about the HPC image, `localsolvers/README.md`, `README.md` and
  `docs/BUILDING.md`.
- **Done when:**
  - a clean `mvn clean install dependency:copy-dependencies` fetches and checks every asset;
  - desktop quick runs of each local solver pass on an Apple-silicon Mac (native arm64, no Rosetta), Linux and
    Windows;
  - the mac installer notarizes.

### PR B3 — a slimmer vcell-batch image

- `docker/build/Dockerfile-batch-dev` moves off `vcell-solvers:v0.8.2` onto a JRE base. It needs only Java,
  `langevin_x64`, `bash` and `curl`.
- `docker/build/batch/entrypoint.sh` keeps the Java tools (`JavaPreprocessor64`, `JavaPostprocessor64`,
  `JavaSimExe64`), `langevin_x64` and `Send*Msg`, and drops the native-solver cases the per-solver images now
  own.
- **Done when:** the Slurm job's pre- and post-processing, failure messaging, Java ODE solvers and Langevin
  (single and batch) all run on dev with the new image.

### PR B4 — docs

- `docs/apptainer-image-build.md`: per-family images, SIF names, adding a new family, and the pre-pull job.

---

## 4. Phase C — vcell-fluxcd and rollout

- **C1 — pre-pull job** (`kustomize/base/vcell-sif-prepull-job.yaml`):
  - pull every `VCELL_HTC_*_APPTAINER_IMAGE` that is set (a pattern loop in place of the fixed
    OPT/BATCH/SOLVERS/FVSOLVER list), keeping OPT and BATCH required;
  - check the 4Gi emptyDir and ephemeral-storage limit against the largest SIF, and raise it if needed.
- **C2 — dev `submit.env`, one family at a time:**
  - add the family's image and list pair, pinning the Phase A `_singularity:<X.Y.Z>`;
  - move its solvers off `VCELL_HTC_VCELLSOLVERS_SOLVER_LIST` or `VCELL_HTC_VCELLFVSOLVER_SOLVER_LIST`;
  - run that solver's reference simulation on dev (§5) before moving the next family.
- **C3 — VCell release to dev** through the usual CD-sites flow, carrying B2 (local executables) and B3 (the
  batch image).
- **C4 — stage and prod** are the user's decision. Note that stage and prod set `VCELL_FENICS_ENABLED=true`
  but pin no FEniCSx image or list today. The `island` and `remote` sites have missing or old solver lists;
  they are flagged here and out of scope.
- **C5 — retire vcell-solvers**, once nothing pins it (no site, pom or Dockerfile): add a deprecation notice to
  its README, then archive it (the user's call).

**Order:** A1–A8 and B1 in parallel → C1 → C2 (per family as each A lands) → B2 and B3 → B4 → C3 → C4 → C5.

---

## 5. Verification

- **Numerical equivalence, per solver, before its first pin.** Run one fixed reference input through the
  legacy binary (`vcell-solvers` v0.0.44-dev4 locally, and the v0.8.2 SIF) and through the new release.
  Compare within tolerance: exact or near-exact for the deterministic solvers, statistically for the
  stochastic ones.
  - FV and Smoldyn: an existing FV model and a Smoldyn model;
  - CVODE and IDA: a `.cvodeInput` / `.idaInput`;
  - Gibson, NFsim, Hybrid, MovingBoundary and Chombo: one reference model each.

  The inputs live in each repo's test data, and each PR records its comparison.
- **Containers.** Each repo's CI smoke test runs `apptainer run --containall` as a non-root user with a bind
  mount and `-tid`. Each SIF is also run once by hand on the cluster with messaging, and its status messages
  must reach the dev broker.
- **VCell.** `mvn test -pl vcell-server -Dtest=SlurmProxyTest`, the clean build that checks every asset,
  desktop quick runs on all three platforms, and the mac notarization.
- **Dev cluster.** After each C2 move, submit that solver's reference simulation from the dev client. It must
  complete with status updates and match a run on the legacy image. `tools/release/verify-deploy.sh` covers
  each service rollout.

## 6. Risks

- **The cluster's glibc.** The Linux archives must run on the cluster's OS. Build on a manylinux_2_28-class
  base, and check the cluster's glibc version before the first pin.
- **Numerical drift.** Newer compilers and dependencies (Sundials, HDF5) can change results slightly. The
  per-solver equivalence check (§5) decides what counts as acceptable.
- **SIF sizes.** Many SIFs strain the pre-pull job's 4Gi emptyDir and the shared image directory. Slim
  runtime images keep each one small; C1 checks the sizes.
- **Notarization.** Every Mach-O file in `mac64` must be signed, including the bundled dylibs. A missed file
  fails the mac installer.
- **Chombo MPI.** Parallel Chombo on the cluster needs an MPI-enabled image and Slurm changes. It is a
  follow-up to A7.
- **Hybrid solvers have no working baseline:** v0.8.2 was built without them. Their check is statistical.

## Critical files

- **Solver repos:** each repo's `.github/workflows/*` (release, docker), `Dockerfile`, a new
  `docker/entrypoint.sh`, and its packaging scripts.
- **vcell:**
  - `pom.xml` and `vcell-core/pom.xml`;
  - `vcell-server/src/main/java/cbit/vcell/message/server/htc/slurm/SlurmProxy.java` and `SlurmProxyTest`
    with its fixtures;
  - `vcell-core/src/main/java/cbit/vcell/resource/PropertyLoader.java`;
  - `vcell-core/src/main/java/cbit/vcell/solver/stoch/HybridSolver.java`;
  - `docker/build/installers/VCell.install4j`;
  - `docker/build/Dockerfile-batch-dev` and `docker/build/batch/entrypoint.sh`;
  - `docs/apptainer-image-build.md`.
- **vcell-fluxcd:** `kustomize/base/vcell-sif-prepull-job.yaml` and `kustomize/config/dev/submit.env`.
