import sys

import visgui
from visgui import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

import visContext
from visContext.visContextAbstract import visContextAbstract
from visContext.visContextAbstract import overrides

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
    @overrides(visContextAbstract)
    def getRenderWindow(self,windowID):
        return self._renderWindow

    @overrides(visContextAbstract)
    def installEventFilter(self, eventFilter):
        self._renderWindow.getBareRenderWindow().centralWidget().installEventFilter(eventFilter)


    @overrides(visContextAbstract)
    def openOne(self,filename,variableName,bSameDomain,onSuccessCallback,onErrorCallback):
        assert(isinstance(filename,basestring))
        assert(isinstance(variableName,basestring))
        assert(isinstance(bSameDomain,bool))
        print "\n\nvisContextFake: openOne("+filename+","+variableName+","+str(bSameDomain)+")"
        onSuccessCallback(None)

    @overrides(visContextAbstract)
    def getMDVariableNames(self):
        return ["var1", "var2"]

    @overrides(visContextAbstract)
    def quit(self):
        pass

    @overrides(visContextAbstract)
    def setOperatorEnabled(self, bEnable):
        assert isinstance(bEnable,bool)
        self._operatorEnabled = bEnable

    @overrides(visContextAbstract)
    def getOperatorEnabled(self):
        return self._operatorEnabled

    @overrides(visContextAbstract)
    def setOperatorAxis(self, axis):
        assert (axis in (0,1,2))
       
    @overrides(visContextAbstract)
    def getOperatorAxis(self):
        pass
    
    @overrides(visContextAbstract)
    def setOperatorPercent(self, percent):
        assert ((percent >=0) and (percent <=100))
        self._operatorPercent = percent;

    @overrides(visContextAbstract)
    def getOperatorPercent(self):
        return self._operatorPercent

    @overrides(visContextAbstract)
    def setOperatorProject2d(self, bProject2d):
        assert isinstance(bProject2d, bool)
        self._operatorProject2d = bProject2d

    @overrides(visContextAbstract)
    def getOperatorProject2d(self):
        return self._operatorProject2d
        
    @overrides(visContextAbstract)
    def setMinColormapValue(self,minValue):
        pass
        
    @overrides(visContextAbstract)
    def setMaxColormapValue(self,maxValue):
        pass

    #@overrides(visContextAbstract)
    #def lineout(self,start,end):
    #    pass
