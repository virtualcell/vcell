import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * vtk.wasm field viewer (docs/salad-3d-renderer-design.md §8B).
 *
 * Runs VCell's finite-volume surface-smoothing pipeline entirely CLIENT-SIDE in the browser:
 * build the raw whole-voxel unstructured grid in-memory from server-sent arrays → vtkGeometryFilter
 * (extract boundary surface) → vtkWindowedSincPolyDataFilter (the faithful FV smoothing) →
 * render via WebGL2. All filters are constructable in the standalone session via the
 * marshalling-coverage patch (virtualcell/vcell-vtk-wasm).
 *
 * The desktop client points this at its own loopback field server with ?base=&sim=&job=. The
 * viewer then drives itself: it asks the server what variables, domains and times exist and lets
 * the user move between them, rather than being pinned to whatever the Java results panel had
 * selected when the browser was opened. Geometry and field values come from separate endpoints
 * because the geometry does not change as you scrub time.
 *
 * The bundle is loaded same-origin from /assets/vtk-wasm/ (placed there at build time), via the UMD
 * loader injected as a <script> — the ESM entry's runtime import(blobUrl) is broken by webpack.
 */
const LOADER_URL = '/assets/vtk-wasm/vtk.umd.js';
const BUNDLE_URL = '/assets/vtk-wasm/vcell-vtk-wasm32-emscripten.tar.gz';
const FIXTURE_GRID_URL = '/assets/fv-sample-grid.json';
const CANVAS_SELECTOR = '#vtk-wasm-canvas';

/**
 * Smoothing slider (0-100). The default sits at NOMINAL_STRENGTH, which reproduces the server's
 * reference parameters exactly, so the viewer still matches the VisIt/pyvcell result out of the box.
 *
 * The slider exists because on a coarse mesh the reference parameters cannot remove the voxel
 * staircase without also eroding thin features — the step and the anatomy are the same spatial
 * frequency — so the right setting depends on what the user is looking at.
 */
const NOMINAL_STRENGTH = 20;
const MAX_ITERATIONS = 60;
const MIN_PASS_BAND = 0.005;

function formatPassBand(v: number): string {
  return v >= 0.01 ? v.toFixed(3) : v.toExponential(1);
}

interface Dataset {
  base: string;
  sim: string;
  job: string;
  variable: string | null;
  domain: string | null;
  time: string | null;
}

/**
 * The desktop client passes the dataset it registered, not a frozen snapshot URL, so the viewer can
 * choose variables and times for itself. Only loopback origins are accepted, so a crafted link
 * cannot turn this page into a fetcher for an arbitrary host.
 */
function datasetFromSearch(search: string): Dataset | null {
  const p = new URLSearchParams(search);
  const base = p.get('base');
  const sim = p.get('sim');
  if (!base || !sim) return null; // no dataset named → bundled fixture
  let parsed: URL;
  try {
    parsed = new URL(base);
  } catch {
    throw new Error(`'base' is not a valid URL: ${base}`);
  }
  const loopback = parsed.hostname === '127.0.0.1' || parsed.hostname === 'localhost' || parsed.hostname === '[::1]';
  if (parsed.protocol !== 'http:' || !loopback) {
    throw new Error(`'base' must be an http URL on the loopback interface, got ${parsed.origin}`);
  }
  return {
    base: parsed.origin,
    sim,
    job: p.get('job') ?? '0',
    variable: p.get('var'),
    domain: p.get('domain'),
    time: p.get('time'),
  };
}

interface FvInfo {
  simId: string;
  times: number[];
  domains: string[];
  variables: { name: string; domain: string; isFunction: boolean }[];
}

interface FvGeometry {
  geometryId: string;
  numPoints: number;
  points: number[];
  cellType: number; // 11 = VTK_VOXEL
  cells: number[][];
  domain?: string;
  sinc: { iterations: number; feature_angle: number; pass_band: number };
}

interface FvField {
  geometryId: string;
  name: string;
  domain: string;
  time: number;
  location: 'cell' | 'point';
  values: (number | null)[];
  range: [number, number];
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
      <!--
        vtk.wasm takes the canvas over unless told otherwise: it stamps position:absolute +
        width/height:100% on it and installs a resize observer. A positioned, sized box keeps the
        geometry predictable either way.
      -->
      <div class="canvas-box">
        <canvas id="vtk-wasm-canvas" tabindex="0"></canvas>
      </div>

