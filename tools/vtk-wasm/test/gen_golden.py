#!/usr/bin/env python3
"""Regenerate the golden-file fixtures for the vtk.wasm WindowedSinc test.

Reproduces VCell's finite-volume surface-smoothing pipeline (the reference
implementation in pyvcell `_internal/simdata/vtk/vtkmesh_utils.py`,
`smooth_unstructured_grid_surface`) on a real VCell mesh, and writes:

  fixtures/input_surface.json  -- the pre-smooth boundary surface (points + quad polys)
  fixtures/golden_points.json  -- the reference-smoothed points (the golden)

The browser harness (golden-test.html) feeds `input_surface.json` into the
vtk.wasm session, runs ONLY `vtkWindowedSincPolyDataFilter` with the exact
params below, and asserts its output matches `golden_points.json`.

Requires pyvcell importable (its .venv has the reference VTK). Run e.g.:

    /path/to/pyvcell/.venv/bin/python gen_golden.py

Env overrides:
  MESH_FILE  -- path to a VCell .mesh (default: pyvcell's SimID_946368938 fixture)
  DOMAIN     -- volume domain name (default: cytosol)
"""
import json
import math
import os
from collections import Counter
from pathlib import Path

import vtk
from pyvcell._internal.simdata.mesh import CartesianMesh
from pyvcell._internal.simdata.vtk.fv_mesh_mapping import from_mesh3d_volume
from pyvcell._internal.simdata.vtk.vtkmesh_utils import get_volume_vtk_grid

HERE = Path(__file__).resolve().parent
OUT = HERE / "fixtures"
DOMAIN = os.environ.get("DOMAIN", "cytosol")
# Default to pyvcell's checked-in mesh fixture (a real VCell finite-volume mesh).
DEFAULT_MESH = (
    Path(__file__).resolve().parents[4].parent
    / "pyvcell/tests/fixtures/data/solver_output/SimID_946368938_0_.mesh"
)
MESH_FILE = Path(os.environ.get("MESH_FILE", DEFAULT_MESH))

# WindowedSinc params -- MUST match smooth_unstructured_grid_surface exactly.
SINC = dict(iterations=12, feature_angle=120.0, pass_band=0.05)


def main() -> None:
    print("reference VTK", vtk.vtkVersion().GetVTKVersion())
    if not MESH_FILE.exists():
        raise SystemExit(f"mesh not found: {MESH_FILE}\nset MESH_FILE=... (a VCell .mesh)")
    OUT.mkdir(exist_ok=True)

    mesh = CartesianMesh(mesh_file=MESH_FILE)
    mesh.read()
    grid = get_volume_vtk_grid(from_mesh3d_volume(mesh, DOMAIN))
    ct = Counter(grid.GetCellType(i) for i in range(grid.GetNumberOfCells()))
    print(f"volume grid: pts={grid.GetNumberOfPoints()} cells={grid.GetNumberOfCells()} celltypes={dict(ct)}")

    # reference pipeline (exactly smooth_unstructured_grid_surface, up to the sinc output)
    ug = vtk.vtkUnstructuredGridGeometryFilter()
    ug.PassThroughPointIdsOn()
    ug.MergingOff()
    ug.SetInputData(grid)
    ug.Update(0)
    gf = vtk.vtkGeometryFilter()
    gf.SetInputData(ug.GetOutput())
    gf.Update(0)
    presmooth = gf.GetOutput()

    sinc = vtk.vtkWindowedSincPolyDataFilter()
    sinc.SetInputData(presmooth)
    sinc.SetNumberOfIterations(SINC["iterations"])
    sinc.BoundarySmoothingOff()
    sinc.FeatureEdgeSmoothingOff()
    sinc.SetFeatureAngle(SINC["feature_angle"])
    sinc.SetPassBand(SINC["pass_band"])
    sinc.NonManifoldSmoothingOff()
    sinc.NormalizeCoordinatesOn()
    sinc.Update(0)
    golden = sinc.GetOutput()

    def coords(pd):
        P = pd.GetPoints()
        out = []
        for i in range(pd.GetNumberOfPoints()):
            out.extend(P.GetPoint(i))
        return out

    polys, ca, idl = [], presmooth.GetPolys(), vtk.vtkIdList()
    ca.InitTraversal()
    while ca.GetNextCell(idl):
        polys.append([idl.GetId(k) for k in range(idl.GetNumberOfIds())])

    json.dump(
        {"numPoints": presmooth.GetNumberOfPoints(), "points": coords(presmooth), "polys": polys},
        open(OUT / "input_surface.json", "w"),
    )
    json.dump(
        {"numPoints": golden.GetNumberOfPoints(), "points": coords(golden), "sinc": SINC},
        open(OUT / "golden_points.json", "w"),
    )

    pin, pout = presmooth.GetPoints(), golden.GetPoints()
    mx = max(math.dist(pin.GetPoint(i), pout.GetPoint(i)) for i in range(golden.GetNumberOfPoints()))
    print(f"surface: pts={presmooth.GetNumberOfPoints()} polys={len(polys)} "
          f"polysizes={dict(Counter(len(p) for p in polys))}")
    print(f"reference max smoothing displacement = {mx:.6g}")
    print(f"WROTE {OUT}/input_surface.json + golden_points.json")


if __name__ == "__main__":
    main()
