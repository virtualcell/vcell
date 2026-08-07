#!/usr/bin/env node
// Build-time fetch of VCell's VTK.wasm bundle into webapp-viewer's assets, so the field viewer loads it
// SAME-ORIGIN (GitHub release assets can't be fetched cross-origin from the browser — no CORS header).
// The @kitware/vtk-wasm loader's loadAsync({url}) takes the .tar.gz and untars it in-browser
// (DecompressionStream + untar), so we serve the .tar.gz as-is. Idempotent. Env:
//   VTK_WASM_TAG     release tag in virtualcell/vcell-vtk-wasm (default v1.1.0)
//   VTK_WASM_BUNDLE  use a local .tar.gz instead of downloading (offline/dev)
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const DEST = path.resolve(HERE, '../assets/vtk-wasm');   // served at assets/vtk-wasm/
const TAG = process.env.VTK_WASM_TAG ?? 'v1.1.0';
const ASSET = 'vcell-vtk-wasm32-emscripten.tar.gz';
const TARGET = path.join(DEST, ASSET);
const URL = `https://github.com/virtualcell/vcell-vtk-wasm/releases/download/${TAG}/${ASSET}`;
const MARKER = path.join(DEST, '.vtk-wasm-tag');

if (fs.existsSync(TARGET) && fs.existsSync(MARKER) && fs.readFileSync(MARKER, 'utf8').trim() === TAG) {
  console.log(`[vtk-wasm] already present (${TAG}) at ${path.relative(process.cwd(), TARGET)}`);
  process.exit(0);
}
fs.mkdirSync(DEST, { recursive: true });
if (process.env.VTK_WASM_BUNDLE) {
  console.log(`[vtk-wasm] using local bundle ${process.env.VTK_WASM_BUNDLE}`);
  fs.copyFileSync(process.env.VTK_WASM_BUNDLE, TARGET);
} else {
  console.log(`[vtk-wasm] downloading ${TAG} from ${URL}`);
  const r = await fetch(URL);
  if (!r.ok) { console.error(`[vtk-wasm] download failed HTTP ${r.status} (is the ${TAG} release published?)`); process.exit(1); }
  fs.writeFileSync(TARGET, Buffer.from(await r.arrayBuffer()));
}
fs.writeFileSync(MARKER, TAG + '\n');
const mb = (fs.statSync(TARGET).size / 1048576).toFixed(1);
console.log(`[vtk-wasm] ready: ${path.relative(process.cwd(), TARGET)} (${mb} MB, tag ${TAG})`);
