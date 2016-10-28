import sys

sys.path.append("D:/Developer/VTK6.0.0/build/Wrapping/Python")
sys.path.append("D:/Developer/VTK6.0.0/build/Wrapping/Python")

import vtk
import vtk.qt4
import vtk.qt4.QVTKRenderWindowInteractor
print("vtk imported without an exception")

import visgui.visQt as visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

from visContext.visContextAbstract import visContextAbstract
from visContext.visContextAbstract import overrides


class visContextVTK(visContextAbstract):

    def setMaxColormapValue(self, maxValue):
        pass

    def getPick(self, screenX, screenY, dataReadyCallback, onErrorCallback):
        pass

    def getMDVariableNames(self):
        pass

    def doLineout(self, startPoint, endPoint, dataReadyCallback=None, onErrorCallback=None):
        pass

    def setMinColormapValue(self, minValue):
        pass

    def __init__(self):
        visContextAbstract.__init__(self)
        #self._var = ""
        self._parent = None
        self._frame = None
        self._widget = None
        self._dataset = None
        self._plot = None
        self._renderer = None
        self.renWin = None
        assert isinstance(self._widget,vtk.qt4.QVTKRenderWindowInteractor.QVTKRenderWindowInteractor) or self._widget is None
        assert isinstance(self._dataset,Dataset) or self._dataset is None
        assert isinstance(self._plot,Plot) or self._dataset is None
 
        #self._variable = None

        self._currentOperator = None  # None, "Slice", "Clip"
        self._currentPlot = None      # None, ("Pseudocolor", varName)
        self._hbox = None
        self._operatorEnabled = False
        self._operatorAxis = 0 # 0=x, 1=y, 2=z
        self._operatorPercent = 50
        self._operatorProject2d = False

    @overrides(visContextAbstract)
    def getRenderWindow(self,parent):
        if self._frame is None:
            self._parent = parent
            self._frame = QtGui.QFrame(parent)
            self._frame.setObjectName("vtkRenderWindowFrame1")
            self._frame.setMinimumSize(799, 800)
            self._hbox = QtGui.QHBoxLayout(self._frame)
            self._frame.setLayout(self._hbox)
            self._widget = vtk.qt4.QVTKRenderWindowInteractor.QVTKRenderWindowInteractor(self._frame)
            self._hbox.addWidget(self._widget)
            self._widget.Initialize()
            self._widget.Start()

            renderer = vtk.vtkRenderer()
            renderer.SetBackground(0,0,0) #  Background color white
            renWin = self._widget.GetRenderWindow()
            renWin.AddRenderer(renderer)
            self._widget.repaint()

        return self._frame

    @overrides(visContextAbstract)
    def installEventFilter(self, eventFilter):
        assert isinstance(self._frame, QtGui.QFrame)
        self._frame.installEventFilter(eventFilter)



    @overrides(visContextAbstract)
    def openOne(self, filename, vtuVariableName, bSameDomain, onSuccessCallback, onErrorCallback):
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextVisit: openOne("+filename+","+vtuVariableName+","+str(bSameDomain)+") ... PREPARING ASYNCH TASK"
        try:
            self._timer = QtCore.QTimer.singleShot(50, self.openOne_internal(filename,vtuVariableName,bSameDomain, onSuccessCallback, onErrorCallback))
        except:
            print("\n\nvisContextVisit: openOne() failed to create or dispatch task")
           # print(exc.message)


    def openOne_internal(self,filename,variableName,bSameDomain,onSuccessCallback,onErrorCallback):
        print("in visContextVTK.open(): begin")
        #self._var = None
        filename = str(filename)
        print("filename is "+filename)
        self._dataset = Dataset(filename)
        if self._renderer is None:
            self._renderer = vtk.vtkRenderer()

        if self._plot is not None and self._plot._actor is not None:
            self._renderer.RemoveActor(self._plot.getActor())

        self._plot = PseudocolorPlot(self._dataset,variableName)
        actor = self._plot.getActor()
        self._renderer.AddActor(actor)
        self._renderer.SetBackground(1,0,0) #  Background color white

        self.renWin=self._widget.GetRenderWindow()
        self.renWin.AddRenderer(self._renderer)

        self._setVariable(variableName)
        onSuccessCallback(None)

    @overrides(visContextAbstract)
    def getVariableName(self):
        if (self._plot is not None):
            return self._plot.getVar()

    @overrides(visContextAbstract)
    def getPickMode(self):
        return (self._plot != None)

    def _setVariable(self,var):
        print("in visContextVTK.setVariable("+str(var)+"): begin")
        self._plot.setVariable(var)
        actor = self._plot.getActor()
       # Add the actor to the scene

        renderer = vtk.vtkRenderer()
        renderer.AddActor(actor)
        renderer.SetBackground(1,0,0) #  Background color white

        self.renWin=self._widget.GetRenderWindow()
        self.renWin.AddRenderer(renderer)
        self._widget.repaint()

    def _updateDisplay(self):
        self._widget.repaint()

    @overrides(visContextAbstract)
    def quit(self):
        pass # nothing special to close

    @overrides(visContextAbstract)
    def setOperatorEnabled(self, bEnable):
        assert isinstance(bEnable,bool)
        self._operatorEnabled = bEnable
        self._updateDisplay()

    @overrides(visContextAbstract)
    def getOperatorEnabled(self):
        return self._operatorEnabled

    @overrides(visContextAbstract)
    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
        self._operatorAxis = axis
        self._updateDisplay()
       
    @overrides(visContextAbstract)
    def getOperatorAxis(self):
        return self._operatorAxis
    
    @overrides(visContextAbstract)
    def setOperatorPercent(self, percent, onSuccessCallback, onErrorCallback):
        assert ((percent >=0) and (percent <=100))
        self._operatorPercent = percent
        try:
            self._updateDisplay()
            onSuccessCallback()
        except:
            onErrorCallback()

    @overrides(visContextAbstract)
    def getOperatorPercent(self):
        return self._operatorPercent

    @overrides(visContextAbstract)
    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)
        self._operatorProject2d = bProject2d
        self._updateDisplay()

    @overrides(visContextAbstract)
    def getOperatorProject2d(self):
        return self._operatorProject2d


