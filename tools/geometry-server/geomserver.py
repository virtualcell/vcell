#!/usr/bin/env python3
"""Stateless geometry-server spike for VCell finite-volume field visualization.

The alternative to shipping raw voxel arrays and doing the work in the browser: extract the
smoothed boundary surface SERVER-side (where VTK/pyvista already live) and send finished
geometry to a light client that only has to draw it.

Deliberately stateless -- no sessions, no trame, no per-client render context. Every response is
a pure function of its URL, so ordinary HTTP caching does the work a session would otherwise do.

The split that makes this work:

  /surface  expensive, but depends ONLY on (sim, job, domain) -- the mesh never changes for a
            finished run, so it is immutable and cacheable forever. Returns the smoothed surface
            AND `origCellIds`, mapping each surface cell back to its domain voxel.
  /field    cheap: one float per domain voxel, no VTK and no extraction at all. The client
            gathers field[origCellIds[i]] for surface cell i.

That keeps the per-timepoint request small and VTK-free while the costly step is computed once.

AUTHORIZATION. This service reads simulation data straight off disk, so on its own it would
happily serve any user's private simulation to anyone who guesses a sim id. It must never be
publicly reachable on its own authority. Two deployment shapes work:

  1. Quarkus proxies it. vcell-rest authenticates (OIDC/Auth0) and authorizes (GroupAccess),
     then calls this service over a cluster-internal address and streams the bytes back.
     Simple, but every byte of geometry passes through Java.

  2. Quarkus mints a capability. vcell-rest does the same authn/authz, then signs a short-lived
     token scoped to exactly one (sim, job, domain) which this service verifies statelessly with
     a shared secret. The browser then fetches geometry directly. This is the S3-presigned-URL
     pattern: no session, no callback, no ACL logic duplicated in Python -- the signature IS the
     authorization decision, already made by the component that owns it.

Shape 2 is what this spike implements, because authentication is the easy half and AUTHORIZATION
is the hard half: deciding whether a user may read a given simulation needs VCell's permission
model, which lives in Java and the database. A FastAPI service validating Auth0 JWTs itself would
still have to call back into Java to answer "may this user read sim X", at which point it is a
worse version of shape 1. Never reimplement GroupAccess in Python.

Run:  <pyvcell>/.venv/bin/python geomserver.py
Env:  GEOM_DATA_DIR (solver output dir), GEOM_PORT (default 9200),
      GEOM_SECRET (shared secret for capability tokens; required),
      GEOM_ALLOW_ANON=1 (spike-only escape hatch, logs loudly)
"""
import hashlib
import hmac
import io
import json
import os
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import parse_qs, urlparse

import numpy as np
import vtk
from pyvcell._internal.simdata.mesh import CartesianMesh
from pyvcell._internal.simdata.vtk.fv_mesh_mapping import from_mesh3d_volume
from pyvcell._internal.simdata.vtk.vtkmesh_utils import get_volume_vtk_grid
from pyvcell.sim_results.result import Result

PORT = int(os.environ.get("GEOM_PORT", "9200"))
def _default_data_dir() -> Path:
    """pyvcell's checked-in solver output, if this repo sits beside a pyvcell checkout."""
    here = Path(__file__).resolve()
    rel = Path("pyvcell/tests/fixtures/data/solver_output")
    for parent in here.parents:
        candidate = parent.parent / rel
        if candidate.is_dir():
            return candidate
    return Path("/nonexistent-set-GEOM_DATA_DIR")


DATA_DIR = Path(os.environ["GEOM_DATA_DIR"]) if os.environ.get("GEOM_DATA_DIR") else _default_data_dir()

SECRET = os.environ.get("GEOM_SECRET", "")
ALLOW_ANON = os.environ.get("GEOM_ALLOW_ANON") == "1"

# Must match pyvcell's smooth_unstructured_grid_surface (and the wasm client) exactly.
SINC = dict(iterations=12, feature_angle=120.0, pass_band=0.05)
ORIG_CELL_ID = "__origCellId"


# ---------------------------------------------------------------------------
# geometry
# ---------------------------------------------------------------------------

def volume_grid(sim: int, job: int, domain: str):
    """The raw whole-voxel grid for one domain, tagged so cells survive extraction."""
    mesh = CartesianMesh(mesh_file=DATA_DIR / f"SimID_{sim}_{job}_.mesh")
    mesh.read()
    grid = get_volume_vtk_grid(from_mesh3d_volume(mesh, domain))
    n = grid.GetNumberOfCells()
    ids = vtk.vtkIntArray()
    ids.SetName(ORIG_CELL_ID)
    ids.SetNumberOfTuples(n)
    for i in range(n):
        ids.SetTuple1(i, i)
    grid.GetCellData().AddArray(ids)
    return grid


