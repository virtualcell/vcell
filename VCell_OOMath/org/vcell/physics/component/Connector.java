package org.vcell.physics.component;
/**
 * Insert the type's description here.
 * Creation date: (11/18/2005 11:23:32 AM)
 * @author: Jim Schaff
 */
public class Connector {
	private String name = null;
	private ModelComponent parent = null;
	private Variable effortVariable = null;
	private Variable flowVariable = null;
	//
	// for blocks (causality determined) can be of causality input or output
	// what about flow?
	// what about "Type" (real, ...?)
	// name
	// scoping: scope of children of component
	//
	// connectors can contain other connectors (like an electrical bus ... or harness).
	//
	// NOTE: we could have the modelers do their modeling in Modelica and "compile" a finite set of "Blocks" from them, with combinations of input/output ...

/**
 * Connector constructor comment.
 */
public Connector(ModelComponent argParent, String argName, Variable argEffortVariable, Variable argFlowVariable) {
	super();
	this.name = argName;
	this.parent = argParent;
	this.effortVariable = argEffortVariable;
	this.flowVariable = argFlowVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:16:48 PM)
 * @return ncbc.physics2.component.Variable
 */
public Variable getEffortVariable() {
	return effortVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:16:48 PM)
 * @return ncbc.physics2.component.Variable
 */
public Variable getFlowVariable() {
	return flowVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2006 8:24:52 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2006 8:23:56 PM)
 * @return ncbc.physics2.component.ModelComponent
 */
public ModelComponent getParent() {
	return parent;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:16:48 PM)
 * @param newEffortVariable ncbc.physics2.component.Variable
 */
public void setEffortVariable(Variable newEffortVariable) {
	effortVariable = newEffortVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:16:48 PM)
 * @param newFlowVariable ncbc.physics2.component.Variable
 */
public void setFlowVariable(Variable newFlowVariable) {
	flowVariable = newFlowVariable;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/2006 8:24:52 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
}