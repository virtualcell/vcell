# VCell 3D Field Visualization — Design & Decision Record

**Status: exploratory.** Nothing described here is committed to a roadmap. The code that exists is
behind `vcell.fieldViewer.enabled`, which defaults to false, so none of it is reachable in a normal
client. This document records what has been measured, what has been decided, and what is still open,
so the reasoning survives whether or not the approach ships.

## Scope, and why this is a separate document

[`salad-3d-renderer-design.md`](salad-3d-renderer-design.md) is the design record for the
**SpringSaLaD spatiotemporal trajectory viewer** — a particle/glyph movie player that lives in
Swing, inside the desktop client. That work shipped (phases 0, 1 and 1.5).

Its §8 and §8B grew a second subject: visualizing **PDE finite-volume fields**, which is a different
workload with different data, a different renderer and a different host. That subject has outgrown a
document titled after the SaLaD viewer, so it continues here. The SaLaD document remains the
authority on the trajectory viewer and on how the field-viz question originally arose; this one
carries the field-visualization design forward.

## What has been measured

These are the results that constrain the design. Each was produced by a working prototype, not by
argument.

- **The finite-volume pipeline runs faithfully client-side.** `vtkWindowedSincPolyDataFilter` in a
  browser, through a custom VTK-compiled-to-WebAssembly bundle, reproduces the pyvcell/VisIt
  reference **bit-identically** on the golden fixture (max deviation 0.0). Client-side smoothing is
  not an approximation of what VCell already produces.
- **Python is not required on the desktop.** A pure-Java server reproduces the pyvcell export
  bit-identically — same connectivity across all 4718 cells, field values agreeing to 0.0.
- **Geometry can be built without the simulation's files.** The visualization mesh can be constructed
  in memory from the solver mesh a `DataSetController` returns, plus the domain naming from the math
  description. Verified identical to the file reader across all three domains of a test run
  (ec 122351 points, cytosol 16306, Nucleus 3959).
- **Interaction must be hand-driven.** `vtkRenderWindowInteractor` cannot work in a standalone
  wasm session: it binds DOM listeners and accepts a trackball style, but `startEventLoop` exists
  only on `vtkRemoteSession`, so nothing ever pumps it. It looks correctly wired while being inert.
- **Smoothing is resolution-bound, not parameter-bound.** On a 51×51×19 mesh the cell's mean local
  thickness is ~2.3 voxels, so the one-voxel staircase and the anatomy are the same spatial
  frequency. No parameter choice removes the staircase without eroding thin processes.
