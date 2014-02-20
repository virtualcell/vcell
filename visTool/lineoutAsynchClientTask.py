import visit
from asynchClientTask import *

class LineoutAsynchClientTask(AsynchClientTask):

    def __init__(self, startPoint, endPoint, dataReadyCallback,  onErrorCallback):
        super(LineoutAsynchClientTask, self).__init__("Lineout",1, dataReadyCallback, onErrorCallback)
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
        self._state = 0
        print("lineout initial state = "+str(self.getTaskState()))

        self.stateTransitions = {-1: self.foundError, 0 : self.doLineout, 1: self.isDataReady, 2: self.doCleanup, 3: self.isCleanupDone, 4: self.cleanupIsDone}

    def checkState(self):

        print("lineout checking state. =="+str(self._state))
        try:
            self.stateTransitions[self.getTaskState()]()
        except: 
            print("EXCEPTION CHECKING LINEOUT'S STATE.  Prior state was: "+str(self._state))
            self._state = -1

    def doLineout(self):

        self._state = 1
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
            self._state = -1
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
                    self._state = 2
                    self._onDataIsReady(self._lineOutData) # trigger callback
                    return True
                else:
                    self._state = -1
                    return False
            else:
                return false
        except:
            self._state = -1
            return None

    def getLineoutData(self):
        assert self._state > 1
        return self._lineOutData

    def doCleanup(self):
        try:
            assert self._state>0 and self._state<3
            self._state = 2
            visit.SetActiveWindow(2)
            assert visit.GetNumPlots()!=0 
            visit.ClearWindow()
            visit.DeleteAllPlots()
            visit.SetActiveWindow(1)
            self._state = 3
        except:
            self._state = -1
        finally:
            visit.SetActiveWindow(1)

    def isCleanupDone(self):
        assert self._state == 3
        try:
            visit.SetActiveWindow(2)
            if visit.GetNumPlots()==0:
                self._state = 4
                visit.SetActiveWindow(1)
                return True
            else:
                SetActiveWindow(1)
                return False
        except:
            self._state = -1
            return None
 
    def cleanupIsDone(self):
        pass # Someone should call getLineoutData if they haven't already and let me be forgotton memory anytime now

    def foundError(self):
        self._onExceptionEncountered("lineOutAsychClientTask error encountered")