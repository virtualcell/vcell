package cbit.vcell.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import java.awt.*;
/**
 * Insert the type's description here.
 * Creation date: (4/12/01 3:03:38 PM)
 * @author: Jim Schaff
 */
public class StructureMappingFeatureShape extends FeatureShape {
/**
 * StructureMappingFeatureShape constructor comment.
 * @param feature cbit.vcell.model.Feature
 * @param model cbit.vcell.model.Model
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public StructureMappingFeatureShape(cbit.vcell.model.Feature feature, cbit.vcell.model.Model model, GraphModel graphModel) {
	super(feature, model, graphModel);
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {

	java.awt.FontMetrics fm = g.getFontMetrics();
	labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
	labelSize.width = fm.stringWidth(getLabel());

	if (childShapeList.size()>0){
		int totalHeight = labelSize.height;
		int maxWidth = labelSize.width;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = (Shape)childShapeList.elementAt(i);
			Dimension childDim = child.getPreferedSize(g);
			totalHeight += childDim.height + defaultSpacingY;
			maxWidth = Math.max(maxWidth,childDim.width);
		}
		preferedSize.width = maxWidth + defaultSpacingX*2;
		preferedSize.height = totalHeight + defaultSpacingY;
	}else{
		preferedSize.width = labelSize.width + defaultSpacingX*2;
		preferedSize.height = labelSize.height + defaultSpacingY*2;
	}	
	return preferedSize;
}


public Point getSeparatorDeepCount() {
	int selfCountX = 1;
	int selfCountY = 1;

	//
	// column is StructureShapes
	//
	Point sepCount = new Point(selfCountX,selfCountY);
	for (int i = 0; i < countChildren(); i++) {
		Shape child = (Shape) childShapeList.elementAt(i);
		if (child instanceof StructureShape) {
			Point childCount = child.getSeparatorDeepCount();
			sepCount.x = Math.max(sepCount.x, childCount.x);
			sepCount.y += childCount.y + 1;
		}
	}

	if (countChildren()>0){
		sepCount.x += 2;
	}
	return sepCount;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public void layout() throws LayoutException {

	//
	// calculate total height and max width of Child compartments (membranes)
	//
	int memHeight = 0;
	int memWidth = 0;
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		memHeight += child.screenSize.height;
		memWidth = Math.max(memWidth,child.screenSize.width);
	}

	//
	// position label
	//
	int centerX = screenSize.width/2;
	int currentY = labelSize.height;
	labelPos.x = centerX - labelSize.width/2;
	labelPos.y = currentY;
	currentY += labelSize.height;
	
	//
	// find current centerX
	//	
	int totalSpacingX = screenSize.width - memWidth;
	if (totalSpacingX<0){
		throw new LayoutException("unable to fit children within container (width)");
	}	
	int spacingX = totalSpacingX/2;
	int extraSpacingX = totalSpacingX%2;
	int currentX = spacingX + extraSpacingX;
	
	//
	// position child compartments
	//
	int totalSpacingY = (screenSize.height-currentY) - memHeight;
	if (totalSpacingY<0){
		throw new LayoutException("unable to fit children within container (height)");
	}		
	int spacingY = totalSpacingY/(countChildren()+1);
	int extraSpacingY = totalSpacingY%(countChildren()+1);
	centerX = currentX + memWidth/2;
	//
	// position children (and label)
	//
	int colY = currentY + extraSpacingY;
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		Dimension childDim = child.screenSize;
		child.screenPos.x = centerX - child.screenSize.width/2;
		child.screenPos.y = colY;
		colY += child.screenSize.height + spacingY;
	}
	currentX += spacingX;
	colY += spacingY;
	if (colY != screenSize.height){
		throw new RuntimeException("layout for column incorrect ("+getLabel()+"), currentY="+currentY+", screenSize.height="+screenSize.height);
	}
	if (countChildren()>0){
		currentX += memWidth;
	}	
	if (currentX != screenSize.width){
		throw new RuntimeException("layout for column widths incorrect ("+getLabel()+"), currentX="+currentX+", screenSize.width="+screenSize.width);
	}
	
}
}