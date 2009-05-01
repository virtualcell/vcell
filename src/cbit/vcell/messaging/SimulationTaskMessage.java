package cbit.vcell.messaging;
import javax.jms.*;

import org.vcell.util.MessageConstants;
import org.vcell.util.document.FieldDataIdentifierSpec;

import cbit.vcell.xml.XmlParseException;

import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;

/**
 * Insert the type's description here.
 * Creation date: (12/31/2003 11:39:39 AM)
 * @author: Fei Gao
 */
public class SimulationTaskMessage {
	private SimulationTask simTask = null;

/**
 * SimulationMessageHelper constructor comment.
 */
public SimulationTaskMessage(SimulationTask simTask0) {
	super();
	simTask = simTask0;
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2004 11:02:09 AM)
 * @param message javax.jms.Message
 */
public SimulationTaskMessage(Message message) throws XmlParseException, JMSException {
	parse(message);
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2004 11:01:40 AM)
 * @return cbit.vcell.solver.Simulation
 */
public SimulationTask getSimulationTask() {
	return simTask;
}


/**
 * Insert the method's description here.
 * Creation date: (12/29/2003 2:51:01 PM)
 * @return cbit.vcell.solver.Simulation
 * @param xml java.lang.String
 */
private void parse(Message message) throws XmlParseException, JMSException {
	if (message == null | !(message instanceof javax.jms.TextMessage)) {
		return;
	}		
		
	String xmlString = ((TextMessage)message).getText();
	Simulation simulation = XmlHelper.XMLToSim(xmlString);
	int taskID = -1;
	int jobIndex = -1;
	FieldDataIdentifierSpec[] fieldDataIDs = null;
	try {
		taskID = ((Integer)JmsUtils.parseProperty(message, MessageConstants.TASKID_PROPERTY, int.class)).intValue();
	} catch (MessagePropertyNotFoundException ex) {
		throw new JMSException("Required property " + MessageConstants.TASKID_PROPERTY + " is missing");
	}	
	try {
		jobIndex = ((Integer)JmsUtils.parseProperty(message, MessageConstants.JOBINDEX_PROPERTY, int.class)).intValue();
	} catch (MessagePropertyNotFoundException ex) {
		throw new JMSException("Required property " + MessageConstants.JOBINDEX_PROPERTY + " is missing");
	}

	// is ok if there is no field data
	try {
		String fdstrs = (String)JmsUtils.parseProperty(message, MessageConstants.FIELDDATAID_PROPERTY, String.class);
		java.util.StringTokenizer st = new java.util.StringTokenizer(fdstrs, "\n");
		fieldDataIDs = new FieldDataIdentifierSpec[st.countTokens()];
		int count = 0;
		while (st.hasMoreTokens()) {
			try{
				fieldDataIDs[count ++] = 
					FieldDataIdentifierSpec.fromCSVString(st.nextToken());
			}catch(ExpressionException e){
				throw new XmlParseException("Error creating FieldDataIdentifierSpec "+e.getMessage());
			}
		}
	} catch (MessagePropertyNotFoundException ex) {
		 System.out.println("Property " + MessageConstants.FIELDDATAID_PROPERTY + " is missing");
	}	

	simTask = new SimulationTask(new SimulationJob(simulation, fieldDataIDs, jobIndex), taskID);
}




/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 11:08:17 AM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
public void sendSimulationTask(JmsSession session) throws javax.jms.JMSException, cbit.vcell.xml.XmlParseException {
	session.sendMessage(JmsUtils.getQueueSimJob(), toMessage(session), DeliveryMode.PERSISTENT, 0);
}


/**
 * Insert the method's description here.
 * Creation date: (12/29/2003 2:49:00 PM)
 * @return java.lang.String
 * @param simulation cbit.vcell.solver.Simulation
 */
private String simulation2XML() throws XmlParseException {
	return XmlHelper.simToXML(simTask.getSimulationJob().getWorkingSim());
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 11:08:17 AM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
private javax.jms.Message toMessage(JmsSession session) throws javax.jms.JMSException, cbit.vcell.xml.XmlParseException {
	javax.jms.Message message = session.createTextMessage(simulation2XML());		

	message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE); // must have
	message.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, simTask.getSimulationJob().getJobIndex()); // must have
	message.setIntProperty(MessageConstants.TASKID_PROPERTY, simTask.getTaskID()); // must have
	
	message.setStringProperty(MessageConstants.USERNAME_PROPERTY, simTask.getUserName()); // might be used to remove from the job queue when do stopSimulation
	message.setLongProperty(MessageConstants.SIMKEY_PROPERTY, Long.parseLong(simTask.getSimKey() + "")); // might be used to remove from the job queue when do stopSimulation

	message.setDoubleProperty(MessageConstants.SIZE_MB_PROPERTY, simTask.getEstimatedMemorySizeMB()); // for worker message filter
	
	if (simTask.getComputeResource() != null) {
		message.setStringProperty(MessageConstants.COMPUTE_RESOURCE_PROPERTY, simTask.getComputeResource()); // for worker message filter
	}

	FieldDataIdentifierSpec[] fieldDataIDs = simTask.getSimulationJob().getFieldDataIdentifierSpecs();
	if (fieldDataIDs != null && fieldDataIDs.length > 0) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fieldDataIDs.length; i ++) {
			sb.append(fieldDataIDs[i].toCSVString() + "\n");
		}
		message.setStringProperty(MessageConstants.FIELDDATAID_PROPERTY, sb.toString());
	}
	return message;
}
}