import sys, os

import visQt
QtCore = visQt.QtCore
QtGui = visQt.QtGui
import visContextAbstract

class DataSetChooserGroupBox(QtGui.QGroupBox):
    
    simulationSelected = QtCore.Signal(QtCore.QObject)

    def __init__(self, parent):
        super(DataSetChooserDialog, self).__init__(parent)
        self.setTitle("Choose result set")
        self._dataSetComboBox = None
        self._selectedSim = None
        self._domainSelected = None
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
        self._domainChoiceComboBox = QtGui.QComboBox(self)
        self._dataSetComboBox.activated.connect(self._changeSimulationSelectionAction)
        self._domainChoiceComboBox.activated.connect(self._newDomainSelectedAction)

        gridLayout.addWidget(self._dataSetComboBox,0,0,) 
        gridLayout.addWidget(self._domainChoiceComboBox,1,0) 
        

        # Get available simulation dataset of open models from the VCell client
        simList = None
        try:
            print("calling self._vis.getVCellProxy().getSimsFromOpenModels()")
            self._vis.getVCellProxy().open()
            simList = self._vis.getVCellProxy().getClient().getSimsFromOpenModels()
        except:
            simList = None
            print("Exception looking for open model datasets")
        finally:
            self._vis.getVCellProxy().close()

        if (simList==None or len(simList)==0):
            raise Exception("No simulations found")
            return

        # populate the QComboBox if we found datasets
        for sim in simList:
            self._dataSetComboBox.addItem(sim.simName,sim)
            self._changeSimulationSelectionAction()


    def _changeSimulationSelectionAction(self):
        print('starting _changeSimulationSelectionAction')
        currentIndex = self._dataSetComboBox.currentIndex()
        currentSimSelection = self._dataSetComboBox.itemData(currentIndex)
        try:
            self._vis.getVCellProxy().open()
            varList = self._vis.getVCellProxy().getClient().getVariableList(self._dataSetComboBox.itemData(currentIndex))
            domainList = list(set([varList[i].domainName for i in range(len(varList)-1)]))
            self._domainChoiceComboBox.clear()
            for domain in domainList:
                if (domain != "None"):
                    self._domainChoiceComboBox.addItem(domain, domain)
        except Exception as exc:
            print("Exception occurred getting domains")
            print(str(exc))
        finally:
            self._vis.getVCellProxy().close()


    def _newDomainSelectedAction(self):
        currentIndex = self._dataSetComboBox.currentIndex()
        if (currentIndex == None):
            return(None)
        self._selectedSim = self._dataSetComboBox.itemData(currentIndex)
        currentIndex = self._domainChoiceComboBox.currentIndex()
        if (currentIndex == None):
            return(None)
        self._selectedDomain = self._domainChoiceComboBox.itemData(currentIndex)

        self.simulationSelected.emit(self)


    def getSelectedDomain(self):
        return self._selectedDomain

    def getSelectedSimulation(self):
        return self._selectedSim