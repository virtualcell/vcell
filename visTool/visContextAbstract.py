import sys

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui


from visDataContext import VisDataContext

class visContextAbstract(object):
    """description of class"""
    def __init__(self):
        self._renderWindow = None
        assert isinstance(self._renderWindow,QtCore.QObject) or self._renderWindow == None
        self._visDataContext = VisDataContext()

    # No setter.  visDataContext is meant to be a singleton (per visContext).
    # visDataContext lives in visContextAbstract because it is in no way VisIt dependent.
    def getVisDataContext(self):
        return self._visDataContext

    # abstract method
    def getRenderWindow(self,windowID):
        pass

    def open(self,filename):
        pass

    def getVariableNames(self):
        return None

    # abstract method
    def quit(self):
        pass

    def setTimeIndex(self, timeIndex):
        pass

    def getNumberOfTimePoints(self):
        pass

    def getTimes(self):
        pass

    def setOperatorEnabled(self, bEnable):
        assert isinstance(bEnable,bool)

    def getOperatorEnabled(self):
        pass

    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
       
    def getOperatorAxis(self):
        pass
    
    def setOperatorPercent(self, percent):
        assert ((percent >=0) and (percent <=100))

    def getOperatorPercent(self):
        pass

    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)

    def getOperatorProject2d(self):
        pass
        
    def setMinColormapValue(self,minValue):
        pass
        
    def setMaxColormapValue(self,maxValue):
        pass

    def lineout(self,start,end):
        pass
