try:
    import sys
    import visit
    import visit.evalfuncs
    import visit.frontend
    import visQt 
    import visit.pyside_support
    import visit.writescript
    QtGui = visQt.QtGui
    QtCore = visQt.QtCore
    print("visit imported without an exception")
except ImportError as ex:
    print("visit import : exception happened "+str(ex))

import asynchClientTaskManager
from asynchClientTaskManager import AsynchClientTaskManager

import asynchClientTask
from asynchClientTask import AsynchClientTask

import lineoutAsynchClientTask
import timeSeriesAsynchClientTask
from lineoutAsynchClientTask import LineoutAsynchClientTask
from timeSeriesAsynchClientTask import TimeSeriesAsynchClientTask

import vcellProxy
import visContextAbstract

from asynchClientTask import *


class PlotWindow(QtGui.QWidget):
 
    def __init__(self,windowID):
        print("in PlotWindow() constructor: begin")
        super(PlotWindow,self).__init__()
        self.setMinimumSize(500, 500)
        hbox = QtGui.QHBoxLayout(self)
        self.setLayout(hbox)
        self._rwin = visit.pyside_support.GetRenderWindow(windowID)
        hbox.addWidget(self._rwin)
        print("in PlotWindow() constructor: end")

    def getRenderWindow(self):
        return self._rwin
        
class SuppressWindow(QtCore.QObject):
    def eventFilter(self, obj, evt):
        if QtCore.QEvent.Show:
            print "Ignoring show pick window event"
            evt.ignore()
            return True 
        #all others return false since we won't be handling those events
        return False
   
class visContextVisit(visContextAbstract.visContextAbstract):

#    import fakeVisit as visit


    def __init__(self):
        visContextAbstract.visContextAbstract.__init__(self)
        self._plotWindow = PlotWindow(1)
        self._asynchClientTaskManager = asynchClientTaskManager.AsynchClientTaskManager(self._plotWindow)
        self._databaseName = None
        self._variable = None

        self._defaultOperatorAxis = 0 # 0=x, 1=y, 2=z
        self._defaultOperatorPercent = 50
        self._defaultOperatorProject2d = False

        self._currentOperator = None  # None, "Slice", "Clip"
        self._currentPlot = None      # None, ("Pseudocolor", varName)
        self._operatorEnabled = False
        self._operatorAxis = self._defaultOperatorAxis
        self._operatorPercent = self._defaultOperatorPercent
        self._operatorProject2d = self._defaultOperatorProject2d
 
        self._defaultPseudoColorAttributes = self._getDefaultPseudoColorAttributes()
        self._pseudocolorAttributes = self._defaultPseudoColorAttributes

        self._pickMode = 0     # 0 = pickMode off   1 = pickMode on

