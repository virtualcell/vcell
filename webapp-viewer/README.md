# VCell field viewer (standalone)

A browser viewer for VCell finite-volume fields. The desktop client serves this page from its own
loopback field server and opens a browser at it; page and data then share one origin.

Design record: [`../docs/3d-renderer-design.md`](../docs/3d-renderer-design.md).

## Deliberately framework-free, and with no build step

The whole app is `index.html` plus `viewer.js`. There is no bundler, no framework and no
`node_modules` — **this directory is the deployable**. The only build-time action is fetching the
wasm bundle, which is a download, not a compile.

It began as a component inside `webapp-ng`, which turned out to be a poor fit:

- It used no Angular features of substance — no dependency injection, services, `HttpClient`, router
  or forms. Just a template and two lifecycle hooks.
- It dragged ~5.5 MB of application shell along to render one canvas.
- The Angular `index.html` pulls Bootstrap, the Auth0 theme and Google Fonts from CDNs, which is
  wrong for a viewer whose purpose is looking at a local simulation, possibly offline.
- It put the ~12 MB wasm bundle on the webapp's build and deploy path for a feature that is off by
  default.

Standalone, none of that applies: the page loads from the client's own server and contacts nothing
else. Verified — the only origin it talks to is the loopback server.

## Running it

```bash
npm run fetch:vtk-wasm     # downloads the pinned wasm bundle into assets/ (gitignored)
npm run serve              # fetch + a static server on :4400
```

Then open `http://localhost:4400/?sim=<simulationKey>&job=<n>`, or just use **View in 3D** in the
desktop client, which starts its field server and opens the right URL for you.

Query parameters:

| parameter | meaning |
|---|---|
| `sim`, `job` | the dataset to view (required) |
| `var`, `domain`, `time` | initial selection; the viewer then drives itself from `/info` |
| `base` | data origin, if not this page's own; **loopback only** |

## Browser support

The vtk.wasm bundle needs **WebAssembly JSPI** (JavaScript Promise Integration: `WebAssembly.Suspending`,
`WebAssembly.promising`), and rendering needs **WebGL 2**. VTK links its WebAssembly module with `-sJSPI=1`
whenever the WebGPU renderer is built in, which the stock all-modules bundle does, even though this viewer
draws with WebGL 2.

| browser | runs the viewer |
|---|---|
| Safari 26 (WebKit 26.6 checked) | yes |
| Safari 18 and earlier (WebKit 18.2 checked; Safari 18.6 reported) | no |
| Chrome / Edge 137 and later (Chromium 153 checked) | yes |
| Firefox 153 and 155 (checked) | yes |
| Firefox 132 (checked) | no |

A browser without JSPI or WebGL 2 gets a message saying what it lacks and which browsers work, with the page's
address to copy into one (`missingBrowserSupport` in `viewer.js`), rather than the loader's own "undefined is
not a constructor (evaluating 'new WebAssembly.Suspending(…)')". Checked with Playwright's WebKit, Firefox and
Chromium builds (current and a year old) and the installed Firefox. A bundle for older browsers would be a
second build without JSPI, and therefore without WebGPU, in virtualcell/vcell-vtk-wasm.

## What it does

