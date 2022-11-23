import visContext
from visContext.visContextAbstract import visContextAbstract

def overrides(interface_class):
    def overrider(method):
        assert(method.__name__ in dir(interface_class))
        return method
    return overrider


#from visContext.visContextAbstract import overrides
from visContextVisitImpl import VisContextVisitImpl
from visClipOperatorContext import VisClipOperatorContext

class visContextVisit(visContextAbstract):

    def __init__(self):
        self._visContextImpl = VisContextVisitImpl()
        self._visClipOperatorContext = VisClipOperatorContext()
    
    @overrides(visContextAbstract)
    def getRenderWindow(self, parent):
        return self._visContextImpl.getRenderWindow(parent)

    @overrides(visContextAbstract)
    def installEventFilter(self, eventFilter):
        return self._visContextImpl.getBareRenderWindow().centralWidget().installEventFilter(eventFilter)

    @overrides(visContextAbstract)
    def getMDVariableNames(self):
        return self._visContextImpl.getMDVariableNames()

    @overrides(visContextAbstract)
    def setOperatorEnabled(self, bEnable):
        # self._visContextImpl.setOperatorEnabled(bEnable)
        self._visClipOperatorContext.setCurrentOperatorEnabled(bEnable)
        if (bEnable != self._visContextImpl.getOperatorEnabled()):
            self._visContextImpl.setOperatorEnabled(bEnable)
            self._visContextImpl.updatePlot()

    @overrides(visContextAbstract)
    def getOperatorEnabled(self):
        return self._visContextImpl.getOperatorEnabled()

    @overrides(visContextAbstract)
    def setOperatorAxis(self, axis):
        # self._visContextImpl.setOperatorAxis(axis)
        self._visClipOperatorContext.setCurrentOperatorAxis(axis)
        if (axis != self._visContextImpl.getOperatorAxis()):
            self._visContextImpl.setOperatorAxis(axis)
            self._visContextImpl.updatePlot()

    @overrides(visContextAbstract)
    def getOperatorAxis(self):
        return self._visContextImpl.getOperatorAxis()

    @overrides(visContextAbstract)
    def setOperatorPercent(self, percent, onSuccessCallback, onErrorCallback):
        assert ((percent >=0) and (percent <=100))
        self._visClipOperatorContext.setCurrentOperatorPercent(percent)
        if (percent != self._visContextImpl.getOperatorPercent()):
            self._visContextImpl.setOperatorPercent(self._visClipOperatorContext, onSuccessCallback, onErrorCallback)

    #@overrides(visContextAbstract)
    #def setOperatorPercent(self, percent):
    #    # self._visContextImpl.setOperatorPercent(percent)
    #    self._visClipOperatorContext.setCurrentOperatorPercent(percent)
    #    if (percent != self._visContextImpl.getOperatorPercent()):
    #        self._visContextImpl.setOperatorPercent(percent)
    #        self._visContextImpl.updatePlot()

    @overrides(visContextAbstract)
    def getOperatorPercent(self):
        return self._visContextImpl.getOperatorPercent()

    @overrides(visContextAbstract)
    def setOperatorProject2d(self, bProject2d):
        # self._visContextImpl.setOperatorProject2d(bProject2d)
        self._visClipOperatorContext.setCurrentProject2d(bProject2d)
        if (bProject2d != self._visContextImpl.getOperatorProject2d()):
            self._visContextImpl.setOperatorProject2d(bProject2d)
            self._visContextImpl.updatePlot()

    @overrides(visContextAbstract)
    def getOperatorProject2d(self):
        return self._visContextImpl.getOperatorProject2d()

    @overrides(visContextAbstract)
    def quit(self):
        self._visContextImpl.quit()
            
    @overrides(visContextAbstract)
    def setMaxColormapValue(self, max):
        self._visContextImpl.setMaxColormapValue(max)

    @overrides(visContextAbstract)
    def setMinColormapValue(self, min):
        self._visContextImpl.setMinColormapValue(min)

    @overrides(visContextAbstract)
    def doLineout(self, startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None): # points must be tuples
        self._visContextImpl.doLineout(startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None)

    @overrides(visContextAbstract)
    def openOne(self, filename, vtuVariableName, bSameDomain, onSuccessCallback, onErrorCallback):
        self._visContextImpl.openOne(filename, vtuVariableName, bSameDomain, onSuccessCallback,onErrorCallback)

    @overrides(visContextAbstract)
    def getPick(self, screenX, screenY, dataReadyCallback = None, onErrorCallback = None):
        return self._visContextImpl.getPick(screenX, screenY, dataReadyCallback, onErrorCallback)

    @overrides(visContextAbstract)
    def getPickMode(self):
        return self._visContextImpl.getPickMode()

    @overrides(visContextAbstract)
    def getVariableName(self):
        return self._visContextImpl.getVariable();
