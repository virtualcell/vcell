# SpringSaLaD Spatiotemporal 3D Renderer — Design & Decision Record

**Status:** Draft — for discussion
**Date:** 2026-07-29
**Scope:** An integrated spatiotemporal 3D renderer / movie player for SpringSaLaD
(Langevin particle) simulations in VCell, with a possible extension to visualizing
PDE (reaction-diffusion) solutions using a more capable stack.

> This is a living design record: it captures the problem, what already exists in
> the codebase, the 2026 technology landscape, and a phased recommendation. The
> strategic desktop-vs-web decision (Phase 2) is deliberately left open pending a
> prototype; Phase 0/1 are actionable now.

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

### Phase 0 — Serve the SaLaD trajectory file *(prerequisite, renderer-agnostic)*
The trajectory data already exists **and already persists** on the shared store
(§2.4); nothing deletes it at job end. The work is to **canonicalize and serve it**,
not to retain it. Per the §2.4 trace the hook is small and symmetric with existing
langevin result handling:

1. **Canonicalize** (recommended — *Option B*): in the solver's `postprocess` step
   (which already writes the aggregate `.ida` files flat into `/simdata/<userid>/`),
   also copy the viewer file up to a flat canonical name
   `SimID_<key>_0__VIEW_Run0.txt` in the user dir. It then participates in the same
   discovery, **archival**, and serving as the `.ida` files. *(One line of copy.)*
   - *Option A (read-in-place)* — add a getter that reaches into
     `…_FOLDER/viewer_files/…` with no postprocess change. Works for fresh sims but
     is **archival-fragile** (lost once the sim is moved to secondary; see §2.4), so
     it is not the durable answer.
2. **Serve** — add `SimulationData.getLangevinViewerFile()` (mirroring
   `getLangevinFile()`), a `DataSetControllerImpl`/`LocalDataSetController` method
   (mirroring `getLangevinBatchResultSet()`), and the RPC/REST plumbing. This mirrors
   code that already exists for the aggregates.
3. **Parse** — a trivial tab-delimited SCENE reader (§5) → in-memory trajectory model
   (frames of `{id, radius, color, xyz}` + links).

**Net:** no solver-algorithm change and no SLURM change — one canonicalizing copy in
`postprocess` plus a `getLangevinFile`-shaped serving path.

### Phase 1 — Tactical: desktop SaLaD glyph movie player
Build on the existing `cbit.vcell.render` trackball/camera; render glyphs with
**Java2D impostors** (lowest risk, zero native deps, confirmed viable at this scale)
— or **JavaFX 3D** if we want real z-buffer/lighting headroom cheaply. Ships SaLaD
value fast. The *player shell* (playback, scrub, time-sync, bookmarks, frame/movie
export) is reusable regardless of what draws pixels later; only the glyph rasterizer
would be subsumed if a VTK-based path takes over rendering.

### Phase 2 — Strategic: PDE field viz as the web segue
Because the `.vtu`/`.vti` pipeline **already exists** (§2.3), the lowest-cost,
highest-leverage capable-stack move is a **vtk.js viewer in `webapp-ng`, fed by the
data VCell already emits** — replacing the VisIt hand-off with in-browser volume/
isosurface/slice. This is the concrete "segue to true web-based rendering," cheap
*because the data layer is done*. Reserve **Java-VTK** only as an interim if field
viz is needed *inside the Swing app* before the web path is ready.

**Net:** desktop delivers the SaLaD movie player; the web becomes home for the
capable field-viz tier — unified by the VTK data model VCell already standardized on.

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
