import sys

from visgui import visQt
QtCore = visQt.QtCore

#
# a decorator which provides @overrides(interface_name) for overridden methods
# similar to #Overrides in java
#
def overrides(interface_class):
    def overrider(method):
        assert(method.__name__ in dir(interface_class))
        return method
    return overrider


#
# abstract class for visContext
#
class visContextAbstract(object):
    """description of class"""
    def __init__(self):
        self._renderWindow = None
        assert isinstance(self._renderWindow,QtCore.QObject) or self._renderWindow == None
 
    # abstract method
    def getRenderWindow(self,windowID):
        raise NotImplementedError()

    def installEventFilter(self, eventFilter):
       raise NotImplementedError()

    def openOne(self,filename,variableName,bSameDomain,onSuccessCallback,onErrorCallback):
        assert(isinstance(filename,basestring))
        assert(isinstance(variableName,basestring))
        assert(isinstance(bSameDomain,bool))
        raise NotImplementedError()

    def getMDVariableNames(self):
       raise NotImplementedError()

    def getVariableName(self):
       raise NotImplementedError()

    # abstract method
    def quit(self):
       raise NotImplementedError()

    def setOperatorEnabled(self, bEnable):
       assert isinstance(bEnable,bool)
       raise NotImplementedError()

    def getOperatorEnabled(self):
       raise NotImplementedError()

    def setOperatorAxis(self, axis):
       assert (axis in (0,1,2))
       raise NotImplementedError()
       
    def getOperatorAxis(self):
       raise NotImplementedError()
    
    def setOperatorPercent(self, percent, onSuccessCallback, onErrorCallback):
       assert ((percent >=0) and (percent <=100))
       raise NotImplementedError()

    def getOperatorPercent(self):
       raise NotImplementedError()

    def getPick(self, screenX, screenY, dataReadyCallback, onErrorCallback):
        raise NotImplementedError()

    def getPickMode(self):
        raise NotImplementedError()

    def setOperatorProject2d(self, bProject2d):
       assert isinstance(bProject2d, bool)
       raise NotImplementedError()

    def getOperatorProject2d(self):
       raise NotImplementedError()
        
    def setMinColormapValue(self,minValue):
        assert isinstance(minValue,int) or isinstance(minValue,float)
        raise NotImplementedError()
        
    def setMaxColormapValue(self,maxValue):
        assert isinstance(maxValue,int) or isinstance(maxValue,float)
        raise NotImplementedError()

    def doLineout(self, startPoint, endPoint, dataReadyCallback = None, onErrorCallback = None): # points must be tuples
        assert isinstance(startPoint, tuple)
        assert isinstance(endPoint, tuple)
        raise NotImplementedError()
