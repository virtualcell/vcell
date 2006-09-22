package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Model;
/**
 * This type was created in VisualAge.
 */
public class GeometryContextStructureShape extends ContainerShape {
	Model model = null;

/**
 * This method was created in VisualAge.
 * @param graphModel cbit.vcell.graph.GraphModel
 * @param model cbit.vcell.model.Model
 * @param featureContainerShape cbit.vcell.graph.ReactionContainerShape
 */
public GeometryContextStructureShape(GraphModel graphModel, Model model) {
	super(graphModel);
	this.model = model;
	defaultBG = java.awt.Color.lightGray;
	backgroundColor = defaultBG;
	setLabel("Physiology (structures)");
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return model;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {
	Dimension d = super.getPreferedSize(g);
	Dimension childDim = null;
	if (childShapeList.size()!=0){
		childDim = ((Shape)childShapeList.elementAt(0)).getPreferedSize(g);
	}else{
		childDim = new Dimension(100,100);
	}
	Dimension newDim = new Dimension(childDim.width, d.height+childDim.height);
	return newDim;
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {

	super.layout();

	int centerX = screenSize.width/2;
	int centerY = screenSize.height/2;

	//
	// calculate total height and max width of SubVolumeContainerShape
	//
	int childHeight = 0;
	int childWidth = 0;
	for (int i=0;i<childShapeList.size();i++){
		Shape shape = (Shape)childShapeList.elementAt(i);
		childHeight += shape.screenSize.height;
		childWidth = Math.max(childWidth,shape.screenSize.width);
	}

	int currY = Math.max(0,centerY - childHeight/2) + labelSize.height+2;
	
	for (int i=0;i<childShapeList.size();i++){
		Shape shape = (Shape)childShapeList.elementAt(i);
		shape.screenPos.x = centerX - shape.screenSize.width/2;
		shape.screenPos.y = currY;
		currY += shape.screenSize.height;
		shape.layout();
	}
}
}