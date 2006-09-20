package cbit.vcell.export.server;

import java.util.*;
import cbit.rmi.event.*;
import cbit.vcell.export.ExportEvent;

import java.net.*;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 4:12:10 PM)
 * @author: Ion Moraru
 */
public class ExportLogEntry implements java.io.Serializable {
	private String format = null;
	private URL location = null;
	private cbit.util.KeyValue simulationRef = null;
	private cbit.util.KeyValue eleKey = null;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:14:08 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public ExportLogEntry(ExportEvent event) {
	if (event != null) {
		setFormat(event.getFormat());
		try {
			setLocation(new URL(event.getLocation()));
		} catch (MalformedURLException exc) {
			// too bad...
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:19:58 PM)
 * @param lineEntry java.lang.String
 */
public ExportLogEntry(String lineEntry) {
	if (lineEntry != null) {
		StringTokenizer tokenizer = new StringTokenizer(lineEntry, ",");
		setFormat(tokenizer.nextToken());
		try {
			setLocation(new URL(tokenizer.nextToken()));
		} catch (MalformedURLException exc) {
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:19:58 PM)
 * @param lineEntry java.lang.String
 */
public ExportLogEntry(String argFormat,URL argLocation,cbit.util.KeyValue argSimulationRef,cbit.util.KeyValue argELEKey) {
	
	setFormat(argFormat);
	setLocation(argLocation);
	simulationRef = argSimulationRef;
	eleKey = argELEKey;
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 4:40:18 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.KeyValue getELEKey() {
	return eleKey;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:13:40 PM)
 * @return java.lang.String
 */
public java.lang.String getFormat() {
	return format;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:13:40 PM)
 * @return URL
 */
public URL getLocation() {
	return location;
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 4:39:20 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.KeyValue getSimulationRef() {
	return simulationRef;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:13:40 PM)
 * @param newFormat java.lang.String
 */
private void setFormat(java.lang.String newFormat) {
	format = newFormat;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:13:40 PM)
 * @param newLocation URL
 */
private void setLocation(URL newLocation) {
	location = newLocation;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:18:40 PM)
 * @return java.lang.String
 */
public String toString() {
	return (getFormat() + "," + getLocation());
}
}
