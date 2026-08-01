# VTK source patches for the custom wasm bundle

`build.sh` applies every `*.patch` here to the freshly-cloned VTK tree (at the pinned commit) with
`git apply`, before configuring. Application is idempotent (re-runs detect an already-applied patch)
and **fails the build loudly** if a patch no longer applies — so a VTK pin bump forces a re-review.

## Why these exist — serialization (marshalling) coverage

The `@kitware/vtk-wasm` standalone session can only **construct** a class if VTK generated
(de)serialization code (SerDes) for it. That is opt-in at three levels:

1. the module has `INCLUDE_MARSHAL` in its `vtk.module`,
2. the class is annotated `VTK_MARSHALAUTO` (auto, property-based) or `VTK_MARSHALMANUAL` (hand-coded),
3. `VTK::WebAssembly` lists the module in `OPTIONAL_DEPENDS` (`Web/WebAssembly/vtk.module`) so the
   SerDes are packaged into the bundle (VTK errors at configure if an `INCLUDE_MARSHAL` module is not
   a WebAssembly dependency).

Classes in modules VTK hasn't opted in (`Filters/Geometry`, `IO/XML`, …) are compiled into the bundle
but **cannot be constructed** in the session — verified by probing the bundle: the type is named but
`vtk.vtkGeometryFilter()` fails with `Constructor not found`.

## `0001-marshal-coverage-filters-geometry.patch`

Adds marshalling coverage for the two **surface-extraction filters** the FV field viewer needs:
`vtkGeometryFilter` and `vtkDataSetSurfaceFilter` (module `INCLUDE_MARSHAL` + class `VTK_MARSHALAUTO`
+ `VTK::FiltersGeometry` in WebAssembly's `OPTIONAL_DEPENDS`). This makes the faithful FV smoothing
pipeline — `vtkThreshold` → `vtkGeometryFilter` → `vtkWindowedSincPolyDataFilter` — fully
constructable **client-side** (all three verified `true` in a session probe of the patched bundle).
Turning the module on also unlocks the other already-annotated `Filters/Geometry` classes for free.

**Not covered:** the `IO/XML` readers (`vtkXMLUnstructuredGridReader`, …). Same recipe would work, but
the viewer builds grids **in-memory** from server-sent arrays instead (avoids the wasm-FS read path),
so readers are intentionally left unpatched.

## Upstreaming

These annotations are good candidates to contribute to VTK (it is progressively marshalling classes),
which would retire the patch. If upstreaming, move the `#include "vtkWrappingHints.h"` up into each
header's include block rather than beside the class declaration.
