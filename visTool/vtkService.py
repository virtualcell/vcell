import argparse
import copy
import os

import sys
import traceback

import vtk

from pyvcell.ttypes import SimulationDataSetRef
from pyvcell.ttypes import VariableInfo
from thrift.TSerialization import TBinaryProtocol
from thrift.TSerialization import deserialize
from thrift.TSerialization import serialize
from vcellvismesh.ttypes import ChomboIndexData
from vcellvismesh.ttypes import FiniteVolumeIndexData
from vcellvismesh.ttypes import MovingBoundaryIndexData
from vcellvismesh.ttypes import VisIrregularPolyhedron
from vcellvismesh.ttypes import VisLine
from vcellvismesh.ttypes import VisMesh
from vcellvismesh.ttypes import VisPolygon
from vcellvismesh.ttypes import VisTetrahedron


def writeChomboVolumeVtkGridAndIndexData(visMesh, domainname, vtkfile, indexfile):
    assert isinstance(visMesh, VisMesh)
    assert type(domainname) is str

    originalVisMesh = visMesh
    correctedVisMesh = originalVisMesh  # same mesh if no irregularPolyhedra
    if originalVisMesh.irregularPolyhedra is not None:
        correctedVisMesh = copy.deepcopy(visMesh)
        if correctedVisMesh.tetrahedra is None:
            correctedVisMesh.tetrahedra = []
        for irregularPolyhedron in correctedVisMesh.irregularPolyhedra:
            tets = createTetrahedra(irregularPolyhedron, correctedVisMesh)
            for tet in tets:
                correctedVisMesh.tetrahedra.append(tet)
        correctedVisMesh.irregularPolyhedra = None

    assert isinstance(correctedVisMesh, VisMesh)
    vtkgrid = getVolumeVtkGrid(correctedVisMesh)
    assert isinstance(vtkgrid, vtk.vtkUnstructuredGrid)
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


def writeChomboMembraneVtkGridAndIndexData(visMesh, domainname, vtkfile, indexfile):
    assert isinstance(visMesh, VisMesh)
    assert type(domainname) is str

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


def writeFiniteVolumeSmoothedVtkGridAndIndexData(visMesh, domainName, vtuFile, indexFile):
    assert isinstance(visMesh, VisMesh)
    vtkgrid = getVolumeVtkGrid(visMesh)
    vtkgridSmoothed = smoothUnstructuredGridSurface(vtkgrid)
    writevtk(vtkgridSmoothed, vtuFile)
    finiteVolumeIndexData = FiniteVolumeIndexData()
    finiteVolumeIndexData.finiteVolumeIndices = []
    finiteVolumeIndexData.domainName = domainName
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
        print "didn't find any indices ... bad"

    writeFiniteVolumeIndexData(indexFile, finiteVolumeIndexData)


def writeFiniteVolumeIndexData(finiteVolumeIndexFile, finiteVolumeIndexData):
    assert isinstance(finiteVolumeIndexData, FiniteVolumeIndexData)
    blob = serialize(finiteVolumeIndexData, protocol_factory=TBinaryProtocol.TBinaryProtocolFactory())
    ff = open(finiteVolumeIndexFile,'wb')
    ff.write(blob)
    ff.close()
    print("wrote finitevolume data to file "+str(finiteVolumeIndexFile))


def writeMovingBoundaryVolumeVtkGridAndIndexData(visMesh, domainName, vtuFile, indexFile):
    assert isinstance(visMesh, VisMesh)
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
        print "didn't find any indices ... bad"

    if movingBoundaryIndexData.movingBoundaryVolumeIndices != None and len(movingBoundaryIndexData.movingBoundaryVolumeIndices) == 0:
        print "didn't find any indices ... bad"

    if movingBoundaryIndexData.movingBoundarySurfaceIndices != None and len(movingBoundaryIndexData.movingBoundarySurfaceIndices) == 0:
        print "didn't find any indices ... bad"

    writeMovingBoundaryIndexData(indexFile, movingBoundaryIndexData)


