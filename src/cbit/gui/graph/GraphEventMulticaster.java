/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;
/**
 * This is the event multicaster class to support the cbit.vcell.graph.GraphListenerinterface.
 *
 * WARNING ... BUG IN GENERATED CODE FROM VisualAge for Java !!!!!!!!!
 *
 * This was a generated subclass of AWTEventMulticaster and the remove(EventListener) method was to be 
 * overridden.  Instead, VisualAge for Java generated the method: remove(GraphListener) which doesn't
 * override anything ... and causes ClassCastExceptions upon removal of a listener.
 *
 * The fix for this bug is to change the argument type of the generated remove() method to that of java.util.EventListener.
 *
 */
public class GraphEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.gui.graph.GraphListener {
	/**
	 * Constructor to support multicast events.
	 * @param a java.util.EventListener
	 * @param b java.util.EventListener
	 */
	protected GraphEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
		super(a, b);
	}


	/**
	 * Add new listener to support multicast events.
	 * @return cbit.vcell.graph.GraphListener
	 * @param a cbit.vcell.graph.GraphListener
	 * @param b cbit.vcell.graph.GraphListener
	 */
	public static cbit.gui.graph.GraphListener add(cbit.gui.graph.GraphListener a, cbit.gui.graph.GraphListener b) {
		return (cbit.gui.graph.GraphListener)addInternal(a, b);
	}


	/**
	 * Add new listener to support multicast events.
	 * @return java.util.EventListener
	 * @param a java.util.EventListener
	 * @param b java.util.EventListener
	 */
	protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
		if (a == null)  return b;
		if (b == null)  return a;
		return new GraphEventMulticaster(a, b);
	}


	/**
	 * 
	 * @param event cbit.vcell.graph.GraphEvent
	 */
	public void graphChanged(cbit.gui.graph.GraphEvent event) {
		((cbit.gui.graph.GraphListener)a).graphChanged(event);
		((cbit.gui.graph.GraphListener)b).graphChanged(event);
	}


	/**
	 * Remove listener to support multicast events.
	 * @return cbit.vcell.graph.GraphListener
	 * @param l cbit.vcell.graph.GraphListener
	 * @param oldl cbit.vcell.graph.GraphListener
	 */
	public static cbit.gui.graph.GraphListener remove(cbit.gui.graph.GraphListener l, cbit.gui.graph.GraphListener oldl) {
		if (l == oldl || l == null)
			return null;
		if(l instanceof GraphEventMulticaster)
			return (cbit.gui.graph.GraphListener)((cbit.gui.graph.GraphEventMulticaster) l).remove(oldl);
		return l;
	}


	/**
	 * 
	 * @return java.util.EventListener
	 * @param oldl cbit.vcell.graph.GraphListener
	 */
	@Override
	protected java.util.EventListener remove(java.util.EventListener oldl) {
		if (oldl == a)  return b;
		if (oldl == b)  return a;
		java.util.EventListener a2 = removeInternal(a, oldl);
		java.util.EventListener b2 = removeInternal(b, oldl);
		if (a2 == a && b2 == b)
			return this;
		return addInternal(a2, b2);
	}
}
