import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * First render increment for the vtk.wasm field viewer (docs/salad-3d-renderer-design.md §8B).
 *
 * Proves the end-to-end path in webapp-ng: load the custom VTK-compiled-to-WebAssembly bundle
 * SAME-ORIGIN from /assets/vtk-wasm/ (the .tar.gz is placed there at build time by
 * scripts/fetch-vtk-wasm.mjs, fetched from the virtualcell/vcell-vtk-wasm release; the UMD loader is
 * copied there by angular.json assets), create a standalone session, and render through it (WebGL2).
 *
 * The loader is the UMD build injected as a <script> — the ESM entry's runtime does a dynamic
 * import() of the untar'd glue that webpack breaks; the UMD build isn't webpack-processed. A cone
 * here is a smoke test; the FV pipeline (in-memory unstructured grid → vtkThreshold → vtkGeometryFilter
 * → vtkWindowedSincPolyDataFilter, all constructable via the marshal-coverage patch) is next.
 */
const LOADER_URL = '/assets/vtk-wasm/vtk.umd.js';
const BUNDLE_URL = '/assets/vtk-wasm/vcell-vtk-wasm32-emscripten.tar.gz';
const CANVAS_SELECTOR = '#vtk-wasm-canvas';

function loadUmdLoader(): Promise<void> {
  if (window.vtkwasm) return Promise.resolve();
  return new Promise((resolve, reject) => {
    const existing = document.querySelector<HTMLScriptElement>(`script[data-vtk-wasm-loader]`);
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
      this.status = 'loading vtk.wasm bundle…';

      const runtime = await window.vtkwasm.loadAsync({ url: BUNDLE_URL });
      if (this.disposed) return;
      const vtk = runtime.createStandaloneSession().vtk;
      this.status = `bundle loaded (${Math.round(performance.now() - t0)} ms) — rendering…`;

      const cone = vtk.vtkConeSource({ resolution: 32 });
      const mapper = vtk.vtkPolyDataMapper();
      await mapper.setInputConnection(await cone.getOutputPort());
      const actor = vtk.vtkActor({ mapper });
      actor.property.color = [0.24, 0.62, 0.86];

      const renderer = vtk.vtkRenderer({ background: [0.07, 0.07, 0.1] });
      await renderer.addActor(actor);
      await renderer.resetCamera();

      const renderWindow = vtk.vtkRenderWindow({ canvasSelector: CANVAS_SELECTOR });
      await renderWindow.addRenderer(renderer);
      vtk.vtkRenderWindowInteractor({ canvasSelector: CANVAS_SELECTOR, renderWindow });
      await renderWindow.render();

      if (this.disposed) return;
      this.status = `rendered ✓ (total ${Math.round(performance.now() - t0)} ms) — drag to rotate`;
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
