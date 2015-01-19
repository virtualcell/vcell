/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.jdom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.server.SolverFileWriter;


/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class NFSimFileWriter extends SolverFileWriter 
{
	private long randomSeed = 0; //value assigned in the constructor
	private RandomDataGenerator dist = new RandomDataGenerator();
	
	private File inputFile = null;
	private String baseFileName = null;
	
	
public NFSimFileWriter(String baseName, SimulationTask simTask, boolean bMessaging) 
{
	super(null, simTask, bMessaging);
	baseFileName = baseName;
	this.inputFile = new File(baseFileName + SimDataConstants.NFSIM_INPUT_FILE_EXTENSION); 
	
	//get user defined random seed. If it doesn't exist, we assign system time (in millisecond) to it.
	NFsimSimulationOptions smoldynSimulationOptions = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
	if (smoldynSimulationOptions.getRandomSeed() != null) {
		this.randomSeed = smoldynSimulationOptions.getRandomSeed();
	} else {
		this.randomSeed = System.currentTimeMillis();
	}
	//We add jobindex to the random seed in case there is a parameter scan.
	randomSeed = randomSeed + simTask.getSimulationJob().getJobIndex();
	dist.reSeed(randomSeed);
}

@Override
public void write(String[] parameterNames) throws Exception {	
	FileOutputStream fos = new FileOutputStream(inputFile);
	try {
		NFsimSimulationOptions nfsimSimulationOptions = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
		Element root = NFsimXMLWriter.writeNFsimXML(simTask, randomSeed, nfsimSimulationOptions);
		XmlUtil.writeXmlToStream(root, false, fos);
	}finally{
		if (fos!=null){
			fos.close();
		}
	}
}


}