def writeComsolVolumeVtkGridAndIndexData(visMesh, domainName, vtuFile, indexFile):
    assert isinstance(visMesh, VisMesh)
    vtkgrid = getVolumeVtkGrid(visMesh)
    writevtk(vtkgrid, vtuFile)
    # movingBoundaryIndexData = MovingBoundaryIndexData()
    # movingBoundaryIndexData.movingBoundaryVolumeIndices = []
    # movingBoundaryIndexData.domainName = domainName
    # movingBoundaryIndexData.timeIndex = 0
    # if visMesh.dimension == 2:
    #     # if volume
    #     if visMesh.polygons is not None:
    #         for polygon in visMesh.polygons:
    #             movingBoundaryIndexData.movingBoundaryVolumeIndices.append(polygon.movingBoundaryVolumeIndex)
    #     # if membrane
    #     if visMesh.visLines is not None:
    #         for visLine in visMesh.visLines:
    #             movingBoundaryIndexData.movingBoundarySurfaceIndices.append(visLine.movingBoundarySurfaceIndex)
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

    # if movingBoundaryIndexData.movingBoundaryVolumeIndices == None and movingBoundaryIndexData.movingBoundarySurfaceIndices == None:
    #     print "didn't find any indices ... bad"
    #
    # if movingBoundaryIndexData.movingBoundaryVolumeIndices != None and len(movingBoundaryIndexData.movingBoundaryVolumeIndices) == 0:
    #     print "didn't find any indices ... bad"
    #
    # if movingBoundaryIndexData.movingBoundarySurfaceIndices != None and len(movingBoundaryIndexData.movingBoundarySurfaceIndices) == 0:
    #     print "didn't find any indices ... bad"
    #
    #writeMovingBoundaryIndexData(indexFile, None)


def writeMovingBoundaryIndexData(movingBoundaryIndexFile, movingBoundaryIndexData):
    assert isinstance(movingBoundaryIndexData, MovingBoundaryIndexData)
    blob = serialize(movingBoundaryIndexData, protocol_factory=TBinaryProtocol.TBinaryProtocolFactory())
    ff = open(movingBoundaryIndexFile,'wb')
    ff.write(blob)
    ff.close()
    print("wrote movingboundary data to file "+str(movingBoundaryIndexFile))



def writeChomboIndexData(chomboIndexFile, chomboIndexData):
    """

    Returns:
        None:
    """
    assert isinstance(chomboIndexData, ChomboIndexData)
    blob = serialize(chomboIndexData, protocol_factory=TBinaryProtocol.TBinaryProtocolFactory())
    ff = open(chomboIndexFile,'wb')
    ff.write(blob)
    ff.close()
    print("wrote chomboIndex data to file "+str(chomboIndexFile))


#
# read a vtkUnstructuredGrid from the XML format
#
def readvtk(vtkfile):
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
def writevtk(vtkgrid, filename):
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
def writeDataArrayToNewVtkFile(emptyMeshFile, varName, data, newMeshFile):
    assert type(emptyMeshFile) is str
    assert type(varName) is str
    assert type(newMeshFile) is str
    try:
        #raise ImportError("dummy exception")
        import numpy as np
        from vtk.util import numpy_support
        print("writing varData using numpy")

        data = np.array(data)
        vtkgrid = readvtk(emptyMeshFile)
        assert isinstance(vtkgrid, vtk.vtkUnstructuredGrid)

        #
        # add cell data array to the empty mesh for this variable
        #
        dataArray = vtk.vtkDoubleArray()
        dataArray = numpy_support.numpy_to_vtk(data)
        assert isinstance(dataArray, vtk.vtkDoubleArray)
        dataArray.SetName(varName)
        cellData = vtkgrid.GetCellData()
        assert isinstance(cellData, vtk.vtkCellData)
        cellData.AddArray(dataArray)

        #
        # write mesh and data to the file for that domain and time
        #
        writevtk(vtkgrid, newMeshFile)

    except ImportError as nonumpy:
        import array
        print("writing varData using array package")

        print type(data)
        print str(data)
        data = array.array('d', data)
        vtkgrid = readvtk(emptyMeshFile)
        assert isinstance(vtkgrid, vtk.vtkUnstructuredGrid)

        #
        # add cell data array to the empty mesh for this variable
        #
        dataArray = vtk.vtkDoubleArray()
        dataArray.SetVoidArray(data, len(data), 1)
        dataArray.SetNumberOfComponents(1)
        dataArray.SetName(varName)
        cellData = vtkgrid.GetCellData()
        assert isinstance(cellData, vtk.vtkCellData)
        cellData.AddArray(dataArray)

        #
        # write mesh and data to the file for that domain and time
        #
        writevtk(vtkgrid, newMeshFile)