      <div class="controls" *ngIf="dataset">
        <label for="variable">Variable</label>
        <select id="variable" [disabled]="!ready || busy"
                (change)="onVariableChange($any($event.target).value)">
          <option *ngFor="let v of variables" [value]="v.name" [selected]="v.name === selectedVar">
            {{ v.name }} · {{ v.domain }}
          </option>
        </select>
        <label for="time">Time</label>
        <input id="time" type="range" min="0" [max]="times.length - 1" step="1" [value]="timeIndex"
               [disabled]="!ready || busy || times.length < 2"
               (input)="previewTime($any($event.target).valueAsNumber)"
               (change)="onTimeChange($any($event.target).valueAsNumber)" />
        <span class="readout">t = {{ timeLabel }}<span *ngIf="rangeLabel"> · {{ rangeLabel }}</span></span>
      </div>

      <div class="controls">
        <label for="smoothing">Smoothing</label>
        <input id="smoothing" type="range" min="0" max="100" step="1"
               [value]="smoothing" [disabled]="!ready || busy"
               (input)="previewSmoothing($any($event.target).valueAsNumber)"
               (change)="applySmoothing($any($event.target).valueAsNumber)" />
        <span class="readout">
          {{ iterations }} iters &middot; pass-band {{ passBandLabel }}
          <em *ngIf="smoothing === nominalStrength">VCell nominal</em>
          <em *ngIf="smoothing === 0">off &mdash; raw voxel surface</em>
        </span>
        <button type="button" (click)="applySmoothing(nominalStrength)"
                [disabled]="!ready || busy || smoothing === nominalStrength">Reset</button>
      </div>

