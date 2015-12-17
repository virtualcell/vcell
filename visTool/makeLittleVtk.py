import vtk
import vtkService

x = [1,2,3,4]
y = [1,2,3,4]
ugrid = vtk.vtkUnstructuredGrid()
points = vtk.vtkPoints()
points.SetDataTypeToDouble()
xyToNode = [[] for i in range(len(x))]
index = 0
for i, xCoord in enumerate(x):
    for yCoord in y:
          points.InsertNextPoint(xCoord, yCoord, 0.0)
          xyToNode[i].append(index)
          index += 1
ugrid.SetPoints(points)

# Add the volume elements
for i, xCoord in enumerate(x[:-1]):
    for j, yCoord in enumerate(y[:-1]):
          idList = vtk.vtkIdList()
          idList.InsertNextId(xyToNode[i][j])
          idList.InsertNextId(xyToNode[i + 1][j])
          idList.InsertNextId(xyToNode[i + 1][j + 1])
          idList.InsertNextId(xyToNode[i][j + 1])
          ugrid.InsertNextCell(vtk.VTK_QUAD, idList)

vtkService.writevtk(ugrid,"abc.vtu")
vtkService.writeDataArrayToNewVtkFile("abc.vtu","var",[1.,2.,3.,4.,5.,6.,7.,8.,9.],"abc_var.vtu")