def getPopulatedMeshFileLocation(basedir, simulationDataSetRef, varInfo, timeIndex):
    assert type(basedir) is str
    assert isinstance(simulationDataSetRef, SimulationDataSetRef)
    assert isinstance(varInfo, VariableInfo)
    assert type(timeIndex) is int

    #create the dataset directory if necessary
    if not os.path.isdir(basedir):
        os.makedirs(basedir)

    #
    # compose the specific mesh filename
    #
    timeIndexStr = "%06d" % timeIndex
    domainName = varInfo.domainName
    varName = varInfo.variableVtuName.replace(":", "_")
    simStr = "SimID_" + simulationDataSetRef.simId + "_" + str(simulationDataSetRef.jobIndex)
    vtuDataFile = os.path.join(basedir, simStr + "__" + domainName + "__" + varName + "__" + timeIndexStr + ".vtu")
    return vtuDataFile


def getMembraneVtkGrid(visMesh):
    assert isinstance(visMesh, VisMesh)
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


def getVolumeVtkGrid(visMesh):
    """

    Returns:
        vtk.vtkUnstructuredGrid:
    """
    assert isinstance(visMesh, VisMesh)
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


def getVtkFaceStream(irregularPolyhedron):
    assert isinstance(irregularPolyhedron, VisIrregularPolyhedron)
    faceStream = [len(irregularPolyhedron.polyhedronFaces), ]
    for polyhedronFace in irregularPolyhedron.polyhedronFaces:
        faceStream.append(len(polyhedronFace.getVertices()))
        for v in polyhedronFace.vertices:
            faceStream.append(v)
    intFaceStream = [int(v) for v in faceStream]
    return intFaceStream


def smoothUnstructuredGridSurface(vtkGrid):
    assert isinstance(vtkGrid, vtk.vtkUnstructuredGrid)
    ugGeometryFilter = vtk.vtkUnstructuredGridGeometryFilter()
    ugGeometryFilter.PassThroughPointIdsOn()
    ugGeometryFilter.MergingOff()
    try:
        ugGeometryFilter.SetInputData(vtkGrid)
    except AttributeError:
        ugGeometryFilter.SetInput(vtkGrid)
    ugGeometryFilter.Update()
    surfaceUnstructuredGrid = ugGeometryFilter.GetOutput()
    assert isinstance(surfaceUnstructuredGrid, vtk.vtkUnstructuredGrid)
    originalPointsIdsName = ugGeometryFilter.GetOriginalPointIdsName()

    cellData = surfaceUnstructuredGrid.GetCellData()
    numCellArrays = cellData.GetNumberOfArrays()
    for i in range(0, numCellArrays):
        cellArrayName = cellData.GetArrayName(i)
        print("CellArray(" + str(i) + ") '" + cellArrayName + "')")
    pointData = surfaceUnstructuredGrid.GetPointData()
    assert isinstance(pointData, vtk.vtkPointData)
    numPointArrays = pointData.GetNumberOfArrays()
    for i in range(0, numPointArrays):
        pointArrayName = pointData.GetArrayName(i)
        print("PointArray(" + str(i) + ") '" + pointArrayName + "'")

    geometryFilter = vtk.vtkGeometryFilter()
    assert isinstance(geometryFilter, vtk.vtkGeometryFilter)
    try:
        geometryFilter.SetInputData(surfaceUnstructuredGrid)
    except AttributeError:
        geometryFilter.SetInput(surfaceUnstructuredGrid)
    geometryFilter.Update()
    polyData = geometryFilter.GetOutput()
    assert isinstance(polyData, vtk.vtkPolyData)

    filter = vtk.vtkWindowedSincPolyDataFilter()
    try:
        filter.SetInputData(polyData)
    except AttributeError:
        filter.SetInput(polyData)
    filter.SetNumberOfIterations(15)
    filter.BoundarySmoothingOff()
    filter.FeatureEdgeSmoothingOff()
    filter.SetFeatureAngle(120.0)
    filter.SetPassBand(0.001)
    filter.NonManifoldSmoothingOff()
    filter.NormalizeCoordinatesOn()
    filter.Update()

    smoothedPolydata = filter.GetOutput()

    smoothedPoints = smoothedPolydata.GetPoints()
    assert isinstance(smoothedPoints, vtk.vtkPoints)

    smoothedPointData = smoothedPolydata.GetPointData()
    assert isinstance(smoothedPointData, vtk.vtkPointData)
    pointIdsArray = smoothedPointData.GetArray(originalPointsIdsName)
    assert isinstance(pointIdsArray, vtk.vtkIdTypeArray)
    pointsIdsArraySize = pointIdsArray.GetSize()
    origPoints = vtkGrid.GetPoints()
    for i in range(0, pointsIdsArraySize):
        pointId = pointIdsArray.GetValue(i)
        smoothedPoint = smoothedPoints.GetPoint(i)
        origPoints.SetPoint(pointId, smoothedPoint)

    return vtkGrid