class Dataset(object):
    def __init__(self, filename):
        self._filename = filename
        self._ugrid = None
        assert isinstance(self._ugrid,vtk.vtkUnstructuredGrid) or self._ugrid == None

    def _getGrid(self):
        if self._ugrid == None:
            reader = vtk.vtkXMLUnstructuredGridReader()
            reader.SetFileName(str(self._filename))
            reader.Update()
            self._ugrid = reader.GetOutput()
        assert isinstance(self._ugrid,vtk.vtkUnstructuredGrid)
        return self._ugrid

    def getFilename(self):
        return self._filename

    def getVariableNames(self):
        cellData = self._getGrid().GetCellData()
        return [cellData.GetArrayName(i) for i in range(cellData.GetNumberOfArrays())]


class Plot(object):
    def __init__(self,dataset):
        assert isinstance(dataset,Dataset)
        self._dataset = dataset
        self._actor = vtk.vtkActor()
        self._lut = None
        assert isinstance(self._lut,vtk.vtkLookupTable) or self._lut == None

    def getDataset(self):
        return self._dataset

    @staticmethod
    def _getLUT(low, high):
        lut = vtk.vtkLookupTable()
        lut.SetNumberOfTableValues(256)
        lut.SetHueRange(0.0,0.6)
        lut.SetValueRange(1,1)
        lut.SetAlphaRange(1,1)
        lut.SetRange(low,high)
        lut.Build()    
        return lut

class PseudocolorPlot(Plot):
    def __init__(self,dataset,var):
        super(PseudocolorPlot,self).__init__(dataset)
        self._var = var

    def getVar(self):
        return self._var

    def setVariable(self,var):
        self._var = str(var)

    def getActor(self):
        uGrid = self._dataset._getGrid()       
        numCells = uGrid.GetNumberOfCells()
        cellData = uGrid.GetCellData()
        (gridMinX,gridMaxX,gridMinY,gridMaxY,gridMinZ,gridMaxZ) = uGrid.GetBounds()
 
        if self._var == None:
            self._var = cellData.GetArrayName(0)
        cellScalars1 = cellData.GetArray(self._var)
        if cellScalars1 != None:
            (dataMin1,dataMax1) = cellScalars1.GetRange()
        else:
            dataMin1=0
            dataMax1=1

        #plane = vtk.vtkPlane()
        #plane.SetNormal(0,-1,0)
        #plane.SetOrigin(30,30,30)

        #clip = vtk.vtkClipDataSet()
        #clip.SetClipFunction(plane)
        #clip.GenerateClipScalarsOn()
        ##clippedGrid.SetValue(0.5)

        mapper1 = vtk.vtkDataSetMapper()
        if vtk.vtkVersion.GetVTKMajorVersion() == 6: 
        #    clip.SetInputConnection(uGrid)
        #    g = vtk.vtkDataSetSurfaceFilter()
        #    g.SetInputConnection(clip.GetOutputPort())
        #    mapper1 = vtk.vtkPolyDataMapper()
        #    mapper1.SetInputConnection(g.GetOutputPort())
        #    #mapper1.SetInputData(clip.getOutput())
            mapper1.SetInputData(uGrid)  # vtk 6.0
        else:
        #    clip.SetInput(uGrid)
        #    #clip.InsideOutOn()
        #    clip.Update()
        #    mapper1.SetInput(clip.GetOutput())      # vtk 5.0
            mapper1.SetInput(uGrid)      # vtk 5.0

        mapper1.SetLookupTable(self._getLUT(dataMin1,dataMax1))
        mapper1.SetScalarRange(dataMin1,dataMax1)
        mapper1.SelectColorArray(self._var)
        mapper1.ScalarVisibilityOn()
        mapper1.SetScalarModeToUseCellFieldData()
       # mapper1.SetColorModeToMapScalars()
        mapper1.Update()

        self._actor = vtk.vtkActor()
        self._actor.SetMapper(mapper1)
        #actor1.GetProperty().SetRepresentationToWireframe();
        #actor1.GetProperty().EdgeVisibilityOn();
        self._actor.GetProperty().SetEdgeColor(0,0,0)
        #actor1.GetProperty().SetLineWidth(1.5);

        return self._actor
 