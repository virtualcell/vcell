/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.server;
import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.ode.ODESimData;

/**
 * This type was created in VisualAge.
 */
/**
 * This class has been modified April 2,2007.
 * Since we've added more simulaiton methods such as stochastic and monte carlo(multiple stoch simulation),
 * we have to give different description when exporting the data file.
 * Data type can be "ODE Simulation", "PDE Simulation", "Stochastic Simulation" and "Monte Carlo Simulation".
 * The time-serie data will have time series output and the monte carlo simulation will have data based on trial number.
 */
public class SimulationDescription {
	private String name = null;
	private String dataType = null;
	private double times[] = null;
	private int variableNumber = 0;
	private int timeNumber = 0;
	private String variables[] = null;
	private boolean bMultiTrialData = false;

/**
 * SimulationInfo constructor comment. 
 * @param variableNames TODO
 */
public SimulationDescription(OutputContext outputContext,User user, DataServerImpl dataServerImpl, 
		VCDataIdentifier vcdID, boolean isODEData, String[] variableNames) throws DataAccessException, RemoteException {
	super();

	String dataID = vcdID.getID();
	this.name = dataID;

	if (isODEData) {
		ODESimData odeSimData = dataServerImpl.getODEData(user, vcdID);
		//set times and variables
		try {
			if(odeSimData.findColumn("t") > -1)
			{
				this.dataType = "ODE/Stochastic Simulation in which data comes by time series.";
				this.times = odeSimData.extractColumn(odeSimData.findColumn("t"));
				this.variables = new String[odeSimData.getColumnDescriptionsCount()];
				for (int i=0;i<variables.length;i++) {
					variables[i] = odeSimData.getColumnDescriptions(i).getDisplayName();
				}
			}
			else if(odeSimData.findColumn("TrialNo") > -1)
			{
				bMultiTrialData = true;
				this.dataType = "Monte Carlo Simulation (multiple stochastic simulations) in which data comes by trial numbers. ";
				this.times = odeSimData.extractColumn(odeSimData.findColumn("TrialNo"));
				this.variables = new String[odeSimData.getColumnDescriptionsCount()-1];
				for (int i=0;i<variables.length;i++) { //trail no is the first column, we don't need it
					variables[i] = odeSimData.getColumnDescriptions(i+1).getDisplayName();
				}
			}
			else throw new RuntimeException("error getting time or trial number in simulation. ");
			
		}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("error getting time: "+e.getMessage());
		}
		this.timeNumber = times.length;
		this.variableNumber = variables.length;
	} else {
		this.dataType = "PDE Simulation";
		this.times = dataServerImpl.getDataSetTimes(user, vcdID);
		this.timeNumber = times.length;
		if (variableNames == null) {
			DataIdentifier dataIdentifiers[] = dataServerImpl.getDataIdentifiers(outputContext,user, vcdID);
			this.variables = new String[dataIdentifiers.length];
			for (int i = 0; i < variables.length; i++){
				variables[i] = dataIdentifiers[i].getName();
			}
		}
		else {
			this.variables = variableNames;
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
	String line3 = "";
	String line4 = null;
	String line5 = null;
	if (format.equals(".csv")) {
		line1 = "("+name +" ("+ dataType + "))\n";
		if(bMultiTrialData)
		{
			line2 = "Trial range,," + times[0] + "," + times[times.length - 1] + "\n";
			line3 = "Total trials,," + timeNumber + "\n";
		}
		else
		{
			line2 = "\"Sim time range (" + times[0] + " " + times[times.length - 1] + ") (saved timepoints "+timeNumber+")\"\n";
//			line3 = "Saved timepoints,," + timeNumber + "\n";
		}
		line4 = "\"Number of variables (" + variableNumber + ")\"\n";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < variables.length; i++) {
			buffer.append(",\""+variables[i]+"\"");
		}
		line5 = "\"Variable names\"" + buffer.toString() + "\n";
	}
	header = line1 + line2 + line3 + line4 + line5 + "\n";
	return header;
}
}
