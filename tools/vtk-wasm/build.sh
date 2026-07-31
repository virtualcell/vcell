#!/usr/bin/env bash
#
# Build a custom VTK.wasm bundle for VCell's browser field viewer.
#
# Produces vtkWebAssembly.{mjs,wasm} (the format the @kitware/vtk-wasm loader consumes) by
# compiling VTK to WebAssembly with VTK's *own* wasm CI configuration AND toolchain — the only
# combination proven to link a working JS-wrapped bundle (see README.md "Why stock all-modules" and
# "Why the VTK CI image"). Run in CI (see .github/workflows/vtk-wasm-build.yml, inside the
# kitware/vtk CI container on a native amd64 runner) or locally via tools/vtk-wasm/Dockerfile.
#
# Requires: cmake >= 3.29, ninja, git. Emscripten is bootstrapped below (VTK's pinned emsdk) if it
# isn't already on PATH — so the intended base image is VTK's CI image (which has cmake/ninja/git but
# NOT emsdk), NOT emscripten/emsdk (see the toolchain note below for why that image fails).
set -euo pipefail

# The VTK commit the @kitware/vtk-wasm loader (2.1.8, bundle "9.7.20260726") tracks. Building a
# different commit risks the loader-vs-bundle glue API drifting; bump both together.
VTK_COMMIT="${VTK_COMMIT:-8fcb79cafc338bf890579ba9f565019130c7b1e8}"
# The emsdk SDK version VTK's CI pins for this commit (.gitlab/ci/download_emsdk.cmake).
EMSDK_VERSION="${EMSDK_VERSION:-4.0.20}"
# NO_OPTIMIZATION | LITTLE | MORE | BEST | SMALL | SMALLEST | SMALLEST_WITH_CLOSURE
OPT="${VTK_WASM_OPTIMIZATION:-SMALL}"

SRC="${VTK_SRC_DIR:-/tmp/vtk-src}"
BUILD="${VTK_BUILD_DIR:-/tmp/vtk-build}"
OUT="${OUT_DIR:-$PWD/dist}"

echo ">>> VTK commit : $VTK_COMMIT"
echo ">>> emsdk      : $EMSDK_VERSION"
echo ">>> optimization: $OPT"

# --- fetch VTK at the pinned commit (shallow) ---
if [ ! -e "$SRC/CMakeLists.txt" ]; then
  mkdir -p "$SRC"; cd "$SRC"
  git init -q
  git remote add origin https://github.com/Kitware/VTK.git 2>/dev/null || true
  git fetch --depth 1 origin "$VTK_COMMIT"
  git checkout -q FETCH_HEAD
fi
cd "$SRC"

# --- toolchain: install VTK's *own* pinned emsdk exactly the way VTK's wasm CI does ---
# WHY not just use the public emscripten/emsdk:$EMSDK_VERSION image: it FAILS at link even with the
# stock all-modules config below — the same `undefined symbol: $` (referenced by compiled C/C++)
# that emscripten then turns into a malformed nameless stub `function (...args){abort('missing
# function: $')}`, which the acorn `--export-es6 unsignPointers` pass rejects with a SyntaxError.
# The emscripten *binary* is byte-identical (same commit) between that image and this install, and
# the link command/module set are identical too — so the divergence is environmental (the image's
# emscripten ports/cache state leaves `$` unresolved). VTK's CI image + VTK's emsdk install resolves
# `$` cleanly. So we reproduce VTK's toolchain: download_emsdk.cmake -> emsdk install/activate, and
# on x86_64 also download_node.cmake (VTK puts that node first on PATH; it runs the acorn JS passes).
if ! command -v em++ >/dev/null 2>&1; then
  echo ">>> emscripten not on PATH — installing VTK's pinned toolchain"
  export CMAKE_CONFIGURATION=wasm32_emscripten_linux   # download_node/download_emsdk key off this
  if [ "$(uname -m)" = "x86_64" ]; then
    cmake -P .gitlab/ci/download_node.cmake
    export PATH="$SRC/.gitlab/node/bin:$PATH"
    echo ">>> node (VTK-pinned, first on PATH): $(node --version)"
  else
    echo ">>> non-x86_64 host: download_node has no arch map; using the emsdk-bundled node"
  fi
  cmake -P .gitlab/ci/download_emsdk.cmake
  EMSDK_DIR="$(ls -d "$SRC"/.gitlab/emsdk* 2>/dev/null | head -1)"
  "$EMSDK_DIR/emsdk" install "$EMSDK_VERSION"
  "$EMSDK_DIR/emsdk" activate "$EMSDK_VERSION"
  # shellcheck disable=SC1091
  source "$EMSDK_DIR/emsdk_env.sh"
  export EMCC_SKIP_SANITY_CHECK=1
  # emsdk_env prepends emsdk's own node; re-prepend VTK's downloaded node (x86_64) so it stays first.
  [ -d "$SRC/.gitlab/node/bin" ] && export PATH="$SRC/.gitlab/node/bin:$PATH"
