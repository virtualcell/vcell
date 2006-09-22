package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;
import java.awt.Point;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
/**
 * This type was created in VisualAge.
 */
public class ContainerContainerShape extends ContainerShape {

	Model model = null;
	
	//
	// either membrane
	//
	ReactionContainerShape insideContainer = null;
	ReactionContainerShape outsideContainer = null;
	ReactionContainerShape membraneContainer = null;

	//
	// or compartment
	//
	ReactionContainerShape featureContainer = null;

/**
 * This method was created in VisualAge.
 * @param graphModel cbit.vcell.graph.GraphModel
 * @param model cbit.vcell.model.Model
 * @param featureContainerShape cbit.vcell.graph.ReactionContainerShape
 */
public ContainerContainerShape(GraphModel graphModel, Model model, ReactionContainerShape featureReactionContainer) {
	super(graphModel);
	this.featureContainer = featureReactionContainer;
	this.model = model;
	setLabel(" ");

	addChildShape(featureContainer);
	
}


/**
 * ContainerContainerShape constructor comment.
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ContainerContainerShape(GraphModel graphModel, Model model, ReactionContainerShape insideReactionContainer, 
								ReactionContainerShape membraneReactionContainer, 
								ReactionContainerShape outsideReactionContainer) {
	super(graphModel);
	this.insideContainer = insideReactionContainer;
	this.membraneContainer = membraneReactionContainer;
	this.outsideContainer = outsideReactionContainer;
	this.model = model;
	setLabel(" ");

	addChildShape(insideReactionContainer);
	addChildShape(membraneReactionContainer);
	addChildShape(outsideReactionContainer);
	
	Structure inside   = (Structure)insideContainer.getModelObject();
	Structure membrane = (Structure)membraneContainer.getModelObject();
	Structure outside  = (Structure)outsideContainer.getModelObject();
	
	if (!(membrane instanceof Membrane) ||
		!(inside instanceof Feature)    ||
		!(outside instanceof Feature)){
		throw new IllegalArgumentException("membrane reactionContainerShape not a membrane");
	}
	if (((Membrane)membrane).getInsideFeature() != inside){
		throw new IllegalArgumentException("inside feature is incorrect");
	}
	if (((Membrane)membrane).getOutsideFeature() != outside){
		throw new IllegalArgumentException("outside feature is incorrect");
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return model;
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
		if (shape instanceof ReactionContainerShape){
			emptySize.width = Math.max(emptySize.width,shape.getLocation().x+shape.getPreferedSize(g).width);
			emptySize.height = Math.max(emptySize.height,shape.getLocation().y+shape.getPreferedSize(g).height);
		}
	}
	return emptySize;
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {

	if (featureContainer!=null){
		//
		// position feature
		//
		featureContainer.screenPos.x = 0;
		featureContainer.screenPos.y = 0;
	}else{
		int currentX = 0;
		int currentY = 0;
		//
		// position outside shape
		//
		outsideContainer.screenPos.x = currentX;
		outsideContainer.screenPos.y = currentY;
		currentX += outsideContainer.screenSize.width;

		//
		// position membrane shape
		//
		membraneContainer.screenPos.x = currentX;
		membraneContainer.screenPos.y = currentY;
		currentX += membraneContainer.screenSize.width;

		//
		// position inside shape
		//
		insideContainer.screenPos.x = currentX;
		insideContainer.screenPos.y = currentY;
		currentX += insideContainer.screenSize.width;
	}
	//
	// layout the edges
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof ReactionContainerShape)){
			child.layout();
		}
	}	
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint ( java.awt.Graphics2D g, int parentOffsetX, int parentOffsetY ) {

	int absPosX = screenPos.x + parentOffsetX;
	int absPosY = screenPos.y + parentOffsetY;

	g.setColor(backgroundColor);
	g.fillRect(absPosX,absPosY,screenSize.width,screenSize.height);
	g.setColor(forgroundColor);
	g.drawRect(absPosX,absPosY,screenSize.width,screenSize.height);

//	g.drawString(getLabel(),labelPos.x,labelPos.y);

	//
	// draw the edges first
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof ReactionContainerShape)){
			child.paint(g,absPosX,absPosY);
		}
	}	

	//
	// draw reactionContainerShapes now (Shape.bNoFill = true, won't overwrite)
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof ReactionContainerShape){
			child.paint(g,absPosX,absPosY);
		}
	}	

}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.graph.Shape
 * @param x int
 * @param y int
 */
public Shape pick(Point point) {

	if (isInside(point)==false) return null;
	Point childPoint = new Point(point.x-screenPos.x,point.y-screenPos.y);

	//
	// pick the reactionContainerShapes first
	//
	Shape childPick = null;
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof ReactionContainerShape){
			Shape tryPick = child.pick(childPoint);
			if (tryPick!=null){
				childPick = tryPick;
				break;
			}
		}
	}
	//
	// if only the reactionContainer got picked, then keep going (look for edges)
	//
	// this allows reactionContainer itself to be transparent to edges for picking purposes
	//
	if (childPick != null){
		//
		// selected a fluxNode or reactionNode
		//
		if (!(childPick instanceof ReactionContainerShape)){
			return childPick;
		}
	}
	
	//
	// pick the edges next
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof ReactionContainerShape)){
			Shape tryPick = child.pick(childPoint);
			if (tryPick!=null){
				childPick = tryPick;
				break;
			}
		}
	}

	//
	// if missed all of the nodes and edges, then return compartment, (or at least the ContainerContainer)
	//
	if (childPick!=null){
		return childPick;
	}else{
		return this;
	}
}


