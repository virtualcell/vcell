package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;

import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (4/12/01 3:03:38 PM)
 * @author: Jim Schaff
 */
public class StructureMappingFeatureShape extends FeatureShape {

	public StructureMappingFeatureShape(Feature feature, Model model, GraphModel graphModel) {
		super(feature, model, graphModel);
	}

	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(getLabel());
		if(childShapeList.size()>0) {
			int totalHeight = labelSize.height;
			int maxWidth = labelSize.width;
			for (int i=0;i<childShapeList.size();i++){
				Shape child = childShapeList.get(i);
				Dimension childDim = child.getPreferedSize(g);
				totalHeight += childDim.height + defaultSpacingY;
				maxWidth = Math.max(maxWidth,childDim.width);
			}
			preferredSize.width = maxWidth + defaultSpacingX*2;
			preferredSize.height = totalHeight + defaultSpacingY;
		} else {
			preferredSize.width = labelSize.width + defaultSpacingX*2;
			preferredSize.height = labelSize.height + defaultSpacingY*2;
		}	
		return preferredSize;
	}

	@Override
	public Point getSeparatorDeepCount() {
		int selfCountX = 1;
		int selfCountY = 1;
		// column is StructureShapes
		Point sepCount = new Point(selfCountX,selfCountY);
		for (int i = 0; i < countChildren(); i++) {
			Shape child = childShapeList.get(i);
			if (child instanceof StructureShape) {
				Point childCount = child.getSeparatorDeepCount();
				sepCount.x = Math.max(sepCount.x, childCount.x);
				sepCount.y += childCount.y + 1;
			}
		}
		if (countChildren() > 0) {
			sepCount.x += 2;
		}
		return sepCount;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		// calculate total height and max width of Child compartments (membranes)
		int memHeight = 0;
		int memWidth = 0;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			memHeight += child.shapeSize.height;
			memWidth = Math.max(memWidth,child.shapeSize.width);
		}
		// position label
		int centerX = shapeSize.width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2;
		labelPos.y = currentY;
		currentY += labelSize.height;
		// find current centerX
		int totalSpacingX = shapeSize.width - memWidth;
		if(LayoutException.bActivated) {
			if (totalSpacingX<0){
				throw new LayoutException("unable to fit children within container (width)");
			}			
		}
		int spacingX = totalSpacingX/2;
		int extraSpacingX = totalSpacingX%2;
		int currentX = spacingX + extraSpacingX;
		// position child compartments
		int totalSpacingY = (shapeSize.height-currentY) - memHeight;
		if(LayoutException.bActivated) {
			if (totalSpacingY<0){
				throw new LayoutException("unable to fit children within container (height)");
			}				
		}
		int spacingY = totalSpacingY/(countChildren()+1);
		int extraSpacingY = totalSpacingY%(countChildren()+1);
		centerX = currentX + memWidth/2;
		// position children (and label)
		int colY = currentY + extraSpacingY;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			child.relativePos.x = centerX - child.shapeSize.width/2;
			child.relativePos.y = colY;
			colY += child.shapeSize.height + spacingY;
		}
		currentX += spacingX;
		colY += spacingY;
		if (colY != shapeSize.height){
			throw new RuntimeException("layout for column incorrect (" + getLabel() + "), currentY=" 
					+ currentY + ", screenSize.height=" + shapeSize.height);
		}
		if (countChildren()>0){
			currentX += memWidth;
		}	
		if (currentX != shapeSize.width){
			throw new RuntimeException("layout for column widths incorrect (" + getLabel() + 
					"), currentX=" + currentX + ", screenSize.width="+shapeSize.width);
		}
	}
}