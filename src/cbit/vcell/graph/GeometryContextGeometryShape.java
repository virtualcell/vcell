package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.geometry.Geometry;
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public class GeometryContextGeometryShape extends ContainerShape {

	Geometry geometry = null;
	
/**
 * This method was created in VisualAge.
 * @param graphModel cbit.vcell.graph.GraphModel
 * @param model cbit.vcell.model.Model
 * @param featureContainerShape cbit.vcell.graph.ReactionContainerShape
 */
public GeometryContextGeometryShape(GraphModel graphModel, Geometry geometry) {
	super(graphModel);
	this.geometry = geometry;
	setLabel("Geometry (subdomains)");
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return geometry;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {
	Dimension d = super.getPreferedSize(g);
	int height = 2 * labelSize.height + 2;
	Dimension childDim = new Dimension();
	if (childShapeList.size()>0){
		for (int i = 0; i < childShapeList.size(); i++){
			Shape child = (Shape)childShapeList.elementAt(i);
			childDim.height += child.getPreferedSize(g).height;
			childDim.width = Math.max(childDim.width,child.getPreferedSize(g).width);
		}
	}else{
		childDim = new Dimension(100,100);
	}
	Dimension newDim = new Dimension(Math.max(d.width,childDim.width)+10, height+childDim.height);
	return newDim;
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {

//	super.layout();
    final int PAD_Y = 5;

	int centerX = screenSize.width/2;
	int totalPadY = PAD_Y * (childShapeList.size()-1);

	int totalChildHeight = 0;
	int maxChildWidth = 0;
	for (int i = 0; i < childShapeList.size(); i++){
		Shape shape = (Shape)childShapeList.elementAt(i);
		totalChildHeight += shape.screenSize.height;
		maxChildWidth = Math.max(maxChildWidth,shape.screenSize.width);
	}

	int currentY = screenSize.height/2 + labelSize.height/2 - totalPadY/2 - totalChildHeight/2;
	for (int i = 0; i < childShapeList.size(); i++){
		Shape shape = (Shape)childShapeList.elementAt(i);
		if (i==childShapeList.size()-1){
			shape.screenPos.x = centerX - shape.screenSize.width/2;
		}else{
			shape.screenPos.x = centerX - maxChildWidth/2;
		}
		shape.screenPos.y = currentY;
		currentY += shape.screenSize.height + PAD_Y;
		shape.layout();
	}
}
}