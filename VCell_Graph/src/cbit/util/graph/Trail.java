package cbit.util.graph;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 12:19:18 AM)
 * @author: Jim Schaff
 */
public class Trail extends Walk {
/**
 * Path constructor comment.
 */
public Trail() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:15:40 AM)
 * @param trail cbit.vcell.mapping.potential.Trail
 */
public Trail(Trail trail) {
	super(trail);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	//
	// "Trails" don't duplicate edges
	//
	if (contains(newEdge)){
		throw new RuntimeException("edge "+newEdge+" already exists in list");
	}
	super.addEdge(newEdge);
}
}