#!/usr/bin/env node
// Build-time fetch of VCell's VTK.wasm bundle into webapp-ng's assets, so the field viewer loads it
// SAME-ORIGIN (GitHub release assets can't be fetched cross-origin from the browser — no CORS header).
// Idempotent: skips if the pinned tag is already present. Env:
//   VTK_WASM_TAG     release tag in virtualcell/vcell-vtk-wasm (default v1.0.0)
//   VTK_WASM_BUNDLE  use a local .tar.gz instead of downloading (offline/dev)
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFileSync } from 'node:child_process';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const DEST = path.resolve(HERE, '../src/assets/vtk-wasm');   // served at /assets/vtk-wasm/
const TAG = process.env.VTK_WASM_TAG ?? 'v1.0.0';
const ASSET = 'vcell-vtk-wasm32-emscripten.tar.gz';
const URL = `https://github.com/virtualcell/vcell-vtk-wasm/releases/download/${TAG}/${ASSET}`;
const MARKER = path.join(DEST, '.vtk-wasm-tag');

if (fs.existsSync(path.join(DEST, 'vtkWebAssembly.wasm')) &&
    fs.existsSync(MARKER) && fs.readFileSync(MARKER, 'utf8').trim() === TAG) {
  console.log(`[vtk-wasm] already present (${TAG}) at ${path.relative(process.cwd(), DEST)}`);
  process.exit(0);
}
fs.mkdirSync(DEST, { recursive: true });
const tmp = path.join(os.tmpdir(), `${ASSET}.${process.pid}`);
try {
  if (process.env.VTK_WASM_BUNDLE) {
    console.log(`[vtk-wasm] using local bundle ${process.env.VTK_WASM_BUNDLE}`);
    fs.copyFileSync(process.env.VTK_WASM_BUNDLE, tmp);
  } else {
    console.log(`[vtk-wasm] downloading ${TAG} from ${URL}`);
    const r = await fetch(URL);
    if (!r.ok) throw new Error(`download failed HTTP ${r.status} (is the ${TAG} release published?)`);
    fs.writeFileSync(tmp, Buffer.from(await r.arrayBuffer()));
  }
  execFileSync('tar', ['-xzf', tmp, '-C', DEST, 'vtkWebAssembly.mjs', 'vtkWebAssembly.wasm'], { stdio: 'inherit' });
  fs.writeFileSync(MARKER, TAG + '\n');
  const mb = (fs.statSync(path.join(DEST, 'vtkWebAssembly.wasm')).size / 1048576).toFixed(0);
  console.log(`[vtk-wasm] ready: ${path.relative(process.cwd(), DEST)} (wasm ${mb} MB, tag ${TAG})`);
} finally {
  fs.rmSync(tmp, { force: true });
}
