import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui
Qt = visQt.QtCore.Qt

import zipfile, StringIO, glob, urllib2

import visGuiSliceControls
import visGuiQueryControls
import visDataSetChooserDialog
import vcellProxy
import pyvcell
from visContext import visContextAbstract
import visDataContext
from Queue import Queue
from threading import Lock


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
        if (self._vis.getPickMode() == 1) and (self._vis.getVariableName() is not None):
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
        self.sliceSliderQueue = Queue()
        self.timeSliderQueue = Queue()
        self.sliderLock = Lock()
        self.initUI()
        self.progress = None
        self.otherEventsQueue = Queue()
        self.otherEventTimer = QtCore.QTimer(self)
        self.otherEventTimer.timeout.connect(self.otherEventHandler)
        self.otherEventTimer.start(250) # runs always on main thread checking for otherEvents deposited from non-main threads
        self.minMaxExtents = None

    def otherEventHandler(self): # display modal message box with text from queue on the main thread
        if self.otherEventsQueue.empty() != True:
            nextElem = self.otherEventsQueue.get()
            if isinstance(nextElem,str):
                self.modalProgress(None)
                self.otherEventsQueue.task_done()
                msgBox = QtGui.QMessageBox(self)
                msgBox.setWindowModality(Qt.WindowModal)
                msgBox.setText(nextElem)
                msgBox.show()
            elif isinstance(nextElem,tuple):
                #print("---------------"+str(isinstance(nextElem,str))+" "+repr(nextElem))
                if nextElem[0] == "cursorWait" and QtGui.QApplication.overrideCursor() == None:
                    QtGui.QApplication.setOverrideCursor(QtGui.QCursor(QtCore.Qt.WaitCursor))
                elif nextElem[0] == "cursorRestore":
                    QtCore.QCoreApplication.instance().restoreOverrideCursor()
                self.otherEventsQueue.task_done()
                self.otherEventHandler()

    def closeEvent(self, event):
        self._vis.quit()
        ## do stuff
        #if self.canExit():
        #    print("canExit() true")
        #    event.accept() # let the window close
        #else:
        #    print("canExit() false")
        #    event.ignore()
  
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
        gridLayout.setColumnStretch(1,1)
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
        self._variableListWidget.setSelectionMode(QtGui.QAbstractItemView.SingleSelection)
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


        self.timeGroup = QtGui.QGroupBox(frame)
        self.timeGroup.setObjectName("timeGroup")
        self.timeGroup.setTitle("Time")
        sizePolicyTimegroup = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        sizePolicyTimegroup.setHorizontalStretch(0)
        sizePolicyTimegroup.setVerticalStretch(0)
        sizePolicyTimegroup.setHeightForWidth(self.timeGroup.sizePolicy().hasHeightForWidth())
        self.timeGroup.setSizePolicy(sizePolicyTimegroup)

        boxLayout_time = QtGui.QVBoxLayout(self.timeGroup)
        boxLayout_time.setObjectName("boxLayout_time")

        # Time Controls----------------------------------------------------
        self.timecntnr = QtGui.QWidget(self)
        cntnrSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Expanding, QtGui.QSizePolicy.Fixed,QtGui.QSizePolicy.DefaultType)
        self.timecntnr.setSizePolicy(cntnrSizePolicy)
        timecntnrgridLayout = QtGui.QGridLayout(self.timecntnr)

        self._timeminlabel = QtGui.QLabel(self)
        self._timeminlabel.setText("0")
        labelSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Minimum, QtGui.QSizePolicy.Fixed,QtGui.QSizePolicy.Label)
        self._timeminlabel.setSizePolicy(labelSizePolicy)
        timecntnrgridLayout.addWidget(self._timeminlabel, 0, 0, 1, 1)

        self._timemaxlabel = QtGui.QLabel(self)
        self._timemaxlabel.setText("1.0")
        labelSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Minimum, QtGui.QSizePolicy.Fixed,QtGui.QSizePolicy.Label)
        self._timemaxlabel.setSizePolicy(labelSizePolicy)
        timecntnrgridLayout.addWidget(self._timemaxlabel, 0, 2, 1, 1)

        self._timeSlider = QtGui.QSlider(self)
        self._timeSlider.setObjectName("sliceSlider")
        sliderSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Expanding, QtGui.QSizePolicy.Fixed,QtGui.QSizePolicy.Slider)
        sliderSizePolicy.setHorizontalStretch(100)
        sliderSizePolicy.setVerticalStretch(0)
        sliderSizePolicy.setHeightForWidth(self._timeSlider.sizePolicy().hasHeightForWidth())
        self._timeSlider.setSizePolicy(sliderSizePolicy)
        self._timeSlider.setOrientation(QtCore.Qt.Horizontal)
        timecntnrgridLayout.addWidget(self._timeSlider, 0, 1, 1, 1)

        boxLayout_time.addWidget(self.timecntnr)
        #self._timeSlider.sliderReleased.connect(self._onTimeSliderChanged)
        #self._timeSlider.valueChanged.connect(lambda: self.timeGroup.setTitle("Time: "+str(self._visDataContext.getCurrentDataSetTimePoints()[self._timeSlider.value()])))
        self._timeSlider.valueChanged.connect(self._onTimeSliderChanged)
        #---------------------------------------------------------

        gridLayout_3.addWidget(self.timeGroup, 0, 0, 1, 1)

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

        #actionOpen = QtGui.QAction(self);
        #actionOpen.setObjectName("actionOpen")
        self._actionOpenSimFromOpenVCellModels = QtGui.QAction(self)
        self._actionOpenSimFromOpenVCellModels.setObjectName("actionOpenSimFromOpenVCellModels")
        actionExit = QtGui.QAction(self);
        actionExit.setObjectName("actionExit")
        menuBar.addAction(menuFile.menuAction())
        
        #menuFile.addAction(actionOpen)
        menuFile.addAction(self._actionOpenSimFromOpenVCellModels)
        menuFile.addSeparator()
        menuFile.addAction(actionExit)

        self.setWindowTitle("VCell VisIt viewer")
        #actionOpen.setText("Open")
        self._actionOpenSimFromOpenVCellModels.setText("Load Sim from open VCell Models")
        actionExit.setText("Exit")
        #tabWidget.setTabText(tabWidget.indexOf(sliceTab), "Slice")
        tabWidget.setTabText(tabWidget.indexOf(sliceTab), "Plot")
        #tabWidget.setTabText(tabWidget.indexOf(volumeTab),"Volume")
        variableGroup.setTitle("Variables")
        menuFile.setTitle("File")


        


        #
        # set up all connections
        #
        #actionOpen.setStatusTip("Open file")
        #actionOpen.triggered.connect(self._showLoadFile)
        self._actionOpenSimFromOpenVCellModels.setStatusTip("Open Sim from VCell open models")
        self._actionOpenSimFromOpenVCellModels.triggered.connect(self._showOpenSimFromVCellOpenModels)
        actionExit.setStatusTip("Exit viewer")
        actionExit.triggered.connect(self._exitApplication)
        self._variableListWidget.clicked.connect(self._onSelectedVariableChanged)

        self._sliceControl.setChecked(self._vis.getOperatorEnabled())
        self._sliceControl.getSliceSlider().setValue(self._vis.getOperatorPercent())
        self._sliceControl.getShowAsImageCheckbox().setChecked(self._vis.getOperatorProject2d())
        self._sliceControl.getSlice_X_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==0)
        self._sliceControl.getSlice_Y_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==1)
        self._sliceControl.getSlice_Z_AxisRadioButton().setChecked(self._vis.getOperatorAxis()==2)

        self._sliceControl.clicked.connect(self._onSliceEnableCheckboxPressed)
        self._sliceControl.getSliceSlider().valueChanged.connect(self._onSliceSliderChanged)
        #self._sliceControl.getSliceSlider().sliderReleased.connect(self._onSliceSliderChanged)
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
        
        self._vis.installEventFilter(self._mouseEventFilter)
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
        def successCallback(results):
            print("_onMouseButtonPressedForPick: getPick() success "+str(results));
            self._showStatusMessage(results)   # note screen to window coordinate system translation in the Y (screen) axis

        def errorCallback(errorMessage):
            print("_onMouseButtonPressedForPick: getPick() error: "+str(errorMessage));
            self._showStatusMessage("Exception occurred while retrieving data\n"+errorMessage)

        self._vis.getPick(pos.x(),size.height()-pos.y(), successCallback, errorCallback)


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
            msgBox = QtGui.QMessageBox()
            msgBox.setText("Exception occurred: "+exc.message)
            msgBox.exec_()
            return
        finally:
            vcellProxy2.close()

 #   def _showLoadFile(self):
 #       print('showLoadFile()')
 #       if visQt.isPyside():
 #           fnames,pattern = QtGui.QFileDialog.getOpenFileNames(self, 'Open file', '/.', "VTK (*.vtu *.vtk);;all files (*.*)")
 #       elif visQt.isPyQt4():
 #           fnames = QtGui.QFileDialog.getOpenFileName(self, 'Open file', '/.', "VTK (*.vtu *.vtk);;all files (*.*)")

 ##       fname = "C:\Developer\eclipse\workspace\pyVCell\PysideVCell\data\5323530454\SimID_533617164_0__0_.feature_EC_*.vtk database"
 #       if (fnames == None or len(fnames)==0):
 #           return

 #       self._vis.open(fnames)
 #       print ("\n\n\n\n\ngetting var names .... self._vis.getVariableNames()\n\n\n\n\n");
 #       varNames = self._vis.getVariableNames()
 #       assert isinstance(self._variableListWidget,QtGui.QListWidget)
 #       self._variableListWidget.clear()
 #       self._variableListWidget.addItems(varNames)
 #       if visQt.isPyside():
 #           self._variableListWidget.setCurrentItem(QtGui.QListWidgetItem(str(0)))
 #       elif visQt.isPyQt4:
 #           self._variableListWidget.setItemSelected(QtGui.QListWidgetItem(str(0)),True)

 #       times = self._vis.getTimes()
 #       if times == None or len(times)==0:
 #           self._timeSlider.setMinimum(0)
 #           self._timeSlider.setMaximum(0)
 #           self._timeLabel.setText("0.0")
 #       else:
 #           self._timeSlider.setMinimum(0)
 #           self._timeSlider.setMaximum(len(times)-1)
 #           self._timeLabel.setText("0.0")

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

    CONST_MODALPROGRESSMAX = 25
    def modalProgress(self,message):
        #count = len(self.findChildren(QtGui.QDialog))
        if message != None:
            if self.progress == None:
                self.progress = QtGui.QProgressDialog('...', '', 0, self.CONST_MODALPROGRESSMAX) #default application modal
                #self.progress.setWindowModality(Qt.WindowModal) #if only wondow modal wanted
                self.progress.setMinimumDuration(0)
            self.progress.setValue(1)
            self.progress.setLabelText(message)
            self.progress.show()
            # Make timer to animate progress bar because progress is unknown
            self.timer = QtCore.QTimer()
            self.timer.timeout.connect(lambda : (self.progress.setValue(((self.progress.value()+1) if (self.progress.value()<(self.CONST_MODALPROGRESSMAX-1)) else (self.CONST_MODALPROGRESSMAX-1)))))
            self.timer.start(1000) # 1 second
        else:
            self.timer.stop()
            self.progress.setValue(self.CONST_MODALPROGRESSMAX)
            

    def _onSimulationSelected(self, dialog):
       selectedSim = dialog.getSelectedSimulation()
       dialog.close()
       print("-----")
       assert isinstance(selectedSim, pyvcell.ttypes.SimulationDataSetRef)
       print(selectedSim)
       print("---------- Starting Dialog ----------")
       self.loadSim(selectedSim)

    def loadSim(self, selectedSim):
       assert isinstance(selectedSim, pyvcell.ttypes.SimulationDataSetRef)
       self.modalProgress("Loading '"+selectedSim.simName+"'...")

       self.lst = LoadSimThread(selectedSim,self)
       #self.lst.finished.connect(self.otherEventHandler)
       self.lst.start()

    def _onSimulationSelected0(self, sim):
        
        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            vcellProxy2.open()
            variables = vcellProxy2.getClient().getVariableList(sim)
            all(isinstance(n,pyvcell.ttypes.VariableInfo) for n in variables)
            if (len(variables)<1):
                print "no variables, ignoring dataset ... new"
                return

            currVar = variables[0]
            assert isinstance(currVar,pyvcell.ttypes.VariableInfo)

            dataFileName = vcellProxy2.getClient().getDataSetFileOfVariableAtTimeIndex(sim,currVar,0)
            self._visDataContext.setVariableInfos(variables);
            self._visDataContext.setCurrentVariable(currVar);
            self._visDataContext.setCurrentDataSet(sim);
            self._visDataContext.setCurrentDataSetTimePoints(vcellProxy2.getClient().getTimePoints(sim))
            self._visDataContext.setCurrentTimeIndex(0)
            
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
            #if visQt.isPyside():
            #    #self._variableListWidget.setCurrentItem(QtGui.QListWidgetItem(str(0)))
            #    self._variableListWidget.item(0).setSelected(True)
            #elif visQt.isPyQt4:
            #    self._variableListWidget.setItemSelected(QtGui.QListWidgetItem(str(0)),True)

            times = self._visDataContext.getCurrentDataSetTimePoints()
            self._timeSlider.blockSignals(True)
            if times == None or len(times)==0:
                self._timeSlider.setMinimum(0)
                self._timeSlider.setMaximum(0)
                self.timeGroup.setTitle("Time: 0.0")
            else:
                self._timeSlider.setMinimum(0)
                self._timeSlider.setMaximum(len(times)-1)
                self.timeGroup.setTitle("Time: 0.0")
                self._timemaxlabel.setText(str(times[len(times)-1]))

            self._timeSlider.setValue(0)
            self._timeSlider.blockSignals(False)

            def successCallback(results):
                self.minMaxExtents = results
                print("_onSimulationSelected: openOne() success "+str(self.minMaxExtents));
                self._variableListWidget.setCurrentRow(0)
                self._variableListWidget.item(0).setSelected(True)
                self._variableListWidget.setFocus()
                self.modalProgress(None)
                print(str(sim))
                newTtitle = "VCell Visit View " + ("(Math)" if sim.isMathModel else "(Bio) ") + '\''+sim.modelName+'\'->' + (('\''+sim.simulationContextName+"\'->") if sim.simulationContextName != None else '') + '\''+sim.simName+'\''
                self.setWindowTitle(newTtitle)

            def errorCallback(errorMessage):
                print("_onSimulationSelected: openOne() error: "+str(errorMessage));
                self.modalProgress(None)

            self._vis.openOne(dataFileName,self._visDataContext.getCurrentVariable().variableVtuName,False,successCallback,errorCallback)

            #vcellProxy2.getClient().displayPostProcessingDataInVCell(sim)
 
        except Exception as exc:
            print(exc.message)
            self.otherEventsQueue.put("Exception occurred while retrieving data\n"+exc.message)
            return
        finally:
            vcellProxy2.close()



    def _getStatusBar(self):
        return self._statusBar

    def _onSelectedVariableChanged(self):
        self.otherEventsQueue.put(("cursorWait",None))
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
                newFilename = vcellProxy2.getClient().getDataSetFileOfVariableAtTimeIndex(sim,newVariable,timeIndex)
                
                def successCallback(results):
                    self.otherEventsQueue.put(("cursorRestore",None))
                    self.minMaxExtents = results
                    print("_onSelectedVariableChanged: openOne() success "+str(self.minMaxExtents));
                    if self._sliceControl.isChecked():
                        self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE+" "+str(self.getExtentAlongSliderAxis(self._sliceControl.getSliceSlider().value())))

                def errorCallback(errorMessage):
                    self.otherEventsQueue.put(("cursorRestore",None))
                    print("_onSelectedVariableChanged: openOne() error: "+str(errorMessage));
    
                self._vis.openOne(newFilename, newVariable.variableVtuName, bSameDomain, successCallback, errorCallback)
            except Exception as exc:
                self.otherEventsQueue.put(("cursorRestore",None))
                print(exc.message)
                msgBox = QtGui.QMessageBox()
                msgBox.setText("Exception occurred: "+exc.message)
                msgBox.exec_()
                return
            finally:
                vcellProxy2.close()

        print('_onSelectedVariableChanged()')

    def _onPointerModePushButton(self):
        print('_onPointerModePushButton()')

    def _onSaveImagePushButtonPressed(self):
        print('_onSaveImagePushButtonPressed()')

    def _exitApplication(self):
        self._vis.quit()

    def _onTimeSliderChanged(self):
        self.otherEventsQueue.put(("cursorWait",None))
        self.timeGroup.setTitle("Time: "+str(self._visDataContext.getCurrentDataSetTimePoints()[self._timeSlider.value()]))
        #lastSlidePos = self.sliderQ(self._timeSlider.sliderPosition())
        lastSlidePos = self.sliderQ(self._timeSlider.value(),self.timeSliderQueue)
        if lastSlidePos[VCellPysideApp.CONST_WASNEW] == False:
            self.lst = TimeChangedThread(self)
            self.lst.start()

    def _onTimeSliderChanged0(self):
        QtCore.QThread.msleep(250)

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
            #self.timeGroup.setTitle("Time: "+str(self._visDataContext.getCurrentTimePoint()))
            dataFileName = vcellProxy2.getClient().getDataSetFileOfVariableAtTimeIndex(
                self._visDataContext.getCurrentDataSet(),
                self._visDataContext.getCurrentVariable(),
                self._visDataContext.getCurrentTimeIndex())
        except Exception as exc:
            print(exc.message)
            msgBox = QtGui.QMessageBox()
            msgBox.setText("Exception occurred: "+exc.message)
            msgBox.exec_()
            return
        finally:
            vcellProxy2.close()
                
        def successCallback(results):
            self.otherEventsQueue.put(("cursorRestore",None))
            print("_onTimeSliderChanged: openOne() success "+str(results));
            checkSlidePos = self.sliderQ(None,self.timeSliderQueue)
            if checkSlidePos[VCellPysideApp.CONST_POSITION] != self._visDataContext.getCurrentTimeIndex():
               self._onTimeSliderChanged()

        def errorCallback(errorMessage):
            self.otherEventsQueue.put(("cursorRestore",None))
            print("_onTimeSliderChanged: openOne() error: "+str(errorMessage));
            self.sliderQ(None,self.timeSliderQueue) #clear queue

        self._vis.openOne(dataFileName,self._visDataContext.getCurrentVariable().variableVtuName,True,successCallback,errorCallback)

    CONST_POSITION = 0
    CONST_WASNEW = 1
    def sliderQ(self,newSlidePos,theSliderQueue):
        try:
            self.sliderLock.acquire()
            if theSliderQueue.qsize() != 0:
                assert theSliderQueue.qsize() == 1
                lastPos = theSliderQueue.get(False)
                theSliderQueue.task_done()
                if newSlidePos == None:
                    return (lastPos,True)
                theSliderQueue.put(newSlidePos)
                return (newSlidePos,True)
            else:
                assert newSlidePos != None
                theSliderQueue.put(newSlidePos)
                return (newSlidePos,False)
        finally:
            self.sliderLock.release()

    CONST_MIN_EXTENT = 0
    CONST_MAX_EXTENT = 1
    CONST_EXTENT_X = 0
    CONST_EXTENT_Y = 1
    CONST_EXTENT_Z = 2
    def getExtentAlongSliderAxis(self,position):
        #checkSlidePos = self.sliderQ(None,self.sliceSliderQueue)
        ext = None
        if self.minMaxExtents != None:
            if self._sliceControl.getSlice_X_AxisRadioButton().isChecked():
                print "X checked"
                ext = self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_X] + \
                    ((self.minMaxExtents[VCellPysideApp.CONST_MAX_EXTENT][VCellPysideApp.CONST_EXTENT_X] - self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_X]) * \
                    position/100)
            elif self._sliceControl.getSlice_Y_AxisRadioButton().isChecked():
                print "Y checked"
                ext = self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_Y] + \
                    ((self.minMaxExtents[VCellPysideApp.CONST_MAX_EXTENT][VCellPysideApp.CONST_EXTENT_Y] - self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_Y]) * \
                    position/100)
            elif self._sliceControl.getSlice_Z_AxisRadioButton().isChecked():
                print "Z checked"
                ext = self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_Z] + \
                    ((self.minMaxExtents[VCellPysideApp.CONST_MAX_EXTENT][VCellPysideApp.CONST_EXTENT_Z] - self.minMaxExtents[VCellPysideApp.CONST_MIN_EXTENT][VCellPysideApp.CONST_EXTENT_Z]) * \
                    position/100)
        print ext
        return ext

    def _onSliceSliderChanged(self):
        lastSlidePos = self.sliderQ(self._sliceControl.getSliceSlider().sliderPosition(),self.sliceSliderQueue)
        #self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE+" "+str(lastSlidePos[VCellPysideApp.CONST_POSITION]))
        self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE+" "+str(self.getExtentAlongSliderAxis(lastSlidePos[VCellPysideApp.CONST_POSITION])))
        #CONST_SLICE_TITLE
        if lastSlidePos[VCellPysideApp.CONST_WASNEW] == False:
            print('_onSliceSliderChanged()')
            #self._vis.enableOperatorMode(True)
            #self._vis.setProject2d(self._sliceControl.getShowAsImageCheckbox().isChecked())
            print("_onSliceSliderChanged() is hard-coding project2d and enable=true")

            def successCallback(results):
                checkSlidePos = self.sliderQ(None,self.sliceSliderQueue)
                print("_onSliceSliderChanged: updateOperatorPercent() success "+str(results));
                if checkSlidePos[VCellPysideApp.CONST_POSITION] != results:
                    self._onSliceSliderChanged()
            def errorCallback(errorMessage):
                print("_onSliceSliderChanged: updateOperatorPercent() error: "+str(errorMessage));
                self.sliderQ(None,self.sliceSliderQueue) #clear queue
    
            self._vis.setOperatorPercent(lastSlidePos[VCellPysideApp.CONST_POSITION], successCallback, errorCallback)
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
        self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE+" "+str(self.getExtentAlongSliderAxis(self._sliceControl.getSliceSlider().sliderPosition())))

    def _onSliceAsImageCheckboxPressed(self):
        print('_onSlicePlanePushButtonPressed()')
        if self._sliceControl.getShowAsImageCheckbox().isChecked():
            self._vis.setOperatorProject2d(True)
        else:
            self._vis.setOperatorProject2d(False)

    def _onSliceEnableCheckboxPressed(self):
        print('_onSliceEnableCheckboxPressed()')
        if self._sliceControl.isChecked():
            self._vis.setOperatorEnabled(True)
            print "----------"+str(self.getExtentAlongSliderAxis(self._sliceControl.getSliceSlider().value()))
            self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE+" "+str(self.getExtentAlongSliderAxis(self._sliceControl.getSliceSlider().value())))
        else:
            self._vis.setOperatorEnabled(False)
            self._sliceControl.setTitle(visGuiSliceControls.sliceControl.CONST_SLICE_TITLE)

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


class LoadSimThread(QtCore.QThread):
    def __init__(self, sim, app, parent = None):
        QtCore.QThread.__init__(self,parent)
        self._sim = sim
        self._app = app
    def run(self):
        self._app._onSimulationSelected0(self._sim)

class TimeChangedThread(QtCore.QThread):
    def __init__(self, app,parent = None):
        QtCore.QThread.__init__(self,parent)
        self._app = app
    def run(self):
        self._app._onTimeSliderChanged0()
