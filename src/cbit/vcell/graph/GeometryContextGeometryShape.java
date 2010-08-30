package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.*;
import cbit.gui.graph.Shape;
import cbit.vcell.geometry.Geometry;
import java.awt.*;

public class GeometryContextGeometryShape extends ContainerShape {

	Geometry geometry = null;

	public GeometryContextGeometryShape(GraphModel graphModel, Geometry geometry) {
		super(graphModel);
		this.geometry = geometry;
		setLabel("Geometry (subdomains)");
	}

	@Override
	public Object getModelObject() {
		return geometry;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		Dimension d = super.getPreferedSize(g);
		int height = 2 * labelSize.height + 2;
		Dimension childDim = new Dimension();
		if (childShapeList.size()>0){
			for (int i = 0; i < childShapeList.size(); i++){
				Shape child = childShapeList.get(i);
				childDim.height += child.getPreferedSize(g).height;
				childDim.width = Math.max(childDim.width,child.getPreferedSize(g).width);
			}
		}else{
			childDim = new Dimension(100,100);
		}
		Dimension newDim = new Dimension(Math.max(d.width,childDim.width)+10, height+childDim.height);
		return newDim;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		final int PAD_Y = 5;
		int centerX = shapeSize.width/2;
		int totalPadY = PAD_Y * (childShapeList.size()-1);
		int totalChildHeight = 0;
		int maxChildWidth = 0;
		for (int i = 0; i < childShapeList.size(); i++){
			Shape shape = childShapeList.get(i);
			totalChildHeight += shape.shapeSize.height;
			maxChildWidth = Math.max(maxChildWidth,shape.shapeSize.width);
		}
		int currentY = shapeSize.height/2 + labelSize.height/2 - totalPadY/2 - totalChildHeight/2;
		for (int i = childShapeList.size() - 1; i >= 0; --i){
			Shape shape = childShapeList.get(i);
			if (i == childShapeList.size()-1){
				shape.relativePos.x = centerX - shape.shapeSize.width/2;
			} else {
				shape.relativePos.x = centerX - maxChildWidth/2;
			}
			shape.relativePos.y = currentY;
			currentY += shape.shapeSize.height + PAD_Y;
			shape.refreshLayout();
		}
	}
}