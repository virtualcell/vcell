import pyvcell
import pyvcell.ttypes

class VisDataContext(object):
    def __init__(self):
        print ("VisDataContext init")
        self._currentDataSet = None
        self._currentDataSetTimePoints = None
        self._currentTimeIndex = None
        self._currentTimePoint = None
        self._currentVariable = None
        self._variableInfos = None


    def getCurrentDataSet(self):
        return self._currentDataSet

    def setCurrentDataSet(self, dataSet):
        self._currentDataSet = dataSet

    def setCurrentDataSetTimePoints(self, timePoints):
        self._currentDataSetTimePoints = timePoints

    def getCurrentDataSetTimePoints(self):
        return self._currentDataSetTimePoints

    def getCurrentTimeIndex(self):
        return self._currentTimeIndex

    def setCurrentTimeIndex(self, timeIndex):
        self._currentTimeIndex = timeIndex

    def getCurrentTimePoint(self):
        return self._currentTimePoint

    def setCurrentTimePoint(self, timePoint):
        self._currentTimePoint = timePoint

    def getCurrentVariable(self):
        assert(isinstance(self._currentVariable,pyvcell.ttypes.VariableInfo))
        return self._currentVariable

    def setCurrentVariable(self, variable):
        self._currentVariable = variable

    def getVariableInfos(self):
        return self._variableInfos

    def setVariableInfos(self, variableInfos):
        self._variableInfos = variableInfos

    def getVtuVariableFromDisplayName(self, varDisplayName):
        for varInfo in self._variableInfos:
            assert(isinstance(varInfo,pyvcell.ttypes.VariableInfo));
            if varInfo.variableDisplayName == varDisplayName:
                return varInfo
        return None
