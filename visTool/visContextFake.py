import sys

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

from visContextAbstract import visContextAbstract
from visDataContext import VisDataContext

class FakeRenderWindow(QtGui.QWidget):

    def __init__(self):
        QtGui.QWidget.__init__(self)
        self._bareRenderWindow = FakeBareRenderWindow()

    def setObjectName(self,name):
        pass

    def getBareRenderWindow(self):
        return self._bareRenderWindow

class FakeBareRenderWindow(QtGui.QWidget):

    def __init__(self):
        QtGui.QWidget.__init__(self)
        self._centralWidget = FakeCentralWidget()

    def centralWidget(self):
        return self._centralWidget

 
class FakeCentralWidget(QtGui.QWidget):

    def __init__(self):
        QtGui.QWidget.__init__(self)
 
    def installEventFilter(self,mouseEventFilter):
        pass


class visContextFake(visContextAbstract):
    """description of class"""
    def __init__(self):
        visContextAbstract.__init__(self)
        self._renderWindow = FakeRenderWindow()
        assert isinstance(self._renderWindow,QtCore.QObject) or self._renderWindow == None
        self._operatorEnabled = False
        assert isinstance(self._operatorEnabled,bool)
        self._operatorProject2d = False
        assert isinstance(self._operatorProject2d,bool)
        self._operatorPercent = 50.0
        assert isinstance(self._operatorPercent,float)
        self._varName = None
        assert isinstance(self._varName,str) or (self._varName == None)

    # abstract method
    def getRenderWindow(self,windowID):
        return self._renderWindow

    def getBareRenderWindow(self):
        return self._renderWindow.getBareRenderWindow()

    def openOne(self,filename,variableName,bSameDomain):
        assert(isinstance(filename,basestring))
        assert(isinstance(variableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextFake: openOne("+filename+","+variableName+","+str(bSameDomain)+")"

    def getMDVariableNames(self):
        return ["var1", "var2"]

    def getVariable(self):
        return self._varName

    #def setVariable(self,varName):
    #    self._varName = varName;
    #    print "\n\nvisContextFake.setVariableName("+varName+")"

    # abstract method
    def quit(self):
        pass

    def setOperatorEnabled(self, bEnable):
        assert isinstance(bEnable,bool)
        self._operatorEnabled = bEnable

    def getOperatorEnabled(self):
        return self._operatorEnabled

    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
       
    def getOperatorAxis(self):
        pass
    
    def setOperatorPercent(self, percent):
        assert ((percent >=0) and (percent <=100))
        self._operatorPercent = percent;

    def getOperatorPercent(self):
        return self._operatorPercent

    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)
        self._operatorProject2d = bProject2d

    def getOperatorProject2d(self):
        return self._operatorProject2d
        
    def setMinColormapValue(self,minValue):
        pass
        
    def setMaxColormapValue(self,maxValue):
        pass

    def lineout(self,start,end):
        pass
