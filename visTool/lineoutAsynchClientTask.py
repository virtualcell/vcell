import visit
from asynchClientTask import *

class LineoutAsynchClientTask(AsynchClientTask):

    INTERNALSTATE_1_CHECK_DATA =    "check data"
    INTERNALSTATE_2_CLEANUP =       "cleanup"
    INTERNALSTATE_3_CHECK_CLEANUP = "check cleanup"

    def __init__(self, startPoint, endPoint, dataReadyCallback,  onErrorCallback):
        super(LineoutAsynchClientTask, self).__init__("Lineout", True, dataReadyCallback, onErrorCallback)
        assert isinstance(startPoint, tuple)
        assert len(startPoint) in [2,3]
        #assert isinstance(startPoint,(int,float,long))
        assert isinstance(endPoint,tuple)
        assert len(endPoint) in [2,3]
        #assert isinstance(endpoint,(int,float,long))
        print("lineout: passed assertions")
        self._startPoint = startPoint
        self._endPoint = endPoint
        
        self._lineOutData = None
        self._state = AsynchClientTask.STATE_INITIAL
        print("lineout initial state = "+str(self.getTaskState()))

        self.stateFunctions = {
            AsynchClientTask.STATE_EXCEPTION:               self.foundError, 
            AsynchClientTask.STATE_INITIAL:                 self.doLineout, 
            INTERNALSTATE_1_CHECK_DATA:                     self.isDataReady,
            INTERNALSTATE_2_CLEANUP:                        self.doCleanup,
            INTERNALSTATE_3_CHECK_CLEANUP:                  self.isCleanupDone,
            AsynchClientTask.STATE_DONE:                    self.cleanupIsDone}

    def checkState(self):

        print("lineout checking state. =="+str(self._state))
        try:
            self.stateFunctions[self.getTaskState()]()
        except: 
            print("EXCEPTION CHECKING LINEOUT'S STATE.  Prior state was: "+str(self._state))
            self._state = AsynchClientTask.STATE_EXCEPTION

    def doLineout(self):

        self._state = INTERNALSTATE_1_CHECK_DATA
        try:
            print("DO LINEOUT")
            if (2 in visit.GetGlobalAttributes().GetWindows()):
                visit.SetActiveWindow(2)
                assert visit.GetNumPlots()==0 
            print("passed assertion that Window2 doesn't exist yet or has no plots")
            print(visit.SetActiveWindow(1))
            visit.Lineout(self._startPoint, self._endPoint)
        except:
            print("EXCEPTION IN doLineout")
            self._state = AsynchClientTask.STATE_EXCEPTION
        finally:
            visit.SetActiveWindow(1)

    def isDataReady(self):
        try:
            visit.SetActiveWindow(2)
            if visit.GetNumPlots()==1:
                print("Lineout data ready\n")
                self._lineOutData = visit.GetPlotInformation()['Curve']
                print("The data from the lineout from (0,0,0) to (1,1,1) is:\n")
                print("\n")
                print(str(self._lineOutData))
                print("\n")
                if isinstance(self._lineOutData, tuple):
                    self._state = INTERNALSTATE_2_CLEANUP
                    self._onDataIsReady(self._lineOutData) # trigger callback
                    return True
                else:
                    self._state = AsynchClientTask.STATE_EXCEPTION
                    return False
            else:
                return false
        except:
            self._state = AsynchClientTask.STATE_EXCEPTION
            return None

    def getLineoutData(self):
        assert self._state in [ INTERNALSTATE_2_CLEANUP, INTERNALSTATE_3_CHECK_CLEANUP, AsynchClientTask.STATE_DONE ]
        return self._lineOutData

    def doCleanup(self):
        try:
            assert self._state == INTERNALSTATE_2_CLEANUP
            self._state = INTERNALSTATE_2_CLEANUP
            visit.SetActiveWindow(2)
            assert visit.GetNumPlots()!=0 
            visit.ClearWindow()
            visit.DeleteAllPlots()
            visit.SetActiveWindow(1)
            self._state = INTERNALSTATE_3_CHECK_CLEANUP
        except:
            self._state = AsynchClientTask.STATE_EXCEPTION
        finally:
            visit.SetActiveWindow(1)

    def isCleanupDone(self):
        assert self._state == AsynchClientTask.STATE_3_CLEANUP_REQUESTED
        try:
            visit.SetActiveWindow(2)
            if visit.GetNumPlots()==0:
                self._state = AsynchClientTask.STATE_DONE
                visit.SetActiveWindow(1)
                return True
            else:
                SetActiveWindow(1)
                return False
        except:
            self._state = AsynchClientTask.STATE_EXCEPTION
            return None
 
    def cleanupIsDone(self):
        pass # Someone should call getLineoutData if they haven't already and let me be forgotton memory anytime now

    def foundError(self):
        self._onExceptionEncountered("lineOutAsychClientTask error encountered")