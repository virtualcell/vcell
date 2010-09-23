package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;


public class ContainerContainerShape extends ContainerShape {
	Model model = null;
	// either membrane
	ReactionContainerShape insideContainer = null;
	ReactionContainerShape outsideContainer = null;
	ReactionContainerShape membraneContainer = null;
	// or compartment
	ReactionContainerShape featureContainer = null;

	public ContainerContainerShape(GraphModel graphModel, Model model, 
			ReactionContainerShape featureReactionContainer) {
		super(graphModel);
		this.featureContainer = featureReactionContainer;
		this.model = model;
		setLabel(" ");

		addChildShape(featureContainer);

	}

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
	public Dimension getPreferedSize(Graphics2D g) {
		// get size when empty
		Dimension emptySize = super.getPreferedSize(g);
		// make larger than empty size so that children fit
		for (int i = 0; i < childShapeList.size(); i++){
			Shape shape = childShapeList.get(i);
			if (shape instanceof ReactionContainerShape){
				emptySize.width = 
					Math.max(emptySize.width, 
							shape.getSpaceManager().getRelPos().x + shape.getPreferedSize(g).width);
				emptySize.height = 
					Math.max(emptySize.height, 
							shape.getSpaceManager().getRelPos().y + shape.getPreferedSize(g).height);
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
			currentX += outsideContainer.getSpaceManager().getSize().width;
			// position membrane shape
			membraneContainer.getSpaceManager().setRelPos(currentX, currentY);
			currentX += membraneContainer.getSpaceManager().getSize().width;
			// position inside shape
			insideContainer.getSpaceManager().setRelPos(currentX, currentY);
			currentX += insideContainer.getSpaceManager().getSize().width;
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
		graphics.fillRect(xThis, yThis, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		graphics.setColor(forgroundColor);
		graphics.drawRect(xThis, yThis, getSpaceManager().getSize().width, getSpaceManager().getSize().height);		
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
	public void resize(Graphics2D g, Dimension newSize) throws Exception {
		getSpaceManager().setSize(newSize);
		if (featureContainer!=null){
			// allocate space according to feature layout
			featureContainer.resize(g,  new Dimension(getSpaceManager().getSize()));
		} else {
			// allocate space according to membrane layout
			int insideWidth = insideContainer.getPreferedSize(g).width;
			int membraneWidth = membraneContainer.getPreferedSize(g).width;
			int outsideWidth = outsideContainer.getPreferedSize(g).width;
			int remainingWidth = getSpaceManager().getSize().width - insideWidth - membraneWidth - outsideWidth;
			insideWidth += remainingWidth*1/6;
			membraneWidth += remainingWidth*4/6;
			outsideWidth += remainingWidth*1/6;
			insideContainer.resize(g,   new Dimension(insideWidth, getSpaceManager().getSize().height));
			membraneContainer.resize(g, new Dimension(membraneWidth, getSpaceManager().getSize().height));
			outsideContainer.resize(g,  new Dimension(outsideWidth, getSpaceManager().getSize().height));
		}
		refreshLayout();
	}

	@Override
	public void setRandomLayout(boolean isRandom) {
		if (insideContainer != null){
			insideContainer.setRandomLayout(isRandom);
		}
		if (membraneContainer != null){
			membraneContainer.setRandomLayout(isRandom);
		}
		if (outsideContainer != null){
			outsideContainer.setRandomLayout(isRandom);
		}
		if (featureContainer != null){
			featureContainer.setRandomLayout(isRandom);
		}
	}
}