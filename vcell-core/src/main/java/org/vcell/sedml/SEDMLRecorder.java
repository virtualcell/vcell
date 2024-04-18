package org.vcell.sedml;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

import java.io.IOException;

import org.vcell.util.VCellUtilityHub;
import org.vcell.util.recording.Recorder;
import org.vcell.util.recording.TextFileRecord;
import org.vcell.util.recording.GsonExceptionSerializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Sedml-specific recorder class
 */
public class SEDMLRecorder extends Recorder {

	private final static Logger logger = LogManager.getLogger(SEDMLRecorder.class);
	
	private final String identifier;
	private final SEDMLConversion operation;
	private final List<SEDMLTaskRecord> taskLogs = new LinkedList<>();
	private final transient Set<Class<? extends Exception>> exceptionTypes;
	private final transient TextFileRecord jsonFile;
	

	/**
	 * Creates a recorder object with no correlated json
	 * 
	 * @param jobName name of the job this recorder is recording
	 * @param conversion whether the sedml is imported or exported
	 */
	public SEDMLRecorder(String jobName, SEDMLConversion conversion) {
		this(jobName, conversion, null);
	}

	/**
	 * Creates a recorder object with output to a json.
	 * 
	 * @param jobName name of the job this recorder is recording
	 * @param conversion whether the sedml is imported or exported
	 * @param jsonFilePath path to the json file to be created.
	 */
	public SEDMLRecorder(String jobName, SEDMLConversion conversion, String jsonFilePath) {
		super(SEDMLRecorder.class);
		this.identifier = jobName;
		this.operation = conversion;
		this.exceptionTypes = new HashSet<>();
		if (jsonFilePath != null) {
			this.jsonFile = VCellUtilityHub.getLogManager().requestNewRecord(jsonFilePath);
		} else {
			this.jsonFile = null;
		}
	}

	/**
	 * Gets the job name of this recorder
	 * @return the job name / identifier
	 */
	public String getIdentifier(){
		return this.identifier;
	}

	/**
	 * Get the operation type as a string
	 * @return whether the sedml recorded is for import or export.
	 */
	public String getOperationAsString(){
        return switch (this.operation) {
			case EXPORT -> "Export";
			case IMPORT -> "Import";
        };
	}

	/**
	 * Gets any already written records
	 * @return the task logs
	 */
	public List<SEDMLTaskRecord> getRecords() {
		return this.taskLogs;
	}

	/**
	 * Checks if any failure has been recorded
	 * @return if any failure is recorded
	 */
	public boolean hasErrors() {
		for (SEDMLTaskRecord taskLog : taskLogs) 
			if (taskLog.taskResult() == TaskResult.FAILED)
				return true;
		return false; 
	}

	/**
	 * Adds a new record
	 * 
	 * @param taskName name of the task to record
	 * @param taskType type of the task to record
	 * @param taskResult what the task resulted in
	 * @param exception if an expection was recorded
	 */
	public void addTaskRecord(String taskName, TaskType taskType, TaskResult taskResult, Exception exception) {
		this.taskLogs.add(new SEDMLTaskRecord(identifier, taskName, taskType, taskResult, exception));
		this.addException(exception);
	}

	/**
	 * Get the records in csv formatted string
	 * 
	 * @return the csv formatted string
	 */
	public String getRecordsAsCSV() {
		StringBuilder lines = new StringBuilder("\n-----------START-EXPORT-LOG-CSV-----------\n");
		lines.append(this.identifier).append(",").append(this.operation).append("\n");
		for (SEDMLTaskRecord taskLog : taskLogs) {
			lines.append(taskLog.getCSV()).append("\n");
		}
		lines.append("------------END-EXPORT-LOG-CSV------------\n");
		return lines.toString();
	}

	/**
	 * If initialized with a json path, export the records to a json file.
	 * Regardless, return the records in json format.
	 * 
	 * @return the records in json-formatted string
	 */
	public String exportToJSON() {
		Gson gson; GsonBuilder builder;
		String jsonPrefix;

		// Gson construction
		builder = new GsonBuilder(); // Now for the custom serializers
		builder.registerTypeAdapter(this.getClass(), new GsonSEDMLRecorderSerializer());
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

	private <T extends Exception> void addException(T e){
		if (e != null) this.exceptionTypes.add(e.getClass());
	}
}
