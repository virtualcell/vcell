/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.server;
import java.io.PrintWriter;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.messaging.server.SimulationTask;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public abstract class SolverFileWriter {
	protected PrintWriter printWriter = null;
	protected boolean bUseMessaging = true;
	protected final SimulationTask simTask;
	
	enum SolverInputFileKeyword {
		JMS_PARAM_BEGIN,
		JMS_BROKER,
		JMS_USER,
		JMS_QUEUE,
		JMS_TOPIC,
		VCELL_USER,
		SIMULATION_KEY,
		JOB_INDEX,
		JMS_PARAM_END,
	}
/**
 * OdeFileCoder constructor comment.
 */
public SolverFileWriter(PrintWriter pw, SimulationTask simTask, boolean messaging) {
	printWriter = pw;
	this.simTask = simTask;
	bUseMessaging = messaging;
}


public void write() throws Exception {
	write(null);
}

public abstract void write(String[] parameterNames) throws Exception;

/**
# JMS_Paramters
JMS_PARAM_BEGIN
JMS_BROKER tcp://code:2506
JMS_USER username passwd
JMS_QUEUE workerEventDev
JMS_TOPIC serviceControlDev
VCELL_USER fgao
SIMULATION_KEY 22489731
JOB_INDEX 0
JMS_PARAM_END
 */
protected void writeJMSParamters() {
	if (bUseMessaging) {
		printWriter.println("# JMS_Paramters");
		printWriter.println(SolverInputFileKeyword.JMS_PARAM_BEGIN);
		printWriter.println(SolverInputFileKeyword.JMS_BROKER + " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL));
	    printWriter.println(SolverInputFileKeyword.JMS_USER + " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser) + " " + PropertyLoader.getRequiredProperty(PropertyLoader.jmsPassword));
	    printWriter.println(SolverInputFileKeyword.JMS_QUEUE + " " + VCellQueue.WorkerEventQueue.getName());  
		printWriter.println(SolverInputFileKeyword.JMS_TOPIC + " " + VCellTopic.ServiceControlTopic.getName());
		printWriter.println(SolverInputFileKeyword.VCELL_USER + " " + simTask.getSimulation().getVersion().getOwner().getName());
		printWriter.println(SolverInputFileKeyword.SIMULATION_KEY + " " + simTask.getSimulation().getVersion().getVersionKey());
		printWriter.println(SolverInputFileKeyword.JOB_INDEX + " " + simTask.getSimulationJob().getJobIndex());
		printWriter.println(SolverInputFileKeyword.JMS_PARAM_END);
		printWriter.println();
	}
}

protected SimulationTask getSimulationTask()
{
	return simTask;
}

}