def getPointIndices(irregularPolyhedron):
    assert isinstance(irregularPolyhedron, VisIrregularPolyhedron)
    pointIndicesSet = set()
    for face in irregularPolyhedron.polyhedronFaces:
        for pointIndex in face.vertices:
            pointIndicesSet.add(pointIndex)
    pointArray = [int(x) for x in pointIndicesSet]
    return pointArray


def createTetrahedra(clippedPolyhedron, visMesh):
    assert isinstance(clippedPolyhedron, VisIrregularPolyhedron)
    assert isinstance(visMesh, VisMesh)

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
    vtkgrid2 = delaunayFilter.GetOutput()
    assert isinstance(vtkgrid2, vtk.vtkUnstructuredGrid)

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
        if (isinstance(cell, vtk.vtkTetra)):
            vtkTet = cell
            assert isinstance(vtkTet, vtk.vtkTetra)
            tetPointIds = vtkTet.GetPointIds()
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

#import sys

def main():

    #sys.setrecursionlimit(100000)

    try:
        parser = argparse.ArgumentParser()
        list_of_meshtypes = ["chombovolume", "chombomembrane", "finitevolume", "movingboundary", "comsolvolume"]
        parser.add_argument("meshtype", help="type of visMesh processing required and index file generated", choices=list_of_meshtypes)
        parser.add_argument("domainname", help="domain name for output mesh")
        parser.add_argument("vismeshfile",help="filename of input visMesh to be processed (thrift serialization via TBinaryProtocol)")
        parser.add_argument("vtkfile",help="filename of output vtk mesh (VTK XML unstructured grid")
        parser.add_argument("indexfile",help="filename of output ChomboIndexData or FiniteVolumeIndexData (thrift serialization via TBinaryProtocol)")
        args = parser.parse_args()


        f_vismesh = open(args.vismeshfile, "rb")
        blob_vismesh = f_vismesh.read()
        print("read "+str(len(blob_vismesh))+" bytes from "+args.vismeshfile)
        f_vismesh.close()

        visMesh = VisMesh()
        protocol_factory = TBinaryProtocol.TBinaryProtocolFactory
    #    deserialize(visMesh, blob_vismesh, protocol_factory = protocol_factory())
        print("starting deserialization")
        deserialize(visMesh, blob_vismesh, protocol_factory = protocol_factory())
        print("done with deserialization")

        if args.meshtype == "chombovolume":
            writeChomboVolumeVtkGridAndIndexData(visMesh, args.domainname, args.vtkfile, args.indexfile)
        elif args.meshtype == "chombomembrane":
            writeChomboMembraneVtkGridAndIndexData(visMesh, args.domainname, args.vtkfile, args.indexfile)
        elif args.meshtype == "finitevolume":
            writeFiniteVolumeSmoothedVtkGridAndIndexData(visMesh, args.domainname, args.vtkfile, args.indexfile)
        elif args.meshtype == "movingboundary":
            writeMovingBoundaryVolumeVtkGridAndIndexData(visMesh, args.domainname, args.vtkfile, args.indexfile)
        elif args.meshtype == "comsolvolume":
            writeComsolVolumeVtkGridAndIndexData(visMesh, args.domainname, args.vtkfile, args.indexfile)
        else:
            raise Exception("meshtype "+str(args.meshtype)+" not supported")

    except:
        e_info = sys.exc_info()
        traceback.print_exception(e_info[0],e_info[1],e_info[2],file=sys.stdout)
        sys.stderr.write("exception: "+str(e_info[0])+": "+str(e_info[1])+"\n")
        sys.stderr.flush()
        sys.exit(-1)
    else:
        sys.exit(0)


if __name__ == '__main__':
    main()
