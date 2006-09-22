package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
import cbit.vcell.modelapp.GeometryContext;

import java.awt.*;

/**
 * This type was created in VisualAge.
 */
public class GeometryContextContainerShape extends ContainerShape {

	GeometryContext geometryContext = null;
	
	GeometryContextStructureShape structureContainer = null;
	GeometryContextGeometryShape geometryContainer = null;

/**
 * ContainerContainerShape constructor comment.
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public GeometryContextContainerShape(GraphModel graphModel, GeometryContext geoContext, GeometryContextStructureShape structureContainer, 
								GeometryContextGeometryShape geometryContainer) {
	super(graphModel);
	this.structureContainer = structureContainer;
	this.geometryContainer = geometryContainer;
	this.geometryContext = geoContext;

	addChildShape(structureContainer);
	addChildShape(geometryContainer);
	
	setLabel("geoContext");
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return geometryContext;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {
	Dimension geometryChildDim = geometryContainer.getPreferedSize(g);
	Dimension structureChildDim = structureContainer.getPreferedSize(g);
	Dimension newDim = new Dimension(geometryChildDim.width+structureChildDim.width, Math.max(geometryChildDim.height,structureChildDim.height));
	return newDim;
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {

	int currentX = 0;
	int currentY = 0;
	//
	// position structureContainer shape
	//
	structureContainer.screenPos.x = currentX;
	structureContainer.screenPos.y = currentY;
	currentX += structureContainer.screenSize.width;

	//
	// position subvolumeContainer shape
	//
	geometryContainer.screenPos.x = currentX;
	geometryContainer.screenPos.y = currentY;
	currentX += geometryContainer.screenSize.width;

	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		child.layout();
	}	
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint ( java.awt.Graphics2D g, int parentOffsetX, int parentOffsetY ) {

	int absPosX = screenPos.x + parentOffsetX;
	int absPosY = screenPos.y + parentOffsetY;

//	g.setColor(backgroundColor);
	g.setColor(java.awt.Color.yellow);
	g.fillRect(absPosX,absPosY,screenSize.width,screenSize.height);
	g.setColor(forgroundColor);
	g.drawRect(absPosX,absPosY,screenSize.width,screenSize.height);

//	g.drawString(getLabel(),labelPos.x,labelPos.y);


	//
	// draw cell structures
	//
	structureContainer.paint(g,absPosX,absPosY);

	//
	// draw subvolumes
	//
	geometryContainer.paint(g,absPosX,absPosY);

	//
	// draw featureMappings
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof FeatureMappingShape){
			child.paint(g,absPosX,absPosY);
		}
	}	

}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.graph.Shape
 * @param x int
 * @param y int
 */
public Shape pick(Point point) {

	if (isInside(point)==false) return null;
	Point childPoint = new Point(point.x-screenPos.x,point.y-screenPos.y);

	//
	// pick the reactionContainerShapes first
	//
	Shape childPick = null;
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (child instanceof ReactionContainerShape){
			Shape tryPick = child.pick(childPoint);
			if (tryPick!=null){
				childPick = tryPick;
				break;
			}
		}
	}
	//
	// if only the reactionContainer got picked, then keep going (look for edges)
	//
	// this allows reactionContainer itself to be transparent to edges for picking purposes
	//
	if (childPick != null){
		//
		// selected a fluxNode or reactionNode
		//
		if (!(childPick instanceof ReactionContainerShape)){
			return childPick;
		}
	}
	
	//
	// pick the edges next
	//
	for (int i=0;i<childShapeList.size();i++){
		Shape child = (Shape)childShapeList.elementAt(i);
		if (!(child instanceof ReactionContainerShape)){
			Shape tryPick = child.pick(childPoint);
			if (tryPick!=null){
				childPick = tryPick;
				break;
			}
		}
	}

	//
	// if missed all of the nodes and edges, then return compartment, (or at least the ContainerContainer)
	//
	if (childPick!=null){
		return childPick;
	}else{
		return this;
	}
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void resize(java.awt.Graphics2D g, java.awt.Dimension newSize) throws Exception {
	
	screenSize = newSize;
	
//	int structNewWidth = screenSize.width * structureContainer.getPreferedSize(g).width / (structureContainer.getPreferedSize(g).width + geometryContainer.getPreferedSize(g).width);
//	int geomNewWidth = screenSize.width - structNewWidth - 1;

	//
	// try to make geometryContainer have full width and structureContainer have rest
	//
	int geomNewWidth = Math.min(screenSize.width-10,geometryContainer.getPreferedSize(g).width);
	int structNewWidth = screenSize.width - geomNewWidth - 1;
	structureContainer.resize(g, new java.awt.Dimension(structNewWidth, screenSize.height - labelSize.height));
	geometryContainer.resize(g, new java.awt.Dimension(geomNewWidth, screenSize.height - labelSize.height));
	
	layout();
}
}