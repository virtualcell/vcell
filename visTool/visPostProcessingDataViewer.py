import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

class postProcessingDataViewer(QtGui.QWidget):
    
    def __init__(self, parent):
        super(queryControl,self).__init__(parent)
        assert isinstance(parent,QtGui.QWidget)
        self.initUI()
  
    def initUI(self):
        self.setObjectName("postProcessingDataViewerWidget")
        selfSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)

        gridLayout = QtGui.QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        self._variableLost = QtGui.QListWidget()
