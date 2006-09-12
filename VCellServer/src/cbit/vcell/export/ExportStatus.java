package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2001 1:20:44 PM)
 * @author: Ion Moraru
 */
public class ExportStatus {
	private Long jobID;
	private String format;
	private JProgressBar progressBar;
	private Boolean complete;
	private String destination;
	private String resultSetID;
/**
 * ExportStatus constructor comment.
 */
public ExportStatus(long jobID, String resultSetID) {
	this.resultSetID = resultSetID;
	this.jobID = new Long(jobID);
	this.format = "";
	this.progressBar = new JProgressBar();
	progressBar.setStringPainted(true);
	this.complete = Boolean.FALSE;
	this.destination = "";
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:27:58 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getComplete() {
	return complete;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:27:58 PM)
 * @return java.lang.String
 */
public java.lang.String getDestination() {
	return destination;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 4:54:47 PM)
 * @return java.lang.String
 */
public java.lang.String getFormat() {
	return format;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:28:24 PM)
 * @return java.lang.Long
 */
public java.lang.Long getJobID() {
	return jobID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:27:58 PM)
 * @return javax.swing.JProgressBar
 */
public javax.swing.JProgressBar getProgressBar() {
	return progressBar;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2001 6:34:23 PM)
 * @return java.lang.String
 */
public java.lang.String getResultSetID() {
	return resultSetID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:27:58 PM)
 * @param newComplete java.lang.Boolean
 */
public void setComplete(java.lang.Boolean newComplete) {
	complete = newComplete;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 1:27:58 PM)
 * @param newDestination java.lang.String
 */
public void setDestination(java.lang.String newDestination) {
	destination = newDestination;
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2001 4:54:47 PM)
 * @param newFormat java.lang.String
 */
public void setFormat(java.lang.String newFormat) {
	format = newFormat;
}
}
