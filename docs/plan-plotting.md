# Plan — multi-point time plots and kymographs in the browser field viewer

**Status:** in progress: P1 to P4 merged (2026-09-29). This is a living plan: tick PRs off as they merge.

## Context

The browser field viewer (`webapp-viewer/`, served by the desktop client's loopback `FieldViewerServer`)
renders VCell's spatial results in 3D and 2D for four kinds of run:
- finite volume (FV) on a Cartesian grid;
- Chombo;
- MovingBoundary;
- FEniCSx results bundles.

Clicking a point gives one time course (`/timeseries`). The Java desktop PDE data viewer can do two things
the browser viewer can't:

1. **Time plots over several points:** one value-vs-time trace per selected point, all in one plot.
2. **Kymographs:** a line (or polyline) through the data, shown as a time × position intensity image.

This plan brings both to the browser viewer. They are part of the field viewer's path to replacing the
desktop viewer's plotting for the solvers it covers (see [`plan-fenics.md`](plan-fenics.md) for FEniCSx,
and the design records [`3d-renderer-design.md`](3d-renderer-design.md) and
[`salad-3d-renderer-design.md`](salad-3d-renderer-design.md)).

## Decisions taken

| # | Question | Decision |
|---|---|---|
| 1 | Membrane curves (a kymograph along a membrane) in the first pass? | **No.** They are the last increment (P7). |
| 2 | FV samples outside the variable's domain | **Gaps** (null), not the raw array values the desktop shows. `raw=1` keeps the raw values for the parity test. |
| 3 | Limits | 64 points (the UI caps at 12), 64 line vertices, 2,000 body-fitted samples, 500,000 samples × times per kymograph. |
| 4 | MovingBoundary kymograph | **Included** (P6), as a fixed lab-frame (Eulerian) line. |
| 5 | Browser tests | **Committed to the repo** (`webapp-viewer/test/`), with a Java launcher for the fixture server. |
| 6 | Units | **Deferred.** Axes are labelled µm and s, VCell's default units. Reporting real units from the server is a later item. |
| 7 | Point plot and Stats plot | **Separate panels.** |

And from the design below:
- **Kymographs are computed on the server, always.** The browser never fetches every time step. This is the
  viewer's existing rule for time courses (README, "Picking is plain JS…": never fetch every timestep to
  build one curve), and it is how the desktop does it.
- **A probe is a lab-frame point in every mode** (`points=x,y,z;…`). For FV the browser sends the centre of
  the picked voxel. The existing `cell=` form stays for one point.
- **FV lines reuse the desktop's own sampling code on the server**, so FV kymographs match the desktop's by
  construction.
- **Markers and lines in the 3D view are an SVG overlay**, projected in JavaScript from the camera, not new
  VTK actors.

## Conventions

- Each PR below is its own branch off `master` and merges with a merge commit.
- Build and test per [`BUILDING.md`](BUILDING.md) (Java 17): `mvn test -pl vcell-client -Dtest=…`.
- Line numbers are approximate, from `master` at `6a0f6d5121`.
- The viewer's standing rules apply (`webapp-viewer/README.md`, "Notes for anyone editing this"):
  - no framework and no build step;
  - no `is not permitted` refusals in the console, since the wasm invoker logs refusals rather than throwing;
  - verify on WebKit and Firefox as well as Chromium (README, "Browser support").

---

## 1. Reference: what the desktop PDE data viewer does

### 1.1 Where selections come from

- Every selection is drawn on the **2D slice image** with the curve editor's point, line and polyline tools
  (`CurveEditorToolPanel`, `PDEDataContextPanel.getInitalCurveSelection`, PDEDataContextPanel.java:965).
- This holds for 3D data too: `fetchSpatialSelections0` projects curves onto the slice
  (`projectCurveOntoSlice`, PDEDataContextPanel.java:689-697). A 3D line therefore always lies in the
  current slice plane.
- The **Plot** button's menu has Spatial, **Time** and **Kymograph** entries
  (`PDEDataViewer.getTimePlotMenuItem` / `getKymographMenuItem`, PDEDataViewer.java:2034-2058).
  Kymograph is enabled only when there is more than one time point (`updateDataSamplerContext`, :2891).
- **Volume vs membrane:**
  - for a volume variable, a `ControlPointCurve` becomes a `SpatialSelectionVolume`;
  - for a membrane variable, the user picks segments of the membrane contour visible in the slice
    (`CurveSelectionCurve` matched against `membranesAndIndexes`), which become a `SpatialSelectionMembrane`
    (PDEDataContextPanel.java:701-758).

### 1.2 Time plot over several points

- `PDEDataViewer.showTimePlot()` (:2597) collects every visible point selection
  (`fetchSpatialSelections(true, true)`).
- It maps each one to a data index with `getIndex(0)`:
  - volume: `mesh.getVolumeIndex` of the containing voxel;
  - membrane: the membrane element.
- It builds **one** `TimeSeriesJobSpec(new String[]{var}, new int[][]{indices}, null, t0, 1, tEnd, …)`
  (:2647), which the data server answers in one job.
- The result opens in `PdeTimePlotMultipleVariablesPanel`
  (vcell-client/…/simdata/gui/PdeTimePlotMultipleVariablesPanel.java):
  - a "Selected Points" list labelled `P[i] (x,y,z)`;
  - a "Y Axis" variable multi-select, re-run as one job with `indices[v][i]` (:190);
  - `var at P[i]` traces drawn with `SingleXPlot2D` (:223).
- The window is a **snapshot**: it does not follow the viewer's time or selection, only its variable list.

### 1.3 Kymograph

- **Selection:** `PDEDataViewer.showKymograph()` (:2438) takes the non-point selections.
  - Volume variable: `SpatialSelectionVolume.getIndexSamples(0.0, 1.0)` gives the sample `indices`,
    `crossingMembraneIndices` and `accumDistances` (:2471-2475).
  - Membrane variable: `SpatialSelectionMembrane.getIndexSamples()` (:2477).
  - With several lines, the **last one wins**: the loop overwrites.
- **Data:** `KymographPanel.initDataManagerVariable` (KymographPanel.java:2010) builds **one**
  `TimeSeriesJobSpec` with `new int[][]{indices}` and the crossing indices, and runs it as a background data
  job. It is **server-side**: for a remote run only samples × times values travel.
- **Volume sampling** (`SpatialSelectionVolume.getIndexSamples`, vcell-core/…/simdata/SpatialSelectionVolume.java:104):
  - An axis-aligned or 45° polyline uses `sampleSymmetric()`, an integer lattice walk that snaps samples to
    voxel centres (:603).
  - Any other line is sampled at 10 points per smallest voxel dimension (`SAMPLE_MULT`), keeping one sample
    per distinct voxel crossed (:137-190).
  - `resampleMeshBoundaries` (:401) computes where the line crosses each voxel face
    (`lineMeshFaceIntersect3D`). At each **membrane face** it inserts **two samples at the same point**, one
    per side, and records the membrane index in `membraneIndexesInOut`.
  - Arc length is the accumulated distance between the sample points (`SSHelper.calculateAccumWCLengths`,
    SpatialSelection.java). Sampling is **one sample per voxel crossed**, not uniform.
