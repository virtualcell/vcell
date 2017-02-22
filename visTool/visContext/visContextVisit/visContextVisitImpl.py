try:
    import exceptions
    import sys
    import os
    import visit
    import visit.evalfuncs
    import visit.frontend
    from visgui import visQt 
    import visit.pyside_support
    import visit.writescript
    QtGui = visQt.QtGui
    QtCore = visQt.QtCore
    print("visit imported without an exception")
except ImportError as ex:
    print("visit import : exception happened "+str(ex))

import visContext
from asynchTaskManager import AsynchTask
from asynchTaskManager import AsynchTaskManager

import vcellProxy
from visContext.visContextAbstract import visContextAbstract
from visContext.visContextAbstract import overrides
from visClipOperatorContext import VisClipOperatorContext

from copy import deepcopy

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
   
class VisContextVisitImpl(visContextAbstract):

#    import fakeVisit as visit


    def __init__(self):
        visContextAbstract.__init__(self)
        self._plotWindow = PlotWindow(1)
        self._asynchTaskManager = AsynchTaskManager(self._plotWindow)
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
        #
        # Start ComputeEngine explicitly to avoid setup dialog later when opening first database
        #
        #visit.OpenComputeEngine("localhost",("-np","1","-nn","1"))
        visit.OpenComputeEngine("localhost",("-serial"))



    def getRenderWindow(self, parent):
        return self._plotWindow


    def getBareRenderWindow(self):
        return self._plotWindow.getRenderWindow()

    def getMDVariableNames(self):
        initialVarList = list()
        md = self._getMetaData()
        for i in xrange(md.GetNumScalars()):
            print md.GetScalars(i).name
            initialVarList.append(md.GetScalars(i).name)
        # remove duplicates
        varList = _removeDupes(initialVarList)
        return varList
        
    def getVariable(self):
         return self._variable   

    def getPickMode(self):
        return self._pickMode

    def setPickMode(self, newMode):
        assert newMode in [0,1]
        self._pickMode = newMode


    def resetPickLetter(self):
        visit.ResetPickLetter()

    def _updateDisplay(self,bSameDomain):
        print("")
        print("_updateDisplay() - start  ..... variable="+str(self._variable)+", plot="+str(self._currentPlot)+", op="+str(self._currentOperator)+", opEnabled="+str(self._operatorEnabled))
        #
        # remove old plot if not applicable
        #
        if (bSameDomain == False or (self._currentPlot != None and (self._variable == None or (self._currentPlot[1] != self._variable)))):
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
            # Turn off display database name in plot window to reduce clutter
            attributes = visit.AnnotationAttributes()
            attributes.databaseInfoFlag=0
            visit.SetAnnotationAttributes(attributes)

        #
        # if operator needed, set operator attributes (add new operator if needed)
        #
        if (self._operatorEnabled == True and self._currentPlot != None):
            #
            # if no operator, then add one
            #
            if (self._currentOperator == None):
                if (self._operatorProject2d):
                    print("    _updateDisplay() - adding operator 'Slice'")
                    visit.AddOperator("Slice")
                    self._currentOperator = "Slice"
                else:
                    print("    _updateDisplay() - adding operator 'Clip'")
                    visit.AddOperator("Clip")
                    self._currentOperator = "Clip"
            
            #
            # set operator attributes
            #
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
        # self._updatePlot()

    def getOperatorEnabled(self):
        return self._operatorEnabled

    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
        self._operatorAxis = axis;
        # self._updatePlot()
       
    def getOperatorAxis(self):
        return self._operatorAxis
    
    def setOperatorPercent(self, visClipOperatorContext, onSuccessCallback, onErrorCallback):
        assert (isinstance(visClipOperatorContext,VisClipOperatorContext))
        print "\n\nvisContextVisitImpl: updateOperatorPercent("+str(visClipOperatorContext.getCurrentOperatorPercent())+") ... PREPARING ASYNCH TASK"
        try:
            _updateOperatorPercentAsynchTask = UpdateOperatorPercentAsynchTask(self, visClipOperatorContext, onSuccessCallback, onErrorCallback)
            self._asynchTaskManager.addTask(_updateOperatorPercentAsynchTask)
        except exceptions.BaseException as exc:
            print("\n\nvisContextVisit: updateOperatorPercent() failed to create or dispatch task")
            print(exc.message)

    def getOperatorPercent(self):
        return self._operatorPercent

    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)
        self._operatorProject2d = bProject2d
        # self._updatePlot()

    def getOperatorProject2d(self):
        return self._operatorProject2d

    def quit(self):
        if sys.platform == "win32":
            # This works on Windows
            os.kill(os.getpid(), -9)
        else:
            # This works on Mac/Linux
            os.system("kill -9 %d" % os.getpid())

    def clearPicks(self):
        if (self.getVariable() is not None):
            visit.ClearPickPoints()

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
            _lineoutAsynchTask = LineoutAsynchTask(startPoint, endPoint, dataReadyCallback, onErrorCallback)
            print("visContextVisit.doLineout: created task")
            self._asynchTaskManager.addTask(_lineoutAsynchTask)
        except:
            print("visContextVisitImpl: Problem creating lineoutAsynchTask or adding it to the asynchTaskManager")

    def openOne(self, filename, vtuVariableName, bSameDomain, onSuccessCallback, onErrorCallback):
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextVisit: openOne("+filename+","+vtuVariableName+","+str(bSameDomain)+") ... PREPARING ASYNCH TASK"
        try:
            _openOneAsynchTask = OpenOneAsynchTask(self,filename,vtuVariableName,bSameDomain, onSuccessCallback, onErrorCallback)
            self._asynchTaskManager.addTask(_openOneAsynchTask)
        except exceptions.BaseException as exc:
            print("\n\nvisContextVisit: openOne() failed to create or dispatch task")
            print(exc.message)


    def updatePlot(self):
        print "\n\nvisContextVisitImpl: updatePlot() ... PREPARING ASYNCH TASK"
        try:
            _updatePlotAsynchTask = UpdatePlotAsynchTask(self, None, None)
            self._asynchTaskManager.addTask(_updatePlotAsynchTask)
        except exceptions.BaseException as exc:
            print("\n\nvisContextVisit: updatePlot() failed to create or dispatch task")
            print(exc.message)


    def _setActiveWindow(self, windowIndex): 
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
    #    pseudocolorAttributes.tubeDisplayDensity = 10
        pseudocolorAttributes.tubeRadiusSizeType = pseudocolorAttributes.FractionOfBBox  # Absolute, FractionOfBBox
        pseudocolorAttributes.tubeRadiusAbsolute = 0.125
        pseudocolorAttributes.tubeRadiusBBox = 0.005
    #    pseudocolorAttributes.varyTubeRadius = 0
    #    pseudocolorAttributes.varyTubeRadiusVariable = ""
    #    pseudocolorAttributes.varyTubeRadiusFactor = 10
    #    pseudocolorAttributes.endPointType = pseudocolorAttributes.None  # None, Tails, Heads, Both
    #    pseudocolorAttributes.endPointStyle = pseudocolorAttributes.Spheres  # Spheres, Cones
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

    def getPick(self, screenX, screenY, onSuccessCallback, onErrorCallback):
        #assert(isinstance(screenX,int))
        #assert(isinstance(screenY,int))
        print "\n\nvisContextVisitImpl: getPick("+str(screenX)+","+str(screenY)+") ... PREPARING ASYNCH TASK"
        try:
            _pickAsynchTask = PickAsynchTask(self,screenX,screenY, onSuccessCallback, onErrorCallback)
            self._asynchTaskManager.addTask(_pickAsynchTask)
        except exceptions.BaseException as exc:
            print("\n\nvisContextVisit: getPick() failed to create or dispatch task")
            print(exc.message)

    def getOtherWindow(self, windowName):
        assert isinstance(windowName, basestring)
        print("About to get window object named: "+str(windowName))
        otherWindow = visit.pyside_support.GetOtherWindow(windowName)
        otherWindow = visit.pyside_support.GetOtherWindow(windowName)
        print(str(otherWindow))
        return otherWindow

    def getOtherWindowNames(self):
        return visit.pyside_support.GetOtherWindowNames()

        
        