fi
echo ">>> toolchain: $(em++ --version | head -1)"

# --- configure with VTK's own wasm CI cache (-C), STOCK all-modules ---
# The -C file transitively includes configure_wasm_linux.cmake -> configure_wasm_common.cmake ->
# configure_common.cmake, giving the complete working set: VTK_BUILD_ALL_MODULES, WRAP_SERIALIZATION,
# BUILD_TYPES_JSON, DISPATCH_SOA_ARRAYS, ENABLE_WEBGPU, WEBASSEMBLY_THREADS=OFF, and the module
# disables for libs absent from the toolchain image. This reproduces VTK's own wasm CI build 1:1.
#
# The loader bundle (vtkWebAssembly.{mjs,wasm}, standalone-session API) is produced by the
# **VTK::WebAssembly module** (Web/WebAssembly, LIBRARY_NAME vtkWebAssembly, depends on
# WebAssemblySession + SerializationManager), which VTK's all-modules config already enables — NOT by
# `VTK_WRAP_JAVASCRIPT` (that path emits the older per-class `vtkweb.js` and tries to link the
# VTK::WebAssembly *executable* into its own target → "may not be linked into another target").
#
# IMPORTANT — do NOT cherry-pick / trim modules here (see README "Why stock all-modules"). Trimming
# the module set (e.g. dropping RenderingWebGPU/WebXR/VR to cut size) breaks the link with the same
# `undefined symbol: $` described in the toolchain note above. Build the stock set; size-trimming is
# a SEPARATE later exercise that must re-introduce disables one module at a time.
#
# VTK's CI enables an `sccache` compiler launcher (its own build cache) that isn't present here —
# override to empty so every compile doesn't die with "sccache: not found". (A real ccache/sccache
# + Actions cache is a worthwhile speed follow-up for this large build.)
emcmake cmake -S "$SRC" -B "$BUILD" -G Ninja \
  -C "$SRC/.gitlab/ci/configure_wasm32_emscripten_linux.cmake" \
  -DCMAKE_BUILD_TYPE=Release \
  -DCMAKE_C_COMPILER_LAUNCHER= \
  -DCMAKE_CXX_COMPILER_LAUNCHER= \
  -DVTK_WASM_OPTIMIZATION="$OPT" \
  -DVTK_BUILD_TESTING=OFF

# --- build (VTK's wasm cache pins the link job pool to 1 to dodge an emscripten file-lock bug) ---
cmake --build "$BUILD"

# --- collect the loader bundle (the VTK::WebAssembly module emits vtkWebAssembly.{js|mjs,wasm}) ---
mkdir -p "$OUT"
find "$BUILD" -name "vtkWebAssembly.wasm" -exec cp {} "$OUT/" \;
find "$BUILD" \( -name "vtkWebAssembly.mjs" -o -name "vtkWebAssembly.js" \) -exec cp {} "$OUT/" \;
# Pick the glue file (.mjs preferred, .js fallback). Use -f tests, NOT `ls a b` — `ls` on a
# missing arg exits 2, which under `set -euo pipefail` would kill the script after a good build.
if [ -f "$OUT/vtkWebAssembly.mjs" ]; then
  GLUE="$OUT/vtkWebAssembly.mjs"
elif [ -f "$OUT/vtkWebAssembly.js" ]; then
  GLUE="$OUT/vtkWebAssembly.js"
else
  GLUE=""
fi
if [ ! -f "$OUT/vtkWebAssembly.wasm" ] || [ -z "$GLUE" ]; then
  echo "ERROR: expected vtkWebAssembly.{wasm, mjs|js} not produced" >&2
  find "$BUILD" \( -name "vtkWebAssembly*" -o -name "vtkweb*" \) 2>/dev/null | head >&2
  exit 1
fi
cd "$OUT"
tar czf "vcell-vtk-wasm32-emscripten.tar.gz" vtkWebAssembly.wasm "$(basename "$GLUE")"
echo ">>> bundle: $(basename "$GLUE") + vtkWebAssembly.wasm"
ls -lh "$OUT"
echo ">>> gzipped wasm: $(gzip -c vtkWebAssembly.wasm | wc -c | awk '{printf "%.1f MB", $1/1048576}')"
