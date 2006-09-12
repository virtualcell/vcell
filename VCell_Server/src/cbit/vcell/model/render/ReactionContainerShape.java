package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;

import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class ReactionContainerShape extends ContainerShape {
	private Structure structure = null;

/**
 * ReactionContainerShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ReactionContainerShape(Structure structure, GraphModel graphModel) {
	super(graphModel);
	this.structure = structure;
	setRandomLayout(false);
	bNoFill = true;
	defaultFGselect = java.awt.Color.red;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return structure;
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
	Font origFont = g.getFont();
	g.setFont(getLabelFont(g));
	try{
		Dimension emptySize = super.getPreferedSize(g);

		//
		// make larger than empty size so that children fit
		//
		for (int i = 0; i < childShapeList.size(); i++){
			Shape shape = (Shape)childShapeList.elementAt(i);
			if (shape instanceof ReactionStepShape || shape instanceof SpeciesContextShape){
				emptySize.width = Math.max(emptySize.width,shape.getLocation().x+shape.getSize().width);
				emptySize.height = Math.max(emptySize.height,shape.getLocation().y+shape.getSize().height);
			}
		}
		return emptySize;
	}finally{
		g.setFont(origFont);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 */
public Structure getStructure() {
	return structure;
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
	Shape selectedShape = null;
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof EdgeShape)){
			if(child.isSelected()){
				selectedShape = child;
			}else{
				child.paint(g,absPosX,absPosY);
			}
		}
	}
	if(selectedShape != null){//selected over all others
		selectedShape.paint(g,absPosX,absPosY);
	}
	if(isSelected()){
		drawLabel(g,absPosX,absPosY);
	}
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
		if (child instanceof SpeciesContextShape || child instanceof ReactionStepShape){
			//
			// position normally about the center
			//
			child.screenPos = getRandomPosition();
		}	
	}

	//
	// calculate locations and sizes of reactionParticipant edges
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof ReactionParticipantShape){
			
			((ReactionParticipantShape)child).layout();
			
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
	if (getStructure() instanceof Membrane){
		setLabel(getStructure().getName()+" (Voltage=\""+((Membrane)getStructure()).getMembraneVoltage().getName()+"\")");
	}else{
		setLabel(getStructure().getName());
	}
}
}