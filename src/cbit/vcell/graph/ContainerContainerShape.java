package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.sybil.util.lists.ListUtil;

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

	// or all compartments
	List<ReactionSliceContainerShape> structureContainers = null;

	public ContainerContainerShape(GraphModel graphModel, Model model, 
			ReactionContainerShape featureReactionContainer) {
		super(graphModel);
		this.featureContainer = featureReactionContainer;
		this.model = model;
		setLabel(" ");

		addChildShape(featureContainer);

	}

	public ContainerContainerShape(GraphModel graphModel, Model model, 
			List<ReactionSliceContainerShape> reactionContainers) {
		super(graphModel);
		this.structureContainers = new ArrayList<ReactionSliceContainerShape>(reactionContainers);
		this.model = model;
		setLabel(" ");

		for(ReactionSliceContainerShape reactionContainer : reactionContainers) {
			addChildShape(reactionContainer);
		}

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

	@Override
	public Object getModelObject() {
		return model;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		// get size when empty
		Dimension emptySize = super.getPreferedSize(g);
		// make larger than empty size so that children fit
		for(Shape shape : childShapeList) {
			if (shape instanceof ReactionSliceContainerShape){
				emptySize.width = Math.max(emptySize.width, 
						shape.getSpaceManager().getRelPos().x + shape.getPreferedSize(g).width);
				emptySize.width += 8; // spaces between compartments
				emptySize.height = Math.max(emptySize.height, 
						shape.getSpaceManager().getRelPos().y+shape.getPreferedSize(g).height);
			}
		}
		return emptySize;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		if (featureContainer!=null){
			// position feature
			featureContainer.getSpaceManager().setRelPos(0, 0);
		} else if (membraneContainer!=null){
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
		} else if (structureContainers!=null){
			int draggedIndex = -1;
			for (int i = 0; i < structureContainers.size(); i++) {
				if (structureContainers.get(i).isBeingDragged){
					draggedIndex = i;
				}
			}
			// preliminary layout (before reordering)
			int currentX = 4;
			int currentY = 0;
			for (int i = 0; i < structureContainers.size(); i++) {
				if (i != draggedIndex) {
					structureContainers.get(i).getSpaceManager().setRelPos(currentX, currentY);
				}
				currentX += structureContainers.get(i).getSpaceManager().getSize().width;
				currentX += 8;
			}
			// if the position of the one being dragged is out of place, reorder and do it again once.
			if (draggedIndex>=0){
				int insertionIndex = draggedIndex;
				int dragX = structureContainers.get(draggedIndex).getSpaceManager().getRelPos().x;
				for (int i = 0; i < structureContainers.size(); i++) {
					ReactionSliceContainerShape currentShape = structureContainers.get(i);
					if (i != draggedIndex){
						if (dragX >= currentShape.getSpaceManager().getRelPos().x && 
								dragX < 
								currentShape.getSpaceManager().getRelPos().x + 
								currentShape.getSpaceManager().getSize().width/2){
							insertionIndex = Math.min(i, structureContainers.size() - 1);
							break;
						}
						if (dragX >= 
							currentShape.getSpaceManager().getRelPos().x + 
							currentShape.getSpaceManager().getSize().width/2 && 
							dragX <= 
								currentShape.getSpaceManager().getRelPos().x + 
								currentShape.getSpaceManager().getSize().width){
							insertionIndex = Math.min(i + 1, structureContainers.size() - 1);
							break;
						}
					}
				}
				if (insertionIndex!=draggedIndex){
					synchronized (structureContainers) {
						ListUtil.shiftElement(structureContainers, draggedIndex, insertionIndex);
						// layout again
						currentX = 4;
						currentY = 0;
						for(ReactionSliceContainerShape structureContainer : structureContainers) {
							if (!structureContainer.isBeingDragged){
								structureContainer.getSpaceManager().setRelPos(currentX, currentY);
							}
							currentX += structureContainer.getSpaceManager().getSize().width;
							currentX += 8;
						}
					}
				}
			}
		}
		for(Shape child : childShapeList) {
			if (!(child instanceof ReactionSliceContainerShape)){
				child.refreshLayout();
			}
		}	
	}

	public void paintSelf ( java.awt.Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillRect(absPosX, absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
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

		if (structureContainers!=null){
			for (int i = 0; i < structureContainers.size(); i++) {
				structureContainers.get(i).randomize();
			}
		}
	}

	@Override
	public void resize(Graphics2D g, Dimension newSize) throws Exception {
		getSpaceManager().setSize(newSize);
		if (featureContainer!=null){
			// allocate space according to feature layout
			featureContainer.resize(g,  getSpaceManager().getSize());
		}else if (membraneContainer!=null){
			// allocate space according to membrane layout
			int insideWidth = insideContainer.getPreferedSize(g).width;
			int membraneWidth = membraneContainer.getPreferedSize(g).width;
			int outsideWidth = outsideContainer.getPreferedSize(g).width;
			int remainingWidth = 
				getSpaceManager().getSize().width - insideWidth - membraneWidth - outsideWidth;
			insideWidth += remainingWidth*1/6;
			membraneWidth += remainingWidth*4/6;
			outsideWidth += remainingWidth*1/6;
			insideContainer.resize(g,   new Dimension(insideWidth, getSpaceManager().getSize().height));
			membraneContainer.resize(g, new Dimension(membraneWidth, getSpaceManager().getSize().height));
			outsideContainer.resize(g,  new Dimension(outsideWidth, getSpaceManager().getSize().height));
		} else if (structureContainers!=null){
			int remainingWidth = getSpaceManager().getSize().width;
			int[] widths = new int[structureContainers.size()];
			for (int i = 0; i < structureContainers.size(); i++) {
				widths[i] = structureContainers.get(i).getPreferedSize(g).width;
				remainingWidth -= widths[i];
				remainingWidth -= 8;// TODO make 8 (space between reaction slices) a field
			}
			for (int i = 0; i < widths.length; i++) {
				int extraWidth = remainingWidth / structureContainers.size(); // redistribute evenly
				if (i==0){ // add extra width to first element.
					extraWidth += remainingWidth % structureContainers.size();
				}
				structureContainers.get(i).resize(g, new Dimension(widths[i] + extraWidth, 
						getSpaceManager().getSize().height));
			}
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
		if (structureContainers!=null){
			for(ReactionSliceContainerShape structureContainer : structureContainers) {
				structureContainer.setRandomLayout(isRandom);
			}
		}
	}
}
