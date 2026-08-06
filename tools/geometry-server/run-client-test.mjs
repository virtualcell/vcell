#!/usr/bin/env node
// Headless check that the dependency-free client renders what the geometry server serves.
//
// Serves client.html over http (a file:// origin is "null" and cannot fetch cross-origin),
// points it at a running geometry server, and asserts the page reports a successful render.
// Exits 0 on pass, 1 on failure.
//
// Requires a running server, e.g.:
//   GEOM_SECRET=dev-secret <pyvcell>/.venv/bin/python geomserver.py
//
// Env:
//   GEOM_BASE       geometry server base URL (default http://127.0.0.1:9200)
//   GEOM_SECRET     shared secret, to mint the capability token (default dev-secret)
//   SIM, JOB        simulation / job (default 946368938 / 0)
//   DOMAIN, VAR     domain and variable (default cytosol / cytosol::C_cyt)
//   TIME            timepoint (default 1.0)
//   CHROME          Chrome executable (default macOS Google Chrome)
//   PUPPETEER_CORE  path to a puppeteer-core ESM entry (default webapp-ng/node_modules)
//   SHOT            screenshot output path (default ./client-render.png)
import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { fileURLToPath, pathToFileURL } from 'node:url';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const REPO = path.resolve(HERE, '../..');
const PORT = 4350;
const BASE = process.env.GEOM_BASE ?? 'http://127.0.0.1:9200';
const SECRET = process.env.GEOM_SECRET ?? 'dev-secret';
const SIM = process.env.SIM ?? '946368938';
const JOB = process.env.JOB ?? '0';
const DOMAIN = process.env.DOMAIN ?? 'cytosol';
const VAR = process.env.VAR ?? 'cytosol::C_cyt';
const TIME = process.env.TIME ?? '1.0';
const SHOT = process.env.SHOT ?? path.join(HERE, 'client-render.png');
const CHROME = process.env.CHROME ?? '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome';
const PUP = process.env.PUPPETEER_CORE
  ?? path.join(REPO, 'webapp-ng/node_modules/puppeteer-core/lib/esm/puppeteer/puppeteer-core.js');

try {
  const r = await fetch(`${BASE}/health`);
  if (!r.ok) throw new Error(String(r.status));
} catch (e) {
  console.error(`ERROR: no geometry server at ${BASE} (${e.message}). Start geomserver.py first.`);
  process.exit(2);
}
const { default: puppeteer } = await import(pathToFileURL(PUP).href);

// Fixed single route: the served path is a constant, never derived from the request
// (no path traversal -- CodeQL js/path-injection).
const server = http.createServer((req, res) => {
  if (req.url.split('?')[0] !== '/client.html') { res.writeHead(404); res.end('not found'); return; }
  const body = fs.readFileSync(path.join(HERE, 'client.html'));
  res.writeHead(200, { 'content-type': 'text/html', 'content-length': body.length });
  res.end(body);
});
await new Promise((r) => server.listen(PORT, r));

const token = crypto.createHmac('sha256', SECRET).update(String(SIM)).digest('hex').slice(0, 32);
const q = new URLSearchParams({ base: BASE, sim: SIM, job: JOB, domain: DOMAIN, var: VAR, time: TIME, token });
const browser = await puppeteer.launch({
  executablePath: CHROME, headless: 'new',
  args: ['--use-gl=swiftshader', '--enable-unsafe-swiftshader', '--no-sandbox', '--window-size=1100,950'],
});
let ok = false;
try {
  const page = await browser.newPage();
  await page.setViewport({ width: 1100, height: 950 });
  await page.goto(`http://localhost:${PORT}/client.html?${q}`, { waitUntil: 'domcontentloaded', timeout: 60000 });
  for (let i = 0; i < 120; i++) {
    const s = await page.evaluate(() => window.__status || '').catch(() => '');
    if (/rendered|FAILED/.test(s)) break;
    await new Promise((r) => setTimeout(r, 500));
  }
  const status = await page.evaluate(() => window.__status);
  ok = await page.evaluate(() => window.__ok === true);
  await page.screenshot({ path: SHOT });
  console.log(status);
  console.log(ok ? `PASS (screenshot ${SHOT})` : 'FAIL');
} finally {
  // headless Chrome can hang on close; never let that mask the result
  await Promise.race([browser.close(), new Promise((r) => setTimeout(r, 5000))]);
  server.close();
}
process.exit(ok ? 0 : 1);
