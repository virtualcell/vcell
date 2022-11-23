import PySide.QtCore as QtCore
import PySide.QtGui as QtGui
from PySide.QtGui import QApplication
import exceptions
import sys

######################################################################
# AsynchTaskManager stores and executes tasks
######################################################################

class AsynchTaskManager(object):

    def __init__(self, parent):
        self._asynchTaskList=[]
        self._timer = QtCore.QTimer(parent)
        self._timer.setInterval(50)
        self._timer.connect(QtCore.SIGNAL("timeout()"),self.doNextTaskStep)
        self._timer.start()                    

    def doNextTaskStep(self):
        ##
        ## manage system cursor here (should probably be done in the GUI directly).
        ##
        #if (len(self._asynchTaskList) > 0):
        #    print("Checking Tasks.  There are "+str(len(self._asynchTaskList))+" tasks in the list now")
        #    QApplication.setOverrideCursor(QtGui.QCursor(QtCore.Qt.WaitCursor))
        #else:
        #    QApplication.restoreOverrideCursor()

        #
        # take a single step of the task on 'top of stack'
        #
        if len(self._asynchTaskList):
            task = self._asynchTaskList[0]
            try:
                print "doStep() task '" + task.getTaskName() + "'"
                bIsDone = task.doStep()
                if bIsDone:
                    self.onSuccess(task)
                    self.deleteTask(task)   # remove task if done

            except: # catch all exceptions
                self.onError(task, str(sys.exc_info()[0]))  # invoke error callback
                self.deleteTask(task)  # remove task if exception

    def addTask(self, asynchTask):
        assert isinstance(asynchTask, AsynchTask)
        assert not (asynchTask in self._asynchTaskList)
        self._asynchTaskList.append(asynchTask)

    def deleteTask(self, asynchTask):
        assert isinstance(asynchTask, AsynchTask)
        assert (asynchTask in self._asynchTaskList)
        self._asynchTaskList.remove(asynchTask)

    def onSuccess(self, task):
        assert isinstance(task, AsynchTask)
        print("onSuccess for task '"+task._taskName+"'")
        if (task._callbackOnSuccess is not None) and (not task._bCallbackInvoked):
            task._bCallbackInvoked = True
            print("invoking OnSuccess callback")
            task._callbackOnSuccess(task.getResults())

    def onError(self, task, exceptionMessage):
        assert isinstance(task, AsynchTask)
        print("_onError called with message = " + exceptionMessage)
        if (task._callbackOnError is not None) and (not task._bCallbackInvoked):
            task._bCallbackInvoked = True
            if (exceptionMessage is not None): 
                task._callbackOnError(exceptionMessage)
            else:
                task._callbackOnError()



######################################################################
# AsynchTask is abstract superclass for all tasks
######################################################################

class AsynchTask(object):

    def __init__(self, taskName, callbackOnSuccess = None, callbackOnError = None):
        assert isinstance(taskName, str)
        self._taskName = taskName
        self._callbackOnSuccess = callbackOnSuccess
        self._callbackOnError = callbackOnError
        self._bCallbackInvoked = False

    def getTaskName(self):
        return self._taskName

    #-------------------------------------------------------------------------------------------------
    # subclasses override doStep() to perform single operation
    # and update _state or throw exception if there is a problem
    #
    # if this task is top of stack, doStep() gets called once per timer interrupt on the Qt Thread.
    # when task is done, set _state to STATE_DONE
    #
    # returns True if done, returns False if more steps needed.
    #-------------------------------------------------------------------------------------------------
    def doStep(self):
        raise NotImplementedError() 

    def getResults(self):
        raise NotImplementedError()

