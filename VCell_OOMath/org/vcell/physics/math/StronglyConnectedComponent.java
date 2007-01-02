package org.vcell.physics.math;

import org.vcell.physics.component.PhysicalSymbol;

/**
 * Insert the type's description here.
 * Creation date: (1/19/2006 10:03:33 AM)
 * @author: Jim Schaff
 */
public class StronglyConnectedComponent {
	private java.util.HashSet hash = new java.util.HashSet();
	private String name = null;

/**
 * StronglyConnectedComponent constructor comment.
 */
public StronglyConnectedComponent(String argName) {
	super();
	this.name = argName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 10:54:13 AM)
 * @param vertexIndex int
 */
public void addVertex(int vertexIndex) {
	hash.add(new Integer(vertexIndex));
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 12:01:48 PM)
 * @return boolean
 * @param index int
 */
public boolean contains(int vertexIndex) {
	return hash.contains(new Integer(vertexIndex));
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 11:09:18 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (1/30/2006 8:30:03 AM)
 * @return int
 */
public int size() {
	return hash.size();
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 11:00:35 AM)
 * @return java.lang.String
 */
public String toString(PhysicalSymbol[] symbols) {
	StringBuffer buffer = new StringBuffer();
	java.util.Iterator iter = hash.iterator();
	while (iter.hasNext()){
		int index = ((Integer)iter.next()).intValue();
		if (buffer.length()==0){
			buffer.append("[");
		}else{
			buffer.append(",");
		}
		buffer.append(symbols[index].getName());
	}
	return "\""+name+"\" "+buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2006 11:00:35 AM)
 * @return java.lang.String
 */
public String toString(VarEquationAssignment[] varEqnAssignments) {
	StringBuffer buffer = new StringBuffer();
	java.util.Iterator iter = hash.iterator();
	while (iter.hasNext()){
		int index = ((Integer)iter.next()).intValue();
		if (buffer.length()>0){
			buffer.append(",");
		}
		buffer.append(varEqnAssignments[index].getSymbol().getName());
	}
	return buffer.toString();
}
}