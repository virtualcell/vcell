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

## PR V0 — this plan ✅ (#2084)
- `docs/plan-fenics.md`.

## PR V1 — registration (gated, no user-visible change) ✅ (#2085)
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

## PR V2 — desktop Docker quick-run ✅ (#2086)

**Found while building V2 (vcell-fenics side), both resolved 2026-09-22:**
- The image and SIF packages were **private** on GHCR, because the vcell-fenics repo is private.
  Both packages are now public, and anonymous pulls work.
- The image was **linux/amd64 only**. `ARM64_RUNNER=ubuntu-24.04-arm` is now set on vcell-fenics, so
  the image is multi-arch (amd64 + arm64) and Apple-silicon desktops run it natively. V2's
  `linux/amd64` fallback remains for any image that lacks a native build.

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
Split in two:
- **V3a** ✅ (#2087): the `VtuGridParser` fixes, plus `org.vcell.solver.fenics.FenicsBundle`, the
  bundle reader. It lives in vcell-core so the data server can reuse it in V5. Both are tested
  against real bundles.
- **V3b** ✅ (#2088): a bundle data source in `FieldViewerServer` (`FenicsBundleViews`), point data in
  the endpoints and in `webapp-viewer`, and the entry point: a finished FEniCSx quick run opens the
  browser viewer. The dimension fix belongs in the bundle's `/grid` (from gdim), not in
  `handleGridVtu`, whose producers never send a 3D surface.

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

## PR V4 — HPC, single rank (vcell + vcell-fluxcd) — ✅ vcell #2089; vcell-fluxcd #56 (dev) awaits a dev deploy
As built:
- The FEniCSx image properties are **optional** in `SlurmProxy`: a site without them refuses the
  solver. So they are not added to `HtcSimulationWorker`'s required list.
- The server-side bundle cleanup is in `DBBackupAndClean.deleteFileAndLink` and `ResultSetCrawler`.
  `AmplistorUtils` (legacy object store) is untouched.
- The vcell-fenics image is now **multi-arch** (linux/amd64 + linux/arm64, native arm64 runner),
  so Apple-silicon desktops run it natively. The SIF stays amd64.

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
- **V5, remote viewing ✅ (#2090):** built as the RPC `DataSetController.getFenicsBundleFile(vcdID, path)`,
  a default method implemented on the local and messaging paths. Reads go through a `BundleStore`
  (a directory, or the data server via `DataServerBundleStore`, cached). A server-run FEniCSx sim's
  results open in the field viewer. Access follows the data server's existing read policy; the
  bundle lookup confines paths to the bundle. Originally planned as: a `DataSetController` RPC (or vcell-rest route) that serves bundle files by
  relative path, plus a remote `BundleSource` so server-run results open in the field viewer.
- **Geometry check ✅ (#2091),** found by trying a desktop Quick Run. FEniCSx refuses non-analytic
  and non-2D/3D geometries as ERROR issues (`FenicsSolver.unsupportedReasons` →
  `Simulation.gatherIssues`), so the run stops before a container starts. A failure shows only the
  solver's `error:` line.
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

## Moving boundaries (M1–M5)

**Why.** VCell's moving-boundary applications list only the native MovingBoundary solver, although
the FEniCSx ALE backend solves these problems and is cross-validated against mbsolver. The solver
path refuses them today, so they aren't silently solved on the initial shape. M1–M4 are vcell-fenics
PRs, tracked in its
[integration tracker](https://github.com/virtualcell/vcell-fenics/blob/main/docs/integration/vcell-solver-integration.md#moving-boundaries-m1m5).
M5 is here.

**Scope of the first pass** (decided 2026-09-22):
- 2D, with species in the **moving interior volume** only (e.g. a cell carrying its cytoplasm, with
  an empty exterior).
- **Remeshing included:** a new bundle segment per remesh.
- **Species-dependent front velocities allowed** (explicit one-step lag), cross-validated against
  mbsolver.
- Membrane species on a moving front come in a later pass.

**vcell-fenics**
- **M1 — bridge.** Read `MembraneSubDomain/<Velocity>` from the SimulationTask. pyvcell drops it; it
  is e.g. `sobj_cell1_ec0_velX` → `sproc_0.velocityX` → `sin(t)`. Attach it as prescribed motion on
  the interior compartment (v = v_b), and replace the blanket refusal with specific ones. Also fixes
  an identifier-regex bug in function inlining.
- **M2 — results bundle.** Per-row node coordinates (`_coords`) and a new segment per remesh, as ADR
  010 already reserves: writer, recorder (per-row domain measure), reader, and PVD export.
- **M3 — runner.** A moving path using backward Euler with remeshing (`backend/ale.py`), advancing
  `sim.t` (the ALE drivers didn't, so `sin(t)` froze at t = 0).
- **M4 — cross-validation against mbsolver:** translation, a remeshing expansion, and a
  species-dependent velocity. It publishes a new image.

**vcell**
- **M5 — offer and view:**
  - `Feature_Moving` on `SolverDescription.FEniCSx`, so it's listed for moving-boundary
    applications.
  - `FenicsSolver.unsupportedReasons` mirrors M1's refusals: 2D, interior-only species.
  - `FenicsBundle.coords(domain, row)` reads `_coords`.
  - `FenicsBundleViews` serves each row's geometry for an ALE segment (`geometryId …@t<row>`, which
    the viewer's body-fitted path already re-fetches), locates time-series points in each row's
    mesh, and uses the measure per row.
  - Bump the default image and the vcell-fluxcd pin to the M4 image.
- **Verification:** a moving-boundary application lists FEniCSx; a desktop Quick Run → the browser
  viewer shows the moving mesh while scrubbing time.
- **Status (2026-09-23):** M1–M4 merged in vcell-fenics (#160–#163). VCell's semantics turned out to be
  lab-frame ("swept") species, not carried: the front moves and a species without its own velocity stays
  put in the lab, so M3 transports it relative to the ALE mesh (the mesh velocity is bookkeeping and
  cancels out). M5 is in review: the tests use two real moving bundles (a translation, and a remesh
  across two segments), and the default image is `sha-bfdf853`.

## Image geometries (I1–I6)

VCell image-based geometries (segmented 2D/3D label images, 16 % of the corpus) are realized by vcell-fenics
as smoothed, body-fitted meshes with any topology — nested regions, regions cut by the image edge, and
junctions where three subvolumes meet (vcell-fenics ADR 012; PRs #168–#173). On VCell's tutorial image a
nucleocytoplasmic-exchange model agrees with fvsolver to 1.7 % in the nuclear filling curve and 0.8 % in the
cytosolic field (vcell-fenics `cross_validation/README.md`).

- **I6 (here):** `FenicsSolver.unsupportedReasons` accepts image subvolumes. It still refuses CSG, and
  image geometries in moving-boundary applications. The default image is `sha-cf08ec2`: the first that realizes
  images is `sha-a651c4b`, and `sha-cf08ec2` fixes a segfault it had meshing 3D images on Linux.

## 3D moving boundaries (V-3D)

The cleavage furrow in 3D: a sphere pinched by an axisymmetric contractile ring, solved by vcell-fenics's 3D
moving-boundary path (PRs #176–#178: 3D ALE, 3D remeshing, verification against the exact waist motion).

- **Math:** `MembraneSubDomain` carries `velocityZ` (VCML, the XML `<Velocity><Z>`, math comparison), and
  `DiffEquMathMapping` generates it on 3D geometries (it had refused anything but 2D).
- **Solvers:** FEniCSx accepts 3D moving boundaries on analytic geometries. The native Moving Boundary solver
  writes a 2D (x/y) input only, so it is refused for 3D with an ERROR issue (`MovingBoundary_Dimension_NotSupported`).
- **GUI:** the kinematics parameter table shows the Z components on 3D geometries.
- **Tests:** `FenicsSolverTest` reads the task the client generated for the "Furrow 3D" application, checks
  the `<Z>` survives an XML round trip, and checks that the Moving Boundary solver is refused.
- The default image is `sha-223b767`.

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
