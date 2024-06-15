import os

import numpy as np
import vtk
from pathlib import Path
from vtkmodules.util.numpy_support import numpy_to_vtk

from vcelldata.vtk.vismesh import VisIrregularPolyhedron, VisMesh, VisTetrahedron, PolyhedronFace


#
# read a vtkUnstructuredGrid from the XML format
#
def readvtk(vtkfile) -> vtk.vtkUnstructuredGrid:
    if not os.path.isfile(vtkfile):
        raise Exception("unstructured grid " + str(vtkfile) + " not found")

    tester = vtk.vtkXMLFileReadTester()
    tester.SetFileName(str(vtkfile))
    if tester.TestReadFile() != 1:
        raise Exception("expecting XML formatted VTK unstructured grid")

    reader = vtk.vtkXMLUnstructuredGridReader()
    reader.SetFileName(vtkfile)
    reader.Update()
    vtkgrid = reader.GetOutput()
    assert isinstance(vtkgrid, vtk.vtkUnstructuredGrid)
    print("read from file " + str(vtkfile))
    vtkgrid.BuildLinks()
    return vtkgrid


#
# write a vtkUnstructuredGrid to the XML format
#
def writevtk(vtkgrid: vtk.vtkUnstructuredGrid, filename: Path):
    writer = vtk.vtkXMLUnstructuredGridWriter()
    bASCII = False
    if bASCII:
        writer.SetDataModeToAscii()
    else:
        writer.SetCompressorTypeToNone()
        writer.SetDataModeToBinary()
    try:
        writer.SetInputData(vtkgrid)
    except AttributeError:
        writer.SetInput(vtkgrid)
    writer.SetFileName(filename)
    writer.Update()
    print("wrote to file "+str(filename))


#
# create a single-variable vtu file
#
def writeDataArrayToNewVtkFile(emptyMeshFile: str, varName: str, data: np.ndarray, newMeshFile: str):
    data = np.array(data)
    vtkgrid = readvtk(emptyMeshFile)

    dataArray = numpy_to_vtk(data)
    assert isinstance(dataArray, )
    dataArray.SetName(varName)
    cellData: vtk.vtkCellData = vtkgrid.GetCellData()
    cellData.AddArray(dataArray)

    #
    # write mesh and data to the file for that domain and time
    #
    writevtk(vtkgrid, newMeshFile)

def getMembraneVtkGrid(visMesh: VisMesh) -> vtk.vtkUnstructuredGrid:
    vtkpoints = vtk.vtkPoints()
    for visPoint in visMesh.surfacePoints:
        vtkpoints.InsertNextPoint(visPoint.x,visPoint.y,visPoint.z)

    vtkgrid = vtk.vtkUnstructuredGrid()
    vtkgrid.Allocate(len(visMesh.surfacePoints), len(visMesh.surfacePoints))
    vtkgrid.SetPoints(vtkpoints)

    if visMesh.dimension == 2:
        vtkline = vtk.vtkLine()
        lineType = vtkline.GetCellType()

        for line in visMesh.visLines:
            pts = vtk.vtkIdList()
            pts.InsertNextId(line.p1)
            pts.InsertNextId(line.p2)
            vtkgrid.InsertNextCell(lineType, pts)
    else:
        vtktriangle = vtk.vtkTriangle()
        triangleType = vtktriangle.GetCellType()
        for surfaceTriangle in visMesh.surfaceTriangles:
            pts = vtk.vtkIdList()
            for pi in surfaceTriangle.pointIndices:
                pts.InsertNextId(pi)
            # each triangle is a cell
            vtkgrid.InsertNextCell(triangleType, pts)

    vtkgrid.BuildLinks()
    return vtkgrid


