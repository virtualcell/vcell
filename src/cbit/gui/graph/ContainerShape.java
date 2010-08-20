package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * This type was created in VisualAge.
 */
public abstract class ContainerShape extends RectangleShape {
	private boolean bRandomize = false;
	private java.util.Random rand = new java.util.Random();

/**
 * ReactionContainerShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ContainerShape(GraphModel graphModel) {
	super(graphModel);
	screenSize.width = 100;
	screenSize.height = 100;
}

public java.awt.Point getRandomPosition() {

	double meanX = screenSize.width/2;
	double meanY = screenSize.height/2;
	double sdX = screenSize.width/8;
	double sdY = screenSize.height/8;

	double randX = rand.nextGaussian();
	double randY = rand.nextGaussian();
	
	java.awt.Point randPos = new java.awt.Point();
	//
	// position normally about the center
	//
	randPos.x = (int)(sdX*randX + meanX);
	randPos.y = (int)(sdY*randY + meanY);
	//
	// make sure that they fit
	//
	double maxObjectWidth = 50;
	double maxObjectHeight = 50;
	
	randPos.x = (int)Math.max(maxObjectWidth/2,Math.min(randPos.x, screenSize.width - maxObjectWidth/2));
	randPos.y = (int)Math.max(maxObjectHeight/2,Math.min(randPos.y, screenSize.height - maxObjectHeight/2));

	return randPos;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
protected boolean isRandom() {
	return bRandomize;
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {
//System.out.println("ReactionContainerShape.layout(), bRandomize="+bRandomize);
	super.layout();
	if (bRandomize){
		randomize();
	}
}

public void randomize() {
	return;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel(null);
}


/**
 * This method was created in VisualAge.
 * @param isRandom boolean
 */
public void setRandomLayout(boolean isRandom) {
//	System.out.println("ReactionContainerShape.setRandomLayout("+isRandom+")");
	this.bRandomize = isRandom;
}
}