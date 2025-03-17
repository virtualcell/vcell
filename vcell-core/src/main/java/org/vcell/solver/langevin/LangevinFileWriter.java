/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.langevin;

import java.io.PrintWriter;

import org.apache.commons.io.output.WriterOutputStream;
import org.jdom2.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.server.SolverFileWriter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;

/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 */
public class LangevinFileWriter extends SolverFileWriter 
{
	private long randomSeed = 0; //value assigned in the constructor
	
public LangevinFileWriter(PrintWriter pw, SimulationTask simTask, boolean bMessaging) 
{
	super(pw, simTask, bMessaging);
}

@Override
public void write(String[] parameterNames) throws Exception {	
	String langevinLngvString = LangevinLngvWriter.writeLangevinLngv(simTask.getSimulation(), randomSeed);
	
	printWriter.write(langevinLngvString);
	printWriter.flush();
}

public static void main(String[] args) {
	try {
		
		System.out.println("Done");
	} catch (Exception e) {
		e.printStackTrace();
	}
}


}
