import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui
Qt = visQt.QtCore.Qt

import zipfile, StringIO, glob, urllib2

import visContextAbstract
import visGuiSliceControls
import visGuiQueryControls
import visDataSetChooserDialog
import vcellProxy
import pyvcell
import visDataContext
import visClipOperatorContext



# adapted from visitusers.org PySide_Recipes page.
class MouseEventFilter(QtCore.QObject):      
    pressed  = QtCore.Signal(QtCore.QPoint,QtCore.QSize)
    moved    = QtCore.Signal(QtCore.QPoint,QtCore.QSize)
    released = QtCore.Signal(QtCore.QPoint,QtCore.QSize)
 
    def __init__(self, visContext):
        super(MouseEventFilter,self).__init__()
        self.press = 0
        self._vis = visContext
 
    def hit(self):
        self.press = 1
 
    def eventFilter(self, obj, event):

        #print ("pickmode = "+str(self._vis.getPickMode()))
        if (self._vis.getPickMode() == 1) and (self._vis.getVariable() is not None):
            if event.type() == QtCore.QEvent.MouseButtonPress:
                if event.button() == Qt.LeftButton:
                    #print("emitting pressed.")
                    self.pressed.emit(event.pos(), obj.size())
                    if self.press:
                        return 1
            elif event.type() == QtCore.QEvent.MouseMove:
                if self.press:
                    self.moved.emit(event.pos(), obj.size())
                    return 1
            elif event.type() == QtCore.QEvent.MouseButtonRelease:
                if self.press:
                    self.released.emit(event.pos(), obj.size())
                    self.press = 0
                    return 1
        else:
            event.ignore()
        return super(MouseEventFilter,self).eventFilter(obj, event)


#class HidePickWindow(QtCore.QObject):
    
#    def eventFilter(self, obj, evt):
#        if QtCore.QEvent.Show:
#            print "Ignoring show pick window event"
#            evt.ignore()
#            return True 
#        #all others return false since we won't be handling those events
#        return False



