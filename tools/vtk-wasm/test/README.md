# vtk.wasm WindowedSinc golden-file test

Verifies that the custom **vtk.wasm** bundle reproduces VCell's finite-volume surface-smoothing
**exactly**, client-side. This is the numerically-sensitive stage of the field viewer's FV pipeline
(`vtkThreshold` → `vtkGeometryFilter` → **`vtkWindowedSincPolyDataFilter`**), so locking it against a
reference guards against a wasm binding, VTK-version, or parameter regression.

## What it checks

The reference pipeline is pyvcell's `smooth_unstructured_grid_surface`
(`_internal/simdata/vtk/vtkmesh_utils.py`): a real VCell mesh → whole-voxel volume grid →
`vtkUnstructuredGridGeometryFilter` → `vtkGeometryFilter` → `vtkWindowedSincPolyDataFilter`
(iterations 12, feature angle 120°, pass-band 0.05, boundary/feature-edge/non-manifold smoothing off,
normalize-coordinates on).

`gen_golden.py` runs that up to the pre-smooth surface and captures two fixtures:

| fixture | contents |
|---|---|
| `fixtures/input_surface.json` | the pre-smooth boundary surface (7825 points, 7848 quads) |
| `fixtures/golden_points.json` | the reference-smoothed points + the exact sinc params |

`golden-test.html` builds that surface in the **vtk.wasm standalone session**, runs *only*
`vtkWindowedSincPolyDataFilter` with the fixture's params, and compares its output to the golden.
`run-golden-test.mjs` runs it headless and asserts.

**Result:** the wasm output matches the reference **bit-for-bit** (max deviation `0.0` over all 7825
points), while genuinely smoothing (max movement vs input `0.4465`, matching the reference). VTK's
WindowedSinc is deterministic and unchanged 9.6.2 → 9.7, so the wasm binding is faithful.

## Run it

By default it downloads the pinned released bundle from
[`virtualcell/vcell-vtk-wasm`](https://github.com/virtualcell/vcell-vtk-wasm) (the artifact
`webapp-ng` consumes) and validates it. Needs a Chrome and `puppeteer-core` (available via
`webapp-ng`'s dependencies).

```bash
node tools/vtk-wasm/test/run-golden-test.mjs        # downloads + validates the pinned release
# exits 0 on pass, prints max deviation; 1 on regression
```

Env: `BUNDLE_TAG` (release to fetch, default `v1.2.0`), `BUNDLE_TARGZ` (use a local `.tar.gz`
instead — overrides `BUNDLE_TAG`), `CHROME`, `PUPPETEER_CORE`, `TOL` (default `1e-6`; reference is
`0`), `VERBOSE=1` to stream the in-page stages.

`vtk-umd.js` is the vendored `@kitware/vtk-wasm` 2.1.8 UMD loader (BSD-3-Clause), pinned to match the
bundle; bump it together with the VTK commit in the `vcell-vtk-wasm` build repo.

## Regenerate the fixtures

Only needed if the reference pipeline or params change. Requires pyvcell importable (its `.venv` has
the reference VTK 9.6.2) and a VCell `.mesh`:

```bash
/path/to/pyvcell/.venv/bin/python tools/vtk-wasm/test/gen_golden.py
# MESH_FILE=... DOMAIN=... to override the default pyvcell mesh fixture / domain
```

## Status / follow-up

The build now gates on this same test in `virtualcell/vcell-vtk-wasm` before publishing a release, so
the released bundle is already validated at build time. This copy is VCell's **consumer-side** check
of the release it actually loads; wiring it into vcell CI (a job with Chrome + `puppeteer-core`) is a
follow-up. A natural extension is to feed the **voxel volume grid** instead of the pre-smooth surface,
exercising `vtkUnstructuredGridGeometryFilter` + `vtkGeometryFilter` in wasm end-to-end (both are
constructable via the marshal-coverage patch).
