# Local reproduction of the CI vtk.wasm build (the CI uses `container: emscripten/emsdk:4.0.20`
# directly; this Dockerfile is the equivalent for `docker build`).
#
#   On Apple Silicon you MUST target amd64 to match VTK's CI toolchain — the arm64 emscripten
#   image resolves an embind template overload differently and fails to compile VTK's SOA-array
#   bindings:
#
#     docker build --platform linux/amd64 -t vcell-vtk-wasm tools/vtk-wasm
#     docker run --rm --platform linux/amd64 -e VTK_WASM_OPTIMIZATION=SMALL \
#         -v "$PWD/dist:/out" -e OUT_DIR=/out vcell-vtk-wasm
FROM emscripten/emsdk:4.0.20
RUN apt-get update && apt-get install -y --no-install-recommends ninja-build git ca-certificates \
    && rm -rf /var/lib/apt/lists/*
COPY build.sh /work/build.sh
WORKDIR /work
ENTRYPOINT ["bash", "/work/build.sh"]