- **Membrane-adjacent correction:** with crossing indices, `DataSetControllerImpl.getTimeSeriesValues` calls
  `adjustMembraneAdjacentVolumeValues` (DataSetControllerImpl.java:3181, :3315). It replaces the two
  crossing samples with the variable's `_INSIDE` / `_OUTSIDE` membrane-extrapolated values, so kymographs
  show a sharp jump at the membrane.
- **Membrane sampling** (`SpatialSelectionMembrane.getIndexSamples`): one sample per selected membrane
  segment, at segment endpoints, with arc length along the membrane polyline.
- **Display** (`KymographPanel.initStandAloneTimeSeries_private`, :2187):
  - samples are resampled to `RESAMP_SIZE = nSamples` uniformly spaced distances by **nearest neighbour**;
  - x is "Distance Along Sample Line", y is "simulation Time", **one row per saved time**;
  - the colour map is blue-red (`DisplayAdapterService.BLUERED`, grey optional);
  - scale modes are All, LineScan, TimeSeries and User (`configureMinMax`, :320);
  - a crosshair drives a line-scan plot at the chosen time and a time-series plot at the chosen distance
    (`configurePlotData`, :392);
  - Copy puts a tab-separated distances × times matrix on the clipboard (`copyJMenuItem_ActionPerformed`,
    :911).
- Voxels outside the variable's domain are **not masked**: the raw data array is indexed by global volume
  index, so values from other compartments appear.

### 1.4 What to reproduce and what to do differently

| Reproduce | Deliberately different in the browser |
|---|---|
| One server-side job per request, however many points or samples | Probes and lines stay **live**: they follow variable switches and run refreshes, and a time cursor tracks the slider. |
| FV voxel-crossing samples, the two samples at a membrane, the `_INSIDE`/`_OUTSIDE` correction | Lines can be **3D chords** between surface or cut-face picks, not only lines in the slice. With a crop active, picks land on the cut face, which gives the desktop's in-slice lines. |
| Rows = saved times, x = arc length, blue-red map, All and User scaling, line scan at the current time, copy as a matrix | Samples **outside the variable's domain are gaps** (decision 2), not raw values. `raw=1` restores the desktop's behaviour for parity tests. |
| Several points for one variable (several variables later) | The kymograph is drawn at the samples' **true arc-length spans**, not nearest-neighbour resampled. The resampled form is only an export, for parity. |

---

## 2. Current state (what this plan builds on)

- **Click → one time course.** `attachTrackball` (viewer.js:798): a left press that moves less than 4 px
  calls `plotPick` (:829). A **shift-drag pans** (`panning = e.button !== 0 || e.shiftKey`, :808), but a
  shift-*click* without movement still reaches `plotPick`, so shift-click is free to reuse.
  - FV: `castRay` gives a cell ordinal, then `/timeseries?cell=` (:1158-1176).
  - Body-fitted 2D: `labPointFromMouse` gives `x,y` (:1183).
  - Body-fitted 3D: `pickTetrahedron` gives `x,y,z` (:1200). It handles **tetrahedra only** (it skips
    `cell.length !== 4`, :1036), so **Chombo 3D (voxels and polyhedra) is not pickable today**.
- **Plot.** `plotFrame`, `renderPlot` and `renderStatsPlot` (:1217-1319) draw one SVG, `#plotSvg`, shared
  with Stats. `SERIES_COLORS` has six colours (:1282). The frame has no tick labels and no time cursor.
- **Server `/timeseries`:**
  - **FV:** `handleTimeSeries` (FieldViewerServer.java:1005) handles one `cell` and builds
    `TimeSeriesJobSpec(new String[]{var}, new int[][]{{globalIndex}}, …)` (:1037). The spec **already
    accepts many indices**; only the handler limits it to one.
  - **Chombo and MovingBoundary:** `handleTimeSeriesVtu` (:776) locates the point, then calls
    `getVtuMeshData(...)` for **every** time, fetching the whole array over the remote seam, and picks one
    element. More points in the same loop cost nothing extra.
  - **FEniCSx:** `FenicsBundleViews.timeSeries` (FenicsBundleViews.java:274) locates the point once per
    mesh segment (`VtuGridParser.locateCell` + `vertexWeights`), reads `bundle.field(...)` per row and
    interpolates the P1 values. More points in the same loop are again free.
- **Point location.** `VtuGridParser.locateCell` (VtuGridParser.java:180) scans every cell. That is fine
  for one point and too slow for hundreds of samples × moving-mesh rows.
- **HTTP.** Every route is a GET with a query string (`query()`, :1397), wrapped by `wrap` for 400/404/500
  JSON errors. The executor is `newFixedThreadPool(2)` (:245), so one long kymograph job would take half the
  pool.
- **README drift.** The MovingBoundary note says picking is gated off, but the code enables lab-point picks
  for body-fitted 2D (viewer.js:1118-1131, :1183) and 3D tetrahedra (:1098). P2 fixes the README.

---

## 3. Server API

### 3.1 `/timeseries` with several points (extends the route)

**Request**

```
/timeseries?sim=&job=&domain=&var=<name>&points=x1,y1,z1;x2,y2,z2;…[&snap=nearest]
```

- `points` holds 1 to 64 triples. In 2D, `z` may be left out (`x,y;x,y`); the server fills in the mesh
  plane's `z`, not 0.
- `cell=` and the single `x=&y=[&z=]` keep today's response shape, so nothing breaks.
- `snap=nearest` applies to FEniCSx membrane domains (a line mesh in 2D, a surface in 3D). A click almost
  never lands exactly on those, so this moves the point to the nearest point on the mesh, within one cell
  diameter. Membrane probes are useful before membrane curves (P7) and cost little.

**Response** (a list of series, like `/stats`)

```json
{"name": "u", "domain": "cytosol", "times": [...],
 "location": "cell|point",
 "series": [{"point": [x, y, z], "cell": 123, "volumeIndex": 4567, "inDomain": true,
             "values": [...]}, ...]}
```

A point outside the domain, or outside the mesh at some time, has `inDomain: false` or nulls in `values`.
Nulls are gaps in the plot.

**How each mode answers**

| Mode | Point → sample | Data |
|---|---|---|
| FV Cartesian (`handleTimeSeries`) | `mesh.getCoordinateIndexFromFractionalIndex(mesh.getFractionalCoordinateIndex(wc))` → `getVolumeIndex`, the desktop's `SpatialSelectionVolume.getIndex(0)`. `inDomain` = the index is in the domain's `globalIndices` (a cached `BitSet` per domain). | **One** `TimeSeriesJobSpec(new String[]{var}, new int[][]{inDomainIndices}, …)`, results scattered back to the points |
| Chombo (static mesh) | `locateCell` on the one mesh, once per point | One pass over the times: one `getVtuMeshData` per time, indexed for every point |
| MovingBoundary | `locateCell` per point per time (the mesh is cached in `mbGrid`) | The same single pass; gaps where the boundary has passed the point |
| FEniCSx | `locateCell` + `vertexWeights` per point per mesh segment (moved coordinates for ALE runs) | One `bundle.field` per row for all points |

