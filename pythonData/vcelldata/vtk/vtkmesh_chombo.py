from copy import deepcopy

import orjson
import vtk
from pathlib import Path

from vcelldata.vtk.vismesh import VisMesh, VisTetrahedron, \
    ChomboIndexData, VisPolygon, VisLine
from vcelldata.vtk.vtkmesh_utils import createTetrahedra, getVolumeVtkGrid, writevtk, getMembraneVtkGrid


def writeChomboVolumeVtkGridAndIndexData(visMesh: VisMesh, domainname: str, vtkfile, indexfile) -> None:
    originalVisMesh = visMesh
    correctedVisMesh = originalVisMesh  # same mesh if no irregularPolyhedra
    if originalVisMesh.irregularPolyhedra is not None:
        correctedVisMesh = deepcopy(visMesh)
        if correctedVisMesh.tetrahedra is None:
            correctedVisMesh.tetrahedra = []
        for irregularPolyhedron in correctedVisMesh.irregularPolyhedra:
            tets = createTetrahedra(irregularPolyhedron, correctedVisMesh)
            for tet in tets:
                correctedVisMesh.tetrahedra.append(tet)
        correctedVisMesh.irregularPolyhedra = None

    vtkgrid: vtk.vtkUnstructuredGrid = getVolumeVtkGrid(correctedVisMesh)
    writevtk(vtkgrid, vtkfile)
    chomboIndexData = ChomboIndexData()
    chomboIndexData.chomboVolumeIndices = []
    chomboIndexData.domainName = domainname
    if correctedVisMesh.dimension == 2:
        if correctedVisMesh.polygons is not None:
            for polygon in correctedVisMesh.polygons:
                assert isinstance(polygon, VisPolygon)
                chomboIndexData.chomboVolumeIndices.append(polygon.chomboVolumeIndex)
        if chomboIndexData.chomboVolumeIndices is None:
            print("didn't find any indices ... bad")
    elif correctedVisMesh.dimension == 3:
        if correctedVisMesh.visVoxels is not None:
            for voxel in correctedVisMesh.visVoxels:
                chomboIndexData.chomboVolumeIndices.append(voxel.chomboVolumeIndex)
        if correctedVisMesh.irregularPolyhedra is not None:
            raise Exception("unexpected irregular polyhedra in mesh, should have been replaced with tetrahedra")
        if correctedVisMesh.tetrahedra is not None:
            for tetrahedron in correctedVisMesh.tetrahedra:
                assert isinstance(tetrahedron, VisTetrahedron)
                chomboIndexData.chomboVolumeIndices.append(tetrahedron.chomboVolumeIndex)
        if len(chomboIndexData.chomboVolumeIndices) == 0:
            print("didn't find any indices ... bad")
    writeChomboIndexData(indexfile, chomboIndexData)


def writeChomboMembraneVtkGridAndIndexData(visMesh: VisMesh, domainname: str, vtkfile, indexfile) -> None:

    vtkgrid = getMembraneVtkGrid(visMesh)
    writevtk(vtkgrid, vtkfile)

    chomboIndexData = ChomboIndexData()
    chomboIndexData.chomboSurfaceIndices = []
    if domainname.upper().endswith("MEMBRANE") is False:
        raise Exception("expecting domain name ending with membrane")
    chomboIndexData.domainName = domainname
    if visMesh.dimension == 3:
        if visMesh.surfaceTriangles is not None:
            for  surfaceTriangle in visMesh.surfaceTriangles:
                chomboIndexData.chomboSurfaceIndices.append(surfaceTriangle.chomboSurfaceIndex)
    elif visMesh.dimension == 2:
        if visMesh.visLines is not None:
            for visLine in visMesh.visLines:
                assert isinstance(visLine, VisLine)
                chomboIndexData.chomboSurfaceIndices.append(visLine.chomboSurfaceIndex)
    if len(chomboIndexData.chomboSurfaceIndices) == 0:
        print("didn't find any indices ... bad")
    writeChomboIndexData(indexfile, chomboIndexData)



def writeChomboIndexData(chomboIndexFile: Path, chomboIndexData: ChomboIndexData):
    json = orjson.dumps(chomboIndexData, option=orjson.OPT_NAIVE_UTC | orjson.OPT_SERIALIZE_NUMPY)
    with chomboIndexFile.open('wb') as ff:
        ff.write(json)