class VCellPysideApp(QtGui.QMainWindow):

    def __init__(self, vis):
        super(VCellPysideApp,self).__init__()
        self._vis = vis
        assert isinstance(self._vis,visContextAbstract.visContextAbstract)
        self._visDataContext = visDataContext.VisDataContext()
        self.resourcedir = ""
        self._selectedVariable = None
        self.__parse_command_line()
        self.initUI()
  
    def __parse_command_line(self):
        i = 0
        while i < len(sys.argv):
            if sys.argv[i] == "-resourcedir":
                self.resourcedir = sys.argv[i+1]
                i = i + 1
            i = i + 1
 
    def __resource(self, filename):
        if self.resourcedir != "":
            return os.path.join(self.resourcedir, filename)
        return filename
 
    def initUI(self):
        self.setObjectName("QMainWindow")
        self.resize(800, 600);
        centralwidget = QtGui.QWidget(self);
        centralwidget.setObjectName("centralwidget")
        gridLayout = QtGui.QGridLayout(centralwidget);
        gridLayout.setObjectName("gridLayout")
        tabWidget = QtGui.QTabWidget(centralwidget);
        tabWidget.setObjectName("tabWidget")
        sliceTab = QtGui.QWidget();
        sliceTab.setObjectName("sliceTab")
        horizontalLayout = QtGui.QHBoxLayout(sliceTab);
        horizontalLayout.setObjectName("horizontalLayout")
        tabWidget.addTab(sliceTab, "");
        volumeTab = QtGui.QWidget();
        volumeTab.setObjectName("volumeTab")
        #tabWidget.addTab(volumeTab, "");

        renderWindow = self._vis.getRenderWindow(sliceTab)
        renderWindow.setObjectName("renderWindow")

        sliceTab.layout().addWidget(renderWindow);

        #sliceTab.addWidget(renderWindow)

        gridLayout.addWidget(tabWidget, 0, 1, 1, 1);

        frame = QtGui.QFrame(centralwidget);
        frame.setObjectName("frame")
        frame.setFrameShape(QtGui.QFrame.StyledPanel)
        frame.setFrameShadow(QtGui.QFrame.Raised)
        gridLayout_3 = QtGui.QGridLayout(frame)
        gridLayout_3.setObjectName("gridLayout_3")
        variableGroup = QtGui.QGroupBox(frame)
        variableGroup.setObjectName("variableGroup")
        sizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Expanding)
        sizePolicy.setHorizontalStretch(0);
        sizePolicy.setVerticalStretch(0);
        sizePolicy.setHeightForWidth(variableGroup.sizePolicy().hasHeightForWidth());
        variableGroup.setSizePolicy(sizePolicy);
        gridLayout_4 = QtGui.QGridLayout(variableGroup);
        gridLayout_4.setObjectName("gridLayout_4")
        self._variableListWidget = QtGui.QListWidget(variableGroup);
        self._variableListWidget.setObjectName("variableList")
        sizePolicy1 = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Preferred)
        sizePolicy1.setHorizontalStretch(0)
        sizePolicy1.setVerticalStretch(0)
        sizePolicy1.setHeightForWidth(self._variableListWidget.sizePolicy().hasHeightForWidth())
        self._variableListWidget.setSizePolicy(sizePolicy1)

        gridLayout_4.addWidget(self._variableListWidget, 0, 0, 1, 1)


        gridLayout_3.addWidget(variableGroup, 1, 0, 1, 1)

        self._sliceControl = visGuiSliceControls.sliceControl(frame)

        gridLayout_3.addWidget(self._sliceControl, 2, 0, 1, 1)

        self._queryControl = visGuiQueryControls.queryControl(frame)
        gridLayout_3.addWidget(self._queryControl, 3, 0, 1 , 1)


        timeGroup = QtGui.QGroupBox(frame)
        timeGroup.setObjectName("timeGroup")
        sizePolicyTimegroup = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        sizePolicyTimegroup.setHorizontalStretch(0)
        sizePolicyTimegroup.setVerticalStretch(0)
        sizePolicyTimegroup.setHeightForWidth(timeGroup.sizePolicy().hasHeightForWidth())
        timeGroup.setSizePolicy(sizePolicyTimegroup)
        boxLayout_time = QtGui.QVBoxLayout(timeGroup)
        boxLayout_time.setObjectName("boxLayout_time")
        self._timeSlider = QtGui.QSlider(timeGroup)
        self._timeSlider.setObjectName("timeSlider")
        self._timeSlider.setTickInterval(1)
        self._timeSlider.setPageStep(1)
        sizePolicyTimeslider = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Fixed)
        sizePolicyTimeslider.setHorizontalStretch(0)
        sizePolicyTimeslider.setVerticalStretch(0)
        sizePolicyTimeslider.setHeightForWidth(self._timeSlider.sizePolicy().hasHeightForWidth())
        self._timeSlider.setSizePolicy(sizePolicyTimeslider)
        self._timeSlider.setOrientation(QtCore.Qt.Horizontal)

        self._timeLabel = QtGui.QLabel(timeGroup)
        self._timeSlider.setObjectName("timeLabel")
        self._timeLabel.setText("<time>")
        sizePolicyTimelabel = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Fixed)
        sizePolicyTimelabel.setHorizontalStretch(0)
        sizePolicyTimelabel.setVerticalStretch(0)
        sizePolicyTimelabel.setHeightForWidth(self._timeLabel.sizePolicy().hasHeightForWidth())

        boxLayout_time.addWidget(self._timeLabel)
        boxLayout_time.addWidget(self._timeSlider)

        gridLayout_3.addWidget(timeGroup, 0, 0, 1, 1)

        gridLayout.addWidget(frame, 0, 0, 1, 1)

        self._showPostProcessingDataButton = QtGui.QPushButton(frame)
        self._showPostProcessingDataButton.setObjectName("showPostProcessingDataButton")
        self._showPostProcessingDataButton.setText("Show Post Processing Statistics")
        self._showPostProcessingDataButton.setVisible(True)
        gridLayout_3.addWidget(self._showPostProcessingDataButton, 4, 0, 1 , 1)

        self.setCentralWidget(centralwidget)

        menuBar = QtGui.QMenuBar();
        menuBar.setObjectName("menuBar")
        menuBar.setGeometry(0, 0, 1030, 20);
        menuFile = QtGui.QMenu(menuBar)
        menuFile.setObjectName("menuFile")
        self.setMenuBar(menuBar)
        self._statusBar = QtGui.QStatusBar(self)
        self._statusBar.setObjectName("statusBar")
        self.setStatusBar(self._statusBar)

        actionOpen = QtGui.QAction(self);
        actionOpen.setObjectName("actionOpen")
        actionOpenSimFromOpenVCellModels = QtGui.QAction(self)
        actionOpenSimFromOpenVCellModels.setObjectName("actionOpenSimFromOpenVCellModels")
        actionExit = QtGui.QAction(self);
        actionExit.setObjectName("actionExit")
        menuBar.addAction(menuFile.menuAction())
        
        #menuFile.addAction(actionOpen)
        menuFile.addAction(actionOpenSimFromOpenVCellModels)
        menuFile.addSeparator()
        menuFile.addAction(actionExit)

        self.setWindowTitle("VCell VisIt viewer")
        #actionOpen.setText("Open")
        actionOpenSimFromOpenVCellModels.setText("Load Sim from open VCell Models")
        actionExit.setText("Exit")
        #tabWidget.setTabText(tabWidget.indexOf(sliceTab), "Slice")
        tabWidget.setTabText(tabWidget.indexOf(sliceTab), "Plot")
        #tabWidget.setTabText(tabWidget.indexOf(volumeTab),"Volume")
        variableGroup.setTitle("Variables")
        timeGroup.setTitle("Time")
        menuFile.setTitle("File")


        


        #
        # set up all connections
        #
        actionOpen.setStatusTip("Open file")
        actionOpen.triggered.connect(self._showLoadFile)
        actionOpenSimFromOpenVCellModels.setStatusTip("Open Sim from VCell open models")
        actionOpenSimFromOpenVCellModels.triggered.connect(self._showOpenSimFromVCellOpenModels)
        actionExit.setStatusTip("Exit viewer")
        actionExit.triggered.connect(self._exitApplication)
        self._variableListWidget.clicked.connect(self._onSelectedVariableChanged)
        self._timeSlider.sliderReleased.connect(self._onTimeSliderChanged)

        self._sliceControl.getEnableCheckbox().setChecked(self._vis.getOperatorEnabled())
        self._sliceControl.getSliceSlider().setValue(self._vis.getOperatorPercent())
        self._sliceControl.getShowAsImageCheckbox().setChecked(self._vis.getOperatorProject2d())
        self._sliceControl.getSlice_X_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==0)
        self._sliceControl.getSlice_Y_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==1)
        self._sliceControl.getSlice_Z_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==2)

        self._sliceControl.getEnableCheckbox().clicked.connect(self._onSliceEnableCheckboxPressed)
        self._sliceControl.getSliceSlider().valueChanged.connect(self._onSliceSliderChanged)
        self._sliceControl.getShowAsImageCheckbox().clicked.connect(self._onSliceAsImageCheckboxPressed)
        self._sliceControl.getSlice_X_AxisRadioButton().clicked.connect(self._onSliceAxisChange)
        self._sliceControl.getSlice_Y_AxisRadioButton().clicked.connect(self._onSliceAxisChange)
        self._sliceControl.getSlice_Z_AxisRadioButton().clicked.connect(self._onSliceAxisChange)

        self._queryControl.getDoTimeSeriesButton().clicked.connect(self._onDoTimeSeriesButtonPressed)
        self._queryControl.getDoLineoutButton().clicked.connect(self._onDoLineoutButtonPressed)

        self._queryControl.getSetPickModeCheckBox().stateChanged.connect(self._onSetPickModeCheckBoxStateChanged)
        self._queryControl.getSetMouseoverPickModeCheckBox().stateChanged.connect(self._onSetMouseoverPickModeCheckBoxStateChanged)
        self._queryControl.getClearPicksButton().clicked.connect(self._onClearPicksButtonPressed)

        self._showPostProcessingDataButton.clicked.connect(self._onShowPostProcessingDataButtonPressed)

        self._mouseEventFilter = MouseEventFilter(self._vis)
        self._mouseEventFilter.pressed.connect(self._onMouseButtonPressedForPick)
        print("About to install mouse filter")
        
        self._vis.getBareRenderWindow().centralWidget().installEventFilter(self._mouseEventFilter)
        ##renderWindow.installEventFilter(self._mouseEventFilter)
        #print(str(dir(self._mouseEventFilter)))
        #print("Other window names = ")
        #print(str(self._vis.getOtherWindowNames()))
        #pickWindow = self._vis.getOtherWindow("Pick")
        #print("Pick window class name supposedly is:")
        #print(str(pickWindow.__class__.__name__))
        #hpw = HidePickWindow()
        #print("dir(hpw))=")
        #print(str(dir(hpw)))
        #pickWindow.installEventFilter(hpw)
        


 
    def _onMouseButtonPressedForPick(self,pos, size):
        #print("Mouse button pressed.  pos = "+str(pos)+"   size="+str(size))
        self._vis.clearPicks()
        self._showStatusMessage(self._vis.getPick(pos.x(),size.height()-pos.y()))   # note screen to window coordinate system translation in the Y (screen) axis


    def _onClearPicksButtonPressed(self):
        self._vis.clearPicks()
        self._vis.resetPickLetter()

    def _showStatusMessage(self, message):
        self._getStatusBar().showMessage(str(message))
    
    def _onShowPostProcessingDataButtonPressed(self):
        print("in _onShowPostProcessingDataButtonPressed")
        sim = self._visDataContext.getCurrentDataSet()
        if (sim == None):
            return
        print(str(sim))

        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            vcellProxy2.open()
            vcellProxy2.getClient().displayPostProcessingDataInVCell(sim)
        except Exception as exc:
            print(exc.message)
            msgBox = QMessageBox()
            msgBox.setText("Exception occurred: "+exc.message)
            msgBox.exec_()
            return
        finally:
            vcellProxy2.close()

    def _showLoadFile(self):
        print('showLoadFile()')
        if visQt.isPyside():
            fnames,pattern = QtGui.QFileDialog.getOpenFileNames(self, 'Open file', '/.', "VTK (*.vtu *.vtk);;all files (*.*)")
        elif visQt.isPyQt4():
            fnames = QtGui.QFileDialog.getOpenFileName(self, 'Open file', '/.', "VTK (*.vtu *.vtk);;all files (*.*)")

 #       fname = "C:\Developer\eclipse\workspace\pyVCell\PysideVCell\data\5323530454\SimID_533617164_0__0_.feature_EC_*.vtk database"
        if (fnames == None or len(fnames)==0):
            return

        self._vis.open(fnames)

        varNames = self._vis.getVariableNames()
        assert isinstance(self._variableListWidget,QtGui.QListWidget)
        self._variableListWidget.clear()
        self._variableListWidget.addItems(varNames)
        if visQt.isPyside():
            self._variableListWidget.setCurrentItem(QtGui.QListWidgetItem(str(0)))
        elif visQt.isPyQt4:
            self._variableListWidget.setItemSelected(QtGui.QListWidgetItem(str(0)),True)

        times = self._vis.getTimes()
        if times == None or len(times)==0:
            self._timeSlider.setMinimum(0)
            self._timeSlider.setMaximum(0)
            self._timeLabel.setText("0.0")
        else:
            self._timeSlider.setMinimum(0)
            self._timeSlider.setMaximum(len(times)-1)
            self._timeLabel.setText("0.0")

    def _showOpenSimFromVCellOpenModels(self):
        try:
            dialog = visDataSetChooserDialog.DataSetChooserDialog(self)
            dialog.simulationSelected.connect(self._onSimulationSelected)
            dialog.initUI(self._vis)
        except Exception as simFindException:
            print("Error or no datasets found")
            print(simFindException)
            return
        dialog.show()


    
    def _onSimulationSelected(self, dialog):
        sim = dialog.getSelectedSimulation()
