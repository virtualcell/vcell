/**
 * VCell field viewer — see docs/3d-renderer-design.md.
 *
 * Runs VCell's finite-volume surface-smoothing pipeline entirely CLIENT-SIDE: build the raw
 * whole-voxel unstructured grid in memory from server-sent arrays → vtkGeometryFilter (extract the
 * boundary surface) → vtkWindowedSincPolyDataFilter (the faithful FV smoothing) → render via WebGL2,
 * through a custom VTK-compiled-to-WebAssembly bundle (virtualcell/vcell-vtk-wasm).
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
  selectedVar: '',
  selectedDomain: '',
  geometryId: '',
  bounds: null,
  nominalSinc: null,
  smoothing: NOMINAL_STRENGTH,
  ready: false,
  busy: false,
  drawing: false,
};

// vtk handles, kept so controls can update the scene without rebuilding it
let vtk = null;
let geomFilter = null;
let sinc = null;
let mapper = null;
let actor = null;
let renderer = null;
let renderWindow = null;
let camera = null;
let fieldArray = null;
let lut = null;
let scalarBar = null;
let cubeAxes = null;

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

function nearestTimeIndex(t) {
  let best = 0;
  for (let i = 1; i < state.times.length; i++) {
    if (Math.abs(state.times[i] - t) < Math.abs(state.times[best] - t)) best = i;
  }
  return best;
}

async function loadGeometry() {
  setStatus(`loading geometry for ${state.selectedDomain}…`);
  const geometry = await fetchJson(url('/grid', { domain: state.selectedDomain }), '/grid');
  state.geometryId = geometry.geometryId;
  return geometry;
}

async function loadField() {
  const time = state.times[state.timeIndex];
  const field = await fetchJson(
    url('/field', { domain: state.selectedDomain, var: state.selectedVar, time: String(time) }), '/field');
  // values are only meaningful against the geometry they were computed for; pairing them with a
  // different one would draw something silently wrong rather than obviously broken
  if (state.geometryId && field.geometryId !== state.geometryId) {
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
  const points = vtk.vtkPoints();
  await points.setNumberOfPoints(geometry.numPoints);
  const P = geometry.points;
  for (let i = 0; i < geometry.numPoints; i++) await points.setPoint(i, P[3 * i], P[3 * i + 1], P[3 * i + 2]);
  const cellArray = vtk.vtkCellArray();
  for (const cell of geometry.cells) await cellArray.insertNextCell(cell.length, cell);
  const ug = vtk.vtkUnstructuredGrid();
  await ug.setPoints(points);
  await ug.setCells(geometry.cellType, cellArray); // single cell type (VTK_VOXEL)

  fieldArray = null;
  if (field) {
    const arr = vtk.vtkDoubleArray();
    await arr.setName(field.name);
    await arr.setNumberOfComponents(1);
    await arr.setNumberOfTuples(field.values.length);
    // NB: setValue() is NOT in the marshalled invoker whitelist ("SetValue is not permitted");
    // setTuple1(i, v) is the permitted per-element scalar setter in the standalone session.
    for (let i = 0; i < field.values.length; i++) await arr.setTuple1(i, field.values[i] ?? 0);
    await (await ug.getCellData()).setScalars(arr);
    fieldArray = arr;
  }
  await geomFilter.setInputData(ug);

  if (field) {
    await mapper.setScalarModeToUseCellData();
    await mapper.scalarVisibilityOn(); // no-arg form; the boolean setter marshals awkwardly
    await applyFieldRange(field);
  } else {
    // no field → solid surface (via the property method; actor.property.color is a no-op)
    await (await actor.getProperty()).setColor(0.30, 0.65, 0.45);
  }
  state.bounds = boundsOf(geometry);
  if (cubeAxes) await cubeAxes.setBounds(...state.bounds);
  state.nominalSinc = geometry.sinc;
  previewSmoothing(state.smoothing);
}

/** Same geometry, new values: rewrite the scalars in place rather than rebuilding the grid. */
async function applyField(field) {
  if (!fieldArray) return;
  await fieldArray.setName(field.name);
  await fieldArray.setNumberOfTuples(field.values.length);
  for (let i = 0; i < field.values.length; i++) await fieldArray.setTuple1(i, field.values[i] ?? 0);
  await fieldArray.modified();
  await applyFieldRange(field);
}