Refactor: pull the per-point loops of `FenicsBundleViews.timeSeries` and `handleTimeSeriesVtu` into one
helper taking `double[][] points`. The single-point handlers call it with one point.

### 3.2 New `/kymograph`

**Request**

```
/kymograph?sim=&job=&domain=&var=<name>&path=x1,y1,z1;x2,y2,z2[;…]
          [&samples=N][&tstep=k][&raw=1]
```

- `path` is a polyline of 2 to 64 vertices; a straight line is two vertices. Its length must be positive.
- `samples` applies to body-fitted modes only; FV samples are fixed by the voxels crossed. The body-fitted
  default is `clamp(ceil(2·L / h̄), 16, 1000)`, where `h̄` is the mean cell diameter, cached per grid.
- `tstep` is the stride over saved times, mapped onto `TimeSeriesJobSpec`'s `step`. Default 1.
- `raw=1` (FV only) turns off the domain mask, for value-for-value comparison with the desktop.

**Response**

```json
{"name": "C", "domain": "cytosol", "location": "cell|point",
 "sampling": "voxel-crossing|dda|uniform",
 "path": [[x, y, z], ...], "pathLength": 12.3,
 "times": [...], "timeIndices": [0, 2, 4, ...],
 "samples": {"arcLength": [...], "points": [...3n flat...],
             "volumeIndex": [...], "membraneIndex": [...],
             "inDomain": [...]},
 "values": [[...n...], [...n...], ...],
 "range": [min, max]}
```

- `values` has one row per returned time (rows = time, as in `KymographPanel`).
- `volumeIndex` and `membraneIndex` are FV only; `-1` means none.
- `timeIndices` maps the rows back to the viewer's time slider when `tstep > 1`.

**How each mode answers**

| Mode | Sampling | Values |
|---|---|---|
| **FV Cartesian** | Build `new SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(coords)), VariableType.VOLUME, (CartesianMesh) dataManager.getMesh(vcdID)).getIndexSamples(0, 1)`, which gives `getSampledIndexes()`, `getMembraneIndexesInOut()` and `getWorldCoordinateLengths()`. SSHelper has no public getter for its sample points (`meshCoords`): add a one-line getter in vcell-core, or recompute the points from `path` and the arc lengths. If SSHelper throws (it can at a vertex crossing in 3D: "Couldn't adjust for corners", SpatialSelectionVolume.java:444), fall back to a 3D-DDA walk. That is the algorithm of `castRay` (viewer.js:360) ported to Java, with no crossing correction, and the response says `sampling: "dda"`. | **One** `TimeSeriesJobSpec(new String[]{var}, new int[][]{indices}, new int[][]{crossing}, t0, tstep, tEnd, jobId)`, as `KymographPanel` does (:2027). Samples outside the domain become nulls unless `raw=1`. |
| **Chombo** | Uniform samples; `locateCell` once on the static mesh; consecutive samples in the same cell merged into one span, since the data is constant per cell | One pass over the times, one `getVtuMeshData` per time |
| **FEniCSx** | Uniform samples; located, with P1 weights, per mesh segment (per row for ALE runs); outside the domain → null | One `bundle.field` per row |
| **MovingBoundary** (P6) | A **fixed lab-frame (Eulerian) line**: uniform samples, each located in the mesh of every time (the mesh moves); gaps where the moving boundary puts a sample outside the domain at that time | One pass over the times, one `getVtuMeshData` per time, as for multi-point |

A kymograph that follows the material as the boundary moves (a Lagrangian one) can't be built from the saved
data and is out of scope.

**Prerequisite for body-fitted kymographs:** a `VtuGridParser.CellLocator`, a uniform bucket grid of cell
bounding boxes cached per `VtuGrid`. It replaces the linear `locateCell` scan in all three body-fitted modes,
multi-point included.

### 3.3 Limits and errors

| Limit | Value | Behaviour |
|---|---|---|
| Points per request | 64 (the UI caps at 12, for distinct colours) | 400 "at most 64 points" |
| Path vertices | 64 | 400 |
| Samples (body-fitted) | 2,000 | 400 |
| Samples × returned times | 500,000 (about 8 MB of JSON); system property `vcell.fieldViewer.maxKymographValues` | 400 with a suggested `tstep = ceil(n·nt / limit)`. The viewer retries once with that stride and says so. |
| Concurrent heavy jobs (`/kymograph`, multi-point on Chombo or MovingBoundary) | 1, via a `Semaphore`; the pool grows from 2 to 4 threads | 503 "busy" JSON; the viewer retries once |

Also 400:
- a malformed `points` or `path`, or a path of length zero;
- an unknown variable or domain (the existing checks);
- an FV variable that is not a volume variable: "membrane kymographs are not supported yet" (until P7).

A path wholly outside the domain returns **200 with all nulls** and `inDomain` all false. The viewer shows
"the line lies outside <domain>".

---

## 4. Viewer UI

### 4.1 Probes (points)

- **Adding and replacing.** A plain click keeps today's behaviour, now meaning "replace the probes with this
  one". **Shift-click adds** a probe; the existing under-4-px test already tells it apart from a shift-drag
  pan. A **"＋ Add points" toggle** does the same for trackpads and touch screens.
- **Resolution.** A new `pickAt(clientX, clientY)` returns `{point, cell?}` from `castRay`,
  `labPointFromMouse` or `pickTetrahedron`, shared by probes and line vertices. For FV, `castRay` also
  returns the entry distance `t`, so line vertices use the exact surface or cut-face point. Probes keep
  sending the voxel centre.
- **State.** `state.probes = [{id, label: 'P1', point: [x, y, z], color}]`. Probes are lab-frame points, not
  cell ordinals, so they survive a variable or domain switch. A probe that falls outside the new domain gets
  a null series and is listed as "outside <domain>".
- **Probe panel.** A new `#probePanel`, separate from the Stats panel (decision 7), so neither overwrites the
  other. It holds:
  - one `P1 (x, y, z)` row per probe, with a colour swatch, ✕ (remove) and ⌖ (centre the camera on it);
  - Clear and CSV buttons.
- **Fetching.** Any change triggers **one** `/timeseries?points=…` request, debounced by 150 ms, with an
  `AbortController` dropping superseded responses.
- **Plot.** Generalise `renderPlot` into `renderTraces(times, series[])`, keeping its null-gap handling
  (:1255-1273), and add:
  - one shared y scale;
  - tick labels on both axes (about 5 "nice" ticks);
  - axis titles `time (s)` and `<var> [domain]` (decision 6: default units);
  - a vertical **time cursor** at the current time, moved by the slider's `input` event (:1505);
  - clicking the plot moves the time slider to the nearest time.
- **Colours.** `SERIES_COLORS` grows to 12 distinguishable colours. Each probe uses one colour for its trace,
  its list swatch and its 3D marker.

### 4.2 Lines and polylines (kymograph)

- A **Line** tool button. Each click adds a vertex through `pickAt`. **Enter** or a double-click finishes,
  **Backspace** removes the last vertex, **Esc** cancels. Two clicks and Enter give a straight line.
- **In 3D**, two surface picks define a **chord through the interior**. Where it leaves a concave domain it
  shows gaps, which is correct and informative. With a crop active, picks land on the cut face, so the line
  lies in the slice plane, as on the desktop. An optional "snap vertices to the cut plane" checkbox projects
  vertices onto the plane.