- **Client weight, not bandwidth, separates the candidate approaches.** See
  [the geometry-server spike](#the-geometry-server-spike) below.

## The problem splits into two modes

Different constituencies, which could reasonably use different machinery. **Neither is committed.**

### Mode A — the desktop client's viewer, local *and* remote

The PDE results panel opens a viewer regardless of where the simulation is stored, and the viewer
sees one data contract independent of storage location.

**Implemented.** Four of the five data calls already worked against remote simulations unchanged;
only mesh construction was file-based, and that is now done in memory (see *What has been
measured*). Data access is a **registry**, not a single injected source: several spatial results
windows open at once is normal — comparing runs side by side — so one server keyed by simulation and
job serves them all, and each request names its own dataset.

Keying by dataset rather than by window means opening the same results twice re-registers an
equivalent source, so no reference counting is needed. One server also keeps a single port, stops
browser tabs outliving a dead one, and lets windows share the mesh cache, which is exactly what the
compare case wants. A request for an unregistered dataset returns 404 saying its window may have
been closed, rather than stale results.

The viewer registers the `VCDataManager` it already reads through. That is transport-agnostic, so
local and remote windows register identically, and it avoids the trap where adding a method to
`DataSetController` needs implementing in four controllers while a `default` silently returns null.
Auth comes free: the client already holds the session, so the field server authenticates nothing.

#### Geometry is separated from values

Geometry and field values are separate endpoints, because geometry does not change as the user
scrubs time. Measured: **1.23 MB of geometry against 262 KB of field, so a time step costs 5.7×
less** than shipping both. The viewer refetches only the field when time or variable changes, and
rebuilds the grid only when the chosen variable lives on a different domain.

The viewer also drives itself. It asks the server what variables, domains and times a run contains
rather than being pinned to whatever the Java panel had selected; the panel's selection is a
starting point, not a pin.

#### Designed for moving boundaries before they exist

Two decisions here are shaped by what comes after fixed grids. Both are cheap now and awkward to
retrofit.

The upcoming FEniCSx moving-boundary solvers make geometry time-varying: vertex coordinates are a
function of time under ALE, and topology is **piecewise constant**, changing only at each remesh. So
the decomposition is really three things — topology, coordinates, and field values — where fixed
grids are the degenerate case of one segment with constant coordinates.

- **The geometry endpoint is not advertised as immutable.** It is the geometry for a dataset *at a
  time*. It carries an ETag and a short `max-age`, so ordinary HTTP caching expresses the
  piecewise-constancy and the client always just requests the geometry for the frame it wants — the
  cache decides whether that costs a round trip. The client never needs to know about ALE segments.
- **Responses carry the geometry they belong to**, and the viewer refuses to pair a field with a
  different one. Values are only meaningful against their own geometry; mismatching them draws
  something silently wrong rather than obviously broken, which is a real hazard once vertices move.

Expect the **wire format**, not the endpoint decomposition, to be what ALE actually breaks. Under
ALE fresh coordinates are needed every frame, so the geometry/field split buys much less, and JSON
floats stop being defensible for tens of thousands of values per frame. XDMF already expresses
shared-topology-per-segment natively, so the server has a source format that matches the structure.

### Mode B — `webapp-ng` as an independent web client

A different product rather than a variant: browse and visualize accessible spatial datasets on the
web, with its own auth, talking only to the remote API. **Not implemented.**

Here a **geometry server** — extracting the smoothed surface server-side, where pyvcell/pyvista
already has the pipeline, and shipping finished geometry to a light client — becomes attractive,
precisely because the objection that rules it out for local does not apply: a laptop running a local
solver has no Python, but the backend does.

**The question that decides this is what the web viewer is for**, and it is more a product question
than a technical one. The reason wasm was originally chosen over a geometry server was not weight
but capability: unstructured volume rendering and interactive slicing on the real mesh. A geometry
server hands over a finished surface, so moving a slice plane or changing an isovalue means either a
round trip per interaction (the trame model, with its latency and stateful sessions) or not offering
it.

- Goal is "see the shape and a colormapped field of published models" → a geometry server is clearly
  right, and the client can be very light.
- Goal is "do analysis in a browser" → it needs the volume, which means shipping compute to the
  client and paying the bundle size.

#### The geometry-server spike

Prototyped in [`../tools/geometry-server/`](../tools/geometry-server/) — a spike, not a component,
wired into nothing. Stateless by construction: no trame, no sessions, every response a pure function
of its URL. The split that makes that work is that the expensive step (extraction + smoothing,
depending only on the mesh) and the per-timepoint step have different lifetimes.

Authorization without the service knowing about users: `vcell-rest` would authenticate and authorize
via `GroupAccess`, then sign a capability the service verifies statelessly, scoped to the
**simulation id** — the granularity `GroupAccess` actually grants. Authentication is the easy,
portable half; **authorization is the hard half and lives in Java and the database. Do not
reimplement `GroupAccess` in Python.** A service validating JWTs itself would still have to call
back into Java to ask "may this user read sim X", making it a worse version of simply proxying
through Quarkus.

What it found:

- **Correct.** The served surface matches the committed golden fixture to 8.3e-07 (JSON rounding),
  so the server-side and wasm client-side pipelines provably agree.
- **Client weight is the real argument.** A dependency-free WebGL2 client — no vtk.js, no VTK, no
  wasm — renders the result in **6.8 KB raw / 3.1 KB gzipped**, roughly **3,900×** smaller than the
  12 MB wasm bundle.
- **Bandwidth is not the argument.** Gzipped, the extracted surface is 159 KB against 198 KB for the
  raw voxel grid the wasm path ships — only ~20% smaller, because raw integer grids compress well.
  This also weakens the case for capability tokens over simply proxying through Quarkus: proxying
  ~160 KB is nothing, and the proxy shape has no shared secret, no signed-URL leakage surface, and no
  expiry-versus-caching tension.
- **A real performance problem, not inherent but blocking.** The field endpoint takes 2–4 s per
  timepoint, and not from cold start. It traces to pyvcell's `pde_dataset.get_data()`, ~2 s per call
  with no caching, returning 126,025 values where 12,210 are needed. The Java field server produces
  the whole grid *and* field in ~200 ms for comparable data.

### If both modes exist

The smoothing pipeline would exist twice — wasm client-side and pyvista server-side — and the two
must agree, or the same model looks different on the desktop and on the web. The instrument exists:
the golden-file test proved the wasm `WindowedSinc` is bit-identical to the pyvista reference, and
the geometry-server spike's surface matches the same golden. That check has been run by hand; wiring
it into CI is a precondition for shipping both.

One hybrid probably not worth pursuing: having desktop-with-remote-simulation also use the geometry
server. The desktop must carry the wasm bundle anyway for local runs, so it saves no installer
weight and only reduces bytes through the client.

## Open questions

- **Serving the viewer page — decided and half-built.** The direction is settled: the loopback
  server serves the page as well as the data, so the two share an origin. That removes the
  cross-origin fetch entirely — no CORS, no preflight — and sidesteps the question of whether an
  HTTPS page may reach `http://127.0.0.1`, which sits at the intersection of two
  independently-evolving browser policies (mixed content, where loopback is treated as potentially
  trustworthy, and Private Network Access, which has been adding preflight requirements). We do not
  control those rules, so the best move is not to depend on them.

  **The server side is implemented and verified** against a production build: the page, its assets
  and the wasm bundle serve correctly, client-side routes fall back to `index.html`, path traversal
  is contained, and the viewer renders from a single origin with no dev server running. It reads
  `vcell.fieldViewer.staticDir`, defaulting to `<vcell.installDir>/webviewer`; with no such
  directory it registers nothing and falls back to `vcell.fieldViewer.url`, which is how a
  developer points at a dev server.

  **What is not built is the packaging.** Nothing produces or ships that directory:
  `VCell.install4j` has no entry for web content, and the media builds stage none. So in an
  installed client the directory is absent and the fallback URL points at a dev server that is not
  running. The content is [`webapp-viewer/`](../webapp-viewer/), a standalone page extracted from
  `webapp-ng`: `index.html` plus one module, no framework and no bundler, so **the directory is the
  deployable** and the only build-time action is downloading the ~12 MB wasm bundle. What remains is
  a `dirEntry` mounting it at `webviewer` and CI wiring to fetch the bundle and stage it, tracked in
  [#1851](https://github.com/virtualcell/vcell/issues/1851).

  The extraction settled what was previously an open packaging question — whether to ship the whole
  webapp or a local-only configuration of it. Neither: as an Angular route the viewer used no Angular
  feature of substance (no DI, services, `HttpClient`, router or forms) while inheriting a ~5.5 MB
  shell and an `index.html` that pulls Bootstrap, the Auth0 theme and Google Fonts from CDNs — all
  wrong for a viewer whose subject is a local simulation and whose user may be offline. Standalone,
  the page contacts nothing but the client's own loopback server; that is verified in the browser
  test, which asserts the loopback origin is the only one touched. The remaining cost is the ~12 MB
  wasm blob in every platform installer. The real design work is the data-source seam (local loopback
  vs remote REST), not the build split.
- Whether to pre-seed the feature flag in the install4j vmoptions template so testers flip a value
  rather than add a line.
- Which client cost is the right one to pay — 12 MB gzipped for the wasm client against 3.1 KB for a
  dependency-free client drawing server-extracted geometry — which depends on whether a given viewer
  needs volume rendering and interactive slicing at all.
- pyvcell's `get_data()` performance would need addressing before any geometry-server path could
  drive interactive playback.
- The remote path has not been exercised against a genuinely remote simulation in a running client.
  The argument is that mesh construction is provably identical and the transport is the one the
  viewer already uses, which is strong but is not the same as having watched it work.

## Related

- [`salad-3d-renderer-design.md`](salad-3d-renderer-design.md) — the SpringSaLaD trajectory viewer,
  and §8/§8B where the field-viz question originated.
- [`../webapp-viewer/`](../webapp-viewer/) — the standalone viewer page, including the vtk.wasm
  gotchas worth knowing before editing it.
- [`../tools/vtk-wasm/`](../tools/vtk-wasm/) — the custom VTK-to-WebAssembly bundle and its
  golden-file test.
- [`../tools/geometry-server/`](../tools/geometry-server/) — the geometry-server spike.
