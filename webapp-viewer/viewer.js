/**
 * VCell field viewer — see docs/3d-renderer-design.md.
 *
 * Implements VCell's finite-volume DISPLAY CONVENTION entirely client-side: build the raw
 * whole-voxel unstructured grid in memory from server-sent arrays, extract its boundary
 * (vtkGeometryFilter, passing original point ids), smooth the boundary vertices
 * (vtkWindowedSincPolyDataFilter, the faithful FV smoothing), then write the displaced positions
 * BACK INTO THE GRID (vtkVCellDeformGridToSurface, our filter in the custom bundle) — yielding
 * ONE unstructured mesh whose boundary cells are distorted hexahedra reaching the smoothed
 * surface. The shell, the crop/cut face (vtkTableBasedClipDataSet) and the display-mesh
 * statistics (vtkIntegrateAttributes) all derive from that one mesh, so geometry, mesh and
 * statistics tell one consistent story. Solver-grid statistics (/stats, uniform voxel volumes)
 * remain the reference analysis family; the two converge as the mesh refines. Rendered via
 * WebGL2 through the custom VTK-to-WebAssembly bundle (virtualcell/vcell-vtk-wasm >= v1.2.0).
 *
 * Deliberately framework-free. The desktop client serves this page from its own loopback field
 * server, so page and data share an origin, and it must work with no network beyond that server.
 *
 * Point it at a dataset with ?sim=<key>&job=<n>, optionally &var=&domain=&time= for the initial
 * selection, and &base=<url> if the data lives somewhere other than this page's own origin.
 */
const BUNDLE_URL = 'assets/vtk-wasm/vcell-vtk-wasm32-emscripten.tar.gz';

/**
 * Smoothing slider (0-100). The default sits at NOMINAL_STRENGTH, which reproduces the server's
 * reference parameters exactly, so the viewer matches the VisIt/pyvcell result out of the box.
 *
 * The slider exists because on a coarse mesh the reference parameters cannot remove the voxel
 * staircase without also eroding thin features — the step and the anatomy are the same spatial
 * frequency — so the right setting depends on what the user is looking at.
 */
const NOMINAL_STRENGTH = 20;
const MAX_ITERATIONS = 60;
const MIN_PASS_BAND = 0.005;

const el = {
  canvas: document.getElementById('canvas'),
  box: document.querySelector('.canvas-box'),
  overlay: document.getElementById('overlay'),
  runTitle: document.getElementById('runTitle'),
  runName: document.getElementById('runName'),
  runId: document.getElementById('runId'),
  colorbar: document.getElementById('colorbar'),
  axes: document.getElementById('axes'),
  meshStyle: document.getElementById('meshStyle'),
  sliceAxis: document.getElementById('sliceAxis'),
  cutMode: document.getElementById('cutMode'),
  slicePos: document.getElementById('slicePos'),
  sliceReadout: document.getElementById('sliceReadout'),
  cropStats: document.getElementById('cropStats'),
  pickReadout: document.getElementById('pickReadout'),
  plotPanel: document.getElementById('plotPanel'),
  plotTitle: document.getElementById('plotTitle'),
  plotLegend: document.getElementById('plotLegend'),
  plotSvg: document.getElementById('plotSvg'),
  refreshBtn: document.getElementById('refreshBtn'),
  runStatus: document.getElementById('runStatus'),
  plotClose: document.getElementById('plotClose'),
  addProbes: document.getElementById('addProbes'),
  probePanel: document.getElementById('probePanel'),
  probeTitle: document.getElementById('probeTitle'),
  probeList: document.getElementById('probeList'),
  probeSvg: document.getElementById('probeSvg'),
  probeCsv: document.getElementById('probeCsv'),
  probeClear: document.getElementById('probeClear'),
  statsBtn: document.getElementById('statsBtn'),
  lineTool: document.getElementById('lineTool'),
  kymoPanel: document.getElementById('kymoPanel'),
  kymoTitle: document.getElementById('kymoTitle'),
  kymoStale: document.getElementById('kymoStale'),
  kymoRange: document.getElementById('kymoRange'),
  kymoMin: document.getElementById('kymoMin'),
  kymoMax: document.getElementById('kymoMax'),
  kymoSamplesCsv: document.getElementById('kymoSamplesCsv'),
  kymoMatrixCsv: document.getElementById('kymoMatrixCsv'),
  kymoDesktopCsv: document.getElementById('kymoDesktopCsv'),
  kymoPng: document.getElementById('kymoPng'),
  kymoClose: document.getElementById('kymoClose'),
  lineCoords: document.getElementById('lineCoords'),
  kymoNote: document.getElementById('kymoNote'),
  kymoPlot: document.getElementById('kymoPlot'),
  kymoCanvas: document.getElementById('kymoCanvas'),
  kymoSvg: document.getElementById('kymoSvg'),
  kymoReadout: document.getElementById('kymoReadout'),
  kymoProfile: document.getElementById('kymoProfile'),
  dataControls: document.getElementById('dataControls'),
  variable: document.getElementById('variable'),
  time: document.getElementById('time'),
  dataReadout: document.getElementById('dataReadout'),
  smoothing: document.getElementById('smoothing'),
  smoothingReadout: document.getElementById('smoothingReadout'),
  smoothingReset: document.getElementById('smoothingReset'),
  status: document.getElementById('status'),
};

const state = {
  dataset: null,
  variables: [],
  times: [],
  timeIndex: 0,
  runStatus: null, // FEniCSx bundles: 'running' | 'completed' | 'failed' (from /info); null for other runs
  solver: null, // /info's solver: 'FEniCSx', 'Chombo' or 'MovingBoundary'; null for a finite-volume run
  runProgress: null, // fraction 0..1 while running
  selectedVar: '',
  selectedDomain: '',
  geometryId: '',
  dimension: 3,
  bodyFitted: false, // MovingBoundary runs: solver's body-fitted mesh, geometry varies per time
  membraneMesh: false, // the grid is a finite-volume membrane's faces (a membrane variable): drawn as is, no smoothing
  // 'cell' (finite-volume data: one value per cell) or 'point' (FEniCSx P1 data: one value per mesh
  // vertex, interpolated across each cell); set by the field the server sends
  fieldLocation: 'cell',
  bounds: null,
  sliceAxis: -1,
  slicePos: 50,
  cutMode: 'smooth', // 'smooth' (clip through the cells) or 'cells' (keep whole cells)
  cellPoints: null, // the grid's point coordinates and cells, for choosing the whole cells to keep
  cellList: null,
  cellType: null, // the grid's one VTK cell type, or …
  cellTypes: null, // … one per cell (Chombo 3D: voxels and polyhedra)
  cellFaces: null, // a polyhedron's faces, [numFaces, numPoints, ids…, …], null at other cells
  cellPlanes: null, // the 3D pick's per-cell face planes and boxes (cellPlanes())
  pivot: null, // 3D rotation center: the scene center; a pan does not move it (null: take the focal point)
  pick: null, // Cartesian occupancy index of the current grid, for mouse picking
  // probes: lab-frame points, each with a time course. Points, not cell ordinals, so they survive a
  // variable, domain or (moving-boundary) mesh change: {id, label, point: [x, y, z], color}
  probes: [],
  addMode: false, // the '+ Add points' toggle: a plain click adds a probe, as shift-click does
  probeSeries: null, // the last /timeseries?points= response, for the plot, the list and the CSV
  probePlot: null, // the probe plot's time scale and cursor, for moving the cursor with the slider
  cameraView: null, // the camera as last rendered, for projecting probe markers onto the canvas
  // the kymograph's line (docs/plan-plotting.md §4.2): lab-frame vertices, like the probes, so it survives
  // a variable or domain switch. One line at a time, as on the desktop.
  line: null, // {vertices: [[x, y, z], …]}
  lineDraft: null, // the vertices placed so far while the Line tool is on, else null
  kymo: null, // the last /kymograph response, with the line it was computed for and the stride asked
  kymoStale: false, // the run has new times since the kymograph was computed
  kymoView: null, // the drawn kymograph's layout, for the time cursor, clicks and hover
  fieldRange: null, // the last /field range: the kymograph's "3D view" colour range
  fieldValues: null, // raw per-cell values of the shown field (nulls = blanked cells)
  nominalSinc: null,
  smoothing: NOMINAL_STRENGTH,
  ready: false,
  busy: false,
  drawing: false,
};

// vtk handles, kept so controls can update the scene without rebuilding it
let vtk = null;
let surfSource = null; // raw-boundary extractor feeding the smoother (passThroughPointIds on)
let sinc = null;
let deform = null; // vtkVCellDeformGridToSurface: writes the sinc'd points back into the grid
let tableClip = null; // the cut plane, when active: clips the DEFORMED grid (or the raw body-fitted mesh)
let currentUg = null; // the body-fitted solver mesh, for rewiring geomFilter when the crop toggles
let geomFilter = null; // display boundary extractor (deformed or clipped-deformed grid)
let integ = null; // vtkIntegrateAttributes for display-mesh statistics (null if unavailable)
let mapper = null;
let actor = null;
let renderer = null;
let renderWindow = null;
let camera = null;
let fieldArray = null;
let lut = null;
let scalarBar = null;
let cubeAxes = null;
let clipPlane = null;
let extractCells = null; // the whole-cells cut: vtkExtractCells on the same input as tableClip

const setStatus = (text, isError = false) => {
  el.status.textContent = text;
  el.status.classList.toggle('err', isError);
};

// ---------------------------------------------------------------------------
// dataset
// ---------------------------------------------------------------------------

/**
 * The client passes the dataset it registered, not a frozen snapshot, so the viewer can choose
 * variables and times for itself. Only loopback origins are accepted, so a crafted link cannot turn
 * this page into a fetcher for an arbitrary host.
 */
function datasetFromSearch(search) {
  const p = new URLSearchParams(search);
  const sim = p.get('sim');
  if (!sim) return null;
  // served by the field server itself, so its own origin is the data source unless told otherwise
  const base = p.get('base') ?? window.location.origin;
  let parsed;
  try {
    parsed = new URL(base);
  } catch {
    throw new Error(`'base' is not a valid URL: ${base}`);
  }
  const loopback = ['127.0.0.1', 'localhost', '[::1]'].includes(parsed.hostname);
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

const url = (path, params) =>
  `${state.dataset.base}${path}?${new URLSearchParams({ sim: state.dataset.sim, job: state.dataset.job, ...params })}`;

async function fetchJson(u, what) {
  const r = await fetch(u);
  if (!r.ok) throw new Error(`${what} failed: ${r.status} ${r.statusText}`);
  return r.json();
}

/**
 * Label the window and the page with what this run IS — several viewers tile on one screen, and
 * an unlabeled one cannot be told from its neighbours. The name travels via /info rather than the
 * URL because the server holds it for the dataset's lifetime, not just for the opening click.
 * textContent, not innerHTML: the name is user-authored.
 */
function showRunTitle(info) {
  const job = Number(info.jobIndex ?? state.dataset.job);
  const name = (info.simName ?? info.simId) + (job > 0 ? ` · job ${job}` : '');
  el.runName.textContent = name;
  el.runId.textContent = info.simName ? info.simId : '';
  el.runTitle.hidden = false;
  document.title = `${name} — VCell 3D`;
}

async function loadInfo() {
  setStatus('asking the server what this run contains…');
  const info = await fetchJson(url('/info', {}), '/info');
  showRunTitle(info);
  state.solver = info.solver ?? null;
  state.times = info.times ?? [];
  showRunStatus(info);
  state.variables = (info.variables ?? []).map((v) => ({ name: v.name, domain: v.domain }));
  if (!state.variables.length) throw new Error(`run ${info.simId} exposes no volume variables`);

  const requested = state.dataset.variable;
  // a domain asked for without a variable opens that domain's first variable, not one from another domain
  const chosen = (requested ? state.variables.find((v) => v.name === requested) : null)
    ?? (state.dataset.domain ? state.variables.find((v) => v.domain === state.dataset.domain) : null)
    ?? state.variables[0];
  state.selectedVar = chosen.name;
  state.selectedDomain = state.dataset.domain ?? chosen.domain;
  state.timeIndex = nearestTimeIndex(
    state.dataset.time ? Number(state.dataset.time) : state.times[state.times.length - 1]);

  el.variable.innerHTML = '';
  for (const v of state.variables) {
    const opt = document.createElement('option');
    opt.value = v.name;
    opt.textContent = `${v.name} · ${v.domain}`;
    opt.selected = v.name === state.selectedVar;
    el.variable.appendChild(opt);
  }
  el.time.max = String(Math.max(0, state.times.length - 1));
  el.time.value = String(state.timeIndex);
  el.dataControls.hidden = false;
}

/** The run's status next to the time controls; while it is running the viewer refreshes itself. */
function showRunStatus(info) {
  const wasRunning = state.runStatus === 'running';
  state.runStatus = info.status ?? null;
  state.runProgress = Number.isFinite(info.progress) ? info.progress : null;
  const times = `${state.times.length} time${state.times.length === 1 ? '' : 's'}`;
  if (state.runStatus === 'running') {
    const pct = state.runProgress == null ? '' : ` ${Math.round(100 * state.runProgress)}%`;
    el.runStatus.textContent = `running${pct} · ${times} · auto-refresh`;
  } else if (state.runStatus === 'failed') {
    el.runStatus.textContent = `run failed · ${times}`;
  } else {
    // a run seen finishing says so; one that was already complete when the page opened needs no label
    el.runStatus.textContent = wasRunning && state.runStatus === 'completed' ? `completed · ${times}` : '';
  }
}

function nearestTimeIndex(t) {
  let best = 0;
  for (let i = 1; i < state.times.length; i++) {
    if (Math.abs(state.times[i] - t) < Math.abs(state.times[best] - t)) best = i;
  }
  return best;
}

async function loadGeometry() {
  setStatus(`loading geometry for ${state.selectedDomain}…`);
  // ALWAYS send the time: on the very first load the client cannot yet know the run is
  // body-fitted (that flag arrives IN this response), and without a time the server defaults a
  // body-fitted run's geometry to its last frame while the first /field honors the requested
  // initial time — the exact mismatch seen live ("field is for ...@t0 but the view holds
  // ...@tN"). Cartesian runs ignore the parameter.
  const t = state.times[state.timeIndex];
  const geometry = await fetchJson(url('/grid', {
    domain: state.selectedDomain,
    ...(t !== undefined ? { time: String(t) } : {}),
  }), '/grid');
  state.geometryId = geometry.geometryId;
  return geometry;
}

async function loadField(allowMismatch = false) {
  const time = state.times[state.timeIndex];
  const field = await fetchJson(
    url('/field', { domain: state.selectedDomain, var: state.selectedVar, time: String(time) }), '/field');
  // values are only meaningful against the geometry they were computed for; pairing them with a
  // different one would draw something silently wrong rather than obviously broken. The
  // body-fitted scrub path passes allowMismatch and treats a mismatch as "rebuild the geometry".
  if (!allowMismatch && state.geometryId && field.geometryId !== state.geometryId) {
    throw new Error(`field is for geometry ${field.geometryId} but the view holds ${state.geometryId}`);
  }
  el.dataReadout.textContent =
    `t = ${field.time} · [${field.range[0].toExponential(2)}, ${field.range[1].toExponential(2)}]`;
  state.fieldRange = field.range;
  if (state.kymo && el.kymoRange.value === 'view') renderKymograph();
  return field;
}

const describe = () =>
  state.dataset
    ? `${state.selectedVar} @ ${el.dataReadout.textContent} on ${state.selectedDomain}`
    : 'bundled sample';

// ---------------------------------------------------------------------------
// scene
// ---------------------------------------------------------------------------

/**
 * Point the mapper, the lookup table and the color bar at one field together. The scalar bar
 * labels the LUT's range — mapper.setScalarRange alone never reaches it, and a bar labelling
 * [0,1] under a differently-colored surface is silently wrong, so the two update as one.
 */
async function applyFieldRange(field) {
  await mapper.setScalarRange(field.range[0], field.range[1]);
  if (lut) {
    await lut.setTableRange(field.range[0], field.range[1]);
    await lut.build();
  }
  if (scalarBar) await scalarBar.setTitle(field.name);
}

/**
 * Cartesian occupancy index for mouse picking, built once per geometry. The voxels are
 * axis-aligned boxes on a regular lattice, so a pick is a 3D-DDA walk along the mouse ray —
 * a few dozen lattice steps — not a test against every cell. Resolved entirely in JS: the
 * browser already holds the grid and the field, so no round trip and no picker object.
 */
function buildPickIndex(geometry, bounds) {
  const P = geometry.points;
  const first = geometry.cells[0];
  if (!first) return null;
  let mins = [Infinity, Infinity, Infinity];
  let maxs = [-Infinity, -Infinity, -Infinity];
  for (const pi of first) {
    for (let a = 0; a < 3; a++) {
      mins[a] = Math.min(mins[a], P[3 * pi + a]);
      maxs[a] = Math.max(maxs[a], P[3 * pi + a]);
    }
  }
  const d = [maxs[0] - mins[0], maxs[1] - mins[1], maxs[2] - mins[2]];
  for (let a = 0; a < 3; a++) {
    if (!(d[a] > 0)) d[a] = 1; // a 2D grid is flat along one axis: one unit-thick layer
  }
  const o = [bounds[0], bounds[2], bounds[4]];
  const n = [0, 1, 2].map((a) => Math.max(1, Math.round((bounds[2 * a + 1] - bounds[2 * a]) / d[a])));
  const occ = new Map();
  const keyByCell = new Array(geometry.cells.length);
  for (let c = 0; c < geometry.cells.length; c++) {
    let cmin = [Infinity, Infinity, Infinity];
    for (const pi of geometry.cells[c]) {
      for (let a = 0; a < 3; a++) cmin[a] = Math.min(cmin[a], P[3 * pi + a]);
    }
    const i = [0, 1, 2].map((a) => Math.round((cmin[a] - o[a]) / d[a]));
    const key = i[0] + n[0] * (i[1] + n[1] * i[2]);
    occ.set(key, c);
    keyByCell[c] = key;
  }
  return { o, d, n, occ, keyByCell };
}

/**
 * The first cell along the given ray, honoring the crop (cells above an active cut are not pickable), as
 * {cell, t}: t is the ray parameter where the ray enters it — at the cut plane when that is the face it
 * shows. Null when the ray misses.
 */
function castRay(origin, dir) {
  const pk = state.pick;
  if (!pk) return null;
  const b = state.bounds;
  // slab-clip the ray to the grid bounds
  let t0 = 0;
  let t1 = Infinity;
  for (let a = 0; a < 3; a++) {
    if (Math.abs(dir[a]) < 1e-12) {
      if (origin[a] < b[2 * a] || origin[a] > b[2 * a + 1]) return null;
      continue;
    }
    let near = (b[2 * a] - origin[a]) / dir[a];
    let far = (b[2 * a + 1] - origin[a]) / dir[a];
    if (near > far) [near, far] = [far, near];
    t0 = Math.max(t0, near);
    t1 = Math.min(t1, far);
  }
  if (t0 > t1) return null;
  // crop plane: same keep-rule as the renderer (low side of the sliced axis survives)
  let cropAxis = -1;
  let cropPos = 0;
  if (state.sliceAxis >= 0) {
    cropAxis = state.sliceAxis;
    const frac = 0.005 + 0.99 * (state.slicePos / 100);
    cropPos = b[2 * cropAxis] + frac * (b[2 * cropAxis + 1] - b[2 * cropAxis]);
  }
  // Amanatides & Woo lattice walk
  const eps = 1e-9;
  const start = [0, 1, 2].map((a) => origin[a] + (t0 + eps) * dir[a]);
  const i = [0, 1, 2].map((a) =>
    Math.min(pk.n[a] - 1, Math.max(0, Math.floor((start[a] - pk.o[a]) / pk.d[a]))));
  const step = dir.map((v) => (v > 0 ? 1 : -1));
  const tDelta = [0, 1, 2].map((a) => Math.abs(pk.d[a] / (Math.abs(dir[a]) < 1e-12 ? Infinity : dir[a])));
  const tMax = [0, 1, 2].map((a) => {
    if (Math.abs(dir[a]) < 1e-12) return Infinity;
    const edge = pk.o[a] + (i[a] + (step[a] > 0 ? 1 : 0)) * pk.d[a];
    return t0 + (edge - start[a]) / dir[a];
  });
  let tEnter = t0;
  for (let guard = pk.n[0] + pk.n[1] + pk.n[2] + 3; guard > 0; guard--) {
    const cell = pk.occ.get(i[0] + pk.n[0] * (i[1] + pk.n[1] * i[2]));
    if (cell !== undefined) {
      if (cropAxis < 0) return { cell, t: tEnter };
      const center = pk.o[cropAxis] + (i[cropAxis] + 0.5) * pk.d[cropAxis];
      if (center <= cropPos) {
        // a voxel the cut passes through shows its cut face, not the part of it the crop removed
        const t = origin[cropAxis] + tEnter * dir[cropAxis] > cropPos && dir[cropAxis] < 0
          ? (cropPos - origin[cropAxis]) / dir[cropAxis] : tEnter;
        return { cell, t };
      }
    }
    const a = tMax[0] <= tMax[1] ? (tMax[0] <= tMax[2] ? 0 : 2) : (tMax[1] <= tMax[2] ? 1 : 2);
    i[a] += step[a];
    if (i[a] < 0 || i[a] >= pk.n[a] || tMax[a] > t1) return null;
    tEnter = tMax[a];
    tMax[a] += tDelta[a];
  }
  return null;
}

/**
 * Mouse position → world-space ray through the camera. Perspective (vertical FOV) for 3D;
 * parallel projection for the 2D top-down camera, where the ray origin shifts in-plane and the
 * direction is the constant view direction.
 */
async function rayFromMouse(clientX, clientY) {
  const rect = el.canvas.getBoundingClientRect();
  const xN = ((clientX - rect.left) / rect.width) * 2 - 1;
  const yN = 1 - ((clientY - rect.top) / rect.height) * 2;
  const P = await camera.getPosition();
  const F = await camera.getFocalPoint();
  const U = await camera.getViewUp();
  const norm = (v) => {
    const l = Math.hypot(v[0], v[1], v[2]) || 1;
    return [v[0] / l, v[1] / l, v[2] / l];
  };
  const cross = (u, v) => [u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]];
  const fwd = norm([F[0] - P[0], F[1] - P[1], F[2] - P[2]]);
  const right = norm(cross(fwd, U));
  const up = cross(right, fwd);
  const aspect = rect.width / rect.height;
  if (state.dimension === 2) {
    const ps = await camera.getParallelScale();
    const origin = [0, 1, 2].map((a) =>
      P[a] + right[a] * xN * aspect * ps + up[a] * yN * ps);
    return { origin, dir: fwd };
  }
  const halfTan = Math.tan(((await camera.getViewAngle()) * Math.PI) / 360);
  const dir = norm([0, 1, 2].map((a) =>
    fwd[a] + right[a] * xN * aspect * halfTan + up[a] * yN * halfTan));
  return { origin: P, dir };
}

