package org.vcell.ncbc.physics.component.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
/**
 * This type was created in VisualAge.
 */
public class SimpleContainerShape extends ContainerShape {
	private Object fieldObject = null;
/**
 * ReactionContainerShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public SimpleContainerShape(Object object, GraphModel graphModel) {
	super(graphModel);
	setRandomLayout(false);
	fieldObject = object;
	bNoFill = false;
	defaultFGselect = java.awt.Color.red;
	defaultBGselect = java.awt.Color.white;
	backgroundColor = java.awt.Color.white;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
    return fieldObject;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {

	//
	// get size when empty
	//
	Dimension emptySize = super.getPreferedSize(g);

	//
	// make larger than empty size so that children fit
	//
	for (int i = 0; i < childShapeList.size(); i++){
		Shape shape = (Shape)childShapeList.elementAt(i);
		if (shape instanceof ElipseShape){
			emptySize.width = Math.max(emptySize.width,shape.getLocation().x+shape.getSize().width);
			emptySize.height = Math.max(emptySize.height,shape.getLocation().y+shape.getSize().height);
		}
	}
	return emptySize;
}
/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {
//System.out.println("ReactionContainerShape.layout(), bRandomize="+bRandomize);
	super.layout();
	//
	// make sure children fit
	//
	int width = getSize().width;
	int height = getSize().height;

	for (int i = 0; i < childShapeList.size(); i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child.getSize().width+child.getLocation().x > width || child.getSize().height+child.getLocation().y > height){
			throw new LayoutException("cannot fit all reactions");
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint ( java.awt.Graphics2D g, int parentOffsetX, int parentOffsetY ) {

	super.paint(g,parentOffsetX, parentOffsetY);
	
	int absPosX = screenPos.x + parentOffsetX;
	int absPosY = screenPos.y + parentOffsetY;

	//
	// print edges first
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof EdgeShape){
			child.paint(g,absPosX,absPosY);
		}
	}	
	//
	// then print rest of shapes
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof EdgeShape)){
			child.paint(g,absPosX,absPosY);
		}
	}	
	return;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public void randomize() {

//System.out.println("ReactionContainerShape.randomize(), bRandomize="+bRandomize);
	//
	// randomize the locations of speciesContexts and of reactionSteps,
	// then draw in the reactionParticipant edges
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof ElipseShape){
			//
			// position normally about the center
			//
			child.setLocation(getRandomPosition());
		}	
	}

	//
	// calculate locations and sizes of reactionParticipant edges
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof EdgeShape){
			
			((EdgeShape)child).layout();
			
		}
	}
	
	//
	// position label
	//
	int centerX = screenSize.width/2;
	int currentY = labelSize.height;
	labelPos.x = centerX - labelSize.width/2;
	labelPos.y = currentY;
	currentY += labelSize.height;	
}
/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("Physical Model");
}
}
