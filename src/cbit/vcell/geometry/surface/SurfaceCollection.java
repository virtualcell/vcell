package cbit.vcell.geometry.surface;
/**
 * Insert the type's description here.
 * Creation date: (6/28/2003 12:10:26 AM)
 * @author: John Wagner
 */
public class SurfaceCollection {
	private Node[] fieldNodes = new Node[0];
	private java.util.Vector<Surface> fieldSurfaces = new java.util.Vector<Surface>();

/**
 * BoundaryCollection constructor comment.
 */
public SurfaceCollection() {
	super();
}


/**
 * BoundaryCollection constructor comment.
 */
public SurfaceCollection(Surface surface) {
	super();
	addSurface(surface);
}


/**
 * BoundaryCollection constructor comment.
 */
public void addSurface(Surface surface) {
	fieldSurfaces.add(surface);
}


/**
 * BoundaryCollection constructor comment.
 */
public void addSurfaceCollection(SurfaceCollection surfaceCollection) {
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
		addSurface(surfaceCollection.getSurfaces(i));
	}
}


/**
 * BoundaryCollection constructor comment.
 */
public int getNodeCount() {
	return(fieldNodes.length);
}


/**
 * BoundaryCollection constructor comment.
 */
public Node[] getNodes() {
	return(fieldNodes);
}


/**
 * BoundaryCollection constructor comment.
 */
public Node getNodes(int i) {
	return(fieldNodes[i]);
}


/**
 * BoundaryCollection constructor comment.
 */
public int getSurfaceCount() {
	return(fieldSurfaces.size());
}


/**
 * BoundaryCollection constructor comment.
 */
public Surface getSurfaces(int i) {
	return fieldSurfaces.get(i);
}


/**
 * BoundaryCollection constructor comment.
 */
public void removeSurface(int i) {
	fieldSurfaces.remove(i);
}


/**
 * BoundaryCollection constructor comment.
 */
public void removeSurface(Surface surface) {
	fieldSurfaces.remove(surface);
}


/**
 * BoundaryCollection constructor comment.
 */
public void setNodes(Node[] nodes) {
	fieldNodes = nodes;
}


/**
 * BoundaryCollection constructor comment.
 */
public void setSurfaces(int i, Surface surface) {
	fieldSurfaces.set(i, surface);
}

public int getTotalPolygonCount() {
	int numPolygonCount = 0;
	for (int i = 0; i < getSurfaceCount(); i++){
		numPolygonCount += getSurfaces(i).getPolygonCount();
	}
	return numPolygonCount;
}
}