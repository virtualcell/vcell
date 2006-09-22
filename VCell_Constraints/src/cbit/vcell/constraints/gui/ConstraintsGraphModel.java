package cbit.vcell.constraints.gui;
import java.util.Enumeration;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.Shape;
import cbit.gui.graph.SimpleContainerShape;
/**
 * Insert the type's description here.
 * Creation date: (7/9/2003 4:31:04 PM)
 * @author: Jim Schaff
 */
public class ConstraintsGraphModel extends cbit.gui.graph.GraphModel implements java.beans.PropertyChangeListener {
	private cbit.vcell.constraints.ConstraintContainerImpl fieldConstraintContainerImpl = null;

/**
 * ConstraintsGraphModel constructor comment.
 */
public ConstraintsGraphModel() {
	super();
	this.addPropertyChangeListener(this);
}


/**
 * Gets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @return The constraintContainerImpl property value.
 * @see #setConstraintContainerImpl
 */
public cbit.vcell.constraints.ConstraintContainerImpl getConstraintContainerImpl() {
	return fieldConstraintContainerImpl;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("constraintContainerImpl")){
		clearAllShapes();
		refreshAll();
	}
	if (evt.getSource() == getConstraintContainerImpl()){
		refreshAll();
	}
	if (evt.getSource() instanceof cbit.vcell.constraints.GeneralConstraint){
		if (evt.getPropertyName().equals("expression")){
			refreshAll();
		}else{
			fireGraphChanged();
		}
	}
	if (evt.getSource() instanceof cbit.vcell.constraints.SimpleBounds){
		fireGraphChanged();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 9:11:57 AM)
 */
public void refreshAll() {
	//
	// 1) mark all shapes as dirty
	// 2) traverse model adding new shapes or cleaning those that already exist
	// 3) remove remaining dirty shapes
	//

	//
	//	mark all shapes as dirty
	//
	Enumeration enum_shapes = getShapes();
	while (enum_shapes.hasMoreElements()) {
		Shape shape = (Shape) enum_shapes.nextElement();
		shape.setDirty(true);
		if (shape instanceof ConstraintVarNode){
			((ConstraintVarNode)shape).setDegree(1);
		}
	}

	
	ContainerShape containerShape = (ContainerShape)getShapeFromModelObject(getConstraintContainerImpl());
	if (containerShape==null){
		containerShape = new SimpleContainerShape(getConstraintContainerImpl(),this,"Constraints Graph");
		containerShape.setLabel("constraint network");
		addShape(containerShape);
	}
	containerShape.refreshLabel();
	containerShape.setDirty(false);
	//
	// add nodes for GeneralConstraints and edges to its Variables (add Variable when necessary)
	//
	cbit.vcell.constraints.GeneralConstraint generalConstraints[] = getConstraintContainerImpl().getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		String symbols[] = generalConstraints[i].getExpression().getSymbols();
		if (symbols==null){
			continue;
		}
		GeneralConstraintNode generalConstraintNode = (GeneralConstraintNode)getShapeFromModelObject(generalConstraints[i]);
		if (generalConstraintNode == null){
			generalConstraintNode = new GeneralConstraintNode(generalConstraints[i],this,symbols.length);
			containerShape.addChildShape(generalConstraintNode);
			addShape(generalConstraintNode);
		}
		generalConstraintNode.refreshLabel();
		generalConstraintNode.setDirty(false);
		
		for (int j = 0; j < symbols.length; j++){
			ConstraintVarNode constraintVarNode = (ConstraintVarNode)getShapeFromLabel(symbols[j]);
			if (constraintVarNode==null){
				constraintVarNode = new ConstraintVarNode(symbols[j],this,1);
				containerShape.addChildShape(constraintVarNode);
				addShape(constraintVarNode);
			}else{
				constraintVarNode.setDegree(constraintVarNode.getDegree()+1);
			}
			constraintVarNode.refreshLabel();
			constraintVarNode.setDirty(false);

			ConstraintDependencyEdgeShape constraintDependencyEdgeShape = null;
			Enumeration enumShapes = getShapes();
			while (enumShapes.hasMoreElements()){
				Shape shape = (Shape)enumShapes.nextElement();
				if (shape instanceof ConstraintDependencyEdgeShape){
					if (((ConstraintDependencyEdgeShape)shape).getConstraintShape() == generalConstraintNode &&
						((ConstraintDependencyEdgeShape)shape).getVarShape() == constraintVarNode){
						constraintDependencyEdgeShape = (ConstraintDependencyEdgeShape)shape;
					}
				}
			}
			if (constraintDependencyEdgeShape == null){
				constraintDependencyEdgeShape = new ConstraintDependencyEdgeShape(generalConstraintNode,constraintVarNode,this);
				containerShape.addChildShape(constraintDependencyEdgeShape);
				addShape(constraintDependencyEdgeShape);
			}
			constraintDependencyEdgeShape.refreshLabel();
			constraintDependencyEdgeShape.setDirty(false);
		}
	}
	//
	// add nodes for SimpleBounds and edges to its Variables (add Variable when necessary)
	//
	cbit.vcell.constraints.SimpleBounds simpleBounds[] = getConstraintContainerImpl().getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		BoundsNode boundsNode = (BoundsNode)getShapeFromModelObject(simpleBounds[i]);
		if (boundsNode==null){
			boundsNode = new BoundsNode(simpleBounds[i],this);
			containerShape.addChildShape(boundsNode);
			addShape(boundsNode);
		}
		boundsNode.refreshLabel();
		boundsNode.setDirty(false);

		
		ConstraintVarNode constraintVarNode = (ConstraintVarNode)getShapeFromLabel(simpleBounds[i].getIdentifier());
		if (constraintVarNode==null){
			constraintVarNode = new ConstraintVarNode(simpleBounds[i].getIdentifier(),this,1);
			containerShape.addChildShape(constraintVarNode);
			addShape(constraintVarNode);
		}else{
			constraintVarNode.setDegree(constraintVarNode.getDegree()+1);
		}
		constraintVarNode.refreshLabel();
		constraintVarNode.setDirty(false);