/** Axis-aligned bounds of the raw grid — the axes box frames the data, not the smoothed surface. */
function boundsOf(geometry) {
  const b = [Infinity, -Infinity, Infinity, -Infinity, Infinity, -Infinity];
  const P = geometry.points;
  for (let i = 0; i < geometry.numPoints; i++) {
    for (let a = 0; a < 3; a++) {
      const v = P[3 * i + a];
      if (v < b[2 * a]) b[2 * a] = v;
      if (v > b[2 * a + 1]) b[2 * a + 1] = v;
    }
  }
  return b;
}

/** Builds the in-memory grid and points the pipeline at it. Re-run when the domain changes. */
async function buildGrid(geometry, field) {
  state.dimension = geometry.dimension ?? 3;
  // a finite-volume membrane variable lives on its membrane's faces (quads in 3D, segments in 2D): that mesh is
  // drawn as it is, as a body-fitted one is, not through the voxel grid's smoothing and deform
  state.membraneMesh = !state.bodyFitted && !!geometry.membrane;
  const points = vtk.vtkPoints();
  await points.setNumberOfPoints(geometry.numPoints);
  const P = geometry.points;
  for (let i = 0; i < geometry.numPoints; i++) await points.setPoint(i, P[3 * i], P[3 * i + 1], P[3 * i + 2]);
  const ug = vtk.vtkUnstructuredGrid();
  await ug.setPoints(points);
  state.cellPoints = P;
  state.cellList = geometry.cells;
  state.cellType = geometry.cellType;
  state.cellTypes = geometry.cellTypes ?? null;
  state.cellFaces = geometry.cellFaces ?? null;
  state.cellPlanes = null; // the pick's per-cell planes, rebuilt for this grid on first use
  if (geometry.cellTypes) {
    // mixed cell types (Chombo: whole voxels + the polyhedra it cuts at the boundary)
    const faces = geometry.cellFaces;
    if (faces?.some(Boolean)) await ug.initializeFacesRepresentation(0);
    for (let c = 0; c < geometry.cells.length; c++) {
      const cell = geometry.cells[c];
      const face = faces?.[c];
      if (face) {
        // a polyhedron is inserted with its faces: [numFaces, numPoints, ids…] — the count is
        // passed separately, so the stream itself starts at the first face
        await ug.insertNextCell(geometry.cellTypes[c], cell.length, cell, face[0], face.slice(1));
      } else {
        await ug.insertNextCell(geometry.cellTypes[c], cell.length, cell);
      }
    }
  } else {
    const cellArray = vtk.vtkCellArray();
    for (const cell of geometry.cells) await cellArray.insertNextCell(cell.length, cell);
    await ug.setCells(geometry.cellType, cellArray); // single cell type
  }

  fieldArray = null;
  if (field) {
    const arr = vtk.vtkDoubleArray();
    await arr.setName(field.name);
    await arr.setNumberOfComponents(1);
    await arr.setNumberOfTuples(field.values.length);
    // NB: setValue() is NOT in the marshalled invoker whitelist ("SetValue is not permitted");
    // setTuple1(i, v) is the permitted per-element scalar setter in the standalone session.
    for (let i = 0; i < field.values.length; i++) await arr.setTuple1(i, field.values[i] ?? 0);
    state.fieldLocation = field.location === 'point' ? 'point' : 'cell';
    await (await (state.fieldLocation === 'point' ? ug.getPointData() : ug.getCellData())).setScalars(arr);
    fieldArray = arr;
  }
  if (state.bodyFitted || state.membraneMesh) {
    // body-fitted: the mesh IS the solver's geometry — no smoothing, no deform; the crop clips
    // this mesh directly, so a cut exposes the solver's own interior cells (voxels and cut tets).
    // A finite-volume membrane's faces likewise
    currentUg = ug;
    await tableClip.setInputData(ug);
    await extractCells.setInputData(ug);
    await geomFilter.setInputData(ug);
  } else {
    // (back from a membrane variable: the crop and the whole-cells cut take the deformed grid again)
    await tableClip.setInputConnection(await deform.getOutputPort());
    await extractCells.setInputConnection(await deform.getOutputPort());
    // the raw grid feeds the smoothing chain and the deform filter; everything the user sees
    // (shell, crop, cut face) derives from the ONE deformed grid downstream of them
    await surfSource.setInputData(ug);
    await deform.setInputData(ug);
  }

  if (field) {
    // point data is interpolated across each cell (Gouraud), which is what a P1 solution means
    if (state.fieldLocation === 'point') await mapper.setScalarModeToUsePointData();
    else await mapper.setScalarModeToUseCellData();
    await mapper.scalarVisibilityOn(); // no-arg form; the boolean setter marshals awkwardly
    await applyFieldRange(field);
  } else {
    // no field → solid surface (via the property method; actor.property.color is a no-op)
    await (await actor.getProperty()).setColor(0.30, 0.65, 0.45);
  }
  state.bounds = boundsOf(geometry);
  state.pick = state.membraneMesh ? null : buildPickIndex(geometry, state.bounds); // the voxel walk's index
  state.fieldValues = field ? field.values : null;
  if (cubeAxes) await cubeAxes.setBounds(...state.bounds);
  await applyCrop(); // a domain switch changes the bounds the slider position maps into
  state.nominalSinc = geometry.sinc;
  previewSmoothing(state.smoothing);
  if (state.ready) {
    updateLineTool();
    updateSmoothingControls();
  }
}

/**
 * Position the cut plane and crop the volume at it. The clip runs on the DEFORMED grid — the one
 * mesh everything derives from — so the exposed cross-section is flat, its rim lies on the
 * smoothed surface because the boundary cells themselves reach it (distorted hexahedra, VCell's
 * FV display convention), and every cut polygon carries its cell's value. No cap, no trim, no
 * second actor: the shell and the cut face are one boundary of one clipped mesh.
 */
async function applyCrop() {
  if (!tableClip) return;
  const axis = state.sliceAxis;
  if (axis < 0) {
    if (state.bodyFitted || state.membraneMesh) {
      if (currentUg) await geomFilter.setInputData(currentUg);
    } else {
      await geomFilter.setInputConnection(await deform.getOutputPort());
    }
    el.sliceReadout.textContent = '';
    el.cropStats.textContent = '';
    return;
  }
  const b = state.bounds;
  // inset from the ends: a plane exactly on the outermost faces clips a degenerate sliver
  const frac = 0.005 + 0.99 * (state.slicePos / 100);
  const pos = b[2 * axis] + frac * (b[2 * axis + 1] - b[2 * axis]);
  const origin = [(b[0] + b[1]) / 2, (b[2] + b[3]) / 2, (b[4] + b[5]) / 2];
  origin[axis] = pos;
  // the clip keeps the side where the plane function is positive: -axis keeps everything below
  // the cut, so the slider sweeps from almost-nothing (0) to the whole volume (100)
  const normal = [0, 0, 0];
  normal[axis] = -1;
  await clipPlane.setOrigin(...origin);
  await clipPlane.setNormal(...normal);
  if (state.cutMode === 'cells') {
    await setWholeCells(axis, pos);
    await geomFilter.setInputConnection(await extractCells.getOutputPort());
  } else {
    await geomFilter.setInputConnection(await tableClip.getOutputPort());
  }
  el.sliceReadout.textContent = `${'xyz'[axis]} = ${pos.toFixed(2)}`;
}

/**
 * The whole-cells cut: keep every cell with a vertex on the kept (low) side of the plane, so the cells
 * the plane passes through stay intact and the cut face is made of the mesh's own faces — a staircase
 * that shows the elements as they are, where the smooth clip shows the polygons a plane slices out of
 * them (arbitrary triangles and quadrilaterals, often long and thin even in a good mesh). The selection
 * uses the grid's own coordinates; for voxel data the deform moves only boundary vertices, and only
 * slightly, so the choice of cells is the same.
 */
async function setWholeCells(axis, pos) {
  const P = state.cellPoints;
  const runs = [];
  state.cellList.forEach((cell, c) => {
    if (!cell.some((v) => P[3 * v + axis] <= pos)) return;
    const last = runs[runs.length - 1];
    if (last && last[1] === c - 1) last[1] = c;
    else runs.push([c, c]);
  });
  await extractCells.setCellList(vtk.vtkIdList()); // clears the previous selection
  for (const [first, last] of runs) await extractCells.addCellRange(first, last);
}

/** Re-crop and re-render as the slider drags; in-flight guard, as for orbit. */
let cropInFlight = false;
async function refreshCrop() {
  if (!state.ready || cropInFlight) return;
  cropInFlight = true;
  try {
    await applyCrop();
    await render();
    await updateCropStats();
  } catch (e) {
    setStatus('crop update failed: ' + (e?.message ?? e), true);
  } finally {
    cropInFlight = false;
  }
}

/**
 * Display-mesh statistics of the cropped region (#1859 discussion): min/max from the clipped
 * mesh's scalar range, volume-weighted mean from vtkIntegrateAttributes with
 * DivideAllCellDataByVolume — integrated over the DEFORMED mesh, so the numbers are
 * self-consistent with the geometry on screen and labeled as such. The solver-grid statistics
 * (/stats, uniform voxel volumes, the solver's own bookkeeping) remain the reference family;
 * near membranes the two differ by design and converge as the mesh refines.
 */
async function updateCropStats() {
  if (!integ || state.sliceAxis < 0) return;
  try {
    await tableClip.update();
    const clipped = await tableClip.getOutput();
    const pointData = state.fieldLocation === 'point';
    const scalars = await (await (pointData ? clipped.getPointData() : clipped.getCellData())).getScalars();
    const range = await scalars.getRange();
    await integ.update();
    const integratedOutput = await integ.getOutput();
    // integrated point data lands in the output's point data; "Volume" is always cell data
    const integrated = await (pointData ? integratedOutput.getPointData() : integratedOutput.getCellData());
    const volumeData = await integratedOutput.getCellData();
    // integrated scalars are ∫f dV; "Volume" is ∫dV of the clipped smoothed mesh
    const sumArr = (await integrated.getScalars()) ?? (await integrated.getArray(state.selectedVar));
    const volArr = await volumeData.getArray('Volume');
    const integral = sumArr ? await sumArr.getTuple1(0) : null;
    const volume = volArr ? await volArr.getTuple1(0) : null;
    const mean = integral != null && volume ? integral / volume : null;
    const p = smoothingFor(state.smoothing);
    el.cropStats.textContent =
      `display-mesh (cropped): min ${range[0].toExponential(3)} · `
      + (mean != null && Number.isFinite(mean) ? `mean ${mean.toExponential(3)} · ` : '')
      + `max ${range[1].toExponential(3)}`
      + (volume != null && Number.isFinite(volume) ? ` · volume ${volume.toExponential(3)}` : '')
      + (state.bodyFitted ? ' (body-fitted solver mesh)' : state.membraneMesh ? ' (membrane faces)'
        : ` (smoothing ${p.iterations} iters · pass-band ${formatPassBand(p.passBand)})`);
  } catch (e) {
    console.warn('display-mesh stats failed', e);
    el.cropStats.textContent = '';
  }
}

/** Same geometry, new values: rewrite the scalars in place rather than rebuilding the grid. */
async function applyField(field) {
  if (!fieldArray) return;
  state.fieldValues = field.values;
  await fieldArray.setName(field.name);
  await fieldArray.setNumberOfTuples(field.values.length);
  for (let i = 0; i < field.values.length; i++) await fieldArray.setTuple1(i, field.values[i] ?? 0);
  await fieldArray.modified();
  await applyFieldRange(field);
}

async function buildScene(geometry, field) {
  // The deformed-grid convention (VCell FV display): extract the raw boundary, smooth its
  // vertices, then write the displaced positions BACK INTO THE VOLUME GRID — boundary cells
  // become distorted hexahedra reaching the smoothed surface, interior cells stay regular, and
  // every downstream operation (shell, crop, cut face) derives from that one mesh.
  surfSource = vtk.vtkGeometryFilter();
  await surfSource.passThroughPointIdsOn(); // the write-back addresses grid points by original id

  sinc = vtk.vtkWindowedSincPolyDataFilter();
  await sinc.setInputConnection(await surfSource.getOutputPort());
  state.dimension = geometry.dimension ?? 3;
  state.bodyFitted = !!geometry.bodyFitted;
  if (state.dimension === 2) {
    // 2D: the quads ARE the display and the mesh's boundary edge is the domain outline — exactly
    // what the convention smooths. A flat regular interior is already a Laplacian fixed point,
    // so with boundary smoothing ON only the outline relaxes and the interior stays put.
    await sinc.boundarySmoothingOn();
  } else {
    await sinc.boundarySmoothingOff();
  }
  await sinc.featureEdgeSmoothingOff();
  await sinc.nonManifoldSmoothingOff();
  await sinc.normalizeCoordinatesOn();
  if (geometry.sinc) {
    await sinc.setNumberOfIterations(geometry.sinc.iterations);
    await sinc.setFeatureAngle(geometry.sinc.feature_angle);
    await sinc.setPassBand(geometry.sinc.pass_band);
  }

  deform = vtk.vtkVCellDeformGridToSurface(); // our custom filter; bundle >= v1.2.0
  await deform.setSourceConnection(await sinc.getOutputPort());

  clipPlane = vtk.vtkPlane();
  tableClip = vtk.vtkTableBasedClipDataSet();
  await tableClip.setClipFunction(clipPlane);
  await tableClip.setInputConnection(await deform.getOutputPort());
  extractCells = vtk.vtkExtractCells();
  await extractCells.setInputConnection(await deform.getOutputPort());

  // display boundary extractor; applyCrop points it at the deformed grid or its clipped half
  geomFilter = vtk.vtkGeometryFilter();

  // display-mesh statistics of the cropped region; degrade quietly if the bundle lacks the filter
  try {
    integ = vtk.vtkIntegrateAttributes();
    await integ.setInputConnection(await tableClip.getOutputPort());
    // NB: DivideAllCellDataByVolume is a plain vtkSetMacro (no On/Off variants) and boolean
    // setters marshal awkwardly — left at its default (off), so the integrated scalars are
    // ∫f dV and the mean is computed in JS from them and the "Volume" array
  } catch (e) {
    integ = null;
    console.warn('vtkIntegrateAttributes unavailable — display-mesh statistics disabled', e);
  }

  // Surface normals so the mapper shades smoothly (Gouraud) instead of flat/faceted.
  let surfacePort = await geomFilter.getOutputPort();
  try {
    const normals = vtk.vtkPolyDataNormals();
    await normals.setInputConnection(surfacePort);
    await normals.setFeatureAngle(60);
    surfacePort = await normals.getOutputPort();
  } catch { /* normals filter unavailable in the session — fall back to flat shading */ }

  mapper = vtk.vtkPolyDataMapper();
  await mapper.setInputConnection(surfacePort);
  // Own the lookup table rather than borrowing the mapper's implicit one, so the surface and the
  // color bar read the same table and cannot disagree about what color means what value.
  lut = vtk.vtkLookupTable();
  // low→high as blue→red, the direction the desktop results viewer already taught users;
  // VTK's default rainbow runs the other way
  await lut.setHueRange(0.66667, 0.0);
  await mapper.setLookupTable(lut);
  await mapper.useLookupTableScalarRangeOn(); // no-arg form, as for scalarVisibilityOn
  actor = vtk.vtkActor({ mapper });

  scalarBar = vtk.vtkScalarBarActor();
  await scalarBar.setLookupTable(lut);
  // the default bar spans most of the viewport and auto-scales its text to match — huge; keep it
  // a slim strip on the right edge
  await scalarBar.setWidth(0.08);
  await scalarBar.setHeight(0.72);
  await scalarBar.setPosition(0.9, 0.14);

  await buildGrid(geometry, field);

  renderer = vtk.vtkRenderer({ background: [0.07, 0.07, 0.1] });
  await renderer.addActor(actor);
  await renderer.addViewProp(scalarBar); // addActor2D is refused by the invoker; addViewProp is the route
  await renderer.resetCamera();
  renderWindow = vtk.vtkRenderWindow({ canvasSelector: '#canvas' });
  await renderWindow.addRenderer(renderer);
  await matchBufferToCanvas();
  // NOTE: vtkRenderWindowInteractor is deliberately NOT used. It binds DOM listeners and accepts a
  // trackball style, but nothing ever pumps it: the event loop (startEventLoop) exists only on
  // vtkRemoteSession, not on the standalone session we run. Interaction is driven directly against
  // the camera below.
  camera = await renderer.getActiveCamera();
  if (state.dimension === 2) {
    // top-down orthographic view of the z=0 plane; interaction becomes pan/zoom (no orbit)
    await camera.parallelProjectionOn();
    const sliceRow = el.sliceAxis.closest('.controls');
    if (sliceRow) sliceRow.hidden = true;
  }
  // The axes box needs the camera: its labels and fly-to-a-corner behaviour track the view. That
  // is exactly what an HTML fallback cannot fake, which is why this actor earns its keep.
  cubeAxes = vtk.vtkCubeAxesActor();
  await cubeAxes.setBounds(...state.bounds);
  await cubeAxes.setCamera(camera);
  await cubeAxes.setXTitle('X');
  await cubeAxes.setYTitle('Y');
  await cubeAxes.setZTitle('Z');
  await renderer.addViewProp(cubeAxes);
  attachTrackball();
  await render();
}