async function buildScene(geometry, field) {
  geomFilter = vtk.vtkGeometryFilter();

  sinc = vtk.vtkWindowedSincPolyDataFilter();
  await sinc.setInputConnection(await geomFilter.getOutputPort());
  await sinc.boundarySmoothingOff();
  await sinc.featureEdgeSmoothingOff();
  await sinc.nonManifoldSmoothingOff();
  await sinc.normalizeCoordinatesOn();
  await sinc.setNumberOfIterations(geometry.sinc.iterations);
  await sinc.setFeatureAngle(geometry.sinc.feature_angle);
  await sinc.setPassBand(geometry.sinc.pass_band);

  // Surface normals so the mapper shades smoothly (Gouraud) instead of flat/faceted.
  let surfacePort = await sinc.getOutputPort();
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
  el.canvas.addEventListener('pointerdown', (e) => {
    if (e.button !== 0) return;
    dragging = true; lastX = e.clientX; lastY = e.clientY;
    el.canvas.setPointerCapture(e.pointerId);
    e.preventDefault();
  });
  el.canvas.addEventListener('pointermove', (e) => {
    if (!dragging) return;
    const dx = e.clientX - lastX;
    const dy = e.clientY - lastY;
    lastX = e.clientX; lastY = e.clientY;
    if (dx || dy) void orbit(dx, dy);
    e.preventDefault();
  });
  const release = (e) => {
    if (!dragging) return;
    dragging = false;
    try { el.canvas.releasePointerCapture(e.pointerId); } catch { /* already released */ }
  };
  el.canvas.addEventListener('pointerup', release);
  el.canvas.addEventListener('pointercancel', release);
  el.canvas.addEventListener('wheel', (e) => {
    void dolly(e.deltaY < 0 ? 1.1 : 1 / 1.1);
    e.preventDefault();
  }, { passive: false });
}

async function orbit(dx, dy) {
  if (!camera || state.drawing) return;
  state.drawing = true;
  try {
    await camera.azimuth(-dx * 0.5);
    await camera.elevation(dy * 0.5);
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
  const nom = state.nominalSinc ?? { iterations: 12, feature_angle: 120, pass_band: 0.05 };
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
    await sinc.update();
    await renderWindow.render();
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
    setStatus(`${describe()} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('field update failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
  }
}

async function rebuildGeometry() {
  if (!state.ready || state.busy || !state.dataset) return;
  state.busy = true;
  const t0 = performance.now();
  try {
    const geometry = await loadGeometry();
    await buildGrid(geometry, await loadField());
    await renderer.resetCamera();
    await renderWindow.render();
    setStatus(`${describe()} ✓ (${Math.round(performance.now() - t0)} ms)`);
  } catch (e) {
    setStatus('geometry update failed: ' + (e?.message ?? e), true);
  } finally {
    state.busy = false;
  }
}

el.time.addEventListener('input', () => {
  state.timeIndex = Number(el.time.value);
  el.dataReadout.textContent = `t = ${state.times[state.timeIndex] ?? ''}`;
});
el.time.addEventListener('change', () => {
  state.timeIndex = Number(el.time.value);
  void refreshField();
});
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
    el.smoothing.disabled = false;
    el.colorbar.disabled = false;
    el.axes.disabled = false;
    previewSmoothing(state.smoothing);
    setStatus(`rendered ${describe()} ✓ (${Math.round(performance.now() - t0)} ms) — drag to rotate, wheel to zoom`);
  } catch (e) {
    setStatus('viewer failed: ' + (e?.message ?? e), true);
    console.error('vcell field viewer', e);
  }
})();