#        self._vcellProxy = VCellProxyHandler()

        #visit.SuppressMessages(2)
        #visit.SuppressQueryOutputOn()
        #visQt.QtGui.QDockWidget().setWidget(visit.pyside_support.GetOtherWindow("Pick"))
        #pw = visit.pyside_support.GetOtherWindow("Pick")
        #pw.installEventFilter(SuppressWindow())
        #print(pw.__class__.__name__)
        self._pickDockWidget = visQt.QtGui.QDockWidget()
        self._pickDockWidget.setWidget(visit.pyside_support.GetOtherWindow("Pick")) 


        self._infoWindowDockWidget = visQt.QtGui.QDockWidget()
        self._infoWindowDockWidget.setWidget(visit.pyside_support.GetOtherWindow("Information")) 
        self.setPickMode(1)

 #   def getVCellProxy(self):
 #       return self._vcellProxy

    def getRenderWindow(self, parent):
        return self._plotWindow


    def getBareRenderWindow(self):
        return self._plotWindow.getRenderWindow()

    def _openOne(self, filename, vtuVariableName, bSameDomain):
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextVisit._openOne("+filename+","+vtuVariableName+","+str(bSameDomain)+") ... INTERNAL METHOD CALLED FROM ASYNCH TASK"
        bSameDomain = False
        assert(isinstance(bSameDomain,bool))
        if (self._databaseName != filename):
            self._databaseName = filename;
            if (bSameDomain):
                retcode = visit.ReplaceDatabase(self._databaseName)
            else:
                retcode = visit.OpenDatabase(self._databaseName)   

        self._variable = vtuVariableName 
        print("new database name is \"" + self._databaseName + "\"")
        self._updateDisplay()
        
    def getMDVariableNames(self):
        initialVarList = list()
        md = self._getMetaData()
        for i in xrange(md.GetNumScalars()):
            print md.GetScalars(i).name
            initialVarList.append(md.GetScalars(i).name)
        # remove duplicates
        varList = removeDupes(initialVarList)
        return varList
        
    #def setVariable(self,var):
    #    if var != None:
    #        var = str(var)
    #    #if not (var.startswith('vcRegion')):
    #    #    domainStr = str(self._visDataContext.getCurrentDomain())
    #    #    if len(domainStr)>0:
    #    #        var=domainStr+'__DOMAINSEPARATOR__'+var
    #    self._variable = var
    #    print("in visContextVisit.setVariable("+str(var)+"): begin")
    #    #SetActiveWindow(self.windowID)
    #    self._updateDisplay()
    #    print("in visContextVisit.addVariable(): end")


    def getVariable(self):
         return self._variable   

    def getPickMode(self):
        return self._pickMode

    def setPickMode(self, newMode):
        assert newMode in [0,1]
        self._pickMode = newMode


    def resetPickLetter(self):
        visit.ResetPickLetter()

    def _updateDisplay(self):
        print("")
        print("_updateDisplay() - start  ..... variable="+str(self._variable)+", plot="+str(self._currentPlot)+", op="+str(self._currentOperator)+", opEnabled="+str(self._operatorEnabled))
        #
        # remove old plot if not applicable
        #
        if (self._currentPlot != None and (self._variable == None or (self._currentPlot[1] != self._variable))):
            print("    _updateDisplay() - removing plots and operators")
            visit.DeleteAllPlots()
            self._currentPlot = None
            visit.RemoveAllOperators()
            self._currentOperator = None

        #
        # remove old operator if not needed
        #
        if (self._currentOperator != None and self._operatorEnabled == False):
            print("    _updateDisplay() - removing operators, operatorEnabled is False")
            visit.RemoveAllOperators()
            self._currentOperator = None

        #
        # remove old operator if wrong type
        #
        if (self._currentOperator == "Slice" and self._operatorProject2d == False):
            print("    _updateDisplay() - removing operators, 'Slice' incompatible with project2d==False")
            visit.RemoveAllOperators()
            self._currentOperator = None
        if (self._currentOperator == "Clip" and self._operatorProject2d == True):
            print("    _updateDisplay() - removing operators, 'Clip' incompatible with project2d==True")
            visit.RemoveAllOperators()
            self._currentOperator = None

        #
        # add new plot if needed
        #
        if (self._variable != None and self._currentPlot == None):
            print("    _updateDisplay() - adding 'Pseudocolor' plot for variable '"+str(self._variable)+"'")
            print("    _updateDisplay() -- variable without str = ")
            print(self._variable)
            visit.AddPlot("Pseudocolor", str(self._variable))
            visit.SetPlotOptions(self._pseudocolorAttributes)
            self._currentPlot = ("Pseudocolor",str(self._variable))

        #
        # add new operator if needed
        #
        if (self._operatorEnabled == True and self._currentPlot != None):
            if (self._currentOperator == None):
                if (self._operatorProject2d):
                    print("    _updateDisplay() - adding operator 'Slice'")
                    visit.AddOperator("Slice")
                    self._currentOperator = "Slice"
                else:
                    print("    _updateDisplay() - adding operator 'Clip'")
                    visit.AddOperator("Clip")
                    self._currentOperator = "Clip"
            
            if (self._currentOperator == "Slice"):
                print("    _updateDisplay() - setting operator options for 'Slice'")
                visit.SetOperatorOptions(self._getSliceAttributes())
            else:
                print("    _updateDisplay() - setting operator options for 'Clip'")
                visit.SetOperatorOptions(self._getClipAttributes())		

        #
        # Redraw
        #
        print("    _updateDisplay() - redrawing")
        visit.DrawPlots()
        print("_updateDisplay() - end")
        print("")




    def setOperatorEnabled(self, bEnable):
        assert isinstance(bEnable,bool)
        self._operatorEnabled = bEnable
        self._updateDisplay()

    def getOperatorEnabled(self):
        return self._operatorEnabled

    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
        self._operatorAxis = axis;
        self._updateDisplay()
       
    def getOperatorAxis(self):
        return self._operatorAxis
    
    def setOperatorPercent(self, percent):
        assert ((percent >=0) and (percent <=100))
        self._operatorPercent = percent
        self._updateDisplay()

    def getOperatorPercent(self):
        return self._operatorPercent

    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)
        self._operatorProject2d = bProject2d
        self._updateDisplay()

    def getOperatorProject2d(self):
        return self._operatorProject2d

    def quit(self):
        visit.Close()

    #def setTimeIndex(self, index):

    #    #nStates = visit.TimeSliderGetNStates()      
    #    print(index)
    #    #visit.SetTimeSliderState(index)
    #    self._visDataContext.setCurrentTimeIndex(index)
    #    print("Setting time index.")
    #    try:
    #       self._vcellProxy.open()
    #       self._visDataContext.setCurrentDataSetTimePoints(self._vcellProxy.getClient().getTimePoints(self._visDataContext.getCurrentDataSet()))
    #    finally:
    #        self._vcellProxy.close()
    #    self._visDataContext.setCurrentTimePoint(self._visDataContext.getCurrentDataSetTimePoints()[index])
    #    visit.DrawPlots()


    #def getNumberOfTimePoints(self):
    #    numberOfStates = self.getVCellProxy().getClient().getEndTimeIndex() + 1
    #    print("VCell Proxy says number of time points available is: "+str(numberOfStates))
    #    #numberOfStates = visit.GetMetaData(self._databaseName).numStates
    #    assert (isinstance(numberOfStates, int))
    #    return numberOfStates
    
    
    def clearPicks(self):
        if (self.getVariable() is not None):
            visit.ClearPickPoints()

    #def getTimes(self):
    #    if (self._databaseName == None):
    #        return []
    #    #md = visit.GetMetaData(self._databaseName)
    #    #if (md != None):
    #    #    times = md.times
    #    try:
    #        self._vcellProxy.open()
    #        times = self._vcellProxy.getClient().getTimePoints(self._visDataContext.getCurrentDataSet())
    #    finally:
    #        self._vcellProxy.close()
    #    if (times !=None):
    #        #assert (isinstance(times, tuple))
    #        return times
    #    else:
    #        return []

    def _getSliceAttributes(self):
        sliceAttributes = visit.SliceAttributes()
        sliceAttributes.originType = sliceAttributes.Percent
        if self._operatorProject2d:
            sliceAttributes.project2d = int(True)
        else:
            print("previous value for sliceAttributes.project2d = "+str(sliceAttributes.project2d))
            sliceAttributes.project2d = int(False)
        sliceAttributes.axisType = self._operatorAxis
        sliceAttributes.originPercent = self._operatorPercent
        print(sliceAttributes)
        return sliceAttributes

    def _getClipAttributes(self):
        (minx,miny,minz) = self._getMinSpatialExtents()
        (maxx,maxy,maxz) = self._getMaxSpatialExtents()
        clipAttributes = visit.ClipAttributes()
        clipAttributes.quality = clipAttributes.Accurate  # Fast, Accurate
        clipAttributes.funcType =  clipAttributes.Plane  # Plane, Sphere
        clipAttributes.plane1Status = 1  
        clipAttributes.plane2Status = 0 
        clipAttributes.plane3Status = 0
        midx = ((minx+maxx)/2.0)
        midy = ((miny+maxy)/2.0)
        midz = ((minz+maxz)/2.0)
        if (self._operatorAxis==0):
            newX = minx + self._operatorPercent/100.0*(maxx-minx)
            clipAttributes.plane1Origin = ( newX, midy, midz )
            clipAttributes.plane1Normal = (1, 0, 0)
        elif (self._operatorAxis==1):
            newY = miny + self._operatorPercent/100.0*(maxy-miny)
            clipAttributes.plane1Origin = ( midx, newY, midz )
            clipAttributes.plane1Normal = (0, 1, 0)
        elif (self._operatorAxis==2):
            newZ = minz + self._operatorPercent/100.0*(maxz-minz)
            clipAttributes.plane1Origin = ( midx, midy, newZ )
            clipAttributes.plane1Normal = (0, 0, 1)
        clipAttributes.planeInverse = 0
        clipAttributes.planeToolControlledClipPlane = clipAttributes.None  # None, Plane1, Plane2, Plane3  -We're not even using this tool
        clipAttributes.center = (0, 0, 0) # not used at present
        clipAttributes.radius = 1 # not used presently
        clipAttributes.sphereInverse = 0 # not used presently
        print(clipAttributes)
        return clipAttributes

    def setMaxColormapValue(self, max):
        assert isinstance(max,int) or isinstance(max,float) or instance(max, double)
        self._getPseudocolorAttributes().max = max
        self._getPseudocolorAttributes().maxFlag = 1

    def setMinColormapValue(self, min):
        assert isinstance(min,int) or isinstance(min,float) or instance(min, double)
        self._getPseudocolorAttributes().min = min
        self._getPseudocolorAttributes().minFlag = 1

    def doLineout(self, startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None): # points must be tuples
        assert isinstance(startPoint, tuple)
        assert isinstance(endPoint, tuple)
        try:
            _lineoutAsynchClientTask = LineoutAsynchClientTask(startPoint, endPoint, dataReadyCallback, onErrorCallback)
            print("visContextVisit.doLineout: created task")
            self._asynchClientTaskManager.addTask(_lineoutAsynchClientTask)
        except:
            print("visContextVisit: Problem creating lineoutAsynchClientTask or adding it to the asynchClientTaskManager")

    def openOne(self, filename, vtuVariableName, bSameDomain):
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextVisit: openOne("+filename+","+vtuVariableName+","+str(bSameDomain)+") ... PREPARING ASYNCH TASK"
        try:
            _openOneAsynchClientTask = OpenOneAsynchClientTask(self,filename,vtuVariableName,bSameDomain, None, None)
            self._asynchClientTaskManager.addTask(_openOneAsynchClientTask)
        except:
            print("\n\nvisContextVisit: openOne() failed to create or dispatch task")


    #def doTimeSeries(self, point, dataReadyCallback = None, onErrorCallback = None):
    #    _timeSeriesAsynchClientTask = None
    #    assert isinstance(point, tuple)
    #    print("visContextVisit.doTimeSeries: passed assertion")
    #    try:
    #        _timeSeriesAsynchClientTask = TimeSeriesAsynchClientTask(point, dataReadyCallback, onErrorCallback)
    #        print("visContextVisit.doTimeSeries: created task")
    #        self._asynchClientTaskManager.addTask(_timeSeriesAsynchClientTask)
    #        print("visContextVisit.doTimeSeries: created added task to task list")
    #    except:
    #        print("visContextVisit: Problem creating timeSeriesAsynchClientTask or adding it to the asynchClientTaskManager")
    #    return _timeSeriesAsynchClientTask

    def setActiveWindow(self, windowIndex): 
        assert isinstance(windowIndex, int)
        assert windowIndex > 0 and windowIndex < 17
        visit.SetActiveWindow(windowIndex)


    def _getDefaultPseudoColorAttributes(self):
        pseudocolorAttributes = visit.PseudocolorAttributes()
        pseudocolorAttributes.scaling = pseudocolorAttributes.Linear  # Linear, Log, Skew
        pseudocolorAttributes.skewFactor = 1
        pseudocolorAttributes.limitsMode = pseudocolorAttributes.OriginalData  # OriginalData, CurrentPlot
        pseudocolorAttributes.minFlag = 0
        pseudocolorAttributes.min = 0
        pseudocolorAttributes.maxFlag = 0
        pseudocolorAttributes.max = 0
        pseudocolorAttributes.centering = pseudocolorAttributes.Natural  # Natural, Nodal, Zonal
        pseudocolorAttributes.colorTableName = "hot"
        pseudocolorAttributes.invertColorTable = 0
        pseudocolorAttributes.opacityType = pseudocolorAttributes.FullyOpaque  # ColorTable, FullyOpaque, Constant, Ramp, VariableRange
        pseudocolorAttributes.opacityVariable = ""
        pseudocolorAttributes.opacity = 1
        pseudocolorAttributes.opacityVarMin = 0
        pseudocolorAttributes.opacityVarMax = 1
        pseudocolorAttributes.opacityVarMinFlag = 0
        pseudocolorAttributes.opacityVarMaxFlag = 0
        pseudocolorAttributes.pointSize = 0.05
        pseudocolorAttributes.pointType = pseudocolorAttributes.Point  # Box, Axis, Icosahedron, Octahedron, Tetrahedron, SphereGeometry, Point, Sphere
        pseudocolorAttributes.pointSizeVarEnabled = 0
        pseudocolorAttributes.pointSizeVar = "default"
        pseudocolorAttributes.pointSizePixels = 2
        pseudocolorAttributes.lineType = pseudocolorAttributes.Line  # Line, Tube, Ribbon
        pseudocolorAttributes.lineStyle = pseudocolorAttributes.SOLID  # SOLID, DASH, DOT, DOTDASH
        pseudocolorAttributes.lineWidth = 0
        pseudocolorAttributes.tubeDisplayDensity = 10
        pseudocolorAttributes.tubeRadiusSizeType = pseudocolorAttributes.FractionOfBBox  # Absolute, FractionOfBBox
        pseudocolorAttributes.tubeRadiusAbsolute = 0.125
        pseudocolorAttributes.tubeRadiusBBox = 0.005
        pseudocolorAttributes.varyTubeRadius = 0
        pseudocolorAttributes.varyTubeRadiusVariable = ""
        pseudocolorAttributes.varyTubeRadiusFactor = 10
        pseudocolorAttributes.endPointType = pseudocolorAttributes.None  # None, Tails, Heads, Both
        pseudocolorAttributes.endPointStyle = pseudocolorAttributes.Spheres  # Spheres, Cones
        pseudocolorAttributes.endPointRadiusSizeType = pseudocolorAttributes.FractionOfBBox  # Absolute, FractionOfBBox
        pseudocolorAttributes.endPointRadiusAbsolute = 1
        pseudocolorAttributes.endPointRadiusBBox = 0.005
        pseudocolorAttributes.endPointRatio = 2
        pseudocolorAttributes.renderSurfaces = 1
        pseudocolorAttributes.renderWireframe = 0
        pseudocolorAttributes.renderPoints = 0
        pseudocolorAttributes.smoothingLevel = 0
        pseudocolorAttributes.legendFlag = 1
        pseudocolorAttributes.lightingFlag = 1
        return pseudocolorAttributes

    def _getMetaData(self):
        return visit.GetMetaData(self._databaseName)

    def _getMinSpatialExtents(self):
        md = self._getMetaData()
        if md == None:
            return None
        else:
            return md.GetMeshes(0).minSpatialExtents

    def _getMaxSpatialExtents(self):
        md = self._getMetaData()
        if md == None:
            return None
        else:
            return md.GetMeshes(0).maxSpatialExtents


    def setPickAttributes(self):   
    
        pickAttributes = visit.GetPickAttributes()
        pickAttributes.variables = ("default")
        pickAttributes.showIncidentElements = 1
        pickAttributes.showNodeId = 1
        pickAttributes.showNodeDomainLogicalCoords = 0
        pickAttributes.showNodeBlockLogicalCoords = 0
        pickAttributes.showNodePhysicalCoords = 0
        pickAttributes.showZoneId = 1
        pickAttributes.showZoneDomainLogicalCoords = 0
        pickAttributes.showZoneBlockLogicalCoords = 0
        pickAttributes.doTimeCurve = 0
        pickAttributes.conciseOutput = 1
        pickAttributes.showTimeStep = 1
        pickAttributes.showMeshName = 1
        pickAttributes.blockPieceName = ""
        pickAttributes.groupPieceName = ""
        pickAttributes.showGlobalIds = 0
        pickAttributes.showPickLetter = 0  # 1 to show the letters
        pickAttributes.reusePickLetter = 0
        pickAttributes.meshCoordType = pickAttributes.XY  # XY, RZ, ZR
        pickAttributes.createSpreadsheet = 0
        pickAttributes.floatFormat = "%g"
        pickAttributes.timePreserveCoord = 1
        #print("Setting NodePick attributes")
        print(visit.SetPickAttributes(pickAttributes))
        visit.SetWindowMode("zone pick")

    def getPick(self, screenX, screenY):
        self.setPickAttributes()
        visit.ZonePick(screenX,screenY,self.getVariable())
        #print("Pick done. Data: "+str(visit.GetPickOutput()))
        pickOutputObject = visit.GetPickOutputObject()
        visit.SetWindowMode("navigate")
        if pickOutputObject is not None:
            return ("Pick of "+str(self.getVariable())+" at point "+str(pickOutputObject['point'])+ " is: "+str(pickOutputObject[self.getVariable()]))
        else:
            return ("No Data.  Click on plot for values.")

    def getOtherWindow(self, windowName):
        assert isinstance(windowName, basestring)
        print("About to get window object named: "+str(windowName))
        otherWindow = visit.pyside_support.GetOtherWindow(windowName)
        otherWindow = visit.pyside_support.GetOtherWindow(windowName)
        print(str(otherWindow))
        return otherWindow

    def getOtherWindowNames(self):
        return visit.pyside_support.GetOtherWindowNames()

        
        