def _removeDupes(seq):
    checked = []
    for e in seq:
        if e not in checked:
            checked.append(e)
    return checked
    


##########################################################
# Asynch Task for Opening Database and changing variable
##########################################################

class OpenOneAsynchTask(AsynchTask):
    STATE_INITIAL = "initial"
    STATE_DATABASE_CONFIRM = "Database Confirm"
    STATE_PLOT_UPDATE_REQUEST = "Plot Update Request"
    STATE_PLOT_CONFIRM = "Plot Confirm"
    STATE_DONE = "done"

    def __init__(self, vis, filename, vtuVariableName, bSameDomain, dataReadyCallback = None,  onErrorCallback = None):
        super(OpenOneAsynchTask, self).__init__("OpenOne", dataReadyCallback, onErrorCallback)
        assert(isinstance(vis,VisContextVisitImpl))
        assert(isinstance(filename,basestring))
        assert(isinstance(vtuVariableName,basestring))
        assert(isinstance(bSameDomain,bool))

        print "\n\nvisContextVisit.OpenOneAsynchTask.__init__("+filename+","+vtuVariableName+","+str(bSameDomain)+")"
        self._bSameDomain = bSameDomain
        self._filename = filename
        self._vtuVariableName = vtuVariableName
        self._state = self.STATE_INITIAL
        self._vis = vis
        print("openOne initial state = "+str(self._state))

        self._stateFunctions = {
            self.STATE_INITIAL:                     self.doOpenDatabase, 
            self.STATE_DATABASE_CONFIRM:            self.confirmDatabase,
            self.STATE_PLOT_UPDATE_REQUEST:         self.requestPlot,
            self.STATE_PLOT_CONFIRM:                self.confirmPlot,
            self.STATE_DONE:                        self.done}

    @overrides(AsynchTask)
    def doStep(self):
        self._stateFunctions[self._state]()
        return self._state == self.STATE_DONE

    @overrides(AsynchTask)
    def getResults(self):
        dbName0 = visit.GetPlotList().GetPlots(0).GetDatabaseName()
        metaData0 = visit.GetMetaData(dbName0)
        return (metaData0.GetMeshes(0).minSpatialExtents,metaData0.GetMeshes(0).maxSpatialExtents)

    def doOpenDatabase(self):
        print("OpenOneAsynchTask.doOpenDatabase()")
        assert(self._state == self.STATE_INITIAL) # current state
        if (self._vis._databaseName != self._filename):
            self._vis._databaseName = self._filename;
            if (self._bSameDomain):
                retcode = visit.ReplaceDatabase(self._vis._databaseName)
            else:
                retcode = visit.OpenDatabase(self._vis._databaseName)   
        print("new database name is \"" + self._vis._databaseName + "\"")
        self._state = self.STATE_DATABASE_CONFIRM # new state


    def confirmDatabase(self):
        # a noop state, should we check something ... poll VisIt?
        print("OpenOneAsynchTask.confirmDatabase()")
        assert(self._state == self.STATE_DATABASE_CONFIRM) # current state
        self._state = self.STATE_PLOT_UPDATE_REQUEST # new state

    def requestPlot(self):
        print("OpenOneAsynchTask.requestPlot()")
        assert(self._state == self.STATE_PLOT_UPDATE_REQUEST) # current state
        self._vis._variable = self._vtuVariableName
        self._vis._updateDisplay(self._bSameDomain)
        self._state = self.STATE_PLOT_CONFIRM # new state

    def confirmPlot(self):
        # a noop state, should we check something ... poll VisIt?
        print("OpenOneAsynchTask.confirmPlot()")
        assert(self._state == self.STATE_PLOT_CONFIRM) # current state
        self._state = self.STATE_DONE # new state
 
    def done(self):
        print("OpenOneAsynchTask.done()")
        assert(self._state == self.STATE_DONE) # current state ... shouldn't event reach this


