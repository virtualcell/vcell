/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging;
import javax.jms.JMSException;

import org.vcell.util.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 2:54:42 PM)
 * @author: Fei Gao
 */
public class JmsProviderFactory {
/**
 * JmsProviderFactory constructor comment.
 */
public JmsProviderFactory() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2001 4:48:25 PM)
 * @return cbit.vcell.appserver.JmsFactory
 * @param jmsProvider java.lang.String
 * @param host java.lang.String
 * @param userid java.lang.String
 * @param password java.lang.String
 */
public static JmsProvider getJmsProvider() throws JMSException {
	return getJmsProvider(JmsUtils.getJmsProvider(), JmsUtils.getJmsUrl(), JmsUtils.getJmsUserID(), JmsUtils.getJmsPassword());
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2001 4:48:25 PM)
 * @return cbit.vcell.appserver.JmsFactory
 * @param jmsProvider java.lang.String
 * @param host java.lang.String
 * @param userid java.lang.String
 * @param password java.lang.String
 */
public static JmsProvider getJmsProvider(String provider, String url, String userid, String password) throws JMSException {
	if (provider.equalsIgnoreCase(JmsProvider.SONICMQ)){
		return new SonicMQJmsProvider(url,userid,password);	 
	} else {
		throw new IllegalArgumentException("unexpected jmsProvider '" + provider + "', expecting " + JmsProvider.SONICMQ);// + " or " + JBOSSMQ);
	}
}
}
