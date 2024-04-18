package org.vcell.sedml;


public record SEDMLTaskRecord(String identifier, String taskName, TaskType taskType, TaskResult taskResult, Exception exception) {
    public String getCSV() {
        return String.join(",", this.identifier, this.taskName, this.taskType.toString(), this.taskResult.toString(), this.getExecutionResult());
    }

    private String getExecutionResult(){
        return this.exception == null ? "null:null" : "\"" + this.exception.getClass().getName() + "\":\"" + this.exception.getMessage() + "\"";
    }
}

