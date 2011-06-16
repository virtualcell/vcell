/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.awt.event.*;
/**
 * This type was created in VisualAge.
 */
public class UserThread extends Thread {
	public static int NORMAL_EXIT = 0;
	public static int ABNORMAL_EXIT = 1;
	private int exitStatus = ABNORMAL_EXIT;
	private Throwable thrownException = null;
	//
	protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
/**
 * UserThread constructor comment.
 */
public UserThread() {
	super();
}
/**
 * UserThread constructor comment.
 * @param target java.lang.Runnable
 */
public UserThread(Runnable target) {
	super(target);
}
/**
 * UserThread constructor comment.
 * @param target java.lang.Runnable
 * @param name java.lang.String
 */
public UserThread(Runnable target, String name) {
	super(target, name);
}
/**
 * UserThread constructor comment.
 * @param name java.lang.String
 */
public UserThread(String name) {
	super(name);
}
/**
 * UserThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param target java.lang.Runnable
 */
public UserThread(ThreadGroup group, Runnable target) {
	super(group, target);
}
/**
 * UserThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param target java.lang.Runnable
 * @param name java.lang.String
 */
public UserThread(ThreadGroup group, Runnable target, String name) {
	super(group, target, name);
}
/**
 * UserThread constructor comment.
 * @param group java.lang.ThreadGroup
 * @param name java.lang.String
 */
public UserThread(ThreadGroup group, String name) {
	super(group, name);
}
/**
 * Adds an actionListener to the Timer
 */
public void addActionListener(ActionListener listener) {
	listenerList.add(ActionListener.class, listener);
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireAbnormalExit(Throwable e) {
	this.exitStatus = ABNORMAL_EXIT;
	this.thrownException = e;
	fireActionPerformed(new ActionEvent (this, ABNORMAL_EXIT, "abnormalExit"));
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireAbnormalExit0() {
	this.exitStatus = ABNORMAL_EXIT;
	fireActionPerformed(new ActionEvent (this, ABNORMAL_EXIT, "abnormalExit"));
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireActionPerformed(ActionEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
		if (listeners[i] == ActionListener.class) {
			((ActionListener) listeners[i + 1]).actionPerformed(e);
		}
	}
}
/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireNormalExit() {
	this.exitStatus = NORMAL_EXIT;
	fireActionPerformed(new ActionEvent (this, NORMAL_EXIT, "normalExit"));
}
/**
 * Adds an actionListener to the Timer
 */
public int getExitStatus() {
	return (this.exitStatus);
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/00 10:20:58 AM)
 * @return java.lang.Throwable
 */
public Throwable getThrownException() {
	return thrownException;
}
/**
 * Removes an ActionListener from the Timer.
 */
public void removeActionListener(ActionListener listener) {
	listenerList.remove(ActionListener.class, listener);
}
}