/**
 * Every render goes through here, so the probe markers follow what VTK draws: after the frame, cache the
 * camera it was drawn with, re-project the markers from it, and (debounced, since it casts rays) re-check
 * which markers the geometry hides.
 */
async function render() {
  await renderWindow.render();
  try {
    await cacheCameraView();
    drawOverlay();
    scheduleOcclusion();
  } catch (e) {
    console.warn('probe overlay update failed', e);
  }
}

/**
 * Size the drawing buffer to the canvas's displayed size, measuring the BOX rather than the canvas:
 * vtk stretches the canvas to fill the box only once it takes it over, so reading the canvas here
 * yields its 300x150 default and locks the buffer to that.
 */
async function matchBufferToCanvas() {
  if (!renderWindow || !el.box) return;
  const dpr = window.devicePixelRatio || 1;
  const w = Math.max(1, Math.round(el.box.clientWidth * dpr));
  const h = Math.max(1, Math.round(el.box.clientHeight * dpr));
  if (w <= 1 || h <= 1) return;
  try {
    await renderWindow.setSize(w, h);
  } catch (e) {
    console.warn('renderWindow.setSize failed, buffer stays at its default', e);
  }
}

/**
 * Re-sync the buffer when the window resizes. The box's viewport-height clamp means resizing can
 * change its aspect ratio, and a buffer left at the old shape would draw stretched. Debounced:
 * setSize allocates a new buffer, far too heavy to run per resize event.
 */
let resizeTimer = 0;
window.addEventListener('resize', () => {
  clearTimeout(resizeTimer);
  resizeTimer = setTimeout(async () => {
    try {
      await matchBufferToCanvas();
      if (renderWindow) await render();
      if (state.kymo) renderKymograph();
    } catch (e) {
      console.warn('resize re-render failed', e);
    }
  }, 150);
});

// ---------------------------------------------------------------------------
// interaction
// ---------------------------------------------------------------------------

function attachTrackball() {
  let dragging = false;
  let lastX = 0;
  let lastY = 0;
  let dragDistance = 0;
  let panning = false; // 3D: shift-, right- or middle-drag pans; a plain left drag orbits
  el.canvas.addEventListener('contextmenu', (e) => e.preventDefault()); // right-drag pans, no menu
  el.canvas.addEventListener('pointerdown', (e) => {
    if (e.button !== 0 && e.button !== 1 && e.button !== 2) return;
    dragging = true; lastX = e.clientX; lastY = e.clientY; dragDistance = 0;
    panning = e.button !== 0 || e.shiftKey;
    el.canvas.setPointerCapture(e.pointerId);
    e.preventDefault();
  });
  el.canvas.addEventListener('pointermove', (e) => {
    if (!dragging) {
      void hoverPick(e.clientX, e.clientY);
      return;
    }
    const dx = e.clientX - lastX;
    const dy = e.clientY - lastY;
    lastX = e.clientX; lastY = e.clientY;
    dragDistance += Math.abs(dx) + Math.abs(dy);
    if (dx || dy) void (state.dimension === 2 ? pan2d(dx, dy) : panning ? pan3d(dx, dy) : orbit(dx, dy));
    e.preventDefault();
  });
  const release = (e) => {
    if (!dragging) return;
    dragging = false;
    try { el.canvas.releasePointerCapture(e.pointerId); } catch { /* already released */ }
    // a left press that never really moved is a pick: with the Line tool on it places a vertex; otherwise
    // shift (or the Add toggle) adds a probe, and a plain click replaces them
    if (dragDistance < 4 && e.button === 0) {
      if (state.lineDraft) void addLineVertex(e.clientX, e.clientY);
      else void probeAt(e.clientX, e.clientY, e.shiftKey || state.addMode);
    }
  };
  // a double-click finishes a line (its two clicks have placed its last vertex, once: repeats are dropped)
  el.canvas.addEventListener('dblclick', (e) => {
    if (!state.lineDraft) return;
    e.preventDefault();
    lineVertexQueue = lineVertexQueue.then(() => finishLine());
  });
  el.canvas.addEventListener('pointerup', release);
  el.canvas.addEventListener('pointercancel', release);
  el.canvas.addEventListener('pointerleave', () => { el.pickReadout.textContent = ''; });
  el.canvas.addEventListener('wheel', (e) => {
    const f = e.deltaY < 0 ? 1.1 : 1 / 1.1;
    void (state.dimension === 2 ? zoom2d(f) : dolly(f));
    e.preventDefault();
  }, { passive: false });
}

/** Rodrigues: v rotated by `degrees` about the unit axis k. */
function rotateAbout(v, k, degrees) {
  const t = (degrees * Math.PI) / 180;
  const c = Math.cos(t);
  const s = Math.sin(t);
  const kv = k[0] * v[0] + k[1] * v[1] + k[2] * v[2];
  const kx = [k[1] * v[2] - k[2] * v[1], k[2] * v[0] - k[0] * v[2], k[0] * v[1] - k[1] * v[0]];
  return [0, 1, 2].map((a) => v[a] * c + kx[a] * s + k[a] * kv * (1 - c));
}

/**
 * 3D drag: orbit the camera about the pivot — the scene center, which a pan leaves where it is — so a
 * panned object still turns in place rather than about the middle of the screen. The same motion as
 * vtkCamera azimuth (about the view up) then elevation (about the view's right axis), but centered on
 * the pivot instead of the focal point; with no pan the two coincide.
 */
async function orbit(dx, dy) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    const P = await camera.getPosition();
    const F = await camera.getFocalPoint();
    let U = await camera.getViewUp();
    if (!state.pivot) state.pivot = F;
    const C = state.pivot;
    const unit = (v) => {
      const l = Math.hypot(v[0], v[1], v[2]) || 1;
      return [v[0] / l, v[1] / l, v[2] / l];
    };
    const around = (p, axis, deg) => {
      const r = rotateAbout([p[0] - C[0], p[1] - C[1], p[2] - C[2]], axis, deg);
      return [C[0] + r[0], C[1] + r[1], C[2] + r[2]];
    };
    // azimuth: about the view up
    const up = unit(U);
    let p = around(P, up, -dx * 0.5);
    let f = around(F, up, -dx * 0.5);
    // elevation: about the right axis of the turned view
    const fwd = [f[0] - p[0], f[1] - p[1], f[2] - p[2]];
    const right = unit([fwd[1] * up[2] - fwd[2] * up[1], fwd[2] * up[0] - fwd[0] * up[2], fwd[0] * up[1] - fwd[1] * up[0]]);
    p = around(p, right, -dy * 0.5);
    f = around(f, right, -dy * 0.5);
    U = rotateAbout(up, right, -dy * 0.5);
    await camera.setPosition(...p);
    await camera.setFocalPoint(...f);
    await camera.setViewUp(...U);
    await camera.orthogonalizeViewUp();
    await renderer.resetCameraClippingRange();
    await render();
  } catch (e) {
    console.warn('orbit failed', e);
  } finally {
    state.drawing = false;
  }
}

async function dolly(factor) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    await camera.dolly(factor);
    await renderer.resetCameraClippingRange();
    await render();
  } catch (e) {
    console.warn('dolly failed', e);
  } finally {
    state.drawing = false;
  }
}

/**
 * 3D pan: slide the camera and its focal point together across the view plane, scaled so the scene
 * at the focal distance moves with the cursor (the perspective frustum's height there spans the canvas).
 */
async function pan3d(dx, dy) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    const rect = el.canvas.getBoundingClientRect();
    const P = await camera.getPosition();
    const F = await camera.getFocalPoint();
    const U = await camera.getViewUp();
    if (!state.pivot) state.pivot = F; // the rotation center stays on the scene, not the screen
    const fwd = [F[0] - P[0], F[1] - P[1], F[2] - P[2]];
    const distance = Math.hypot(...fwd);
    const right = [fwd[1] * U[2] - fwd[2] * U[1], fwd[2] * U[0] - fwd[0] * U[2], fwd[0] * U[1] - fwd[1] * U[0]];
    const rl = Math.hypot(...right) || 1;
    const up = [
      (right[1] * fwd[2] - right[2] * fwd[1]) / (rl * distance),
      (right[2] * fwd[0] - right[0] * fwd[2]) / (rl * distance),
      (right[0] * fwd[1] - right[1] * fwd[0]) / (rl * distance),
    ];
    const worldPerPixel = (2 * distance * Math.tan(((await camera.getViewAngle()) * Math.PI) / 360)) / rect.height;
    // the scene follows the cursor, so the camera moves the other way (screen y grows downward)
    const m = [0, 1, 2].map((a) => (-dx * right[a] / rl + dy * up[a]) * worldPerPixel);
    await camera.setPosition(P[0] + m[0], P[1] + m[1], P[2] + m[2]);
    await camera.setFocalPoint(F[0] + m[0], F[1] + m[1], F[2] + m[2]);
    await renderer.resetCameraClippingRange();
    await render();
  } catch (e) {
    console.warn('pan failed', e);
  } finally {
    state.drawing = false;
  }
}

/** 2D drag: pan the top-down orthographic camera; the world under the cursor follows it. */
async function pan2d(dx, dy) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    const rect = el.canvas.getBoundingClientRect();
    const worldPerPixel = (2 * (await camera.getParallelScale())) / rect.height;
    const P = await camera.getPosition();
    const F = await camera.getFocalPoint();
    const mx = -dx * worldPerPixel;
    const my = dy * worldPerPixel; // screen y grows downward; world y grows upward
    await camera.setPosition(P[0] + mx, P[1] + my, P[2]);
    await camera.setFocalPoint(F[0] + mx, F[1] + my, F[2]);
    await render();
  } catch (e) {
    console.warn('pan failed', e);
  } finally {
    state.drawing = false;
  }
}

/** 2D wheel: zoom by shrinking the parallel scale (dolly does nothing under an ortho camera). */
async function zoom2d(factor) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    await camera.setParallelScale((await camera.getParallelScale()) / factor);
    await render();
  } catch (e) {
    console.warn('zoom failed', e);
  } finally {
    state.drawing = false;
  }
}

// ---------------------------------------------------------------------------
// picking (#1859 items 6 and 8)
// ---------------------------------------------------------------------------

/**
 * Mouse → lab-frame world coordinates under the 2D orthographic camera. The inverse of what
 * pan2d/zoom2d maintain: the view maps world x/y linearly onto the canvas, scaled so the
 * camera's parallel scale spans half the canvas height.
 */
async function labPointFromMouse(clientX, clientY) {
  const rect = el.canvas.getBoundingClientRect();
  const worldPerPixel = (2 * (await camera.getParallelScale())) / rect.height;
  const F = await camera.getFocalPoint();
  const x = F[0] + (clientX - rect.left - rect.width / 2) * worldPerPixel;
  const y = F[1] - (clientY - rect.top - rect.height / 2) * worldPerPixel;
  return [x, y];
}

/** Center coordinates of a picked cell, for the readouts. */
function cellCenter(cell) {
  const pk = state.pick;
  const key = pk.keyByCell[cell];
  if (key === undefined) return null;
  const iz = Math.floor(key / (pk.n[0] * pk.n[1]));
  const iy = Math.floor((key - iz * pk.n[0] * pk.n[1]) / pk.n[0]);
  const ix = key % pk.n[0];
  return [pk.o[0] + (ix + 0.5) * pk.d[0], pk.o[1] + (iy + 0.5) * pk.d[1], pk.o[2] + (iz + 0.5) * pk.d[2]];
}

/** VTK cell types a 3D pick can enter, and the faces of the fixed-shape ones as vertex positions in the cell. */
const VTK_TETRA = 10;
const VTK_VOXEL = 11;
const VTK_HEXAHEDRON = 12;
const VTK_POLYHEDRON = 42;
const CELL_FACES = {
  [VTK_TETRA]: [[0, 1, 2], [0, 1, 3], [0, 2, 3], [1, 2, 3]],
  // x varies fastest, then y, then z
  [VTK_VOXEL]: [[0, 2, 3, 1], [4, 5, 7, 6], [0, 1, 5, 4], [2, 6, 7, 3], [0, 4, 6, 2], [1, 3, 7, 5]],
  [VTK_HEXAHEDRON]: [[0, 1, 2, 3], [4, 5, 6, 7], [0, 1, 5, 4], [1, 2, 6, 5], [2, 3, 7, 6], [3, 0, 4, 7]],
};

/**
 * Each 3D cell's bounding planes, for the ray pick, built once per grid: per cell a Float64Array of
 * (nx, ny, nz, d) per face, the normal pointing out of the cell (away from its vertex centroid), so a point x is
 * inside the face's half-space when n·x ≤ d; plus the cell's bounding box. Tetrahedra, voxels and hexahedra use
 * their fixed faces; a polyhedron (Chombo's cut cells) the faces the server sends. Cells are taken to be convex,
 * as Chombo's cut cells and every tetrahedron are. Cells of other types (polygons, lines) are null.
 */
function cellPlanes() {
  if (state.cellPlanes) return state.cellPlanes;
  const P = state.cellPoints;
  const cells = state.cellList;
  const planes = new Array(cells.length).fill(null);
  const boxes = new Float64Array(6 * cells.length);
  for (let c = 0; c < cells.length; c++) {
    const cell = cells[c];
    const type = state.cellTypes ? state.cellTypes[c] : state.cellType;
    let faces;
    if (type === VTK_POLYHEDRON) {
      const stream = state.cellFaces?.[c]; // [numFaces, numPoints, ids…, numPoints, ids…]
      if (!stream) continue;
      faces = [];
      for (let k = 1, f = 0; f < stream[0]; f++) {
        faces.push(stream.slice(k + 1, k + 1 + stream[k]));
        k += 1 + stream[k];
      }
    } else if (CELL_FACES[type] && cell.length === (type === VTK_TETRA ? 4 : 8)) {
      faces = CELL_FACES[type].map((f) => f.map((i) => cell[i]));
    } else {
      continue;
    }
    const centre = [0, 0, 0];
    for (const i of cell) for (let a = 0; a < 3; a++) centre[a] += P[3 * i + a] / cell.length;
    const out = new Float64Array(4 * faces.length);
    let n = 0;
    for (const face of faces) {
      // the normal of the face's polygon (Newell's method: robust for a quad that is not quite planar)
      let nx = 0; let ny = 0; let nz = 0;
      for (let v = 0; v < face.length; v++) {
        const i = face[v];
        const j = face[(v + 1) % face.length];
        nx += (P[3 * i + 1] - P[3 * j + 1]) * (P[3 * i + 2] + P[3 * j + 2]);
        ny += (P[3 * i + 2] - P[3 * j + 2]) * (P[3 * i] + P[3 * j]);
        nz += (P[3 * i] - P[3 * j]) * (P[3 * i + 1] + P[3 * j + 1]);
      }
      const len = Math.hypot(nx, ny, nz);
      if (!(len > 0)) continue;
      nx /= len; ny /= len; nz /= len;
      const i0 = face[0];
      let d = nx * P[3 * i0] + ny * P[3 * i0 + 1] + nz * P[3 * i0 + 2];
      if (nx * centre[0] + ny * centre[1] + nz * centre[2] > d) { // point out of the cell
        nx = -nx; ny = -ny; nz = -nz; d = -d;
      }
      out.set([nx, ny, nz, d], 4 * n++);
    }
    planes[c] = out.subarray(0, 4 * n);
    const o = 6 * c;
    boxes.fill(Infinity, o, o + 3);
    boxes.fill(-Infinity, o + 3, o + 6);
    for (const i of cell) {
      for (let a = 0; a < 3; a++) {
        boxes[o + a] = Math.min(boxes[o + a], P[3 * i + a]);
        boxes[o + 3 + a] = Math.max(boxes[o + 3 + a], P[3 * i + a]);
      }
    }
  }
  state.cellPlanes = { planes, boxes };
  return state.cellPlanes;
}

/**
 * The first point of a body-fitted 3D mesh the mouse ray reaches, as the view shows it: the ray is clipped
 * against each cell's faces (tetrahedra, voxels, hexahedra and polyhedra alike: `cellPlanes`), and, for the
 * smooth cut, the cut's half-space; for the whole-cells cut only the kept cells take part. The nearest entry
 * wins. Returns the point nudged just inside that cell (a boundary point is ambiguous for a containment test),
 * with its cell and, for a tetrahedron, its barycentric weights; null when the ray misses. O(cells) per call.
 */
