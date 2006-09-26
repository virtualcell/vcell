package cbit.vcell.util.events;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 2:33:15 PM)
 * @author: IIM
 */
public class MessageData implements java.io.Serializable {
	private java.io.Serializable data = null;
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:34:47 PM)
 * @param data java.io.Serializable
 */
public MessageData(java.io.Serializable data) {
	this.data = data;
}
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 2:35:46 PM)
 * @return java.io.Serializable
 */
public java.io.Serializable getData() {
	return data;
}
}
