package org.vcell.sedml;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import java.io.IOException;

import org.vcell.util.VCellUtilityHub;
import org.vcell.util.recording.Recorder;
import org.vcell.util.recording.FileRecord;
import org.vcell.util.recording.GsonExceptionSerializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SEDMLLogger extends Recorder {

	private final static Logger logger = LogManager.getLogger(SEDMLLogger.class);
	
	private String identifier; 
	private SEDMLConversion operation;
	private List<SEDMLTaskRecord> taskLogs = new LinkedList<>();
	private transient FileRecord jsonFile;

	public SEDMLLogger(String jobId, SEDMLConversion conversion) {
		super(SEDMLLogger.class);
		this.identifier = jobId;
		this.operation = conversion;
		this.jsonFile = null;
	}

	public SEDMLLogger(String jobName, SEDMLConversion conversion, String jsonFilePath) {
		super(SEDMLLogger.class);
		this.identifier = jobName;
		this.operation = conversion;
		if (jsonFilePath != null) this.jsonFile = VCellUtilityHub.getLogManager().requestNewFileLog(jsonFilePath);
		else this.jsonFile = null;
	}

	public String getIdentifier(){
		return this.identifier;
	}

	public String getOperationAsString(){
		String result;
		switch(this.operation){
			case EXPORT:
				result = "Export";
				break;
			case IMPORT:
				result = "Import";
				break;
			default:
				result = null;
				break;
		}
		return result;
	}

	public List<SEDMLTaskRecord> getLogs() {
		return this.taskLogs;
	}

	public boolean hasErrors() {
		for (SEDMLTaskRecord taskLog : taskLogs) 
			if (taskLog.getTaskResult() == TaskResult.FAILED) 
				return true;
		return false; 
	}

	public void addTaskLog(String taskName, TaskType taskType, TaskResult taskResult, Exception exception) {
		this.taskLogs.add(new SEDMLTaskRecord(taskName, taskType, taskResult, exception));
	}

	public String getLogsCSV() {
		String lines = "\n-----------START-EXPORT-LOG-CSV-----------\n";
		lines += this.identifier + "," + this.operation + "\n";
		for (SEDMLTaskRecord taskLog : taskLogs) {
			lines += taskLog.getCSV() + "\n";
		}
		lines += "------------END-EXPORT-LOG-CSV------------\n";
		return lines;
	}

	public String exportToJSON() {
		Gson gson; GsonBuilder builder;
		String jsonPrefix;
		Set<Class<? extends Exception>> exceptionTypes = new HashSet<>();
		for(SEDMLTaskRecord task : this.taskLogs)
			if (task.getException() != null) 
				exceptionTypes.add(task.getException().getClass());

		// Gson construction
		builder = new GsonBuilder(); // Now for the custom serializers
		builder.registerTypeAdapter(this.getClass(), new GsonSEDMLLoggerSerializer());
		builder.registerTypeAdapter(Exception.class, new GsonExceptionSerializer());
		for (Class<? extends Exception> clazz : exceptionTypes) // we must register *every* exception we had this way
			builder.registerTypeAdapter(clazz, new GsonExceptionSerializer());
		builder.setPrettyPrinting().disableHtmlEscaping();
		
		// Gson creation
		gson = builder.create();
		jsonPrefix = gson.toJson(this);
		if (this.jsonFile != null) try {
			this.jsonFile.print(jsonPrefix);
		} catch (IOException e){
			logger.warn(String.format("Unable to export json report to file <%s>", this.jsonFile.getPath()), e);
		}
		return jsonPrefix;
	}
}
