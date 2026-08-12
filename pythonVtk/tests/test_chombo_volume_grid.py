"""
A Chombo cut cell is written as a VTK_POLYHEDRON carrying its own faces, so that a face it shares
with a neighbour stays shared (virtualcell/vcell#1895). These tests pin that: the cell keeps its
faces, and the grid a full voxel and a cut neighbour produce has no unshared interior face.
"""
from collections import Counter

import vtkmodules.all as vtk

from python_vtk.vcellvismesh.ttypes import ChomboVolumeIndex
from python_vtk.vcellvismesh.ttypes import PolyhedronFace
from python_vtk.vcellvismesh.ttypes import VisIrregularPolyhedron
from python_vtk.vcellvismesh.ttypes import VisMesh
from python_vtk.vcellvismesh.ttypes import VisPoint
from python_vtk.vcellvismesh.ttypes import VisVoxel
from python_vtk.vtkService.vtkService import getVolumeVtkGrid, getVtkFaceStream

# two unit cubes stacked in z, sharing the face 4,5,7,6
POINTS = [(0, 0, 0), (1, 0, 0), (0, 1, 0), (1, 1, 0),
          (0, 0, 1), (1, 0, 1), (0, 1, 1), (1, 1, 1),
          (0, 0, 2), (1, 0, 2), (0, 1, 2), (1, 1, 2)]
UPPER_FACES = [[4, 8, 10, 6], [5, 7, 11, 9], [4, 5, 9, 8],
               [6, 10, 11, 7], [7, 5, 4, 6], [8, 9, 11, 10]]


def _mesh() -> VisMesh:
    mesh = VisMesh()
    mesh.dimension = 3
    mesh.points = [VisPoint(float(x), float(y), float(z)) for (x, y, z) in POINTS]
    voxel = VisVoxel(list(range(8)))
    voxel.chomboVolumeIndex = ChomboVolumeIndex(0, 0, 0, 1.0)
    mesh.visVoxels = [voxel]
    upper = VisIrregularPolyhedron()
    upper.polyhedronFaces = [PolyhedronFace(list(f)) for f in UPPER_FACES]
    upper.chomboVolumeIndex = ChomboVolumeIndex(0, 0, 1, 0.5)
    mesh.irregularPolyhedra = [upper]
    return mesh


def test_face_stream_is_count_then_each_face() -> None:
    poly = VisIrregularPolyhedron()
    poly.polyhedronFaces = [PolyhedronFace([0, 1, 2]), PolyhedronFace([0, 1, 3, 2])]
    assert getVtkFaceStream(poly) == [2, 3, 0, 1, 2, 4, 0, 1, 3, 2]


def test_cut_cell_is_written_as_a_polyhedron_with_its_faces() -> None:
    grid = getVolumeVtkGrid(_mesh())
    assert grid.GetNumberOfCells() == 2
    assert grid.GetCellType(0) == vtk.VTK_VOXEL
    assert grid.GetCellType(1) == vtk.VTK_POLYHEDRON

    faces = vtk.vtkIdList()
    grid.GetFaceStream(1, faces)
    assert faces.GetId(0) == len(UPPER_FACES)


def test_the_shared_face_is_shared() -> None:
    """
    Every face of the two-cell grid is either on the outside (one owner) or between them (two).
    A face with one owner that is NOT on the outside boundary is the #1895 defect.
    """
    grid = getVolumeVtkGrid(_mesh())
    owners: Counter = Counter()
    for c in range(grid.GetNumberOfCells()):
        cell = grid.GetCell(c)
        for f in range(cell.GetNumberOfFaces()):
            face = cell.GetFace(f)
            ids = [face.GetPointId(i) for i in range(face.GetNumberOfPoints())]
            owners[tuple(sorted(ids))] += 1

    shared = [f for f, n in owners.items() if n == 2]
    assert shared == [(4, 5, 6, 7)]        # the interface, seen from both cells
    assert max(owners.values()) == 2       # nothing is claimed more than twice

    # and the exposed surface is closed: every edge of it used exactly twice
    edges: Counter = Counter()
    for f, n in owners.items():
        if n != 1:
            continue
        ring = list(f) if len(f) == 3 else [f[0], f[1], f[3], f[2]]  # sorted quad -> a ring
        for i in range(len(ring)):
            a, b = ring[i], ring[(i + 1) % len(ring)]
            edges[(min(a, b), max(a, b))] += 1
    assert set(edges.values()) == {2}