- **In 2D**, vertices come from `labPointFromMouse`, or from the voxel entry point for FV.
- The line's row has an **editable coordinate field** (`x,y,z; …`). It makes lines reproducible, and lets you
  type the exact endpoints used on the desktop for the cross-check (§6.3).
- One active line at a time, as on the desktop.

### 4.3 Markers and line in the 3D view (overlay)

- An absolutely positioned `<svg class="overlay">` over the canvas inside `.canvas-box`, with
  `pointer-events: none`.
- `projectToScreen(X)` inverts `rayFromMouse` (viewer.js:392):
  - perspective: `z = (X−P)·fwd`, `xN = (X−P)·right / (z·tan(θ/2)·aspect)`, `yN = (X−P)·up / (z·tan(θ/2))`;
  - 2D orthographic: divide by `parallelScale` instead.
- The camera parameters are cached after every render by a new `render()` wrapper. It replaces the direct
  `renderWindow.render()` calls in orbit, pan, dolly, zoom, crop and time step.
- **Occlusion.** After a camera move (debounced), cast a ray from the camera to each marker with `castRay` or
  `pickTetrahedron`. If the first hit is nearer than the marker, draw the marker hollow at 40 % opacity. The
  line is always drawn, dashed where occluded (tested per segment at the sample points).
- **Why not VTK actors.** `vtkSphereSource` is constructible (probe.html), but point size, line width and
  `renderPointsAsSpheres` are untested in the wasm bundle. The invoker refuses silently (README), and labels
  would need `vtkTextActor`, also untested. VTK markers can be a follow-up after a `probe.html` check.

### 4.4 Kymograph panel

- A new `#kymoPanel` with:
  - a `<canvas>` for the image: x = arc length (µm), y = time (s), one row per returned time, drawn top down
    as in `KymographPanel`;
  - an SVG frame around it with ticks and the titles `distance along line (µm)` and `time (s)`;
  - a colour bar with its min and max;
  - an SVG **line profile at the current time** beneath the image.
- **Drawing.** Build `ImageData` at `W = min(1024, 4·n)` columns. Each pixel column finds its sample by
  binary search over the sample **span midpoints**, so FV voxel spans appear at their true widths. The image
  is scaled with `image-rendering: pixelated`. P1 samples (`location: "point"`) are interpolated linearly
  between samples instead.
- **Colour map.** A JavaScript port of the viewer's `vtkLookupTable`: 256 entries, hue 0.66667 → 0 (set at
  viewer.js:712), saturation 1, value 1, clamped outside the range. Gaps are a neutral hatched grey and the
  legend says "no data". This is deliberately *not* VTK's dark-red NaN colour.
- **Range.** "All (kymograph)" by default; "3D view (current time)", which uses the last `/field` range; or
  "User" min/max inputs. These mirror the desktop's All and User modes.
- **Interaction.**
  - A horizontal cursor marks the current time row and follows the slider.
  - Clicking the image moves the time slider to that row.
  - Hovering shows `d = …, t = …, value`.
  - Shift-clicking the image **adds a probe** at that sample's point. That gives the desktop's crosshair
    time-series plot, through the probe panel.
- **Keeping it current.**
  - A variable or domain change refetches it (one request).
  - When a run in progress adds times (`refreshRun`), the panel shows a "stale – recompute" chip rather than
    refetching every 10 s.
  - A crop doesn't change it: a kymograph is data along a line, not what is on screen.

### 4.5 Export

- **Probes CSV:** comment lines `# sim, job, var, domain`, a header `time,P1(x;y;z),P2(…),…`, then one row
  per time. Gaps are empty fields.
- **Kymograph CSV:**
  1. `kymo-samples.csv`: `i,arcLength,x,y,z,volumeIndex,membraneIndex,inDomain`;
  2. `kymo-matrix.csv`: first row the arc lengths, then one `time, values…` row per time;
  3. optional `kymo-desktop-resampled.csv`: a port of `initStandAloneTimeSeries_private`'s nearest-neighbour
     uniform resampling, for diffing against the desktop's Copy output.
- **PNG** of the kymograph via `canvas.toBlob`.
- All exports use `URL.createObjectURL`: nothing goes over the network.

---

## 5. Data correctness

- **Cell values vs point values.**
  - FV and Chombo values are per cell (constant in each). Probes read the containing voxel, as the desktop
    does, and FV kymograph samples are voxel spans.
  - FEniCSx values are P1 (per vertex). Probes and samples are interpolated (`vertexWeights`), so the
    kymograph and profile are continuous.
  - The response's `location` tells the viewer whether to draw steps or linear segments.
- **Lines through several compartments.** A request carries one variable, which lives in one domain.
  Samples in other domains are gaps (decision 2):
  - FV: masked with the domain's `globalIndices` `BitSet`, because the raw array covers the whole mesh;
  - body-fitted modes: `locateCell < 0`.
- **FV membrane crossings.** With the crossing indices, the two samples at a membrane carry the `_INSIDE` /
  `_OUTSIDE` extrapolated values, as on the desktop. One of the pair usually lies in the other compartment,
  so the mask removes it and keeps the in-domain membrane-adjacent value. `raw=1` keeps both.
- **Membrane variables.**
  - FV `/info` lists only volume variables (FieldViewerServer.java:396), so FV membrane probes and
    kymographs wait until membrane variables are served (P7).
  - FEniCSx membrane variables are listed already: probes use `snap=nearest` (P1). A kymograph needs a curve
    *along* the membrane, because a straight line essentially never lies on a surface or curve mesh. The 2D
    case is P7; 3D surface curves (geodesics) are later.
- **MovingBoundary** (P6). The line is fixed in the lab frame, so a sample's value at each time is the value
  of whichever cell contains that point at that time. Samples outside the moving domain are gaps, so the
  kymograph shows the boundary moving past as the edge of its gaps. Label it "fixed line (lab frame)".
- **2D vs 3D.** A 2D path's `z` is the mesh plane's `z`, filled in by the server when omitted.
- **Picks, as today:**
  - FV probes send the picked voxel's centre, so the voxel is unambiguous;
  - body-fitted 2D probes send the orthographic mouse point;
  - body-fitted 3D probes send the tetrahedron entry point, nudged 1e-3 of the chord inward (viewer.js:1069).

  All of these stay. Line vertices use the exact entry points.
- **Time axis.** Rows are the saved times, not a uniform time scale, as on the desktop; tick labels show the
  real times. With `tstep > 1`, `timeIndices` gives the slider position of each row.

---

## 6. Testing

### 6.1 Java

- **FEniCSx** (`FenicsBundleViewsTest`; fixtures `membrane_efflux.fenics` and `moving_translate.fenics`):
  - `points=` with two triangle centroids and one point outside: each series equals the single-point
    response and the centroid mean (as the existing `timeSeriesInterpolatesAtALabPoint` does); the outside
    series is all null;
  - a kymograph along a disk diameter: arc length runs from 0 to the path length; sample *i* equals
    `/timeseries` at `points[i]`; endpoints outside the disk are null;
  - a kymograph on the ALE fixture: the null band moves along x over time;
  - limits: too many points gives 400; samples × times over the limit gives 400 with the suggested `tstep`.
