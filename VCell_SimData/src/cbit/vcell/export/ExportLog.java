package cbit.vcell.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2001 3:55:31 PM)
 * @author: Ion Moraru
 */
public class ExportLog implements Serializable {
	private ExportLogEntry[] exportLogEntries = new ExportLogEntry[0];
	private String simulationID = null;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 3:56:22 PM)
 * @param exportLogFile java.io.File
 */
public ExportLog(cbit.util.KeyValue argSimulationRef,ExportLogEntry[] argExportLogEntries){

	this.simulationID = argSimulationRef.toString();
	this.exportLogEntries = argExportLogEntries;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 3:56:22 PM)
 * @param exportLogFile java.io.File
 */
public ExportLog(File exportLogFile) throws FileNotFoundException, IOException {
	// if it seems it's the right stuff, try to read it
	if (exportLogFile != null && exportLogFile.exists() && exportLogFile.isFile() && exportLogFile.getName().endsWith(".export")) {
		LineNumberReader reader = new LineNumberReader(new FileReader(exportLogFile));
		Vector entries = new Vector();
		String line = reader.readLine();
		while (line != null) {
			entries.add(new ExportLogEntry(line));
			line = reader.readLine();
		}
		setExportLogEntries((ExportLogEntry[])cbit.util.BeanUtils.getArray(entries, ExportLogEntry.class));
		String fileName = exportLogFile.getName();
		String prefix = "SimID_";
		String suffix = ".export";
		if (fileName.indexOf(prefix)<0){
			throw new RuntimeException("unexpected filename = '"+fileName+"', should contain '"+prefix+"'");
		}
		if (fileName.indexOf(suffix)<0){
			throw new RuntimeException("unexpected filename = '"+fileName+"', should contain '"+suffix+"'");
		}
		this.simulationID = fileName.substring(fileName.lastIndexOf(prefix)+prefix.length(),fileName.lastIndexOf(suffix));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:35:12 PM)
 * @return cbit.vcell.export.server.ExportLogEntry[]
 */
public ExportLogEntry[] getExportLogEntries() {
	return exportLogEntries;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/01 3:34:22 PM)
 * @return java.lang.String
 */
private String getSimulationID() {
	return simulationID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 8:12:06 PM)
 * @return java.lang.String
 */
public String getSimulationIdentifier() {
	return "SimID_" + getSimulationID();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/01 4:22:59 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.KeyValue getSimulationKey() {
	return new cbit.util.KeyValue(getSimulationID());
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:35:12 PM)
 * @param newExportLogEntries cbit.vcell.export.server.ExportLogEntry[]
 */
private void setExportLogEntries(ExportLogEntry[] newExportLogEntries) {
	exportLogEntries = newExportLogEntries;
}
}
