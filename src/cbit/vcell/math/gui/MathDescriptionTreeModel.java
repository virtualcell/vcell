package cbit.vcell.math.gui;
import cbit.util.EventDispatchRunWithException;
import cbit.vcell.parser.Expression;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.tree.DefaultTreeModel;
import cbit.vcell.desktop.BioModelNode;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class MathDescriptionTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.math.MathDescription fieldMathDescription = null;

/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MathDescriptionTreeModel() {
	super(new BioModelNode("empty",false),true);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createBaseTree() {
	if (getMathDescription()==null){
		return new BioModelNode(" ",false);
	}
	
	//
	// make root node
	//
	BioModelNode rootNode = new BioModelNode("math description",true);
	//
	// create subTree of Constants
	//
	BioModelNode constantRootNode = new BioModelNode("constants",true);
	Enumeration enum1 = getMathDescription().getConstants();
	while (enum1.hasMoreElements()){
		Constant constant = (Constant)enum1.nextElement();
		BioModelNode constantNode = new BioModelNode(constant,false);
		constantRootNode.add(constantNode);
	}
	if (constantRootNode.getChildCount()>0){
		rootNode.add(constantRootNode);
	}

	//
	// create subTree of Functions
	//
	BioModelNode functionRootNode = new BioModelNode("functions",true);
	enum1 = getMathDescription().getFunctions();
	while (enum1.hasMoreElements()){
		Function function = (Function)enum1.nextElement();
		BioModelNode functionNode = new BioModelNode(function,false);
		functionRootNode.add(functionNode);
	}
	if (functionRootNode.getChildCount()>0){
		rootNode.add(functionRootNode);
	}

	//
	// create subTree of VolumeSubDomains
	//
	BioModelNode volumeRootNode = new BioModelNode("volume domains",true);
	enum1 = getMathDescription().getSubDomains();
	while (enum1.hasMoreElements()){
		SubDomain subDomain = (SubDomain)enum1.nextElement();
		if (subDomain instanceof cbit.vcell.math.CompartmentSubDomain){
			CompartmentSubDomain volumeSubDomain = (CompartmentSubDomain)subDomain;
			BioModelNode volumeSubDomainNode = new BioModelNode(volumeSubDomain,true);
			if(getMathDescription().isStoch()) //stochastic subtree
			{
				//add stoch variable initial conditions
				BioModelNode varIniConditionNode = new BioModelNode("variable_initial_conditions",true);
				Enumeration iniConditions = volumeSubDomain.getVarIniConditions().elements();
				while (iniConditions.hasMoreElements()){
					VarIniCondition varIni = (VarIniCondition)iniConditions.nextElement();
					BioModelNode varIniNode = new BioModelNode(varIni,false);
					varIniConditionNode.add(varIniNode);
				}
				volumeSubDomainNode.add(varIniConditionNode);
				//add jump processes
				Enumeration jumpProcesses = volumeSubDomain.getJumpProcesses().elements();
				while (jumpProcesses.hasMoreElements())
				{
					JumpProcess jp = (JumpProcess)jumpProcesses.nextElement();
					BioModelNode jpNode = new BioModelNode(jp,true);
					//add probability rate.
					String probRate = "P_"+jp.getName();
					BioModelNode prNode = new BioModelNode("probability_rate = "+probRate,false);
					jpNode.add(prNode);
					//add Actions
					Enumeration actions = jp.getActions().elements();
					while (actions.hasMoreElements())
					{
						Action action = (Action)actions.nextElement();
						BioModelNode actionNode = new BioModelNode(action,false);
						jpNode.add(actionNode);
					}
					volumeSubDomainNode.add(jpNode);	
				}
			}
			else //non-stochastic subtree 
			{ 
				//
				// add equation children
				//
				Enumeration eqnEnum = volumeSubDomain.getEquations();
				while (eqnEnum.hasMoreElements()){
					Equation equation = (Equation)eqnEnum.nextElement();
					BioModelNode equationNode = new BioModelNode(equation,false);
					volumeSubDomainNode.add(equationNode);
				}
				//
				// add fast system
				//
				FastSystem fastSystem = volumeSubDomain.getFastSystem();
				if (fastSystem!=null){
					BioModelNode fsNode = new BioModelNode(fastSystem,true);
					Enumeration enumFI = fastSystem.getFastInvariants();
					while (enumFI.hasMoreElements()){
						FastInvariant fi = (FastInvariant)enumFI.nextElement();
						fsNode.add(new BioModelNode(fi,false));
					}	
					Enumeration enumFR = fastSystem.getFastRates();
					while (enumFR.hasMoreElements()){
						FastRate fr = (FastRate)enumFR.nextElement();
						fsNode.add(new BioModelNode(fr,false));
					}	
					volumeSubDomainNode.add(fsNode);
				}
			}
			volumeRootNode.add(volumeSubDomainNode);
		}
	}
	if (volumeRootNode.getChildCount()>0){
		rootNode.add(volumeRootNode);
	}
	//
	// create subTree of MembraneSubDomains
	//
	BioModelNode membraneRootNode = new BioModelNode("membrane domains",true);
	enum1 = getMathDescription().getSubDomains();
	while (enum1.hasMoreElements()){
		SubDomain subDomain = (SubDomain)enum1.nextElement();
		if (subDomain instanceof MembraneSubDomain){
			MembraneSubDomain membraneSubDomain = (MembraneSubDomain)subDomain;
			BioModelNode membraneSubDomainNode = new BioModelNode(membraneSubDomain,true);
			//
			// add equation children
			//
			Enumeration eqnEnum = membraneSubDomain.getEquations();
			while (eqnEnum.hasMoreElements()){
				Equation equation = (Equation)eqnEnum.nextElement();
				BioModelNode equationNode = new BioModelNode(equation,false);
				membraneSubDomainNode.add(equationNode);
			}
			//
			// add jump condition children
			//
			Enumeration jcEnum = membraneSubDomain.getJumpConditions();
			while (jcEnum.hasMoreElements()){
				JumpCondition jumpCondition = (JumpCondition)jcEnum.nextElement();
				BioModelNode jcNode = new BioModelNode(jumpCondition,false);
				membraneSubDomainNode.add(jcNode);
			}
			//
			// add fast system
			//
			FastSystem fastSystem = membraneSubDomain.getFastSystem();
			if (fastSystem!=null){
				BioModelNode fsNode = new BioModelNode(fastSystem,true);
				Enumeration enumFI = fastSystem.getFastInvariants();
				while (enumFI.hasMoreElements()){
					FastInvariant fi = (FastInvariant)enumFI.nextElement();
					fsNode.add(new BioModelNode(fi,false));
				}	
				Enumeration enumFR = fastSystem.getFastRates();
				while (enumFR.hasMoreElements()){
					FastRate fr = (FastRate)enumFR.nextElement();
					fsNode.add(new BioModelNode(fr,false));
				}	
				membraneSubDomainNode.add(fsNode);
			}
			membraneRootNode.add(membraneSubDomainNode);
		}
	}
	if (membraneRootNode.getChildCount()>0){
		rootNode.add(membraneRootNode);
	}

	return rootNode;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 * @see #setMathDescription
 */
public cbit.vcell.math.MathDescription getMathDescription() {
	return fieldMathDescription;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
private void refreshTree() {
	new EventDispatchRunWithException() {
		public Object runWithException() throws Exception {
			if (getMathDescription()!=null){
				setRoot(createBaseTree());
			}else{
				setRoot(new BioModelNode("empty"));
			}
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();	
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @param mathDescription The new value for the property.
 * @see #getMathDescription
 */
public void setMathDescription(cbit.vcell.math.MathDescription mathDescription) {
	cbit.vcell.math.MathDescription oldValue = fieldMathDescription;
	fieldMathDescription = mathDescription;
	refreshTree();
	firePropertyChange("mathDescription", oldValue, mathDescription);
}
}