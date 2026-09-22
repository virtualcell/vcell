# Plan — FEniCSx as a VCell solver (VCell Java side)

**Status:** planning (2026-09-22). This is a living plan: tick PRs off as they merge. The
solver itself lives in [virtualcell/vcell-fenics](https://github.com/virtualcell/vcell-fenics); its
side of the contract is done, and its tracker is
[`docs/integration/vcell-solver-integration.md`](https://github.com/virtualcell/vcell-fenics/blob/main/docs/integration/vcell-solver-integration.md).

## Context

vcell-fenics already runs as a VCell solver on its own side (PRs #147–#157). It takes the
SimulationTask XML and writes a VTU + zarr results bundle. It speaks VCell's status protocol:
`[[[progress/data]]]` on stdout, or REST WorkerEvents through a Langevin-style messaging config. It
accepts `-tid N`, and its image and SIF are on GHCR. Nothing in this repo knows it exists yet.

The contract is fixed by vcell-fenics
[ADR 010 (results bundle)](https://github.com/virtualcell/vcell-fenics/blob/main/docs/decisions/010-results-bundle-vtu-zarr.md)
and [ADR 011 (solver contract)](https://github.com/virtualcell/vcell-fenics/blob/main/docs/decisions/011-vcell-solver-contract.md);
ADR 011 §6 lists the Java follow-up this plan sequences. It runs as a series of small PRs
against `virtualcell/vcell`, plus one against `vcell-fluxcd`.

Decisions taken:
- **Desktop first:** a Docker quick-run plus the local field viewer, all verifiable on a developer
  machine. HPC comes second.
- **Gated** behind a property until viewing works end to end.
- **MPI later.** The first HPC pass is single-rank.

## Conventions

- Each PR below is its own branch off `master`, and each merges with a merge commit.
- Build and test per [`BUILDING.md`](BUILDING.md) (Java 17). Run per-module tests with
  `mvn test -pl <module> -Dtest=…`.
- Mirror progress in the vcell-fenics tracker.
- Line numbers below are approximate, taken from `master` at `925d63a83e`.

## PR V0 — this plan
- `docs/plan-fenics.md`.

## PR V1 — registration (gated, no user-visible change)
Templates: `MovingBoundarySolver` for shape, and `LangevinSolver` for the messaging config and
`--vc-*` argv (both on `master`).

**Solver definition**
- `vcell-core/.../solver/SolverDescription.java`: add `FEniCSx`.
  - Database and short name `FEniCSx`.
  - Features `{Feature_Spatial, Feature_Deterministic}`.
  - `SupportedTimeSpec.DEFAULT_EXPLICIT_UNIFORM`.
  - KiSAO `"KISAO"` placeholder; long description `SolverLongDesc.FENICSX`.
  - Add an `isFenicsSolver()` helper.
  - No native `SolverExecutable`.
- `SolverExecutable.java`: add no entry. `canQuickRun` needs a branch for FEniCSx instead (`getExes`
  would throw `UnsupportedOperationException` on null).

**Solver class**
- New `vcell-core/.../solvers/FenicsSolver.java extends SimpleCompiledSolver`.
- `initialize()` writes `SimID_*_.fenicsMessagingConfig` when messaging is on. Put it in the same
  properties format as `LangevinSolver.writeLangevinMessagingConfig`, which is what `status.py`
  parses. Add the extension constant to `SimDataConstants`.
- `getMathExecutableCommand()` returns `vcell-fenics --simtask <simtask.xml> --out <userDir>` plus
  either `--vc-send-status-config=<cfg>` or `--vc-print-status`.
  - The simtask file already exists: `SolverFactory.createSolver` writes it.
  - The server appends `-tid N` itself, and the CLI accepts it.
- `getApplicationMessage()` parses `data:`/`progress:`, copied from Langevin.
- A launcher hook (e.g. a settable `commandPrefix`) lets V2 wrap the argv in `docker run …`. HPC
  doesn't need it: SlurmProxy adds its own container prefix.

**Factory and gate**
- `SolverFactory.java`: add a `FACTORY.put(FEniCSx, …)` entry.
- **Gate:** add a `vcell.fenics.enabled` property (default false) to `PropertyLoader`. Filter FEniCSx
  out of the solver combo box (`SolverTaskDescriptionAdvancedPanel` ~l.343, next to the deprecated
  filter) and out of `canQuickRun`/`canServerRun` while it is off.

**Tests**
- Extend `SolverDescriptionRegression` (db-name/label round-trips; check `diffSupportingMath` for the
  new spatial member).
- A `FenicsSolverTest` for the argv in both messaging modes and for the config file contents.

## PR V2 — desktop Docker quick-run
**Container runner**
- New small `ContainerRunner` utility in vcell-core/`org.vcell.util.exe`.
- It finds `docker` through `ResourceUtil.getExecutable` (VCellConfiguration → PATH), then checks the
  daemon with `docker info`.
- Image comes from the property `vcell.fenics.image`, default a pinned `ghcr.io/virtualcell/vcell-fenics:<sha>`.
- It builds `docker run --rm -v <simdir>:<simdir> [--user uid:gid on unix] <image> vcell-fenics …`.
  The entrypoint already takes this form.

**Quick-run wiring**
- `ClientSimManager.createQuickRunSolver` (~l.1091): for FEniCSx, skip `prepareSolverExecutable` and
  install the docker prefix on `FenicsSolver`.
- `SimulationListPanel.canQuickRun` (~l.1058): allow FEniCSx when the gate is on and Docker is present.
  Otherwise show an explanatory message.
- **First pull:** `docker image inspect`, and if the image is missing, run `docker pull` in an
  `AsynchClientTask` with a progress dialog before starting the solver.

**Results and cleanup**
- Results: until V3, finishing a FEniCSx quick run reports the bundle path instead of calling
  `showSimulationResults0`. That call would fail in `SimulationData`, which looks for the `.log`.
- Cleanup: `ResourceUtil.getLocalSimDir` (~l.321) must delete `SimID_*.fenics/` directories
  recursively. `File.delete()` fails silently on a non-empty directory.

## PR V3 — viewing FEniCSx bundles in the field viewer (local)
**`VtuGridParser` fixes** (`vcell-client/.../viz/`)
- Add `VTK_LINE` (length measure, picking).
- Compute triangle/polygon area and pick in 3D, instead of the shoelace formula that drops z.
- Reject a VTU with a `compressor` attribute instead of misreading it.
- In `handleGridVtu`, report `dimension` from the domain's gdim so a 3D membrane isn't reported as 2D.

**Bundle reader**
- New `FenicsBundle` in `vcell-client/.../viz/` (or vcell-core, if the data server needs it in V5).
- Reads the manifest from `.zattrs` and rejects a newer `schema`.
- Row counts come from `times`, never from array shapes.
- Chunks: a hand-rolled zarr v2 read (`.zarray` JSON, `"<row>.0"` chunk files, `Inflater`,
  little-endian f8, NaN fill). No zarr library exists in the poms, and ADR 010 planned for this.
- Handles only the one-segment case now, but parses `segments`/`prefix` so a segmented bundle can
  follow.

**`FieldViewerServer`**
- Extract a small data-source interface from the `DataSource` record and its branches.
- Add a `BundleSource`: fixed profile → STATIC-like, segmented → TIME_VARYING keyed by segment.
- Endpoints:
  - `/info` lists point variables.
  - `/field` returns `"location":"point"`.
  - `/timeseries` interpolates within the located cell.
  - `/stats` reads the precomputed `stats/` array and adds a `total` field.

**`webapp-viewer/viewer.js`**
- Branch on `field.location === "point"`: `getPointData().setScalars` and
  `setScalarModeToUsePointData`.
- Point-aware readout and stats.
- Render line cells for 2D membranes.
- Check the vtk.wasm calls with `probe.html`.

**Entry point**
- For FEniCSx sims, "results" registers a `BundleSource` for the local simdir and opens the browser
  viewer directly, bypassing `PDEDataViewer`.
- The FEniCSx gate implies `vcell.fieldViewer.enabled` for this path.

**Fixture**
- A small bundle generated by `vcell-fenics --simtask tests/fixtures/simtask/…` and committed under
  `vcell-client/src/test/resources/`.
- Unit tests for the reader, the parser fixes and the endpoint JSON.

## PR V4 — HPC, single rank (vcell + vcell-fluxcd)
**vcell**
- `PropertyLoader`: add `htc_vcellfenics_apptainer_image` / `htc_vcellfenics_solver_list`, declared
  so the `VCELL_HTC_VCELLFENICS_*` env names are recognised.
- `HtcSimulationWorker` required-property list (~l.463–526).
- `SlurmProxy.generateScript` (~l.941–955): add a fourth `else if` branch and update its error message.
- Test: a `SlurmProxyTest` case plus a golden fixture under `slurm_fixtures/fenicsx/` (simtask and the
  expected `.slurm.sub`).
- Docs: `docs/apptainer-image-build.md`, with vcell-fenics as an independently-tagged image.
- Server cleanup: `DBBackupAndClean.deleteFileAndLink` and `ResultSetCrawler` must delete `.fenics/`
  directories recursively.

**vcell-fluxcd** (separate PR)
- Add `VCELL_HTC_VCELLFENICS_APPTAINER_IMAGE` and `_SOLVER_LIST=FEniCSx` to `submit.env` for **every**
  site (dev, stage, prod, island, remote). The prepull job treats an empty value as fatal.
- Add the variable to `SIF_VARS` in `kustomize/base/vcell-sif-prepull-job.yaml`.
- Check the SIF size against the 4Gi `emptyDir`/ephemeral limits and raise them if needed.

**Verify on dev:** deploy, run a FEniCSx sim from a client pointed at dev, and watch STARTING → PROGRESS
→ COMPLETED. The CLI sends 1003 itself, and the bundle lands in the user dir.

## Later (each its own PR, sequenced after V4)
- **V5, remote viewing:** a `DataSetController` RPC (or vcell-rest route) that serves bundle files by
  relative path, plus a remote `BundleSource` so server-run results open in the field viewer.
- **V6, UI and options:**
  - `FenicsSolverOptions` wired through `SolverTaskDescription` / `XMLTags` / `Xmlproducer` /
    `XmlReader` / VCML. Copy the VCML shape from `SundialsPdeSolverOptions`, not MB, whose `getVCML`
    never writes `EndBlock`. Keep it small: `taskDesc` is `varchar2(4000)`.
  - A self-hiding options panel in `SolverTaskDescriptionAdvancedPanel`.
  - FEM mesh size in `MeshTabPanel`.
  - Estimates in `checkSimulationParameters`.
- **V7, MPI:** emit `--ntasks` in `slurmScriptInit`, wrap with `srun --mpi=pmi2` / in-container
  `mpiexec` from `ncpus`, and add fixture tests.
- **Flip the gate on** once V3 and V4 have been verified. That needs a release-notes entry.
- **pyvcell:** a `FenicsResult` beside `Result`/`MovingBoundaryResult` (port
  `vcell_fenics/results/reader.py`, adding segment handling), and upstream the SimulationTask reader.

## Verification
- **Per PR:**
  - `mvn test` on the touched modules with the new unit tests.
  - For V2/V3, a desktop end-to-end run:
    1. Build the client and set `vcell.fenics.enabled=true` and `vcell.fieldViewer.enabled=true`.
    2. Open an analytic-geometry spatial model and choose FEniCSx.
    3. Quick-run: the image pulls, the progress bar advances, and the bundle appears under
       `~/.vcell/simdata/temp`.
    4. Open the results: the field viewer shows point data, and the stats match
       `python -m vcell_fenics.results` on the same bundle.
- **Tracker:** update `docs/integration/vcell-solver-integration.md` after each merge.
- **V4:** the golden-script test, then the dev-site run described above.