def smoothed_surface(grid):
    """pyvcell's reference pipeline. Cell data rides through, carrying ORIG_CELL_ID."""
    ug = vtk.vtkUnstructuredGridGeometryFilter()
    ug.PassThroughPointIdsOn()
    ug.MergingOff()
    ug.SetInputData(grid)
    ug.Update(0)

    gf = vtk.vtkGeometryFilter()
    gf.SetInputData(ug.GetOutput())
    gf.Update(0)

    sinc = vtk.vtkWindowedSincPolyDataFilter()
    sinc.SetInputData(gf.GetOutput())
    sinc.SetNumberOfIterations(SINC["iterations"])
    sinc.BoundarySmoothingOff()
    sinc.FeatureEdgeSmoothingOff()
    sinc.SetFeatureAngle(SINC["feature_angle"])
    sinc.SetPassBand(SINC["pass_band"])
    sinc.NonManifoldSmoothingOff()
    sinc.NormalizeCoordinatesOn()
    sinc.Update(0)
    return sinc.GetOutput()


def surface_payload(poly, fmt: str):
    """Serialize the surface either as .vtp (native for vtk.js) or as plain JSON."""
    cd = poly.GetCellData().GetArray(ORIG_CELL_ID)
    orig = [int(cd.GetTuple1(i)) for i in range(poly.GetNumberOfCells())] if cd else []

    if fmt == "vtp":
        w = vtk.vtkXMLPolyDataWriter()
        w.SetInputData(poly)
        w.WriteToOutputStringOn()
        w.SetDataModeToBinary()
        w.SetCompressorTypeToZLib()
        w.Write()
        body = w.GetOutputString()
        if isinstance(body, str):
            body = body.encode("utf-8", "surrogateescape")
        # origCellIds ride along as a cell array inside the .vtp itself
        return body, "application/vnd.vtk.vtp"

    pts = poly.GetPoints()
    coords = []
    for i in range(poly.GetNumberOfPoints()):
        coords.extend(poly.GetPoint(i))
    polys, ca, idl = [], poly.GetPolys(), vtk.vtkIdList()
    ca.InitTraversal()
    while ca.GetNextCell(idl):
        polys.append([idl.GetId(k) for k in range(idl.GetNumberOfIds())])
    doc = {
        "numPoints": poly.GetNumberOfPoints(),
        "points": [round(c, 6) for c in coords],
        "polys": polys,
        "origCellIds": orig,
        "sinc": SINC,
    }
    return json.dumps(doc).encode(), "application/json"


# ---------------------------------------------------------------------------
# field values -- no VTK on this path at all
# ---------------------------------------------------------------------------

_result_cache: dict = {}


def result_for(sim: int, job: int) -> Result:
    """Reading the run is a pure function of (sim, job); reuse the reader, not any client state."""
    key = (sim, job)
    if key not in _result_cache:
        _result_cache[key] = Result(solver_output_dir=DATA_DIR, sim_id=sim, job_id=job)
    return _result_cache[key]


def field_values(sim: int, job: int, domain: str, var: str, t: float):
    r = result_for(sim, job)
    full = var if "::" in var else f"{domain}::{var}"
    times = list(r.time_points)
    tt = min(times, key=lambda x: abs(x - t))
    dense = r.vtk_data.pde_dataset.get_data(full, tt)
    index_map = r.vtk_data.global_index_map[domain]
    vals = np.asarray(dense)[index_map]
    return vals, tt, times


# ---------------------------------------------------------------------------
# HTTP
# ---------------------------------------------------------------------------

def capability(sim: int) -> str:
    """What vcell-rest would sign AFTER deciding this user may read this simulation.

    Scoped to the SIMULATION ID and nothing finer. VCell's permission model (GroupAccess) grants
    access to a simulation as a whole, so per-job or per-domain tokens would be finer-grained than
    the decision they represent -- extra machinery guarding a boundary that does not exist. One
    token covers every job, domain, variable and timepoint of one simulation.
    """
    return hmac.new(SECRET.encode(), str(sim).encode(), hashlib.sha256).hexdigest()[:32]


