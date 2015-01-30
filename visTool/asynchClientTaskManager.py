import PySide.QtCore as QtCore
from asynchClientTask import AsynchClientTask

class AsynchClientTaskManager(object):

    def __init__(self, parent):
        self._asynchClientTaskList=[]
        self._timer = QtCore.QTimer(parent)
        self._timer.setInterval(100)
        self._timer.connect(QtCore.SIGNAL("timeout()"),self.checkTasks)
        self._timer.start()                    

    def checkTasks(self):
        #print("Checking Tasks.  There are "+str(len(self._asynchClientTaskList))+" tasks in the list now")
        for task in self._asynchClientTaskList:
            try:
                print("Checking state of task: "+task.getTaskName()) 
                
                # DELETE task when it is completely done.  We may want to make this a togglable feature. 
                task.checkState()
                if task.getTaskState() in [AsynchClientTask.STATE_EXCEPTION, AsynchClientTask.STATE_DONE]:
                    #print("about to delete finished or bolluxed task")
                    self.deleteTask(task)
                    #print("Done task deleted.")
                    break

            except:
                raise Exception("Problem checking state of a "+str(task.__class__.__name__))

            if (task is not None) and (task.isBlocking() and not task.isFinished()):
                print("waiting for other tasks to finish")
                break

    def addTask(self, asynchClientTask):
        try:

            assert isinstance(asynchClientTask, AsynchClientTask)
            assert not (asynchClientTask in self._asynchClientTaskList)
            self._asynchClientTaskList.append(asynchClientTask)
        except:
            raise Exception("Problem adding new AsynchClientTask to asynchClientTaskList")

    def deleteTask(self, asynchClientTask):
        try:
            print("entering deleteTask")
            assert isinstance(asynchClientTask, AsynchClientTask)
            assert (asynchClientTask in self._asynchClientTaskList)
            print("deleteTask: passed assertions")
            self._asynchClientTaskList.remove(asynchClientTask)
        except:
            raise Exception("Problem removing a AsynchClientTask from asynchClientTaskList")

