// vtk.js ships some modules (e.g. filters) without .d.ts files. Declare them so strict TS builds
// can import them (as `any`). See docs/salad-3d-renderer-design.md §6 (Phase 2 spike frictions).
declare module '@kitware/vtk.js/Filters/General/ImageMarchingCubes';
