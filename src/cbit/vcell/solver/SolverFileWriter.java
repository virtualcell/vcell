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
	protected Simulation simulation = null;
	protected int jobIndex = 0;

/**
 * OdeFileCoder constructor comment.
 */
public SolverFileWriter(PrintWriter pw, Simulation sim, int ji, boolean messaging) {
	printWriter = pw;
	simulation = sim;
	jobIndex = ji;
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
JMS_USER serverUser cbittech
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
		printWriter.println("JMS_PARAM_BEGIN");
		printWriter.println("JMS_BROKER " + JmsUtils.getJmsUrl());
	    printWriter.println("JMS_USER " + JmsUtils.getJmsUserID() + " " + JmsUtils.getJmsPassword());
	    printWriter.println("JMS_QUEUE " + JmsUtils.getQueueWorkerEvent());  
		printWriter.println("JMS_TOPIC " + JmsUtils.getTopicServiceControl());
		printWriter.println("VCELL_USER " + simulation.getVersion().getOwner().getName());
		printWriter.println("SIMULATION_KEY " + simulation.getVersion().getVersionKey());
		printWriter.println("JOB_INDEX " + jobIndex);
		printWriter.println("JMS_PARAM_END");
		printWriter.println();
	}
}

}
