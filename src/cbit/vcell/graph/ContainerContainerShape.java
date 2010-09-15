package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import java.awt.*;

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
	public ContainerContainerShape(GraphModel graphModel, Model model, 
			ReactionContainerShape featureReactionContainer) {
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
	public ContainerContainerShape(GraphModel graphModel, Model model, 
			ReactionContainerShape insideReactionContainer, 
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

		Structure inside   = (Structure) insideContainer.getModelObject();
		Structure membrane = (Structure) membraneContainer.getModelObject();
		Structure outside  = (Structure) outsideContainer.getModelObject();

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

	@Override
	public Object getModelObject() { return model; }

	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		// get size when empty
		Dimension emptySize = super.getPreferedSize(g);
		// make larger than empty size so that children fit
		for (int i = 0; i < childShapeList.size(); i++){
			Shape shape = childShapeList.get(i);
			if (shape instanceof ReactionContainerShape){
				emptySize.width = 
					Math.max(emptySize.width, shape.getLocation().x + shape.getPreferedSize(g).width);
				emptySize.height = 
					Math.max(emptySize.height, shape.getLocation().y + shape.getPreferedSize(g).height);
			}
		}
		return emptySize;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		if (featureContainer!=null){
			// position feature
			featureContainer.getSpaceManager().setRelPos(0, 0);
		}else{
			int currentX = 0;
			int currentY = 0;
			// position outside shape
			outsideContainer.getSpaceManager().setRelPos(currentX, currentY);
			currentX += outsideContainer.shapeSize.width;
			// position membrane shape
			membraneContainer.getSpaceManager().setRelPos(currentX, currentY);
			currentX += membraneContainer.shapeSize.width;
			// position inside shape
			insideContainer.getSpaceManager().setRelPos(currentX, currentY);
			currentX += insideContainer.shapeSize.width;
		}
		// layout the edges
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (!(child instanceof ReactionContainerShape)){
				child.refreshLayout();
			}
		}	
	}

	@Override
	public void paintSelf(Graphics2D graphics, int xThis, int yThis) {
		graphics.setColor(backgroundColor);
		graphics.fillRect(xThis,yThis, shapeSize.width, shapeSize.height);
		graphics.setColor(forgroundColor);
		graphics.drawRect(xThis, yThis, shapeSize.width, shapeSize.height);		
	}

	@Override
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

	@Override
	public void resize(java.awt.Graphics2D g, java.awt.Dimension newSize) throws Exception {
		shapeSize = newSize;
		if (featureContainer!=null){
			// allocate space according to feature layout
			featureContainer.resize(g,  new java.awt.Dimension(shapeSize.width, shapeSize.height));
		} else {
			// allocate space according to membrane layout
			int insideWidth = insideContainer.getPreferedSize(g).width;
			int membraneWidth = membraneContainer.getPreferedSize(g).width;
			int outsideWidth = outsideContainer.getPreferedSize(g).width;
			int remainingWidth = shapeSize.width - insideWidth - membraneWidth - outsideWidth;
			//		if (remainingWidth>0){
			insideWidth += remainingWidth*1/6;
			membraneWidth += remainingWidth*4/6;
			outsideWidth += remainingWidth*1/6;
			//		}
			insideContainer.resize(g,   new java.awt.Dimension(insideWidth, shapeSize.height));
			membraneContainer.resize(g, new java.awt.Dimension(membraneWidth, shapeSize.height));
			outsideContainer.resize(g,  new java.awt.Dimension(outsideWidth, shapeSize.height));
		}
		refreshLayout();
	}

	@Override
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