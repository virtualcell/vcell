class AsynchClientTask(object):

    def __init__(self, taskName, taskType, callbackOnSuccess = None, callbackOnError = None):
        assert isinstance(taskName, str)
        assert isinstance(taskType, int) and (taskType in [1,2,3])
        self._taskName = taskName
        self._taskType = taskType
        self._state = 0  # 0 = No request yet. 1 = request initiated 2 = data ready, 3 = cleanup requested, 4 = cleanup done, -1 = exception encountered 
        self._callbackOnSuccess = callbackOnSuccess
        self._callbackOnError = callbackOnError
        self._bSucceededOrFailedAlready = False

    def getTaskName(self):
        return self._taskName

    def getTaskType(self):
        return self._taskType

    def getTaskState(self):
        return self._state

    def checkState(self):
        pass

    def isBlocking(self):
        if self.getTaskType() in [1,3]:
            return True
        else:
            return False

    def isDataReady(self):
        pass

    def _onDataIsReady(self, returnedData):
        print("onDataIsReadyCalled")
        if (self._callbackOnSuccess is not None) and (not self._bSucceededOrFailedAlready):
            self._bSucceededOrFailedAlready = True
            print("_onDataIsReady: just set self._bSucceededOrFailedAlready = True")
            print(str(self._callbackOnSuccess))
            print(str(returnedData))
            self._callbackOnSuccess(returnedData)
            print("just executed self._callbackOnSuccess(returnedData)")

    def _onExceptionEncountered(self, exceptionMessage = None):
        print("onExceptionEncounteredCalled")
        if (not self._bSucceededOrFailedAlready):
            self._bSucceededOrFailedAlready = True
            if (exceptionMessage is not None): 
                self._callbackOnError(exceptionMessage)
            else:
                self._callbackOnError()

    def isFinished(self):
        if self.getTaskState()==4:
            return True
        else:
            return False