import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui
import visContextAbstract
import vcellProxy
import pyvcell

class DataSetChooserDialog(QtGui.QDialog):
    
    simulationSelected = QtCore.Signal(QtCore.QObject)

    def __init__(self, parent=None):
        super(DataSetChooserDialog, self).__init__(parent)
        self.setWindowTitle("Choose result set")
        self._dataSetComboBox = None
        self._selectedSim = None
#        self._domainSelected = None
        self.setAttribute(QtCore.Qt.WA_DeleteOnClose)

    def initUI(self, vis):
        self._vis = vis

        assert isinstance(self._vis,visContextAbstract.visContextAbstract)
        self.setObjectName("queryControlWidget")
        selfSizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Preferred, QtGui.QSizePolicy.Minimum)
        selfSizePolicy.setHorizontalStretch(0)
        selfSizePolicy.setVerticalStretch(0)
        selfSizePolicy.setHeightForWidth(self.sizePolicy().hasHeightForWidth())
        self.setSizePolicy(selfSizePolicy)
        self.setMinimumSize(300,150)
        gridLayout = QtGui.QGridLayout(self)
        gridLayout.setObjectName("gridLayout")

        self._dataSetComboBox = QtGui.QComboBox(self)
#        self._domainChoiceComboBox = QtGui.QComboBox(self)
#        self._dataSetComboBox.activated.connect(self._changeSimulationSelectionAction)


        gridLayout.addWidget(self._dataSetComboBox,0,0,) 
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
        simList = None
        vcellProxy2 = vcellProxy.VCellProxyHandler()
        try:
            print("calling vcellProxy2.getSimsFromOpenModels()")
            vcellProxy2.open()
            simList = vcellProxy2.getClient().getSimsFromOpenModels()
        except:
            simList = None
            print("Exception looking for open model datasets")
        finally:
            vcellProxy2.close()

        if (simList==None or len(simList)==0):
            raise Exception("No simulations found")
            return

        # populate the QComboBox if we found datasets
        for sim in simList:
            self._dataSetComboBox.addItem(sim.simName,sim)
            #self._changeSimulationSelectionAction()


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
        currentIndex = self._dataSetComboBox.currentIndex()
        if (currentIndex == None):
            return(None)
        self._selectedSim = self._dataSetComboBox.itemData(currentIndex)
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


