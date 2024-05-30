import orjson
from pathlib import Path

from vcelldata.vtk.vismesh import VisMesh, FiniteVolumeIndexData
from vcelldata.vtk.vtkmesh_utils import writevtk, getVolumeVtkGrid, smoothUnstructuredGridSurface


def writeFiniteVolumeSmoothedVtkGridAndIndexData(visMesh: VisMesh, domainName: str, vtuFile: Path, indexFile: Path) -> None:
    vtkgrid = getVolumeVtkGrid(visMesh)
    if visMesh.dimension == 3:
        vtkgridSmoothed = smoothUnstructuredGridSurface(vtkgrid)
    else:
        vtkgridSmoothed = vtkgrid
    writevtk(vtkgridSmoothed, vtuFile)
    finiteVolumeIndexData = FiniteVolumeIndexData(domainName=domainName, finiteVolumeIndices=[])
    if visMesh.dimension == 2:
        # if volume
        if visMesh.polygons is not None:
            for polygon in visMesh.polygons:
                finiteVolumeIndexData.finiteVolumeIndices.append(polygon.finiteVolumeIndex)
        # if membrane
        if visMesh.visLines is not None:
            for visLine in visMesh.visLines:
                finiteVolumeIndexData.finiteVolumeIndices.append(visLine.finiteVolumeIndex)
    elif visMesh.dimension == 3:
        # if volume
        if visMesh.visVoxels is not None:
            for voxel in visMesh.visVoxels:
                finiteVolumeIndexData.finiteVolumeIndices.append(voxel.finiteVolumeIndex)
        if visMesh.irregularPolyhedra is not None:
            raise Exception("unexpected irregular polyhedra in mesh, should have been replaced with tetrahedra")
        if visMesh.tetrahedra is not None:
            for tetrahedron in visMesh.tetrahedra:
                finiteVolumeIndexData.finiteVolumeIndices.append(tetrahedron.finiteVolumeIndex)
        # if membrane
        if visMesh.polygons is not None:
            for polygon in visMesh.polygons:
                finiteVolumeIndexData.finiteVolumeIndices.append(polygon.finiteVolumeIndex)

    if finiteVolumeIndexData.finiteVolumeIndices == None or len(finiteVolumeIndexData.finiteVolumeIndices) == 0:
        print("didn't find any indices ... bad")

    writeFiniteVolumeIndexData(indexFile, finiteVolumeIndexData)


def writeFiniteVolumeIndexData(finiteVolumeIndexFile: Path, finiteVolumeIndexData: FiniteVolumeIndexData):
    json = orjson.dumps(finiteVolumeIndexData, option=orjson.OPT_NAIVE_UTC | orjson.OPT_SERIALIZE_NUMPY)
    with finiteVolumeIndexFile.open('wb') as ff:
        ff.write(json)