################################################################
# Asynch Task for updating plot after operator settings change
################################################################

class UpdatePlotAsynchTask(AsynchTask):


    STATE_INITIAL = "initial"
    STATE_PLOT_CONFIRM = "Plot Confirm"
    STATE_DONE = "done"

    def __init__(self, vis, dataReadyCallback = None,  onErrorCallback = None):
        super(UpdatePlotAsynchTask, self).__init__("UpdatePlot", dataReadyCallback, onErrorCallback)
        assert(isinstance(vis,VisContextVisitImpl))

        print "\n\UpdatePlotAsynchTask.__init__()"
        self._state = self.STATE_INITIAL
        self._vis = vis
        print("openOne initial state = "+str(self._state))

        self._stateFunctions = {
            self.STATE_INITIAL:                     self.requestPlot, 
            self.STATE_PLOT_CONFIRM:                self.confirmPlot,
            self.STATE_DONE:                        self.done}

    @overrides(AsynchTask)
    def doStep(self):
        self._stateFunctions[self._state]()
        return self._state == self.STATE_DONE

    @overrides(AsynchTask)
    def getResults(self):
        return None

    def requestPlot(self):
        print("UpdatePlotAsynchTask.requestPlot()")
        assert(self._state == self.STATE_INITIAL) # current state
        self._vis._updateDisplay(True)
        self._state = self.STATE_PLOT_CONFIRM  # new state

    def confirmPlot(self):
        # noop state (should we check something)
        print("UpdatePlotAsynchTask.confirmPlot()")
        assert(self._state == self.STATE_PLOT_CONFIRM) # current state
        self._state = self.STATE_DONE # new state
 
    def done(self):
        print("UpdatePlotAsynchTask.done()")
        assert(self._state == self.STATE_DONE) # current state - end state