function pickCell(origin, dir) {
  const P = state.cellPoints;
  const cells = state.cellList;
  if (!P || !cells || state.dimension !== 3) return null;
  const { planes, boxes } = cellPlanes();
  const axis = state.sliceAxis;
  let cutPos = null;
  if (axis >= 0) {
    const b = state.bounds;
    cutPos = b[2 * axis] + (0.005 + 0.99 * (state.slicePos / 100)) * (b[2 * axis + 1] - b[2 * axis]);
  }
  const wholeCells = cutPos !== null && state.cutMode === 'cells';
  const inv = dir.map((v) => 1 / v);
  let best = null;
  for (let c = 0; c < cells.length; c++) {
    const pl = planes[c];
    if (!pl) continue;
    if (wholeCells && !cells[c].some((i) => P[3 * i + axis] <= cutPos)) continue;
    let tIn = 0;
    let tOut = Infinity;
    // the bounding box first (slabs): most cells are rejected here
    const o = 6 * c;
    for (let a = 0; a < 3 && tIn <= tOut; a++) {
      if (!Number.isFinite(inv[a])) {
        if (origin[a] < boxes[o + a] || origin[a] > boxes[o + 3 + a]) tOut = -Infinity;
        continue;
      }
      let t0 = (boxes[o + a] - origin[a]) * inv[a];
      let t1 = (boxes[o + 3 + a] - origin[a]) * inv[a];
      if (t0 > t1) [t0, t1] = [t1, t0];
      tIn = Math.max(tIn, t0);
      tOut = Math.min(tOut, t1);
    }
    if (tIn > tOut || (best && tIn >= best.t)) continue;
    if (cutPos !== null && !wholeCells) {
      // the kept side of the smooth cut: x[axis] <= cutPos
      if (Math.abs(dir[axis]) < 1e-15) {
        if (origin[axis] > cutPos) continue;
      } else {
        const t = (cutPos - origin[axis]) / dir[axis];
        if (dir[axis] > 0) tOut = Math.min(tOut, t);
        else tIn = Math.max(tIn, t);
      }
    }
    for (let f = 0; f < pl.length && tIn <= tOut; f += 4) {
      // inside the face's half-space: n·(origin + t·dir) <= d
      const num = pl[f + 3] - (pl[f] * origin[0] + pl[f + 1] * origin[1] + pl[f + 2] * origin[2]);
      const den = pl[f] * dir[0] + pl[f + 1] * dir[1] + pl[f + 2] * dir[2];
      if (Math.abs(den) < 1e-300) {
        if (num < 0) tOut = -Infinity; // parallel and outside this face
      } else if (den > 0) {
        tOut = Math.min(tOut, num / den);
      } else {
        tIn = Math.max(tIn, num / den);
      }
    }
    if (tIn > tOut || (best && tIn >= best.t)) continue;
    best = { t: tIn, tOut, cell: c };
  }
  if (!best) return null;
  const t = best.t + 1e-3 * (best.tOut - best.t);
  const point = [0, 1, 2].map((a) => origin[a] + t * dir[a]);
  const cell = cells[best.cell];
  const type = state.cellTypes ? state.cellTypes[best.cell] : state.cellType;
  if (type !== VTK_TETRA) return { point, cell: best.cell, weights: null };
  // barycentric weights: the volume of the sub-tetrahedron opposite each vertex
  const sub3 = (a, b) => [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
  const dot3 = (a, b) => a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  const cross3 = (a, b) => [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]];
  const vol = (a, b, c2, d) => dot3(sub3(b, a), cross3(sub3(c2, a), sub3(d, a)));
  const [q0, q1, q2, q3] = cell.map((i) => [P[3 * i], P[3 * i + 1], P[3 * i + 2]]);
  const total = vol(q0, q1, q2, q3);
  const weights = [vol(point, q1, q2, q3), vol(q0, point, q2, q3), vol(q0, q1, point, q3), vol(q0, q1, q2, point)]
    .map((w) => w / total);
  return { point, cell: best.cell, weights };
}

/**
 * The first point of a surface mesh the mouse ray meets: a membrane of a 3D model (FEniCSx triangles, or a
 * finite-volume membrane's quads), which `pickCell` (3D cells only) can't pick. Each polygon is a fan of
 * triangles, each tested with Möller–Trumbore; the crop is honoured as the picture shows it (the smooth cut's
 * kept side, or the whole cells the cut keeps). Returns {point, cell, weights} — barycentric weights for a
 * triangle, for its P1 value — or null. O(cells) per call.
 */
/** VTK_TRIANGLE, VTK_POLYGON and VTK_QUAD: the cells of a surface mesh. */
const SURFACE_CELL_TYPES = new Set([5, 7, 9]);

function pickSurface(origin, dir) {
  const P = state.cellPoints;
  const cells = state.cellList;
  if (!P || !cells || state.dimension !== 3) return null;
  const axis = state.sliceAxis;
  let cutPos = null;
  if (axis >= 0) {
    const b = state.bounds;
    cutPos = b[2 * axis] + (0.005 + 0.99 * (state.slicePos / 100)) * (b[2 * axis + 1] - b[2 * axis]);
  }
  const wholeCells = cutPos !== null && state.cutMode === 'cells';
  const v = (i) => [P[3 * i], P[3 * i + 1], P[3 * i + 2]];
  const sub3 = (a, b) => [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
  const dot3 = (a, b) => a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  const cross3 = (a, b) => [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]];
  let best = null;
  for (let c = 0; c < cells.length; c++) {
    const cell = cells[c];
    const type = state.cellTypes ? state.cellTypes[c] : state.cellType;
    if (!SURFACE_CELL_TYPES.has(type)) continue; // a volume cell's vertices are no polygon: pickCell's job
    if (wholeCells && !cell.some((i) => P[3 * i + axis] <= cutPos)) continue;
    const a = v(cell[0]);
    for (let k = 1; k + 1 < cell.length; k++) {
      const e1 = sub3(v(cell[k]), a);
      const e2 = sub3(v(cell[k + 1]), a);
      const pv = cross3(dir, e2);
      const det = dot3(e1, pv);
      if (Math.abs(det) < 1e-300) continue;
      const tv = sub3(origin, a);
      const u = dot3(tv, pv) / det;
      if (u < -1e-9 || u > 1 + 1e-9) continue;
      const qv = cross3(tv, e1);
      const w = dot3(dir, qv) / det;
      if (w < -1e-9 || u + w > 1 + 1e-9) continue;
      const t = dot3(e2, qv) / det;
      if (!(t > 0) || (best && t >= best.t)) continue;
      const point = [0, 1, 2].map((x) => origin[x] + t * dir[x]);
      if (cutPos !== null && !wholeCells && point[axis] > cutPos) continue; // clipped away
      best = { t, cell: c, point, weights: cell.length === 3 ? [1 - u - w, u, w] : null };
    }
  }
  return best && { point: best.point, cell: best.cell, weights: best.weights };
}

/** The 3D pick: a finite-volume membrane's faces, else the body-fitted mesh's 3D cells, or its surface cells. */
function pick3d(origin, dir) {
  return state.membraneMesh ? pickSurface(origin, dir) : (pickCell(origin, dir) ?? pickSurface(origin, dir));
}

/** The distance along a unit ray to the first geometry it meets, as the picture shows it; null for a miss. */
function firstHitDistance(origin, dir) {
  if (state.bodyFitted || state.membraneMesh) {
    const hit = pick3d(origin, dir);
    return hit ? Math.hypot(...[0, 1, 2].map((a) => hit.point[a] - origin[a])) : null;
  }
  return castRay(origin, dir)?.t ?? null;
}

/** The shown field at a picked point of a 3D cell: P1-interpolated in a tetrahedron, or the cell's value. */
function valueAtCellPick(hit) {
  const values = state.fieldValues;
  if (!values) return null;
  if (state.fieldLocation !== 'point' || !hit.weights) return values[hit.cell] ?? null;
  const cell = state.cellList[hit.cell];
  let sum = 0;
  for (let k = 0; k < hit.weights.length; k++) {
    const x = values[cell[k]];
    if (x == null) return null;
    sum += hit.weights[k] * x;
  }
  return sum;
}

const pickHint = () => (state.lineDraft
  ? (lineKind() === 'membrane' ? ' — click: add a point on the membrane · Enter: finish' : ' — click: add a line vertex · Enter: finish')
  : ' — click: probe · shift-click: add a probe');

let hoverBusy = false;
async function hoverPick(clientX, clientY) {
  if (!state.ready || hoverBusy) return;
  if ((state.bodyFitted || state.membraneMesh) && state.dimension === 3) {
    hoverBusy = true;
    try {
      const { origin, dir } = await rayFromMouse(clientX, clientY);
      const hit = pick3d(origin, dir);
      if (!hit) {
        el.pickReadout.textContent = '';
        return;
      }
      const value = valueAtCellPick(hit);
      const at = ` @ (${hit.point.map((x) => x.toFixed(2)).join(', ')})`;
      el.pickReadout.textContent = (value == null ? `no data${at}` : `${state.selectedVar} = ${value.toExponential(3)}${at}`)
        + pickHint();
    } catch (e) {
      console.warn('hover pick failed', e);
    } finally {
      hoverBusy = false;
    }
    return;
  }
  if (state.bodyFitted || state.membraneMesh) {
    // no occupancy index on a body-fitted mesh or a membrane's faces; the readout shows where a click would sample
    if (state.dimension !== 2) return;
    hoverBusy = true;
    try {
      const [x, y] = await labPointFromMouse(clientX, clientY);
      el.pickReadout.textContent = `lab (${x.toFixed(2)}, ${y.toFixed(2)})${pickHint()}`;
    } catch (e) {
      console.warn('hover failed', e);
    } finally {
      hoverBusy = false;
    }
    return;
  }
  if (!state.pick) return;
  hoverBusy = true;
  try {
    const { origin, dir } = await rayFromMouse(clientX, clientY);
    const hit = castRay(origin, dir);
    if (!hit) {
      el.pickReadout.textContent = '';
      return;
    }
    const cell = hit.cell;
    const v = state.fieldValues?.[cell];
    const c = cellCenter(cell);
    const at = c ? ` @ (${c.slice(0, state.dimension === 2 ? 2 : 3).map((x) => x.toFixed(1)).join(', ')})` : '';
    el.pickReadout.textContent =
      (v == null ? `no data${at}` : `${state.selectedVar} = ${v.toExponential(3)}${at}`) + pickHint();
  } catch (e) {
    console.warn('hover pick failed', e);
  } finally {
    hoverBusy = false;
  }
}

// ---------------------------------------------------------------------------
// probes: time courses at lab-frame points (docs/plan-plotting.md §4.1, §4.3)
// ---------------------------------------------------------------------------

/**
 * What lies under the mouse, as a lab-frame point: {point, cell?}. Shared by probes and, later, line
 * vertices. Finite volume: the centre of the first voxel the ray reaches (its vertex mean — the point the
 * server maps back to that voxel exactly as the desktop does), plus `entry`, the exact surface or cut-face
 * point. Body-fitted 2D: the point under the orthographic camera, in the mesh's plane. Body-fitted 3D: the
 * entry point into the first cell the ray meets (tetrahedron, voxel or polyhedron: `pickCell`), nudged just
 * inside. Null where nothing is picked.
 */
async function pickAt(clientX, clientY) {
  if (state.bodyFitted || state.membraneMesh) {
    if (state.dimension === 2) {
      const [x, y] = await labPointFromMouse(clientX, clientY);
      return { point: [x, y, state.bounds ? state.bounds[4] : 0] };
    }
    const { origin, dir } = await rayFromMouse(clientX, clientY);
    const hit = pick3d(origin, dir);
    return hit ? { point: hit.point, cell: hit.cell } : null;
  }
  if (!state.pick) return null;
  const { origin, dir } = await rayFromMouse(clientX, clientY);
  const hit = castRay(origin, dir);
  if (!hit) return null;
  return {
    point: cellCentroid(hit.cell),
    cell: hit.cell,
    entry: [0, 1, 2].map((a) => origin[a] + hit.t * dir[a]),
  };
}

/** The mean of a cell's vertices in the served grid. */
function cellCentroid(cell) {
  const P = state.cellPoints;
  const ids = state.cellList[cell];
  const c = [0, 0, 0];
  for (const i of ids) {
    for (let a = 0; a < 3; a++) c[a] += P[3 * i + a] / ids.length;
  }
  return c;
}

/**
 * Twelve colours that stay apart on the light page and the dark canvas, shared by the Stats plot and the
 * probes; a probe keeps its colour for its trace, its list swatch and its marker. The UI caps the probes
 * at this many (the server takes 64).
 */
const SERIES_COLORS = ['#2a7', '#d70', '#07c', '#c2c', '#a33', '#578', '#e6b800', '#0aa', '#85f', '#b60', '#6a0', '#f58'];
const MAX_PROBES = SERIES_COLORS.length;

/** Click on the canvas: replace the probes with the picked point, or add it to them. */
async function probeAt(clientX, clientY, add) {
  if (!state.ready || !state.dataset) return;
  try {
    const hit = await pickAt(clientX, clientY);
    if (!hit) return;
    addProbe(hit.point, add);
  } catch (e) {
    setStatus('probe failed: ' + (e?.message ?? e), true);
  }
}

/** A probe at a lab-frame point, added to the others or replacing them. */
function addProbe(point, add = true) {
  if (!add) state.probes = [];
  if (state.probes.length >= MAX_PROBES) {
    setStatus(`at most ${MAX_PROBES} probes; remove one first`, true);
    return;
  }
  const used = new Set(state.probes.map((p) => p.color));
  let id = 1;
  while (state.probes.some((p) => p.id === id)) id++;
  state.probes.push({
    id,
    label: `P${id}`,
    point,
    color: SERIES_COLORS.find((c) => !used.has(c)) ?? SERIES_COLORS[0],
  });
  probesChanged();
}

function removeProbe(id) {
  state.probes = state.probes.filter((p) => p.id !== id);
  probesChanged();
}

function clearProbes() {
  state.probes = [];
  probesChanged();
}

/** Any change to the probes: redraw what we already know at once, and fetch the new traces. */
function probesChanged() {
  occluded.clear();
  drawOverlay();
  scheduleOcclusion();
  renderProbes();
  scheduleProbeFetch();
}

let probeFetchTimer = 0;
let probeFetch = null; // the AbortController of the request in flight

/**
 * One /timeseries request for all the probes, debounced so a burst of clicks (or a variable switch right
 * after one) costs one request, with a superseded request aborted rather than raced.
 */
function scheduleProbeFetch() {
  clearTimeout(probeFetchTimer);
  probeFetchTimer = setTimeout(() => void fetchProbes(), 150);
}

async function fetchProbes() {
  probeFetch?.abort();
  probeFetch = null;
  if (!state.probes.length || !state.dataset) {
    state.probeSeries = null;
    renderProbes();
    return;
  }
  const controller = new AbortController();
  probeFetch = controller;
  const probes = state.probes.slice();
  const params = {
    domain: state.selectedDomain,
    var: state.selectedVar,
    points: probes.map((p) => p.point.map(String).join(',')).join(';'),
  };
  // a membrane probe snaps onto the curve or surface it was clicked beside; volume domains ignore it
  if (state.bodyFitted) params.snap = 'nearest';
  try {
    const r = await fetch(url('/timeseries', params), { signal: controller.signal });
    if (!r.ok) {
      let detail = '';
      try { detail = (await r.json()).error ?? ''; } catch { /* not JSON */ }
      throw new Error(`/timeseries failed: ${r.status} ${detail || r.statusText}`);
    }
    const series = await r.json();
    if (probeFetch !== controller) return; // superseded while the body was read
    state.probeSeries = { ...series, probes };
    renderProbes();
    setStatus(`${describe()} ✓ — ${probes.length} probe${probes.length === 1 ? '' : 's'}`);
  } catch (e) {
    if (e?.name === 'AbortError') return;
    setStatus('probe time series failed: ' + (e?.message ?? e), true);
  } finally {
    if (probeFetch === controller) probeFetch = null;
  }
}

const fmtCoord = (v) => String(Number(v.toPrecision(4)));
const probeWhere = (p) => `(${p.point.slice(0, state.dimension === 2 ? 2 : 3).map(fmtCoord).join(', ')})`;

/** The panel: one row per probe, and the plot. Both read the last response, matched to probes by id. */
function renderProbes() {
  if (!state.probes.length) {
    el.probePanel.hidden = true;
    el.probeList.replaceChildren();
    el.probeSvg.replaceChildren();
    return;
  }
  el.probePanel.hidden = false;
  const data = state.probeSeries;
  const seriesOf = (probe) => {
    const k = data ? data.probes.findIndex((p) => p === probe) : -1;
    return k >= 0 ? data.series[k] : null;
  };
  el.probeTitle.textContent = `${state.selectedVar} · ${state.selectedDomain} · ${state.probes.length} probe${state.probes.length === 1 ? '' : 's'}`
    + (data ? '' : ' · loading…');
  el.probeList.replaceChildren(...state.probes.map((probe) => {
    const li = document.createElement('li');
    li.dataset.probe = String(probe.id);
    const swatch = document.createElement('span');
    swatch.className = 'swatch';
    swatch.style.background = probe.color;
    const label = document.createElement('span');
    label.textContent = `${probe.label} ${probeWhere(probe)}`;
    const value = document.createElement('span');
    const s = seriesOf(probe);
    if (s && !s.inDomain) {
      value.className = 'outside';
      value.textContent = `outside ${data.domain}`;
    } else {
      const v = s?.values[state.timeIndex];
      value.className = 'value';
      value.textContent = v == null ? (s ? 'no data now' : '') : `= ${v.toExponential(3)}`;
    }
    const centre = document.createElement('button');
    centre.type = 'button';
    centre.textContent = '⌖';
    centre.title = `centre the view on ${probe.label}`;
    centre.addEventListener('click', () => void centreOn(probe.point));
    const remove = document.createElement('button');
    remove.type = 'button';
    remove.textContent = '✕';
    remove.title = `remove ${probe.label}`;
    remove.addEventListener('click', () => removeProbe(probe.id));
    li.append(swatch, label, value, centre, remove);
    return li;
  }));
  if (data) {
    renderTraces(data.times, state.probes.map((p) => ({ probe: p, series: seriesOf(p) })).filter((t) => t.series));
  }
}

/** "Nice" tick values (1, 2 or 5 × 10^k apart) covering [lo, hi], about `count` of them. */
function niceTicks(lo, hi, count = 5) {
  const span = hi - lo;
  if (!(span > 0)) return { step: 1, ticks: [lo] };
  const raw = span / count;
  const mag = 10 ** Math.floor(Math.log10(raw));
  const step = [1, 2, 5, 10].map((m) => m * mag).find((s) => s >= raw) ?? 10 * mag;
  const ticks = [];
  for (let v = Math.ceil(lo / step - 1e-9) * step; v <= hi + step * 1e-9; v += step) ticks.push(Math.abs(v) < step * 1e-9 ? 0 : v);
  return { step, ticks };
}

/** A tick label: plain decimals when the step allows, exponent form otherwise. */
function tickLabel(v, step) {
  const a = Math.abs(step);
  if (a >= 1e-3 && a < 1e5) return v.toFixed(Math.max(0, -Math.floor(Math.log10(a) + 1e-9)));
  return v.toExponential(Math.max(0, Math.round(Math.log10(Math.abs(v) || a) - Math.log10(a))));
}

/**
 * One trace per probe on one shared scale, with ticked axes, titled `time (s)` and `<var> [<domain>]`
 * (VCell's default units, until the server reports units), and a time cursor at the slider's time. Null
 * values break a trace — outside the domain, or where a moving boundary has passed the point — and a
 * lone value is a dot. Drawn at the SVG's own pixel size, so the labels are not stretched.
 */
