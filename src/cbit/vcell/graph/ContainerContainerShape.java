package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.sybil.util.lists.ListUtil;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.StructureUtil;


public class ContainerContainerShape extends ContainerShape {
	Model model = null;

	// or all compartments
	List<ReactionContainerShape> structureContainers = null;

	public ContainerContainerShape(GraphModel graphModel, Model model, 
			List<ReactionContainerShape> reactionContainers) {
		super(graphModel);
		this.structureContainers = new ArrayList<ReactionContainerShape>(reactionContainers);
		this.model = model;
		setLabel(" ");
		for(ReactionContainerShape reactionContainer : reactionContainers) {
			addChildShape(reactionContainer);
		}

	}

	@Override
	public Object getModelObject() {
		return model;
	}

	public void addReactionContainer(ReactionContainerShape reactionContainerShape) {
		if(!structureContainers.contains(reactionContainerShape)) {
			structureContainers.add(reactionContainerShape);
		}
		if(!getChildren().contains(reactionContainerShape)) {
			addChildShape(reactionContainerShape);
		}
		try {
			refreshLayout();
		} catch (LayoutException e) {
			e.printStackTrace();
		}
	}
	
	public void removeReactionContainer(ReactionContainerShape reactionContainerShape) {
		while(structureContainers.contains(reactionContainerShape)) {
			structureContainers.remove(reactionContainerShape);
		}
		while(getChildren().contains(reactionContainerShape)) {
			removeChild(reactionContainerShape);
		}
		try {
			refreshLayout();
		} catch (LayoutException e) {
			e.printStackTrace();
		}
	}

	public void setReactionContainerShapeList(List<ReactionContainerShape> newList) {
		structureContainers = new ArrayList<ReactionContainerShape>(newList);
		for(ReactionContainerShape newShape : newList) {
			if(!getChildren().contains(newShape)) {
				addChildShape(newShape);
			}
		}
		Set<Shape> childrenToBeRemoved = new HashSet<Shape>();
		for(Shape child : getChildren()) {
			if(!structureContainers.contains(child)) {
				childrenToBeRemoved.add(child);
			}
		}
		for(Shape childToBeRemoved : childrenToBeRemoved) {
			removeChild(childToBeRemoved);
		}
		try {
			refreshLayout();
		} catch (LayoutException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		// get size when empty
		Dimension emptySize = super.getPreferedSize(g);
		// make larger than empty size so that children fit
		for(Shape shape : childShapeList) {
			if (shape instanceof ReactionContainerShape){
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
			int padding = 8;
			if(i < structureContainers.size() - 1) {
				Structure structure1 = structureContainers.get(i).getStructure();
				Structure structure2 = structureContainers.get(i + 1).getStructure();
				if(StructureUtil.areAdjacent(structure1, structure2)) {
					padding = 0;
				}
			}
			currentX += padding;											
		}
		// if the position of the one being dragged is out of place, reorder and do it again once.
//		if (draggedIndex>=0){
//			int insertionIndex = draggedIndex;
//			int dragX = structureContainers.get(draggedIndex).getSpaceManager().getRelPos().x;
//			for (int i = 0; i < structureContainers.size(); i++) {
//				ReactionContainerShape currentShape = structureContainers.get(i);
//				if (i != draggedIndex){
//					if (dragX >= currentShape.getSpaceManager().getRelPos().x && 
//							dragX < 
//							currentShape.getSpaceManager().getRelPos().x + 
//							currentShape.getSpaceManager().getSize().width/2){
//						insertionIndex = Math.min(i, structureContainers.size() - 1);
//						break;
//					}
//					if (dragX >= 
//						currentShape.getSpaceManager().getRelPos().x + 
//						currentShape.getSpaceManager().getSize().width/2 && 
//						dragX <= 
//							currentShape.getSpaceManager().getRelPos().x + 
//							currentShape.getSpaceManager().getSize().width){
//						insertionIndex = Math.min(i + 1, structureContainers.size() - 1);
//						break;
//					}
//				}
//			}
//			if (insertionIndex!=draggedIndex){
//				synchronized (structureContainers) {
//					ListUtil.shiftElement(structureContainers, draggedIndex, insertionIndex);
//					// layout again
//					currentX = 4;
//					currentY = 0;
//					for (int i = 0; i < structureContainers.size(); i++) {
//						if (i != draggedIndex) {
//							structureContainers.get(i).getSpaceManager().setRelPos(currentX, currentY);
//						}
//						currentX += structureContainers.get(i).getSpaceManager().getSize().width;
//						int padding = 8;
//						if(i < structureContainers.size() - 1) {
//							Structure structure1 = structureContainers.get(i).getStructure();
//							Structure structure2 = structureContainers.get(i + 1).getStructure();
//							if(StructureUtil.areAdjacent(structure1, structure2)) {
//								padding = 0;
//							}
//						}
//						currentX += padding;											
//					}
//				}
//			}
//		}
//		for(Shape child : childShapeList) {
//			if (!(child instanceof ReactionContainerShape)){
//				child.refreshLayout();
//			}
//		}	
	}

	public void paintSelf (Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillRect(absPosX, absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
	}

	@Override
	public void randomize() {
		for (int i = 0; i < structureContainers.size(); i++) {
			structureContainers.get(i).randomize();
		}
	}

	@Override
	public void resize(Graphics2D g, Dimension newSize) throws Exception {
		getSpaceManager().setSize(newSize);
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
		refreshLayout();
	}

	@Override
	public void setRandomLayout(boolean isRandom) {
		for(ReactionContainerShape structureContainer : structureContainers) {
			structureContainer.setRandomLayout(isRandom);
		}
	}

	@Override
	public void notifySelected() {
		notifyUnselected();
	}
}
