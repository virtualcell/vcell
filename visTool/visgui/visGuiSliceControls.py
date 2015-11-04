import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui

class sliceControl(QtGui.QWidget):
    
    def __init__(self, parent):
        super(sliceControl,self).__init__(parent)
        assert isinstance(parent,QtGui.QWidget)
        self.initUI()
  
    def initUI(self):
        self.setObjectName("sliceControlWidget")
        selfSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)

        gridLayout = QtGui.QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        self._enableCheckbox = QtGui.QCheckBox()
        self._enableCheckbox.setObjectName("enableCheckbox")
        self._enableCheckbox.setText("slice control")

        gridLayout.addWidget(self._enableCheckbox, 0, 0, 1, 1)

        self._slice_X_AxisRadioButton = QtGui.QRadioButton(self);
        self._slice_X_AxisRadioButton.setObjectName("xAxisRadioButton")
        self._slice_X_AxisRadioButton.setText("X axis")
        self._slice_X_AxisRadioButton.click()
        #self._slice_X_AxisRadioButton.

        gridLayout.addWidget(self._slice_X_AxisRadioButton, 1, 0, 1, 1)

        self._slice_Y_AxisRadioButton = QtGui.QRadioButton(self);
        self._slice_Y_AxisRadioButton.setObjectName("yAxisRadioButton")
        self._slice_Y_AxisRadioButton.setText("Y axis")

        gridLayout.addWidget(self._slice_Y_AxisRadioButton, 1, 1, 1, 1)

        self._slice_Z_AxisRadioButton = QtGui.QRadioButton(self)
        self._slice_Z_AxisRadioButton.setObjectName("zAxisRadioButton")
        self._slice_Z_AxisRadioButton.setText("Z axis")

        gridLayout.addWidget(self._slice_Z_AxisRadioButton, 1, 2, 1, 1)

        self._sliceButtonGroup = QtGui.QButtonGroup(self)
        self._sliceButtonGroup.setObjectName("buttonGroup")
        self._sliceButtonGroup.addButton(self._slice_X_AxisRadioButton)
        self._sliceButtonGroup.addButton(self._slice_Y_AxisRadioButton)
        self._sliceButtonGroup.addButton(self._slice_Z_AxisRadioButton)

        self._sliceSlider = QtGui.QSlider(self)
        self._sliceSlider.setObjectName("sliceSlider")
        sliderSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Expanding, QtGui.QSizePolicy.Fixed)
        sliderSizePolicy.setHorizontalStretch(0)
        sliderSizePolicy.setVerticalStretch(0)
        sliderSizePolicy.setHeightForWidth(self._sliceSlider.sizePolicy().hasHeightForWidth())
        self._sliceSlider.setSizePolicy(sliderSizePolicy)
        self._sliceSlider.setOrientation(QtCore.Qt.Horizontal)

        gridLayout.addWidget(self._sliceSlider, 2, 0, 1, 3)

        verticalSpacer1 = QtGui.QSpacerItem(20, 15, QtGui.QSizePolicy.Minimum, QtGui.QSizePolicy.Minimum);

        gridLayout.addItem(verticalSpacer1, 3, 0, 1, 1);

        self._showAsImageCheckbox = QtGui.QCheckBox(self)
        self._showAsImageCheckbox.setObjectName("showAsImageCheckbox")
        self._showAsImageCheckbox.setText("show as image")

        gridLayout.addWidget(self._showAsImageCheckbox, 4, 0, 1, 3)

        verticalSpacer2 = QtGui.QSpacerItem(20, 40, QtGui.QSizePolicy.Minimum, QtGui.QSizePolicy.Expanding);

        gridLayout.addItem(verticalSpacer2, 5, 0, 1, 1);

    def getSliceSlider(self):
        return self._sliceSlider

    def getShowAsImageCheckbox(self):
        return self._showAsImageCheckbox

    def getSlice_X_AxisRadioButton(self):
        return self._slice_X_AxisRadioButton

    def getSlice_Y_AxisRadioButton(self):
        return self._slice_Y_AxisRadioButton

    def getSlice_Z_AxisRadioButton(self):
        return self._slice_Z_AxisRadioButton

    def getEnableCheckbox(self):
        return self._enableCheckbox;

