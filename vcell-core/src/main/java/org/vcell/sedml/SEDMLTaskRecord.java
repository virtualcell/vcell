package org.vcell.sedml;


public class SEDMLTaskRecord {
    
    private String identifier; 
    private String taskName; 
    private TaskType taskType; 
    private TaskResult taskResult; 
    private Exception exception;

    public SEDMLTaskRecord(){
    	this.identifier = "";
        this.taskName = "";
        this.taskType = null;
        this.taskResult = null;
        this.exception = null;
    }

    public SEDMLTaskRecord(String identifier, String taskName, TaskType taskType, TaskResult taskResult, Exception exception) {
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskResult = taskResult;
        this.exception = exception;
        this.identifier = identifier;
    }

    public TaskResult getTaskResult(){
        return this.taskResult;
    }
    
    public Exception getException(){
        return this.exception;
    }

    public String getCSV() {
        return String.join(",", this.identifier, this.taskName, this.taskType.toString(), this.taskResult.toString(), this.getExecutionResult());
    }

    private String getExecutionResult(){
        return this.exception == null ? "null:null" : "\"" + this.exception.getClass().getName() + "\":\"" + this.exception.getMessage() + "\"";
    }
}