function renderTraces(times, traces) {
  const svg = el.probeSvg;
  const W = Math.max(320, Math.round(svg.clientWidth || 640));
  const H = 240;
  const M = { l: 70, r: 12, t: 12, b: 38 };
  svg.setAttribute('viewBox', `0 0 ${W} ${H}`);
  svg.replaceChildren();
  const mk = (tag, attrs, text) => {
    const n = document.createElementNS(SVG_NS, tag);
    for (const [k, v] of Object.entries(attrs)) n.setAttribute(k, v);
    if (text != null) n.textContent = text;
    svg.appendChild(n);
    return n;
  };
  const finite = traces.flatMap((t) => t.series.values).filter((v) => v != null && Number.isFinite(v));
  let lo = finite.length ? Math.min(...finite) : 0;
  let hi = finite.length ? Math.max(...finite) : 1;
  const pad = hi > lo ? 0.04 * (hi - lo) : Math.abs(lo) * 0.05 || 1; // keep the traces off the frame
  lo -= pad;
  hi += pad;
  const t0 = times[0];
  const t1 = times.length > 1 ? times[times.length - 1] : t0 + 1;
  const sx = (t) => M.l + ((t - t0) / (t1 - t0 || 1)) * (W - M.l - M.r);
  const sy = (v) => H - M.b - ((v - lo) / (hi - lo)) * (H - M.t - M.b);
  const yt = niceTicks(lo, hi);
  for (const v of yt.ticks) {
    mk('line', { class: 'grid', x1: M.l, x2: W - M.r, y1: sy(v), y2: sy(v) });
    mk('text', { class: 'lbl', x: M.l - 5, y: sy(v) + 4, 'text-anchor': 'end' }, tickLabel(v, yt.step));
  }
  const xt = niceTicks(t0, t1);
  for (const t of xt.ticks) {
    mk('line', { class: 'axis', x1: sx(t), x2: sx(t), y1: H - M.b, y2: H - M.b + 4 });
    mk('text', { class: 'lbl', x: sx(t), y: H - M.b + 15, 'text-anchor': 'middle' }, tickLabel(t, xt.step));
  }
  mk('line', { class: 'axis', x1: M.l, y1: H - M.b, x2: W - M.r, y2: H - M.b });
  mk('line', { class: 'axis', x1: M.l, y1: M.t, x2: M.l, y2: H - M.b });
  mk('text', { class: 'title', x: (M.l + W - M.r) / 2, y: H - 4, 'text-anchor': 'middle' }, 'time (s)');
  const yTitle = `${state.probeSeries?.name ?? state.selectedVar} [${state.probeSeries?.domain ?? state.selectedDomain}]`;
  mk('text', { class: 'title', x: 0, y: 0, 'text-anchor': 'middle',
    transform: `translate(12 ${(M.t + H - M.b) / 2}) rotate(-90)` }, yTitle);
  if (!finite.length) {
    mk('text', { class: 'lbl', x: (M.l + W - M.r) / 2, y: (M.t + H - M.b) / 2, 'text-anchor': 'middle' },
      `no data: the probes lie outside ${state.probeSeries?.domain ?? state.selectedDomain}`);
  }
  for (const { probe, series } of traces) {
    const g = mk('g', { class: 'trace-group', 'data-probe': String(probe.id) });
    let seg = [];
    const flush = () => {
      if (seg.length > 1) {
        const n = document.createElementNS(SVG_NS, 'polyline');
        n.setAttribute('class', 'trace');
        n.setAttribute('stroke', probe.color);
        n.setAttribute('points', seg.join(' '));
        g.appendChild(n);
      } else if (seg.length === 1) {
        const [cx, cy] = seg[0].split(',');
        const n = document.createElementNS(SVG_NS, 'circle');
        n.setAttribute('cx', cx);
        n.setAttribute('cy', cy);
        n.setAttribute('r', '3');
        n.setAttribute('fill', probe.color);
        g.appendChild(n);
      }
      seg = [];
    };
    times.forEach((t, i) => {
      const v = series.values[i];
      if (v == null || !Number.isFinite(v)) {
        flush();
        return;
      }
      seg.push(`${sx(t).toFixed(1)},${sy(v).toFixed(1)}`);
    });
    flush();
  }
  const cursor = mk('line', { class: 'time-cursor', y1: M.t, y2: H - M.b });
  state.probePlot = { times, sx, cursor, M, W };
  updateTimeCursor();
}

/** Keep the plot's time cursor, and the list's current values, on the slider's time. */
function updateTimeCursor() {
  const plot = state.probePlot;
  if (!plot || !plot.cursor.isConnected) return;
  const t = state.times[state.timeIndex];
  const visible = t != null && t >= plot.times[0] && t <= plot.times[plot.times.length - 1];
  plot.cursor.setAttribute('visibility', visible ? 'visible' : 'hidden');
  if (visible) {
    const x = plot.sx(t).toFixed(1);
    plot.cursor.setAttribute('x1', x);
    plot.cursor.setAttribute('x2', x);
  }
}

/** Clicking the plot moves the time slider to the nearest saved time, as if the slider were dragged there. */
el.probeSvg.addEventListener('click', (e) => {
  const plot = state.probePlot;
  if (!plot || !state.ready) return;
  const rect = el.probeSvg.getBoundingClientRect();
  const x = ((e.clientX - rect.left) / rect.width) * plot.W;
  let best = 0;
  plot.times.forEach((t, i) => {
    if (Math.abs(plot.sx(t) - x) < Math.abs(plot.sx(plot.times[best]) - x)) best = i;
  });
  const index = nearestTimeIndex(plot.times[best]);
  if (index === state.timeIndex) return;
  el.time.value = String(index);
  el.time.dispatchEvent(new Event('input'));
  el.time.dispatchEvent(new Event('change'));
});

/**
 * The probes as CSV: comment lines naming the run, then `time,P1(x;y;z),…` and one row per time. Gaps
 * are empty fields. Built and saved in the page — nothing goes over the network.
 */
function probesCsv() {
  const data = state.probeSeries;
  if (!data) return null;
  const lines = [
    `# sim: ${state.dataset.sim}`,
    `# job: ${state.dataset.job}`,
    `# var: ${data.name}`,
    `# domain: ${data.domain}`,
    ['time', ...data.probes.map((p) => `${p.label}(${p.point.map(fmtCoord).join(';')})`)].join(','),
  ];
  data.times.forEach((t, i) => {
    lines.push([t, ...data.series.map((s) => (s.values[i] == null ? '' : s.values[i]))].join(','));
  });
  return lines.join('\n') + '\n';
}

function saveProbesCsv() {
  const text = probesCsv();
  if (!text) return;
  saveBlob(new Blob([text], { type: 'text/csv' }), `${state.probeSeries.name}-probes.csv`);
}

/** ⌖: move the view so the probe is at its centre, keeping the direction and distance; orbit then turns about it. */
async function centreOn(point) {
  if (!camera || !state.ready || state.drawing) return;
  state.drawing = true;
  try {
    const P = await camera.getPosition();
    const F = await camera.getFocalPoint();
    const d = [0, 1, 2].map((a) => point[a] - F[a]);
    if (state.dimension === 2) d[2] = 0; // the top-down camera stays above the plane
    await camera.setPosition(P[0] + d[0], P[1] + d[1], P[2] + d[2]);
    await camera.setFocalPoint(F[0] + d[0], F[1] + d[1], F[2] + d[2]);
    state.pivot = [F[0] + d[0], F[1] + d[1], F[2] + d[2]];
    if (state.dimension !== 2) await renderer.resetCameraClippingRange();
    await render();
  } catch (e) {
    console.warn('centring the view failed', e);
  } finally {
    state.drawing = false;
  }
}

el.addProbes.addEventListener('click', () => {
  state.addMode = !state.addMode;
  el.addProbes.setAttribute('aria-pressed', String(state.addMode));
});
el.probeClear.addEventListener('click', clearProbes);
el.probeCsv.addEventListener('click', saveProbesCsv);

// ---------------------------------------------------------------------------
// the overlay: probe markers drawn over the canvas, projected from the camera
// ---------------------------------------------------------------------------

/** The camera VTK last drew with; projection reads this, so markers and picture cannot disagree. */
async function cacheCameraView() {
  if (!camera) return;
  state.cameraView = {
    P: await camera.getPosition(),
    F: await camera.getFocalPoint(),
    U: await camera.getViewUp(),
    viewAngle: await camera.getViewAngle(),
    parallelScale: state.dimension === 2 ? await camera.getParallelScale() : null,
  };
}

/**
 * A world point → canvas pixels (CSS), the inverse of rayFromMouse: perspective (vertical view angle) in
 * 3D, the top-down orthographic view in 2D. Null for a point behind the camera.
 */
function projectToScreen(X) {
  const view = state.cameraView;
  if (!view) return null;
  const rect = el.canvas.getBoundingClientRect();
  const norm = (v) => {
    const l = Math.hypot(v[0], v[1], v[2]) || 1;
    return [v[0] / l, v[1] / l, v[2] / l];
  };
  const cross = (u, v) => [u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]];
  const dot = (u, v) => u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
  const { P, F, U } = view;
  const fwd = norm([F[0] - P[0], F[1] - P[1], F[2] - P[2]]);
  const right = norm(cross(fwd, U));
  const up = cross(right, fwd);
  const aspect = rect.width / rect.height;
  const d = [X[0] - P[0], X[1] - P[1], X[2] - P[2]];
  let xN;
  let yN;
  if (state.dimension === 2) {
    xN = dot(d, right) / (view.parallelScale * aspect);
    yN = dot(d, up) / view.parallelScale;
  } else {
    const z = dot(d, fwd);
    if (z <= 0) return null;
    const halfTan = Math.tan((view.viewAngle * Math.PI) / 360);
    xN = dot(d, right) / (z * halfTan * aspect);
    yN = dot(d, up) / (z * halfTan);
  }
  return [((xN + 1) / 2) * rect.width, ((1 - yN) / 2) * rect.height];
}

const occluded = new Set(); // ids of the probes the geometry hides from the current view

/** One marker per probe: a ring in its colour with its label, hollow and faint where hidden. */
function drawOverlay() {
  const svg = el.overlay;
  if (!svg) return;
  svg.replaceChildren();
  for (const probe of state.probes) {
    const at = projectToScreen(probe.point);
    if (!at) continue;
    const hidden = occluded.has(probe.id);
    const g = document.createElementNS(SVG_NS, 'g');
    g.setAttribute('class', 'marker' + (hidden ? ' occluded' : ''));
    g.dataset.probe = String(probe.id);
    g.setAttribute('opacity', hidden ? '0.4' : '1');
    const ring = document.createElementNS(SVG_NS, 'circle');
    ring.setAttribute('cx', at[0].toFixed(1));
    ring.setAttribute('cy', at[1].toFixed(1));
    ring.setAttribute('r', '5');
    ring.setAttribute('stroke', probe.color);
    ring.setAttribute('fill', hidden ? 'none' : probe.color);
    const text = document.createElementNS(SVG_NS, 'text');
    text.setAttribute('x', (at[0] + 8).toFixed(1));
    text.setAttribute('y', (at[1] - 6).toFixed(1));
    text.textContent = probe.label;
    g.append(ring, text);
    svg.appendChild(g);
  }
  drawLineOverlay(svg);
}

let occlusionTimer = 0;
function scheduleOcclusion() {
  clearTimeout(occlusionTimer);
  if ((!state.probes.length && !state.line) || state.dimension === 2) return; // a 2D view hides nothing
  occlusionTimer = setTimeout(() => {
    try {
      updateOcclusion();
      drawOverlay();
    } catch (e) {
      console.warn('probe occlusion check failed', e);
    }
  }, 150);
}

/**
 * Which markers something else hides: cast a ray from the camera to each probe (the voxel walk, or the
 * body-fitted cell pick, honoring the crop as the picture does), and call it hidden when the ray meets the
 * geometry clearly before it reaches the probe. "Clearly" is a voxel's diagonal for finite volume (a probe
 * sits at its voxel's centre, inside the surface the ray meets) and a sliver of the scene for a body-fitted
 * mesh (a probe sits just inside the surface it was picked on).
 */
function updateOcclusion() {
  occluded.clear();
  const view = state.cameraView;
  if (!view || !state.bounds) return;
  const b = state.bounds;
  const slack = state.bodyFitted || state.membraneMesh || !state.pick
    ? 0.01 * Math.hypot(b[1] - b[0], b[3] - b[2], b[5] - b[4])
    : Math.hypot(...state.pick.d);
  for (const probe of state.probes) {
    const d = [0, 1, 2].map((a) => probe.point[a] - view.P[a]);
    const dist = Math.hypot(...d);
    if (!(dist > 0)) continue;
    const dir = d.map((v) => v / dist);
    const t = firstHitDistance(view.P, dir);
    if (t != null && t < dist - slack) occluded.add(probe.id);
  }
  updateLineOcclusion(slack);
}

const SVG_NS = 'http://www.w3.org/2000/svg';

// ---------------------------------------------------------------------------
// lines and kymographs (docs/plan-plotting.md §4.2, §4.4, §4.5)
// ---------------------------------------------------------------------------

/** The server's limit on a line's vertices. */
const MAX_LINE_VERTICES = 64;
/** Each segment of the line is drawn in this many pieces, each dashed on its own where the geometry hides it. */
const LINE_PIECES = 16;

// vertex picks are async (they read the camera); Enter, Backspace and a double-click wait for them in order
let lineVertexQueue = Promise.resolve();

/** A coordinate as the line's field shows it, and as the line keeps it: six significant digits. */
const fmtVertex = (v) => String(Number(v.toPrecision(6)));
const roundPoint = (p) => p.map((v) => Number(v.toPrecision(6)));

function formatLine(vertices) {
  return vertices.map((v) => v.slice(0, state.dimension === 2 ? 2 : 3).map(fmtVertex).join(',')).join('; ');
}

/** `x,y,z; x,y,z; …` (in 2D `x,y` will do) → vertices; throws with a message saying what is wrong. */
function parseLine(text) {
  const vertices = text.split(';').map((t) => t.trim()).filter(Boolean).map((entry) => {
    const parts = entry.split(',').map((t) => Number(t.trim()));
    if (parts.length === 2 && state.dimension === 2) parts.push(state.bounds ? state.bounds[4] : 0);
    if (parts.length !== 3 || !parts.every(Number.isFinite)) {
      throw new Error(`'${entry}' is not a point: write x,y,z${state.dimension === 2 ? ' (or x,y)' : ''}`);
    }
    return parts;
  });
  if (vertices.length < 2) throw new Error('a line needs at least two vertices, separated by ;');
  if (vertices.length > MAX_LINE_VERTICES) throw new Error(`a line has at most ${MAX_LINE_VERTICES} vertices`);
  return vertices;
}

/**
 * What a line is on the current domain (docs/plan-plotting.md P7):
 * - 'line': a polyline through the data, sampled along its straight segments;
 * - 'membrane': a curve along a 2D membrane (a body-fitted line mesh). The picks are waypoints: the server
 *   snaps each onto the membrane and runs the curve along the mesh between them, the shorter way round, so the
 *   overlay draws the curve's own samples, not straight segments between the picks;
 * - 'surface': a 3D membrane surface, where curves (geodesics) are not served yet.
 * A finite-volume membrane is a 'membrane' in 2D and 3D: its curves are the desktop's, in the one slice of a 2D
 * run or, in 3D, in the crop's cut plane (the curve where the plane cuts the membrane), so there the tool needs the
 * crop on.
 */
function lineKind() {
  if (state.membraneMesh) return 'membrane'; // a finite-volume membrane: its curve in 2D, or in the cut plane in 3D
  if (!state.bodyFitted || state.cellTypes) return 'line';
  if (state.cellType === 3) return 'membrane'; // VTK_LINE
  if (state.cellType === 5 && state.dimension === 3) return 'surface'; // VTK_TRIANGLE in 3D
  return 'line';
}

const LINE_TOOL_TITLE = 'Draw a line for a kymograph: click to add vertices, Enter or double-click to finish, '
  + 'Backspace removes the last, Esc cancels';
const CURVE_TOOL_TITLE = 'Draw a curve along the membrane for a kymograph: click on or beside the membrane to add '
  + 'points (the curve follows the membrane between them), Enter or double-click to finish, Backspace removes the '
  + 'last, Esc cancels';

/** The Line tool for the current domain: a curve on a 2D membrane, off (with the reason) on a 3D membrane surface. */
function updateLineTool() {
  const kind = lineKind();
  const needsCut = kind === 'membrane' && state.membraneMesh && state.dimension === 3 && state.sliceAxis < 0;
  el.lineTool.disabled = kind === 'surface' || needsCut;
  el.lineTool.title = kind === 'surface'
    ? 'Curves on a 3D membrane surface are not supported yet: choose a volume variable for a line through the data'
    : needsCut ? 'A curve along a 3D membrane lies in a slice, as on the desktop: turn on the crop (Slice), then click '
      + 'along the membrane at the cut'
      : kind === 'membrane' ? CURVE_TOOL_TITLE : LINE_TOOL_TITLE;
  if ((kind === 'surface' || needsCut) && state.lineDraft) setLineTool(false);
}

/** The crop's cut plane: {axis, pos} in lab coordinates, or null with the crop off. */
function cutPlane() {
  const axis = state.sliceAxis;
  if (axis < 0 || !state.bounds) return null;
  const b = state.bounds;
  return { axis, pos: b[2 * axis] + (0.005 + 0.99 * (state.slicePos / 100)) * (b[2 * axis + 1] - b[2 * axis]) };
}

/** The Line tool: on starts a new line (the current one stays until the new one is finished); off cancels it. */
function setLineTool(on) {
  state.lineDraft = on ? [] : null;
  el.lineTool.setAttribute('aria-pressed', String(on));
  el.box.classList.toggle('drawing-line', on);
  el.kymoPanel.hidden = !on && !state.line;
  el.lineCoords.value = formatLine(on ? [] : state.line?.vertices ?? []);
  updateKymoNote();
  drawOverlay();
}

/** A click with the Line tool on: a vertex at the exact surface or cut-face point under the mouse. */
function addLineVertex(clientX, clientY) {
  lineVertexQueue = lineVertexQueue.then(async () => {
    if (!state.lineDraft) return;
    try {
      const hit = await pickAt(clientX, clientY);
      if (!hit || !state.lineDraft) return;
      const picked = (hit.entry ?? hit.point).slice();
      const cut = state.membraneMesh && state.dimension === 3 ? cutPlane() : null;
      if (cut) picked[cut.axis] = cut.pos; // a curve along a 3D membrane lies in the cut plane: its picks too
      const point = roundPoint(picked);
      const last = state.lineDraft[state.lineDraft.length - 1];
      if (last && last.every((v, a) => v === point[a])) return; // the second click of a double-click
      state.lineDraft.push(point);
      el.lineCoords.value = formatLine(state.lineDraft);
      updateKymoNote();
      drawOverlay();
      if (state.lineDraft.length >= MAX_LINE_VERTICES) finishLine();
    } catch (e) {
      setStatus('placing a line vertex failed: ' + (e?.message ?? e), true);
    }
  });
  return lineVertexQueue;
}

/** Enter or a double-click: the draft becomes the line, once it has two vertices. */
function finishLine() {
  const draft = state.lineDraft;
  if (!draft) return;
  if (draft.length < 2) {
    updateKymoNote('a line needs at least two vertices: click the view again, or Esc to cancel', true);
    return;
  }
  setLine(draft);
}

/** Make `vertices` the line, drawn and fetched; ends any drawing. */
function setLine(vertices) {
  state.line = { vertices };
  state.lineDraft = null;
  el.lineTool.setAttribute('aria-pressed', 'false');
  el.box.classList.remove('drawing-line');
  el.kymoPanel.hidden = false;
  el.lineCoords.value = formatLine(vertices);
  lineOccluded.clear();
  drawOverlay();
  scheduleOcclusion();
  void fetchKymograph();
}

