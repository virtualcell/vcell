import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

class queryControl(QtGui.QWidget):
    
    def __init__(self, parent):
        super(queryControl,self).__init__(parent)
        assert isinstance(parent,QtGui.QWidget)
        self.initUI()
  
    def initUI(self):
        self.setObjectName("queryControlWidget")
        selfSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)

        gridLayout = QtGui.QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        self._doTimeSeriesButton = QtGui.QPushButton()
        self._doTimeSeriesButton.setObjectName("doTimeSeries")
        self._doTimeSeriesButton.setText("do time series")
        self._doTimeSeriesButton.setDisabled(True)
        self._doTimeSeriesButton.setVisible(False)

        gridLayout.addWidget(self._doTimeSeriesButton, 0, 0, 1, 1)

        self._doLineoutButton = QtGui.QPushButton()
        self._doLineoutButton.setObjectName("doLineout")
        self._doLineoutButton.setText("do lineout")
        self._doLineoutButton.setDisabled(True)
        self._doLineoutButton.setVisible(False)

        gridLayout.addWidget(self._doLineoutButton, 0, 1, 1, 1)

        self._setPickModeCheckBox = QtGui.QCheckBox()
        self._setPickModeCheckBox.setObjectName("setPickMode")
        self._setPickModeCheckBox.setText("Pick Mode")
        self._setPickModeCheckBox.setVisible(False)

        gridLayout.addWidget(self._setPickModeCheckBox, 1, 0, 1, 1)

        self._setMouseoverPickModeCheckBox = QtGui.QCheckBox()
        self._setMouseoverPickModeCheckBox.setObjectName("setMouseoverPickMode")
        self._setMouseoverPickModeCheckBox.setText("Mouseover Pick Mode")
        self._setMouseoverPickModeCheckBox.setDisabled(True)
        self._setMouseoverPickModeCheckBox.setVisible(False)

        gridLayout.addWidget(self._setMouseoverPickModeCheckBox, 1, 1, 1, 1)

        self._clearPicksButton = QtGui.QPushButton()
        self._clearPicksButton.setObjectName("clearPicks")
        self._clearPicksButton.setText("Clear picks.")
        self._clearPicksButton.setVisible(False)
        

        gridLayout.addWidget(self._clearPicksButton, 2, 0, 1, 1)

    def getDoTimeSeriesButton(self):
        return self._doTimeSeriesButton

    def getDoLineoutButton(self):
        return self._doLineoutButton

    def getSetMouseoverPickModeCheckBox(self):
        return self._setMouseoverPickModeCheckBox

    def getSetPickModeCheckBox(self):
        return self._setPickModeCheckBox

    def getClearPicksButton(self):
        return self._clearPicksButton