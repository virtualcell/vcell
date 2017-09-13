import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui
import visContext
from visContext.visContextAbstract import visContextAbstract;
import vcellProxy
import pyvcell
from operator import attrgetter

class DataSetChooserDialog(QtGui.QDialog):

    simulationSelected = QtCore.Signal(QtCore.QObject)

    def __init__(self, parent=None):
        super(DataSetChooserDialog, self).__init__(parent)
        self.setWindowTitle("Choose result set")
        self._simTable = None
        self.simList = None
        self._selectedSim = None
#        self._domainSelected = None
        self.setAttribute(QtCore.Qt.WA_DeleteOnClose)

    def initUI(self, vis):
        self._vis = vis

        assert isinstance(self._vis,visContextAbstract)
        self.setObjectName("queryControlWidget")
        selfSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)
        self.setMinimumSize(300,200)
        gridLayout = QtGui.QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        self._simTable = QtGui.QTableWidget(self)
        self._simTable.setSelectionMode(QtGui.QAbstractItemView.SingleSelection)
        self._simTable.setSelectionBehavior(QtGui.QAbstractItemView.SelectRows)
        self._simTable.setColumnCount(4)
        self._simTable.setHorizontalHeaderLabels(("Model","Application","Simulation","jobIndex"))
#        self._domainChoiceComboBox = QtGui.QComboBox(self)
#        self._dataSetComboBox.activated.connect(self._changeSimulationSelectionAction)


        gridLayout.addWidget(self._simTable,0,0,) 
 #       gridLayout.addWidget(self._domainChoiceComboBox,1,0) 
        openButton = QtGui.QPushButton("Open",self)
        cancelButton = QtGui.QPushButton("Cancel",self)
        buttonLayout = QtGui.QHBoxLayout()
        buttonLayout.addWidget(openButton)
        buttonLayout.addStretch(1)
        buttonLayout.addWidget(cancelButton)
        openButton.pressed.connect(self._openButtonPressedAction)
        cancelButton.pressed.connect(self._cancelButtonPressedAction)
        gridLayout.addLayout(buttonLayout,2,0)

        # Get available simulation dataset of open models from the VCell client
        self.simList = None
        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            print("calling vcellProxy2.getSimsFromOpenModels()")
            vcellProxy2.open()
            self.simList = vcellProxy2.getClient().getSimsFromOpenModels()
            # Progressively sort
            self.simList.sort(key=attrgetter('jobIndex'))
            self.simList.sort(key=attrgetter('simName'))
            #self.simList.sort(key=lambda sim: '(math)' if sim.isMathModel else sim.simulationContextName)
            self.simList.sort(key=attrgetter('simulationContextName'))
            self.simList.sort(key=attrgetter('modelName'))
        except Exception as simFromModelException:
            self.simList = simFromModelException
            print("Exception looking for open model datasets "+repr(simFromModelException))
        finally:
            vcellProxy2.close()
        print(self.simList)
        if (isinstance(self.simList,Exception) or self.simList==None or len(self.simList)==0):
            msgBox = QtGui.QMessageBox()
            if (isinstance(self.simList,Exception)):
                msgBox.setText("VCell communication error:\nCheck VCell is started and has at least 1 open model with spatial simulation data")
            else:
                msgBox.setText("no completed 2D or 3D spatial simulations found in open VCell models")
            msgBox.exec_()
            raise Exception("No simulations found")
            #return

        # populate the QTableWidget if we found datasets
        self._simTable.setRowCount(len(self.simList))
        for i,sim in enumerate(self.simList):
            self._simTable.setItem(i,0,QtGui.QTableWidgetItem(sim.modelName))
            self._simTable.setItem(i,1,QtGui.QTableWidgetItem("(math)" if sim.simulationContextName == None else sim.simulationContextName))
            self._simTable.setItem(i,2,QtGui.QTableWidgetItem(sim.simName))
            self._simTable.setItem(i,3,QtGui.QTableWidgetItem(str(sim.jobIndex)))
            #self._dataSetComboBox.addItem(sim.simName,sim)
            #self._changeSimulationSelectionAction()

        self._simTable.resizeColumnsToContents()

        vwidth = self._simTable.verticalHeader().width()
        hwidth = self._simTable.horizontalHeader().length()
        swidth = self._simTable.style().pixelMetric(QtGui.QStyle.PM_ScrollBarExtent)
        fwidth = self._simTable.frameWidth() * 2
        self.resize(vwidth + hwidth + swidth + fwidth,self.size().height())
        #self._simTable.setHorizontalScrollBarPolicy(QtCore.Qt.ScrollBarAlwaysOff)

    #def _changeSimulationSelectionAction(self):
    #    print('starting _changeSimulationSelectionAction')
    #    currentIndex = self._dataSetComboBox.currentIndex()
    #    currentSimSelection = self._dataSetComboBox.itemData(currentIndex)
    #    vcellProxy2 = vcellProxy.VCellProxyHandler()
    #    try:
    #        vcellProxy2.open()
    #        simulationDataSetRef = self._dataSetComboBox.itemData(currentIndex)
    #        assert isinstance(simulationDataSetRef, pyvcell.ttypes.SimulationDataSetRef)
    #        varList = vcellProxy2.getClient().getVariableList(simulationDataSetRef)
    #        domainList = list(set([varList[i].domainName for i in range(len(varList)-1)]))
    #        self._domainChoiceComboBox.clear()
    #        for domain in domainList:
    #            if (domain != "None"):
    #                self._domainChoiceComboBox.addItem(domain, domain)
    #    except Exception as exc:
    #        print("Exception occurred getting domains")
    #        print(str(exc))
    #    finally:
    #        vcellProxy2.close()


    def _openButtonPressedAction(self):
        currentIndex = self._simTable.currentRow()
        if (currentIndex == None):
            return(None)
        self._selectedSim = self.simList[currentIndex]
        if (visQt.isPyQt4() and isinstance(self._selectedSim, QtCore.QVariant)):
            self._selectedSim = self._selectedSim.toPyObject()
        #currentIndex = self._domainChoiceComboBox.currentIndex()
        #if (currentIndex == None):
        #    return(None)
        #self._selectedDomain = self._domainChoiceComboBox.itemData(currentIndex)

        self.simulationSelected.emit(self)

    def _cancelButtonPressedAction(self):
        self.close()

    #def getSelectedDomain(self):
    #    return self._selectedDomain

    def getSelectedSimulation(self):
        return self._selectedSim


