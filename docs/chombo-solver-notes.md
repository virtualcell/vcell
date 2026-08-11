# Chombo solver — status, run specification, and file formats

Working notes gathered while adding Chombo support to the VTK-based field viewer
(PR #1890). Chombo is a retired solver: stored results exist, but as of August 2026
no deployed image or solver bundle can *run* it (details below). This document
records what is needed to read its output today and to resurrect the solver later.

> **2026-08-11 update — it builds and runs from source.** `VCellChombo3D_x64` built from
> vcell-solvers master (macOS arm64, homebrew gcc + HDF5 2.1.0) ran the "chombo 3d" sim of
> BioModel 276459600 to completion locally, producing modern-format output end-to-end through
> the VTK seam. Recipe: symlink the binary into `localsolvers/mac64/`, generate
> `.fvinput`/`.functions`/`..._0__0.simtask.xml` via `FVSolverStandalone` from the VCML, then run
> `VCellChombo3D_x64 <fvinput>` (serial mode writes everything; no `-ccd` pass needed). A
> self-contained input+expected-output package for a Java-free vcell-solvers unit test lives at
> `/tmp/chombo-local/` (see its README). Four defects found on the way:
>
> 1. **`ChomboScheduler::writeData` buffer overflow** (vcell-solvers): the per-time output path is
>    sprintf'd into `char hdf5FileName[128]`; a long base directory silently creates the file at a
>    truncated path and the follow-up `H5Fopen` aborts. Run in a short directory until fixed.
> 2. **`VtkServicePython` passed the output directory where the .vtu path belongs** (since July
>    2023) — server-side Chombo/FV/comsol VTU generation was silently broken. Fixed in PR #1893.
> 3. **`Hdf5PostProcessor` rejects statistic name `mean`** — written by *today's* solver, not just
>    legacy archives — and aborts all data access for the run (issue #1894).
> 4. **Chombo .vtu files are in doubled-index coordinates**, not microns: `ChomboMeshMapping`
>    maps vertices to `(p-origin)*N/extent*2 - 1` (exact integers, a VisIt-era convention).
>    Consumers must invert this (`p = origin + (v+1)*extent/(2N)`); the field viewer does so in
>    `FieldViewerServer.chomboToPhysical`.

## 1. Current availability (as of 2026-08)

- **Source still exists**: `virtualcell/vcell-solvers` repo, directories
  `VCellChombo/` (the VCell driver) and `Chombo/` (the LBNL library). The CMake
  options `OPTION_TARGET_CHOMBO2D_SOLVER` and `OPTION_TARGET_CHOMBO3D_SOLVER`
  default to **off**, and current CI does not enable them.
- **No binaries anywhere current**:
  - `localsolvers/` bundles (release `v0.0.44-dev4`: linux64/mac64/win64) contain
    FiniteVolume, MovingBoundary, Sundials, VCellStoch, NFsim, smoldyn, langevin —
    no `VCellChombo2D`/`VCellChombo3D`.
  - `ghcr.io/virtualcell/vcell-batch:8.0.11.01` (the image the HPC Singularity/Apptainer
    image is built from): `find / -iname '*hombo*'` is empty.
  - `ghcr.io/virtualcell/vcell-solvers:latest` and `:v0.0.42-dev3`: none.
- **Consequence**: a Chombo simulation submitted today would fail at executable
  resolution (`SolverUtilities.getExes` → `ResourceUtil.findSolverExecutable`)
  both client-side and on HPC. Resurrection means either building from source with
  the two CMake options enabled (linux/amd64; Fortran + HDF5 toolchain) or
  recovering binaries from a VCell 7.x-era image.

## 2. How a Chombo run is specified

- `SolverDescription.Chombo` — display name **"Chombo Standalone"** ("EBChombo,
  Semi-Implicit (Fixed Time Step), Experimental"), KISAO:0000285. Features:
  spatial, deterministic, region-size functions, Dirichlet-at-membrane-boundary,
  parallel, fast systems.
- `SolverExecutable.VCellChombo` maps to **two** executables: `VCellChombo2D` and
  `VCellChombo3D`; `FVSolverStandalone` (the same Java driver used for the
  FiniteVolume solver, via its `isChombo` flag) picks `getExes()[0]` for 2D and
  `[1]` for 3D. `FiniteVolumeFileWriter` writes the input file with
  `bChomboSolver` branching.
- VCML: `<SolverTaskDescription Solver="Chombo Standalone">` containing a
  `<ChomboSolverSpec>`:

  ```xml
  <ChomboSolverSpec>
    <MaxBoxSize>32</MaxBoxSize>
    <FillRatio>0.9</FillRatio>
    <SaveVCellOutput>true</SaveVCellOutput>
    <SaveChomboOutput>true</SaveChomboOutput>
    <ActivateFeatureUnderDevelopment>false</ActivateFeatureUnderDevelopment>
    <SmallVolfracThreshold>0.0</SmallVolfracThreshold>
    <BlockFactor>4</BlockFactor>
    <TagsGrow>2</TagsGrow>
    <TimeBound>
      <TimeInterval StartTime="0.0" EndTime="1.0" TimeStep="0.1" OutputTimeStep="0.1"/>
    </TimeBound>
    <MeshRefinement/>
  </ChomboSolverSpec>
  ```

- `<SaveChomboOutput>true</SaveChomboOutput>` is what makes the run VTK-visualizable
  later: `SimulationData.getChomboFiles()` refuses to build the VTK view of a run
  whose stored `..._0_.simtask.xml` lacks it ("Export of Chombo simulations to VTK
  requires chombo data"). Old simtask files predate the element entirely.
- Reference model with runnably-small Chombo sims: **BioModel 276459600 (user
  `schaff`, "Solver_Suite_7_5")**, saved at `exampleModels/Solver_Suite_7_5.vcml`
  (git-ignored). It has a "2D chombo" application (sims on 8×8 meshes) and a
  "3D chombo" application (8×8×8 and 16×16×16) — ideal fixture size once the
  solver can run again.

## 3. Output file formats (per `SimID_<key>_<job>_` prefix)

All HDF5 files are written by the solver itself (native code), so format follows
the *solver build* era, not the reading-code era.

| File | Content |
|---|---|
| `.mesh.hdf5` | Chombo mesh: group `mesh` with attrs `dimension`, `Nx`, extents, and datasets including the compound table `membrane elements` |
| `_%06d.feature_<subdomain>.vol<ivol>.hdf5` (alt: `_%06d_<subdomain>_vol<ivol>.hdf5`) | per-output-time, per-feature, per-volume solution data — the files `SimulationData.findChomboFeatureVolFile` looks for |
| `.sim.hdf5` (often zipped into the sim `.zip`) | raw Chombo solver output; **not** readable by the vis pipeline — only the loose feature/vol extracts are |
| `_.hdf5` | post-processing output (statistics etc.) |
| `.simtask.xml`, `.log`, `.functions` | as for other FV-family solvers |

### Format eras (observed in real archives on the dev cluster)

- **~2011**: `membrane elements` uses an older layout whose columns are typed
  differently — current `CartesianMeshChombo.collectMembraneElements` fails with
  `ClassCastException` (`[D` vs `[I`). Not readable without a legacy branch.
- **~2014**: modern-looking mesh, but only zipped `.sim.hdf5` solver output — no
  loose feature/vol files, so the VTK path has nothing to read.
- **≥ ~2015**: modern `membrane elements` schema. 2D columns:
  `index, level, i, j, x, y, normalX, normalY, volumeFraction, areaFraction,
  membraneId, cornerPhaseMask`; 3D adds `k`, `z`, `normalZ`. Note: even a
  2015-era set (boris/SimID_96785812, which has mesh + loose vol files at
  t=0/50/100) still failed inside `org.vcell.vis`'s Chombo dataset reader with an
  NPE ("Cannot read the array length because <local9> is null") — there is at
  least one more schema gap in that era that was not diagnosed.
- **Legacy post-processing files** use a statistic named `mean`, which the current
  `Hdf5PostProcessor.StatisticType.fromName` rejects (`No Statistic with name mean`)
  and which aborts *unrelated* data access for the run. Workaround when reading
  legacy runs: move `SimID_<key>_<job>_.hdf5` aside (or extend the enum).

### Reading requires linux/amd64

The Chombo readers go through the legacy native HDF5 library (`NativeLib.HDF5`),
which cannot load on macOS arm64 (documented in `VtkMeshGenerator`). Practical
recipe: run the reading code inside `ghcr.io/virtualcell/vcell-data:<tag>`
(`--platform linux/amd64`), jars on classpath from `/usr/local/app/lib/*`, with
`-Dvcell.installDir=/usr/local/app -Dvcell.primarySimdatadir.internal=<mount>
-Dvcell.python.executable=/usr/local/bin/python -Dvcell.vtk.pythonDir=/usr/local/app/pythonVtk`.

## 4. VTK / field-viewer pipeline for Chombo results

- Transport is the same VTU seam used for MovingBoundary (`VCDataManager`:
  `getVtuTimes`, `getEmptyVtuMeshFiles(vcdID, timeIndex)`, `getVtuVarInfos`,
  `getVtuMeshData`) — no interface changes. The distinction is
  `FieldViewerServer.VtuMode`: Chombo is **STATIC** (one mesh for all times;
  `getEmptyVtuMeshFiles` must be called with `timeIndex = 0`), MovingBoundary is
  **TIME_VARYING** (mesh per time index). Detection: `getMesh() instanceof
  CartesianMeshChombo` vs `CartesianMeshMovingBoundary`.
- The Python VTK service (`pythonVtk/python_vtk/vtkService/vtkService.py`,
  `writeChomboVolumeVtkGridAndIndexData`) converts Chombo cut cells (irregular
  polyhedra) to tetrahedra and emits ordinal `chomboVolumeIndices` pairing data
  values to cells — 2D: polygons; 3D: **voxels first, then tets**. So a 3D Chombo
  VTU is a mixed-cell grid: `VTK_VOXEL` (11) interior + `VTK_TETRA` (10) at the
  embedded boundary; 2D mixes triangle/quad/polygon.
- VTU files are written single-piece, LittleEndian, binary **uncompressed**
  (`SetCompressorTypeToNone` + `SetDataModeToBinary`), UInt32 headers, inline
  base64 — exactly what `org.vcell.client.viz.VtuGridParser` parses (unit-tested
  against a reference file).
- The viewer renders mixed-cell VTUs via per-cell `insertNextCell(type, npts, ids)`
  (verified marshalled in vtk.wasm ≥ v1.2.0) in "bodyFitted" mode: no deform/smooth,
  the solver mesh is shown as computed.

## 5. Known real datasets (dev cluster `/simdata`, for future resurrection work)

| Dataset | Era | Notes |
|---|---|---|
| `boris/SimID_96785812_0_` | ~2015, 3D | modern mesh schema + loose feature/vol files at t-index 0/50/100; still trips the undiagnosed `org.vcell.vis` reader NPE |
| `boris/SimID_85735024_0_`, `SimID_78363520_0_` | 2011–2014 | old membrane-element schema and/or zipped-only solver output — not readable |
| `schaff/SimID_1058*/1059*/106104475` (105x series) | 2017, **2D** | modern schema, plenty of loose vol files — best 2D candidates |

The cleanest path to a *test fixture*, once the solver is buildable again, is not
these archives but a fresh local run of the small sims in BioModel 276459600.