def removeDupes(seq):
    checked = []
    for e in seq:
        if e not in checked:
            checked.append(e)
    return checked
    


##########################################################
# Asynch Task for Opening Database and changing variable
##########################################################
from asynchClientTask import AsynchClientTask

class OpenOneAsynchClientTask(AsynchClientTask):

    INTERNALSTATE_DATABASE_CONFIRM = "Database Confirm"
    INTERNALSTATE_PLOT_UPDATE_REQUEST = "Plot Update Request"
    INTERNALSTATE_PLOT_CONFIRM = "Plot Confirm"

    def __init__(self, vis, filename, vtuVariableName, bSameDomain, dataReadyCallback = None,  onErrorCallback = None):
        super(OpenOneAsynchClientTask, self).__init__("OpenOne", True, dataReadyCallback, onErrorCallback)
        assert(isinstance(vis,visContextVisit))
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextVisit.OpenOneAsynchClientTask.__init__("+filename+","+vtuVariableName+","+str(bSameDomain)+")"
        self._bSameDomain = bSameDomain
        self._filename = filename
        self._vtuVariableName = vtuVariableName
        self._state = AsynchClientTask.STATE_INITIAL
        self._vis = vis
        print("openOne initial state = "+str(self.getTaskState()))

        self._stateFunctions = {
            AsynchClientTask.STATE_EXCEPTION:               self.foundError, 
            AsynchClientTask.STATE_INITIAL:                 self.doOpenDatabase, 
            self.INTERNALSTATE_DATABASE_CONFIRM:            self.confirmDatabase,
            self.INTERNALSTATE_PLOT_UPDATE_REQUEST:         self.requestPlot,
            self.INTERNALSTATE_PLOT_CONFIRM:                self.confirmPlot,
            AsynchClientTask.STATE_DONE:                    self.done}

    def checkState(self):
        print("checking state. =="+str(self._state))
        try:
            self._stateFunctions[self.getTaskState()]()
        except: 
            print("EXCEPTION CHECKING OPENONE'S STATE.  Prior state was: "+str(self._state))
            self._state = AsynchClientTask.STATE_5_EXCEPTION

    def doOpenDatabase(self):
        print("visContextVisit.OpenOneAsynchClientTask.doOpenDatabase()")
        assert(self._state == AsynchClientTask.STATE_INITIAL)
        assert(isinstance(self._vis,visContextVisit))
        if (self._vis._databaseName != self._filename):
            self._vis._databaseName = self._filename;
            if (self._bSameDomain):
                retcode = visit.ReplaceDatabase(self._vis._databaseName)
            else:
                retcode = visit.OpenDatabase(self._vis._databaseName)   
        print("new database name is \"" + self._vis._databaseName + "\"")
        self._state = self.INTERNALSTATE_DATABASE_CONFIRM


    def confirmDatabase(self):
        print("visContextVisit.OpenOneAsynchClientTask.confirmDatabase()")
        self._state = self.INTERNALSTATE_PLOT_UPDATE_REQUEST

    def requestPlot(self):
        print("visContextVisit.OpenOneAsynchClientTask.requestPlot()")
        self._vis._variable = self._vtuVariableName 
        self._vis._updateDisplay()
        self._state = self.INTERNALSTATE_PLOT_CONFIRM

    def confirmPlot(self):
        print("visContextVisit.OpenOneAsynchClientTask.confirmPlot()")
        self._state = AsynchClientTask.STATE_DONE
 
    def done(self):
        print("visContextVisit.OpenOneAsynchClientTask.done()")
        pass # Someone should call getLineoutData if they haven't already and let me be forgotton memory anytime now

    def foundError(self):
        print("visContextVisit.OpenOneAsynchClientTask.foundError()")
        self._onExceptionEncountered("OpenOneAsychClientTask error encountered")