def authorized(q: dict) -> bool:
    """Verify the capability, constant-time. A token for one simulation is useless against
    another, which is the property that matters: a public model's token cannot read a private one."""
    if ALLOW_ANON:
        return True
    try:
        expected = capability(int(q["sim"]))
    except (KeyError, ValueError):
        return False
    return hmac.compare_digest(expected, q.get("token", ""))


class Handler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, *a):
        pass

    def _send(self, code, body: bytes, ctype: str, immutable=False, etag=None):
        self.send_response(code)
        self.send_header("Content-Type", ctype)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Access-Control-Allow-Origin", "*")
        if etag:
            self.send_header("ETag", etag)
        # a finished run's geometry never changes -- let the browser keep it
        self.send_header("Cache-Control", "public, max-age=31536000, immutable" if immutable
                         else "public, max-age=60")
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        u = urlparse(self.path)
        q = {k: v[0] for k, v in parse_qs(u.query).items()}
        t0 = time.monotonic()
        try:
            if u.path == "/health":
                return self._send(200, b"ok", "text/plain")

            if u.path == "/info":
                sim, job = int(q["sim"]), int(q.get("job", 0))
                r = result_for(sim, job)
                mesh = CartesianMesh(mesh_file=DATA_DIR / f"SimID_{sim}_{job}_.mesh")
                mesh.read()
                doc = {
                    "sim": sim, "job": job,
                    "domains": list(mesh.get_volume_domain_names()),
                    "variables": list(r.volume_variable_names),
                    "times": list(r.time_points),
                }
                return self._send(200, json.dumps(doc).encode(), "application/json")

            if u.path in ("/surface", "/field", "/info") and not authorized(q):
                return self._send(403, b'{"error":"missing or invalid capability token"}',
                                  "application/json")

            if u.path == "/surface":
                sim, job = int(q["sim"]), int(q.get("job", 0))
                domain, fmt = q["domain"], q.get("format", "vtp")
                poly = smoothed_surface(volume_grid(sim, job, domain))
                body, ctype = surface_payload(poly, fmt)
                etag = '"%s"' % hashlib.sha1(body).hexdigest()[:16]
                ms = (time.monotonic() - t0) * 1000
                print(f"  /surface {domain} fmt={fmt}: {poly.GetNumberOfPoints()} pts / "
                      f"{poly.GetNumberOfCells()} cells, {len(body)} bytes, {ms:.0f} ms")
                return self._send(200, body, ctype, immutable=True, etag=etag)

            if u.path == "/field":
                sim, job = int(q["sim"]), int(q.get("job", 0))
                domain, var = q["domain"], q["var"]
                vals, tt, _ = field_values(sim, job, domain, var, float(q.get("time", 0)))
                finite = vals[np.isfinite(vals)]
                doc = {
                    "name": var, "domain": domain, "time": tt,
                    "location": "cell",
                    "values": [None if not np.isfinite(v) else round(float(v), 12) for v in vals],
                    "range": [float(finite.min()), float(finite.max())] if finite.size else [0.0, 0.0],
                }
                body = json.dumps(doc).encode()
                ms = (time.monotonic() - t0) * 1000
                print(f"  /field {var}@{tt}: {len(vals)} values, {len(body)} bytes, {ms:.0f} ms")
                return self._send(200, body, "application/json", immutable=True)

            self._send(404, b'{"error":"no such endpoint"}', "application/json")
        except KeyError as e:
            self._send(400, json.dumps({"error": f"missing parameter {e}"}).encode(), "application/json")
        except Exception as e:
            import traceback
            traceback.print_exc()
            self._send(500, json.dumps({"error": f"{type(e).__name__}: {e}"}).encode(), "application/json")


if __name__ == "__main__":
    if not SECRET and not ALLOW_ANON:
        raise SystemExit("refusing to start: set GEOM_SECRET (or GEOM_ALLOW_ANON=1 for a local spike run)")
    if not DATA_DIR.is_dir():
        raise SystemExit(f"solver output dir not found: {DATA_DIR}\nset GEOM_DATA_DIR=<dir containing SimID_*_.mesh>")
    print(f"geometry server (stateless) on http://127.0.0.1:{PORT}")
    print(f"  data dir: {DATA_DIR}")
    if ALLOW_ANON:
        print("  *** GEOM_ALLOW_ANON=1 -- capability checks DISABLED. Spike/local use only. ***")
    else:
        print("  capability tokens required (HMAC over the simulation id)")
    ThreadingHTTPServer(("127.0.0.1", PORT), Handler).serve_forever()
