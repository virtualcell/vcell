import visit
from asynchClientTask import *

class TimeSeriesAsynchClientTask(AsynchClientTask):

    def __init__(self, point, dataReadyCallback, onErrorCallback):
        super(TimeSeriesAsynchClientTask, self).__init__("TimeSeries", True, dataReadyCallback, onErrorCallback)
        assert isinstance(point, tuple)
        assert len(point) in [2,3]
        #assert isinstance(point,(int,float,long))
        self._point = point
        print("timeSeries init, point = "+str(self._point))
        self._timeSeriesData = None
        self._state = AsynchClientTask.STATE_0_NO_REQUEST_YET
        print("timeseries initial state = "+str(self.getTaskState()))
        self.stateTransitions = {
            AsynchClientTask.STATE_5_EXCEPTION:         self.foundError, 
            AsynchClientTask.STATE_0_NO_REQUEST_YET:    self.doTimeSeries, 
            AsynchClientTask.STATE_1_REQUEST_INITIATED: self.isDataReady, 
            AsynchClientTask.STATE_2_DATA_ALREADY:      self.doCleanup,
            AsynchClientTask.STATE_3_CLEANUP_REQUESTED: self.isCleanupDone, 
            AsynchClientTask.STATE_4_CLEANUP_DONE:      self.cleanupIsDone}

    def checkState(self):
        try:
            print("timeSeries checking state. =="+str(self._state))

            self.stateTransitions[self.getTaskState()]()
        except: 
            self._state = AsynchClientTask.STATE_5_EXCEPTION

    def doTimeSeries(self):
        self._state = AsynchClientTask.STATE_1_REQUEST_INITIATED
        try:
            print("doTimeSeries")
            if (2 in visit.GetGlobalAttributes().GetWindows()):
                visit.SetActiveWindow(2)
                assert visit.GetNumPlots()==0 
            print("passed assertion that Window2 doesn't exist yet or has no plots")
            print(visit.SetActiveWindow(1))
            print("Numplots = "+str(visit.GetNumPlots()))
            self.setTimeCurvePickAttributes()
            print("Set ActiveWindow = 1 and set TimeCurvePick Attributes")
            print("about to pick at point: "+str(self._point))
            visit.ZonePick(self._point)
            print("fired off NodePick")
        except:
            self._state = AsynchClientTask.STATE_5_EXCEPTION
        finally:
            visit.SetActiveWindow(1)

    def isDataReady(self):
        try:
            print("timeSeries: about to set window to 2.  windows are: "+str(visit.GetGlobalAttributes().GetWindows()))

            if (2 in visit.GetGlobalAttributes().GetWindows()):
                visit.SetActiveWindow(2)
                if visit.GetNumPlots()==1:
                    print("time series data ready\n")
                    self._timeSeriesData = visit.GetPlotInformation()['Curve']
                    print("Got plot information")
                    print(self._timeSeriesData.__class__.__name__)
                    if isinstance(self._timeSeriesData, tuple):
                        print("The data from the time series is:\n")
                        print(str(self._timeSeriesData))
                        print("\n")
                        self._state = AsynchClientTask.STATE_2_DATA_ALREADY
                        self._onDataIsReady(self._timeSeriesData)
                        return True
                    else:
                        self._state = AsynchClientTask.STATE_5_EXCEPTION
                        return False
                else:
                    return false
            else:
                print("timeSeries: Window 2 doesn't currently exist (yet)")
                return False
        except:
            self._state = AsynchClientTask.STATE_5_EXCEPTION
            return None

    def getTimeSeriesData(self):
        assert self._state in [AsynchClientTask.STATE_2, AsynchClientTask.STATE_3_CLEANUP_REQUESTED, AsynchClientTask.STATE_4_CLEANUP_DONE]
        return self._timeSeriesData

    def doCleanup(self):
        try:
            assert self._state in [AsynchClientTask.STATE_1_REQUEST_INITIATED, AsynchClientTask.STATE_2_DATA_ALREADY]
            self._state = AsynchClientTask.STATE_2_DATA_ALREADY
            visit.SetActiveWindow(2)
            assert visit.GetNumPlots()!=0 
            visit.DeleteAllPlots()
            visit.SetActiveWindow(1)
            self._state = AsynchClientTask.STATE_3_CLEANUP_REQUESTED
        except:
            self._state = AsynchClientTask.STATE_5_EXCEPTION
        finally:
            visit.SetActiveWindow(1)


    def isCleanupDone(self):
        print("timeSeries isCleanupDone state = " + str(self._state))
        assert self._state == AsynchClientTask.STATE_3_CLEANUP_REQUESTED
        try:
            visit.SetActiveWindow(2)
            if visit.GetNumPlots()==0:
                self._state = AsynchClientTask.STATE_4_CLEANUP_DONE
                visit.SetActiveWindow(1)
                return True
            else:
                SetActiveWindow(1)
                return False
        except:
            self._state = AsynchClientTask.STATE_5_EXCEPTION
            return None
 
    def cleanupIsDone(self):
        pass # Someone should call getLineoutData if they haven't already and let me be forgotten memory anytime now

    def foundError(self):
        self._onExceptionEncountered("timeSeriesAsychClientTask error encountered")

    def setTimeCurvePickAttributes(self):   
        print("Starting setTimeCurvePickAttributes") 
        pickAttributes = visit.GetPickAttributes()
        pickAttributes.variables = ("default")
        pickAttributes.showIncidentElements = 1
        pickAttributes.showNodeId = 1
        pickAttributes.showNodeDomainLogicalCoords = 0
        pickAttributes.showNodeBlockLogicalCoords = 0
        pickAttributes.showNodePhysicalCoords = 0
        pickAttributes.showZoneId = 1
        pickAttributes.showZoneDomainLogicalCoords = 0
        pickAttributes.showZoneBlockLogicalCoords = 0
        pickAttributes.doTimeCurve = 1
        pickAttributes.conciseOutput = 0
        pickAttributes.showTimeStep = 1
        pickAttributes.showMeshName = 1
        pickAttributes.blockPieceName = ""
        pickAttributes.groupPieceName = ""
        pickAttributes.showGlobalIds = 0
        pickAttributes.showPickLetter = 1
        pickAttributes.reusePickLetter = 0
        pickAttributes.meshCoordType = pickAttributes.XY  # XY, RZ, ZR
        pickAttributes.createSpreadsheet = 0
        pickAttributes.floatFormat = "%g"
        pickAttributes.timePreserveCoord = 1
        pickAttributes.timeCurveType = pickAttributes.Single_Y_Axis  # Single_Y_Axis, Multiple_Y_Axes
        #print("Setting NodePick attributes")
        print(visit.SetPickAttributes(pickAttributes))
        visit.SetWindowMode("zone pick")