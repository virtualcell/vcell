package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class ReactionSliceContainerShape extends ContainerShape {
	private Structure structure = null;
	public boolean isBeingDragged = false;

/**
 * ReactionContainerShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ReactionSliceContainerShape(Structure structure, GraphModel graphModel) {
	super(graphModel);
	this.structure = structure;
	setRandomLayout(false);
	bNoFill = true;
	defaultFGselect = java.awt.Color.red;
	defaultBG = Color.lightGray;
	defaultBGselect = Color.lightGray;
	backgroundColor = defaultBG;
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
		for(Shape shape : childShapeList) {
			if (shape instanceof ReactionStepShape || shape instanceof SpeciesContextShape){
				emptySize.width = 
					Math.max(emptySize.width, 
							shape.getSpaceManager().getRelPos().x +
							shape.getSpaceManager().getSize().width);
				emptySize.height = 
					Math.max(emptySize.height, 
							shape.getSpaceManager().getRelPos().y + 
							shape.getSpaceManager().getSize().height);
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
	// make sure children fit
	int width = getSpaceManager().getSize().width;
	int height = getSpaceManager().getSize().height;

	for(Shape child : childShapeList) {
		if (child.getSpaceManager().getSize().width + child.getSpaceManager().getRelPos().x > width || 
				child.getSpaceManager().getSize().height + child.getSpaceManager().getRelPos().y 
				> height){
			throw new LayoutException("cannot fit all reactions");
		}
	}
}

public void paintSelf ( java.awt.Graphics2D g, int absPosX, int absPosY ) {
	super.paintSelf(g, absPosX, absPosY);
	if(isSelected()){
		drawLabel(g,absPosX,absPosY);
	}
}

public void randomize() {
	// randomize the locations of speciesContexts and of reactionSteps,
	// then draw in the reactionParticipant edges
	for(Shape child : childShapeList) {
		if (child instanceof SpeciesContextShape || child instanceof ReactionStepShape){
			// position normally about the center
			child.getSpaceManager().setRelPos(getRandomPosition());
		}	
	}

	//
	// calculate locations and sizes of reactionParticipant edges
	//
	for(Shape child : childShapeList) {
		if (child instanceof ReactionParticipantShape){
			((ReactionParticipantShape)child).refreshLayout();
		}
	}
	
	//
	// position label
	//
	int centerX = getSpaceManager().getSize().width/2;
	int currentY = labelSize.height;
	labelPos.x = centerX - labelSize.width/2;
	labelPos.y = currentY;
	currentY += labelSize.height;	
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
//	if (getStructure() instanceof Membrane){
//		setLabel(getStructure().getName()+" (Voltage=\""+((Membrane)getStructure()).getMembraneVoltage().getName()+"\")");
//	}else{
		setLabel(getStructure().getName());
//	}
}
}