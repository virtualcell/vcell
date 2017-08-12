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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.PathwayContainerShape;

public class GraphContainerLayoutPathways implements GraphContainerLayout {
	
	public static final Dimension PATHWAY_CONTAINER_MIN_SIZE = new Dimension(500, 250);
	public static final int MIN_AREA_PER_NODE = 2000;
	public static final int TOP_LABEL_HEIGHT = 0;
	public static final int HEIGHT_PADDING = 14;
	public static final int WIDTH_PADDING = 14;
	
	public Dimension getPreferedSize(Shape shape, Graphics2D g) {
		if(shape instanceof PathwayContainerShape) {
			return getPreferedSizePathwayContainerShape((PathwayContainerShape)shape, g);
		}
		return shape.getPreferedSizeSelf(g);
	}
	
	public Dimension getPreferedSizePathwayContainerShape(PathwayContainerShape shape, Graphics2D g) {
		// get size when empty
		Font origFont = g.getFont();
		g.setFont(shape.getLabelFont(g));
		try{
			Dimension preferredSize = shape.getPreferedSizeSelf(g);
			// make larger than empty size so that children fit
			for(Shape child : shape.getChildren()) {
				if (child instanceof BioPaxShape) {
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
			if(preferredSize.width < PATHWAY_CONTAINER_MIN_SIZE.width) {
				preferredSize.width = PATHWAY_CONTAINER_MIN_SIZE.width;
			}
			if(preferredSize.height < PATHWAY_CONTAINER_MIN_SIZE.height) {
				preferredSize.height = PATHWAY_CONTAINER_MIN_SIZE.height;
			}
			int nNodes = shape.getChildren().size();
			int minArea = nNodes*MIN_AREA_PER_NODE;
			while(preferredSize.width*preferredSize.height < minArea) {
				preferredSize.width += preferredSize.width/20;
				preferredSize.height += preferredSize.height/20;
			}
			return preferredSize;
		} finally {
			g.setFont(origFont);
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
			resize(topShape, this.getPreferedSize(topShape, g2d), g2d);
		}
		int topShapeX = 0, topShapeY = 0;
		if(topShape.getWidth() < size.width) {
			topShapeX = (size.width - topShape.getWidth()) / 2;
		}
		if(topShape.getHeight() < size.height) {
			topShapeY = (size.height - topShape.getHeight()) / 2;
		}
		topShape.setAbsPos(topShapeX, topShapeY);
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
		if(shape instanceof ElipseShape) {
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
		resizeShape(shape, newSize, g);
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
	}
	
	public Rectangle getBoundaryForAutomaticLayout(Shape shape) {
		if(shape instanceof PathwayContainerShape) {
			Point absLoc = shape.getAbsPos();
			Dimension size = shape.getSize();
			return new Rectangle(absLoc.x + WIDTH_PADDING, absLoc.y + HEIGHT_PADDING + TOP_LABEL_HEIGHT, 
					size.width - 2*WIDTH_PADDING, size.height - 2*HEIGHT_PADDING - TOP_LABEL_HEIGHT);			
		}
		return new Rectangle(shape.getAbsPos(), shape.getSize());
	}

	public boolean isContainerForAutomaticLayout(Shape shape) {
		return shape instanceof PathwayContainerShape;
	}

	public boolean isNodeForAutomaticLayout(Shape shape) {
		return shape instanceof BioPaxShape;
	}
	
}
