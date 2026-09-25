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
  statsBtn: document.getElementById('statsBtn'),
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
  runProgress: null, // fraction 0..1 while running
  selectedVar: '',
  selectedDomain: '',
  geometryId: '',
  dimension: 3,
  bodyFitted: false, // MovingBoundary runs: solver's body-fitted mesh, geometry varies per time
  // 'cell' (finite-volume data: one value per cell) or 'point' (FEniCSx P1 data: one value per mesh
  // vertex, interpolated across each cell); set by the field the server sends
  fieldLocation: 'cell',
  bounds: null,
  sliceAxis: -1,
  slicePos: 50,
  cutMode: 'smooth', // 'smooth' (clip through the cells) or 'cells' (keep whole cells)
  cellPoints: null, // the grid's point coordinates and cells, for choosing the whole cells to keep
  cellList: null,
  pivot: null, // 3D rotation center: the scene center; a pan does not move it (null: take the focal point)
  pick: null, // Cartesian occupancy index of the current grid, for mouse picking
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
  state.times = info.times ?? [];
  showRunStatus(info);
  state.variables = (info.variables ?? []).map((v) => ({ name: v.name, domain: v.domain }));
  if (!state.variables.length) throw new Error(`run ${info.simId} exposes no volume variables`);

  const requested = state.dataset.variable;
  const chosen = (requested ? state.variables.find((v) => v.name === requested) : null) ?? state.variables[0];
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

