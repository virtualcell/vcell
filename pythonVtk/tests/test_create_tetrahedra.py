"""
createTetrahedra() has to produce a decomposition that is exact and that AGREES with the
neighbouring cell across a shared face — otherwise the shared face is no longer shared and gets
extracted as if it were part of the domain surface (virtualcell/vcell#1895).
"""
import math

from python_vtk.vcellvismesh.ttypes import PolyhedronFace
from python_vtk.vcellvismesh.ttypes import VisIrregularPolyhedron
from python_vtk.vcellvismesh.ttypes import VisMesh
from python_vtk.vcellvismesh.ttypes import VisPoint
from python_vtk.vtkService.vtkService import createTetrahedra, signedVolume

# unit cube corners, in the VisVoxel ordering used by ChomboMeshMapping
CUBE_POINTS = [(0, 0, 0), (1, 0, 0), (0, 1, 0), (1, 1, 0),
               (0, 0, 1), (1, 0, 1), (0, 1, 1), (1, 1, 1)]
CUBE_FACES = [[0, 4, 6, 2], [1, 3, 7, 5], [0, 1, 5, 4], [2, 6, 7, 3], [0, 2, 3, 1], [4, 5, 7, 6]]


def _mesh(points: list[tuple[float, float, float]]) -> VisMesh:
    mesh = VisMesh()
    mesh.points = [VisPoint(float(x), float(y), float(z)) for (x, y, z) in points]
    return mesh


def _polyhedron(faces: list[list[int]]) -> VisIrregularPolyhedron:
    poly = VisIrregularPolyhedron()
    poly.polyhedronFaces = [PolyhedronFace(list(face)) for face in faces]
    return poly


def _volume(mesh: VisMesh, tets) -> float:
    return sum(abs(signedVolume(mesh, tet.pointIndices)) for tet in tets)


def test_decomposition_is_exact_and_positively_oriented() -> None:
    mesh = _mesh(CUBE_POINTS)
    tets = createTetrahedra(_polyhedron(CUBE_FACES), mesh)

    assert math.isclose(_volume(mesh, tets), 1.0, rel_tol=1e-12)
    for tet in tets:
        assert signedVolume(mesh, tet.pointIndices) > 0


def test_clipped_cell_volume_matches_the_polyhedron() -> None:
    # cube with the corner at (1,1,1) cut off by the plane through its three neighbours
    points = [(0, 0, 0), (1, 0, 0), (0, 1, 0), (1, 1, 0),
              (0, 0, 1), (1, 0, 1), (0, 1, 1)]
    mesh = _mesh(points)
    faces = [
        [0, 4, 6, 2],        # x-
        [0, 1, 5, 4],        # y-
        [0, 2, 3, 1],        # z-
        [1, 3, 5],           # x+ (clipped)
        [2, 6, 3],           # y+ (clipped)
        [4, 5, 6],           # z+ (clipped)
        [3, 5, 6],           # the cut face
    ]
    tets = createTetrahedra(_polyhedron(faces), mesh)
    assert math.isclose(_volume(mesh, tets), 1.0 - 1.0 / 6.0, rel_tol=1e-12)


def test_neighbours_cut_a_shared_face_identically() -> None:
    """
    Two cells stacked in z share the face 4,5,7,6. Each lists it in its own winding and starting
    corner, as ChomboMeshMapping does; the triangles produced on that face must be identical or
    the face stops being shared.
    """
    points = CUBE_POINTS + [(0, 0, 2), (1, 0, 2), (0, 1, 2), (1, 1, 2)]
    mesh = _mesh(points)

    lower = _polyhedron(CUBE_FACES)                                  # ...z+ face is [4, 5, 7, 6]
    upper = _polyhedron([[4, 8, 10, 6], [5, 7, 11, 9], [4, 5, 9, 8],
                         [6, 10, 11, 7], [7, 5, 4, 6], [8, 9, 11, 10]])  # shared face reversed
    lower_tets = createTetrahedra(lower, mesh)
    upper_tets = createTetrahedra(upper, mesh)

    shared = {4, 5, 6, 7}
    lower_on_face = {frozenset(t.pointIndices) & shared for t in lower_tets}
    upper_on_face = {frozenset(t.pointIndices) & shared for t in upper_tets}
    triangles = lambda faces: {f for f in faces if len(f) == 3}
    assert triangles(lower_on_face) == triangles(upper_on_face)
    assert len(triangles(lower_on_face)) == 2  # the quad is split, and split the same way


def test_degenerate_polyhedron_is_skipped() -> None:
    mesh = _mesh(CUBE_POINTS)
    assert createTetrahedra(_polyhedron([[0, 1, 2]]), mesh) == []
