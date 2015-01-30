


class AsynchClientTask(object):

    STATE_INITIAL = "initial"
    STATE_DONE = "done"
    STATE_EXCEPTION = "exception"

    def __init__(self, taskName, bBlocking, callbackOnSuccess = None, callbackOnError = None):
        assert isinstance(taskName, str)
        assert isinstance(bBlocking, bool)
        self._taskName = taskName
        self._bBlocking = bBlocking
        self._state = AsynchClientTask.STATE_INITIAL
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
        return self._bBlocking

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
        if self.getTaskState()==AsynchClientTask.STATE_DONE:
            return True
        else:
            return False