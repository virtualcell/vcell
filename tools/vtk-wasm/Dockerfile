# Local reproduction of the CI vtk.wasm build — mirrors .github/workflows/vtk-wasm-build.yml:
# VTK's own wasm CI image (has cmake/ninja/git, NOT emsdk) + build.sh, which installs VTK's pinned
# emsdk the way VTK's CI does. This exact toolchain is what links a working bundle; the public
# emscripten/emsdk image fails at link even with the stock config (see README "Why the VTK CI image").
#
#   docker build -t vcell-vtk-wasm tools/vtk-wasm
#   docker run --rm -e VTK_WASM_OPTIMIZATION=SMALL -v "$PWD/dist:/out" -e OUT_DIR=/out vcell-vtk-wasm
#
# Works natively on arm64 and amd64 — build.sh installs the arch-matching emsdk and VTK's stock
# all-modules config links cleanly on both (verified on arm64; CI runs amd64).
FROM kitware/vtk:ci-fedora42-20260603
COPY build.sh /work/build.sh
WORKDIR /work
ENTRYPOINT ["bash", "/work/build.sh"]
