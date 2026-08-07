# Geometry-server spike

A **spike**, not a component. It exists to de-risk the alternative to
[`tools/vtk-wasm`](../vtk-wasm/): instead of shipping raw voxel arrays and doing the finite-volume
surface extraction in the browser, extract the smoothed surface **server-side** — where VTK and
pyvista already live — and send finished geometry to a light client.

Nothing here is wired into the build, the app, or CI. See PR #1829 for the framing.

## Design

Deliberately **stateless**: no trame, no sessions, no per-client render context. Every response is
a pure function of its URL, so ordinary HTTP caching does the work a session would otherwise do.

The split that makes that work is that the two costs have different lifetimes:

| endpoint | cost | depends on | cacheability |
|---|---|---|---|
| `/surface?sim&job&domain` | extraction + smoothing | the mesh only | immutable; `Cache-Control: immutable` + ETag |
| `/field?sim&job&domain&var&time` | a data read | variable + timepoint | per-request; **no VTK on this path at all** |

`/surface` also returns `origCellIds`, mapping each surface cell back to its domain voxel, so the
client gathers `field[origCellIds[i]]`. That is the entire client-side computation.

## Authorization

The service reads simulation data straight off disk, so on its own it would serve any user's
private simulation to anyone who guesses a sim id. **It must never be publicly reachable on its own
authority.** Two deployment shapes work:

1. **Quarkus proxies it.** `vcell-rest` authenticates (OIDC/Auth0) and authorizes (`GroupAccess`),
   then calls this service over a cluster-internal address and streams the bytes back.
2. **Quarkus mints a capability.** Same authn/authz, then it signs a token scoped to one simulation
   which this service verifies statelessly with a shared secret. The browser fetches directly.

This spike implements shape 2, because that was the part needing proof. Note the measurements
below weaken its main justification: proxying ~160 KB through Java is cheap, and shape 1 has no
shared secret, no signed-URL leakage surface, and no expiry-versus-caching tension.

The token is scoped to the **simulation id** and nothing finer, because that is the granularity
`GroupAccess` actually grants — per-job or per-domain tokens would guard a boundary that does not
exist. Authentication is the easy, portable half; **authorization is the hard half and lives in
Java and the database. Do not reimplement `GroupAccess` in Python.**

## Findings

- **Correct.** The served surface matches the committed golden fixture in
  `tools/vtk-wasm/test/fixtures/golden_points.json` to 8.3e-07 (JSON rounding), so the server-side
  and wasm client-side pipelines provably agree.
- **Client weight is the real argument.** `client.html` is the *entire* client — no vtk.js, no VTK,
  no wasm, just WebGL2 — at **6.8 KB raw / 3.1 KB gzipped**, roughly **3,900×** smaller than the
  12 MB wasm bundle.
- **Bandwidth is not the argument.** Gzipped, the surface is 159 KB against 198 KB for the raw
  voxel grid the wasm path ships — only ~20% smaller, because raw integer grids compress well.
  Uncompressed it is 7×, but nothing serves uncompressed.
- **`/field` is slow: 2–4 s per timepoint**, and not from cold start — repeat calls for the same
  timepoint are equally slow. It traces to pyvcell's `pde_dataset.get_data()` (~2 s per call, no
  caching, returning 126,025 values where 12,210 are needed). For comparison the Java field server
  in `vcell-client` produces the whole grid *and* field in ~200 ms. Fixable in pyvcell's reader,
  but as it stands this path cannot drive an interactive time slider.

## Running it

Needs pyvcell importable (its `.venv` has the reference VTK), same as
`tools/vtk-wasm/test/gen_golden.py`.

```bash
# 1. serve
GEOM_SECRET=dev-secret \
GEOM_DATA_DIR=/path/to/pyvcell/tests/fixtures/data/solver_output \
  /path/to/pyvcell/.venv/bin/python geomserver.py

# 2. render it headlessly and screenshot
npm install                                   # puppeteer-core, drives headless Chrome
GEOM_SECRET=dev-secret npm run test:client
```

`run-client-test.mjs` is verification only — it drives real headless Chrome because a WebGL render
cannot be checked with `curl`. Neither the server nor `client.html` depends on it.

`GEOM_DATA_DIR` defaults to a sibling pyvcell checkout if one is found. `GEOM_ALLOW_ANON=1`
disables capability checks for local poking; the server logs loudly and refuses to start without
either that or `GEOM_SECRET`.

Mint a token by hand with:

```bash
python3 -c "import hmac,hashlib;print(hmac.new(b'dev-secret',b'946368938',hashlib.sha256).hexdigest()[:32])"
```
