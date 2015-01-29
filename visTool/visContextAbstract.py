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
 
    # abstract method
    def getRenderWindow(self,windowID):
        raise NotImplementedError()

    def getBareRenderWindow(self):
       raise NotImplementedError()

    def openOne(self,filename,variableName,bSameDomain):
       raise NotImplementedError()

    def getMDVariableNames(self):
       raise NotImplementedError()

    def getVariableName(self):
       raise NotImplementedError()

    #def setVariable(self,varName):
    #   raise NotImplementedError()

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
    
    def setOperatorPercent(self, percent):
       assert ((percent >=0) and (percent <=100))
       raise NotImplementedError()

    def getOperatorPercent(self):
       raise NotImplementedError()

    def setOperatorProject2d(self, bProject2d):
       assert isinstance(bProject2d, bool)
       raise NotImplementedError()

    def getOperatorProject2d(self):
       raise NotImplementedError()
        
    def setMinColormapValue(self,minValue):
       raise NotImplementedError()
        
    def setMaxColormapValue(self,maxValue):
       raise NotImplementedError()

    def lineout(self,start,end):
       raise NotImplementedError()
