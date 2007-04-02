package cbit.vcell.messaging;
import javax.jms.*;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import cbit.vcell.xml.XmlParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

import cbit.vcell.xml.Xmlproducer;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import java.io.StringReader;
import java.io.IOException;
import java.beans.PropertyVetoException;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.PropertyLoader;

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
public void sendSimulationTask(VCellQueueSession session) throws javax.jms.JMSException, cbit.vcell.xml.XmlParseException {
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
public javax.jms.Message toLsfInfoMessage(VCellJmsSession session, String jobid, String stdout, String stderr) throws javax.jms.JMSException, cbit.vcell.xml.XmlParseException {
	Message message = session.createTextMessage(simulation2XML());
	
	message.setStringProperty(MessageConstants.LSFJOBID_PROPERTY, jobid);  //
	message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDOUT_FILE,stdout);
	message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDERR_FILE, stderr);	
	message.setIntProperty(MessageConstants.TASKID_PROPERTY, simTask.getTaskID());
	message.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, simTask.getSimulationJob().getJobIndex());
	
	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 11:08:17 AM)
 * @return javax.jms.Message
 * @param session cbit.vcell.messaging.VCellSession
 */
private javax.jms.Message toMessage(VCellQueueSession session) throws javax.jms.JMSException, cbit.vcell.xml.XmlParseException {
	javax.jms.Message message = session.createTextMessage(simulation2XML());		

	message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE); // must have
	message.setIntProperty(MessageConstants.JOBINDEX_PROPERTY, simTask.getSimulationJob().getJobIndex()); // must have
	message.setIntProperty(MessageConstants.TASKID_PROPERTY, simTask.getTaskID()); // must have
	
	message.setStringProperty(MessageConstants.USERNAME_PROPERTY, simTask.getUserName()); // might be used to remove from the job queue when do stopSimulation
	message.setLongProperty(MessageConstants.SIMKEY_PROPERTY, Long.parseLong(simTask.getSimKey() + "")); // might be used to remove from the job queue when do stopSimulation

	message.setStringProperty(MessageConstants.SOLVER_TYPE_PROPERTY, simTask.goodForLSF() ? MessageConstants.SOLVER_TYPE_LSF_PROPERTY : MessageConstants.SOLVER_TYPE_JAVA_PROPERTY); // for worker message filter
	message.setDoubleProperty(MessageConstants.SIZE_MB_PROPERTY, simTask.getEstimatedSizeMB()); // for worker message filter

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