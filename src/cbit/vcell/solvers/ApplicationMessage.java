package cbit.vcell.solvers;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (6/27/2001 2:40:13 PM)
 * @author: Ion Moraru
 */
public class ApplicationMessage {
	// types of message
	public static final int PROGRESS_MESSAGE = 1000;
	public static final int ERROR_MESSAGE = 1001;
	public static final int DATA_MESSAGE = 1002;
	// info
	private int messageType = -1;
	private double progress = -1;
	private double timepoint = -1;
	private String error = null;
	private String message = null;
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:44:48 PM)
 * @param progress double
 * @param error java.lang.String
 * @param message java.lang.String
 */
public ApplicationMessage(int messageType, double progress, double timepoint, String error, String message) {
	this.messageType = messageType;
	this.progress = progress;
	this.timepoint = timepoint;
	this.error = error;
	this.message = message;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:43:55 PM)
 * @return java.lang.String
 */
public java.lang.String getError() {
	return error;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:43:55 PM)
 * @return java.lang.String
 */
public java.lang.String getMessage() {
	return message;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:50:46 PM)
 * @return int
 */
public int getMessageType() {
	return messageType;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:50:46 PM)
 * @return int
 */
public String getMessageTypeName() {
	switch (messageType){
		case PROGRESS_MESSAGE: {
			return "Progress";
		}
		case ERROR_MESSAGE: {
			return "Error";
		}
		case DATA_MESSAGE: {
			return "Data";
		}
		default: {
			return "";
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:43:55 PM)
 * @return double
 */
public double getProgress() {
	return progress;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 3:02:55 PM)
 * @return double
 */
public double getTimepoint() {
	return timepoint;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/01 3:36:56 PM)
 * @return java.lang.String
 */
public String toString() {
	return "ApplicationMessage(type='"+getMessageTypeName()+"', progress='"+getProgress()+"', timepoint='"+getTimepoint()+"', error='"+getError()+"', message='"+getMessage()+"')";
}
}
