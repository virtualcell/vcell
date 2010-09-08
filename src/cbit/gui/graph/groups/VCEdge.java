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