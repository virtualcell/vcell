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

import org.jdom.Element;

import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public abstract class SolverFileWriter {
	protected PrintWriter printWriter = null;
	protected boolean bUseMessaging = true;
	protected final SimulationTask simTask;
	
	protected enum SolverInputFileKeyword {
		JMS_PARAM_BEGIN("jms"),
		JMS_BROKER("broker"),
		JMS_USER("jmsUser"),
		JMS_QUEUE("queue"),
		JMS_TOPIC("topic"),
		VCELL_USER("vcellUser"),
		SIMULATION_KEY("simKey"),
		JOB_INDEX("jobIndex"),
		JMS_PARAM_END(""),
		JMS_PW("pw"); //currently only used in xml 
		
		SolverInputFileKeyword(String xml ) {
			this.xml = xml;
		}
		public final String xml;
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

/**
 * create Element 
 * @param sifk
 * @param content may be null
 * @return new Element
 */
private Element create(SolverInputFileKeyword sifk, String content) {
	Element e = new Element(sifk.xml);
	if (content != null) {
		e.addContent(content);
	}
	
	return e;
}

/**
 * create Element 
 * @param sifk
 * @param number
 * @return new Element
 */
private Element create(SolverInputFileKeyword sifk, int number) {
	return create(sifk,Integer.toString(number));
}

/**
 * 
 * @return JMS parameters in XML format
 */
protected Element xmlJMSParameters () {
	Element jms = create(SolverInputFileKeyword.JMS_PARAM_BEGIN,null);
	Element broker = create(SolverInputFileKeyword.JMS_BROKER, PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL)) ;
	Element jmsUser = create(SolverInputFileKeyword.JMS_USER, PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser));
	Element pw = create(SolverInputFileKeyword.JMS_PW, PropertyLoader.getRequiredProperty(PropertyLoader.jmsPassword));
	Element queue = create(SolverInputFileKeyword.JMS_QUEUE, VCellQueue.WorkerEventQueue.getName());  
	Element topic = create(SolverInputFileKeyword.JMS_TOPIC, VCellTopic.ServiceControlTopic.getName());
	Element vcellUser = create(SolverInputFileKeyword.VCELL_USER, simTask.getSimulation().getVersion().getOwner().getName());
	Element simkey = create(SolverInputFileKeyword.SIMULATION_KEY, simTask.getSimulation().getVersion().getVersionKey().toString());
	Element jobindex = create(SolverInputFileKeyword.JOB_INDEX, simTask.getSimulationJob().getJobIndex());

	jms.addContent(broker).addContent(jmsUser).addContent(pw).addContent(queue).addContent(topic).addContent(vcellUser).addContent(simkey).addContent(jobindex);

	return jms; 
}

public SimulationTask getSimulationTask()
{
	return simTask;
}

}