      <div class="status" [class.err]="error">{{ error || status }}</div>
    </div>
  `,
  styles: [`
    .wrap { display:flex; flex-direction:column; gap:8px; padding:12px; align-items:flex-start; }
    .canvas-box { position:relative; width:100%; max-width:960px; aspect-ratio:4/3;
                  background:#12121a; border-radius:6px; overflow:hidden; }
    .canvas-box canvas { display:block; width:100%; height:100%; cursor:grab; touch-action:none; }
    .canvas-box canvas:active { cursor:grabbing; }
    .controls { display:flex; align-items:center; gap:10px; font:12px/1.4 monospace; color:#444; width:100%; max-width:960px; }
    .controls input[type=range] { flex:0 0 200px; }
    .controls select { font:12px monospace; max-width:260px; }
    .readout { flex:1 1 auto; }
    .readout em { color:#2a7; font-style:normal; }
    .status { font:12px/1.4 monospace; color:#666; }
    .status.err { color:#c0392b; white-space:pre-wrap; }
  `],
})
export class VtkWasmViewerComponent implements AfterViewInit, OnDestroy {
  status = 'loading vtk.wasm loader…';
  error = '';
  ready = false;
  busy = false;

  dataset: Dataset | null = null;
  variables: { name: string; domain: string }[] = [];
  times: number[] = [];
  timeIndex = 0;
  selectedVar = '';
  selectedDomain = '';
  timeLabel = '';
  rangeLabel = '';

  /** Slider position whose parameters are exactly the server's (VCell reference) values. */
  readonly nominalStrength = NOMINAL_STRENGTH;
  smoothing = NOMINAL_STRENGTH;
  iterations = 0;
  passBandLabel = '';

  private disposed = false;
  /* eslint-disable @typescript-eslint/no-explicit-any */
  private vtk: any = null;
  private geomFilter: any = null;
  private sinc: any = null;
  private mapper: any = null;
  private actor: any = null;
  private renderer: any = null;
  private renderWindow: any = null;
  private camera: any = null;
  private fieldArray: any = null;
  /* eslint-enable @typescript-eslint/no-explicit-any */
  private geometryId = '';
  private nominalSinc: FvGeometry['sinc'] | null = null;
  private canvasEl: HTMLCanvasElement | null = null;
  private detachTrackball: (() => void) | null = null;
  private drawing = false;

  // ------------------------------------------------------------------
  // startup
  // ------------------------------------------------------------------

  async ngAfterViewInit(): Promise<void> {
    try {
      const t0 = performance.now();
      await loadUmdLoader();
      if (!window.vtkwasm) throw new Error('vtkwasm global not available after loading the UMD loader');

      this.dataset = datasetFromSearch(window.location.search);
      this.canvasEl = document.querySelector<HTMLCanvasElement>(CANVAS_SELECTOR);
      this.status = 'loading vtk.wasm bundle…';

      const runtime = await window.vtkwasm.loadAsync({ url: BUNDLE_URL });
      if (this.disposed) return;
      const session = runtime.createStandaloneSession();
      this.vtk = session.vtk;
      // vtk.wasm otherwise takes the canvas over (stamps position:absolute + 100%/100% on it and
      // installs a resize observer that pins the drawing buffer back to its default). The loader's
      // own RemoteSession turns both off; the standalone session does not, so do it here.
      try {
        session.wasmModule?._setDefaultExpandVTKCanvasToContainer?.(false);
        session.wasmModule?._setDefaultInstallHTMLResizeObserver?.(false);
      } catch (e) {
        // eslint-disable-next-line no-console
        console.warn('vtk-wasm-viewer: could not disable the canvas takeover', e);
      }

      const { geometry, field } = await this.loadDataset();
      if (this.disposed) return;

      await this.buildScene(geometry, field);
      if (this.disposed) return;
      this.ready = true;
      this.status = `rendered ${this.describe()} ✓ (${Math.round(performance.now() - t0)} ms) — drag to rotate, wheel to zoom`;
    } catch (e: unknown) {
      this.error = 'vtk.wasm viewer failed: ' + ((e as Error)?.message ?? String(e));
      // eslint-disable-next-line no-console
      console.error('vtk-wasm-viewer', e);
    }
  }

  ngOnDestroy(): void {
    this.disposed = true;
    this.detachTrackball?.();
    this.detachTrackball = null;
  }

  // ------------------------------------------------------------------
  // data
  // ------------------------------------------------------------------

  private url(path: string, params: Record<string, string>): string {
    const d = this.dataset!;
    const q = new URLSearchParams({ sim: d.sim, job: d.job, ...params });
    return `${d.base}${path}?${q}`;
  }

  private async fetchJson<T>(url: string, what: string): Promise<T> {
    const r = await fetch(url);
    if (!r.ok) throw new Error(`${what} failed: ${r.status} ${r.statusText}`);
    return (await r.json()) as T;
  }

  /** Fixture mode has no server to ask, so it renders one static grid with no field. */
  private async loadDataset(): Promise<{ geometry: FvGeometry; field: FvField | null }> {
    if (!this.dataset) {
      this.status = 'loading bundled fixture…';
      const geometry = await this.fetchJson<FvGeometry>(FIXTURE_GRID_URL, 'fixture');
      return { geometry, field: null };
    }

    this.status = 'asking the server what this run contains…';
    const info = await this.fetchJson<FvInfo>(this.url('/info', {}), '/info');
    this.times = info.times ?? [];
    this.variables = (info.variables ?? []).map((v) => ({ name: v.name, domain: v.domain }));
    if (!this.variables.length) throw new Error(`run ${info.simId} exposes no volume variables`);

    const requested = this.dataset.variable;
    const chosen = (requested ? this.variables.find((v) => v.name === requested) : undefined) ?? this.variables[0];
    this.selectedVar = chosen.name;
    this.selectedDomain = this.dataset.domain ?? chosen.domain;
    this.timeIndex = this.nearestTimeIndex(this.dataset.time ? Number(this.dataset.time) : this.times[this.times.length - 1]);

    const geometry = await this.loadGeometry();
    const field = await this.loadField();
    return { geometry, field };
  }

  private nearestTimeIndex(t: number): number {
    if (!this.times.length) return 0;
    let best = 0;
    for (let i = 1; i < this.times.length; i++) {
      if (Math.abs(this.times[i] - t) < Math.abs(this.times[best] - t)) best = i;
    }
    return best;
  }

  private async loadGeometry(): Promise<FvGeometry> {
    this.status = `loading geometry for ${this.selectedDomain}…`;
    const geometry = await this.fetchJson<FvGeometry>(
      this.url('/grid', { domain: this.selectedDomain }), '/grid');
    this.geometryId = geometry.geometryId;
    return geometry;
  }

  private async loadField(): Promise<FvField> {
    const time = this.times[this.timeIndex];
    const field = await this.fetchJson<FvField>(
      this.url('/field', { domain: this.selectedDomain, var: this.selectedVar, time: String(time) }), '/field');
    // values are only meaningful against the geometry they were computed for; pairing them with a
    // different one would draw something silently wrong rather than obviously broken
    if (this.geometryId && field.geometryId !== this.geometryId) {
      throw new Error(`field is for geometry ${field.geometryId} but the view holds ${this.geometryId}`);
    }
    this.timeLabel = String(field.time);
    this.rangeLabel = `[${field.range[0].toExponential(2)}, ${field.range[1].toExponential(2)}]`;
    return field;
  }

  private describe(): string {
    if (!this.dataset) return 'bundled fixture';
    return `${this.selectedVar} @ t=${this.timeLabel} on ${this.selectedDomain} — range ${this.rangeLabel}`;
  }

  // ------------------------------------------------------------------
  // controls
  // ------------------------------------------------------------------

  /** Move the readout while dragging; the refetch waits for release. */
  previewTime(index: number): void {
    this.timeIndex = index;
    this.timeLabel = String(this.times[index] ?? '');
  }

  async onTimeChange(index: number): Promise<void> {
    this.previewTime(index);
    await this.refreshField();
  }

  /**
   * Switching variable can also switch domain — a nuclear species lives on different geometry from
   * a cytosolic one — in which case the grid is rebuilt, not just recoloured.
   */
  async onVariableChange(name: string): Promise<void> {
    const chosen = this.variables.find((v) => v.name === name);
    if (!chosen || this.busy) return;
    this.selectedVar = chosen.name;
    if (chosen.domain && chosen.domain !== this.selectedDomain) {
      this.selectedDomain = chosen.domain;
      await this.rebuildGeometry();
    } else {
      await this.refreshField();
    }
  }

  private async refreshField(): Promise<void> {
    if (!this.ready || this.busy || !this.dataset) return;
    this.busy = true;
    const t0 = performance.now();
    try {
      const field = await this.loadField();
      await this.applyField(field);
      await this.renderWindow?.render();
      this.status = `${this.describe()} ✓ (${Math.round(performance.now() - t0)} ms)`;
    } catch (e: unknown) {
      this.error = 'field update failed: ' + ((e as Error)?.message ?? String(e));
    } finally {
      this.busy = false;
    }
  }

  private async rebuildGeometry(): Promise<void> {
    if (!this.ready || this.busy || !this.dataset) return;
    this.busy = true;
    const t0 = performance.now();
    try {
      const geometry = await this.loadGeometry();
      const field = await this.loadField();
      await this.buildGrid(geometry, field);
      await this.renderer?.resetCamera();
      await this.renderWindow?.render();
      this.status = `${this.describe()} ✓ (${Math.round(performance.now() - t0)} ms)`;
    } catch (e: unknown) {
      this.error = 'geometry update failed: ' + ((e as Error)?.message ?? String(e));
    } finally {
      this.busy = false;
    }
  }

  // ------------------------------------------------------------------
  // vtk scene
  // ------------------------------------------------------------------

  /** Builds the in-memory grid and points the pipeline at it. Re-run when the domain changes. */
  private async buildGrid(geometry: FvGeometry, field: FvField | null): Promise<void> {
    const vtk = this.vtk;
    const points = vtk.vtkPoints();
    await points.setNumberOfPoints(geometry.numPoints);
    const P = geometry.points;
    for (let i = 0; i < geometry.numPoints; i++) await points.setPoint(i, P[3 * i], P[3 * i + 1], P[3 * i + 2]);
    const cellArray = vtk.vtkCellArray();
    for (const cell of geometry.cells) await cellArray.insertNextCell(cell.length, cell);
    const ug = vtk.vtkUnstructuredGrid();
    await ug.setPoints(points);
    await ug.setCells(geometry.cellType, cellArray); // single cell type (VTK_VOXEL)

    this.fieldArray = null;
    if (field) {
      const arr = vtk.vtkDoubleArray();
      await arr.setName(field.name);
      await arr.setNumberOfComponents(1);
      await arr.setNumberOfTuples(field.values.length);
      // NB: setValue() is NOT in the marshalled invoker whitelist ("SetValue is not permitted");
      // setTuple1(i, v) is the permitted per-element scalar setter in the standalone session.
      for (let i = 0; i < field.values.length; i++) await arr.setTuple1(i, field.values[i] ?? 0);
      await (await ug.getCellData()).setScalars(arr);
      this.fieldArray = arr;
    }
    await this.geomFilter.setInputData(ug);

    if (this.mapper) {
      if (field) {
        await this.mapper.setScalarModeToUseCellData();
        await this.mapper.scalarVisibilityOn(); // no-arg form; the boolean setter marshals awkwardly
        await this.mapper.setScalarRange(field.range[0], field.range[1]);
      } else if (this.actor) {
        // no field → solid surface (via the property method; actor.property.color is a no-op)
        await (await this.actor.getProperty()).setColor(0.30, 0.65, 0.45);
      }
    }
    this.nominalSinc = geometry.sinc;
    this.previewSmoothing(this.smoothing);
  }

  /** Same geometry, new values: rewrite the scalars in place rather than rebuilding the grid. */
  private async applyField(field: FvField): Promise<void> {
    if (!this.fieldArray) return;
    await this.fieldArray.setName(field.name);
    await this.fieldArray.setNumberOfTuples(field.values.length);
    for (let i = 0; i < field.values.length; i++) await this.fieldArray.setTuple1(i, field.values[i] ?? 0);
    await this.fieldArray.modified();
    await this.mapper?.setScalarRange(field.range[0], field.range[1]);
  }

  private async buildScene(geometry: FvGeometry, field: FvField | null): Promise<void> {
    const vtk = this.vtk;
    this.geomFilter = vtk.vtkGeometryFilter();

    const sinc = vtk.vtkWindowedSincPolyDataFilter();
    this.sinc = sinc;
    await sinc.setInputConnection(await this.geomFilter.getOutputPort());
    await sinc.boundarySmoothingOff();
    await sinc.featureEdgeSmoothingOff();
    await sinc.nonManifoldSmoothingOff();
    await sinc.normalizeCoordinatesOn();
    await sinc.setNumberOfIterations(geometry.sinc.iterations);
    await sinc.setFeatureAngle(geometry.sinc.feature_angle);
    await sinc.setPassBand(geometry.sinc.pass_band);

    // Compute surface normals so the mapper shades smoothly (Gouraud) instead of flat/faceted.
    let surfacePort = await sinc.getOutputPort();
    try {
      const normals = vtk.vtkPolyDataNormals();
      await normals.setInputConnection(surfacePort);
      await normals.setFeatureAngle(60);
      surfacePort = await normals.getOutputPort();
    } catch { /* normals filter unavailable in the session — fall back to flat shading */ }

    this.mapper = vtk.vtkPolyDataMapper();
    await this.mapper.setInputConnection(surfacePort);
    this.actor = vtk.vtkActor({ mapper: this.mapper });

    await this.buildGrid(geometry, field);

    this.renderer = vtk.vtkRenderer({ background: [0.07, 0.07, 0.1] });
    await this.renderer.addActor(this.actor);
    await this.renderer.resetCamera();
    this.renderWindow = vtk.vtkRenderWindow({ canvasSelector: CANVAS_SELECTOR });
    await this.renderWindow.addRenderer(this.renderer);
    await this.matchBufferToCanvas();
    // NOTE: vtkRenderWindowInteractor is deliberately NOT used. It binds DOM listeners and accepts
    // a trackball style, but nothing ever pumps it: the event loop (startEventLoop) exists only on
    // vtkRemoteSession, not on the standalone session we run. Interaction is driven directly
    // against the camera below.
    this.camera = await this.renderer.getActiveCamera();
    this.attachTrackball();
    await this.renderWindow.render();
  }

  /**
   * Size the drawing buffer to the canvas's displayed size, measuring the BOX rather than the
   * canvas: vtk stretches the canvas to fill the box only once it takes it over, so reading the
   * canvas here yields its 300x150 default and locks the buffer to that.
   */
  private async matchBufferToCanvas(): Promise<void> {
    const box = document.querySelector<HTMLElement>('.canvas-box');
    if (!box || !this.renderWindow) return;
    const dpr = window.devicePixelRatio || 1;
    const w = Math.max(1, Math.round(box.clientWidth * dpr));
    const h = Math.max(1, Math.round(box.clientHeight * dpr));
    if (w <= 1 || h <= 1) return;
    try {
      await this.renderWindow.setSize(w, h);
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('vtk-wasm-viewer: renderWindow.setSize failed, buffer stays at its default', e);
    }
  }

  // ------------------------------------------------------------------
  // interaction
  // ------------------------------------------------------------------

  private attachTrackball(): void {
    const canvas = this.canvasEl;
    if (!canvas || this.detachTrackball) return;
    let dragging = false;
    let lastX = 0;
    let lastY = 0;

    const down = (e: PointerEvent) => {
      if (e.button !== 0) return;
      dragging = true;
      lastX = e.clientX;
      lastY = e.clientY;
      canvas.setPointerCapture(e.pointerId);
      e.preventDefault();
    };
    const move = (e: PointerEvent) => {
      if (!dragging) return;
      const dx = e.clientX - lastX;
      const dy = e.clientY - lastY;
      lastX = e.clientX;
      lastY = e.clientY;
      if (dx || dy) void this.orbit(dx, dy);
      e.preventDefault();
    };
    const up = (e: PointerEvent) => {
      if (!dragging) return;
      dragging = false;
      try { canvas.releasePointerCapture(e.pointerId); } catch { /* already released */ }
    };
    const wheel = (e: WheelEvent) => {
      void this.dolly(e.deltaY < 0 ? 1.1 : 1 / 1.1);
      e.preventDefault();
    };

    canvas.addEventListener('pointerdown', down);
    canvas.addEventListener('pointermove', move);
    canvas.addEventListener('pointerup', up);
    canvas.addEventListener('pointercancel', up);
    canvas.addEventListener('wheel', wheel, { passive: false });
    this.detachTrackball = () => {
      canvas.removeEventListener('pointerdown', down);
      canvas.removeEventListener('pointermove', move);
      canvas.removeEventListener('pointerup', up);
      canvas.removeEventListener('pointercancel', up);
      canvas.removeEventListener('wheel', wheel);
    };
  }

  private async orbit(dx: number, dy: number): Promise<void> {
    if (!this.camera || this.drawing) return;
    this.drawing = true;
    try {
      await this.camera.azimuth(-dx * 0.5);
      await this.camera.elevation(dy * 0.5);
      await this.camera.orthogonalizeViewUp();
      await this.renderer?.resetCameraClippingRange();
      await this.renderWindow?.render();
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('vtk-wasm-viewer: orbit failed', e);
    } finally {
      this.drawing = false;
    }
  }

  private async dolly(factor: number): Promise<void> {
    if (!this.camera || this.drawing) return;
    this.drawing = true;
    try {
      await this.camera.dolly(factor);
      await this.renderer?.resetCameraClippingRange();
      await this.renderWindow?.render();
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('vtk-wasm-viewer: dolly failed', e);
    } finally {
      this.drawing = false;
    }
  }

  // ------------------------------------------------------------------
  // smoothing
  // ------------------------------------------------------------------

  /**
   * Maps the slider to filter parameters, anchored so the default position reproduces the server's
   * reference values exactly.
   *
   * Below nominal we only reduce the iteration count (0 iterations is an exact pass-through). Above
   * nominal we add iterations and narrow the pass-band geometrically. The pass-band is never raised
   * above nominal: WindowedSinc diverges as it approaches 1.0 (measured — a pass-band of 1.0
   * displaced points by 26 units where the reference moves 0.45).
   */
  private smoothingFor(strength: number): { iterations: number; passBand: number } {
    const nom = this.nominalSinc ?? { iterations: 12, feature_angle: 120, pass_band: 0.05 };
    if (strength <= NOMINAL_STRENGTH) {
      const f = strength / NOMINAL_STRENGTH;
      return { iterations: Math.round(nom.iterations * f), passBand: nom.pass_band };
    }
    const f = (strength - NOMINAL_STRENGTH) / (100 - NOMINAL_STRENGTH);
    const maxIters = Math.max(MAX_ITERATIONS, nom.iterations);
    const minPass = Math.min(MIN_PASS_BAND, nom.pass_band);
    return {
      iterations: Math.round(nom.iterations + f * (maxIters - nom.iterations)),
      passBand: nom.pass_band * Math.pow(minPass / nom.pass_band, f),
    };
  }

  /** Update the readout while dragging, without re-running the filter. */
  previewSmoothing(strength: number): void {
    this.smoothing = strength;
    const p = this.smoothingFor(strength);
    this.iterations = p.iterations;
    this.passBandLabel = formatPassBand(p.passBand);
  }

  /** Re-run the smoother and redraw. Bound to (change), so it fires on release, not per-pixel. */
  async applySmoothing(strength: number): Promise<void> {
    this.previewSmoothing(strength);
    if (!this.ready || this.busy || !this.sinc || !this.renderWindow) return;
    this.busy = true;
    const t0 = performance.now();
    try {
      const p = this.smoothingFor(strength);
      await this.sinc.setNumberOfIterations(p.iterations);
      await this.sinc.setPassBand(p.passBand);
      await this.sinc.update();
      await this.renderWindow.render();
      this.status = `smoothing: ${p.iterations} iters · pass-band ${this.passBandLabel} ✓ (${Math.round(performance.now() - t0)} ms)`;
    } catch (e: unknown) {
      this.error = 'smoothing update failed: ' + ((e as Error)?.message ?? String(e));
    } finally {
      this.busy = false;
    }
  }
}
