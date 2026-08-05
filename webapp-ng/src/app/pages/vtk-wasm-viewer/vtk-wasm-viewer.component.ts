import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * vtk.wasm field viewer (docs/salad-3d-renderer-design.md §8B).
 *
 * Runs VCell's finite-volume surface-smoothing pipeline entirely CLIENT-SIDE in the browser:
 * build the raw whole-voxel unstructured grid in-memory from server-sent arrays (from the desktop
 * client's loopback server via ?src=, else a bundled fixture) → vtkGeometryFilter (extract boundary surface) →
 * vtkWindowedSincPolyDataFilter (the faithful FV smoothing: iters 12, feature angle 120°,
 * pass-band 0.05) → render via WebGL2. All filters are constructable in the standalone session via
 * the marshalling-coverage patch (virtualcell/vcell-vtk-wasm).
 *
 * The bundle is loaded same-origin from /assets/vtk-wasm/ (placed there at build time), via the UMD
 * loader injected as a <script> — the ESM entry's runtime import(blobUrl) is broken by webpack.
 */
const LOADER_URL = '/assets/vtk-wasm/vtk.umd.js';
const BUNDLE_URL = '/assets/vtk-wasm/vcell-vtk-wasm32-emscripten.tar.gz';
const FIXTURE_GRID_URL = '/assets/fv-sample-grid.json';
const CANVAS_SELECTOR = '#vtk-wasm-canvas';

/**
 * The desktop client passes ?src=<url> pointing at its own loopback field server
 * (FieldViewerServer in vcell-client); without it we fall back to the bundled fixture.
 *
 * Only loopback origins are accepted, so a crafted link cannot turn this page into a fetcher
 * for an arbitrary host.
 */
function resolveGridUrl(search: string): string {
  const src = new URLSearchParams(search).get('src');
  if (!src) return FIXTURE_GRID_URL;
  let parsed: URL;
  try {
    parsed = new URL(src);
  } catch {
    throw new Error(`'src' is not a valid URL: ${src}`);
  }
  const loopback = parsed.hostname === '127.0.0.1' || parsed.hostname === 'localhost' || parsed.hostname === '[::1]';
  if (parsed.protocol !== 'http:' || !loopback) {
    throw new Error(`'src' must be an http URL on the loopback interface, got ${parsed.origin}`);
  }
  return parsed.href;
}

interface FvGrid {
  numPoints: number;
  points: number[];
  cellType: number;         // 11 = VTK_VOXEL
  cells: number[][];        // per-cell point-id lists
  field?: { name: string; location: 'cell' | 'point'; values: number[]; range: [number, number] };
  domain?: string;
  time?: number;
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

      const gridUrl = resolveGridUrl(window.location.search);
      this.status = 'loading vtk.wasm bundle + FV grid…';
      const [runtime, grid] = await Promise.all([
        window.vtkwasm.loadAsync({ url: BUNDLE_URL }),
        fetch(gridUrl).then((r) => {
          if (!r.ok) throw new Error(`grid fetch failed: ${r.status} ${r.statusText} from ${gridUrl}`);
          return r.json() as Promise<FvGrid>;
        }),
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

      // Attach the field (e.g. a species concentration) as cell scalars — vtkGeometryFilter carries
      // cell data through to the extracted surface, so the membrane is colored by the field.
      if (grid.field) {
        const arr = vtk.vtkDoubleArray();
        await arr.setName(grid.field.name);
        await arr.setNumberOfComponents(1);
        await arr.setNumberOfTuples(grid.field.values.length);
        // NB: setValue() is NOT in the marshalled invoker whitelist ("SetValue is not permitted");
        // setTuple1(i, v) is the permitted per-element scalar setter in the standalone session.
        for (let i = 0; i < grid.field.values.length; i++) await arr.setTuple1(i, grid.field.values[i]);
        await (await ug.getCellData()).setScalars(arr);
      }
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
      // Compute surface normals so the mapper shades smoothly (Gouraud) instead of flat/faceted.
      let surfacePort = await sinc.getOutputPort();
      try {
        const normals = vtk.vtkPolyDataNormals();
        await normals.setInputConnection(surfacePort);
        await normals.setFeatureAngle(60);
        surfacePort = await normals.getOutputPort();
      } catch { /* normals filter unavailable in the session — fall back to flat shading */ }

      const mapper = vtk.vtkPolyDataMapper();
      await mapper.setInputConnection(surfacePort);
      if (grid.field) {
        // colormap by the field's cell scalars (default blue→red LUT over the field range)
        await mapper.setScalarModeToUseCellData();
        await mapper.scalarVisibilityOn();   // no-arg form; setScalarVisibility(true) marshals awkwardly
        await mapper.setScalarRange(grid.field.range[0], grid.field.range[1]);
      }
      const actor = vtk.vtkActor({ mapper });
      if (!grid.field) {
        // no field → solid surface (set color via the property method; actor.property.color is a no-op)
        await (await actor.getProperty()).setColor(0.30, 0.65, 0.45);
      }

      const renderer = vtk.vtkRenderer({ background: [0.07, 0.07, 0.1] });
      await renderer.addActor(actor);
      await renderer.resetCamera();
      const renderWindow = vtk.vtkRenderWindow({ canvasSelector: CANVAS_SELECTOR });
      await renderWindow.addRenderer(renderer);
      vtk.vtkRenderWindowInteractor({ canvasSelector: CANVAS_SELECTOR, renderWindow });
      await renderWindow.render();

      if (this.disposed) return;
      const label = grid.field
        ? `${grid.field.name} @ t=${grid.time} on ${grid.domain} — range [${grid.field.range[0].toExponential(2)}, ${grid.field.range[1].toExponential(2)}]`
        : 'smoothed membrane';
      this.status = `rendered ${label} ✓ (${Math.round(performance.now() - t0)} ms) — drag to rotate`;
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
