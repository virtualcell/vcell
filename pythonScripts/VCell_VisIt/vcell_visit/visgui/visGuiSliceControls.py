from PySide6.QtWidgets import QGroupBox, QWidget, QGridLayout, QSizePolicy, \
    QRadioButton, QButtonGroup, QLabel, QSlider, QCheckBox, QSpacerItem
from PySide6.QtCore import Qt


class SliceControl(QGroupBox):
    
    def __init__(self, parent: QWidget):
        super(SliceControl,self).__init__(parent)
        self._sliceSlider = None
        self._showAsImageCheckbox = None
        self._minlabel = None
        self.slicecntnr = None
        self._sliceButtonGroup = None
        self._slice_Z_AxisRadioButton = None
        self._slice_Y_AxisRadioButton = None
        self._slice_X_AxisRadioButton = None
        assert isinstance(parent, QWidget)
        self.init_ui()
  
    CONST_SLICE_TITLE = "Slice:"

    def init_ui(self):
        self.setTitle(SliceControl.CONST_SLICE_TITLE)
        self.setCheckable(True)
        self.setChecked(False)
        self.setObjectName("sliceControlWidget")
        selfSizePolicy = QSizePolicy(QSizePolicy.Preferred, QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)

        gridLayout = QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        #self._enableCheckbox = QtGui.QCheckBox()
        #self._enableCheckbox.setObjectName("enableCheckbox")
        #self._enableCheckbox.setText("slice control")

        #gridLayout.addWidget(self._enableCheckbox, 0, 0, 1, 1)

        self._slice_X_AxisRadioButton = QRadioButton(self);
        self._slice_X_AxisRadioButton.setObjectName("xAxisRadioButton")
        self._slice_X_AxisRadioButton.setText("X axis")
        self._slice_X_AxisRadioButton.click()
        #self._slice_X_AxisRadioButton.

        gridLayout.addWidget(self._slice_X_AxisRadioButton, 1, 0, 1, 1)

        self._slice_Y_AxisRadioButton = QRadioButton(self);
        self._slice_Y_AxisRadioButton.setObjectName("yAxisRadioButton")
        self._slice_Y_AxisRadioButton.setText("Y axis")

        gridLayout.addWidget(self._slice_Y_AxisRadioButton, 1, 1, 1, 1)

        self._slice_Z_AxisRadioButton = QRadioButton(self)
        self._slice_Z_AxisRadioButton.setObjectName("zAxisRadioButton")
        self._slice_Z_AxisRadioButton.setText("Z axis")

        gridLayout.addWidget(self._slice_Z_AxisRadioButton, 1, 2, 1, 1)

        self._sliceButtonGroup = QButtonGroup(self)
        self._sliceButtonGroup.setObjectName("buttonGroup")
        self._sliceButtonGroup.addButton(self._slice_X_AxisRadioButton)
        self._sliceButtonGroup.addButton(self._slice_Y_AxisRadioButton)
        self._sliceButtonGroup.addButton(self._slice_Z_AxisRadioButton)

        self.slicecntnr = QWidget(self)
        cntnrSizePolicy = QSizePolicy(QSizePolicy.Expanding, QSizePolicy.Fixed,QSizePolicy.DefaultType)
        self.slicecntnr.setSizePolicy(cntnrSizePolicy)
        cntnrgridLayout = QGridLayout(self.slicecntnr)
        #cntnrgridLayout.setAlignment(self.slicecntnr,QtCore.Qt.AlignLeft)

        self._minlabel = QLabel(self)
        self._minlabel.setText("0%")
        #self._minlabel.setAlignment(QtCore.Qt.AlignLeft)
        #cntnrgridLayout.setAlignment(self._minlabel,QtCore.Qt.AlignLeft)
        labelSizePolicy = QSizePolicy(QSizePolicy.Minimum, QSizePolicy.Fixed, QSizePolicy.Label)
        self._minlabel.setSizePolicy(labelSizePolicy)
        cntnrgridLayout.addWidget(self._minlabel, 0, 0, 1, 1)

        self._maxlabel = QLabel(self)
        self._maxlabel.setText("100%")
        labelSizePolicy = QSizePolicy(QSizePolicy.Minimum, QSizePolicy.Fixed,QSizePolicy.Label)
        self._maxlabel.setSizePolicy(labelSizePolicy)
        cntnrgridLayout.addWidget(self._maxlabel, 0, 2, 1, 1)

        self._sliceSlider = QSlider(self)
        self._sliceSlider.setObjectName("sliceSlider")
        sliderSizePolicy = QSizePolicy(QSizePolicy.Expanding, QSizePolicy.Fixed,QSizePolicy.Slider)
        sliderSizePolicy.setHorizontalStretch(100)
        sliderSizePolicy.setVerticalStretch(0)
        sliderSizePolicy.setHeightForWidth(self._sliceSlider.sizePolicy().hasHeightForWidth())
        self._sliceSlider.setSizePolicy(sliderSizePolicy)
        self._sliceSlider.setOrientation(Qt.Horizontal)

        cntnrgridLayout.addWidget(self._sliceSlider, 0, 1, 1, 1)

        gridLayout.addWidget(self.slicecntnr, 2, 0, 1, 3)

        verticalSpacer1 = QSpacerItem(20, 15, QSizePolicy.Minimum, QSizePolicy.Minimum);

        gridLayout.addItem(verticalSpacer1, 3, 0, 1, 1);

        self._showAsImageCheckbox = QCheckBox(self)
        self._showAsImageCheckbox.setObjectName("showAsImageCheckbox")
        self._showAsImageCheckbox.setText("show as image")

        gridLayout.addWidget(self._showAsImageCheckbox, 4, 0, 1, 3)

        verticalSpacer2 = QSpacerItem(20, 40, QSizePolicy.Minimum, QSizePolicy.Expanding);

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

    #def getEnableCheckbox(self):
    #    return self._enableCheckbox;