def getVolumeVtkGrid(visMesh: VisMesh) -> vtk.vtkUnstructuredGrid:
    bClipPolyhedra = True

    vtkpoints = vtk.vtkPoints()
    for visPoint in visMesh.points:
        vtkpoints.InsertNextPoint(visPoint.x, visPoint.y, visPoint.z)

    vtkgrid = vtk.vtkUnstructuredGrid()
    vtkgrid.Allocate(len(visMesh.points), len(visMesh.points))
    vtkgrid.SetPoints(vtkpoints)

    quadType = vtk.vtkQuad().GetCellType()
    #  lineType = vtk.vtkLine().GetCellType()
    polygonType = vtk.vtkPolygon().GetCellType()
    polyhedronType = vtk.vtkPolyhedron().GetCellType()
    triangleType = vtk.vtkTriangle().GetCellType()
    voxelType = vtk.vtkVoxel().GetCellType()
    tetraType = vtk.vtkTetra().GetCellType()

    if visMesh.polygons != None:
        for visPolygon in visMesh.polygons:
            pts = vtk.vtkIdList()
            polygonPoints = visPolygon.pointIndices
            for p in polygonPoints:
                pts.InsertNextId(p)

            numPoints = len(polygonPoints)
            if numPoints == 4:
                vtkgrid.InsertNextCell(quadType, pts)
            elif numPoints == 3:
                vtkgrid.InsertNextCell(triangleType, pts)
            else:
                vtkgrid.InsertNextCell(polygonType, pts)
    #
    # replace any VisIrregularPolyhedron with a list of VisTetrahedron
    #
    if visMesh.visVoxels != None:
        for voxel in visMesh.visVoxels:
            pts = vtk.vtkIdList()
            polyhedronPoints = voxel.pointIndices
            for p in polyhedronPoints:
                pts.InsertNextId(p)
            vtkgrid.InsertNextCell(voxelType, pts)

    if visMesh.tetrahedra != None:
        for visTet in visMesh.tetrahedra:
            assert isinstance(visTet, VisTetrahedron)
            pts = vtk.vtkIdList()
            tetPoints = visTet.pointIndices
            for p in tetPoints:
                pts.InsertNextId(p)
            vtkgrid.InsertNextCell(tetraType, pts)

    bInitializedFaces = False
    if visMesh.irregularPolyhedra != None:
        for clippedPolyhedron in visMesh.irregularPolyhedra:
            if bClipPolyhedra == True:
                tets = createTetrahedra(clippedPolyhedron, visMesh)
                for visTet in tets:
                    pts = vtk.vtkIdList()
                    tetPoints = visTet.getPointIndices()
                    for p in tetPoints:
                        pts.InsertNextId(p)
                    vtkgrid.InsertNextCell(tetraType, pts)
            else:
                faceStreamList = vtk.vtkIdList()
                faceStream = getVtkFaceStream(clippedPolyhedron)
                for p in faceStream:
                    faceStreamList.InsertNextId(p)
                if bInitializedFaces == False and vtkgrid.GetNumberOfCells() > 0:
                    vtkgrid.InitializeFacesRepresentation(vtkgrid.GetNumberOfCells())
                bInitializedFaces = True
                vtkgrid.InsertNextCell(polyhedronType, faceStreamList)

    vtkgrid.BuildLinks()
    # vtkgrid.Squeeze()
    return vtkgrid


def getVtkFaceStream(irregularPolyhedron: VisIrregularPolyhedron) -> list[int]:
    faceStream = [len(irregularPolyhedron.polyhedronFaces), ]
    for polyhedronFace in irregularPolyhedron.polyhedronFaces:
        faceStream.append(len(polyhedronFace.getVertices()))
        for v in polyhedronFace.vertices:
            faceStream.append(v)
    intFaceStream = [int(v) for v in faceStream]
    return intFaceStream


def smoothUnstructuredGridSurface(vtkGrid: vtk.vtkUnstructuredGrid) -> vtk.vtkUnstructuredGrid:
    ugGeometryFilter = vtk.vtkUnstructuredGridGeometryFilter()
    ugGeometryFilter.PassThroughPointIdsOn()
    ugGeometryFilter.MergingOff()
    ugGeometryFilter.SetInputData(vtkGrid)
    ugGeometryFilter.Update()
    surfaceUnstructuredGrid: vtk.vtkUnstructuredGrid = ugGeometryFilter.GetOutput()
    originalPointsIdsName = ugGeometryFilter.GetOriginalPointIdsName()

    cellData = surfaceUnstructuredGrid.GetCellData()
    numCellArrays = cellData.GetNumberOfArrays()
    for i in range(0, numCellArrays):
        cellArrayName = cellData.GetArrayName(i)
        # print("CellArray(" + str(i) + ") '" + cellArrayName + "')")
    pointData: vtk.vtkPointData = surfaceUnstructuredGrid.GetPointData()
    numPointArrays = pointData.GetNumberOfArrays()
    for i in range(0, numPointArrays):
        pointArrayName = pointData.GetArrayName(i)
        # print("PointArray(" + str(i) + ") '" + pointArrayName + "'")

    geometryFilter = vtk.vtkGeometryFilter()
    geometryFilter.SetInputData(surfaceUnstructuredGrid)
    geometryFilter.Update()
    polyData: vtk.vtkPolyData = geometryFilter.GetOutput()

    filter = vtk.vtkWindowedSincPolyDataFilter()
    filter.SetInputData(polyData)
    filter.SetNumberOfIterations(12)
    filter.BoundarySmoothingOff()
    filter.FeatureEdgeSmoothingOff()
    filter.SetFeatureAngle(120.0)
    filter.SetPassBand(0.05)
    filter.NonManifoldSmoothingOff()
    filter.NormalizeCoordinatesOn()
    filter.Update()

    smoothedPolydata = filter.GetOutput()

    smoothedPoints: vtk.vtkPoints = smoothedPolydata.GetPoints()

    smoothedPointData: vtk.vtkPointData = smoothedPolydata.GetPointData()
    pointIdsArray: vtk.vtkIdTypeArray = smoothedPointData.GetArray(originalPointsIdsName)
    pointsIdsArraySize = pointIdsArray.GetSize()
    origPoints = vtkGrid.GetPoints()
    for i in range(0, pointsIdsArraySize):
        pointId = pointIdsArray.GetValue(i)
        smoothedPoint = smoothedPoints.GetPoint(i)
        origPoints.SetPoint(pointId, smoothedPoint)

    return vtkGrid