function clearLine() {
  clearTimeout(kymoFetchTimer);
  kymoFetch?.abort();
  kymoFetch = null;
  state.line = null;
  state.lineDraft = null;
  state.kymo = null;
  state.kymoStale = false;
  el.kymoStale.hidden = true;
  el.lineTool.setAttribute('aria-pressed', 'false');
  el.box.classList.remove('drawing-line');
  el.kymoPanel.hidden = true;
  renderKymograph();
  drawOverlay();
}

/** The typed line: redraw and refetch when it differs from the current one. */
function applyLineCoords() {
  let vertices;
  try {
    vertices = parseLine(el.lineCoords.value);
  } catch (e) {
    updateKymoNote(e.message, true);
    return;
  }
  if (state.line && !state.lineDraft && formatLine(vertices) === formatLine(state.line.vertices)) return;
  setLine(vertices);
}

el.lineTool.addEventListener('click', () => setLineTool(!state.lineDraft));
el.kymoClose.addEventListener('click', clearLine);
el.lineCoords.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') {
    e.preventDefault();
    applyLineCoords();
  }
});
el.lineCoords.addEventListener('change', applyLineCoords);
el.kymoStale.addEventListener('click', () => void fetchKymograph());
document.addEventListener('keydown', (e) => {
  if (!state.lineDraft) return;
  const t = e.target;
  if (t instanceof HTMLInputElement || t instanceof HTMLSelectElement || t instanceof HTMLTextAreaElement) return;
  if (e.key === 'Enter') {
    e.preventDefault();
    lineVertexQueue = lineVertexQueue.then(() => finishLine());
  } else if (e.key === 'Backspace') {
    e.preventDefault();
    lineVertexQueue = lineVertexQueue.then(() => {
      if (!state.lineDraft) return;
      state.lineDraft.pop();
      el.lineCoords.value = formatLine(state.lineDraft);
      updateKymoNote();
      drawOverlay();
    });
  } else if (e.key === 'Escape') {
    e.preventDefault();
    setLineTool(false);
  }
});

// --- the line over the 3D/2D view ---

const lineOccluded = new Set(); // pieces of the line the geometry hides: index (segment · LINE_PIECES + piece)

const lerp3 = (a, b, f) => [a[0] + f * (b[0] - a[0]), a[1] + f * (b[1] - a[1]), a[2] + f * (b[2] - a[2])];

/**
 * What the overlay draws for the line: {vertices, pieces, marks}. A straight line is its own vertices, each
 * segment in LINE_PIECES pieces (occlusion is tested per piece). A curve along a membrane is the polyline
 * through the samples the server returned, one piece per sample interval, with the marks at the snapped picks
 * (the response's path); until its response arrives, it is drawn like a line through the picks.
 */
function lineGeometry() {
  if (state.lineDraft) return { vertices: state.lineDraft, pieces: LINE_PIECES, marks: state.lineDraft };
  const line = state.line;
  if (!line) return null;
  const k = state.kymo;
  if (k && k.line === line && k.sampling === 'membrane') {
    // a finite-volume curve's samples are in the solver's node-centred frame: drawnPoints are them on the drawn faces
    const P = k.drawnPoints ?? k.samples.points;
    const vertices = [];
    for (let i = 0; i + 2 < P.length; i += 3) vertices.push([P[i], P[i + 1], P[i + 2]]);
    return { vertices, pieces: 1, marks: k.drawnPath ?? k.path };
  }
  return { vertices: line.vertices, pieces: LINE_PIECES, marks: line.vertices };
}

/**
 * The line (or the one being drawn) over the view: white over a dark under-stroke, dashed where the geometry
 * hides it, a dot at each vertex and a tick across its start, where the kymograph's distance 0 is.
 */
function drawLineOverlay(svg) {
  const drafting = !!state.lineDraft;
  const geometry = lineGeometry();
  if (!geometry?.vertices.length) return;
  const { vertices, pieces: perSegment, marks } = geometry;
  const g = document.createElementNS(SVG_NS, 'g');
  g.setAttribute('class', 'kymo-overlay');
  const add = (tag, attrs) => {
    const n = document.createElementNS(SVG_NS, tag);
    for (const [k, v] of Object.entries(attrs)) n.setAttribute(k, v);
    g.appendChild(n);
    return n;
  };
  const pieces = [];
  for (let s = 1; s < vertices.length; s++) {
    for (let k = 0; k < perSegment; k++) {
      const a = projectToScreen(lerp3(vertices[s - 1], vertices[s], k / perSegment));
      const b = projectToScreen(lerp3(vertices[s - 1], vertices[s], (k + 1) / perSegment));
      if (a && b) pieces.push({ a, b, hidden: !drafting && lineOccluded.has((s - 1) * perSegment + k) });
    }
  }
  const seg = (p, cls) => add('line', { class: cls, x1: p.a[0].toFixed(1), y1: p.a[1].toFixed(1),
    x2: p.b[0].toFixed(1), y2: p.b[1].toFixed(1) });
  for (const p of pieces) if (!p.hidden) seg(p, 'kymo-line-under');
  for (const p of pieces) seg(p, 'kymo-line' + (p.hidden ? ' occluded' : '') + (drafting ? ' draft' : ''));
  const screen = vertices.map(projectToScreen);
  if (screen[0] && screen[1]) { // the start tick, across the line's first segment
    const dx = screen[1][0] - screen[0][0];
    const dy = screen[1][1] - screen[0][1];
    const l = Math.hypot(dx, dy) || 1;
    const [px, py] = [(-dy / l) * 7, (dx / l) * 7];
    add('line', { class: 'kymo-start', x1: (screen[0][0] - px).toFixed(1), y1: (screen[0][1] - py).toFixed(1),
      x2: (screen[0][0] + px).toFixed(1), y2: (screen[0][1] + py).toFixed(1) });
  }
  marks.map(projectToScreen).forEach((p, i) => {
    if (p) add('circle', { class: 'kymo-vertex', cx: p[0].toFixed(1), cy: p[1].toFixed(1), r: i === 0 ? '4' : '3' });
  });
  svg.appendChild(g);
}

/** As for the probes (updateOcclusion): a piece is hidden when the camera's ray to its middle meets the geometry first. */
function updateLineOcclusion(slack) {
  lineOccluded.clear();
  const view = state.cameraView;
  const geometry = state.lineDraft ? null : lineGeometry();
  if (!view || !geometry || state.dimension === 2) return;
  const { vertices, pieces } = geometry;
  for (let s = 1; s < vertices.length; s++) {
    for (let k = 0; k < pieces; k++) {
      const m = lerp3(vertices[s - 1], vertices[s], (k + 0.5) / pieces);
      const d = [0, 1, 2].map((a) => m[a] - view.P[a]);
      const dist = Math.hypot(...d);
      if (!(dist > 0)) continue;
      const dir = d.map((v) => v / dist);
      const t = firstHitDistance(view.P, dir);
      if (t != null && t < dist - slack) lineOccluded.add((s - 1) * pieces + k);
    }
  }
}

// --- fetching ---

let kymoFetch = null; // the AbortController of the request in flight
let kymoFetchTimer = 0;

function scheduleKymoFetch() {
  clearTimeout(kymoFetchTimer);
  kymoFetchTimer = setTimeout(() => void fetchKymograph(), 150);
}

/**
 * One /kymograph request for a line: {data, tstep}. Over the server's limit on samples × times it answers 400
 * with the smallest stride over the saved times that fits (`suggestedTstep`): the viewer retries once with
 * it, and says so. A 503 means another heavy job is running on the server: it retries once, after a pause.
 */
async function requestKymograph(line, { raw = false, tstep = 1, signal } = {}) {
  let strided = false;
  let busyRetried = false;
  for (;;) {
    const params = {
      domain: state.selectedDomain,
      var: state.selectedVar,
      path: line.vertices.map((v) => v.map(String).join(',')).join(';'),
    };
    if (tstep > 1) params.tstep = String(tstep);
    if (raw) params.raw = '1';
    // a curve along a 3D finite-volume membrane lies in the slice normal to the crop's axis
    if (state.membraneMesh && state.dimension === 3 && state.sliceAxis >= 0) params.plane = 'xyz'[state.sliceAxis];
    const r = await fetch(url('/kymograph', params), { signal });
    if (r.ok) return { data: await r.json(), tstep };
    let body = {};
    try { body = await r.json(); } catch { /* not JSON */ }
    if (r.status === 400 && Number.isInteger(body.suggestedTstep) && !strided) {
      tstep = body.suggestedTstep;
      strided = true;
      continue;
    }
    if (r.status === 503 && !busyRetried) {
      busyRetried = true;
      await new Promise((resolve) => setTimeout(resolve, 600));
      continue;
    }
    throw new Error(`/kymograph failed: ${r.status} ${body.error || r.statusText}`);
  }
}

async function fetchKymograph() {
  clearTimeout(kymoFetchTimer);
  kymoFetch?.abort();
  kymoFetch = null;
  if (!state.line || !state.dataset) return;
  const controller = new AbortController();
  kymoFetch = controller;
  const line = state.line;
  el.kymoTitle.textContent = `${state.selectedVar} · ${state.selectedDomain} · loading…`;
  try {
    const { data, tstep } = await requestKymograph(line, { signal: controller.signal });
    if (kymoFetch !== controller) return; // superseded while the body was read
    state.kymo = { ...data, line, tstep };
    state.kymoStale = false;
    el.kymoStale.hidden = true;
    renderKymograph();
    if (data.sampling === 'membrane') { // the overlay now follows the curve's own samples
      lineOccluded.clear();
      drawOverlay();
      scheduleOcclusion();
    }
    setStatus(`${describe()} ✓ — kymograph: ${data.samples.arcLength.length} samples × ${data.times.length} times`);
  } catch (e) {
    if (e?.name === 'AbortError') return;
    state.kymo = null;
    renderKymograph();
    updateKymoNote(e?.message ?? String(e), true);
    setStatus('kymograph failed: ' + (e?.message ?? e), true);
  } finally {
    if (kymoFetch === controller) kymoFetch = null;
  }
}

// --- drawing ---

/**
 * The viewer's colour map, ported from the vtkLookupTable it builds for the 3D view (hue 0.66667 → 0,
 * saturation and value 1, 256 entries: blue low, red high), so the kymograph and the view agree.
 */
const KYMO_LUT = (() => {
  const table = new Uint8ClampedArray(256 * 3);
  for (let i = 0; i < 256; i++) {
    const h = 0.66667 + (i * (0.0 - 0.66667)) / 255;
    let r; let g; let b; // vtkMath::HSVToRGB at s = v = 1
    if (h > 1 / 6 && h <= 1 / 3) { g = 1; r = (1 / 3 - h) * 6; b = 0; }
    else if (h > 1 / 3 && h <= 0.5) { g = 1; b = (h - 1 / 3) * 6; r = 0; }
    else if (h > 0.5 && h <= 2 / 3) { b = 1; g = (2 / 3 - h) * 6; r = 0; }
    else if (h > 2 / 3 && h <= 5 / 6) { b = 1; r = (h - 2 / 3) * 6; g = 0; }
    else if (h > 5 / 6 && h <= 1) { r = 1; b = (1 - h) * 6; g = 0; }
    else { r = 1; g = h * 6; b = 0; }
    table[3 * i] = r * 255 + 0.5;
    table[3 * i + 1] = g * 255 + 0.5;
    table[3 * i + 2] = b * 255 + 0.5;
  }
  return table;
})();

/** The LUT entry for v over [lo, hi], clamped at both ends, as vtkLookupTable maps a value. */
function lutIndex(v, lo, hi) {
  const i = Math.floor(((v - lo) * 256) / (hi - lo || 1));
  return i < 0 ? 0 : i > 255 ? 255 : i;
}

/** The kymograph's colour range for the selected mode. */
function kymoRange() {
  const k = state.kymo;
  let [lo, hi] = k.range;
  const mode = el.kymoRange.value;
  if (mode === 'view' && state.fieldRange) [lo, hi] = state.fieldRange;
  if (mode === 'user') {
    const a = parseFloat(el.kymoMin.value);
    const b = parseFloat(el.kymoMax.value);
    if (Number.isFinite(a)) lo = a;
    if (Number.isFinite(b)) hi = b;
  }
  if (!(hi > lo)) hi = lo + (Math.abs(lo) || 1) * 1e-6; // a flat kymograph is one colour, not a divide by zero
  return [lo, hi];
}

/**
 * Which sample each image column shows. Finite volume ('cell'): the sample whose span holds the column's
 * middle, spans ending halfway between neighbouring samples, so each voxel shows at its true width (and a
 * membrane crossing's two samples, at one point, share the gap between their neighbours). FEniCSx ('point'):
 * the two samples either side, and the fraction between them, for linear interpolation.
 */
function columnSamples(k, W) {
  const arc = k.samples.arcLength;
  const n = arc.length;
  const a0 = arc[0];
  const span = arc[n - 1] - a0;
  const cols = [];
  for (let c = 0; c < W; c++) {
    const s = span > 0 ? a0 + ((c + 0.5) / W) * span : a0;
    if (k.location === 'point') {
      let j = 0;
      while (j < n - 2 && arc[j + 1] < s) j++;
      const f = arc[j + 1] > arc[j] ? Math.min(1, Math.max(0, (s - arc[j]) / (arc[j + 1] - arc[j]))) : 0;
      cols.push({ i: f > 0.5 ? j + 1 : j, j, f });
    } else {
      let lo = 0;
      let hi = n - 1;
      while (lo < hi) { // the first sample whose span ends after s
        const mid = (lo + hi) >> 1;
        if (s < (arc[mid] + arc[mid + 1]) / 2) hi = mid;
        else lo = mid + 1;
      }
      cols.push({ i: lo });
    }
  }
  return cols;
}

/** A column's value in a row: the sample's, or interpolated; null for a gap. */
function columnValue(k, row, col) {
  const values = k.values[row];
  if (k.location === 'point') {
    const a = values[col.j];
    const b = values[col.j + 1];
    if (a == null || b == null) return null;
    return a + col.f * (b - a);
  }
  return values[col.i];
}

/** The span of arc length a sample stands for: halfway to each neighbour, and the ends of the line at the ends. */
function sampleSpan(arc, i) {
  const n = arc.length;
  return [i === 0 ? arc[0] : (arc[i - 1] + arc[i]) / 2, i === n - 1 ? arc[n - 1] : (arc[i] + arc[i + 1]) / 2];
}

/** One line of the note under the line's field: while drawing, how to draw; else what to know about the kymograph. */
function updateKymoNote(message, warn = false) {
  let text = message;
  if (text == null) {
    const k = state.kymo;
    if (state.lineDraft && lineKind() === 'membrane') {
      text = `Click on or beside the membrane to add points (${state.lineDraft.length} so far): the curve follows `
        + 'the membrane between them, the shorter way round. Enter or double-click finishes, Backspace removes the '
        + 'last, Esc cancels. Or type the points above and press Enter.';
    } else if (state.lineDraft) {
      text = `Click the view to add vertices (${state.lineDraft.length} so far): Enter or double-click finishes, `
        + 'Backspace removes the last, Esc cancels. Or type the vertices above and press Enter.';
    } else if (k) {
      const notes = [];
      if (!k.samples.inDomain.some(Boolean)) {
        notes.push(`the line lies outside ${k.domain}`);
        warn = true;
      }
      if (k.tstep > 1) {
        notes.push(`every ${k.tstep}${k.tstep === 2 ? 'nd' : k.tstep === 3 ? 'rd' : 'th'} saved time: the full kymograph is over the server's limit on values`);
        warn = true;
      }
      if (k.sampling === 'dda') {
        notes.push("one sample per voxel crossed, by a voxel walk (the desktop's sampling can't take this line, so there is no membrane-crossing correction)");
      } else if (k.sampling === 'voxel-crossing') {
        notes.push("one sample per voxel crossed, two at each membrane (the desktop's sampling); hatched: outside the domain");
      } else if (k.sampling === 'membrane' && k.location === 'cell') {
        notes.push(`${k.samples.arcLength.length} samples along the membrane`
          + (k.slice ? ` in the ${k.slice.axis} slice ${k.slice.index}` : '')
          + ": one per membrane element, each its element's value (the desktop's membrane curve)");
      } else if (k.sampling === 'membrane' && k.location === 'point') {
        notes.push(`${k.samples.arcLength.length} samples along the membrane: the mesh vertices between the picks, `
          + 'each pick snapped onto the membrane; interpolated linearly between vertices');
      } else if (k.sampling === 'uniform') {
        notes.push(`${k.samples.arcLength.length} evenly spaced samples, `
          + (k.location === 'point' ? 'interpolated linearly within each mesh cell' : 'each the value of the cell holding it')
          + '; hatched: outside the domain');
      }
      if (k.movingMesh) {
        notes.push('fixed line (lab frame): the mesh moves through it, so the gaps show where the boundary has passed');
      }
      text = notes.join(' · ');
    } else {
      text = '';
    }
  }
  el.kymoNote.textContent = text;
  el.kymoNote.classList.toggle('warn', warn);
}

/** The kymograph's and the profile's x-axis title. */
const distanceTitle = (k) => (k?.sampling === 'membrane' ? 'distance along the membrane (µm)' : 'distance along line (µm)');

/**
 * The kymograph: an image with one row per returned time (the first at the top, as the desktop draws it)
 * and the line's arc length across, W = min(1024, 4·n) columns scaled up with `image-rendering: pixelated`;
 * ticked axes, a colour bar and a time-row cursor drawn over it in SVG; and the line profile at the current
 * time below. Gaps are transparent, over a hatch.
 */
