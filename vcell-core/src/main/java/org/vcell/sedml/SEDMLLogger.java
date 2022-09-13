package org.vcell.sedml;

import java.util.ArrayList;

public class SEDMLLogger {
	public class TaskLog {
		private String taskName; private TaskType taskType; private TaskResult taskResult; private Exception exception;
		public TaskLog(String taskName, TaskType taskType, TaskResult taskResult, Exception exception) {
			this.taskName = taskName;
			this.taskType = taskType;
			this.taskResult = taskResult;
			this.exception = exception;
		}
		public String getCSV() {
			String excString = exception == null ? "null:null" : "\""+exception.getClass().getName()+"\":\""+exception.getMessage()+"\"";
			return identifier+","+taskName+","+taskType+","+taskResult+","+excString;
		}
		public String toString() {
			return getCSV();
		}
	}
	private ArrayList<TaskLog> taskLogs = new ArrayList<TaskLog>();
	private String identifier; private SEDMLConversion operation;
	public SEDMLLogger(String jobName, SEDMLConversion conversion) {
		identifier = jobName;
		operation = conversion;
	}
	public void addTaskLog(TaskLog taskLog) {
		taskLogs.add(taskLog);
	}
	public String getLogsCSV() {
		String lines = "\n-----------START-EXPORT-LOG-CSV-----------\n";
		lines += identifier+","+operation+"\n";
		for (TaskLog taskLog : taskLogs) {
			lines += taskLog.getCSV()+"\n";
		}
		lines += "------------END-EXPORT-LOG-CSV------------\n";
		return lines;
	}
	public boolean hasErrors() {
		for (TaskLog taskLog : taskLogs) {
			if (taskLog.taskResult == TaskResult.FAILED) return true;
		}
		return false; 
	}
	public String getLogsJSON() {
		// TODO
		return "";
	}
	public ArrayList<TaskLog> getLogs() {
		return taskLogs;
	}

}