- **VtuGridParser:** the `CellLocator` agrees with the linear `locateCell` on `fenics-3d-tetra.vtu` and
  `polyhedron-cells.vtu` for random points.
- **FV Cartesian** (new `FieldViewerServerFvTest`). vcell-client has **no FV harness today**, so build one:
  - data: ~~`vcell-core/src/test/resources/simdata/MembraneFrap3D/`, a 3D FV run with membranes~~. P1 found that
    MembraneFrap3D saves **only membrane variables** (`r_PM`, `rf_PM`), so it has nothing to plot here. The harness
    uses the two `N5ExporterTest` runs instead (see P1 below);
  - a `DataSetControllerImpl` over a `Cachetable`, as `N5ExporterTest` does (N5ExporterTest.java:130-136),
    wrapped as `new VCDataManager(() -> controller)`;
  - registered with `FieldViewerServer.register(vcdID, dm, subdomainInfo, name)`;
  - vcell-client cannot see vcell-core's test resources: copy the fixture, or add a `test-jar` dependency.
    P1 decides.

  Tests:
  - several points match the same number of single-cell `/timeseries?cell=` calls;
  - **desktop parity:** build `SpatialSelectionVolume(...).getIndexSamples(0, 1)` and the crossing-index
    `TimeSeriesJobSpec` directly, run them on the controller, and assert that `/kymograph?raw=1` has
    identical indices, arc lengths and values;
  - a 3D diagonal line through voxel corners exercises the DDA fallback.
- **Chombo and MovingBoundary:** there is no local fixture (both run on the server only). Unit-test the shared
  per-point and per-sample loops against a synthetic `VtuGrid` built from the existing `.vtu` fixtures, with a
  fake value supplier that moves the mesh between rows for the MovingBoundary case.

### 6.2 Browser tests (committed, decision 5)

- **Where:** `webapp-viewer/test/`:
  - `requirements.txt` (Playwright, pinned);
  - `conftest.py` starting the fixture server;
  - one test module per feature;
  - a `README.md` saying how to run them.
- **Fixture server:** a small Java main in vcell-client's test sources,
  `org.vcell.client.viz.FieldViewerFixtureServer`. It registers the FEniCSx fixture bundles, the FV runs and a
  stand-in MovingBoundary run (all from P2 on; see P2) with `FieldViewerServer`, points `vcell.fieldViewer.staticDir` at `webapp-viewer/`, and
  prints the port. The Python fixture runs it through Maven's exec plugin, or `java -cp` against the
  test classpath.
- **Browsers:** Chromium (SwiftShader), WebKit and Firefox. Playwright's builds of all three already reproduce
  the README's browser-support table.
- **Checks:**
  - shift-clicking two points on the canvas gives two traces in the probe SVG, two list rows and two overlay
    markers; ✕ removes one;
  - the time cursor follows the slider;
  - the CSV download has the expected columns;
  - the Line tool (two clicks and Enter) gives a kymograph canvas of the expected size, not blank, with a
    time-row cursor, and clicking a row moves the slider;
  - after orbiting, the markers re-project (their screen positions change);
  - **no `is not permitted` in the console.**
- **Running:** `pip install -r webapp-viewer/test/requirements.txt && playwright install` once, then
  `pytest webapp-viewer/test`. CI can run them later; the first increments run them by hand before each merge.

### 6.3 Against the desktop kymograph (by hand, once per release candidate)

1. Open a 2D FV run and the 3D MembraneFrap3D run in the desktop PDE viewer. Draw a line; Plot → Kymograph →
   Copy gives the distances × times matrix.
2. Open "View in 3D", type the same endpoints in the line's coordinate field, and export
   `kymo-desktop-resampled.csv`.
3. Diff the two. In-domain values must agree to floating-point noise. In 3D the desktop's line lies in its
   slice, so use a crop at the same plane.
4. Record the result in the README's verification notes, like the existing "Verified 2026-09-22…" entries.

FEniCSx runs have no desktop kymograph. As an optional check, compare with the FV kymograph of the same model
through vcell-fenics's `cross_validation/` harness; agreement is approximate (relative L2), not exact.

---

## 7. Increments

Order: P1 → P2 (points end to end) → P3 → P4 (FV kymograph end to end) → P5 → P6 → P7.

### PR P1 — `/timeseries` with several points (server) ✅ (#2120)
- The `points=` list in all four modes; FV point → volume index, with the domain mask and one
  `TimeSeriesJobSpec`; the shared per-point loop for Chombo, MovingBoundary and FEniCSx; `snap=nearest` for
  FEniCSx membranes; the limits.
- The FV test harness (fixture and `VCDataManager` wiring).
- **Done when:** the FEniCSx tests and the new FV test pass, and the legacy `cell=` and `x=&y=` responses are
  byte-for-byte unchanged.

Decisions and deviations recorded in P1:
- **FV fixture.** MembraneFrap3D has no volume variables (above), so the harness uses the `N5ExporterTest` runs
  from `vcell-core/src/test/resources/simdata/n5/ezequiel23/`. Both have two compartments and a membrane:
  - `597714292`: 2D, 15 × 15, `Cyt` inside `EC`, volume variable `Dex` in `Cyt`, 5 times;
  - `868220316`: 3D, 5 × 5 × 5, `subdomain0` and `subdomain1`, volume variables `s0`, `s1`, … in `subdomain0`,
    6 times.

  They are **copied**, not shared through a test-jar: six files per run (`.functions .log .mesh .meshmetrics
  .subdomains 00.zip`, 104 KB) in `vcell-client/src/test/resources/org/vcell/client/viz/fv/`. A copy keeps
  vcell-client's tests independent of vcell-core's test packaging and of CI's two-step build. `.log` and `.zip`
  are ignored globally by `.gitignore`, so they are force-added. **P3 and §6.3 should use these two runs**
  where they say MembraneFrap3D.
- **Wiring.** `DataSetControllerImpl` is not a `DataSetController`, so the harness wraps it the way
  `LocalWorkspace` does: `new VCDataManager(() -> new LocalDataSetController(null, impl, null, owner))`, with
  `SubdomainInfo.read(<.subdomains>)` for the domain names. `FieldViewerServerFvTest.registerFvFixtures(root)`
  stages both runs and registers them, for reuse by P2's fixture server.
- **Shared loop.** `PointSeries` (new) holds the points parser, the per-point loop for FEniCSx, Chombo and
  MovingBoundary (`PointSeries.sample` over a `Rows` supplier; one read per row for all points, none when no point
  is inside), and the response writer. The single-point handlers call it with one point.
