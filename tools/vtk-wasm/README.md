# VCell VTK.wasm bundle

The custom **VTK-compiled-to-WebAssembly** bundle for VCell's browser field viewer is built and
released from a dedicated repository:

**→ https://github.com/virtualcell/vcell-vtk-wasm**

That repo holds the build recipe (`build.sh` + the marshalling-coverage `patches/`), the golden-file
test, and the *Build & release* workflow. Each run publishes the bundle as a GitHub Release asset at a
stable, CDN-backed URL:

```
https://github.com/virtualcell/vcell-vtk-wasm/releases/download/<tag>/vcell-vtk-wasm32-emscripten.tar.gz
```

## What lives here

- **`test/`** — the golden-file test *as VCell consumes it*: it downloads the released bundle and
  asserts the client-side `vtkWindowedSincPolyDataFilter` smoothing reproduces the pyvcell reference
  bit-for-bit. See `test/README.md`.

VCell's `webapp-ng` field viewer loads the released bundle at runtime (via the `@kitware/vtk-wasm`
loader). Architecture: `docs/salad-3d-renderer-design.md` §8 / §8B.