################################################################
# Asynch Task for updating plot after operator settings change
################################################################

class UpdateOperatorPercentAsynchTask(AsynchTask):


    STATE_INITIAL = "initial"
    STATE_PLOT_CONFIRM = "Plot Confirm"
    STATE_DONE = "done"

    def __init__(self, vis, visClipOperatorContext, dataReadyCallback = None,  onErrorCallback = None):
        super(UpdateOperatorPercentAsynchTask, self).__init__("UpdateOperatorPercent", dataReadyCallback, onErrorCallback)
        assert(isinstance(vis,VisContextVisitImpl))
        assert(isinstance(visClipOperatorContext,VisClipOperatorContext))

        print "\n\UpdateOperatorPercentAsynchTask.__init__()"
        self._state = self.STATE_INITIAL
        self._vis = vis
        self._visClipOperatorContext = deepcopy(visClipOperatorContext)
        print("openOne initial state = "+str(self._state))

        self._stateFunctions = {
            self.STATE_INITIAL:                     self.requestPlot, 
            self.STATE_PLOT_CONFIRM:                self.confirmPlot,
            self.STATE_DONE:                        self.done}

    @overrides(AsynchTask)
    def doStep(self):
        self._stateFunctions[self._state]()
        return self._state == self.STATE_DONE

    def requestPlot(self):
        print("UpdateOperatorPercentAsynchTask.requestPlot()")
        assert(self._state == self.STATE_INITIAL) # current state
        self._vis._operatorPercent = self._visClipOperatorContext.getCurrentOperatorPercent()
        self._vis._updateDisplay(True)
        self._state = self.STATE_PLOT_CONFIRM  # new state

    def confirmPlot(self):
        # noop state (should we check something)
        print("UpdateOperatorPercentAsynchTask.confirmPlot()")
        assert(self._state == self.STATE_PLOT_CONFIRM) # current state
        self._state = self.STATE_DONE # new state
 
    def done(self):
        print("UpdateOperatorPercentAsynchTask.done()")
        assert(self._state == self.STATE_DONE) # current state - end state

    @overrides(AsynchTask)
    def getResults(self):
        return self._visClipOperatorContext.getCurrentOperatorPercent()


################################################################
# Asynch Task for getting a pick from the current plot
################################################################

class PickAsynchTask(AsynchTask):


    STATE_INITIAL = "initial"
#    STATE_PLOT_CONFIRM = "Plot Confirm"
    STATE_DONE = "done"

    def __init__(self, vis, screenX, screenY, dataReadyCallback = None,  onErrorCallback = None):
        super(PickAsynchTask, self).__init__("Pick", dataReadyCallback, onErrorCallback)
        #assert(isinstance(screenX,int))
        #assert(isinstance(screenY,int))
        assert(isinstance(vis,VisContextVisitImpl))
 
        print "\n\PickAsynchTask.__init__()"
        self._state = self.STATE_INITIAL
        self._vis = vis
        self._screenX = screenX
        self._screenY = screenY
        self._pickOutputObject = None
        print("getPick() initial state = "+str(self._state))

        self._stateFunctions = {
            self.STATE_INITIAL:                     self.requestPick, 
            self.STATE_DONE:                        self.done}

    @overrides(AsynchTask)
    def doStep(self):
        self._stateFunctions[self._state]()
        return self._state == self.STATE_DONE

    def requestPick(self):
        print("PickAsynchTask.requestPick()")
        assert(self._state == self.STATE_INITIAL) # current state
        self._vis.setPickAttributes()
        visit.ZonePick(self._screenX,self._screenY,self._vis.getVariable())
        #print("Pick done. Data: "+str(visit.GetPickOutput()))
        self._pickOutputObject = visit.GetPickOutputObject()
        visit.SetWindowMode("navigate")
        self._state = self.STATE_DONE  # new state

    #def confirmPlot(self):
    #    # noop state (should we check something)
    #    print("PickAsynchTask.confirmPlot()")
    #    assert(self._state == self.STATE_PLOT_CONFIRM) # current state
    #    self._state = self.STATE_DONE # new state
 
    def done(self):
        print("PickAsynchTask.done()")
        assert(self._state == self.STATE_DONE) # current state - end state

    @overrides(AsynchTask)
    def getResults(self):
        if self._pickOutputObject is not None:
            return ("Pick of "+str(self._vis.getVariable())+" at point "+str(self._pickOutputObject['point'])+ " is: "+str(self._pickOutputObject[self._vis.getVariable()]))
        else:
            return ("No Data.  Click on plot for values.")

 
