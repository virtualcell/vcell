package cbit.vcell.export;
import cbit.util.DataAccessException;
import cbit.util.User;
import cbit.util.VCDataIdentifier;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.*;
import cbit.vcell.math.*;

import java.rmi.*;

/**
 * This type was created in VisualAge.
 */
public class SimulationDescription {
	private String name = null;
	private String dataType = null;
	private double times[] = null;
	private int variableNumber = 0;
	private int timeNumber = 0;
	private String variables[] = null;

/**
 * SimulationInfo constructor comment.
 */
public SimulationDescription(User user, DataServerImpl dataServerImpl, VCDataIdentifier vcdID) throws DataAccessException, RemoteException {
	super();

	String dataID = vcdID.getID();
	this.name = dataID;

	if (dataServerImpl.getIsODEData(user, vcdID)) {
		this.dataType = "ODE Simulation";
		cbit.vcell.simdata.ODESimData odeSimData = dataServerImpl.getODEData(user, vcdID);
		try {
			this.times = odeSimData.extractColumn(odeSimData.findColumn("t"));
		}catch (cbit.vcell.parser.ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("error getting time: "+e.getMessage());
		}
		this.timeNumber = times.length;
		this.variables = new String[odeSimData.getColumnDescriptionsCount()];
		for (int i=0;i<variables.length;i++) {
			variables[i] = odeSimData.getColumnDescriptions(i).getDisplayName();
		}
		this.variableNumber = variables.length;
	} else {
		this.dataType = "PDE Simulation";
		this.times = dataServerImpl.getDataSetTimes(user, vcdID);
		this.timeNumber = times.length;
		DataIdentifier dataIdentifiers[] = dataServerImpl.getDataIdentifiers(user, vcdID);
		this.variables = new String[dataIdentifiers.length];
		for (int i = 0; i < variables.length; i++){
			variables[i] = dataIdentifiers[i].getName();
		}
		this.variableNumber = variables.length;
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getHeader(String format) {
	String header = null;
	String line1 = null;
	String line2 = null;
	String line3 = null;
	String line4 = null;
	String line5 = null;
	if (format.equals(".csv")) {
		line1 = "Simulation name,," + name + ",," + dataType + "\n";
		line2 = "Time range,," + times[0] + "," + times[times.length - 1] + "\n";
		line3 = "Saved timepoints,," + timeNumber + "\n";
		line4 = "Number of variables,," + variableNumber + "\n";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < variables.length; i++) {
			buffer.append(variables[i] + ",");
		}
		line5 = "Variable names,," + buffer.toString() + "\n";
	}
	header = line1 + line2 + line3 + line4 + line5 + "\n";
	return header;
}
}