#!/usr/bin/env bash
#
# Build a custom VTK.wasm bundle for VCell's browser field viewer.
#
# Produces vtkWebAssembly.{mjs,wasm} (the format the @kitware/vtk-wasm loader consumes) by
# compiling VTK to WebAssembly with VTK's *own* wasm CI configuration, which is the only
# self-consistent way to get a working JS-wrapped bundle (cherry-picking module flags fails in a
# cascade — see README.md). Run in CI (see .github/workflows/vtk-wasm-build.yml, inside the
# emscripten/emsdk container on a native amd64 runner) or locally via tools/vtk-wasm/Dockerfile.
#
# Requires emscripten on PATH (emcmake/em++), cmake >= 3.29, ninja, git.
set -euo pipefail

# The VTK commit the @kitware/vtk-wasm loader (2.1.8, bundle "9.7.20260726") tracks. Building a
# different commit risks the loader-vs-bundle glue API drifting; bump both together.
VTK_COMMIT="${VTK_COMMIT:-8fcb79cafc338bf890579ba9f565019130c7b1e8}"
# NO_OPTIMIZATION | LITTLE | MORE | BEST | SMALL | SMALLEST | SMALLEST_WITH_CLOSURE
OPT="${VTK_WASM_OPTIMIZATION:-SMALL}"

SRC="${VTK_SRC_DIR:-/tmp/vtk-src}"
BUILD="${VTK_BUILD_DIR:-/tmp/vtk-build}"
OUT="${OUT_DIR:-$PWD/dist}"

echo ">>> VTK commit : $VTK_COMMIT"
echo ">>> optimization: $OPT"

# --- fetch VTK at the pinned commit (shallow) ---
if [ ! -e "$SRC/CMakeLists.txt" ]; then
  mkdir -p "$SRC"; cd "$SRC"
  git init -q
  git remote add origin https://github.com/Kitware/VTK.git 2>/dev/null || true
  git fetch --depth 1 origin "$VTK_COMMIT"
  git checkout -q FETCH_HEAD
fi

# --- configure with VTK's own wasm CI cache (-C) ---
# The -C file transitively includes configure_wasm_linux.cmake -> configure_wasm_common.cmake ->
# configure_common.cmake, giving the complete working set: VTK_BUILD_ALL_MODULES, WRAP_SERIALIZATION,
# BUILD_TYPES_JSON, DISPATCH_SOA_ARRAYS, ENABLE_WEBGPU, WEBASSEMBLY_THREADS=OFF, and the module
# disables for libs absent from the toolchain image.
#
# The loader bundle (vtkWebAssembly.{mjs,wasm}, standalone-session API) is produced by the
# **VTK::WebAssembly module** (Web/WebAssembly, LIBRARY_NAME vtkWebAssembly, depends on
# WebAssemblySession + SerializationManager) — NOT by `VTK_WRAP_JAVASCRIPT` (that path emits the
# older per-class `vtkweb.js` and, worse, tries to link the VTK::WebAssembly *executable* into its
# own target → "may not be linked into another target"). So we do NOT set VTK_WRAP_JAVASCRIPT; we
# just ensure the module is enabled (VTK's all-modules config already does, but be explicit).
# VTK's CI enables an `sccache` compiler launcher (its own build cache) that isn't in the toolchain
# image — override to empty so every compile doesn't die with "sccache: not found". (A real ccache/
# sccache + Actions cache is a worthwhile speed follow-up for this large build.)
# The vtkWebAssembly module's link pulls in WebGPU (emdawnwebgpu port), WebXR, VR and JSPI, one of
# which leaves a stray `undefined symbol: $` — fatal by default (emscripten's own suggested fix is
# -sERROR_ON_UNDEFINED_SYMBOLS=0; it was a benign warning in earlier builds). Our viewer uses WebGL
# (RenderingOpenGL2), not those, so allow the undefined symbol for now. TODO: trim RenderingWebGPU/
# WebXR/VR (see README) — that removes the `$` cause cleanly *and* cuts size.
emcmake cmake -S "$SRC" -B "$BUILD" -G Ninja \
  -C "$SRC/.gitlab/ci/configure_wasm32_emscripten_linux.cmake" \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_C_COMPILER_LAUNCHER= \
  -DCMAKE_CXX_COMPILER_LAUNCHER= \
  -DCMAKE_EXE_LINKER_FLAGS="-sERROR_ON_UNDEFINED_SYMBOLS=0" \
  -DVTK_MODULE_ENABLE_VTK_WebAssembly=YES \
  -DVTK_WASM_OPTIMIZATION="$OPT" \
  -DVTK_BUILD_TESTING=OFF

# --- build (VTK's wasm cache pins the link job pool to 1 to dodge an emscripten file-lock bug) ---
cmake --build "$BUILD"

# --- collect the loader bundle (the VTK::WebAssembly module emits vtkWebAssembly.{js|mjs,wasm}) ---
mkdir -p "$OUT"
find "$BUILD" -name "vtkWebAssembly.wasm" -exec cp {} "$OUT/" \;
find "$BUILD" \( -name "vtkWebAssembly.mjs" -o -name "vtkWebAssembly.js" \) -exec cp {} "$OUT/" \;
GLUE="$(ls "$OUT"/vtkWebAssembly.mjs "$OUT"/vtkWebAssembly.js 2>/dev/null | head -1)"
if [ ! -f "$OUT/vtkWebAssembly.wasm" ] || [ -z "$GLUE" ]; then
  echo "ERROR: expected vtkWebAssembly.{wasm, mjs|js} not produced" >&2
  find "$BUILD" -name "vtkWebAssembly*" -o -name "vtkweb*" | head >&2
  exit 1
fi
cd "$OUT"
tar czf "vcell-vtk-wasm32-emscripten.tar.gz" vtkWebAssembly.wasm "$(basename "$GLUE")"
echo ">>> bundle: $(basename "$GLUE") + vtkWebAssembly.wasm"
ls -lh "$OUT"
echo ">>> gzipped wasm: $(gzip -c vtkWebAssembly.wasm | wc -c | awk '{printf "%.1f MB", $1/1048576}')"