		ConstraintDependencyEdgeShape constraintDependencyEdgeShape = null;
		Enumeration enumShapes = getShapes();
		while (enumShapes.hasMoreElements()){
			Shape shape = (Shape)enumShapes.nextElement();
			if (shape instanceof ConstraintDependencyEdgeShape){
				if (((ConstraintDependencyEdgeShape)shape).getConstraintShape() == boundsNode &&
					((ConstraintDependencyEdgeShape)shape).getVarShape() == constraintVarNode){
					constraintDependencyEdgeShape = (ConstraintDependencyEdgeShape)shape;
				}
			}
		}
		if (constraintDependencyEdgeShape == null){
			constraintDependencyEdgeShape = new ConstraintDependencyEdgeShape(boundsNode,constraintVarNode,this);
			containerShape.addChildShape(constraintDependencyEdgeShape);
			addShape(constraintDependencyEdgeShape);
		}
		constraintDependencyEdgeShape.refreshLabel();
		constraintDependencyEdgeShape.setDirty(false);
	}
	
	//
	//	remove all dirty shapes (enumerations aren't editable), so build list and delete from that.
	//
	enum_shapes = getShapes();
	java.util.Vector deleteList = new java.util.Vector();
	while (enum_shapes.hasMoreElements()) {
		Shape shape = (Shape) enum_shapes.nextElement();
		if (shape.isDirty()) {
			deleteList.add(shape);
		}
	}
	for (int i = 0; i < deleteList.size(); i++){
		removeShape((Shape)deleteList.elementAt(i));
	}
	
	fireGraphChanged(new GraphEvent(this));
}


/**
 * Sets the constraintContainerImpl property (cbit.vcell.constraints.ConstraintContainerImpl) value.
 * @param constraintContainerImpl The new value for the property.
 * @see #getConstraintContainerImpl
 */
public void setConstraintContainerImpl(cbit.vcell.constraints.ConstraintContainerImpl constraintContainerImpl) {
	cbit.vcell.constraints.ConstraintContainerImpl oldValue = fieldConstraintContainerImpl;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		cbit.vcell.constraints.GeneralConstraint oldConstraints[] = oldValue.getGeneralConstraints();
		for (int i = 0; i < oldConstraints.length; i++){
			oldConstraints[i].removePropertyChangeListener(this);
		}
		cbit.vcell.constraints.SimpleBounds oldSimpleBounds[] = oldValue.getSimpleBounds();
		for (int i = 0; i < oldSimpleBounds.length; i++){
			oldSimpleBounds[i].removePropertyChangeListener(this);
		}
	}
	fieldConstraintContainerImpl = constraintContainerImpl;
	if (fieldConstraintContainerImpl!=null){
		fieldConstraintContainerImpl.addPropertyChangeListener(this);
		cbit.vcell.constraints.GeneralConstraint newConstraints[] = fieldConstraintContainerImpl.getGeneralConstraints();
		for (int i = 0; i < newConstraints.length; i++){
			newConstraints[i].addPropertyChangeListener(this);
		}
		cbit.vcell.constraints.SimpleBounds newSimpleBounds[] = fieldConstraintContainerImpl.getSimpleBounds();
		for (int i = 0; i < newSimpleBounds.length; i++){
			newSimpleBounds[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("constraintContainerImpl", oldValue, constraintContainerImpl);
	refreshAll();
}
}