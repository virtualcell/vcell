package cbit.vcell.messaging;
import cbit.vcell.server.PropertyLoader;
import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/17/2001 10:16:55 AM)
 * @author: Jim Schaff
 */
public class JmsUtils {



/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ProcessStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public static cbit.vcell.messaging.admin.ServicePerformance getServicePerformance() {
	try {
		long javaFreeMemoryBytes = Runtime.getRuntime().freeMemory();
		long javaTotalMemoryBytes = Runtime.getRuntime().totalMemory();
		long maxJavaMemoryBytes = -1;
		try {
			maxJavaMemoryBytes = Long.parseLong(cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.maxJavaMemoryBytesProperty));
		} catch (NumberFormatException e){
			System.out.println("error reading property '"+cbit.vcell.server.PropertyLoader.maxJavaMemoryBytesProperty+"', "+e.getMessage());
		}
		return new cbit.vcell.messaging.admin.ServicePerformance(javaFreeMemoryBytes,javaTotalMemoryBytes,maxJavaMemoryBytes);
	} catch (Throwable e){
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 9:17:51 AM)
 * @return java.lang.String
 * @param message javax.jms.Message
 * @param propertyName java.lang.String
 */
public static Object parseProperty(Message message, String propertyName, Class propertyType) throws MessagePropertyNotFoundException {
	try {
		if (message.propertyExists(propertyName)) {
			if (propertyType.equals(String.class)) {
				return message.getStringProperty(propertyName);
			} else 	if (propertyType.equals(int.class)) {
				return new Integer(message.getIntProperty(propertyName));
			} else 	if (propertyType.equals(long.class)) {
				return new Long(message.getLongProperty(propertyName));
			} else 	if (propertyType.equals(double.class)) {
				return new Double(message.getDoubleProperty(propertyName));
			} else {
				throw new RuntimeException("Unexpected Property [" + propertyName + "," + propertyType + "]");
			}
		} else {
			throw new MessagePropertyNotFoundException(propertyName);
		}
	} catch (JMSException ex) {
		throw new MessagePropertyNotFoundException("JMSException on parsing [" + propertyName + "," + propertyType + "]:" + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:03:40 AM)
 * @return java.lang.String
 * @param message javax.jms.Message
 */
public static String toString(Message message) throws JMSException {
	if (message == null)
		return null;
		
	java.util.Enumeration enum1 = message.getPropertyNames();
	StringBuffer buffer = new StringBuffer();
	while (enum1.hasMoreElements()){
		String propName = (String)enum1.nextElement();
		try {
			String value = (String)parseProperty(message, propName, String.class);
			buffer.append(" " + propName + "='" + value + "'");
		} catch (MessagePropertyNotFoundException ex) {
			// definitely should not happen
		}
	}
	return buffer.toString();
}

public final static String getJmsProvider() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsProvider);
}

public final static String getJmsUrl() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
}

public final static String getJmsUserID() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
}

public final static String getJmsPassword() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsPassword);
}

public static final String getQueueDbReq() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsDbRequestQueue);
}

public static final String getQueueSimDataReq() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsDataRequestQueue);
}


public static final String getQueueSimJob() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimJobQueue);
}


public static final String getQueueSimReq() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimReqQueue);
}


public static final String getQueueWorkerEvent() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsWorkerEventQueue);
}


public static final String getTopicClientStatus() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsClientStatusTopic);
}


public static final String getTopicDaemonControl() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsDaemonControlTopic);
}


public static final String getTopicServiceControl() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsServiceControlTopic);
}

public static final int getMaxOdeJobsPerUser() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxOdeJobsPerUser));
}


public static final int getMaxPdeJobsPerUser() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxPdeJobsPerUser));
}


public static final String getQueueBNGReq() {
	return PropertyLoader.getRequiredProperty(PropertyLoader.jmsBNGRequestQueue);
}
}