########################################################################
# Asynchronous Task for performing a line-out
#
# ... using Window 2 as a invisible, temporary staging area for the curve plot
# ... we'll get the data from the plot and throw it away.
#
########################################################################

class LineoutAsynchTask(AsynchTask):

    STATE_INITIAL =             "initial"
    STATE_1_CHECK_DATA =        "check data"
    STATE_2_CLEANUP =           "cleanup"
    STATE_3_CHECK_CLEANUP =     "check cleanup"
    STATE_DONE =                "done"

    def __init__(self, startPoint, endPoint, dataReadyCallback,  onErrorCallback):
        super(LineoutAsynchTask, self).__init__("Lineout", dataReadyCallback, onErrorCallback)
        assert isinstance(startPoint, tuple)
        assert len(startPoint) in [2,3]
        assert isinstance(endPoint,tuple)
        assert len(endPoint) in [2,3]

        self._startPoint = startPoint
        self._endPoint = endPoint
        
        self._lineOutData = None
        self._state = self.STATE_INITIAL
        print("lineout initial state = "+str(self._state))

        self.stateFunctions = {
            self.STATE_INITIAL:                 self.doLineout, 
            self.STATE_1_CHECK_DATA:            self.isDataReady,
            self.STATE_2_CLEANUP:               self.doCleanup,
            self.STATE_3_CHECK_CLEANUP:         self.isCleanupDone,
            self.STATE_DONE:                    self.cleanupIsDone}

    @overrides(AsynchTask)
    def doStep(self):
        self._stateFunctions[self._state]()
        return self._state == self.STATE_DONE

    @overrides(AsynchTask)
    def getResults(self):
        return None

    def doLineout(self):
        print("LineoutAsynchTask.doLineout()")
        assert(self._state == self.STATE_INITIAL) # current state
        try:
            if (2 in visit.GetGlobalAttributes().GetWindows()):
                visit.SetActiveWindow(2)
                assert visit.GetNumPlots()==0 
            print("passed assertion that Window2 doesn't exist yet or has no plots")
            print(visit.SetActiveWindow(1))
            visit.Lineout(self._startPoint, self._endPoint)
            self._state = self.STATE_1_CHECK_DATA # new state
        finally:
            visit.SetActiveWindow(1)

    def isDataReady(self):
        print("LineoutAsynchTask.isDataReady()")
        assert(self._state == self.STATE_1_CHECK_DATA) # current state

        visit.SetActiveWindow(2)
        if visit.GetNumPlots()==1:
            print("Lineout data ready\n")
            self._lineOutData = visit.GetPlotInformation()['Curve']
            print("The data from the lineout from (0,0,0) to (1,1,1) is:\n")
            print("\n")
            print(str(self._lineOutData))
            print("\n")
            if isinstance(self._lineOutData, tuple):
                self._state = self.STATE_2_CLEANUP # new state
            else:
                raise Exception("LineoutAsynchTask.isDataReady(): failed to return lineout plot data");
        else:
            pass # don't change state ... still waiting for plot

    def doCleanup(self):
        print("LineoutAsynchTask.doCleanup()")
        assert(self._state == self.STATE_2_CLEANUP) # current state
        try:
            visit.SetActiveWindow(2)
            assert visit.GetNumPlots()!=0 
            visit.ClearWindow()
            visit.DeleteAllPlots()
            self._state = self.STATE_3_CHECK_CLEANUP # new state
        finally:
            visit.SetActiveWindow(1)

    def isCleanupDone(self):
        print("LineoutAsynchTask.doCleanup()")
        assert(self._state == self.STATE_3_CHECK_CLEANUP) # current state
        try:
            visit.SetActiveWindow(2)
            if visit.GetNumPlots()==0:
                self._state = self.STATE_DONE # new state
        finally:
            visit.SetActiveWindow(1)

 