#        domain = dialog.getSelectedDomain()
        dialog.close()
        print(sim)

        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            vcellProxy2.open()
            variables = vcellProxy2.getClient().getVariableList(sim)
            all(isinstance(n,pyvcell.ttypes.VariableInfo) for n in variables)
            if (len(variables)<=1):
                print "no variables, ignoring dataset"
                return

            currVar = variables[0]
            assert isinstance(currVar,pyvcell.ttypes.VariableInfo)

            dataFileName = vcellProxy2.getClient().getDataSetFileOfDomainAtTimeIndex(sim,currVar.domainName,0)
            self._visDataContext.setVariableInfos(variables);
            self._visDataContext.setCurrentVariable(currVar);
            self._visDataContext.setCurrentDataSet(sim);
            self._visDataContext.setCurrentDataSetTimePoints(vcellProxy2.getClient().getTimePoints(sim))
            self._visDataContext.setCurrentTimeIndex(0)

            self._vis.openOne(dataFileName,self._visDataContext.getCurrentVariable().variableVtuName,False)

            #vcellProxy2.getClient().displayPostProcessingDataInVCell(sim)
 
        except Exception as exc:
            print(exc.message)
            return
        finally:
            vcellProxy2.close()

        print("List of variable names from VCellProxy is:")
        varDisplayNames = [varInfo.variableDisplayName for varInfo in variables];
        print("var display names: "+str(varDisplayNames))
        varVtuNames = [varInfo.variableVtuName for varInfo in variables];
        print("var mesh names: "+str(varVtuNames))
        print("\nThe list of variable names from the MDServer is:")
        #print(self._vis.getMDVariableNames())

        assert isinstance(self._variableListWidget,QtGui.QListWidget)
        self._variableListWidget.clear()
        

        self._variableListWidget.addItems(varDisplayNames)
        if visQt.isPyside():
            self._variableListWidget.setCurrentItem(QtGui.QListWidgetItem(str(0)))
        elif visQt.isPyQt4:
            self._variableListWidget.setItemSelected(QtGui.QListWidgetItem(str(0)),True)

        times = self._visDataContext.getCurrentDataSetTimePoints()
        if times == None or len(times)==0:
            self._timeSlider.setMinimum(0)
            self._timeSlider.setMaximum(0)
            self._timeLabel.setText("0.0")
        else:
            self._timeSlider.setMinimum(0)
            self._timeSlider.setMaximum(len(times)-1)
            self._timeLabel.setText("0.0")

    def _getStatusBar(self):
        return self._statusBar

    def _onSelectedVariableChanged(self):
        currentText = self._variableListWidget.selectedItems()
        if currentText != None and len(currentText)>0:
            firstItem = currentText[0]
            assert isinstance(firstItem,QtGui.QListWidgetItem)
            print "selected variable is ",firstItem.text()

            oldVariable = self._visDataContext.getCurrentVariable()
            newVariable = self._visDataContext.getVtuVariableFromDisplayName(firstItem.text())
            self._visDataContext.setCurrentVariable(newVariable)
            vcellProxy2 = vcellProxy.VCellProxyHandler()
            try:
                vcellProxy2.open()
                sim = self._visDataContext.getCurrentDataSet()
                timeIndex = self._visDataContext.getCurrentTimeIndex()
                bSameDomain = (oldVariable.domainName == newVariable.domainName)
                newFilename = vcellProxy2.getClient().getDataSetFileOfDomainAtTimeIndex(sim,newVariable.domainName,timeIndex)
                self._vis.openOne(newFilename, newVariable.variableVtuName, bSameDomain)
            finally:
                vcellProxy2.close()

        print('_onSelectedVariableChanged()')

    def _onPointerModePushButton(self):
        print('_onPointerModePushButton()')

    def _onSaveImagePushButtonPressed(self):
        print('_onSaveImagePushButtonPressed()')

    def _exitApplication(self):
        print('closing visContext')
        self._vis.quit()
        print('closing qt application')
        QtCore.QCoreApplication.quit()

    def _onTimeSliderChanged(self):
        print('_onTimeSliderChanged()')
        newIndex = self._timeSlider.sliderPosition()
        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            vcellProxy2.open();
            self._visDataContext.setCurrentTimeIndex(newIndex)
            self._visDataContext.setCurrentTimePoint(self._visDataContext.getCurrentDataSetTimePoints()[newIndex])
            sim = self._visDataContext.getCurrentDataSet()
            print("New current Time Point should be: "+str(self._visDataContext.getCurrentDataSetTimePoints()[newIndex]))
            print("new current Time Point is: "+str(self._visDataContext.getCurrentTimePoint()))
            self._timeLabel.setText(str(self._visDataContext.getCurrentTimePoint()))
            dataFileName = vcellProxy2.getClient().getDataSetFileOfDomainAtTimeIndex(
                self._visDataContext.getCurrentDataSet(),
                self._visDataContext.getCurrentVariable().domainName,
                self._visDataContext.getCurrentTimeIndex())
        finally:
            vcellProxy2.close()
            
        self._vis.openOne(dataFileName,self._visDataContext.getCurrentVariable().variableVtuName,True)



    def _onSliceSliderChanged(self):
        print('_onSliceSliderChanged()')
        newPercent = self._sliceControl.getSliceSlider().sliderPosition()
       # self._vis.enableOperatorMode(True)
       # self._vis.setProject2d(self._sliceControl.getShowAsImageCheckbox().isChecked())
        print("_onSliceSliderChanged() is hard-coding project2d and enable=true")
        self._vis.setOperatorPercent(newPercent)
       # print("_onTimeSliderChanged("+str(newIndex)+", class="+str(newIndex.__class__)+")")

    def _onSliceAxisChange(self):
        print('_onSliceAxisChange()')
        axis = 0  # x axis
        if self._sliceControl.getSlice_X_AxisRadioButton().isChecked():
            axis = 0
        elif self._sliceControl.getSlice_Y_AxisRadioButton().isChecked():
            axis = 1
        elif self._sliceControl.getSlice_Z_AxisRadioButton().isChecked():
            axis = 2
        self._vis.setOperatorAxis(axis)

    def _onSliceAsImageCheckboxPressed(self):
        print('_onSlicePlanePushButtonPressed()')
        if self._sliceControl.getShowAsImageCheckbox().isChecked():
            self._vis.setOperatorProject2d(True)
        else:
            self._vis.setOperatorProject2d(False)

    def _onSliceEnableCheckboxPressed(self):
        print('_onSliceEnableCheckboxPressed()')
        if self._sliceControl.getEnableCheckbox().isChecked():
            self._vis.setOperatorEnabled(True)
        else:
            self._vis.setOperatorEnabled(False)


    def _onSetPickModeCheckBoxStateChanged(self):
        if self._queryControl.getSetPickModeCheckBox().isChecked():
            self._vis.setPickMode(1)
            print("Set pick mode 1")
        else:
            self._vis.setPickMode(0)
            print("Set pick mode 0")

    def _onSetMouseoverPickModeCheckBoxStateChanged(self):
        pass

    def _onAsynchClientTaskTaskTESTActivated(self):
        for n in [1,2,3,4]:
            print("Pass: "+str(n))
            # do time series
            print("adding time series task for point 1,1,1 instance # "+str(n)+"\n")
            self._vis.doTimeSeries((1,1,1), self.reportResults, self.reportError)
            print("adding lineout task from 0,0,0 to 1,1,1 instance # "+str(n)+"\n")
            self._vis.doLineoutLineout((0,0,0),(1,1,1), self.reportResults, self.reportError)

 
    def _onDoTimeSeriesButtonPressed(self):
            print("adding time series task for point 1,1,1 \n")
            self._vis.doTimeSeries((1,1,1), self.reportResults, self.reportError)

    def _onDoLineoutButtonPressed(self):
            print("adding lineout task from 0,0,0 to 5,5,5 \n")
            self._vis.doLineout((0,0,0),(5,5,5), self.reportResults, self.reportError)

 

    # Next two methods exist for asynchClientTask development and debug purposes:    
    def reportResults(self, results):
        print("RESULT SET:")
        print(str(results))

    def reportError(self, errorMessage):
        print("ERROR MESSAGE:")
        print(str(errorMessage))