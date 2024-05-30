import os
from pathlib import Path

import vtk
import orjson

from vcelldata.vtk.vismesh import VisIrregularPolyhedron, VisMesh, FiniteVolumeIndexData, VisTetrahedron, \
    MovingBoundaryIndexData
from vcelldata.vtk.vtkmesh_utils import getVolumeVtkGrid, writevtk


def writeMovingBoundaryVolumeVtkGridAndIndexData(visMesh: VisMesh, domainName: str, vtuFile, indexFile):
    vtkgrid = getVolumeVtkGrid(visMesh)
    writevtk(vtkgrid, vtuFile)
    movingBoundaryIndexData = MovingBoundaryIndexData()
    movingBoundaryIndexData.movingBoundaryVolumeIndices = []
    movingBoundaryIndexData.domainName = domainName
    movingBoundaryIndexData.timeIndex = 0
    if visMesh.dimension == 2:
        # if volume
        if visMesh.polygons is not None:
            for polygon in visMesh.polygons:
                movingBoundaryIndexData.movingBoundaryVolumeIndices.append(polygon.movingBoundaryVolumeIndex)
        # if membrane
        if visMesh.visLines is not None:
            for visLine in visMesh.visLines:
                movingBoundaryIndexData.movingBoundarySurfaceIndices.append(visLine.movingBoundarySurfaceIndex)
    # elif visMesh.dimension == 3:
    #     # if volume
    #     if visMesh.visVoxels is not None:
    #         for voxel in visMesh.visVoxels:
    #             movingBoundaryIndexData.finiteVolumeIndices.append(voxel.finiteVolumeIndex)
    #     if visMesh.irregularPolyhedra is not None:
    #         raise Exception("unexpected irregular polyhedra in mesh, should have been replaced with tetrahedra")
    #     if visMesh.tetrahedra is not None:
    #         for tetrahedron in visMesh.tetrahedra:
    #             movingBoundaryIndexData.finiteVolumeIndices.append(tetrahedron.finiteVolumeIndex)
    #     # if membrane
    #     if visMesh.polygons is not None:
    #         for polygon in visMesh.polygons:
    #             movingBoundaryIndexData.finiteVolumeIndices.append(polygon.finiteVolumeIndex)

    if movingBoundaryIndexData.movingBoundaryVolumeIndices == None and movingBoundaryIndexData.movingBoundarySurfaceIndices == None:
        print("didn't find any indices ... bad")

    if movingBoundaryIndexData.movingBoundaryVolumeIndices != None and len(movingBoundaryIndexData.movingBoundaryVolumeIndices) == 0:
        print("didn't find any indices ... bad")

    if movingBoundaryIndexData.movingBoundarySurfaceIndices != None and len(movingBoundaryIndexData.movingBoundarySurfaceIndices) == 0:
        print("didn't find any indices ... bad")

    writeMovingBoundaryIndexData(indexFile, movingBoundaryIndexData)


def writeMovingBoundaryIndexData(movingBoundaryIndexFile: Path, movingBoundaryIndexData: MovingBoundaryIndexData):
    json = orjson.dumps(movingBoundaryIndexData, option=orjson.OPT_NAIVE_UTC | orjson.OPT_SERIALIZE_NUMPY)
    with movingBoundaryIndexFile.open('wb') as ff:
        ff.write(json)
