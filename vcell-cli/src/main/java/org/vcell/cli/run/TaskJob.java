package org.vcell.cli.run;


public class TaskJob {
    final int jobID;
    final String taskID; // base task for a simulation (instance of Task)

    public TaskJob(String taskID, int jobID) {
        this.taskID = taskID;
        this.jobID = jobID;
    }

    public String getId() {
        return taskID + "_" + jobID;
    }

    public String getTaskId() {
        return taskID;
    }

    public String toString() {
        return getId();
    }

    @Override
    public int hashCode() {
        return taskID.hashCode() + jobID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskJob) {
            TaskJob other = (TaskJob) obj;
            return (taskID.equals(other.taskID) && jobID == other.jobID);
        }
        return false;
    }

}
