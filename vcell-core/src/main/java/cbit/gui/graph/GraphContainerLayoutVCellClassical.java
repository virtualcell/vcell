/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ConcurrentModificationException;
import java.util.List;

import cbit.gui.graph.GraphModel.NotReadyException;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxShape;
import cbit.vcell.constraints.graph.ConstraintGraphNode;
import cbit.vcell.graph.ContainerContainerShape;
import cbit.vcell.graph.FeatureShape;
import cbit.vcell.graph.GeometryClassLegendShape;
import cbit.vcell.graph.GeometryContextContainerShape;
import cbit.vcell.graph.GeometryContextGeometryShape;
import cbit.vcell.graph.GeometryContextStructureShape;
import cbit.vcell.graph.MembraneShape;
import cbit.vcell.graph.ReactionContainerShape;
import cbit.vcell.graph.ReactionStepShape;
import cbit.vcell.graph.SpeciesContextShape;
import cbit.vcell.graph.StructureMappingStructureShape;
import cbit.vcell.graph.StructureShape;
import cbit.vcell.graph.SubVolumeContainerShape;
import cbit.vcell.model.Structure;
import cbit.vcell.model.StructureUtil;

public class GraphContainerLayoutVCellClassical implements GraphContainerLayout {
	
	public Dimension getPreferedSize(Shape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		try {
			if(shape instanceof SimpleContainerShape) {
				return getPreferedSizeSimpleContainerShape((SimpleContainerShape) shape, g);
			} else if(shape instanceof ContainerContainerShape) {
				return getPreferedSizeContainerContainerShape((ContainerContainerShape)shape, g);
			} else if(shape instanceof GeometryContextContainerShape) {
				return getPreferedSizeGeometryContextContainerShape((GeometryContextContainerShape)shape, g);
			} else if(shape instanceof GeometryContextGeometryShape) {
				return getPreferedSizeGeometryContextGeometryShape((GeometryContextGeometryShape)shape, g);
			} else if(shape instanceof GeometryContextStructureShape) {
				return getPreferedSizeGeometryContextStructureShape((GeometryContextStructureShape)shape, g);
			} else if(shape instanceof ReactionContainerShape) {
				return getPreferedSizeReactionContainerShape((ReactionContainerShape)shape, g);
			} else if(shape instanceof MembraneShape) {
				return getPreferedSizeMembraneShape((MembraneShape) shape, g);
			} else if(shape instanceof StructureMappingStructureShape) {
				return getPreferedSizeStructureMappingFeatureShape((StructureMappingStructureShape) shape, g);
			} else if(shape instanceof FeatureShape) {
				return getPreferedSizeFeatureShape((FeatureShape) shape, g);
			} else {
				return shape.getPreferedSizeSelf(g);			
			}
		} catch (NullPointerException exception) {
			throw new GraphModel.NotReadyException(exception);
		}
	}
	
	public Dimension getPreferedSizeStructureMappingFeatureShape(
			StructureMappingStructureShape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		FontMetrics fm = g.getFontMetrics();
		shape.getLabelSize().height = fm.getMaxAscent() + fm.getMaxDescent();
		shape.getLabelSize().width = fm.stringWidth(shape.getLabel());
		if(shape.countChildren() > 0) {
			int totalHeight = shape.getLabelSize().height;
			int maxWidth = shape.getLabelSize().width;
			for(Shape child : shape.getChildren()) {
				Dimension childDim = getPreferedSize(child, g);
				totalHeight += childDim.height + StructureShape.defaultSpacingY;
				maxWidth = Math.max(maxWidth,childDim.width);
			}
			shape.getSpaceManager().setSizePreferred((maxWidth + StructureShape.defaultSpacingX*2), 
					(totalHeight + StructureShape.defaultSpacingY));
		} else {
			shape.getSpaceManager().setSizePreferred(
					(shape.getLabelSize().width + StructureShape.defaultSpacingX*2), 
					(shape.getLabelSize().height + StructureShape.defaultSpacingY*2));
		}	
		return shape.getSpaceManager().getSizePreferred();
	}

