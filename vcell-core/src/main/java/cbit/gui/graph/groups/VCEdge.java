/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.groups;

/*  A general edge between two model elements
 *  September 2010
 */

public class VCEdge {
	protected final Object startObject, endObject, edgeObject;

	public VCEdge(Object startObject, Object endObject, Object edgeObject) {
		this.startObject = startObject;
		this.endObject = endObject;
		this.edgeObject = edgeObject;
	}

	public Object getStartObject() {
		return startObject;
	}

	public Object getEndObject() {
		return endObject;
	}

	public Object getEdgeObject() {
		return edgeObject;
	}
}