- **Response details** the plan left open:
  - `cell` is given for FV (its index in `/grid`'s list, -1 outside the domain) and for a body-fitted mesh that is
    the same at every time. It is left out for MovingBoundary and ALE runs, where a point has no single cell.
  - `volumeIndex` is FV only: -1 outside the mesh. A point in the mesh but in another compartment keeps its
    `volumeIndex`, gets `cell: -1`, `inDomain: false` and null values.
  - Body-fitted `inDomain` is true when the point is inside at any time.
  - `snapped: [x, y, z]` appears when `snap=nearest` moved the point (the first mesh it snapped in).
  - FV probes in the same voxel share one index in the job.
- **Snap.** The nearest point on the membrane's line or triangle cells, accepted within the nearest cell's
  diameter. `snap` is ignored for volume domains. Neither FEniCSx fixture has a membrane domain, so snapping is
  tested on hand-built grids (`PointSeriesTest`).
- **2D `z`.** FV takes the plane of the served grid's points; FEniCSx and the VTU modes take the mesh's `z`.
  The legacy `x=&y=` form still defaults `z` to 0, which keeps its response unchanged.
- **Legacy responses.** Golden strings recorded from `master` before the change guard them
  (`FieldViewerServerFvTest.legacyCellResponsesAreUnchanged`, `FenicsBundleViewsTest.legacySinglePointResponsesAreUnchanged`).
- **Not in P1.** The heavy-job semaphore and the larger pool stay in P3, as listed there. Multi-point requests on
  Chombo and MovingBoundary are not throttled yet.
- The server test classes carry `@ResourceLock("fieldViewerServer")`: CI runs Fast test classes concurrently
  and the server is static. `stop()` now also drops the parsed VTU meshes.

### PR P2 — probes in the viewer ✅ (#2121)
- `pickAt`, `state.probes`, shift-click and the Add toggle, `#probePanel` (list, ✕, ⌖, Clear, CSV), the SVG
  overlay with projection and occlusion, `renderTraces` (ticks, titles, time cursor), refetching on a
  variable or domain switch.
- The browser test harness (§6.2): `webapp-viewer/test/` and `FieldViewerFixtureServer`.
- README: fix the stale MovingBoundary note and document probes.
- **Done when:** the probe browser tests pass on Chromium, WebKit and Firefox with no console refusals, in FV
  2D and 3D, FEniCSx 2D and 3D, and MovingBoundary 2D.

Decisions and deviations recorded in P2:
- **Fixtures for the browser tests.** The done-when above needs FV, FEniCSx 3D and MovingBoundary runs, which
  §6.2 had deferred or lacked, so P2 adds them:
  - **FV:** `FieldViewerFixtureServer` registers the two FV runs from P1 now, not from P3.
  - **FEniCSx 3D:** a new bundle, `receptor_3d.fenics` (84 KB). It has three domains: `cyto_dom` and
    `ext_dom` (tetrahedra) and the membrane `mem_dom` (triangles). It was made with vcell-fenics from its
    `cross_validation/receptor_3d` model at `--h 0.35 --t-final 0.2 --output-dt 0.1`, with `provenance/`
    dropped. It also gives `snap=nearest` an end-to-end test (`FenicsBundleViewsTest`).
  - **MovingBoundary:** `FakeMovingBoundaryRun` (vcell-client test sources) is a proxy `DataSetController`.
    It returns a `CartesianMeshMovingBoundary`, which is how the server recognises the mode, and a different
    ASCII `.vtu` at every time: a disk moving along x. The server serves it through the real VTU seam.
    `FieldViewerServerMovingBoundaryTest` covers it end to end in Java.
- **Running the fixture server.** The tests use `java -cp`, not the exec plugin. The classpath comes from
  `mvn -pl vcell-client -am test-compile dependency:build-classpath` into `vcell-client/target/fixture-classpath.txt`.
  `conftest.py` runs that first unless given `--no-build`, and the server prints `FIXTURE {port, datasets}`.
- **Probe points.** An FV probe sends the **vertex mean** of the picked voxel in the served grid, not the
  pick index's lattice centre. P1's `everyServedVoxelCentreMapsBackToItsOwnCell` proves that point maps
  back to the same voxel. Every body-fitted request sends `snap=nearest`: the server ignores it for volume
  domains and the VTU modes.
- **`castRay`** now returns `{cell, t}`, where `t` is the voxel entry, or the cut-plane point on a cut
  voxel. `pickAt` returns that point as `entry`, ready for P4's line vertices.
- **Labels and colours.** A new probe takes the smallest unused number (`P<n>`) and the first unused
  colour, so a label and colour stay with a probe until it is removed. A plain click restarts at P1.
  `SERIES_COLORS` has 12 colours, shared with Stats. Stats still asks for at most 6 variables
  (`STATS_MAX_VARIABLES`).
- **Occlusion.** A marker counts as hidden when the ray from the camera meets the geometry clearly before
  the probe. "Clearly" is one voxel diagonal for FV and 1 % of the scene diagonal for body-fitted meshes.
  There is no cell-identity test, so cell ordinals that go stale after a domain switch don't matter.
- **The plots.** The probe plot is drawn at the SVG's own pixel size, so its tick labels are not stretched.
  The Stats plot keeps its fixed viewBox. The viewer's old single-trace click plot (`renderPlot`) is gone,
  and the viewer no longer sends `cell=` or `x=&y=`. The server keeps both forms.
- **Test results** (run by hand; not in CI yet): 57 browser tests pass on Chromium (SwiftShader), WebKit and
  Firefox (Playwright 1.63's builds) across FV 2D and 3D, FEniCSx 2D (fixed and ALE) and 3D, and MovingBoundary
  2D, with no `is not permitted` refusals and no page errors. The console's `vtkSerializer …
  vtkIntegrationLinearStrategy` errors at startup predate P2 and are not refusals.
- **For P3.** The fixture server already registers both FV runs. Use `868220316` (3D, membranes) for the
  desktop-parity test in place of MembraneFrap3D. It is 5 × 5 × 5 with unit spacing, so a diagonal through
  voxel corners is easy to write down.

### PR P3 — `/kymograph` for FV (server) ✅ (#2122)
- SSHelper sampling with crossing indices, the DDA fallback, `tstep`, `raw`, the value limit, the heavy-job
  semaphore and the larger pool.
- **Done when:** the desktop-parity Java test (§6.1) passes, and a 3D diagonal line through voxel corners
  returns `sampling: "dda"`.

Decisions and deviations recorded in P3:
- **Where it lives.** `FvLineSampler` (new) holds the sampling: the desktop's
  `SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(vertices)), VOLUME, mesh).getIndexSamples(0, 1)`,
  and the DDA fallback. `FieldViewerServer.handleKymograph` runs **one** `TimeSeriesJobSpec` over all samples
  with the crossing indices, as `KymographPanel.initDataManagerVariable` does. The sample points come from a
  new one-line getter, `SSHelper.getSampleCoordinates()` (vcell-core, tested by `SpatialSelectionVolumeTest`).
- **Voxel rule.** Sampling uses the solver mesh's own rule, as P1's probes do. VCell's Cartesian mesh is
  **node-centred**: element `i` sits at `origin + i·extent/(N−1)`, and a point belongs to the element its
  fractional index rounds to, so the two end elements of an axis are half-width. The served grid (`/grid`,
  `CartesianMeshMapping`) instead draws `N` equal boxes of `extent/N`. So a kymograph's voxel spans can differ
  a little from the voxels drawn in the viewer; the kymograph follows the solver (and the desktop). P4 should
  draw the spans from `arcLength`, not from the drawn grid.
- **The DDA fallback** walks that node-centred lattice (Amanatides and Woo), stepping all axes at once where
  the line passes through a voxel edge or vertex, so it never visits the zero-length neighbours. Its samples
  follow the desktop's layout: the first at the path's start, the last at its end, each other one at the
  middle of its voxel's stretch of the path, and two samples at the ends of a path that stays in one voxel.
  Its arc length is measured along the path. It has no membrane-crossing pairs (`membraneIndex` all -1).
  It is used when SSHelper throws, or returns an index outside the mesh or decreasing arc lengths.
- **Arc length.** `arcLength` is the desktop's: accumulated distance between the sample points, so it starts at
  0. On an axis-aligned or 45° line the desktop snaps samples to voxel centres, so the last `arcLength` can be
  shorter than `pathLength` (which is the polyline's own length).
- **Path checks.** Every vertex must lie in the mesh's box (to a relative 1e-6; then it is clamped into it),
  else 400: SSHelper would index outside the mesh. Repeated consecutive vertices are dropped; fewer than two
  distinct vertices is 400 ("zero length"). In 2D, `z` may be left out and is always set to the served grid's
  plane, so arc lengths are in-plane.
- **Variables.** An unknown variable is 400; a membrane or membrane-region variable is 400 "membrane kymographs
  are not supported yet"; any other non-volume type (e.g. a volume-region variable) is 400. `samples=` is
  ignored for FV. A FEniCSx bundle, Chombo or MovingBoundary run gets 400 "… not supported yet" until P5/P6.
- **`tstep`** is `TimeSeriesJobSpec`'s step: rows are saved times `0, k, 2k, …`, so the last saved time is
  returned only when `k` divides `nt − 1`. `timeIndices` gives each row's index in the saved times.
- **The value limit** is samples × returned times (default 500,000; `-Dvcell.fieldViewer.maxKymographValues`).
  Over it is a 400 whose JSON carries `suggestedTstep`: the **smallest** stride that fits, found by stepping up
  from `ceil(n·nt / limit)` (the plan's formula alone can overshoot or undershoot with the `0, k, 2k` rule).
- **Heavy jobs.** A `Semaphore(1)`, `FieldViewerServer.HEAVY_JOBS`: `/kymograph` and a multi-point
  (`points=`) `/timeseries` on Chombo or MovingBoundary. A second one is refused at once with 503
  `{"error", "busy": true}`, not queued. The pool is 4 threads.
- **Response extras.** `samples.cell`: each sample's voxel in `/grid`'s list (-1 outside the domain), so the
  viewer can probe a sample at its voxel's centre (P2's probe point) rather than at a point on a voxel face;
  `"raw": true|false`; `range` is `[0, 0]` when no sample has a value (a line wholly outside
  the domain), as `/field` does.
- **Masking.** All samples, in the domain or not, go into the one job (so `raw=1` is the desktop's job exactly);
  out-of-domain samples are nulled afterwards, using P1's `DomainIndex`.
- **Tests** (`FieldViewerServerKymographTest`): desktop parity with `raw=1` (indices, arc lengths, points,
  membrane indices and every value identical) on an oblique and an axis-aligned line through the membrane of
  the 3D run, and on a straight line and a polyline in the 2D run; the masking; the DDA fallback on the
  diagonal `(0,0,0)→(4,4,4)` (the desktop's sampling throws on it; samples `0, 31, 62, 93, 124`, arc lengths
  `k·√3`, values equal to `/timeseries` at the sample points); `tstep`; the value limit and its suggested
  stride; the 2D `z`; a line wholly outside the domain; the 400s; and the 503. The MovingBoundary test class
  checks the 503 for multi-point requests and the 400 for its kymograph.
- **Fixture limit.** The 3D run saves no membrane-adjacent data, so its `_INSIDE`/`_OUTSIDE` correction returns
  the voxels' own values; the parity test still proves the job is the desktop's, crossing indices included.
- **Cost** (plan §8). SSHelper sampling on MembraneFrap3D's mesh (21³, 1,158 membrane elements), four lines
  including an oblique 3D chord (which SSHelper handled, 59 samples, 4 crossings): under 1.2 ms each once warm.

### PR P4 — kymograph in the viewer (line and polyline) ✅ (#2123)
- The Line tool (vertex clicks, Enter, Esc, Backspace), the coordinate field, the overlay line with a start
  tick, `#kymoPanel` (canvas, JS colour map, colour bar, range modes, profile, cursors, click → time,
  shift-click → probe), CSV and PNG export, the automatic `tstep` retry.
