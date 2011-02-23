package cbit.gui.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import cbit.vcell.graph.ContainerContainerShape;
import cbit.vcell.graph.ReactionContainerShape;
import cbit.vcell.graph.ReactionStepShape;
import cbit.vcell.graph.SpeciesContextShape;

public class GraphContainerLayoutReactions implements GraphContainerLayout {
	
	public static final Dimension REACTION_CONTAINER_MIN_SIZE = new Dimension(100, 200);
	public static final int HEIGHT_PADDING = 14;
	public static final int WIDTH_PADDING = 14;
	
	public Dimension getPreferedSize(Shape shape, Graphics2D g) {
		if(shape instanceof ContainerContainerShape) {
			return getPreferedSizeContainerContainerShape((ContainerContainerShape)shape, g);
		} else if(shape instanceof ReactionContainerShape) {
			return getPreferedSizeReactionContainerShape((ReactionContainerShape)shape, g);
		}
		return shape.getPreferedSizeSelf(g);
	}
	
	public Dimension getPreferedSizeReactionContainerShape(ReactionContainerShape shape, Graphics2D g) {
		// get size when empty
		Font origFont = g.getFont();
		g.setFont(shape.getLabelFont(g));
		try{
			Dimension preferredSize = shape.getPreferedSizeSelf(g);
			// make larger than empty size so that children fit
			for(Shape child : shape.getChildren()) {
				if (child instanceof ReactionStepShape || child instanceof SpeciesContextShape){
					preferredSize.width = 
						Math.max(preferredSize.width, 
								child.getSpaceManager().getRelPos().x +
								child.getSpaceManager().getSize().width);
					preferredSize.height = 
						Math.max(preferredSize.height, 
								child.getSpaceManager().getRelPos().y + 
								child.getSpaceManager().getSize().height);
				}
			}
			preferredSize.width = preferredSize.width + WIDTH_PADDING;
			preferredSize.height = preferredSize.height + HEIGHT_PADDING;
			if(preferredSize.width < REACTION_CONTAINER_MIN_SIZE.width) {
				preferredSize.width = REACTION_CONTAINER_MIN_SIZE.width;
			}
			if(preferredSize.height < REACTION_CONTAINER_MIN_SIZE.height) {
				preferredSize.height = REACTION_CONTAINER_MIN_SIZE.height;
			}
			return preferredSize;
		} finally {
			g.setFont(origFont);
		}
	}

	public Dimension getPreferedSizeContainerContainerShape(ContainerContainerShape shape, Graphics2D g) {
		// get size when empty
		Dimension emptySize = shape.getPreferedSizeSelf(g);
		// make larger than empty size so that children fit
		int widthSum = 0;
		for(Shape child : shape.getChildren()) {
			if (child instanceof ReactionContainerShape){
				Dimension preferedSizeChild = getPreferedSize(child, g);
				emptySize.width = Math.max(emptySize.width, 
						child.getSpaceManager().getRelPos().x + preferedSizeChild.width);
				widthSum += preferedSizeChild.width;
//				emptySize.width += 8; // spaces between compartments
				emptySize.height = Math.max(emptySize.height, 
						child.getSpaceManager().getRelPos().y + preferedSizeChild.height);
			}
		}
		emptySize.width = Math.max(emptySize.width, widthSum);
		return emptySize;
	}
	
	public void layout(GraphModel graphModel, Graphics2D g2d, Dimension size) {
		Shape topShape = graphModel.getTopShape();
		//
		// compute nominal sizes and positions of children
		//
		if (topShape != null) {
			change_managed(topShape, g2d);
			resize(topShape, this.getPreferedSize(topShape, g2d), g2d);
		}
	}
	
	public void change_managed(Shape shape, Graphics2D g) {
		for (int i = 0; i < shape.countChildren(); i++) {
			Shape child = shape.childShapeList.get(i);
			change_managed(child, g);
		}
		shape.getSpaceManager().setSize(getPreferedSize(shape, g));
		resize(shape, getPreferedSize(shape, g), g);
		shape.refreshLayoutSelf();
		refreshLayoutChildren(shape);
	}
	
	public Point getSeparatorDeepCount(Shape shape) {
		if(shape instanceof SpeciesContextShape) {
			return new Point(0,0);
		} else if(shape instanceof ReactionStepShape) {
			return new Point(0,0);
		} else {
			return getSeparatorDeepCountShape(shape);
		}
	}
	
