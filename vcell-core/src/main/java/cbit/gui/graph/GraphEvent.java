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
 * This is the event class to support the cbit.vcell.graph.GraphListener interface.
 */
@SuppressWarnings("serial")
public class GraphEvent extends java.util.EventObject {
	/**
	 * GraphEvent constructor comment.
	 * @param source java.lang.Object
	 */
	public GraphEvent(java.lang.Object source) {
		super(source);
	}
}