def getPointIndices(irregularPolyhedron: VisIrregularPolyhedron) -> list[int]:
    assert isinstance(irregularPolyhedron, VisIrregularPolyhedron)
    pointIndicesSet = set()
    for face in irregularPolyhedron.polyhedronFaces:
        assert(isinstance(face, PolyhedronFace))
        for pointIndex in face.vertices:
            pointIndicesSet.add(pointIndex)
    pointArray = [int(x) for x in pointIndicesSet]
    return pointArray


def createTetrahedra(clippedPolyhedron: VisIrregularPolyhedron, visMesh: VisMesh):

    vtkpolydata = vtk.vtkPolyData()
    vtkpoints = vtk.vtkPoints()
    polygonType = vtk.vtkPolygon().GetCellType()
    uniquePointIndices = getPointIndices(clippedPolyhedron)
    for point in uniquePointIndices:
        visPoint = visMesh.points[point]
        vtkpoints.InsertNextPoint(visPoint.x, visPoint.y, visPoint.z)
    vtkpolydata.Allocate(100, 100)
    vtkpolydata.SetPoints(vtkpoints)

    for face in clippedPolyhedron.polyhedronFaces:
        faceIdList = vtk.vtkIdList()
        for visPointIndex in face.vertices:
            vtkpointid = -1
            for i in range(0, len(uniquePointIndices)):
                if uniquePointIndices[i] == visPointIndex:
                    vtkpointid = i
            faceIdList.InsertNextId(vtkpointid)
        vtkpolydata.InsertNextCell(polygonType, faceIdList)

    delaunayFilter = vtk.vtkDelaunay3D()
    try:
        delaunayFilter.SetInputData(vtkpolydata)
    except AttributeError:
        delaunayFilter.SetInput(vtkpolydata)
    delaunayFilter.Update()
    delaunayFilter.SetAlpha(0.1)
    vtkgrid2: vtk.vtkUnstructuredGrid = delaunayFilter.GetOutput()
    assert isinstance(vtkgrid2, vtk.vtkUnstructuredGrid) # runtime check, remove later

    visTets = []
    numTets = vtkgrid2.GetNumberOfCells()
    if numTets < 1:
        if len(uniquePointIndices)==4:
            visTet = VisTetrahedron(uniquePointIndices)
            visTet.chomboVolumeIndex = clippedPolyhedron.chomboVolumeIndex
            visTet.finiteVolumeIndex = clippedPolyhedron.finiteVolumeIndex
            visTets.append(visTet)
            print("made trivial tet ... maybe inside out")
        else:
            print("found no tets, there are "+str(len(uniquePointIndices))+" unique point indices")


    #	print("numFaces = "+str(vtkpolydata.GetNumberOfCells())+", numTets = "+str(numTets));
    for cellIndex in range(0, numTets):
        cell = vtkgrid2.GetCell(cellIndex)
        if isinstance(cell, vtk.vtkTetra):
            vtkTet: vtk.vtkTetra = cell
            tetPointIds: vtk.vtkIdList = vtkTet.GetPointIds()
            assert isinstance(tetPointIds, vtk.vtkIdList)
            #
            # translate from vtkgrid pointids to visMesh point ids
            #
            numPoints = tetPointIds.GetNumberOfIds()
            visPointIds = []
            for p in range(0, numPoints):
                visPointIds.append(uniquePointIndices[tetPointIds.GetId(p)])
            visTet = VisTetrahedron(visPointIds)
            if clippedPolyhedron.chomboVolumeIndex != None:
                visTet.chomboVolumeIndex = clippedPolyhedron.chomboVolumeIndex
            if clippedPolyhedron.finiteVolumeIndex != None:
                visTet.finiteVolumeIndex = clippedPolyhedron.finiteVolumeIndex
            visTets.append(visTet)
        else:
            print("ChomboMeshMapping.createTetrahedra(): expecting a tet, found a " + cell.__type__)

    return visTets