/**
 * This method was created in VisualAge.
 */
public void randomize() {
	if (insideContainer!=null){
		insideContainer.randomize();
	}
	if (membraneContainer!=null){
		membraneContainer.randomize();
	}
	if (outsideContainer!=null){
		outsideContainer.randomize();
	}

	
	if (featureContainer!=null){
		featureContainer.randomize();
	}
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void resize(java.awt.Graphics2D g, java.awt.Dimension newSize) throws Exception {

	screenSize = newSize;

	if (featureContainer!=null){
		//
		// allocate space according to feature layout
		//
		featureContainer.resize(g,  new java.awt.Dimension(screenSize.width, screenSize.height));
	}else{
		//
		// allocate space according to membrane layout
		//
		int insideWidth = insideContainer.getPreferedSize(g).width;
		int membraneWidth = membraneContainer.getPreferedSize(g).width;
		int outsideWidth = outsideContainer.getPreferedSize(g).width;
		int remainingWidth = screenSize.width - insideWidth - membraneWidth - outsideWidth;
//		if (remainingWidth>0){
			insideWidth += remainingWidth*1/6;
			membraneWidth += remainingWidth*4/6;
			outsideWidth += remainingWidth*1/6;
//		}
		insideContainer.resize(g,   new java.awt.Dimension(insideWidth, screenSize.height));
		membraneContainer.resize(g, new java.awt.Dimension(membraneWidth, screenSize.height));
		outsideContainer.resize(g,  new java.awt.Dimension(outsideWidth, screenSize.height));
	}
	
	layout();
}


/**
 * This method was created in VisualAge.
 * @param isRandom boolean
 */
public void setRandomLayout(boolean isRandom) {
//	System.out.println("ReactionContainerShape.setRandomLayout("+isRandom+")");
	if (insideContainer!=null){
		insideContainer.setRandomLayout(isRandom);
	}
	if (membraneContainer!=null){
		membraneContainer.setRandomLayout(isRandom);
	}
	if (outsideContainer!=null){
		outsideContainer.setRandomLayout(isRandom);
	}
	
	
	if (featureContainer!=null){
		featureContainer.setRandomLayout(isRandom);
	}
}
}