	public Point getSeparatorDeepCountShape(Shape shape) {
		int selfCountX = 1;
		if (shape.countChildren() > 0) {
			selfCountX++;
		}
		int selfCountY = shape.countChildren() + 1;
		Point totalDeepCount = new Point(selfCountX, selfCountY);
		for (int i = 0; i < shape.countChildren(); i++) {
			Shape child = shape.childShapeList.get(i);
			Point childCount = getSeparatorDeepCount(child);
			totalDeepCount.x += childCount.x;
			totalDeepCount.y += childCount.y;
		}
		return totalDeepCount;
	}

	public void resize(Shape shape, Dimension newSize, Graphics2D g) {
		if(shape instanceof ContainerContainerShape) {
			resizeContainerContainerShape((ContainerContainerShape)shape, newSize, g);
		} else if(shape instanceof SpeciesContextShape) {
			return;
		} else if(shape instanceof ReactionStepShape) {
			return;
		} else {
			resizeShape(shape, newSize, g);
		}
	}
	
	public void resizeContainerContainerShape(ContainerContainerShape shape, Dimension newSize,
			Graphics2D g) {
		shape.getSpaceManager().setSize(newSize);
		int remainingWidth = shape.getSpaceManager().getSize().width;
		int[] widths = new int[shape.getStructureContainers().size()];
		for (int i = 0; i < shape.getStructureContainers().size(); i++) {
			widths[i] = getPreferedSize(shape.getStructureContainers().get(i), g).width;
			remainingWidth -= widths[i];
//			remainingWidth -= 8;// TODO make 8 (space between reaction slices) a field
		}
		for (int i = 0; i < widths.length; i++) {
			int extraWidth = remainingWidth / shape.getStructureContainers().size(); // redistribute evenly
			if (i==0){ // add extra width to first element.
				extraWidth += remainingWidth % shape.getStructureContainers().size();
			}
			resize(shape.getStructureContainers().get(i), new Dimension(widths[i] + extraWidth, 
			shape.getSpaceManager().getSize().height), g);
		}
		refreshLayoutChildren(shape);
	}
	

	public void resizeShape(Shape shape, Dimension newSize, Graphics2D g) {
		int deltaX = newSize.width - shape.getSpaceManager().getSize().width;
		int deltaY = newSize.height - shape.getSpaceManager().getSize().height;
		shape.getSpaceManager().setSize(newSize);
		// allocate extra new space according to deep child count of children
		Point totalDeepCount = getSeparatorDeepCount(shape);
		for (int i = 0; i < shape.countChildren(); i++) {
			Shape child = shape.getChildren().get(i);
			Point childDeepCount = getSeparatorDeepCount(child);
			Dimension childSize = new Dimension(child.getSpaceManager().getSize());
			childSize.width += deltaX * childDeepCount.x / totalDeepCount.x;
			childSize.height += deltaY * childDeepCount.y / totalDeepCount.y;
			resize(child, childSize, g);
		}
		shape.refreshLayoutSelf();
		refreshLayoutChildren(shape);
	}
	
	public void refreshLayoutChildren(Shape shape) {
		if(shape instanceof ContainerContainerShape) {
			refreshLayoutChildrenContainerContainerShape((ContainerContainerShape) shape);
		}
	}
	
	public void refreshLayoutChildrenContainerContainerShape(ContainerContainerShape shape) {
		int draggedIndex = -1;
		List<ReactionContainerShape> structureContainers = shape.getStructureContainers();
		for (int i = 0; i < structureContainers.size(); i++) {
			if (shape.getStructureContainers().get(i).isBeingDragged){
				draggedIndex = i;
			}
		}
		int currentX = 0;
		int currentY = 0;
		for (int i = 0; i < structureContainers.size(); i++) {
			if (i != draggedIndex) {
				structureContainers.get(i).getSpaceManager().setRelPos(currentX, currentY);
			}
			currentX += structureContainers.get(i).getSpaceManager().getSize().width;
//			int padding = 8;
//			if(i < structureContainers.size() - 1) {
//				Structure structure1 = structureContainers.get(i).getStructure();
//				Structure structure2 = structureContainers.get(i + 1).getStructure();
//				if(StructureUtil.areAdjacent(structure1, structure2)) {
//					padding = 0;
//				}
//			}
//			currentX += padding;											
		}
	}

	public Rectangle getBoundaryForAutomaticLayout(Shape shape) {
		if(shape instanceof ReactionContainerShape) {
			Point absLoc = shape.getAbsPos();
			Dimension size = shape.getSize();
			return new Rectangle(absLoc.x + WIDTH_PADDING, absLoc.y + HEIGHT_PADDING, 
					size.width - 2*WIDTH_PADDING, size.height - 2*HEIGHT_PADDING);			
		}
		return new Rectangle(shape.getAbsPos(), shape.getSize());
	}
	
}
