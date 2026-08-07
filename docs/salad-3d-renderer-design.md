# SpringSaLaD Spatiotemporal 3D Renderer — Design & Decision Record

> **PDE field visualization has moved to [`3d-renderer-design.md`](3d-renderer-design.md).**
> This document remains the record for the **SpringSaLaD trajectory viewer**. §8 and §8B below
> record how the field-viz question arose and the thinking as of 2026-07-30; anything later —
> including the implemented desktop field viewer, the grid/field endpoint split and the
> geometry-server alternative — lives in the other document, which supersedes them.

**Status:** Phases 0 & 1 **shipped** and deployed to dev/alpha (`8.0.4.01`). Phase 2
(field viz) — vtk.js `.vti` spike done (merged, structured case). The field-viz
architecture is **settled** in **§8 / §8B**: **vtk.wasm is the renderer everywhere**
(web *and* local desktop via system-browser + a local Java server — decided by the
local-computation requirement, since local has no Python for a geometry server);
solvers emit the **raw** representation (FV a trivial `.vti`, FE the body-fitted mesh)
and **vtk.wasm does the smoothing/slicing/contouring client-side** with stock filters
(FV = `vtkThreshold` + `vtkWindowedSincPolyDataFilter`, confirmed by reading the Java +
pyvcell mappers) — no smoothing writer in C++/Java/Python. Custom vtk.wasm build
prototyped (compiles; runnable bundle needs CI version-pinning). Chombo retired.
**Created:** 2026-07-29 · **Last updated:** 2026-07-30
**Scope:** An integrated spatiotemporal 3D renderer / movie player for SpringSaLaD
(Langevin particle) simulations in VCell, with a possible extension to visualizing
PDE (reaction-diffusion) solutions using a more capable stack.

> This is a living design record: it captures the problem, what already exists in
> the codebase, the 2026 technology landscape, and a phased recommendation. Phases 0
> and 1 are implemented (see the status section below); the strategic desktop-vs-web
> decision for Phase 2 (PDE field viz) is still open.

---

## Implementation status (updated 2026-07-30)

**Phases 0 and 1 are complete and deployed to the dev/alpha site.** The desktop
SpringSaLaD 3D trajectory movie player is live: run a SpringSaLaD simulation (local
*or* remote/HPC), then open its **Results → "3D Trajectory"** tab.

