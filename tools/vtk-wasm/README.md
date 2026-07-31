# Custom VTK.wasm build for VCell's field viewer

Builds a **VTK compiled to WebAssembly** bundle (`vtkWebAssembly.{mjs,wasm}`) for the browser PDE
field viewer described in `docs/salad-3d-renderer-design.md` §8 / §8B. The bundle is loaded by the
[`@kitware/vtk-wasm`](https://www.npmjs.com/package/@kitware/vtk-wasm) JavaScript loader and does the
mesh processing (`vtkThreshold` → `vtkWindowedSincPolyDataFilter`), slicing, contouring, and
rendering **client-side** — the same code path for the web app and for local desktop runs (served by
a small local Java HTTP server + the system browser). No Python or native VTK on the client.

## Why a from-source build (and why it's fiddly)

The released `@kitware/vtk-wasm` bundle already ships the renderer + several filters, but **lacks the
IO readers and `FiltersGeometry` (surface extraction)** we need. So we rebuild VTK with the modules
we require. The hard part is that VTK's JS-wrapped wasm build only configures/compiles cleanly with
VTK's **own, self-consistent CI configuration** — cherry-picking module flags fails in a cascade:

- omit `VTK_WRAP_SERIALIZATION` → the SOA/serialization array classes emit embind bindings that don't
  compile (`no matching member function for call to 'function'` — "couldn't infer 'Callable'"), and
- add it piecemeal → the next inconsistency (`VTK_BUILD_TYPES_JSON`, the wrap-target link error) …

So this build configures via VTK's `-C .gitlab/ci/configure_wasm32_emscripten_linux.cmake` (which
transitively pulls `configure_wasm_common.cmake` + `configure_common.cmake`: `VTK_BUILD_ALL_MODULES`,
`VTK_WRAP_SERIALIZATION`, `VTK_BUILD_TYPES_JSON`, `VTK_DISPATCH_SOA_ARRAYS`, `VTK_ENABLE_WEBGPU`,
`VTK_WEBASSEMBLY_THREADS=OFF`, and the module-disable list).

**Do not set `VTK_WRAP_JAVASCRIPT`.** VTK has two wasm-bundle mechanisms and only one is the loader's:
- `VTK_WRAP_JAVASCRIPT=ON` → the *older* per-class **`vtkweb.js`** (what VTK 9.6.2 emitted), and it
  fails here because it links *all* modules — including the `VTK::WebAssembly` module, which is
  itself an executable (`Target "VTK::WebAssembly" … may not be linked into another target`).
- The **`VTK::WebAssembly` module** (`Web/WebAssembly`, `LIBRARY_NAME vtkWebAssembly`, depends on
  `WebAssemblySession` + `SerializationManager`) → produces **`vtkWebAssembly.{js,wasm}`** with the
  standalone-session API — *this* is what `@kitware/vtk-wasm` loads.

VTK CI never sets `VTK_WRAP_JAVASCRIPT`; the bundle comes from the module. So we just ensure
`VTK_MODULE_ENABLE_VTK_WebAssembly=YES` and build.

## Pins (bump together)

| Pin | Value | Why |
|---|---|---|
| VTK commit | `8fcb79cafc338bf890579ba9f565019130c7b1e8` | the commit the `@kitware/vtk-wasm` 2.1.8 loader's bundle (`9.7.20260726`) tracks — matches the loader's glue API + `vtkWebAssembly` naming + standalone-session |
| emsdk | `4.0.20` | what VTK's CI pins at that commit (`.gitlab/ci/download_emsdk.cmake`) |
| platform | **amd64** | VTK CI is amd64; the arm64 emscripten image miscompiles VTK's SOA embind |

## Run it

**CI (recommended):** GitHub Actions → *Build custom VTK.wasm bundle* (`workflow_dispatch`). Runs
natively on an amd64 runner inside `emscripten/emsdk:4.0.20`, uploads the bundle as an artifact.
Inputs: `vtk_commit`, `optimization`.

**Locally** (Apple Silicon needs `--platform linux/amd64`; emulated → slow):

```bash
docker build --platform linux/amd64 -t vcell-vtk-wasm tools/vtk-wasm
mkdir -p dist
docker run --rm --platform linux/amd64 -e VTK_WASM_OPTIMIZATION=SMALL \
    -e OUT_DIR=/out -v "$PWD/dist:/out" vcell-vtk-wasm
```

## Status / open work

This scaffold produces VTK's **all-modules** bundle (guaranteed to compile — it's what Kitware
ships). Before it's production-ready:

1. **Verify what the bundle exposes.** Load the artifact through `@kitware/vtk-wasm` and confirm the
   session namespace actually surfaces `vtkXMLImageDataReader` / `vtkXMLUnstructuredGridReader`,
   `vtkThreshold`, `vtkGeometryFilter`, `vtkWindowedSincPolyDataFilter`. (A probe of the *released*
   bundle showed the XML reader was **not** registered in the session even though the module builds —
   so this needs confirming, and if readers aren't wrapped we build the grid in-memory via the
   data-model API instead, which the released bundle already exposes.)
2. **Golden-file test:** client-side `vtkThreshold` + `vtkWindowedSincPolyDataFilter` (12 iters,
   pass-band 0.05, feature angle 120°) reproduces the pyvcell/Java `.vtu` for a real VCell mesh.
3. **Trim modules** from the working all-modules baseline to cut size (all-modules is ~77 MB /
   ~12 MB gz; a trimmed `IOXML` + `FiltersGeometry`/`Core`/`General` + rendering set at `SMALLEST`
   should be far smaller). Trim incrementally and keep it building — see the cascade warning above.
4. **Build time / cost:** the all-modules wasm build is large; on a stock GitHub runner it is slow.
   Consider a larger runner and/or ccache before making this routine.
5. **Publish + consume:** once trimmed and verified, publish the bundle (release asset / GitHub
   Packages / an `@virtualcell/vtk-wasm` npm package) and have `webapp-ng` pin + fetch it. If this
   grows its own lifecycle, graduate it to a dedicated `virtualcell/vcell-vtk-wasm` repo.
