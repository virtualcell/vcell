import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * vtk.wasm field viewer (docs/salad-3d-renderer-design.md §8B).
 *
 * Runs VCell's finite-volume surface-smoothing pipeline entirely CLIENT-SIDE in the browser:
 * build the raw whole-voxel unstructured grid in-memory from server-sent arrays (here a sample
 * fixture standing in for the server) → vtkGeometryFilter (extract boundary surface) →
 * vtkWindowedSincPolyDataFilter (the faithful FV smoothing: iters 12, feature angle 120°,
 * pass-band 0.05) → render via WebGL2. All filters are constructable in the standalone session via
 * the marshalling-coverage patch (virtualcell/vcell-vtk-wasm).
 *
 * The bundle is loaded same-origin from /assets/vtk-wasm/ (placed there at build time), via the UMD
 * loader injected as a <script> — the ESM entry's runtime import(blobUrl) is broken by webpack.
 */
const LOADER_URL = '/assets/vtk-wasm/vtk.umd.js';
const BUNDLE_URL = '/assets/vtk-wasm/vcell-vtk-wasm32-emscripten.tar.gz';
const GRID_URL = '/assets/fv-sample-grid.json';
const CANVAS_SELECTOR = '#vtk-wasm-canvas';

interface FvGrid {
  numPoints: number;
  points: number[];
  cellType: number;         // 11 = VTK_VOXEL
  cells: number[][];        // per-cell point-id lists
  sinc: { iterations: number; feature_angle: number; pass_band: number };
}

function loadUmdLoader(): Promise<void> {
  if (window.vtkwasm) return Promise.resolve();
  return new Promise((resolve, reject) => {
    const existing = document.querySelector<HTMLScriptElement>('script[data-vtk-wasm-loader]');
    if (existing) {
      existing.addEventListener('load', () => resolve());
      existing.addEventListener('error', () => reject(new Error('vtk-wasm UMD loader script failed')));
      return;
    }
    const s = document.createElement('script');
    s.src = LOADER_URL;
    s.setAttribute('data-vtk-wasm-loader', '');
    s.onload = () => resolve();
    s.onerror = () => reject(new Error(`failed to load ${LOADER_URL}`));
    document.head.appendChild(s);
  });
}

@Component({
  selector: 'app-vtk-wasm-viewer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="wrap">
      <canvas id="vtk-wasm-canvas" width="720" height="540" tabindex="0"></canvas>
      <div class="status" [class.err]="error">{{ error || status }}</div>
    </div>
  `,
  styles: [`
    .wrap { display:flex; flex-direction:column; gap:8px; padding:12px; }
    canvas { width:720px; height:540px; display:block; background:#12121a; border-radius:6px; }
    .status { font:12px/1.4 monospace; color:#666; }
    .status.err { color:#c0392b; white-space:pre-wrap; }
  `],
})
export class VtkWasmViewerComponent implements AfterViewInit, OnDestroy {
  status = 'loading vtk.wasm loader…';
  error = '';
  private disposed = false;

  async ngAfterViewInit(): Promise<void> {
    try {
      const t0 = performance.now();
      await loadUmdLoader();
      if (!window.vtkwasm) throw new Error('vtkwasm global not available after loading the UMD loader');

      this.status = 'loading vtk.wasm bundle + FV grid…';
      const [runtime, grid] = await Promise.all([
        window.vtkwasm.loadAsync({ url: BUNDLE_URL }),
        fetch(GRID_URL).then((r) => r.json() as Promise<FvGrid>),
      ]);
      if (this.disposed) return;
      const vtk = runtime.createStandaloneSession().vtk;
      this.status = `bundle+grid loaded (${Math.round(performance.now() - t0)} ms) — building grid…`;

      // --- build the raw whole-voxel unstructured grid in-memory ---
      const points = vtk.vtkPoints();
      await points.setNumberOfPoints(grid.numPoints);
      const P = grid.points;
      for (let i = 0; i < grid.numPoints; i++) await points.setPoint(i, P[3 * i], P[3 * i + 1], P[3 * i + 2]);
      const cellArray = vtk.vtkCellArray();
      for (const cell of grid.cells) await cellArray.insertNextCell(cell.length, cell);
      const ug = vtk.vtkUnstructuredGrid();
      await ug.setPoints(points);
      await ug.setCells(grid.cellType, cellArray);   // single cell type (VTK_VOXEL)
      this.status = `grid built (${grid.numPoints} pts / ${grid.cells.length} voxels) — running pipeline…`;

      // --- FV smoothing pipeline: geometry (boundary surface) -> windowed-sinc ---
      const geom = vtk.vtkGeometryFilter();
      await geom.setInputData(ug);
      const sinc = vtk.vtkWindowedSincPolyDataFilter();
      await sinc.setInputConnection(await geom.getOutputPort());
      await sinc.setNumberOfIterations(grid.sinc.iterations);
      await sinc.boundarySmoothingOff();
      await sinc.featureEdgeSmoothingOff();
      await sinc.setFeatureAngle(grid.sinc.feature_angle);
      await sinc.setPassBand(grid.sinc.pass_band);
      await sinc.nonManifoldSmoothingOff();
      await sinc.normalizeCoordinatesOn();

      // --- render the smoothed membrane surface ---
      const mapper = vtk.vtkPolyDataMapper();
      await mapper.setInputConnection(await sinc.getOutputPort());
      const actor = vtk.vtkActor({ mapper });
      actor.property.color = [0.30, 0.65, 0.45];
      actor.property.opacity = 1.0;

      const renderer = vtk.vtkRenderer({ background: [0.07, 0.07, 0.1] });
      await renderer.addActor(actor);
      await renderer.resetCamera();
      const renderWindow = vtk.vtkRenderWindow({ canvasSelector: CANVAS_SELECTOR });
      await renderWindow.addRenderer(renderer);
      vtk.vtkRenderWindowInteractor({ canvasSelector: CANVAS_SELECTOR, renderWindow });
      await renderWindow.render();

      if (this.disposed) return;
      this.status = `rendered smoothed membrane ✓ (total ${Math.round(performance.now() - t0)} ms) — drag to rotate`;
    } catch (e: unknown) {
      this.error = 'vtk.wasm viewer failed: ' + ((e as Error)?.message ?? String(e));
      // eslint-disable-next-line no-console
      console.error('vtk-wasm-viewer', e);
    }
  }

  ngOnDestroy(): void {
    this.disposed = true;
  }
}
