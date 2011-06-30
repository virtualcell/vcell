package cbit.vcell.solver;
import java.io.*;

import cbit.vcell.messaging.JmsUtils;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public abstract class SolverFileWriter {
	protected PrintWriter printWriter = null;
	protected boolean bUseMessaging = true;
	protected final SimulationJob simulationJob;

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
public SolverFileWriter(PrintWriter pw, SimulationJob simJob, boolean messaging) {
	printWriter = pw;
	simulationJob = simJob;
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
		printWriter.println(SolverInputFileKeyword.JMS_BROKER + " " + JmsUtils.getJmsUrl());
	    printWriter.println(SolverInputFileKeyword.JMS_USER + " " + JmsUtils.getJmsUserID() + " " + JmsUtils.getJmsPassword());
	    printWriter.println(SolverInputFileKeyword.JMS_QUEUE + " " + JmsUtils.getQueueWorkerEvent());  
		printWriter.println(SolverInputFileKeyword.JMS_TOPIC + " " + JmsUtils.getTopicServiceControl());
		printWriter.println(SolverInputFileKeyword.VCELL_USER + " " + simulationJob.getSimulation().getVersion().getOwner().getName());
		printWriter.println(SolverInputFileKeyword.SIMULATION_KEY + " " + simulationJob.getSimulation().getVersion().getVersionKey());
		printWriter.println(SolverInputFileKeyword.JOB_INDEX + " " + simulationJob.getJobIndex());
		printWriter.println(SolverInputFileKeyword.JMS_PARAM_END);
		printWriter.println();
	}
}

}
