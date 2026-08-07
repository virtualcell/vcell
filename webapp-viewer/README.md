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
- **The cut plane CROPS the volume; the slice caps the exposed face.** The surface mapper carries
  a GPU clipping plane (`mapper.addClippingPlane`) that discards the half above the cut — this
  works in the wasm build even though WebGL2 has no `gl_ClipDistance`, because VTK emulates
  clipping in-shader. The cap is `vtkCutter` output from the raw grid: cell data passes through,
  so each cut polygon carries its source voxel's exact value. Axis-aligned only, by design — an
  interactive plane widget needs interactor infrastructure the standalone session lacks. When
  judging whether a crop "worked" from a screenshot, orbit toward the REMOVED side: the kept
  hemisphere looks whole from its own side by definition.
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
