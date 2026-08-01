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

VTK CI never sets `VTK_WRAP_JAVASCRIPT`; the bundle comes from the module. VTK's all-modules config
already enables it, so we build the stock config unmodified.

## The `undefined symbol: $` link failure (two independent causes)

Getting the `.mjs` to link at all took pinning down a link error that shows up as:

```
error: undefined symbol: $ (referenced by root reference (e.g. compiled C/C++ code))
```

Left as a hard error, the link dies there. If undefined symbols are *allowed*
(`-sERROR_ON_UNDEFINED_SYMBOLS=0`), emscripten instead emits a **malformed nameless stub**
`function (...args){ abort('missing function: $'); }`, and its `--export-es6 unsignPointers` acorn
pass then rejects that invalid JS with `SyntaxError: Unexpected token` — same failure, one step later.

**Two independent things each produce this dangling `$`; avoid both:**

1. **Trimming the module set.** Do **not** cherry-pick modules (e.g. dropping `RenderingWebGPU` /
   WebXR / VR to shrink the ~78 MB bundle). A trimmed set leaves the `$` unresolved. Build VTK's
   **complete** wasm module set — the same as VTK's own CI — with no module enables/disables and no
   extra linker flags.
2. **The wrong toolchain image** (see next section). Even the *stock* all-modules config fails with
   the same `$` on the public `emscripten/emsdk` image.

The proven-good combination is **VTK's stock all-modules config built on VTK's own CI image with
VTK's pinned emsdk** — it has no dangling `$` and links cleanly. **Size-trimming is a separate,
careful follow-up** that must re-introduce disables **one module at a time**, keeping the `.mjs`
linking at each step, to find the specific module that leaves the `$` — never a blanket disable.

## Why the VTK CI image (not `emscripten/emsdk`)

The build runs in VTK's own wasm CI image, **`kitware/vtk:ci-fedora42-…`**, and `build.sh` installs
VTK's pinned emsdk into it exactly the way VTK's CI does (`download_emsdk.cmake` → `emsdk install`,
plus `download_node.cmake` on x86_64). We tried the obvious simpler path — the public
`emscripten/emsdk:4.0.20` image — and it **fails at link with the `$` error even on the stock
all-modules config**. What's surprising is that this is *not* an emscripten-version difference: the
emscripten binary is **byte-identical** (both commit `6913738`), the node version matches, and the
emitted link command + module set are identical. The divergence is **environmental** — the public
image's emscripten ports/cache state leaves `$` unresolved where VTK's CI image resolves it. Rather
than chase the exact cause, we reproduce VTK's known-good environment. (Verified: a full build on
`kitware/vtk:ci-fedora42` links the bundle cleanly; the same stock config on `emscripten/emsdk:4.0.20`
does not.)

## Pins (bump together)

| Pin | Value | Why |
|---|---|---|
| VTK commit | `8fcb79cafc338bf890579ba9f565019130c7b1e8` | the commit the `@kitware/vtk-wasm` 2.1.8 loader's bundle (`9.7.20260726`) tracks — matches the loader's glue API + `vtkWebAssembly` naming + standalone-session |
| CI image | `kitware/vtk:ci-fedora42-20260603` | VTK's own wasm CI image; the public `emscripten/emsdk` image fails at link (see "Why the VTK CI image") |
| emsdk | `4.0.20` | what VTK's CI pins at that commit (`.gitlab/ci/download_emsdk.cmake`); `build.sh` installs it |
| platform | amd64 (CI) / arm64 (local) | CI runs amd64; the build also links cleanly on native arm64 with this recipe (`build.sh` installs the arch-matching emsdk) |

## Run it

**CI (recommended):** GitHub Actions → *Build custom VTK.wasm bundle* (`workflow_dispatch`). Runs on
an amd64 runner inside `kitware/vtk:ci-fedora42-…`; `build.sh` installs VTK's pinned emsdk, builds,
and uploads the bundle as an artifact. Inputs: `vtk_commit`, `optimization`.

**Locally** (works natively on Apple Silicon — no `--platform` needed):

```bash
docker build -t vcell-vtk-wasm tools/vtk-wasm
mkdir -p dist
docker run --rm -e VTK_WASM_OPTIMIZATION=SMALL -e OUT_DIR=/out -v "$PWD/dist:/out" vcell-vtk-wasm
```

## Status / open work

This produces VTK's **all-modules** bundle (it's what Kitware ships, and it links cleanly — see
"Why stock all-modules" above). Before it's production-ready:

1. **What the bundle exposes — DONE (probed), and why there's a patch.** Loading the bundle through
   `@kitware/vtk-wasm` and enumerating the session showed a key gotcha: the session can only
   *construct* a class if VTK generated serialization (SerDes) code for it — **module presence is not
   enough**. `vtkThreshold`/`vtkCutter`/`vtkContourFilter`/`vtkWindowedSincPolyDataFilter`/
   `vtkProjectedTetrahedra` construct fine, but the surface-extraction filters `vtkGeometryFilter` /
   `vtkDataSetSurfaceFilter` and the XML readers were **named-only** (`Constructor not found`). The
   `patches/` here fix the surface filters (see `patches/README.md`); the readers are left unpatched
   because the viewer builds grids **in-memory** from server arrays. After the patch, all of
   `vtkThreshold → vtkGeometryFilter → vtkWindowedSincPolyDataFilter` construct — verified by re-probe.
2. **Golden-file test:** client-side `vtkThreshold` + `vtkWindowedSincPolyDataFilter` (12 iters,
   pass-band 0.05, feature angle 120°) reproduces the pyvcell/Java `.vtu` for a real VCell mesh.
3. **Trim modules** from the working all-modules baseline to cut size (all-modules is ~78 MB /
   ~12 MB gz). ⚠️ This is delicate — a blanket "drop the backends we don't render with" trim breaks
   the link (the `undefined symbol: $`, see "The `undefined symbol: $` link failure"). Re-introduce
   disables **one module at a time**, keeping the `.mjs` linking at each step, to find the specific
   module that leaves the dangling `$`. Do this only after steps 1–2 confirm the all-modules bundle
   actually works in the loader; size is an optimization, not a blocker.
4. **Build time / cost:** the all-modules wasm build is large; on a stock GitHub runner it is slow.
   Consider a larger runner and/or ccache before making this routine.
5. **Publish + consume:** once trimmed and verified, publish the bundle (release asset / GitHub
   Packages / an `@virtualcell/vtk-wasm` npm package) and have `webapp-ng` pin + fetch it. If this
   grows its own lifecycle, graduate it to a dedicated `virtualcell/vcell-vtk-wasm` repo.
