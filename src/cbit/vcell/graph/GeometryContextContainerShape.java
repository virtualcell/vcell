package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.mapping.GeometryContext;
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
	@Override
	public Object getModelObject() {
		return geometryContext;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(java.awt.Graphics2D g) {
		Dimension geometryChildDim = geometryContainer.getPreferedSize(g);
		Dimension structureChildDim = structureContainer.getPreferedSize(g);
		Dimension newDim = new Dimension(geometryChildDim.width+structureChildDim.width, Math.max(geometryChildDim.height,structureChildDim.height));
		return newDim;
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void layout() throws LayoutException {

		int currentX = 0;
		int currentY = 0;
		//
		// position structureContainer shape
		//
		structureContainer.relativePos.x = currentX;
		structureContainer.relativePos.y = currentY;
		currentX += structureContainer.shapeSize.width;

		//
		// position subvolumeContainer shape
		//
		geometryContainer.relativePos.x = currentX;
		geometryContainer.relativePos.y = currentY;
		currentX += geometryContainer.shapeSize.width;

		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
			child.layout();
		}	
	}


	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {

		//	g.setColor(backgroundColor);
		g.setColor(java.awt.Color.yellow);
		g.fillRect(absPosX,absPosY,shapeSize.width,shapeSize.height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY,shapeSize.width,shapeSize.height);

		//	g.drawString(getLabel(),labelPos.x,labelPos.y);

	}

	@Override
	public Shape pick(Point point) {

		if (isInside(point)==false) return null;
		Point childPoint = new Point(point.x-relativePos.x,point.y-relativePos.y);

		//
		// pick the reactionContainerShapes first
		//
		Shape childPick = null;
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.elementAt(i);
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
			Shape child = childShapeList.elementAt(i);
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
	@Override
	public void resize(java.awt.Graphics2D g, java.awt.Dimension newSize) throws Exception {

		shapeSize = newSize;

		//	int structNewWidth = screenSize.width * structureContainer.getPreferedSize(g).width / (structureContainer.getPreferedSize(g).width + geometryContainer.getPreferedSize(g).width);
		//	int geomNewWidth = screenSize.width - structNewWidth - 1;

		//
		// try to make geometryContainer have full width and structureContainer have rest
		//
		int geomNewWidth = Math.min(shapeSize.width-10,geometryContainer.getPreferedSize(g).width);
		int structNewWidth = shapeSize.width - geomNewWidth - 1;
		structureContainer.resize(g, new java.awt.Dimension(structNewWidth, shapeSize.height - labelSize.height));
		geometryContainer.resize(g, new java.awt.Dimension(geomNewWidth, shapeSize.height - labelSize.height));

		layout();
	}
}