Runs VCell's finite-volume pipeline client-side: build the whole-voxel unstructured grid in memory
from the server's arrays → `vtkGeometryFilter` → `vtkWindowedSincPolyDataFilter` → render via WebGL2,
using the custom bundle from [virtualcell/vcell-vtk-wasm](https://github.com/virtualcell/vcell-vtk-wasm).
The client-side smoothing is bit-identical to the pyvcell/VisIt reference.

Geometry and field values come from separate endpoints, because the geometry does not change as you
scrub time — a time step costs about 5.7× less than shipping both.

## Notes for anyone editing this

- **Load the UMD loader, not the ESM entry.** The ESM runtime does a dynamic `import()` of the
  in-browser untar'd glue; bundlers rewrite that and it breaks. `vendor/vtk.umd.js` is vendored so
  nothing processes it.
- **`vtkRenderWindowInteractor` does not work here** and is deliberately unused. It binds DOM
  listeners and accepts a trackball style, but `startEventLoop` exists only on `vtkRemoteSession`,
  so nothing pumps it — it looks wired while being inert. Camera control is driven directly.
- **`vtkDoubleArray.setValue()` is not in the marshalling invoker whitelist**; use `setTuple1()`.
  Likewise prefer `mapper.scalarVisibilityOn()` over the boolean setter.
- **Turn off the canvas takeover** with `_setDefaultExpandVTKCanvasToContainer(false)` and
  `_setDefaultInstallHTMLResizeObserver(false)`, or vtk stamps the canvas to fill the page and pins
  the drawing buffer to its 300×150 default.
- Size the drawing buffer from the **box**, not the canvas: the canvas is still at its default until
  vtk takes it over.
- **`vtkObjectManager` logs refusals, it does not throw.** A call can appear to succeed in JS while
  doing nothing at all — watch the console for `is not permitted`. This makes capability probes
  actively misleading unless they read the console.
- **`renderer.addActor2D` is refused; `renderer.addViewProp` is not.** That is the route for 2D
  props such as `vtkScalarBarActor`.
- **`VTK_POLYHEDRON` (type 42) cells work, through the five-argument overload**
  `insertNextCell(42, numPoints, points, numFaces, faces)` — where `faces` is
  `[numPoints, ids…, numPoints, ids…]` with no leading face count. Call
  `initializeFacesRepresentation(0)` once before the first one. Both `vtkGeometryFilter` and
  `vtkTableBasedClipDataSet` handle the result, so a polyhedral grid renders and crops. Passing a
  face stream as the point list instead throws `std::bad_alloc`. Chombo sends its cut cells this
  way so that a face shared with a neighbouring voxel stays shared (#1895); note that VTK's clip of
  a polyhedral cell integrates ~0.4% low against the exact half-volume, where a tetrahedral mesh of
  the same cells is exact.
- **The mesh IS the convention: one deformed grid, everything derives from it.** VCell's FV
  display convention smooths the boundary VERTICES OF THE VOLUME MESH (raw boundary →
  `vtkGeometryFilter` with `passThroughPointIds` → windowed sinc → `vtkVCellDeformGridToSurface`,
  our custom write-back filter in the bundle, ≥ v1.2.0), so boundary cells are distorted
  hexahedra reaching the smoothed surface. The shell is that mesh's boundary; the cut plane is
  `vtkTableBasedClipDataSet` on that mesh — the cut face is flat and sharp, its rim lies on the
  smoothed surface *because the cells reach it*, and every cut polygon carries its cell's value.
  No cap, no trim, no GPU clipping planes, one actor. (History, so nobody re-walks it: a GPU-clip
  + raw-grid-cap + implicit-distance-trim approach was built first and rejected in review — the
  trim's per-cell chords can't follow the smoothed silhouette at nominal smoothing. When judging
  a crop from a screenshot, orbit toward the REMOVED side; the kept hemisphere looks whole from
  its own side by definition.)
- **Two statistics families, both documented, neither hidden.** *Solver-grid* (`/stats`): uniform
  voxel volumes, the solver's own bookkeeping, fast, reader-side; min/max exact; means carry the
  known full-voxel distortion near membranes. *Display-mesh* (`vtkIntegrateAttributes` with
  `divideAllCellDataByVolumeOn` over the deformed, possibly clipped mesh): self-consistent with
  what is rendered; means carry the deformation convention instead. The two means converge as h→0
  and their difference is a boundary-resolution diagnostic; min/max are identical by construction.
  Label every displayed number with its family and, for display-mesh, the smoothing parameters.
- **Picking is plain JS, not a VTK picker.** The browser holds the whole grid and field, so the
  hover readout is an occupancy map + 3D-DDA ray walk over the Cartesian lattice — no round trip,
  no registry dependence. The walk applies the same crop keep-rule as the renderer, so picking the
  cut face reads the cap voxel. A click places a probe at the picked voxel's centre (see **Probes**), and
  the probes' time courses come from one `/timeseries?points=` request, reduced server-side next to the
  reader — never fetch every timestep to build one curve (a kymograph likewise comes from one
  `/kymograph` request, see **Kymographs**). The Stats button
  does the same for whole-domain min/mean/max per variable via `/stats` (one space-stats
  `TimeSeriesJobSpec` carrying all the variables at once).
- **2D runs are first-class, through the same convention.** The server emits the mesh's
  `VisPolygon` quads (`cellType` 9, `dimension: 2` in `/grid`) — reading `getVisVoxels()` on a 2D
  mesh returns null (thrift optional), which is how 2D used to 500. The identical deform pipeline
  smooths the domain OUTLINE: with `boundarySmoothingOn`, a flat regular interior is already a
  Laplacian fixed point, so only the boundary polyline relaxes. The camera becomes top-down
  parallel projection (drag pans, wheel scales — `dolly` does nothing under an ortho camera), the
  slice row is hidden, and picking switches to a parallel-projection ray. 1D runs get a clear
  dialog in the desktop client instead of a broken page.
- **MovingBoundary (mbsolver) runs are a third mode: body-fitted, time-varying geometry.** The
  server detects the run from its mesh type and switches to the VTU seam (`getVtuTimes` /
  `getEmptyVtuMeshFiles(timeIndex)` / `getVtuMeshData`), parsing the Python VTK service's
  binary-uncompressed `.vtu` into the same JSON contract (`bodyFitted: true`, `cellType` 7 cut
  polygons, `geometryId` carrying the time index). The viewer bypasses the whole smooth/deform
  chain — the mesh IS the solver's geometry — and re-fetches `/grid` on every time step, keeping
  the camera where the user put it. Smoothing is off, since the mesh is shown as the solver computed
  it. `/stats` integrates each time over that time's own mesh (`"weighting": "measure"`). Probes are
  lab-frame points, not cell ordinals, because the ordinals change from one mesh to the next: the
  server locates each probe in every time's mesh, and a time where the boundary has passed the point
  is a gap in its trace.
- **FEniCSx results bundles are the fourth mode: body-fitted, POINT data.** The server serves a
  FEniCSx run from its results bundle (`FenicsBundleViews`, vcell-fenics ADR 010) in the same
  contract with `bodyFitted: true`, and `/field` says `"location": "point"`: one value per mesh
  vertex (a P1 finite-element solution). The viewer puts those on the grid's point data and switches
  the mapper to `setScalarModeToUsePointData`, so each cell is interpolated (Gouraud) instead of
  flat. `dimension` is the embedding dimension, so a membrane of a 3D model is drawn in 3D.
  `/stats` returns the solver's own integrals (`"weighting": "integral"`, plus `total` and
  `measure`). `/timeseries` interpolates the P1 values at a lab-frame point. The crop's display-mesh
  statistics read the clipped point data. Verified 2026-09-22 in headless Chrome (SwiftShader) on
  a 2D disk and a 3D two-domain bundle: render, time scrub, domain switch, crop statistics and the
  stats plot, with no `is not permitted` refusals.
- **A run still in progress.** For a FEniCSx run, `/info` reports the bundle's `status` (`running`,
  `completed`, `failed`) and `progress` (0..1). While the status is `running` the viewer re-reads
  `/info` every 10 s (paused while the tab is hidden, never overlapping a render), and a **Refresh**
  button does the same on demand for any run. New output times extend the slider. If the view was on
  the last time it follows the run to the new last time; otherwise it stays where the user put it.
  Variable, camera, slice and mesh style are kept. Polling stops once the run completes or fails. The
  server re-reads the bundle's manifest on every request (`BundleStore.cached` never caches
  `.zattrs`), so a newly written row shows up at once, locally or remotely. Checked headlessly
  against a bundle grown from 3 to 13 rows while the page was open.