function renderKymograph() {
  const k = state.kymo;
  const svg = el.kymoSvg;
  const canvas = el.kymoCanvas;
  const has = !!k;
  for (const b of [el.kymoSamplesCsv, el.kymoMatrixCsv, el.kymoDesktopCsv, el.kymoPng]) b.disabled = !has;
  if (!k) {
    svg.replaceChildren();
    el.kymoProfile.replaceChildren();
    canvas.width = 1;
    canvas.height = 1;
    canvas.hidden = true;
    state.kymoView = null;
    el.kymoTitle.textContent = state.line ? `${state.selectedVar} · ${state.selectedDomain}` : 'kymograph';
    el.kymoReadout.textContent = '';
    updateKymoNote();
    return;
  }
  canvas.hidden = false;
  const arc = k.samples.arcLength;
  const n = arc.length;
  const rows = k.values.length;
  const Wpx = Math.max(360, Math.round(el.kymoPlot.clientWidth || 640));
  const Hpx = Math.round(el.kymoPlot.clientHeight || 260);
  const M = { l: 70, r: 96, t: 10, b: 36 };
  const plotW = Wpx - M.l - M.r;
  const plotH = Hpx - M.t - M.b;
  Object.assign(canvas.style, { left: `${M.l}px`, top: `${M.t}px`, width: `${plotW}px`, height: `${plotH}px` });

  // the image
  const W = Math.min(1024, 4 * n);
  canvas.width = W;
  canvas.height = rows;
  const [lo, hi] = kymoRange();
  const cols = columnSamples(k, W);
  const ctx = canvas.getContext('2d');
  const image = ctx.createImageData(W, rows);
  const px = image.data;
  let gaps = false;
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < W; c++) {
      const v = columnValue(k, r, cols[c]);
      const o = 4 * (r * W + c);
      if (v == null || !Number.isFinite(v)) {
        gaps = true;
        continue; // transparent: the hatch behind shows through
      }
      const e = 3 * lutIndex(v, lo, hi);
      px[o] = KYMO_LUT[e];
      px[o + 1] = KYMO_LUT[e + 1];
      px[o + 2] = KYMO_LUT[e + 2];
      px[o + 3] = 255;
    }
  }
  ctx.putImageData(image, 0, 0);

  // the axes, colour bar and cursor
  svg.setAttribute('viewBox', `0 0 ${Wpx} ${Hpx}`);
  svg.replaceChildren();
  const mk = (tag, attrs, text, parent = svg) => {
    const node = document.createElementNS(SVG_NS, tag);
    for (const [key, val] of Object.entries(attrs)) node.setAttribute(key, val);
    if (text != null) node.textContent = text;
    parent.appendChild(node);
    return node;
  };
  const a0 = arc[0];
  const a1 = arc[n - 1] > a0 ? arc[n - 1] : a0 + 1;
  const sx = (s) => M.l + ((s - a0) / (a1 - a0)) * plotW;
  const rowY = (r) => M.t + ((r + 0.5) / rows) * plotH;
  const defs = mk('defs', {});
  const hatch = mk('pattern', { id: 'kymoHatch', width: 6, height: 6, patternUnits: 'userSpaceOnUse',
    patternTransform: 'rotate(45)' }, null, defs);
  mk('rect', { width: 3, height: 6, fill: '#9a9aa3' }, null, hatch);
  mk('rect', { x: 3, width: 3, height: 6, fill: '#c8c8cf' }, null, hatch);
  const grad = mk('linearGradient', { id: 'kymoGradient', x1: 0, y1: 1, x2: 0, y2: 0 }, null, defs);
  for (let i = 0; i <= 32; i++) {
    const e = 3 * Math.min(255, Math.round((i / 32) * 255));
    mk('stop', { offset: (i / 32).toFixed(4), 'stop-color': `rgb(${KYMO_LUT[e]},${KYMO_LUT[e + 1]},${KYMO_LUT[e + 2]})` }, null, grad);
  }
  mk('rect', { class: 'axis', x: M.l, y: M.t, width: plotW, height: plotH, fill: 'none' });
  const xt = niceTicks(a0, a1);
  for (const s of xt.ticks) {
    mk('line', { class: 'axis', x1: sx(s), x2: sx(s), y1: M.t + plotH, y2: M.t + plotH + 4 });
    mk('text', { class: 'lbl', x: sx(s), y: M.t + plotH + 15, 'text-anchor': 'middle' }, tickLabel(s, xt.step));
  }
  mk('text', { class: 'title', x: M.l + plotW / 2, y: Hpx - 4, 'text-anchor': 'middle' }, distanceTitle(k));
  // rows are the saved times, not a uniform time scale: label a handful of rows with their real times
  const labelled = rows <= 6 ? [...Array(rows).keys()]
    : [...new Set([0, 1, 2, 3, 4, 5].map((q) => Math.round((q * (rows - 1)) / 5)))];
  const tStep = rows > 1 ? Math.abs(k.times[rows - 1] - k.times[0]) / 5 : 1;
  for (const r of labelled) {
    mk('line', { class: 'axis', x1: M.l - 4, x2: M.l, y1: rowY(r), y2: rowY(r) });
    mk('text', { class: 'lbl', x: M.l - 6, y: rowY(r) + 4, 'text-anchor': 'end' }, tickLabel(k.times[r], tStep || 1));
  }
  mk('text', { class: 'title', x: 0, y: 0, 'text-anchor': 'middle',
    transform: `translate(12 ${M.t + plotH / 2}) rotate(-90)` }, 'time (s)');
  const barX = M.l + plotW + 12;
  const barH = gaps ? plotH - 22 : plotH;
  mk('rect', { x: barX, y: M.t, width: 12, height: barH, fill: 'url(#kymoGradient)' });
  mk('text', { class: 'lbl', x: barX + 16, y: M.t + 9 }, hi.toExponential(2));
  mk('text', { class: 'lbl', x: barX + 16, y: M.t + barH }, lo.toExponential(2));
  if (gaps) {
    mk('rect', { class: 'no-data', x: barX, y: M.t + plotH - 12, width: 12, height: 12 });
    mk('text', { class: 'lbl', x: barX + 16, y: M.t + plotH - 2 }, 'no data');
  }
  const under = mk('line', { class: 'time-cursor-under', x1: M.l, x2: M.l + plotW });
  const over = mk('line', { class: 'time-cursor', x1: M.l, x2: M.l + plotW });
  state.kymoView = { rows, W, cols, rowY, sx, M, plotW, cursor: [under, over], lo, hi };

  const length = Number(k.pathLength.toPrecision(4));
  el.kymoTitle.textContent = `${k.name} · ${k.domain} · ${n} samples × ${rows} times · `
    + (k.sampling === 'membrane' ? `curve ${length} µm along the membrane` : `line ${length} µm`)
    + (k.movingMesh ? ' · fixed line (lab frame)' : '');
  updateKymoNote();
  updateKymoTime();
}

/** The row showing the slider's time: the one whose saved-time index is nearest (every row, unless strided). */
function kymoRowAt(timeIndex) {
  const idx = state.kymo.timeIndices;
  let best = 0;
  for (let r = 1; r < idx.length; r++) if (Math.abs(idx[r] - timeIndex) < Math.abs(idx[best] - timeIndex)) best = r;
  return best;
}

/** The time-row cursor and the line profile follow the slider. */
function updateKymoTime() {
  const view = state.kymoView;
  if (!view || !state.kymo) return;
  const r = kymoRowAt(state.timeIndex);
  for (const line of view.cursor) {
    line.setAttribute('y1', view.rowY(r).toFixed(1));
    line.setAttribute('y2', view.rowY(r).toFixed(1));
  }
  el.kymoCanvas.dataset.row = String(r);
  renderProfile(r);
}

/**
 * The values along the line at one row, on the kymograph's x scale: steps at the samples' spans for finite
 * volume (a membrane crossing is a jump), straight segments between samples for FEniCSx. Gaps break it.
 */
function renderProfile(row) {
  const k = state.kymo;
  const svg = el.kymoProfile;
  const Wpx = Math.max(360, Math.round(svg.clientWidth || 640));
  const H = 170;
  const M = { l: 70, r: 96, t: 16, b: 34 };
  svg.setAttribute('viewBox', `0 0 ${Wpx} ${H}`);
  svg.replaceChildren();
  const mk = (tag, attrs, text) => {
    const node = document.createElementNS(SVG_NS, tag);
    for (const [key, val] of Object.entries(attrs)) node.setAttribute(key, val);
    if (text != null) node.textContent = text;
    svg.appendChild(node);
    return node;
  };
  const arc = k.samples.arcLength;
  const n = arc.length;
  const a0 = arc[0];
  const a1 = arc[n - 1] > a0 ? arc[n - 1] : a0 + 1;
  const plotW = Wpx - M.l - M.r;
  const sx = (s) => M.l + ((s - a0) / (a1 - a0)) * plotW;
  // one y scale for every row (the kymograph's own range), so the profile moves, not its axis
  let [lo, hi] = k.range;
  if (!(hi > lo)) hi = lo + (Math.abs(lo) || 1);
  const pad = 0.04 * (hi - lo);
  lo -= pad;
  hi += pad;
  const sy = (v) => H - M.b - ((v - lo) / (hi - lo)) * (H - M.t - M.b);
  const yt = niceTicks(lo, hi, 4);
  for (const v of yt.ticks) {
    mk('line', { class: 'grid', x1: M.l, x2: M.l + plotW, y1: sy(v), y2: sy(v) });
    mk('text', { class: 'lbl', x: M.l - 5, y: sy(v) + 4, 'text-anchor': 'end' }, tickLabel(v, yt.step));
  }
  const xt = niceTicks(a0, a1);
  for (const s of xt.ticks) {
    mk('line', { class: 'axis', x1: sx(s), x2: sx(s), y1: H - M.b, y2: H - M.b + 4 });
    mk('text', { class: 'lbl', x: sx(s), y: H - M.b + 15, 'text-anchor': 'middle' }, tickLabel(s, xt.step));
  }
  mk('line', { class: 'axis', x1: M.l, y1: H - M.b, x2: M.l + plotW, y2: H - M.b });
  mk('line', { class: 'axis', x1: M.l, y1: M.t, x2: M.l, y2: H - M.b });
  mk('text', { class: 'title', x: M.l + plotW / 2, y: H - 3, 'text-anchor': 'middle' }, distanceTitle(k));
  mk('text', { class: 'title', x: M.l, y: 11 }, `${k.name} [${k.domain}] at t = ${k.times[row]} s`);
  const values = k.values[row];
  let seg = [];
  const flush = () => {
    if (seg.length > 1) mk('polyline', { class: 'trace', points: seg.join(' ') });
    seg = [];
  };
  for (let i = 0; i < n; i++) {
    const v = values[i];
    if (v == null || !Number.isFinite(v)) {
      flush();
      continue;
    }
    const y = sy(v).toFixed(1);
    if (k.location === 'point') {
      seg.push(`${sx(arc[i]).toFixed(1)},${y}`);
    } else {
      const [s0, s1] = sampleSpan(arc, i);
      seg.push(`${sx(s0).toFixed(1)},${y}`, `${sx(s1).toFixed(1)},${y}`);
    }
  }
  flush();
}

/** The image cell under a mouse event: {row, col, sample}, or null outside the image. */
function kymoCellAt(e) {
  const view = state.kymoView;
  if (!view) return null;
  const rect = el.kymoCanvas.getBoundingClientRect();
  const fx = (e.clientX - rect.left) / rect.width;
  const fy = (e.clientY - rect.top) / rect.height;
  if (fx < 0 || fx >= 1 || fy < 0 || fy >= 1) return null;
  const row = Math.min(view.rows - 1, Math.floor(fy * view.rows));
  const col = Math.min(view.W - 1, Math.floor(fx * view.W));
  return { row, col, sample: view.cols[col].i };
}

/** Click: move the slider to the row's time. Shift-click: a probe at the sample, for its time course. */
el.kymoCanvas.addEventListener('click', (e) => {
  const at = kymoCellAt(e);
  const k = state.kymo;
  if (!at || !k || !state.ready) return;
  if (e.shiftKey) {
    // a finite-volume sample probes its voxel's centre, the point P2's probes use; else the sample's point
    const cell = k.samples.cell?.[at.sample] ?? -1;
    const point = cell >= 0 && k.domain === state.selectedDomain && state.cellList?.[cell]
      ? cellCentroid(cell) : k.samples.points.slice(3 * at.sample, 3 * at.sample + 3);
    addProbe(point, true);
    return;
  }
  const index = k.timeIndices[at.row];
  if (index === state.timeIndex) return;
  el.time.value = String(index);
  el.time.dispatchEvent(new Event('input'));
  el.time.dispatchEvent(new Event('change'));
});
el.kymoCanvas.addEventListener('mousemove', (e) => {
  const at = kymoCellAt(e);
  const k = state.kymo;
  if (!at || !k) {
    el.kymoReadout.textContent = '';
    return;
  }
  const s = k.samples.arcLength[0] + ((at.col + 0.5) / state.kymoView.W)
    * (k.samples.arcLength[k.samples.arcLength.length - 1] - k.samples.arcLength[0]);
  const v = columnValue(k, at.row, state.kymoView.cols[at.col]);
  el.kymoReadout.textContent = `d = ${Number(s.toPrecision(4))} µm · t = ${k.times[at.row]} s · `
    + (v == null ? `no data (outside ${k.domain})` : `${k.name} = ${v.toExponential(4)}`)
    + ' — click: go to this time · shift-click: probe this point';
});
el.kymoCanvas.addEventListener('mouseleave', () => { el.kymoReadout.textContent = ''; });

el.kymoRange.addEventListener('change', () => {
  const user = el.kymoRange.value === 'user';
  el.kymoMin.hidden = !user;
  el.kymoMax.hidden = !user;
  if (user && state.kymoView) {
    el.kymoMin.value = String(state.kymoView.lo);
    el.kymoMax.value = String(state.kymoView.hi);
  }
  if (state.kymo) renderKymograph();
});
for (const input of [el.kymoMin, el.kymoMax]) {
  input.addEventListener('change', () => { if (state.kymo) renderKymograph(); });
}

// --- export (made in the page: nothing goes over the network but the raw refetch for the desktop CSV) ---

function saveBlob(blob, name) {
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = name;
  document.body.appendChild(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(a.href), 1000);
}

const csvCell = (v) => (v == null || (typeof v === 'number' && !Number.isFinite(v)) ? '' : String(v));

function kymoComments(k) {
  return [
    `# sim: ${state.dataset.sim}`,
    `# job: ${state.dataset.job}`,
    `# var: ${k.name}`,
    `# domain: ${k.domain}`,
    `# sampling: ${k.sampling}`,
    `# path: ${k.line.vertices.map((v) => v.join(',')).join('; ')}`,
    ...(k.tstep > 1 ? [`# tstep: ${k.tstep}`] : []),
  ];
}

/** kymo-samples.csv: one row per sample. */
function kymoSamplesCsv(k) {
  const s = k.samples;
  const lines = [...kymoComments(k), 'i,arcLength,x,y,z,volumeIndex,membraneIndex,inDomain'];
  s.arcLength.forEach((a, i) => {
    lines.push([i, a, s.points[3 * i], s.points[3 * i + 1], s.points[3 * i + 2],
      csvCell(s.volumeIndex?.[i]), csvCell(s.membraneIndex?.[i]), s.inDomain[i]].join(','));
  });
  return lines.join('\n') + '\n';
}

/** kymo-matrix.csv: the arc lengths, then one row per time; gaps are empty fields. */
function kymoMatrixCsv(k) {
  const lines = [...kymoComments(k), ['time\\arcLength', ...k.samples.arcLength].join(',')];
  k.times.forEach((t, r) => lines.push([t, ...k.values[r].map(csvCell)].join(',')));
  return lines.join('\n') + '\n';
}

/**
 * The desktop kymograph's display resampling, ported from KymographPanel.initStandAloneTimeSeries_private:
 * n evenly spaced distances from 0 to the last arc length, each taking the nearer of the two samples it
 * falls between (nearest neighbour, ties to the left). Exactly its arithmetic, so its Copy output and this
 * agree value for value. `values` is rows × samples.
 */
function desktopResample(arc, values) {
  const n = arc.length;
  const incr = arc[n - 1] / (n - 1);
  const distances = new Array(n);
  const out = values.map((row) => {
    const res = new Array(n);
    let sourceIndex = 0;
    let currentDistance = 0;
    for (let k = 0; k < n; k++) {
      while (currentDistance > arc[sourceIndex + 1]) sourceIndex++;
      const subShort = currentDistance - arc[sourceIndex];
      const subLong = arc[sourceIndex + 1] - arc[sourceIndex];
      const proportion = subShort / subLong;
      res[k] = row[sourceIndex + (proportion > 0.5 ? 1 : 0)];
      distances[k] = currentDistance;
      currentDistance += incr;
      if (currentDistance > arc[n - 1]) currentDistance = arc[n - 1];
    }
    return res;
  });
  return { distances, values: out };
}

/**
 * kymo-desktop-resampled.csv: the RAW kymograph (raw=1: values outside the domain too, as the desktop
 * shows them) resampled as the desktop resamples it, in the layout of its Copy output: a Distances row,
 * then one row per time.
 */
async function kymoDesktopCsv(k) {
  const { data, tstep } = await requestKymograph(k.line, { raw: true, tstep: k.tstep });
  const r = desktopResample(data.samples.arcLength, data.values);
  const lines = [
    ...kymoComments({ ...data, line: k.line, tstep }),
    '# the desktop kymograph\'s resampling (KymographPanel): raw values, nearest neighbour, evenly spaced distances',
    ['Distances', ...r.distances].join(','),
    'Times',
  ];
  data.times.forEach((t, row) => lines.push([t, ...r.values[row].map(csvCell)].join(',')));
  return lines.join('\n') + '\n';
}

/** The image as PNG: each row and column scaled up by whole pixels, so a few times still make a picture. */
function saveKymoPng() {
  const src = el.kymoCanvas;
  const sxf = Math.max(1, Math.ceil(512 / src.width));
  const syf = Math.max(1, Math.ceil(256 / src.height));
  const out = document.createElement('canvas');
  out.width = src.width * sxf;
  out.height = src.height * syf;
  const ctx = out.getContext('2d');
  ctx.imageSmoothingEnabled = false;
  ctx.drawImage(src, 0, 0, out.width, out.height);
  out.toBlob((blob) => { if (blob) saveBlob(blob, `${state.kymo.name}-kymograph.png`); }, 'image/png');
}

const kymoCsvBlob = (text) => new Blob([text], { type: 'text/csv' });
el.kymoSamplesCsv.addEventListener('click', () => {
  if (state.kymo) saveBlob(kymoCsvBlob(kymoSamplesCsv(state.kymo)), `${state.kymo.name}-kymo-samples.csv`);
});
el.kymoMatrixCsv.addEventListener('click', () => {
  if (state.kymo) saveBlob(kymoCsvBlob(kymoMatrixCsv(state.kymo)), `${state.kymo.name}-kymo-matrix.csv`);
});
el.kymoDesktopCsv.addEventListener('click', async () => {
  const k = state.kymo;
  if (!k) return;
  try {
    saveBlob(kymoCsvBlob(await kymoDesktopCsv(k)), `${k.name}-kymo-desktop-resampled.csv`);
  } catch (e) {
    setStatus('desktop CSV failed: ' + (e?.message ?? e), true);
  }
});
el.kymoPng.addEventListener('click', () => { if (state.kymo) saveKymoPng(); });


/** Fresh axes + scales in the Stats plot's SVG; renderStatsPlot draws on top of this. */
function plotFrame(times, lo, hi) {
  if (hi - lo < 1e-300) hi = lo + 1;
  const W = 640;
  const H = 220;
  const M = { l: 8, r: 8, t: 8, b: 18 };
  const sx = (t) => M.l + ((t - times[0]) / (times[times.length - 1] - times[0] || 1)) * (W - M.l - M.r);
  const sy = (v) => H - M.b - ((v - lo) / (hi - lo)) * (H - M.t - M.b);
  el.plotSvg.innerHTML = '';
  const mk = (tag, attrs, text) => {
    const n = document.createElementNS(SVG_NS, tag);
    for (const [k, v] of Object.entries(attrs)) n.setAttribute(k, v);
    if (text != null) n.textContent = text;
    el.plotSvg.appendChild(n);
    return n;
  };
  mk('line', { class: 'axis', x1: M.l, y1: H - M.b, x2: W - M.r, y2: H - M.b });
  mk('line', { class: 'axis', x1: M.l, y1: M.t, x2: M.l, y2: H - M.b });
  mk('text', { class: 'lbl', x: M.l + 4, y: M.t + 10 }, hi.toExponential(2));
  mk('text', { class: 'lbl', x: M.l + 4, y: H - M.b - 4 }, lo.toExponential(2));
  mk('text', { class: 'lbl', x: W - M.r - 4, y: H - 4, 'text-anchor': 'end' }, `t = ${times[times.length - 1]}`);
  mk('text', { class: 'lbl', x: M.l + 4, y: H - 4 }, `t = ${times[0]}`);
  return { mk, sx, sy, lo };
}

/** the Stats plot keeps to a handful of variables, so it stays readable */
const STATS_MAX_VARIABLES = 6;