| Phase | Status | What shipped |
|---|---|---|
| **0 — data plumbing** | ✅ done | Solver canonicalizes the Run-0 viewer file (LangevinNoVis01 #39, released **1.4.9**); VCell serves it (`getLangevinTrajectory`, `SpringSaladTrajectory` model+parser). PRs #1795, #1797, #1798. |
| **1 — desktop renderer** | ✅ done | `SpringSaladViewerCanvas` (Java2D impostor spheres + quaternion trackball) + `SpringSaladViewerPanel` (movie player: play/scrub/speed), wired as the "3D Trajectory" tab; simulation **box** (with hidden-line removal) + opaque green **membrane** (z=0 plane). PRs #1796, #1797, #1798. |
| **2 — PDE field viz** | 🟡 spike done | Feasibility spike **loads a real `.vti` and renders it with vtk.js in `webapp-ng`** — colormapped slice + isosurface, clean build, lazy-loaded, `url`-polyfill resolved (draft PR #1799). Strategic desktop-vs-web decision still open, but the web path is now de-risked end-to-end. See §6. |

**Reality corrected the Phase 0 plan in two ways** (both fixed):
1. **Local (desktop-solver) runs invoke only the solver's `simulate` step, never
   `postprocess`** — so the viewer file is never canonicalized locally. The read path
   (`SimulationData.getLangevinViewerFile`) now falls back to the solver's
   `<base>_FOLDER/viewer_files/` subfolder after trying the flat canonical name
   (server, archival-safe).
2. **Remote runs fetch the trajectory by RPC**, and the client's remote data
   controller (`LocalDataSetControllerMessaging`, `vcell-apiclient`) was missing the
   `getLangevinTrajectory` delegation — masked by a `default`-null interface method —
   so remote runs silently showed *no tab* until fixed (#1798). Adding a method to
   `DataSetController` requires delegation in **all** real controllers
   (`LocalDataSetController`, `LocalDataSetControllerMessaging`,
   `LocalDataSetControllerProvider`) plus `RpcDataServerProxy`.

**The Java2D impostor-sphere choice held up** (over JavaFX-3D / JOGL / VTK): zero
native-packaging cost, smooth at the hundreds–low-thousands scale, and the
movie-player shell + a headless `renderToImage()` export path are reusable if a GPU
renderer is ever swapped in.

---

## 1. Goal & constraints

- **Primary:** render SpringSaLaD results as an interactive 3D scene of sphere
  glyphs (molecule sites) with links/bonds, playable as a time movie, with an
  interactive trackball camera. Typical scale: **hundreds–low-thousands** of
  glyphs, smooth interactive.
- **Possible extension:** a more capable visualization/analysis stack that can
  *also* render VCell **PDE field** solutions (colormapped slices, isosurfaces,
  volume rendering).
- **Hard constraint:** cross-platform (Windows x64, Linux x64, macOS x64 **and
  Apple Silicon arm64**) with **minimal native-packaging burden**. VCell ships via
  install4j across all five media; every native GPU/binding dependency reintroduces
  exactly that packaging/QA cost.

---

## 2. What already exists in the codebase (audit)

### 2.1 SaLaD trajectory data — **written by the solver, discarded by VCell**

This is the pivotal finding. The solver **`LangevinNoVis01`**
(`../LangevinNoVis01`, the source for the `localsolvers/*/langevin_x64` binaries)
— despite the "NoVis" name, which refers to the removed *GUI* viewer — **still
writes a per-particle position file** every `dt_image` step
(`MySystem.writePositions()`). VCell passes `dt_image` to the solver, the solver
writes the file, and then VCell's `LangevinSolver` collects only the `.ida` /
`.species` aggregates and **never references `viewer_files`** (verified: zero hits
for `viewer_files` / `_VIEW_Run` in vcell-core/server/client `main`).

So the spatiotemporal data is generated on the compute node and left
**unregistered** — see §2.4 for the full data-flow trace. It is *not* deleted at
job end; it persists on the shared store in a subfolder VCell's data layer never
looks in (and is archival-fragile). **The renderer is not blocked on new solver
output — only on serving a file that already exists.** See §5 for the exact format.

### 2.2 Existing 3D render infrastructure — trackball/camera yes, glyphs no

A working pure-Java software 3D stack lives in
`vcell-core/.../cbit/vcell/render/`:

- `Trackball.java` — quaternion virtual trackball (working)
- `Quaternion.java`, `Camera.java`, `Affine.java`, `Matrix3d.java`, `Vect3d.java`
  — camera + linear algebra (working)

It is consumed today by the **geometry** isosurface viewer
(`vcell-client/.../geometry/gui/SurfaceCanvas.java` + `SurfaceRenderer.java`),
which painter's-algorithm–draws polygon surfaces with depth cueing. There is **no
sphere-glyph / particle rendering** anywhere, and none of it is wired to
Langevin/SaLaD data. The trackball/camera math is reusable; the glyph rasterizer
is net-new.

### 2.4 How SaLaD output reaches the client (HPC data-flow trace)

There is **no scratch→primary copy-back step** for SaLaD results. The SLURM job
(`vcell-server/.../slurm/templates/langevinFixture.slurm.sub`) sets
`INPUT_DIR = LOG_DIR = /simdata/${USERID}` and runs
`langevin_x64 simulate <input> <run>` against that path — and `/simdata/<userid>/`
is the **shared NFS store the data server also reads**, so results are written in
place. The template has **no `rm`/`rsync`/cleanup**; nothing deletes solver output
at job end.

The solver roots its output at the input file's parent
(`Global.java` → `defaultFolder = /simdata/<userid>/`), then **splits** into two
conventions (`MySystem.folderSetup()`):

| Output | Path | VCell sees it? |
|---|---|---|
| **Aggregates** (`_Avg/_Max/_Min/_Std.ida`, cluster `.csv`) | **flat** in `/simdata/<userid>/SimID_<key>_0__Avg.ida` … | ✅ yes |
| **Viewer trajectory** | `/simdata/<userid>/SimID_<key>_0__FOLDER/viewer_files/SimID_<key>_0__VIEW_Run0.txt` | ❌ no |

The `postprocess` step canonicalizes the aggregates **up to the flat user dir**; the
viewer file stays in a `SimID_<key>_0__FOLDER/` **subfolder** that is never
canonicalized.

**Why VCell can't see it (the actual gap):**
`DataSetControllerImpl.getLangevinBatchResultSet()` reads results only via
`SimulationData.getLangevinFile(type)`, which resolves
`baseNameWithoutExt + type.suffix() + type.extension()` (e.g.
`SimID_<key>_0__Avg.ida`) as a **flat, canonically-named file in the user dir**
(`amplistorHelper.getFile(name)`). The viewer file is in a subfolder and is not a
known `LangevinFileType`, so it is simply never referenced.

**The one real retention risk is archival, not the SLURM job.** VCell moves old sim
data to secondary/amplistor and prunes the primary using **flat canonical
filenames** (`getFile(name)` / `createCanonicalSimZipFileName`). A `_FOLDER`
subdirectory is not a canonical flat file, so it would not be archived and would be
lost when the primary dir is pruned. Fresh sims have the file; archived ones may not.
→ this is why the Phase 0 hook (§6) should **canonicalize** the viewer file to the
flat user dir rather than read it in the subfolder.

### 2.3 PDE field visualization — a mature VTK pipeline already exists (file/export-oriented)

VCell already standardizes on the **VTK data model** for PDE/finite-volume/Chombo
solutions:

```
VisMesh (Thrift, solver-neutral geometry+field model)
  -> org.vcell.vis.vtk.VtkService / VtkServicePython           (vcell-core)
     -> pythonVtk/python_vtk/vtkService/vtkService.py           (emits .vtu)
        -> served via VtkManager / VtuFileContainer             (vcell-core/server)
           -> handed off to VisIt (external tool)               (today)
```

Key files: `pythonVtk/.../vtkService.py`, `org/vcell/vis/vtk/VtkServicePython.java`,
`cbit/vcell/simdata/VtkManager.java`, `org/vcell/vis/io/VtuFileContainer.java`,
the `org.vcell.vis.mapping.{vcell,chombo,movingboundary,comsol}` writers, and
`cbit/vcell/simdata/VtkMeshGenerator.java` (runs post-simulation, server-side).

It is **offline/batch, server-side, mesh-oriented, and `.vtu`-export based** —
there is no in-application VTK render window, and nothing in it handles particle
data. Reusable for a future PDE viewer: the `VisMesh` abstraction and the
`.vtu`/`.vti` outputs. **The important consequence:** the standard advice
"standardize on the VTK data model so one pipeline can feed both desktop and web"
is *already VCell's reality for fields* — we emit VTK today, we just hand it to
VisIt instead of rendering it ourselves.

---

## 3. The problem is two workloads × two hosts

| | **Particles (SaLaD)** | **PDE fields** |
|---|---|---|
| **Data today** | ❌ trajectory file written but discarded (§2.1) | ✅ VTK pipeline exists (`.vtu`), handed to VisIt |
| **Render today** | ✅ trackball/camera; ❌ no glyphs | ✅ but out-of-app (VisIt) |
| **Render difficulty** | trivial (low-thousands opaque glyphs) | needs a real sci-viz engine (volume/isosurface) |
| **Natural host** | desktop Swing, now | web (strategic) or desktop-VTK (interim) |

The two halves start from completely different places, which is why a single
renderer for everything is the wrong instinct. They are unified only by the **VTK
data model**, which VCell already produces for fields.

---

## 4. Technology landscape (2026)

Researched 2026-07; key sources in §7. Confidence noted inline.

### 4.1 Pure-Java desktop
- **Java2D + impostor sprites** — legitimate, *not* naive, for **opaque** glyphs at
  hundreds–low-thousands. Pre-render a shaded sphere to a `BufferedImage`, blit it
  scaled + depth-tinted, painter's-sort by camera-space Z. Zero native deps. Breaks
  down only with translucency, per-fragment lighting, huge N, or true 3D picking.
  (High confidence.)
- **JavaFX 3D (OpenJFX)** — the healthiest *pure-Java 3D* option in 2026 (OpenJFX 26
  on an active release train). Built-in `Sphere`/`MeshView`/`PhongMaterial`/
  `PerspectiveCamera`/picking; embeds in Swing via `JFXPanel`; first-class macOS
  arm64; uses native GPU under the hood (not tied to deprecated OpenGL). Per-node
  `Sphere` overhead means you merge into a single `TriangleMesh` past a few thousand
  — well-trodden. The escalation from Java2D if we want real z-buffer/lighting.
  (High confidence.)
- **JOGL/JogAmp** — alive again (2.6.0, 2025) but effectively single-maintainer, and
  its macOS future rides Apple's **deprecated** OpenGL (no Metal backend). Bridge
  risk; not recommended for new work. (Medium-high confidence.)
- **LWJGL 3** — healthiest binding, but game/fullscreen-oriented; Swing embedding is
  a community bridge. Wrong altitude for a glyph panel.
- **Java 3D (`javax.media.j3d`)** — dead (last release 2020). Do not use.

### 4.2 Java-VTK (desktop, full VTK in-process)
Kitware **just revived** the Java wrappers in **VTK 9.6 (Feb 2026)** — fixed the
generated-wrapper build, restored Java Maven publishing, added Java CI — and now
ship prebuilt jars (`org.vtk:vtk-java8` on GitHub Packages) with natives for Win/
Linux/mac **x64 and mac arm64**. But it remains a **strategic liability** for a new
viewer: ~100–300 MB native libs per platform (installer into the hundreds of MB),
JNI/JOGL/HiDPI integration warts, a **deprecated OpenGL** backend on macOS with **no
confirmed Java route** to the coming Metal-via-WebGPU renderer, a second-class
distribution channel (GitHub Packages, not Maven Central), and the unmistakable
signal that Kitware's investment has moved to the **web** (vtk.wasm / trame /
WebGPU). Notably, the two features we'd most want — **glyphs and volume rendering**
— are the *last* items on VTK's WebGPU roadmap (late 2026–2027). **Treat as a bridge,
not a 10-year bet.** (High confidence on facts; medium on integration health.)

### 4.3 Web (browser)
- **vtk.js** — actively maintained (v36, 2026); the pragmatic default for **field**
  viz (volume rendering, isosurface, colormapped slices, reads `.vti`/`.vtp`) on the
  stable WebGL backend. Heavier API (full VTK pipeline mental model), which mirrors
  desktop VTK. (High confidence.)
- **three.js** — `InstancedMesh` + built-in Orbit/Trackball/Arcball controls is
  ideal for the **glyph** half; but it has no scientific field pipeline, so you'd
  reinvent isosurface/volume/readers for fields. Right tool for glyphs only.
- **vtk.wasm** — full VTK C++ compiled to WebAssembly; emerging-to-production (best
  proven in the trame server-mirror pattern), WebGPU volume rendering roadmapped
  ~Q3 2026, heavier bundle. **Watch, don't adopt as foundation** — it and vtk.js are
  designed to coexist, so standardizing on vtk.js now keeps the wasm path open.
- **⚠️ Trap:** embedding a web renderer *inside* the Swing app means embedding
  Chromium (JCEF/JxBrowser) — a **larger** multiplatform native dependency than JOGL.
  The web path only pays off if the *client itself* is the browser.

---

## 5. SpringSaLaD viewer-file format (the trajectory data)

Produced by `LangevinNoVis01` `MySystem.writePositions()` / `writeViewerFileHeader()`.

- **Location:** `<SIM_FOLDER>/viewer_files/<name>_VIEW_Run0.txt`
- **Run0 only** (`if(runCounter == 0)` … "we only save movie for Run0") — the movie
  is a single stochastic replicate, not an ensemble.
- **Cadence:** written at t=0, then every `dt_image` (`nextImageTime`).
- **Encoding:** tab-delimited ASCII, appended per frame.

```
TotalTime   <totalTime>
dtimage     <dtimage>
xsize       <xmax>
ysize       <ymax>
z_outside   <-zmin>
z_inside    <zmax>
                                 <- blank line ends header
SCENE
SceneNumber <n>   CurrentTime   <time>
ID   <siteID>  <radius>  <color>  <x>  <y>  <z>      <- one line per site (x/y/z, 6 decimals)
ID   ...
Link <siteID_a>  :  <siteID_b>                        <- structural links...
Link <siteID_a>  :  <siteID_b>                        <- ...and dynamic bonds (both labeled "Link")
                                 <- blank line ends the SCENE
SCENE
...
```

Each `SCENE` is one timepoint carrying every site's **id, radius, color, xyz** plus
**connectivity** — a 1:1 match for sorted sphere glyphs + link cylinders + a frame
slider. The header's `xsize/ysize/z_outside/z_inside` give the scene bounding box
for the camera.

### Caveats to design around
- **Run0 only** — label honestly in the UI as one realization (run count is
  typically 1 anyway per the solver README).
- **Verbose ASCII** — radius+color re-emitted every frame per site. Comfortable at
  hundreds–low-thousands × ~100 frames, but long sims / tiny `dt_image` / many
  particles bloat it; consider compressing or transcoding to a compact binary (or a
  `.vtp`) at collection time.
- **Units/bounds** come from the header — use them; don't assume nm vs µm.
- **Links vs bonds both labeled `Link`** — structural links are constant, bonds are
  dynamic; to render them differently, cross-reference the static topology from the
  model rather than the label.

---

## 6. Recommendation — phased

Unify on the **VTK data model** as the through-line so desktop-now and web-future
coexist without throwaway. Do **not** pick one renderer for everything.

### Phase 0 — Serve the SaLaD trajectory file ✅ **DONE** (#1795, #1797, #1798; solver 1.4.9)
Both options in the original plan turned out to be needed:
- **Option B (canonicalize in `postprocess`)** — shipped for server/remote runs:
  `ConsolidationPostprocessor.canonicalizeTrajectoryFile()` copies the viewer file to
  the flat `SimID_<key>_0__VIEW_Run0.txt` (LangevinNoVis01 #39, released **1.4.9**;
  VCell pinned to it).
- **Option A (read-in-place subfolder fallback)** — *also* required, because **local
  desktop runs invoke only `simulate`, never `postprocess`**, so nothing canonicalizes
  locally. `SimulationData.getLangevinViewerFile()` tries the flat name first, then the
  `_FOLDER/viewer_files/` subfolder.
- **Serve / parse** — `getLangevinTrajectory` down the `getLangevinBatchResultSet`
  chain; `SpringSaladTrajectory` model + SCENE parser (§5). RPC only (no REST yet) —
  sim-data goes through the JMS data server, not `/api/v1`.
- **Remote-run gotcha** — the client's remote controller
  (`LocalDataSetControllerMessaging`) initially missed the RPC delegation (silent null
  via a `default` interface method), fixed in #1798.

### Phase 1 — Desktop SaLaD glyph movie player ✅ **DONE** (#1796, #1797, #1798)
Built on the existing `cbit.vcell.render` trackball/camera with **Java2D impostor
spheres** — the low-risk, zero-native-dep choice, confirmed smooth at scale.
`SpringSaladViewerCanvas` (painter's-sorted impostors, depth-shaded sprite cache) +
`SpringSaladViewerPanel` (play/pause, frame scrubber, speed, Links/Box/Membrane
toggles), wired as the results-viewer **"3D Trajectory"** tab. Added the **simulation
box** (12 edges, hidden-line-removed by dicing into depth-sorted segments) and an
**opaque green membrane** (z=0 plane). A headless `renderToImage()` backs the preview
tests and a future frame/movie export.

### Phase 1.5 — near-term SaLaD-viewer polish (optional, incremental, desktop)
Small, high-value follow-ups, none requiring new infrastructure:
- **Frame / movie export** (PNG sequence or animated GIF) — `renderToImage()` + the
  existing `GIFUtils` path already prototype this in the render test.
- **Rendering quality** — per-fragment specular, configurable membrane color/opacity,
  an always-visible faint reference box, soft shadow/AO on the membrane.
- **Interaction** — pick/hover to identify a site, per-molecule-type show/hide,
  color-by-state legend, named camera views.
- **Data / bridge** — handle very long trajectories (streaming/decimation), and expose
  the trajectory over `/api/v1` (REST) as the bridge toward the web viewer.

### Phase 2 — PDE field viz, the web segue 🟡 **spike done — decision open**
Because the `.vtu`/`.vti` pipeline **already exists** (§2.3), the highest-leverage
capable-stack move is a **vtk.js viewer in `webapp-ng`, fed by the data VCell already
emits** — replacing the VisIt hand-off with in-browser volume/isosurface/slice. This
is the concrete "segue to true web-based rendering."

**The decision to make first** (the strategic fork from §3–4): does the capable
field-viz tier live in the **desktop** (Java-VTK — heavy native packaging, and
Kitware's investment has moved to the web) or the **web** (vtk.js now, vtk.wasm
later)? The analysis favors web.

#### Spike result (de-risk step — done, draft PR #1799)
A standalone `/vtk-spike` route in `webapp-ng` loads a real VCell-style 32³ `.vti` and
renders it with **vtk.js 36.6.0**: a colormapped K-slice + a translucent marching-cubes
isosurface, built and served in the Angular 17 app. **Verdict: feasible** — confirmed by a
headless-Chrome (SwiftShader WebGL) screenshot of the actual rendered route.
Frictions found and their handling (all captured in the PR):

- **Import paths** — the slice mapper is `Rendering/Core/ImageMapper` (there is no
  `ImageSliceMapper`); some filter modules (e.g. `ImageMarchingCubes`) ship **no
  `.d.ts`**, needing `skipLibCheck: true` + a one-line ambient shim.
- **Rendering Profile is mandatory** (silent at build, fatal at runtime) — with
  individually-imported vtk.js modules, nothing registers the WebGL view-node factories, so
  `vtkRenderer` has no view node and `render()` throws (`No vtkOpenGLViewNodeFactory
  implementation found`). Fix: a side-effect `import '@kitware/vtk.js/Rendering/Profiles/All'`.
  The leaner `Geometry` profile registers actors (the isosurface) but **not** `vtkImageSlice`,
  which then silently drops — so `All` is the safe choice for slice + geometry.
- **Bundle size** — vtk.js is ~625 kB gzipped; **lazy-load** the route so it stays out
  of the initial bundle (`main.js` 1.57 MB → 1015 kB). Also allow-list its CommonJS
  deps (`seedrandom`/`spark-md5`/`fast-deep-equal`) in `angular.json`.
- **Real-`.vti` loading — resolved.** vtk.js's XML reader transitively `require`s Node's
  `url` (`XMLReader → xmlbuilder2 → @oozcitak/dom → @oozcitak/url`), which Angular's stock
  browser builder won't resolve. Fix: `@angular-builders/custom-webpack` (a transparent
  drop-in for the `browser`/`dev-server` builders) + a `webpack.config.js` that points
  `resolve.fallback.url` at the browser `url` polyfill. The route now `fetch`es a real
  `.vti` and `reader.parseAsArrayBuffer()`s it — verified headless: ImageData 32³, scalar
  `concentration`, range ~[0, 1.0], 32 768 points. Adoption cost: one dev-dependency + the
  `url` polyfill, and the vtk viewer must stay lazy-loaded (it's ~1 MB with the reader).

**Next (Phase 2 proper):** design the viewer UI (variable / timepoint / isovalue controls)
fed by VCell's export pipeline, replacing the VisIt hand-off with in-browser
slice/isosurface/volume.

**Net:** desktop delivers the SaLaD movie player (done); the web becomes home for the
capable PDE field-viz tier — unified by the VTK data model VCell already standardized on,
and now proven to render in the browser.

> **Superseded by §8 for the field case.** The reasoning above assumed vtk.js could carry
> field viz. It can for the *structured* `.vti` case (spike done), but the faithful FV
> representation is *smoothed unstructured*, which vtk.js cannot render. See **§8** for the
> worked-out two-viewer / two-contract architecture and the vtk.wasm decision.

---

## 7. Sources

Technology landscape (accessed 2026-07):
- JavaFX / OpenJFX: [JavaFX 26 (Gluon, 2026-03-17)](https://gluonhq.com/news/javafx-26-is-now-available/), [openjfx.io](https://openjfx.io/), [Oracle JavaFX 3D graphics](https://docs.oracle.com/javafx/8/3d_graphics/shapes_3d.htm)
- JOGL: [JogAmp releases (2.6.0, 2025-08-31)](https://jogamp.org/), [supported platforms incl. macosx-aarch64](https://jogamp.org/gluegen/doc/JogAmpPlatforms.html)
- LWJGL: [releases (3.4.1, 2026-02-03)](https://github.com/LWJGL/lwjgl3/releases); [lwjgl3-awt](https://github.com/LWJGLX/lwjgl3-awt)
- Java 3D dead: [last release 1.7.0, 2020](https://github.com/hharrison/java3d-core/releases)
- Java-VTK: [VTK 9.6.0 (Kitware, 2026-02-18)](https://www.kitware.com/vtk-9-6-0/), [org.vtk:vtk-java8 GitHub Packages](https://github.com/Kitware/VTK/packages/2400780), [WebGPU roadmap](https://www.kitware.com/webgpu-one-graphics-api-to-rule-them-all/), [VTK WebGPU desktop (unimplemented glyph/volume)](https://www.kitware.com/vtk-webgpu-on-the-desktop/)
- Web: [@kitware/vtk.js (npm)](https://www.npmjs.com/package/@kitware/vtk.js), [vtk.js volume rendering (v26)](https://www.kitware.com/vtk-js-v26-release-notes/), [VTK.wasm + trame](https://www.kitware.com/vtk-wasm-and-its-trame-integration/), [three.js InstancedMesh](https://threejs.org/docs/pages/InstancedMesh.html), [VTK XML file formats](https://docs.vtk.org/en/latest/vtk_file_formats/vtkxml_file_format.html)

Codebase (this repo unless noted):
- SaLaD trajectory writer: `../LangevinNoVis01/src/main/java/edu/uchc/cam/langevin/langevinnovis01/MySystem.java` (`writePositions`, `writeViewerFileHeader`)
- Solver integration: `vcell-core/.../org/vcell/solver/langevin/{LangevinSolver,LangevinLngvWriter,LangevinFileWriter}.java`
- Result model (aggregates only): `vcell-core/.../cbit/vcell/simdata/{LangevinBatchResultSet,LangevinPostProcessor}.java`
- HPC data-flow (§2.4): `vcell-server/src/main/resources/slurm/templates/langevinFixture.slurm.sub` (no cleanup; `/simdata/${USERID}`); solver folder rooting `../LangevinNoVis01/.../Global.java` + `MySystem.folderSetup()`; aggregate write `../LangevinNoVis01/.../ConsolidationPostprocessorOutput.java`; VCell read path `cbit/vcell/simdata/DataSetControllerImpl.java` (`getLangevinBatchResultSet`) → `SimulationData.getLangevinFile()`
- Render infra: `vcell-core/.../cbit/vcell/render/{Trackball,Quaternion,Camera}.java`; consumers `vcell-client/.../geometry/gui/{SurfaceCanvas,SurfaceRenderer}.java`
- VTK/PDE pipeline: `pythonVtk/.../vtkService.py`; `vcell-core/.../org/vcell/vis/vtk/VtkServicePython.java`; `vcell-core/.../cbit/vcell/simdata/{VtkManager,VtkMeshGenerator}.java`

vtk.wasm spike (§8, accessed 2026-07-30):
- [Introducing WebAssembly support in VTK (Kitware)](https://www.kitware.com/introducing-webassembly-support-in-vtk/); [Building VTK with Emscripten](https://docs.vtk.org/en/latest/advanced/build_wasm_emscripten.html); [VTK.wasm site](https://kitware.github.io/vtk-wasm/) + [guide](https://kitware.github.io/vtk-wasm/guide/); [`@kitware/vtk-wasm` npm](https://www.npmjs.com/package/@kitware/vtk-wasm); [VTK build settings — module enable + `VTK_WASM_OPTIMIZATION`](https://docs.vtk.org/en/latest/build_instructions/build_settings.html); [vtk.js UG-reader limitation (#976)](https://github.com/Kitware/vtk-js/issues/976); [VTK::RenderingWebGPU](https://docs.vtk.org/en/latest/modules/vtk-modules/Rendering/WebGPU/README.html)

---

## 8. Phase 2 field-viz architecture — deep dive & decision (2026-07-30)

> **Superseded for anything after 2026-08-06** by
> [`3d-renderer-design.md`](3d-renderer-design.md). Kept here as the record of how the
> architecture was reasoned out; the sections below are accurate as of their date but are no
> longer where field-viz decisions are made.

The Phase 2 vtk.js spike (§6) proved the *structured* case (`.vti` slice + isosurface).
Working through the real requirements changed the architecture substantially. This
section is the decision record.

### 8.1 Two hard truths about the field case

1. **vtk.js is a surface renderer — no unstructured grids.** Verified against
   `@kitware/vtk.js` 36.6.0: the data model is `PolyData` + `ImageData` only (no
   `vtkUnstructuredGrid`); `Cutter` operates on polydata; the XML readers are `.vtp`
   / `.vti` only (no `.vtu`). It can volume-render *ImageData*, nothing unstructured.
2. **Image `.vti` reinforces the staircase membrane** — the exact artifact VCell's
   VTK pipeline was built to remove. VCell solves on an implicit surface and already
   computes a **smoothed unstructured representation** (smoothed staircase quad
   surface + adjacent volume hex elements decimated into tetrahedra) precisely to
   represent solutions faithfully. So **the good FV representation is unstructured**,
   and `.vti` must not be the only FV path.

Together: the field viewer must consume **smoothed unstructured** data, and vtk.js
alone cannot render it.

### 8.2 The finite-element solver moves the center of gravity

`../vcell-fenics` (FEniCSx / **DOLFINx**, Python/conda via pixi, `mpi4py`/`petsc4py`;
`src/vcell_fenics/viz.py` already uses **PyVista + XDMF**) is a **containerized
Slurm/HPC** solver being brought up. It makes **unstructured grids a first-class,
near-term requirement**, and it **pre/post-processes in its own container**, so it can
emit viewer-ready VTK there (where the conda/VTK stack already lives). **Chombo is
retired** — its use case is superseded by vcell-fenics — so the only unstructured
future is FEniCSx. Because the FE solver runs isolated on HPC, its Python-ness does
**not** dictate the language of the VCell services. `pyvcell` independently already
has the FV smoothing pipeline (`write_finite_volume_smoothed_vtk_grid_and_index_data`)
and a trame+pyvista viewer.

### 8.3 The resolving model: two viewers × two data-product contracts

Two stable **data-product contracts** — *image* and *smoothed-unstructured* — and
**every solver emits both** as a postprocess. Each viewer consumes the one that fits:

| | **Legacy in-Swing viewer** (image) | **Modern web viewer** (unstructured) |
|---|---|---|
| **FV (structured native)** | native image — works today | smoothed `.vtu` (existing mesh pipeline) |
| **FEniCSx (unstructured native)** | resample FE → image (in-container, DOLFINx point-location) | native unstructured |

Consequences:
- **Local desktop is fully covered by the existing image viewer** — FV natively,
  FEniCSx via its server-side image resample (downloaded like any result). **No Python,
  no Java-VTK, no bundled renderer on the desktop.** (Image is *a* case for FV, not the
  only one — the good smoothed view lives in the web viewer.)
- **Processing ≠ serving.** Heavy mesh→VTK work is a **per-solver postprocess** (the
  proven SaLaD "canonicalize-and-serve" pattern, §6 Phase 0). **Serving is thin,
  language-agnostic file transport** — the existing Java `vcell-rest` hands out the
  pre-made `.vti`/`.vtu` bytes with no VTK logic. No new Python service is needed for
  serving; `pyvcell` / `viz.py` are postprocess *libraries* in solver containers (and
  the place to converge the mesh→VTK logic rather than fork it a third time).

### 8.4 Producing each contract

- **Image** (regular Cartesian): trivial XML in any language. FV is native; a Java
  on-demand writer (client-side for local, `vcell-rest` for remote, reusing
  `getSimDataBlock` + `CartesianMesh`) can also emit `.vti` with no solver change and
  works on historical results. FEniCSx resamples FE→image in-container.
- **Smoothed unstructured** (`.vtu`): FV via the existing `CartesianMeshVtkFileWriter`
  (pure Java, no VTK lib) or pyvcell; FEniCSx native.

### 8.5 Rendering unstructured in the browser — the vtk.wasm spike

Since vtk.js can't render unstructured, two options, both feeding the smoothed `.vtu`:
- **(A) geometry server + vtk.js** — pyvcell/pyvista cuts the tet mesh into polydata
  (surface / slices / isosurfaces) server-side → `.vtp` → vtk.js renders. Light client;
  interactivity via cached server round-trips; **no true volume rendering**.
- **(B′) vtk.wasm client** — real VTK compiled to WebAssembly reads the `.vtu` and
  slices/contours/volume-renders **client-side**; stateless serving, no render farm.
  (Replaces the earlier trame "pixel-server" option, which the team is cool on.)

**Spike (2026-07-30, headless Chrome + `@kitware/vtk-wasm` 2.1.8, real VCell smoothed
`.vtu`: 16 306 pts / 12 210 cells):**

| | Result |
|---|---|
| Size | released bundle **77 MB wasm / 12 MB gzipped**; load+init ≈300 ms |
| Render path | ✅ real VTK→WASM→WebGL2 renders to canvas headless (SwiftShader) |
| **Present** in released bundle | `vtkUnstructuredGrid`, `vtkPolyData`, `vtkImageData`, **`vtkCutter`**, **`vtkContourFilter`**, `vtkThreshold`, `vtkGlyph3D`, and **`vtkProjectedTetrahedraMapper`** (unstructured *volume* rendering) |
| **Missing** in released bundle | **all IO/XML readers** (`vtkXMLUnstructuredGridReader` → can't read `.vtu`), **surface extraction** (`vtkDataSetSurfaceFilter`/`vtkGeometryFilter`), `vtkClipDataSet`, `GLTFImporter` |
| Frictions | released bundle can't read `.vtu`; async proxy API (every call awaited); **COOP/COEP** headers required |

**Punchline:** the hard part vtk.js lacks — the unstructured data model, slicing,
contouring, and projected-tetrahedra **volume rendering** — is already present and
working in vtk.wasm. The only gap for VCell is the **IO readers** (+ surface filter).

**What a custom build buys us** (module selection via `VTK_MODULE_ENABLE_<m>=WANT` +
`VTK_WASM_OPTIMIZATION`): add exactly `IOXML` (read `.vtu`/`.vti`/`.vtp`) and
`FiltersGeometry` (surface extraction) to the already-proven core; **trim the 77 MB to
a fraction** by dropping unused modules; optional **WebGPU** backend
(`-DVTK_ENABLE_WEBGPU=ON`); algorithmic parity with pyvcell/desktop. It lets us **serve
the `.vtu` VCell already produces**, push all viz compute **client-side** (stateless
serving, no render farm), and retire the VisIt hand-off. Costs: own an Emscripten build
+ CI; COOP/COEP headers; MB-scale binary (lazy-loaded); the wrap/WebGPU APIs are
maturing. A hybrid — a lean IO+filters wasm feeding vtk.js for rendering — is a
smaller-footprint waypoint that reuses the §6 spike.

### 8.6 Decision

- **Modern web unstructured viewer = the build target** (the genuinely new capability).
  Pursue **vtk.wasm (custom build adding `IOXML` + `FiltersGeometry`, module-trimmed)**
  as the strategic renderer: it serves VCell's existing `.vtu`, keeps serving stateless,
  puts compute client-side, and rides Kitware's web/WebGPU investment. **(A)
  geometry-server + vtk.js** (or the wasm-kernel→vtk.js hybrid) is the lighter
  interim/fallback.
- **Local desktop = existing image viewer** (no new local tech); FEniCSx resamples to
  image in-container for it.
- **Serving = thin Java `vcell-rest`** file endpoint. **Processing = per-solver
  postprocess.** **Chombo retired.** Java-VTK and trame de-emphasized (against the grain
  of a Python FE solver / a server render farm the team is cool on).

### 8.7 Next increments

1. **Custom vtk.wasm build** (`IOXML` + `FiltersGeometry`, trimmed) → prove a real VCell
   smoothed `.vtu` renders (surface + cut + contour + projected-tetrahedra volume) in the
   browser; measure trimmed binary size + interaction perf. (Released bundle already
   proved the core; this closes the reader gap.)
2. **Thin Java serve endpoint** in `vcell-rest` for `.vti`/`.vtu` bytes.
3. **FEniCSx postprocess** emits both contracts (native `.vtu` + FE→image resample) as
   it deploys.

---

## 8B. Resolution after a deeper design pass (2026-07-30)

§§8.8–8.13 below **supersede the fallback framing in §8.5–8.7 where they conflict** —
specifically "geometry-server + vtk.js as the near-term path" and "local desktop = the
existing image viewer." A longer design conversation (moving boundaries, local
computation, and reading the actual FV→VTK code) resolved the architecture more sharply.

### 8.8 Moving boundaries make it a geometry movie — but a *piecewise* one

`vcell-fenics` supports **moving boundaries in 2D and 3D**. For those, the geometry is a
function of time — mesh, membrane, and every slice change each frame — so the elegant
"geometry once + scalar stream" optimization applies only to **static-mesh** sims
(regular FV, static FE), not moving ones. It's a *geometry movie*.

But it's **piecewise**: the solver runs **ALE** (nodes move, topology fixed) for several
steps, then **remeshes** when mesh quality degrades. So the series partitions into
segments `[topo T₁ | frames…][remesh][topo T₂ | frames…]…`, which recovers a lot:
- **Connectivity is shipped once per segment** (remesh is occasional → amortized); most
  frames are just node positions + field.
- **The boundary/membrane surface — the primary moving-boundary visual — is cheap**: its
  face topology is fixed within a segment, so surface connectivity ships once per segment
  and you stream boundary positions + field per frame.
- **Per-frame regardless:** arbitrary volume slices (a fixed-world plane crosses different
  points as nodes move) and isosurfaces (field-dependent).

**XDMF** natively expresses exactly this (time grids that *reference a shared topology* +
per-step geometry/attributes), and `vcell-fenics` already emits XDMF → pyvista/VTK read it.

### 8.9 Local computation decides the renderer = **vtk.wasm**

The VCell client spawns **local solvers on the laptop** (the C++ FV / moving-boundary
solvers; **not** FEniCSx, which is a conda/PETSc/MPI stack → HPC-only). **Local has no
Python → no pyvista.** Therefore the **geometry-server option (§8.5-A) cannot serve local
computation at all** — it would leave local on the staircase image viewer.

**vtk.wasm is the only path that gives good unstructured vis both on the web *and*
locally without Python or native code on the client** — because it *is* real VTK, running
as wasm in a browser; the client does the cutting/contouring. Local mechanism, no
Python / no native-VTK / no server:
- The local solver's output is turned into the client data contract locally (see §8.10).
- The desktop spins a **tiny pure-Java local HTTP server** (with the COOP/COEP headers
  wasm threads need), serving the viewer bundle + the local data, and opens the **system
  browser** — deliberately avoiding an embedded Chromium (JCEF), which would be exactly
  the heavy native dependency the packaging constraint (§1) fights.
- The vtk.wasm viewer bundle ships once with the installer (tens of MB).

This is what makes vtk.wasm **strategic, not optional**, and it **supersedes §8.6's
"local desktop = existing image viewer."**

### 8.10 Do the geometry *in* vtk.wasm, fed by a trivial `.vti`

Best data contract: solvers emit the **raw** representation, **not** a pre-smoothed `.vtu`:
- **FV** → a trivial **`.vti`** (regular Cartesian grid: field scalars + a **region-label
  cell array**). Pure serialization — no VTK, no smoothing, no custom mesh enumeration.
- **FE / moving boundary** → the **body-fitted mesh** as-is (XDMF / `.vtu` sequence,
  segmented by ALE topology per §8.8).

Then **vtk.wasm does everything client-side with stock filters**:
- FV: `vtkThreshold` (region == domain → the domain's voxels; VTK preserves original
  point/cell IDs so the field maps back automatically) → `vtkGeometryFilter` →
  `vtkWindowedSincPolyDataFilter` (the smoothing) → slice / contour / render.
- FE: render / slice / contour the body-fitted mesh directly.

Consequences: **no smoothing writer in C++, Java, or Python anywhere** — the smoothing
lives once, in VTK-wasm; smaller transport (raw grid ≪ explicit smoothed unstructured);
fully interactive (the client holds the raw field). Static FV smooths **once** client-side
and caches, streaming scalars; moving boundary ships the segmented mesh and the client
re-cuts per frame — giving **interactive slicing across a moving boundary** that a
precomputed polydata movie can't. This supersedes §8.4/§8.5's "server produces the
smoothed/`.vtp` geometry."

### 8.11 What the FV→VTK transform actually is (code read, 2026-07-30)

Read pyvcell (`_internal/simdata/vtk/`) and Java (`org.vcell.vis`) to settle whether the
smoothing is stock VTK or a custom clip-to-tets algorithm:

- **FV** (`fromMesh3DVolume` in Java = `from_mesh3d_volume` in pyvcell, **identical**):
  emit a **whole hex** (`VisVoxel`) per domain cell + point dedup + FV-index tag, **no
  clipping**. Smoothing = `vtkUnstructuredGridGeometryFilter` → `vtkGeometryFilter` →
  **`vtkWindowedSincPolyDataFilter`** (12 iterations, pass-band 0.05, feature angle 120°,
  boundary/feature-edge smoothing off, normalize-coords on), then displace the boundary
  corner points onto the smoothed surface. **Stock VTK, cheap, deterministic.** The
  "initially a hex … then may be clipped" comment is **vestigial** — nothing clips in FV.
- **FE / moving boundary:** the mesh arrives **body-fitted** from DOLFINx — **no smoothing
  or clipping at all**.
- **Chombo (RETIRED):** the *only* path with clip→irregular-polyhedra→per-cell
  `vtkDelaunay3D` — and there the **solver** emits the embedded-boundary cut cells; the
  mapper only tetrahedralizes them. Superseded by `vcell-fenics`.

So the per-cell `vtkDelaunay3D` (with its inside-out/empty edge cases) **does not apply to
the kept paths.** FV is Threshold + WindowedSinc; FE is already conforming. That is what
makes "geometry in vtk.wasm" cheap and robust rather than a per-cell tessellation gamble.

### 8.12 Custom vtk.wasm build — prototype results (2026-07-30)

A Dockerfile (emsdk 4.0.20 + VTK + trimmed modules: groups `DONT_WANT`, then `WANT`
`IOXML`/`FiltersGeometry`/`FiltersCore`/`FiltersGeneral`/`FiltersSources`/`RenderingOpenGL2`/
`RenderingVolumeOpenGL2`/`RenderingUI`/`Interaction*`, `VTK_WRAP_JAVASCRIPT=ON`), built
incrementally via a docker volume so opt-level changes relink cached objects.

- **Proven:** VTK **9.6.2** configures + fully compiles (9 282 objects) + links to wasm
  with our module set. The recipe is structurally sound.
- **Size:** `LITTLE`-opt, *including* rendering ≈ **62 MB / 10.9 MB gzipped** (vs the
  released all-modules **77 MB / 12 MB**). Modest — real trimming needs `SMALLEST(_WITH_
  CLOSURE)` (which crashed emscripten's acorn optimizer on 9.6.2) or the IO/filters-only
  "data kernel" hybrid (drop rendering, feed vtk.js).
- **Blocked (runnable, loader-compatible bundle):** the `@kitware/vtk-wasm` loader targets
  VTK **master**'s `vtkWebAssembly.mjs` and string-patches the glue; VTK 9.6.2 emits
  `vtkweb.js` (different shape, not directly importable); VTK **master HEAD** would not
  compile core embind against emsdk 4.0.20 in the container (a moving-target break, even
  though VTK's own CI pins that emsdk).
- **Conclusion:** the module recipe works; getting a *runnable, loader-matched, optimized*
  bundle means pinning the exact **(VTK commit, emsdk, configure, loader version)**
  quadruple Kitware's CI uses — a real CI/version-pinning exercise, not a laptop build.
  This confirms the "own the Emscripten build" cost is **real but bounded**.

### 8.13 Settled decision (supersedes §8.6 where they conflict)

- **Renderer: vtk.wasm, everywhere** — `webapp-ng` (web) and the desktop via
  **system-browser + a local pure-Java HTTP server**. It is the only *client-portable*
  real VTK, and the **local-computation requirement (§8.9) is what decides this** over the
  geometry-server.
- **Data contract: solvers emit the raw representation** — FV a trivial `.vti` (grid +
  region labels + field); FE/moving-boundary the body-fitted mesh (XDMF/`.vtu`, ALE-
  segmented). **vtk.wasm does threshold/smooth/slice/contour/render client-side.** No
  smoothing writer in C++/Java/Python.
- **Serving is thin and stateless** (vcell-rest bytes for web; local Java file server for
  desktop). The heavy VTK work is **client-side in wasm**, not a server or a per-solver
  smoothing postprocess.
- **Option A (geometry-server + pyvista `.vtp`) is demoted** — it can't serve local
  computation; keep only as a possible web-only interim if the wasm build slips.
- **Chombo retired; Java-VTK and trame out.**

**Revised next increments:**
1. **Custom vtk.wasm build in CI**, pinned to a loader-compatible `(VTK commit, emsdk,
   configure)`; module set `IOXML` + `FiltersGeometry` + `FiltersCore`/`FiltersGeneral`
   (Threshold, WindowedSinc, Cutter, Contour) + rendering. Home it in the monorepo
   (`tools/vtk-wasm/`, `workflow_dispatch`-gated) for now; graduate to
   `virtualcell/vcell-vtk-wasm` if it grows.
2. **Trivial FV `.vti` writer** (pure-Java or C++): grid + region-label array + field.
3. **Golden-file test:** client-side `vtkThreshold` + `vtkWindowedSincPolyDataFilter`
   reproduces the pyvcell/Java `.vtu`.
4. **webapp-ng viewer** + **desktop launch** (local Java server + system browser).
