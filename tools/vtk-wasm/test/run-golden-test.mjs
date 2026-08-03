#!/usr/bin/env node
// Headless runner for the vtk.wasm WindowedSinc golden-file test.
//
// Serves this directory (golden-test.html + fixtures + vtk-umd.js) plus the wasm bundle,
// runs golden-test.html in headless Chrome, and asserts the client output matches the golden.
// Exits 0 on pass, 1 on failure/regression.
//
// Required:
//   BUNDLE_TARGZ   path to the vtk.wasm bundle (vcell-vtk-wasm-*.tar.gz from the
//                  "Build custom VTK.wasm bundle" GitHub Actions artifact, or a local build)
// Optional:
//   CHROME          Chrome/Chromium executable (default: macOS Google Chrome)
//   PUPPETEER_CORE  path to a puppeteer-core ESM entry (default: webapp-ng/node_modules)
//   TOL             max allowed max-deviation vs golden (default 1e-6; reference is bit-exact = 0)
import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const REPO = path.resolve(HERE, '../../..');
const PORT = 4343;
const TOL = Number(process.env.TOL ?? 1e-6);
const CHROME = process.env.CHROME ?? '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome';
const PUP = process.env.PUPPETEER_CORE
  ?? path.join(REPO, 'webapp-ng/node_modules/puppeteer-core/lib/esm/puppeteer/puppeteer-core.js');
const BUNDLE = process.env.BUNDLE_TARGZ;

if (!BUNDLE || !fs.existsSync(BUNDLE)) {
  console.error('ERROR: set BUNDLE_TARGZ to the vtk.wasm bundle .tar.gz (see header).');
  process.exit(2);
}
const { default: puppeteer } = await import(pathToFileURL(PUP).href);

const MIME = { '.html':'text/html', '.js':'text/javascript', '.mjs':'text/javascript',
  '.wasm':'application/wasm', '.json':'application/json', '.gz':'application/gzip' };
// Fixed allow-list: the request path only selects from constant file paths, so the served path
// never derives from user input (no path traversal — CodeQL js/path-injection).
const ROUTES = {
  '/golden-test.html': path.join(HERE, 'golden-test.html'),
  '/vtk-umd.js': path.join(HERE, 'vtk-umd.js'),
  '/fixtures/input_surface.json': path.join(HERE, 'fixtures', 'input_surface.json'),
  '/fixtures/golden_points.json': path.join(HERE, 'fixtures', 'golden_points.json'),
  '/bundle.tar.gz': BUNDLE,
};
const server = http.createServer((req, res) => {
  const url = decodeURIComponent(req.url.split('?')[0]);
  const headers = {   // cross-origin isolation for wasm
    'Cross-Origin-Opener-Policy': 'same-origin',
    'Cross-Origin-Embedder-Policy': 'require-corp',
    'Cross-Origin-Resource-Policy': 'same-origin',
  };
  const file = Object.prototype.hasOwnProperty.call(ROUTES, url) ? ROUTES[url] : null;
  if (!file) { res.writeHead(404, headers); res.end('not found'); return; }
  const ext = file.endsWith('.tar.gz') ? '.gz' : path.extname(file);
  headers['content-type'] = MIME[ext] || 'application/octet-stream';
  fs.readFile(file, (err, buf) => {
    if (err) { res.writeHead(404, headers); res.end('not found'); return; }
    res.writeHead(200, headers); res.end(buf);
  });
});
await new Promise((r) => server.listen(PORT, r));

const browser = await puppeteer.launch({
  executablePath: CHROME, headless: true,
  args: ['--headless=new','--no-sandbox','--disable-setuid-sandbox','--use-gl=angle',
    '--use-angle=swiftshader','--enable-unsafe-swiftshader','--ignore-gpu-blocklist',
    '--enable-webgl','--enable-features=SharedArrayBuffer'],
});
let result = null, err = null;
try {
  const page = await browser.newPage();
  page.on('pageerror', e => console.error('[pageerror]', e.message));
  if (process.env.VERBOSE) page.on('console', m => console.error('[page]', m.text().split('\n').pop()));
  page.on('requestfailed', r => console.error('[reqfail]', r.url(), r.failure()?.errorText || ''));
  await page.goto(`http://localhost:${PORT}/golden-test.html`, { waitUntil: 'domcontentloaded', timeout: 60000 });
  try {
    await page.waitForFunction(() => window.__gt && window.__gt.done, { timeout: 600000, polling: 500 });
  } catch (e) {
    const gt = await page.evaluate(() => window.__gt).catch(() => null);
    console.error('TIMEOUT — last stage:', gt?.stage, '\nlog tail:\n' + (gt?.log || []).slice(-8).join('\n'));
    throw e;
  }
  const gt = await page.evaluate(() => window.__gt);
  result = gt.result; err = gt.err;
} catch (e) { err = err || (e && e.message) || String(e); }
// Bounded cleanup: headless Chrome's close() can hang; don't let it block the report/exit.
await Promise.race([browser.close().catch(() => {}), new Promise((r) => setTimeout(r, 5000))]);
try { server.close(); } catch {}

console.log('=== vtk.wasm WindowedSinc golden-file test ===');
if (err || !result) { console.error('FAILED to run:', err || 'no result'); process.exit(1); }
console.log(JSON.stringify(result, null, 2));
const pass = result.countMatch
  && result.maxDeviation_vs_golden <= TOL
  && result.maxMovement_output_vs_input > 0.1;   // filter must have actually smoothed
console.log(pass ? `PASS (max deviation ${result.maxDeviation_vs_golden} <= tol ${TOL})`
                 : `FAIL (countMatch=${result.countMatch}, maxDev=${result.maxDeviation_vs_golden}, tol=${TOL}, moved=${result.maxMovement_output_vs_input})`);
process.exit(pass ? 0 : 1);
