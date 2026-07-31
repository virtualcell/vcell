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

# --- configure with VTK's own wasm CI cache (-C), plus JS wrapping + our optimization ---
# The -C file transitively includes configure_wasm_linux.cmake -> configure_wasm_common.cmake ->
# configure_common.cmake, giving the complete working set: VTK_BUILD_ALL_MODULES, WRAP_SERIALIZATION,
# BUILD_TYPES_JSON, DISPATCH_SOA_ARRAYS, ENABLE_WEBGPU, WEBASSEMBLY_THREADS=OFF, and the module
# disables for libs absent from the toolchain image. We only add VTK_WRAP_JAVASCRIPT (not set in the
# cache files) and the optimization knob.
emcmake cmake -S "$SRC" -B "$BUILD" -G Ninja \
  -C "$SRC/.gitlab/ci/configure_wasm32_emscripten_linux.cmake" \
  -DCMAKE_BUILD_TYPE=Release \
  -DVTK_WRAP_JAVASCRIPT=ON \
  -DVTK_WASM_OPTIMIZATION="$OPT" \
  -DVTK_BUILD_TESTING=OFF

# --- build (VTK's wasm cache pins the link job pool to 1 to dodge an emscripten file-lock bug) ---
cmake --build "$BUILD"

# --- collect the loader bundle ---
mkdir -p "$OUT"
find "$BUILD" -name "vtkWebAssembly.wasm" -exec cp {} "$OUT/" \;
find "$BUILD" -name "vtkWebAssembly.mjs"  -exec cp {} "$OUT/" \;
if [ ! -f "$OUT/vtkWebAssembly.wasm" ] || [ ! -f "$OUT/vtkWebAssembly.mjs" ]; then
  echo "ERROR: expected vtkWebAssembly.{wasm,mjs} not produced — check VTK_WRAP_JAVASCRIPT" >&2
  find "$BUILD" -name "vtk*.wasm" -o -name "vtk*.mjs" | head >&2
  exit 1
fi
cd "$OUT"
tar czf "vcell-vtk-wasm32-emscripten.tar.gz" vtkWebAssembly.wasm vtkWebAssembly.mjs
echo ">>> bundle:"
ls -lh "$OUT"
echo ">>> gzipped wasm: $(gzip -c vtkWebAssembly.wasm | wc -c | awk '{printf "%.1f MB", $1/1048576}')"