- **Done when:** the kymograph browser tests pass, and the by-hand desktop cross-check (§6.3) matches on the 2D
  and 3D FV runs.

Decisions and deviations recorded in P4:
- **FV only.** The Line tool is enabled for finite-volume runs and disabled (with a tooltip) for the body-fitted
  modes until P5/P6 serve their kymographs. The drawing code is mode-agnostic: body-fitted picks give their
  `point`, FV picks their exact `entry`. The image already interpolates `location: "point"` samples linearly,
  ready for P5.
- **The tool and the field.** **╱ Line** opens the panel with the coordinate field and starts a draft; the
  current line stays until the draft is finished. Finishing (Enter, a double-click, or the 64th vertex) turns
  the tool off. Typing vertices in the field and pressing Enter sets the line directly (so for §6.3: press Line,
  type, Enter). Vertices are rounded to six significant digits when placed, so the line *is* what the field
  shows and a typed copy reproduces it exactly. A double-click's two clicks give one vertex (a repeat of the
  last vertex is dropped). Vertex picks are async; Enter, Backspace and a double-click queue behind them.
- **The "snap vertices to the cut plane" checkbox** (optional in §4.2) is left out: with a crop on, picks
  already land on the cut face.
- **Image.** Gaps are transparent pixels over a CSS hatch behind the canvas, since a hatch drawn into a
  `W × rows` image would be stretched by the pixelated scaling. A column shows the sample whose span holds its
  middle, spans ending halfway between neighbouring samples, so the two samples of a membrane crossing (one
  point) split the gap between their neighbours. Time ticks label up to six rows with their real times. The
  range modes are All (the response's `range`), 3D view (the last `/field` range, redrawn on each time step
  while chosen) and User (min and max inputs, prefilled with the current range).
- **Profile.** Its y scale is the kymograph's own range, fixed across times, so scrubbing moves the curve,
  not the axis. FV draws a step per sample span.
- **Shift-click → probe** probes the sample's voxel centre (the `samples.cell` P3 added, P2's probe point),
  or the sample point outside the domain.
- **Retries.** A 400 with `suggestedTstep` is retried once with that stride, and the note says "every k-th
  saved time …". A 503 is retried once after 600 ms. Otherwise the error shows in the panel and the status.
- **Keeping it current.** A variable switch refetches (debounced 150 ms, so a domain switch that follows is
  included). A run refresh that adds times shows the "stale – recompute" chip. A crop doesn't refetch.
- **Overlay.** The line is drawn in the probes' SVG overlay, white over a dark under-stroke, with a tick across
  its start and a dot per vertex. Occlusion is tested at the middle of 16 pieces per segment with the probes'
  rule, and hidden pieces are dashed. The draft is drawn dotted, without occlusion.