	public Dimension getPreferedSizeReactionContainerShape(ReactionContainerShape shape, Graphics2D g) {
		// get size when empty
		Font origFont = g.getFont();
		g.setFont(shape.getLabelFont(g));
		try{
			Dimension emptySize = shape.getPreferedSizeSelf(g);
			// make larger than empty size so that children fit
			for(Shape child : shape.getChildren()) {
				if (child instanceof ReactionStepShape || child instanceof SpeciesContextShape){
					emptySize.width = 
						Math.max(emptySize.width, 
								child.getSpaceManager().getRelPos().x +
								child.getSpaceManager().getSize().width);
					emptySize.height = 
						Math.max(emptySize.height, 
								child.getSpaceManager().getRelPos().y + 
								child.getSpaceManager().getSize().height);
				}
			}
			emptySize.width = emptySize.width + emptySize.width/10;
			emptySize.height = emptySize.height + emptySize.height/10;
			return emptySize;
		} finally {
			g.setFont(origFont);
		}
	}

	public Dimension getPreferedSizeMembraneShape(MembraneShape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		FontMetrics fm = g.getFontMetrics();
		shape.setLabelSize(fm.stringWidth(shape.getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		// has 1 child (featureShape)
		if (shape.childShapeList.size()==1){
		FeatureShape featureShape = (FeatureShape)shape.childShapeList.get(0);
		Dimension featureDim = getPreferedSize(featureShape, g);
		shape.getSpaceManager().setSizePreferred((featureDim.width + MembraneShape.memSpacingX*2), 
				(featureDim.height + MembraneShape.memSpacingY*2));
		}
		return shape.getSpaceManager().getSizePreferred();
	}

	public Dimension getPreferedSizeGeometryContextStructureShape(
			GeometryContextStructureShape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		Dimension d = shape.getPreferedSizeSelf(g);
		Dimension childDim = null;
		if (shape.countChildren() != 0){
			childDim = getPreferedSize(shape.getChildren().get(0), g);
		} else {
			childDim = new Dimension(100,100);
		}
		Dimension newDim = new Dimension(childDim.width, d.height+childDim.height);
		return newDim;
	}

	public Dimension getPreferedSizeGeometryContextGeometryShape(
			GeometryContextGeometryShape shape, Graphics2D g) throws GraphModel.NotReadyException {
		Dimension d = shape.getPreferedSizeSelf(g);
		int height = 2 * shape.getLabelSize().height + 2;
		Dimension childDim = new Dimension();
		try {
			if (shape.countChildren() > 0){
				for(Shape child : shape.getChildren()) {
					childDim.height += getPreferedSize(child, g).height;
					childDim.width = Math.max(childDim.width, getPreferedSize(child, g).width);
				}
			}else{
				childDim = new Dimension(100,100);
			}
		} catch(ConcurrentModificationException exception) {
			throw new GraphModel.NotReadyException(exception);
		}
		Dimension newDim = new Dimension(Math.max(d.width,childDim.width)+10, height+childDim.height);
		return newDim;
	}

	public Dimension getPreferedSizeGeometryContextContainerShape(
			GeometryContextContainerShape shape, Graphics2D g) throws GraphModel.NotReadyException {
		Dimension geometryChildDim = getPreferedSize(shape.getGeometryContainer(), g);
		Dimension structureChildDim = getPreferedSize(shape.getStructureContainer(), g);
		Dimension newDim = 
			new Dimension(geometryChildDim.width + structureChildDim.width, 
					Math.max(geometryChildDim.height,structureChildDim.height));
		return newDim;
	}

	public Dimension getPreferedSizeContainerContainerShape(ContainerContainerShape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		// get size when empty
		Dimension emptySize = shape.getPreferedSizeSelf(g);
		// make larger than empty size so that children fit
		for(Shape child : shape.getChildren()) {
			if (child instanceof ReactionContainerShape){
				emptySize.width = Math.max(emptySize.width, 
						child.getSpaceManager().getRelPos().x + getPreferedSize(child, g).width);
//				emptySize.width += 8; // spaces between compartments
				emptySize.height = Math.max(emptySize.height, 
						child.getSpaceManager().getRelPos().y + getPreferedSize(child, g).height);
			}
		}
		return emptySize;
	}
	
	public Dimension getPreferedSizeSimpleContainerShape(SimpleContainerShape shape, Graphics2D g) {
		// get size when empty
		Dimension emptySize = shape.getPreferedSizeSelf(g);
		// make larger than empty size so that children fit
		for(Shape child : shape.getChildren()) {
			if (!(child instanceof EdgeShape)){
				emptySize.width = 
					Math.max(emptySize.width, 
							child.getSpaceManager().getRelPos().x + 
							child.getSpaceManager().getSize().width);
				emptySize.height = 
					Math.max(emptySize.height, 
							child.getSpaceManager().getRelPos().y + 
							child.getSpaceManager().getSize().height);
			}
		}
		return emptySize;
	}
	
	public Dimension getPreferedSizeFeatureShape(FeatureShape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
		Font origfont = g.getFont();
		g.setFont(shape.getLabelFont(g));
		FontMetrics fm = g.getFontMetrics();
		shape.setLabelSize(fm.stringWidth(shape.getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		shape.getLabelPos().x = shape.getSpaceManager().getSize().width/2 - 
		fm.stringWidth(shape.getLabel())/2; 
		shape.getLabelPos().y = 5 + fm.getMaxAscent();
		try{
			if (shape.countChildren() > 0){
				int structCount = 0;
				int childStructureTotalHeight = shape.getLabelSize().height;
				int childStructureMaxWidth = shape.getLabelSize().width;
				for(Shape child : shape.getChildren()) {
					if(child instanceof StructureShape){
						structCount+= 1;
						Dimension childDim = getPreferedSize(child, g);
						childStructureTotalHeight += childDim.height + StructureShape.defaultSpacingY;
						childStructureMaxWidth = Math.max(childStructureMaxWidth,childDim.width);
					}
				}
				int scsCount = 0;
				//int childSCTotalHeight = 0;
				int childSCMaxWidth = 0;
				int childSCMaxHeight = 0;
				for(Shape child : shape.getChildren()) {
					if(child instanceof SpeciesContextShape){
						scsCount+= 1;
						Dimension childDim = getPreferedSize(child, g);
						childSCMaxHeight = Math.max(childSCMaxHeight,
								childDim.height + ((SpeciesContextShape)child).getSmallLabelSize().height);
						childSCMaxWidth = Math.max(childSCMaxWidth,childDim.width);
						childSCMaxWidth = Math.max(childSCMaxWidth,
								((SpeciesContextShape)child).getSmallLabelSize().width);
					}
				}
				Dimension box = new Dimension();
				if(scsCount != 0){
					int squareBoxX = (int)Math.ceil(Math.sqrt(scsCount));
					int squareBoxY = squareBoxX - 
					(int)(((squareBoxX*squareBoxX)%scsCount)/(double)squareBoxX);
					int width1 = (squareBoxX * (childSCMaxWidth+ 0/*defaultSpacingX*/));
					// + defaultSpacingX;
					int height1 = (squareBoxY * (childSCMaxHeight+ 30/*defaultSpacingY*/));
					// + defaultSpacingY;
					box = new Dimension(width1,height1);
				}
				if(structCount == 0) {
					shape.getSpaceManager().setSizePreferred((box.width + 
							StructureShape.defaultSpacingX*2), 
							(box.height + StructureShape.defaultSpacingY*2));
				}else{
					shape.getSpaceManager().setSizePreferred((childStructureMaxWidth + box.width + 
							StructureShape.defaultSpacingX*2), 
							(Math.max(childStructureTotalHeight,box.height) + 
									StructureShape.defaultSpacingY*2));
				}
			} else {
				shape.getSpaceManager().setSizePreferred((shape.getLabelSize().width + 
						StructureShape.defaultSpacingX*2), 
						(shape.getLabelSize().height + StructureShape.defaultSpacingY*2));
			}	
			return shape.getSpaceManager().getSizePreferred();
		} finally {
			g.setFont(origfont);
		}
	}
	
	public void layout(GraphModel graphModel, Graphics2D g2d, Dimension size) 
	throws GraphModel.NotReadyException {
		Shape topShape = graphModel.getTopShape();
		//
		// compute nominal sizes and positions of children
		//
		if (topShape != null) {
			change_managed(topShape, g2d);
			resize(topShape, size, g2d);
		}
	}
	
	public void change_managed(Shape shape, Graphics2D g) 
	throws GraphModel.NotReadyException {
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
		if(shape instanceof SubVolumeContainerShape) {
			return new Point(0,0);
		} else if(shape instanceof StructureMappingStructureShape) {
			return getSeparatorDeepCountStructureMappingFeatureShape(shape);
		} else if(shape instanceof SpeciesContextShape) {
			return new Point(0,0);
		} else if(shape instanceof ReactionStepShape) {
			return new Point(0,0);
		} else if(shape instanceof NodeShape) {
			return new Point(0,0);
		} else if(shape instanceof GeometryClassLegendShape) {
			return new Point(1,1);
		} else if(shape instanceof FeatureShape) {
			return getSeparatorDeepCountFeatureShape(shape);
		} else if(shape instanceof ConstraintGraphNode) {
			return new Point(0,0);
		} else if(shape instanceof BioPaxShape) {
			return new Point(0,0);
		} else {
			return getSeparatorDeepCountShape(shape);
		}
	}
	
	public Point getSeparatorDeepCountStructureMappingFeatureShape(Shape shape) {
		int selfCountX = 1;
		int selfCountY = 1;
		// column is StructureShapes
		Point sepCount = new Point(selfCountX,selfCountY);
		for (int i = 0; i < shape.countChildren(); i++) {
			Shape child = shape.childShapeList.get(i);
			if (child instanceof StructureShape) {
				Point childCount = getSeparatorDeepCount(child);
				sepCount.x = Math.max(sepCount.x, childCount.x);
				sepCount.y += childCount.y + 1;
			}
		}
		if (shape.countChildren() > 0) {
			sepCount.x += 2;
		}
		return sepCount;
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

	public Point getSeparatorDeepCountFeatureShape(Shape shape) {
		int selfCountX = 1;
		if (shape.countChildren()>0){
			selfCountX++;
		}		
		int selfCountY = 1;
		// column1 is speciesContextShapes
		Point column1 = new Point();
		int scShapeCount = 0;
		for (int i=0;i<shape.countChildren();i++){
			Shape child = shape.childShapeList.get(i);
			if (child instanceof SpeciesContextShape){
				scShapeCount++;
			}
		}	
		column1.x = (scShapeCount>0)?1:0;
		column1.y = (scShapeCount>0)?scShapeCount/2:0;	
		// column2 is StructureShapes
		Point column2 = new Point();
		for (int i=0;i<shape.countChildren();i++){
			Shape child = shape.childShapeList.get(i);
			if (child instanceof StructureShape){
				Point childCount = getSeparatorDeepCount(child);
				column2.x += childCount.x;
				column2.y += childCount.y;
			}	
		}
		return new Point(selfCountX+column1.x+column2.x,selfCountY+Math.max(column1.y,column2.y));
	}

	public void resize(Shape shape, Dimension newSize, Graphics2D g) throws NotReadyException {
		if(shape instanceof GeometryContextContainerShape) {
			resizeGeometryContextContainerShape((GeometryContextContainerShape) shape, newSize, g);
		} else if(shape instanceof ContainerContainerShape) {
			resizeContainerContainerShape((ContainerContainerShape)shape, newSize, g);
		} else if(shape instanceof SubVolumeContainerShape) {
			return;
		} else if(shape instanceof SpeciesContextShape) {
			return;
		} else if(shape instanceof ReactionStepShape) {
			return;
		} else if(shape instanceof NodeShape) {
			return;
		} else if(shape instanceof GeometryClassLegendShape) {
			return;
		} else if(shape instanceof ConstraintGraphNode) {
			return;
		} else if(shape instanceof BioPaxShape) {
			return;
//		} else if(shape instanceof MembraneShape) {
//			resizeMembraneShape((MembraneShape) shape, newSize, g);
		} else {
			resizeShape(shape, newSize, g);
		}
	}
	
	public void resizeGeometryContextContainerShape(GeometryContextContainerShape shape, Dimension newSize,
			Graphics2D g) 
	throws GraphModel.NotReadyException {
		shape.getSpaceManager().setSize(newSize);
		// try to make geometryContainer have full width and structureContainer have rest
		int geomNewWidth = Math.min(shape.getSpaceManager().getSize().width - 10, 
				getPreferedSize(shape.getGeometryContainer(), g).width);
		int structNewWidth = shape.getSpaceManager().getSize().width - geomNewWidth - 1;
		resize(shape.getStructureContainer(), new Dimension(structNewWidth, 
		shape.getSpaceManager().getSize().height - shape.getLabelSize().height), g);
		resize(shape.getGeometryContainer(), new Dimension(geomNewWidth, 
		shape.getSpaceManager().getSize().height - shape.getLabelSize().height), g);
		shape.refreshLayoutSelf();
		refreshLayoutChildren(shape);
	}

	
	public void resizeContainerContainerShape(ContainerContainerShape shape, Dimension newSize,
			Graphics2D g) 
	throws GraphModel.NotReadyException {
		shape.getSpaceManager().setSize(newSize);
		int remainingWidth = shape.getSpaceManager().getSize().width;
		int[] widths = new int[shape.getStructureContainers().size()];
		for (int i = 0; i < shape.getStructureContainers().size(); i++) {
			widths[i] = getPreferedSize(shape.getStructureContainers().get(i), g).width;
			remainingWidth -= widths[i];
			remainingWidth -= 8;// TODO make 8 (space between reaction slices) a field
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
	

	public void resizeMembraneShape(MembraneShape shape, Dimension newSize, Graphics2D g) 
	throws GraphModel.NotReadyException {
		int deltaX = newSize.width - shape.getSpaceManager().getSize().width;
		int deltaY = newSize.height - shape.getSpaceManager().getSize().height;
		shape.getSpaceManager().setSize(newSize);
		// allocate all extra new space to featureShape
		if (shape.childShapeList.size()==1){
			FeatureShape featureShape = (FeatureShape) shape.childShapeList.get(0);
			Dimension featureSize = new Dimension(featureShape.getSpaceManager().getSize());
			featureSize.width += deltaX;
			featureSize.height += deltaY;
			resize(featureShape, featureSize, g);
		}
		shape.refreshLayoutSelf();
		refreshLayoutChildren(shape);
	}

	public void resizeShape(Shape shape, Dimension newSize, Graphics2D g) 
	throws GraphModel.NotReadyException {
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
		if(shape instanceof GeometryContextContainerShape) {
			refreshLayoutChildrenGeometryContextContainerShape((GeometryContextContainerShape) shape);
		} else if(shape instanceof GeometryContextGeometryShape) {
			refreshLayoutChildrenGeometryContextGeometryShape((GeometryContextGeometryShape) shape);
		} else if(shape instanceof GeometryContextStructureShape) {
			refreshLayoutChildrenGeometryContextStructureShape((GeometryContextStructureShape) shape);
		} else if(shape instanceof SimpleContainerShape) {
			refreshLayoutChildrenSimpleContainerShape((SimpleContainerShape) shape);
		} else if(shape instanceof ContainerContainerShape) {
			refreshLayoutChildrenContainerContainerShape((ContainerContainerShape) shape);
		} else if(shape instanceof StructureMappingStructureShape) {
			refreshLayoutChildrenStructureMappingFeatureShape((StructureMappingStructureShape) shape);
		} else if(shape instanceof FeatureShape) {
			refreshLayoutChildrenFeatureShape((FeatureShape) shape);
		} else if(shape instanceof MembraneShape) {
			refreshLayoutChildrenMembraneShape((MembraneShape) shape);
		}
	}
	
	public void refreshLayoutChildrenGeometryContextContainerShape(GeometryContextContainerShape shape) {
		int currentX = 0;
		int currentY = 0;
		// position structureContainer shape
		shape.getStructureContainer().getSpaceManager().setRelPos(currentX, currentY);
		currentX += shape.getStructureContainer().getSpaceManager().getSize().width;
		// position subvolumeContainer shape
		shape.getGeometryContainer().getSpaceManager().setRelPos(currentX, currentY);
		currentX += shape.getGeometryContainer().getSpaceManager().getSize().width;
		for (Shape child : shape.getChildren()) {
			child.refreshLayoutSelf();
			refreshLayoutChildren(child);
		}	
	}

	public void refreshLayoutChildrenGeometryContextGeometryShape(GeometryContextGeometryShape shape) {
		final int PAD_Y = 5;
		int centerX = shape.getSpaceManager().getSize().width/2;
		int totalPadY = PAD_Y * (shape.countChildren() - 1);
		int totalChildHeight = 0;
		int maxChildWidth = 0;
		for(Shape child : shape.getChildren()) {
			totalChildHeight += child.getSpaceManager().getSize().height;
			maxChildWidth = Math.max(maxChildWidth,child.getSpaceManager().getSize().width);
		}
		int currentY = 
			shape.getSpaceManager().getSize().height/2 + shape.getLabelSize().height/2 - totalPadY/2 - 
			totalChildHeight/2;
		for (int i = 0; i < shape.countChildren(); ++i){
			Shape child = shape.getChildren().get(i);
			if (i == shape.countChildren() - 1){
				child.getSpaceManager().setRelPos(centerX - child.getSpaceManager().getSize().width/2, currentY);
			} else {
				child.getSpaceManager().setRelPos(centerX - maxChildWidth/2, currentY);
			}
			int dy = child.getSpaceManager().getSize().height + PAD_Y;
			currentY += dy;
			child.refreshLayoutSelf();
			refreshLayoutChildren(child);
		}
	}

	public void refreshLayoutChildrenGeometryContextStructureShape(GeometryContextStructureShape shape) {
		// calculate total height and max width of SubVolumeContainerShape
		int childHeight = 0;
		int childWidth = 0;
		for(Shape child : shape.getChildren()) {
			childHeight += child.getSpaceManager().getSize().height;
			childWidth = Math.max(childWidth,child.getSpaceManager().getSize().width);
		}
		int centerX = shape.getSpaceManager().getSize().width / 2;
		int centerY = shape.getSpaceManager().getSize().height / 2;
		int currY = Math.max(0,centerY - childHeight/2) + shape.getLabelSize().height + 2;		
		for(Shape child : shape.getChildren()) {
			child.getSpaceManager().setRelPos(centerX - child.getSpaceManager().getSize().width/2, currY);
			currY += child.getSpaceManager().getSize().height;
			child.refreshLayoutSelf();
			refreshLayoutChildren(child);
		}		
	}

	public void refreshLayoutChildrenSimpleContainerShape(SimpleContainerShape shape) {
		int width = shape.getSpaceManager().getSize().width;
		int height = shape.getSpaceManager().getSize().height;
		for (int i = 0; i < shape.childShapeList.size(); i++){
			Shape child = shape.childShapeList.get(i);
			if (child.getSpaceManager().getSize().width + 
					child.getSpaceManager().getRelPos().x > width || 
					child.getSpaceManager().getSize().height + 
					child.getSpaceManager().getRelPos().y > height){
				LayoutErrorLog.logErrorMessage("cannot fit all reactions");
			}
		}	
	}

	public void refreshLayoutChildrenContainerContainerShape(ContainerContainerShape shape) {
		List<ReactionContainerShape> structureContainers = shape.getStructureContainers();
		int currentX = 4;
		int currentY = 0;
		for (int i = 0; i < structureContainers.size(); i++) {
			structureContainers.get(i).getSpaceManager().setRelPos(currentX, currentY);
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
	}

	public void refreshLayoutChildrenContainerShape(ContainerShape shape) {
	}

	public void refreshLayoutChildrenStructureMappingFeatureShape(StructureMappingStructureShape shape) {
		// calculate total height and max width of Child compartments (membranes)
		int memHeight = 0;
		int memWidth = 0;
		for(Shape child : shape.getChildren()) {
			memHeight += child.getSpaceManager().getSize().height;
			memWidth = Math.max(memWidth, child.getSpaceManager().getSize().width);
		}
		// position label
		int currentY = shape.getLabelSize().height;
		currentY += shape.getLabelSize().height;
		// find current centerX
		int totalSpacingX = shape.getSpaceManager().getSize().width - memWidth;
		if (totalSpacingX<0){
			LayoutErrorLog.logErrorMessage("unable to fit children within container (width)");
		}			
		int spacingX = totalSpacingX/2;
		int extraSpacingX = totalSpacingX%2;
		int currentX = spacingX + extraSpacingX;
		// position child compartments
		int totalSpacingY = (shape.getSpaceManager().getSize().height-currentY) - memHeight;
		if (totalSpacingY<0){
			LayoutErrorLog.logErrorMessage("unable to fit children within container (height)");
		}				
		int spacingY = totalSpacingY/(shape.countChildren()+1);
		int extraSpacingY = totalSpacingY%(shape.countChildren()+1);
		int centerX = currentX + memWidth/2;
		// position children (and label)
		int colY = currentY + extraSpacingY;
		for(Shape child :shape.getChildren()) {
			child.getSpaceManager().setRelPos(centerX - child.getSpaceManager().getSize().width/2, colY);
			colY += child.getSpaceManager().getSize().height + spacingY;
		}
		currentX += spacingX;
		colY += spacingY;
		if (colY != shape.getSpaceManager().getSize().height){
			throw new RuntimeException("layout for column incorrect (" + shape.getLabel() + "), currentY=" 
					+ currentY + ", screenSize.height=" + shape.getSpaceManager().getSize().height);
		}
		if (shape.countChildren()>0){
			currentX += memWidth;
		}	
		if (currentX != shape.getSpaceManager().getSize().width){
			throw new RuntimeException("layout for column widths incorrect (" + shape.getLabel() + 
					"), currentX=" + currentX + ", screenSize.width=" + 
					shape.getSpaceManager().getSize().width);
		}		
	}

	public void refreshLayoutChildrenFeatureShape(FeatureShape shape) {
		// calculate total height and max width of speciesContext (column1)
		int scCount = 0;
		List<Shape> childShapeList = shape.getChildren();
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof SpeciesContextShape){
				scCount++;
			}	
		}
		// calculate total height and max width of Membranes (column2)
		int memCount = 0;
		int memHeight = 0;
		int memWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				memHeight += child.getSpaceManager().getSize().height;
				memWidth = Math.max(memWidth, child.getSpaceManager().getSize().width);
				memCount++;
			}	
		}
		int centerX = shape.getSpaceManager().getSize().width/2;
		int currentY = shape.getLabelSize().height;
		currentY += shape.getLabelSize().height;
		int totalSpacingY;
		int spacingY;
		int extraSpacingY;
		int currentX = shape.getSpaceManager().getSize().width/2 - memWidth/2;
		int spacingX = StructureShape.defaultSpacingX;
		// position column2 (membranes)
		totalSpacingY = (shape.getSpaceManager().getSize().height - currentY) - memHeight;
		if (totalSpacingY<0){
			LayoutErrorLog.logErrorMessage("unable to fit children within container");
		}				
		spacingY = totalSpacingY/(memCount+1);
		extraSpacingY = totalSpacingY%(memCount+1);
		centerX = currentX + memWidth/2;
		// position children (and label)
		int col2Y = currentY + spacingY + extraSpacingY;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape){
				child.getSpaceManager().setRelPos(centerX - child.getSpaceManager().getSize().width/2, col2Y);
				col2Y += child.getSpaceManager().getSize().height + spacingY;
			}	
		}
		if (col2Y != shape.getSpaceManager().getSize().height){
			LayoutErrorLog.logErrorMessage("layout for column2 incorrect (" + shape.getLabel() + 
					"), currentY=" + currentY + ", screenSize.height=" + 
					shape.getSpaceManager().getSize().height);
		}
		if (memCount>0){
			currentX += spacingX + memWidth;
		}	
		if(scCount > 0){
			//The following code attempts to position SpeciesContextShapes so that their
			//ovals and labels do not overlap other structures
			//Calculate Mask of valid area where SpeciesContextShape may render
			Area currentOval = new Area(new Ellipse2D.Double(
					shape.getSpaceManager().getRelX(), shape.getSpaceManager().getRelY(),
					shape.getSpaceManager().getSize().width, shape.getSpaceManager().getSize().height));
			for (int i=0;i<childShapeList.size();i++){
				Shape child = childShapeList.get(i);
				if (child instanceof StructureShape){
					Area childArea =
						new Area(new Ellipse2D.Double(
								child.getSpaceManager().getRelX() + shape.getSpaceManager().getRelX(),
								child.getSpaceManager().getRelY() + shape.getSpaceManager().getRelY(),
								child.getSpaceManager().getSize().width, 
								child.getSpaceManager().getSize().height));
					currentOval.subtract(childArea);
				}	
			}
			//
			//Position SpeciesContextShapes so they and their labels don't overlap other structures.
			//If fail, try a few more times with reduced spacing between SpeciesContextShapes to see
			//if we can fit
			//	
			final int TOPOFFSET = 20;
			boolean bLayoutFailed = false;
			int layoutRetryCount = 0;
			final int MAX_LAYOUT_RETRY = 3;

			int boxSpacingX = 50;
			final int X_RETRY_SPACE_REDUCTION = 10;
			int boxSpacingY = 30;
			final int Y_RETRY_SPACE_REDUCTION = 5;

			final int XEND = currentOval.getBounds().x + currentOval.getBounds().width;
			final int YEND = currentOval.getBounds().y + currentOval.getBounds().height;
			while(true) {
				bLayoutFailed = false;
				int xCount = 0;
				int boxX = 0;
				int boxY = 0;
				for (int i=0;i<childShapeList.size();i++){
					if (childShapeList.get(i) instanceof SpeciesContextShape){
						SpeciesContextShape child = (SpeciesContextShape) childShapeList.get(i);
						final int XSTEP = child.getSpaceManager().getSize().width + boxSpacingX;
						final int YSTEP = child.getSpaceManager().getSize().height + boxSpacingY;
						while ((layoutRetryCount <= MAX_LAYOUT_RETRY
						// If last try, don't check labels for overlap
								&& (boxY <= TOPOFFSET// away from structure label
								|| !currentOval.contains(
										// SCS label not overlap other structures
										boxX + child.getSmallLabelPos().x
											+ currentOval.getBounds().getX(), boxY
											+ child.getSmallLabelPos().y
											+ currentOval.getBounds().getY()
											- child.getSmallLabelSize().height,
										child.getSmallLabelSize().width,
										child.getSmallLabelSize().height)))
								|| !currentOval.contains(
										// SCS oval not overlap other structures
										boxX + currentOval.getBounds().getX(),
										boxY + currentOval.getBounds().getY(),
										SpeciesContextShape.DIAMETER,
										SpeciesContextShape.DIAMETER)) {
							if (boxY < YEND) {
								boxY += YSTEP;
							} else {
								boxY = (xCount % 2 == 0 ? TOPOFFSET : TOPOFFSET
										+ SpeciesContextShape.DIAMETER);
								boxX += XSTEP;
								xCount += 1;
							}
							if (boxX > XEND && boxY > YEND) {
								bLayoutFailed = true;
								break;
							}
						}
						if (!bLayoutFailed) {
							child.getSpaceManager().setRelPos(boxX, boxY);
							boxY += YSTEP;
						} else {
							break;
						}
					}
				}
				if(bLayoutFailed && layoutRetryCount <= MAX_LAYOUT_RETRY){
					boxSpacingX-=X_RETRY_SPACE_REDUCTION;
					boxSpacingY-=Y_RETRY_SPACE_REDUCTION;
					layoutRetryCount+= 1;
				}else{
					break;
				}
			}
		}
	}

	public void refreshLayoutChildrenMembraneShape(MembraneShape shape) {
		// this is like a row/column layout  (1 column)
		// find featureShape child
		FeatureShape featureShape = null;
		for(Shape child : shape.getChildren()) {
			if (child instanceof FeatureShape){
				featureShape = (FeatureShape) child;
			}
		}
		// calculate total height of all children (not including label)
		// position featureShape (and label)
		int centerX = shape.getSpaceManager().getSize().width/2;
		int currentY = MembraneShape.memSpacingY;
		if (featureShape!=null){
			featureShape.getSpaceManager().setRelPos(
					centerX - featureShape.getSpaceManager().getSize().width/2, currentY);
			currentY += featureShape.getSpaceManager().getSize().height + MembraneShape.memSpacingY;
		}
		// position speciesContextShapes
		// angle = 0 at north pole and increases counter clockwise
		int numSpeciesContexts = shape.countChildren()-1;
		if (numSpeciesContexts>0){
			double deltaAngle = MembraneShape.TotalAngle/(numSpeciesContexts+1);
			double currentAngle = MembraneShape.BeginAngle + deltaAngle;
			for(Shape child : shape.getChildren()) {
				if (child instanceof SpeciesContextShape){
					child.getSpaceManager().setRelPos(shape.getRadialPosition(currentAngle));
					currentAngle = (currentAngle + deltaAngle) % (2*Math.PI);
				}	
			}	
		}						
	}

	public Rectangle getBoundaryForAutomaticLayout(Shape shape) {
		return new Rectangle(shape.getSpaceManager().getAbsLoc(), shape.getSpaceManager().getSize());
	}

	public boolean isContainerForAutomaticLayout(Shape shape) {
		return shape instanceof SimpleContainerShape;
	}

	public boolean isNodeForAutomaticLayout(Shape shape) {
		return shape instanceof ElipseShape;
	}

}
