// The @kitware/vtk-wasm loader is consumed via its UMD build (loaded as a <script>), not the ESM
// entry: the ESM runtime does a dynamic import(blobUrl) of the in-browser-untar'd glue module, which
// webpack rewrites and breaks ("Critical dependency: the request of a dependency is an expression").
// The UMD build isn't processed by webpack, so its native import() works (proven in the render probe).
// loadAsync fetches the bundle .tar.gz, untars it in-browser, and returns a runtime whose
// createStandaloneSession() exposes the `vtk` object (camelCase VTK API, all methods async).
/* eslint-disable @typescript-eslint/no-explicit-any */
interface VtkWasmSession { vtk: any; }
interface VtkWasmRuntime { createStandaloneSession(): VtkWasmSession; }
interface VtkWasmGlobal { loadAsync(options: { url: string; [key: string]: unknown }): Promise<VtkWasmRuntime>; }

declare global {
  interface Window { vtkwasm?: VtkWasmGlobal; }
}
export {};