- **Exports.** Four buttons, not one CSV button with an optional third file, since a browser may block several
  downloads from one click: *Samples CSV*, *Matrix CSV* (first cell `time\arcLength`), *Desktop CSV* and *PNG*.
  - The *Desktop CSV* refetches the line with `raw=1`, because the desktop does not mask. It then applies a
    line-for-line port of `initStandAloneTimeSeries_private`'s resampling (`desktopResample`). The layout is
    `Distances,…`, `Times`, then one `time,values…` row per time. That is the desktop's Copy layout with commas;
    the numbers print as JS prints them, so the comparison is numeric, not textual.
  - The PNG is the image alone, each row and column scaled up by whole pixels (to at least 512 × 256), with
    gaps transparent.
- **§6.3 cross-check, automated.** The desktop GUI can't be driven from a test, so the check has two halves:
  - `KymographDesktopResampleTest` (vcell-client) builds the desktop's `SpatialSelectionVolume`, its
    crossing-index `TimeSeriesJobSpec` and `KymographPanel`'s resampling (copied) for four lines: a straight
    line and a polyline in the 2D run, an oblique and an axis-aligned line through the membrane in the 3D run.
    It checks them against golden files, `vcell-client/src/test/resources/org/vcell/client/viz/kymo/*.csv`.
  - `test_kymograph.py::test_the_desktop_csv_is_what_the_desktop_shows` types the same four lines in the
    viewer and requires its *Desktop CSV* to equal those files **exactly** (the same doubles).
  - Result: identical, all four lines, in all three engines. Recorded in the README ("Verified 2026-09-29 against
    the desktop kymograph").
- **Tests.** `webapp-viewer/test/test_kymograph.py`, 17 tests per engine:
  - Line tool: two clicks and Enter, in 2D and 3D. The canvas is `min(1024, 4n) × times`, not blank, with the
    cursor on the current row, a profile and the overlay line.
  - A row click moves the slider and the cursor; shift-click on the image adds a probe.
  - Backspace, Esc and a double-click.
  - The three CSVs and the PNG signature.
  - The desktop cross-check, four cases.
  - A line outside the domain.
  - The stride retry and the busy retry, faked with `page.route`.
  - A variable switch refetches; the line re-projects after an orbit; the tool is disabled on a FEniCSx run.
  - **Results: 108 browser tests pass** (probes and kymographs) on Chromium (SwiftShader), WebKit and Firefox,
    with no `is not permitted` refusals and no page errors. All 48 `org.vcell.client.viz` Java tests pass.
- **For P5.**
  - Server: `handleKymograph` answers a FEniCSx bundle or a VTU run with 400 today; replace that branch, and
    keep the body-fitted job inside `heavy(…)`.
  - Viewer: enable the tool where the startup sets `el.lineTool.disabled = state.bodyFitted`.
  - Viewer: hide *Desktop CSV* for body-fitted runs, since `raw=1` and the desktop's resampling are FV only.
  - Already ready: the image interpolates `location: "point"` samples; line occlusion uses `pickTetrahedron`
    on body-fitted meshes; shift-click falls back to the sample point when there is no `samples.cell`.

### PR P5 — kymographs for FEniCSx and Chombo
- `CellLocator`, which also speeds up the P1 paths; uniform sampling; P1 interpolation (FEniCSx) and cell
  spans (Chombo); the default sample-count rule.
- The UI enabled for these modes: FEniCSx 2D and 3D, and **Chombo 2D**. Chombo 3D has no picker until P6.
- **Done when:** the FEniCSx kymograph tests pass (including the ALE fixture), the locator agrees with the
  linear scan, and a 3D FEniCSx line through a two-domain bundle shows gaps in the other domain.

### PR P6 — MovingBoundary kymograph, and picking on Chombo 3D
- A fixed lab-frame kymograph for MovingBoundary (§3.2, §5) within the value limit, labelled as such, with
  the UI enabled for it.
- A general ray pick through voxels and polyhedra, so Chombo 3D can place probes and line vertices.
- **Done when:**
  - the synthetic moving-mesh test (§6.1) passes;
  - a server-side MovingBoundary run checked by hand shows gaps that agree with `/timeseries` at the same
    points;
  - Chombo 3D probes and lines work in the browser tests on a Chombo fixture, if a small one can be made
    (otherwise by hand).

### PR P7 — membrane curves (decision 1)
- FEniCSx 2D membrane arcs: the shortest path along the line mesh between two snapped picks, sampled at the
  mesh vertices.
- FV membrane variables in `/info`, and FV membrane kymographs through `SpatialSelectionMembrane`-style
  sampling.
- **Done when:** a membrane arc's length equals the sum of its edge lengths, and its values equal the P1 vertex
  values; FV membrane kymographs match the desktop's on MembraneFrap3D.

### Later
- **Several variables** in the point plot: the desktop's "Y Axis" multi-select, `var=a,b` over the same
  points. The server change is small, because `TimeSeriesJobSpec` already takes `indices[v][i]`.
- **Units** reported by the server (decision 6): `/info` gains the model's length, time and per-variable
  units where known (the desktop has them through `DataSymbolMetadataResolver`), with µm and s as defaults.
- A **binary response** (Float32) for large kymographs, if JSON becomes the bottleneck
  (3d-renderer-design.md, "Expect the wire format…").
- VTK markers in place of the SVG overlay, after a `probe.html` capability check.
- 3D membrane curves (geodesics on surface meshes).

---

## 8. Risks

- **SSHelper in 3D.** It was written for lines in the slice plane and can throw at vertex crossings
  (SpatialSelectionVolume.java:444, :449). Its membrane loop is linear in the number of membrane elements per
  crossing face, which may be slow on large 3D meshes. The DDA fallback covers the throw but loses the
  membrane-crossing correction. Measure the cost on MembraneFrap3D in P3.
- **Payload size.** 500,000 values is still several MB of JSON. Long runs at full resolution may need the
  binary format (Later).
- **Remote seam cost for Chombo and MovingBoundary.** Every time step ships the whole cell array over the
  remote seam (`getVtuMeshData`), as today's single point already does. Several points make this cheaper per
  curve. A MovingBoundary kymograph pays it once per time too, which is why it shares the heavy-job semaphore.
- **Overlay drift.** The projection must match the camera VTK renders with exactly. Check it against a known
  point, such as a cube-axes corner, in all three browsers, in perspective and in 2D orthographic views.
- **MovingBoundary picks and lines** come from the geometry of the time being shown. A probe placed at one
  time may be outside the domain at others; that is shown as gaps, which is the intended meaning.

## Critical files

- `webapp-viewer/viewer.js`, `webapp-viewer/index.html`, `webapp-viewer/README.md`
- `vcell-client/src/main/java/org/vcell/client/viz/FieldViewerServer.java`
- `vcell-client/src/main/java/org/vcell/client/viz/FenicsBundleViews.java`
- `vcell-client/src/main/java/org/vcell/client/viz/VtuGridParser.java`
- `vcell-core/src/main/java/cbit/vcell/simdata/SpatialSelectionVolume.java` (reused), with
  `vcell-client/src/main/java/cbit/vcell/client/data/KymographPanel.java` (the reference) and
  `vcell-client/src/test/java/org/vcell/client/viz/FenicsBundleViewsTest.java` (tests to extend)