- **Mouse, in 3D:** drag orbits, shift-drag / right-drag / middle-drag pans (camera and focal point
  slide together across the view plane, scaled so the scene at the focal distance follows the
  cursor), the wheel dollies, and a left click without movement picks. In 2D, any drag pans.
  The orbit turns about a pivot — the scene center — that a pan does not move, so a panned object
  still rotates in place (ParaView's behavior) rather than about the middle of the screen; the orbit
  is computed in JS (azimuth about the view up, then elevation about the right axis, centered on the
  pivot) and matches vtkCamera azimuth/elevation pixel for pixel when nothing has been panned.
- **Picking on a 3D body-fitted mesh** casts the mouse ray through the tetrahedra in JS
  (`pickTetrahedron`: clip the ray against each tet's four faces, and against the cut's half-space
  for the smooth cut, or over the kept cells only for the whole-cells cut) and takes the nearest
  entry: the point on the surface, or on the cut face, under the mouse. Hover reads the P1 value
  there; a click places a probe there, a fixed lab-frame point (gaps where the moving boundary has passed
  it), as the 2D pick does. Server-side, locating the point in each row's mesh
  had copied the whole point array per tetrahedron face (5 s for 25 rows of a 40k-tet mesh); it now
  takes ~40 ms.
- **Probes: time courses at several points.** A click puts a probe where it lands and replaces the
  others; **shift-click**, or a click while **＋ Add points** is pressed (for trackpads and touch screens),
  adds one, up to 12. A probe is a lab-frame point: the picked voxel's centre for finite volume, the
  point under the 2D camera, or the tetrahedron entry point in 3D (`pickAt`). Probes therefore carry over
  a variable or domain switch and a run refresh, and are refetched after either. A probe outside the new
  domain is listed as "outside <domain>". On a body-fitted run the request asks `snap=nearest`, so a click
  beside a FEniCSx membrane lands on it. All the probes are fetched in **one** request
  (`/timeseries?points=x,y,z;…`), debounced by 150 ms, with a superseded request aborted.
  - The **probe panel** is separate from Stats. It lists `P1 (x, y, z)` with the value at the current time,
    ⌖ (centre the view on it) and ✕ (remove), plus Clear and CSV buttons.
  - The **plot** has one trace per probe on one shared scale, ticked axes titled `time (s)` and
    `<var> [domain]` (VCell's default units), and a time cursor that follows the slider. Clicking the plot
    moves the slider to the nearest saved time. Nulls are gaps.
  - **CSV** is made in the page: comment lines `# sim / job / var / domain`, a header
    `time,P1(x;y;z),…`, then one row per time, with gaps as empty fields.
  - **Markers** are an SVG overlay over the canvas, not VTK actors. `render()` wraps every
    `renderWindow.render()`: after each frame it caches the camera it drew with and re-projects the
    markers from it (`projectToScreen`, the inverse of `rayFromMouse`). A marker's colour matches its trace.
    After a camera move, debounced, a ray is cast from the camera to each probe (`castRay` or
    `pickTetrahedron`, honoring the crop). A probe the geometry hides is drawn hollow at 40 % opacity.
  - Checked by the committed browser tests (`test/`, see its README) in Chromium, WebKit and Firefox:
    - FV 2D and 3D, FEniCSx 2D (fixed and moving) and 3D, and stand-in MovingBoundary and Chombo runs;
    - markers land within 1.5 px of the clicked point on the body-fitted runs;
    - no `is not permitted` refusals.
- **Kymographs: a variable along a line, over time**, for finite volume, FEniCSx (2D, 3D, moving meshes) and
  Chombo 2D. MovingBoundary follows (plan P6), as does Chombo 3D, which first needs a picker for its voxels and
  polyhedra; the Line tool says why it is off there.
  - **Drawing.** **╱ Line** starts a line: each click on the view adds a vertex, at the exact surface or
    cut-face point under the mouse (`pickAt`'s `entry`). **Enter** or a double-click finishes, **Backspace**
    removes the last vertex, **Esc** cancels. In 3D two surface picks make a chord through the inside; with a
    crop on, picks land on the cut face, so the line lies in the slice, as on the desktop. The line's field
    (`x,y,z; x,y,z; …`, µm, six significant digits) shows the vertices and takes typed ones (Enter redraws), so
    a line can be reproduced exactly. One line at a time. It is a lab-frame polyline, so it carries over a
    variable or domain switch (refetched). ✕ removes it.
  - **Sampling (the FV rule).** The server samples the line exactly as the desktop kymograph does
    (`SpatialSelectionVolume.getIndexSamples`, see `FvLineSampler`): one sample per voxel crossed, in the
    solver's node-centred voxels, and **two samples at each membrane crossing**, one per side, carrying the
    `_INSIDE`/`_OUTSIDE` membrane values. All samples × times come from one server-side `TimeSeriesJobSpec`,
    so only the kymograph's values travel. A 3D line the desktop's code can't take (one through voxel
    vertices) is walked voxel by voxel instead, without the membrane correction, and the panel says so.
  - **Sampling (body-fitted: FEniCSx, Chombo).** The line is sampled **evenly**: by default
    `clamp(ceil(2·L / h̄), 16, 1000)` samples, two per mean cell diameter `h̄` of the mesh, at most 2,000 with
    `samples=`. The server locates each sample in the mesh (a bucket grid of cell bounding boxes,
    `VtuGridParser.CellLocator`, built once per mesh) and reads each saved time once for all samples, the loop
    the multi-point `/timeseries` uses. A FEniCSx sample is interpolated from the P1 vertex values of the cell
    holding it, so the image and profile are continuous (linear between samples); a Chombo sample takes its
    cell's value, drawn as a step per sample. There is no *Desktop CSV* for these runs: the desktop has no
    kymograph of them.
  - **A moving mesh** (a FEniCSx ALE run; MovingBoundary in P6): the line stays where it was drawn, a **fixed
    line in the lab frame** (an Eulerian line), and each sample reads whichever cell holds its point at that
    time. The moving boundary therefore shows as the edge of the gaps, moving across the image. The title and
    note say "fixed line (lab frame)". A line that follows the material can't be built from the saved data.
  - **Gaps.** Samples outside the variable's compartment are **gaps**, drawn as a grey hatch and listed as
    "no data" by the colour bar (the desktop shows the raw array there, i.e. another compartment's numbers). A
    line wholly outside says "the line lies outside <domain>". On a body-fitted run the other compartment is
    another mesh, so a 3D line through a ball in a box is a gap in the ball for the box's variable, and the
    other way round. A FEniCSx membrane domain has no kymograph yet (a straight line almost never lies on it;
    curves along a membrane are plan P7).
  - **The image** has one row per saved time, the first at the top as on the desktop, and arc length across.
    It is `min(1024, 4·n)` columns, scaled up with `image-rendering: pixelated`. Each column shows the sample
    whose span holds it, a span ending halfway to each neighbouring sample, so voxels keep their true widths
    (the desktop instead resamples to evenly spaced distances). The colour map is a JS port of the 3D view's
    `vtkLookupTable` (blue low, red high). The range is the kymograph's own (default), the 3D view's at the
    current time, or typed. Rows are the saved times, labelled with their real times.
  - **Interaction.** A dashed cursor marks the slider's time and follows it; a click on the image moves the
    slider to that row's time; hovering reads `d, t, value`; **shift-click adds a probe** at that sample (its
    voxel's centre for finite volume, the sample's own point otherwise), for its time course in the probe panel. Below, the line profile at the current time (a
    step per voxel, jumps at membranes).
  - **Limits.** Over the server's 500,000 samples × times the viewer retries once with the stride the server
    suggests (every k-th saved time) and says so; a busy server (another kymograph running) is retried once. A
    run in progress doesn't refetch it every 10 s: new times show a "stale – recompute" chip.
  - **Over the view** the line is an SVG polyline in the probes' overlay: a tick across its start (distance 0),
    a dot per vertex, dashed where the geometry hides it (tested in 16 pieces per segment).
  - **Export**, made in the page: *Samples CSV* (`i,arcLength,x,y,z,volumeIndex,membraneIndex,inDomain`),
    *Matrix CSV* (the arc lengths, then `time,values…` per time; gaps empty), *Desktop CSV* (the raw values,
    `raw=1`, resampled exactly as `KymographPanel` resamples them for display and Copy: `Distances,…` then one
    row per time) and *PNG* (the image, scaled up by whole pixels, gaps transparent).
  - **Verified 2026-09-29 against the desktop kymograph** (plan §6.3), on the 2D and 3D FV fixture runs, two
    lines each (straight, oblique, a polyline). The desktop GUI can't be driven from a test, so
    `KymographDesktopResampleTest` builds what the desktop builds (its `SpatialSelectionVolume`, its crossing
    `TimeSeriesJobSpec`, and `KymographPanel`'s resampling, copied) and checks golden files; the browser test
    types the same lines and compares the viewer's *Desktop CSV* with them: identical, value for value, in
    Chromium, WebKit and Firefox.
- `probe.html` is a scratch page for exactly these capability probes: point it at a suspect class,
  read the on-page result, and keep the console open for `is not permitted`.
- **The scalar bar labels the lookup table's range, not the mapper's.** `mapper.setScalarRange`
  alone never reaches the bar — it would label [0,1] under a correctly-colored surface. The viewer
  therefore owns one `vtkLookupTable` (hue range flipped to blue-low→red-high, the desktop
  convention) shared by mapper and bar, with `useLookupTableScalarRangeOn()` so the table's range
  drives both. Verified with a monotonic ramp field: surface sweep and bar direction agree.
- Probed 2026-08-07 and available through the standalone session: `vtkScalarBarActor`,
  `vtkCubeAxesActor` (incl. `setBounds`/`setXTitle`/`setCamera`), `vtkPlane` + `vtkCutter`
  (`setCutFunction`), `vtkColorTransferFunction`, `vtkLookupTable`, `vtkCellPicker` (incl. `pick`).
  **`vtkOutlineFilter` has no registered constructor** — compiled into the `.wasm`, but absent from
  the deserializer, which is the distinction that matters.
- **The Mesh selector** (Surface / Surface + edges / Wireframe; the `m` key cycles it) sets one
  `vtkProperty` on the display actor — `setRepresentation` (1 wireframe, 2 surface) and
  `setEdgeVisibility` — so it holds across time, variable and slice changes. For a 3D mesh the edges
  are those of the boundary surface and of the slice's cut face (the display is the grid's surface).
  Checked headlessly on a 2D FEniCSx bundle (triangle edges over the field) and a 3D one (wireframe).
- **The cut mode** (next to the slice slider). *Cut through cells* (the default) clips the mesh at the
  plane (`vtkTableBasedClipDataSet`): a flat cross-section of the field, but its polygons are what the
  plane slices out of each cell — arbitrary triangles and quadrilaterals, often long and thin even in a
  good mesh, so with edges on the cut face looks like a poor mesh when it is not. *Whole cells* keeps
  every cell with a vertex on the kept side (`vtkExtractCells`, cell ranges chosen in JS from the grid's
  coordinates), so the cut face is a staircase of the mesh's own faces — the view for judging element
  size and shape. The crop statistics are always those of the exact clip. Checked headlessly on the 3D
  Furrow bundle at t = 0 and on a remeshed segment at t = 12.