/**
 * Item 9 (#1859): per-variable spatial min/max/mean over time — mean as a solid curve, the
 * min-to-max envelope as a translucent band in the same color. One shared scale across all
 * series, so the curves are directly comparable.
 */
function renderStatsPlot(stats) {
  const { times, series } = stats;
  if (!times?.length || !series?.length) return;
  const finite = series.flatMap((s) => [...s.min, ...s.max]).filter((v) => v != null && Number.isFinite(v));
  if (!finite.length) return;
  const f = plotFrame(times, Math.min(...finite), Math.max(...finite));
  series.forEach((s, k) => {
    const color = SERIES_COLORS[k % SERIES_COLORS.length];
    const fwd = times.map((t, i) => `${f.sx(t).toFixed(1)},${f.sy(s.min[i] ?? f.lo).toFixed(1)}`);
    const back = [...times.keys()].reverse().map((i) => `${f.sx(times[i]).toFixed(1)},${f.sy(s.max[i] ?? f.lo).toFixed(1)}`);
    f.mk('path', { d: `M${fwd.join('L')}L${back.join('L')}Z`, fill: color, 'fill-opacity': '0.15', stroke: 'none' });
    f.mk('polyline', {
      class: 'curve', stroke: color,
      points: times.map((t, i) => `${f.sx(t).toFixed(1)},${f.sy(s.mean[i] ?? f.lo).toFixed(1)}`).join(' '),
    });
  });
  el.plotTitle.textContent = `min / mean / max over space · ${times.length} timepoints`
    + (stats.weighting === 'measure' ? ' · area-weighted over the moving domain'
      : stats.weighting === 'integral' ? ' · mean = finite-element integral over the domain' : '');
  el.plotLegend.innerHTML = '';
  series.forEach((s, k) => {
    const item = document.createElement('span');
    const swatch = document.createElement('span');
    swatch.className = 'swatch';
    swatch.style.background = SERIES_COLORS[k % SERIES_COLORS.length];
    item.appendChild(swatch);
    item.appendChild(document.createTextNode(s.name));
    el.plotLegend.appendChild(item);
  });
  el.plotPanel.hidden = false;
}

async function plotStats() {
  if (!state.ready || !state.dataset) return;
  try {
    // the server defaults to all volume variables; ask for a handful so the plot stays readable
    const vars = state.variables.slice(0, STATS_MAX_VARIABLES).map((v) => v.name);
    setStatus(`fetching space statistics for ${vars.length} variable(s)…`);
    const stats = await fetchJson(url('/stats', { var: vars.join(',') }), '/stats');
    renderStatsPlot(stats);
    setStatus(`${describe()} ✓`);
  } catch (e) {
    setStatus('stats failed: ' + (e?.message ?? e), true);
  }
}

el.statsBtn.addEventListener('click', () => void plotStats());
el.plotClose.addEventListener('click', () => { el.plotPanel.hidden = true; });

// ---------------------------------------------------------------------------
// smoothing
// ---------------------------------------------------------------------------

/**
 * Maps the slider to filter parameters, anchored so the default position reproduces the server's
 * reference values exactly.
 *
 * Below nominal we only reduce the iteration count (0 iterations is an exact pass-through). Above
 * nominal we add iterations and narrow the pass-band geometrically. The pass-band is never raised
 * above nominal: WindowedSinc diverges as it approaches 1.0 (measured — a pass-band of 1.0 displaced
 * points by 26 units where the reference moves 0.45).
 */
function smoothingFor(strength) {
  const nom = state.nominalSinc ?? { iterations: 16, feature_angle: 120, pass_band: 0.05 };
  if (strength <= NOMINAL_STRENGTH) {
    return { iterations: Math.round(nom.iterations * (strength / NOMINAL_STRENGTH)), passBand: nom.pass_band };
  }
  const f = (strength - NOMINAL_STRENGTH) / (100 - NOMINAL_STRENGTH);
  const maxIters = Math.max(MAX_ITERATIONS, nom.iterations);
  const minPass = Math.min(MIN_PASS_BAND, nom.pass_band);
  return {
    iterations: Math.round(nom.iterations + f * (maxIters - nom.iterations)),
    passBand: nom.pass_band * Math.pow(minPass / nom.pass_band, f),
  };
}

const formatPassBand = (v) => (v >= 0.01 ? v.toFixed(3) : v.toExponential(1));

/** Update the readout while dragging, without re-running the filter. */
/** The smoothing controls: off, with a note, for a mesh drawn as it is (body-fitted, or a membrane's faces). */
function updateSmoothingControls() {
  el.smoothing.disabled = state.bodyFitted || state.membraneMesh;
  if (state.bodyFitted) return; // set once at startup
  if (state.membraneMesh) {
    el.smoothingReadout.textContent = 'membrane faces — shown as computed';
    el.smoothingReset.disabled = true;
  } else {
    previewSmoothing(state.smoothing);
  }
}

function previewSmoothing(strength) {
  if (state.bodyFitted || state.membraneMesh) return; // the readout explains the mode; there is nothing to preview
  state.smoothing = strength;
  const p = smoothingFor(strength);
  let note = '';
  if (strength === NOMINAL_STRENGTH) note = ' <em>VCell nominal</em>';
  else if (strength === 0) note = ' <em>off — raw voxel surface</em>';
  el.smoothingReadout.innerHTML =
    `${p.iterations} iters · pass-band ${formatPassBand(p.passBand)}${note}`;
  el.smoothingReset.disabled = !state.ready || state.busy || strength === NOMINAL_STRENGTH;
}

async function applySmoothing(strength) {
  previewSmoothing(strength);
  if (!state.ready || state.busy || !sinc) return;
  state.busy = true;
  const t0 = performance.now();
  try {
    const p = smoothingFor(strength);
    await sinc.setNumberOfIterations(p.iterations);
    await sinc.setPassBand(p.passBand);
    // the deform filter re-executes downstream at render, re-shaping the ONE mesh that the
    // shell, the crop and the display-mesh statistics all derive from
    await render();
    await updateCropStats();
    setStatus(`smoothing: ${p.iterations} iters · pass-band ${formatPassBand(p.passBand)} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('smoothing update failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
    previewSmoothing(state.smoothing);
  }
}

// ---------------------------------------------------------------------------
// control wiring
// ---------------------------------------------------------------------------

async function refreshField() {
  if (!state.ready || state.busy || !state.dataset) return;
  state.busy = true;
  const t0 = performance.now();
  try {
    await applyField(await loadField());
    await render();
    await updateCropStats(); // new values, same mesh
    setStatus(`${describe()} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('field update failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
  }
}

async function rebuildGeometry(resetCam = true) {
  if (!state.ready || state.busy || !state.dataset) return;
  state.busy = true;
  const t0 = performance.now();
  try {
    const geometry = await loadGeometry();
    await buildGrid(geometry, await loadField());
    if (resetCam) {
      await renderer.resetCamera();
      state.pivot = null;
    }
    await render();
    setStatus(`${describe()} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('geometry update failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
  }
}

// ---------------------------------------------------------------------------
// refresh: a run still in progress keeps writing output times
// ---------------------------------------------------------------------------

const AUTO_REFRESH_MS = 10000;
let autoRefreshTimer = null;

/**
 * Re-read /info and take in the output times written since the last look. If the view was on the
 * last time, it moves to the new last time -- following the run as it goes -- and otherwise stays
 * where the user put it. The variable, domain, camera, slice and mesh style are all kept. The server
 * re-reads the bundle's manifest on every request, so a new row is visible as soon as it is written.
 */
async function refreshRun({ auto = false } = {}) {
  if (!state.ready || !state.dataset) return;
  if (state.busy) {
    if (auto) scheduleAutoRefresh();
    return;
  }
  try {
    const info = await fetchJson(url('/info', {}), '/info');
    const wasAtEnd = state.timeIndex >= state.times.length - 1;
    const before = state.times.length;
    state.times = info.times ?? state.times;
    showRunStatus(info);
    el.time.max = String(Math.max(0, state.times.length - 1));
    el.time.disabled = state.times.length < 2;
    if (state.times.length > before && state.probes.length) scheduleProbeFetch(); // the traces grow too
    // a kymograph is not refetched every 10 s: it says it is out of date, and recomputes on request
    if (state.times.length > before && state.kymo) {
      state.kymoStale = true;
      el.kymoStale.hidden = false;
    }
    if (state.times.length > before && wasAtEnd) {
      state.timeIndex = state.times.length - 1;
      el.time.value = String(state.timeIndex);
      await (state.bodyFitted ? refreshTimeStep() : refreshField());
    } else {
      state.timeIndex = Math.min(state.timeIndex, state.times.length - 1);
      el.time.value = String(state.timeIndex);
      if (!auto) {
        const added = state.times.length - before;
        setStatus(added > 0 ? `${describe()} ✓ — ${added} new time${added === 1 ? '' : 's'} (slider extended)`
          : `${describe()} ✓ — no new output`);
      }
    }
  } catch (e) {
    if (!auto) setStatus('refresh failed: ' + (e?.message ?? e), true);
    else console.warn('auto-refresh failed', e);
  }
  scheduleAutoRefresh();
}

/** Poll while the run is in progress (and the page is visible); stop once it completes or fails. */
function scheduleAutoRefresh() {
  clearTimeout(autoRefreshTimer);
  autoRefreshTimer = null;
  if (state.runStatus !== 'running') return;
  autoRefreshTimer = setTimeout(() => {
    if (document.hidden) scheduleAutoRefresh();
    else void refreshRun({ auto: true });
  }, AUTO_REFRESH_MS);
}

el.refreshBtn.addEventListener('click', () => void refreshRun());
document.addEventListener('visibilitychange', () => {
  if (!document.hidden && state.runStatus === 'running') void refreshRun({ auto: true });
});

el.time.addEventListener('input', () => {
  state.timeIndex = Number(el.time.value);
  el.dataReadout.textContent = `t = ${state.times[state.timeIndex] ?? ''}`;
  if (state.probes.length) renderProbes(); // the time cursor and the list's current values
  if (state.kymo) updateKymoTime(); // the kymograph's time-row cursor and its line profile
});
el.time.addEventListener('change', () => {
  state.timeIndex = Number(el.time.value);
  void (state.bodyFitted ? refreshTimeStep() : refreshField());
});

/**
 * Body-fitted time step: fetch the field first and let its geometryId decide. A static mesh
 * (Chombo) matches and only the colors change; a per-time mesh (MovingBoundary) differs and the
 * geometry is rebuilt — with the camera kept where the user put it.
 */
async function refreshTimeStep() {
  if (!state.ready || state.busy || !state.dataset) return;
  state.busy = true;
  const t0 = performance.now();
  try {
    const field = await loadField(true);
    if (field.geometryId !== state.geometryId) {
      const geometry = await loadGeometry();
      await buildGrid(geometry, field);
    } else {
      await applyField(field);
    }
    await render();
    setStatus(`${describe()} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('time step failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
  }
}
el.variable.addEventListener('change', () => {
  const chosen = state.variables.find((v) => v.name === el.variable.value);
  if (!chosen || state.busy) return;
  state.selectedVar = chosen.name;
  // probes are lab-frame points, so they carry over to the new variable (and domain): one request
  if (state.probes.length) scheduleProbeFetch();
  if (state.line) scheduleKymoFetch(); // the line too (debounced: the domain may change just below)
  // switching variable can also switch domain — a nuclear species lives on different geometry from
  // a cytosolic one — in which case the grid is rebuilt, not just recoloured
  if (chosen.domain && chosen.domain !== state.selectedDomain) {
    state.selectedDomain = chosen.domain;
    void rebuildGeometry();
  } else {
    void refreshField();
  }
});
/** Show/hide an annotation prop. visibilityOn/Off: the boolean setter marshals awkwardly. */
async function toggleProp(prop, on) {
  if (!prop || !state.ready) return;
  try {
    if (on) await prop.visibilityOn();
    else await prop.visibilityOff();
    await render();
  } catch (e) {
    console.warn('toggling annotation failed', e);
  }
}

el.colorbar.addEventListener('change', () => void toggleProp(scalarBar, el.colorbar.checked));
el.axes.addEventListener('change', () => void toggleProp(cubeAxes, el.axes.checked));

// VTK property representations (vtkProperty.h): points 0, wireframe 1, surface 2
const REPRESENTATION = { surface: 2, edges: 2, wireframe: 1 };

/**
 * Show the mesh: 'surface' (the colored field only), 'edges' (the field with the triangles' edges drawn
 * over it — for a 3D mesh, the boundary's and the slice's), or 'wireframe' (edges only, colored by the
 * field). One property on the one actor, so it holds across time, variable and slice changes.
 */
async function applyMeshStyle(style) {
  if (!actor || !state.ready) return;
  try {
    const property = await actor.getProperty();
    await property.setRepresentation(REPRESENTATION[style] ?? 2);
    await property.setEdgeVisibility(style === 'edges' ? 1 : 0);
    await property.setEdgeColor(0.08, 0.08, 0.1);
    await property.setLineWidth(1.0);
    await render();
  } catch (e) {
    console.warn('changing the mesh style failed', e);
  }
}

el.meshStyle.addEventListener('change', () => void applyMeshStyle(el.meshStyle.value));
document.addEventListener('keydown', (event) => {
  if (event.key !== 'm' || event.target instanceof HTMLInputElement || event.target instanceof HTMLSelectElement) return;
  if (el.meshStyle.disabled) return;
  const options = Array.from(el.meshStyle.options).map((o) => o.value);
  el.meshStyle.value = options[(options.indexOf(el.meshStyle.value) + 1) % options.length];
  void applyMeshStyle(el.meshStyle.value);
});

el.sliceAxis.addEventListener('change', () => {
  state.sliceAxis = el.sliceAxis.value === '' ? -1 : Number(el.sliceAxis.value);
  el.slicePos.disabled = state.sliceAxis < 0;
  el.cutMode.disabled = state.sliceAxis < 0;
  updateLineTool(); // a curve along a 3D finite-volume membrane needs the cut plane
  void refreshCrop();
});
el.cutMode.addEventListener('change', () => {
  state.cutMode = el.cutMode.value;
  void refreshCrop();
});
el.slicePos.addEventListener('input', () => {
  state.slicePos = Number(el.slicePos.value);
  void refreshCrop();
});

el.smoothing.addEventListener('input', () => previewSmoothing(Number(el.smoothing.value)));
el.smoothing.addEventListener('change', () => void applySmoothing(Number(el.smoothing.value)));
el.smoothingReset.addEventListener('click', () => {
  el.smoothing.value = String(NOMINAL_STRENGTH);
  void applySmoothing(NOMINAL_STRENGTH);
});

// ---------------------------------------------------------------------------
// startup
// ---------------------------------------------------------------------------

/**
 * What this browser lacks for the viewer, or null. The vtk.wasm bundle is linked with JSPI (WebAssembly
 * JavaScript Promise Integration: `WebAssembly.Suspending` / `WebAssembly.promising`), because VTK links its
 * WebAssembly module asynchronously whenever the WebGPU renderer is built in. A browser without JSPI —
 * Safari 18 and earlier, Chrome and Edge before 137, older Firefox — fails deep inside the loader with
 * "undefined is not a constructor (evaluating 'new WebAssembly.Suspending(…)')". Say so up front instead.
 * Rendering is WebGL2.
 */
function missingBrowserSupport() {
  const missing = [];
  if (typeof WebAssembly === 'undefined' || typeof WebAssembly.Suspending !== 'function'
      || typeof WebAssembly.promising !== 'function') {
    missing.push('WebAssembly JSPI (JavaScript Promise Integration)');
  }
  if (!document.createElement('canvas').getContext('webgl2')) missing.push('WebGL 2');
  if (!missing.length) return null;
  return `This browser can't run the field viewer: it lacks ${missing.join(' and ')}.\n`
    + 'Use a current Safari (26 or later), Chrome or Edge (137 or later), or Firefox — updating this one may be '
    + 'enough — or copy this page\'s address into one of them:\n' + window.location.href;
}

(async () => {
  try {
    const t0 = performance.now();
    if (!window.vtkwasm) throw new Error('vtkwasm global not available — vendor/vtk.umd.js did not load');
    const unsupported = missingBrowserSupport();
    if (unsupported) {
      setStatus(unsupported, true);
      const notice = document.createElement('div'); // where the picture would be, not only below the controls
      notice.className = 'notice';
      notice.textContent = unsupported;
      el.box.append(notice);
      console.error('vcell field viewer: unsupported browser —', navigator.userAgent);
      return;
    }
    state.dataset = datasetFromSearch(window.location.search);
    if (!state.dataset) {
      throw new Error('no dataset given. Open this page with ?sim=<simulationKey>&job=<n> — the '
        + 'VCell client does that for you from "View in 3D".');
    }
    previewSmoothing(NOMINAL_STRENGTH);

    setStatus('loading vtk.wasm bundle…');
    const runtime = await window.vtkwasm.loadAsync({ url: BUNDLE_URL });
    const session = runtime.createStandaloneSession();
    vtk = session.vtk;
    // vtk.wasm otherwise takes the canvas over (stamps position:absolute + 100%/100% on it and
    // installs a resize observer that pins the drawing buffer back to its default). The loader's own
    // RemoteSession turns both off; the standalone session does not, so do it here.
    try {
      session.wasmModule?._setDefaultExpandVTKCanvasToContainer?.(false);
      session.wasmModule?._setDefaultInstallHTMLResizeObserver?.(false);
    } catch (e) {
      console.warn('could not disable the canvas takeover', e);
    }

    await loadInfo();
    const geometry = await loadGeometry();
    const field = await loadField();
    await buildScene(geometry, field);

    state.ready = true;
    el.variable.disabled = false;
    el.time.disabled = state.times.length < 2;
    el.smoothing.disabled = state.bodyFitted;
    el.colorbar.disabled = false;
    el.axes.disabled = false;
    el.meshStyle.disabled = false;
    el.sliceAxis.disabled = false; // body-fitted 3D included: the crop clips the solver mesh
    el.statsBtn.disabled = false;
    el.refreshBtn.disabled = false;
    // probes and lines need a picker, which every mode has: the voxel walk (finite volume), the 2D camera point,
    // and the 3D cell pick (tetrahedra, voxels and polyhedra)
    el.addProbes.disabled = false;
    // kymographs (docs/plan-plotting.md P4–P7): every mode; a MovingBoundary line is fixed in the lab frame, and
    // on a 2D membrane the line is a curve along it
    updateLineTool();
    // the desktop's resampling of the raw (unmasked) values: finite-volume parity only
    el.kymoDesktopCsv.hidden = state.bodyFitted;
    scheduleAutoRefresh();
    if (state.bodyFitted) {
      el.smoothingReadout.textContent = 'body-fitted solver mesh — shown as computed';
      el.smoothingReset.disabled = true;
    }
    previewSmoothing(state.smoothing);
    updateSmoothingControls(); // a run that opens on a membrane variable
    setStatus(`rendered ${describe()} ✓ (${Math.round(performance.now() - t0)} ms) — ${state.dimension === 2 ? 'drag to pan' : 'drag to rotate, shift- or right-drag to pan'}, wheel to zoom, click to probe`);
  } catch (e) {
    setStatus('viewer failed: ' + (e?.message ?? e), true);
    console.error('vcell field viewer', e);
  }
})();