/** Cell under the given ray, honoring the crop: cells above an active cut are not pickable. */
function castRay(origin, dir) {
  const pk = state.pick;
  if (!pk) return -1;
  const b = state.bounds;
  // slab-clip the ray to the grid bounds
  let t0 = 0;
  let t1 = Infinity;
  for (let a = 0; a < 3; a++) {
    if (Math.abs(dir[a]) < 1e-12) {
      if (origin[a] < b[2 * a] || origin[a] > b[2 * a + 1]) return -1;
      continue;
    }
    let near = (b[2 * a] - origin[a]) / dir[a];
    let far = (b[2 * a + 1] - origin[a]) / dir[a];
    if (near > far) [near, far] = [far, near];
    t0 = Math.max(t0, near);
    t1 = Math.min(t1, far);
  }
  if (t0 > t1) return -1;
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
  for (let guard = pk.n[0] + pk.n[1] + pk.n[2] + 3; guard > 0; guard--) {
    const cell = pk.occ.get(i[0] + pk.n[0] * (i[1] + pk.n[1] * i[2]));
    if (cell !== undefined) {
      if (cropAxis < 0) return cell;
      const center = pk.o[cropAxis] + (i[cropAxis] + 0.5) * pk.d[cropAxis];
      if (center <= cropPos) return cell;
    }
    const a = tMax[0] <= tMax[1] ? (tMax[0] <= tMax[2] ? 0 : 2) : (tMax[1] <= tMax[2] ? 1 : 2);
    i[a] += step[a];
    if (i[a] < 0 || i[a] >= pk.n[a] || tMax[a] > t1) return -1;
    tMax[a] += tDelta[a];
  }
  return -1;
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
  const points = vtk.vtkPoints();
  await points.setNumberOfPoints(geometry.numPoints);
  const P = geometry.points;
  for (let i = 0; i < geometry.numPoints; i++) await points.setPoint(i, P[3 * i], P[3 * i + 1], P[3 * i + 2]);
  const ug = vtk.vtkUnstructuredGrid();
  await ug.setPoints(points);
  state.cellPoints = P;
  state.cellList = geometry.cells;
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
  if (state.bodyFitted) {
    // body-fitted: the mesh IS the solver's geometry — no smoothing, no deform; the crop clips
    // this mesh directly, so a cut exposes the solver's own interior cells (voxels and cut tets)
    currentUg = ug;
    await tableClip.setInputData(ug);
    await extractCells.setInputData(ug);
    await geomFilter.setInputData(ug);
  } else {
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
  state.pick = buildPickIndex(geometry, state.bounds);
  state.fieldValues = field ? field.values : null;
  if (cubeAxes) await cubeAxes.setBounds(...state.bounds);
  await applyCrop(); // a domain switch changes the bounds the slider position maps into
  state.nominalSinc = geometry.sinc;
  previewSmoothing(state.smoothing);
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
    if (state.bodyFitted) {
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
    await renderWindow.render();
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
      + (state.bodyFitted ? ' (body-fitted solver mesh)'
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
  await renderWindow.render();
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
      if (renderWindow) await renderWindow.render();
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
    // a left press that never really moved is a pick, not an orbit
    if (dragDistance < 4 && e.button === 0) void plotPick(e.clientX, e.clientY);
  };
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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

/**
 * The first point of a body-fitted 3D mesh the mouse ray reaches, as the view shows it: the ray is
 * clipped against each tetrahedron's four faces (and, for the smooth cut, the cut's half-space; for
 * the whole-cells cut, only the kept cells take part), and the nearest entry wins. Returns the point
 * nudged just inside that tetrahedron — a boundary point is ambiguous for a containment test — with
 * its cell and barycentric weights, or null when the ray misses. O(cells) per call, a few ms.
 */
function pickTetrahedron(origin, dir) {
  const P = state.cellPoints;
  const cells = state.cellList;
  if (!P || !cells) return null;
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
    if (cell.length !== 4) continue;
    if (wholeCells && !cell.some((i) => P[3 * i + axis] <= cutPos)) continue;
    const q = cell.map(v);
    let tIn = 0;
    let tOut = Infinity;
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
    for (let f = 0; f < 4 && tIn <= tOut; f++) {
      const [a, b, d] = [0, 1, 2, 3].filter((k) => k !== f).map((k) => q[k]);
      let n = cross3(sub3(b, a), sub3(d, a));
      if (dot3(n, sub3(q[f], a)) > 0) n = [-n[0], -n[1], -n[2]]; // point away from the opposite vertex
      const num = -dot3(n, sub3(origin, a));
      const den = dot3(n, dir);
      if (Math.abs(den) < 1e-300) {
        if (num < 0) tOut = -Infinity; // parallel and outside this face
      } else if (den > 0) {
        tOut = Math.min(tOut, num / den);
      } else {
        tIn = Math.max(tIn, num / den);
      }
    }
    if (tIn > tOut || (best && tIn >= best.t)) continue;
    best = { t: tIn, tOut, cell: c, q };
  }
  if (!best) return null;
  const t = best.t + 1e-3 * (best.tOut - best.t);
  const point = [0, 1, 2].map((a) => origin[a] + t * dir[a]);
  // barycentric weights: the volume of the sub-tetrahedron opposite each vertex
  const vol = (a, b, c2, d) => dot3(sub3(b, a), cross3(sub3(c2, a), sub3(d, a)));
  const [q0, q1, q2, q3] = best.q;
  const total = vol(q0, q1, q2, q3);
  const weights = [vol(point, q1, q2, q3), vol(q0, point, q2, q3), vol(q0, q1, point, q3), vol(q0, q1, q2, point)]
    .map((w) => w / total);
  return { point, cell: best.cell, weights };
}

/** The shown field at a picked tetrahedron point: P1-interpolated, or the cell's value. */
function valueAtTetPick(hit) {
  const values = state.fieldValues;
  if (!values) return null;
  if (state.fieldLocation !== 'point') return values[hit.cell] ?? null;
  const cell = state.cellList[hit.cell];
  let sum = 0;
  for (let k = 0; k < 4; k++) {
    const x = values[cell[k]];
    if (x == null) return null;
    sum += hit.weights[k] * x;
  }
  return sum;
}

let hoverBusy = false;
async function hoverPick(clientX, clientY) {
  if (!state.ready || hoverBusy) return;
  if (state.bodyFitted && state.dimension === 3) {
    hoverBusy = true;
    try {
      const { origin, dir } = await rayFromMouse(clientX, clientY);
      const hit = pickTetrahedron(origin, dir);
      if (!hit) {
        el.pickReadout.textContent = '';
        return;
      }
      const value = valueAtTetPick(hit);
      const at = ` @ (${hit.point.map((x) => x.toFixed(2)).join(', ')})`;
      el.pickReadout.textContent = (value == null ? `no data${at}` : `${state.selectedVar} = ${value.toExponential(3)}${at}`)
        + ' — click to plot time course';
    } catch (e) {
      console.warn('hover pick failed', e);
    } finally {
      hoverBusy = false;
    }
    return;
  }
  if (state.bodyFitted) {
    // no occupancy index on a body-fitted mesh; the readout shows where a click would sample
    if (state.dimension !== 2) return;
    hoverBusy = true;
    try {
      const [x, y] = await labPointFromMouse(clientX, clientY);
      el.pickReadout.textContent = `lab (${x.toFixed(2)}, ${y.toFixed(2)}) — click to plot time course`;
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
    const cell = castRay(origin, dir);
    if (cell < 0) {
      el.pickReadout.textContent = '';
      return;
    }
    const v = state.fieldValues?.[cell];
    const c = cellCenter(cell);
    const at = c ? ` @ (${c.slice(0, state.dimension === 2 ? 2 : 3).map((x) => x.toFixed(1)).join(', ')})` : '';
    el.pickReadout.textContent =
      v == null ? `no data${at}` : `${state.selectedVar} = ${v.toExponential(3)}${at}`;
  } catch (e) {
    console.warn('hover pick failed', e);
  } finally {
    hoverBusy = false;
  }
}

/**
 * Click → time course at the picked cell. The series comes from /timeseries, which reduces
 * server-side next to the reader — fetching every timestep to build one curve is the design
 * explicitly ruled out in #1859.
 */
async function plotPick(clientX, clientY) {
  if (!state.ready || !state.dataset) return;
  if (state.bodyFitted) return void plotLabPick(clientX, clientY);
  if (!state.pick) return;
  try {
    const { origin, dir } = await rayFromMouse(clientX, clientY);
    const cell = castRay(origin, dir);
    if (cell < 0) return;
    const c = cellCenter(cell);
    setStatus(`fetching time series for ${state.selectedVar} at cell ${cell}…`);
    const series = await fetchJson(
      url('/timeseries', { domain: state.selectedDomain, var: state.selectedVar, cell: String(cell) }),
      '/timeseries');
    renderPlot(series, c);
    setStatus(`${describe()} ✓`);
  } catch (e) {
    setStatus('time-series pick failed: ' + (e?.message ?? e), true);
  }
}

/**
 * Click on a moving-boundary view → time course at that fixed LAB-FRAME point. The point does not
 * follow the material: /timeseries locates it in each saved time's own mesh, and frames where the
 * boundary has moved past it come back as nulls, drawn as gaps in the curve.
 */
async function plotLabPick(clientX, clientY) {
  if (state.dimension === 3) return void plotLabPick3d(clientX, clientY);
  if (state.dimension !== 2) return;
  try {
    const [x, y] = await labPointFromMouse(clientX, clientY);
    setStatus(`fetching time series for ${state.selectedVar} at lab (${x.toFixed(2)}, ${y.toFixed(2)})…`);
    const series = await fetchJson(
      url('/timeseries', { domain: state.selectedDomain, var: state.selectedVar, x: String(x), y: String(y) }),
      '/timeseries');
    renderPlot(series, [x, y, 0]);
    setStatus(`${describe()} ✓`);
  } catch (e) {
    setStatus('time-series pick failed: ' + (e?.message ?? e), true);
  }
}

/** The 3D counterpart: the lab-frame point is where the mouse ray first meets the mesh on screen. */
async function plotLabPick3d(clientX, clientY) {
  try {
    const { origin, dir } = await rayFromMouse(clientX, clientY);
    const hit = pickTetrahedron(origin, dir);
    if (!hit) return;
    const [x, y, z] = hit.point;
    setStatus(`fetching time series for ${state.selectedVar} at lab (${x.toFixed(2)}, ${y.toFixed(2)}, ${z.toFixed(2)})…`);
    const series = await fetchJson(
      url('/timeseries', { domain: state.selectedDomain, var: state.selectedVar, x: String(x), y: String(y), z: String(z) }),
      '/timeseries');
    renderPlot(series, hit.point);
    setStatus(`${describe()} ✓`);
  } catch (e) {
    setStatus('time-series pick failed: ' + (e?.message ?? e), true);
  }
}

const SVG_NS = 'http://www.w3.org/2000/svg';

/** Fresh axes + scales in the plot SVG; both plot modes draw on top of this. */
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

/**
 * Framework-free line plot: polylines in an SVG, min/max labels, nothing else to maintain. Null
 * values break the curve into segments — on a moving-boundary run a null means the domain had
 * moved past the sampled lab point at that time, a physically meaningful gap that must not be
 * drawn through. A segment of one frame renders as a dot so it does not vanish.
 */
function renderPlot(series, center) {
  const { times, values, name } = series;
  const finite = values.filter((v) => v != null && Number.isFinite(v));
  if (!times?.length || !finite.length) return;
  const f = plotFrame(times, Math.min(...finite), Math.max(...finite));
  let seg = [];
  const flush = () => {
    if (seg.length > 1) {
      f.mk('polyline', { class: 'curve', points: seg.join(' ') });
    } else if (seg.length === 1) {
      const [cx, cy] = seg[0].split(',');
      f.mk('circle', { class: 'curve-dot', cx, cy, r: 3 });
    }
    seg = [];
  };
  times.forEach((t, i) => {
    const v = values[i];
    if (v == null || !Number.isFinite(v)) {
      flush();
      return;
    }
    seg.push(`${f.sx(t).toFixed(1)},${f.sy(v).toFixed(1)}`);
  });
  flush();
  const gaps = values.length - finite.length;
  const at = center ? ` @ (${center.slice(0, state.dimension === 2 ? 2 : 3).map((x) => x.toFixed(1)).join(', ')})` : '';
  el.plotTitle.textContent = `${name}${at} · ${times.length} timepoints`
    + (gaps > 0 ? ` · ${gaps} outside the moving domain` : '');
  el.plotLegend.innerHTML = '';
  el.plotPanel.hidden = false;
}

const SERIES_COLORS = ['#2a7', '#d70', '#07c', '#c2c', '#a33', '#578'];

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
    const vars = state.variables.slice(0, SERIES_COLORS.length).map((v) => v.name);
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
function previewSmoothing(strength) {
  if (state.bodyFitted) return; // the readout explains the mode; there is nothing to preview
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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
    await renderWindow.render();
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

(async () => {
  try {
    const t0 = performance.now();
    if (!window.vtkwasm) throw new Error('vtkwasm global not available — vendor/vtk.umd.js did not load');
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
    scheduleAutoRefresh();
    if (state.bodyFitted) {
      el.smoothingReadout.textContent = 'body-fitted solver mesh — shown as computed';
      el.smoothingReset.disabled = true;
    }
    previewSmoothing(state.smoothing);
    setStatus(`rendered ${describe()} ✓ (${Math.round(performance.now() - t0)} ms) — ${state.dimension === 2 ? 'drag to pan' : 'drag to rotate, shift- or right-drag to pan'}, wheel to zoom`);
  } catch (e) {
    setStatus('viewer failed: ' + (e?.message ?? e), true);
    console.error('vcell field viewer', e);
  }
})();
