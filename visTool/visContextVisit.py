import visContextAbstract
from visContextVisitImpl import VisContextVisitImpl
import visClipOperatorContext
from visClipOperatorContext import VisClipOperatorContext

class visContextVisit(visContextAbstract.visContextAbstract):



    def __init__(self):
        self._visContextImpl = VisContextVisitImpl()
        self._visClipOperatorContext = visClipOperatorContext.VisClipOperatorContext()
    
    def getRenderWindow(self, parent):
        return self._visContextImpl.getRenderWindow(parent)

    def getBareRenderWindow(self):
        return self._visContextImpl.getBareRenderWindow()

    def _openOne(self, filename, vtuVariableName, bSameDomain):
        self._visContextImpl._openOne(filename, vtuVariableName, bSameDomain)


    def getMDVariableNames(self):
        return self._visContextImpl.getMDVariableNames()

    def getVariable(self):
        return self._visContextImpl.getVariable()
 
    def getPickMode(self):
        return self._visContextImpl.getPickMode()

    def setPickMode(self, newMode):
        self._visContextImpl.setPickMode(newMode)

    def resetPickLetter(self):
        self._visContextImpl.resetPickLetter()

    def _updateDisplay(self):
        self._visContextImpl._updateDisplay()

    def setOperatorEnabled(self, bEnable):
        # self._visContextImpl.setOperatorEnabled(bEnable)
        self._visClipOperatorContext.setCurrentOperatorEnabled(bEnable)
        if (bEnable != self._visContextImpl.getOperatorEnabled()):
            self._visContextImpl.setOperatorEnabled(bEnable)
            self._visContextImpl.updatePlot()

    def getOperatorEnabled(self):
        return self._visContextImpl.getOperatorEnabled()

    def setOperatorAxis(self, axis):
        # self._visContextImpl.setOperatorAxis(axis)
        self._visClipOperatorContext.setCurrentOperatorAxis(axis)
        if (axis != self._visContextImpl.getOperatorAxis()):
            self._visContextImpl.setOperatorAxis(axis)
            self._visContextImpl.updatePlot()

    def getOperatorAxis(self):
        return self._visContextImpl.getOperatorAxis()

    def setOperatorPercent(self, percent):
        # self._visContextImpl.setOperatorPercent(percent)
        self._visClipOperatorContext.setCurrentOperatorPercent(percent)
        if (percent != self._visContextImpl.getOperatorPercent()):
            self._visContextImpl.setOperatorPercent(percent)
            self._visContextImpl.updatePlot()

    def getOperatorPercent(self):
        return self._visContextImpl.getOperatorPercent()

    def setOperatorProject2d(self, bProject2d):
        # self._visContextImpl.setOperatorProject2d(bProject2d)
        self._visClipOperatorContext.setCurrentProject2d(bProject2d)
        if (bProject2d != self._visContextImpl.getOperatorProject2d()):
            self._visContextImpl.setOperatorProject2d(bProject2d)
            self._visContextImpl.updatePlot()

    def getOperatorProject2d(self):
        return self._visContextImpl.getOperatorProject2d()

    def quit(self):
        self._visContextImpl.quit()
            
    def clearPicks(self):
        self._visContextImpl.clearPicks()

    def _getSliceAttributes(self):
        return self._visContextImpl._getSliceAttributes()

    def _getClipAttributes(self):
        return self._visContextImpl._getClipAttributes()

    def setMaxColormapValue(self, max):
        self._visContextImpl.setMaxColormapValue(max)

    def setMinColormapValue(self, min):
        self._visContextImpl.setMinColormapValue(min)

    def doLineout(self, startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None): # points must be tuples
        self._visContextImpl.doLineout(startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None)

    def openOne(self, filename, vtuVariableName, bSameDomain):
        self._visContextImpl.openOne(filename, vtuVariableName, bSameDomain)

    #def doTimeSeries(self, point, dataReadyCallback = None, onErrorCallback = None):
        #self._visContextImpl.doTimeSeries(self, point, dataReadyCallback = None, onErrorCallback = None)

    def setActiveWindow(self, windowIndex): 
        self._visContextImpl.setActiveWindow(windowIndex)

    def _getDefaultPseudoColorAttributes(self):
        return self._visContextImpl._getDefaultPseudoColorAttributes()

    def _getMetaData(self):
        return self._visContextImpl._getMetaData()

    def _getMinSpatialExtents(self):
        return self._visContextImpl._getMinSpatialExtents()

    def _getMaxSpatialExtents(self):
        return self._visContextImpl._getMaxSpatialExtents()

    def setPickAttributes(self):   
        self._visContextImpl.setPickAttributes()

    def getPick(self, screenX, screenY):
        return self._visContextImpl.getPick(screenX, screenY)

    def getOtherWindow(self, windowName):
        return self._visContextImpl.getOtherWindow(windowName)

    def getOtherWindowNames(self):
        self._visContextImpl.getOtherWindowNames()